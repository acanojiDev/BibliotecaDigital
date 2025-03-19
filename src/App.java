import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import connection.ConnectionPool;
import libro.BookService;
import libro.Libro;
import prestamo.Prestamo;
import prestamo.PrestamoService;
import usuario.UserService;
import usuario.Usuario;

public class App {
    private static String url = "jdbc:mysql://localhost:3306/biblioteca";
    private static String usuario = "root";
    private static String clave = "";
    private static Connection conn;
    private static ConnectionPool pool;
    private static UserService userService;
    private static BookService bookService;
    private static PrestamoService prestamoService;
    private static Scanner scanner = new Scanner(System.in);

    private static void connectDatabase(){
        try {
            pool = new ConnectionPool(url, usuario, clave);
            conn = pool.getConnection();
            userService = new UserService(conn);
            bookService = new BookService(conn);
            prestamoService = new PrestamoService(conn);
            /** System.out.println("Conexión establecida, creando servicios...");

            userService = new UserService(conn);
            System.out.println("UserService inicializado.");

            bookService = new BookService(conn);
            System.out.println("BookService inicializado.");

            prestamoService = new PrestamoService(conn);
            System.out.println("PrestamoService inicializado.");

            System.out.println("Todos los servicios inicializados correctamente.");
            pool.closeAll();*/
        } catch (Exception e) {
            System.out.println("ERROR: No se pudo conectar a la base de datos o inicializar servicios.");
            e.printStackTrace();
        }
    }

    private static void introduccion(){
        System.out.print("""
                       Bienvenido a CANO
             La 'CANOA' que te lleva al conocimiento...
         ----------------------------------------------------
         |   Somos una biblioteca digital en la que puedes   |
         |  comprar, tomar prestado o vender un libro con    |
         | otros lectores de forma digital para proporcionar |
         |         una mayor red a los lectores              |
         ----------------------------------------------------
        """);
        System.out.println("Presione ENTER para entrar");
        scanner.nextLine();
    }

    private static void menuGeneral(){
        System.out.print("""
        1. Iniciar Sesión
        2. Registrarse
        3. Salir
        """);
        System.out.println("Ingrese tu opción: ");
    }

    private static boolean verifyPassword(String password){
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[\\#@!\\|/\\(\\)\\.,].*");
        boolean isCorrectLength = password.length() >= 7 && password.length() <= 25;

        if (isCorrectLength && hasUpperCase && hasNumber && hasSpecialChar) {
            return true;
        } else {
            System.out.println("""
                La contraseña debe contener entre 7 y 15 caracteres,
                al menos una mayúscula, un número y un carácter especial (#@!|/().,""");
            return false;
        }
    }

    private static Usuario iniciarSesion(){
        System.out.println("Inicio de sesión.");
        System.out.print("Gmail: ");
        String gmail = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            Usuario usuario = userService.getUserByEmail(gmail);
            if (usuario != null && usuario.getPassword().equals(password)) {
                System.out.println("Inicio de sesión exitoso.");
                return usuario;
            } else {
                System.out.println("Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al acceder a la base de datos.");
        }
        return null;
    }

    private static void registrarUsuario() {
        System.out.println("Registro de nuevo usuario.");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Gmail: ");
        String correo = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        if (!verifyPassword(password)) {
            return; // No continúa si la contraseña es inválida
        }

        String rol = "usuario";
        System.out.print("Si tienes clave de administrador, ingrésala ahora (Enter para continuar como usuario): ");
        String adminClave = scanner.nextLine();
        if (adminClave.equals("CanoBestAdmin")) {
            rol = "ADMIN";
        }

        try {
            long id = userService.create(new Usuario(0, nombre, correo, password, rol));
            if (id != -1) {
                System.out.println("Registro exitoso. ID: " + id);
            } else {
                System.out.println("Error al registrar usuario.");
            }
        } catch (SQLException e) {
            System.out.println("Error en la base de datos.");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            connectDatabase();
            introduccion();

            boolean running = true;
            while (running) {
                menuGeneral();
                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        Usuario usuario = iniciarSesion();
                        if (usuario != null) {
                            if (usuario.getRol().equals("ADMIN")) {
                                menuAdmin(usuario);
                            } else {
                                menuUsuario(usuario);
                            }
                        }
                        break;
                    case "2":
                        registrarUsuario();
                        break;
                    case "3":
                        System.out.println("Saliendo...");
                        running = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos.");
        } finally {
            pool.closeAll();
        }
    }

     private static void menuAdmin(Usuario admin) throws SQLException {
        while (true) {
            System.out.println("\nBienvenido Admin " + admin.getNombre() + " que desea hacer.");
            System.out.println("1. Gestionar Usuarios");
            System.out.println("2. Gestionar Libros");
            System.out.println("3. Gestionar Préstamos");
            System.out.println("4. Eliminar Cuenta de Admin");
            System.out.println("5. Salir");
            System.out.print("Ingrese su opción: ");

            int opcion = Integer.parseInt(scanner.nextLine());
            switch (opcion) {
                case 1:
                    gestionarUsuarios();
                    break;
                case 2:
                    gestionarLibros();
                    break;
                case 3:
                    gestionarPrestamos();
                    break;
                case 4:
                    userService.delete(admin.getId());
                    System.out.println("Cuenta de administrador eliminada.");
                    return;
                case 5:
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void gestionarUsuarios() throws SQLException {
        System.out.println("1. Ver Usuarios");
        System.out.println("2. Ver Préstamos de un Usuario");
        System.out.println("3. Banear Usuario");
        System.out.println("4. Volver");
        System.out.print("Ingrese su opción: ");
        int opcion = Integer.parseInt(scanner.nextLine());
        switch (opcion) {
            case 1:
                ArrayList<Usuario> usuarios = userService.requestAll();
                for (Usuario usuario : usuarios) {
                    System.out.println(usuario);
                }
                break;
            case 2:
                System.out.print("ID del usuario: ");
                long id = Long.parseLong(scanner.nextLine());
                ArrayList<Prestamo> prestamos = prestamoService.getPrestamosByUsuario(id);
                for (Prestamo prestamo : prestamos) {
                    System.out.println(prestamo);
                }
                break;
            case 3:
                System.out.print("ID del usuario a banear: ");
                id = Long.parseLong(scanner.nextLine());
                userService.delete(id);
                System.out.println("Usuario baneado.");
                break;
            case 4:
                return;
        }
    }

    private static void gestionarLibros() throws SQLException {
        System.out.println("1. Ver Libros");
        System.out.println("2. Añadir Libro");
        System.out.println("3. Modificar Libro");
        System.out.println("4. Eliminar Libro");
        System.out.println("5. Volver");
        System.out.print("Ingrese su opción: ");
        int opcion = Integer.parseInt(scanner.nextLine());
        switch (opcion) {
            case 1:
                System.out.println("Los libros disponibles en nuestro biblioteca digital son: ");
                ArrayList<Libro> libros = bookService.requestAll();
                for (Libro libro : libros) {
                    System.out.println(libro);
                }
                break;
            case 2:
                System.out.print("Título: ");
                String titulo = scanner.nextLine();
                System.out.print("Autor: ");
                String autor = scanner.nextLine();
                System.out.print("Stock: ");
                int stock = Integer.parseInt(scanner.nextLine());
                bookService.create(new Libro(0, autor, titulo, stock));
                System.out.println("Libro añadido.");
                break;
            case 3:
                System.out.print("ID del libro a modificar: ");
                long id = Long.parseLong(scanner.nextLine());
                System.out.print("Nuevo título: ");
                titulo = scanner.nextLine();
                System.out.print("Nuevo autor: ");
                autor = scanner.nextLine();
                System.out.print("Nuevo stock: ");
                stock = Integer.parseInt(scanner.nextLine());
                bookService.update(new Libro(id, autor, titulo, stock));
                System.out.println("Libro modificado.");
                break;
            case 4:
                System.out.print("ID del libro a eliminar: ");
                id = Long.parseLong(System.console().readLine());
                bookService.delete(id);
                System.out.println("Libro eliminado.");
                break;
            case 5:
                return;
        }
    }

    private static void gestionarPrestamos() throws SQLException {
        System.out.println("1. Ver Préstamos");
        System.out.println("2. Registrar Préstamo");
        System.out.println("3. Marcar Préstamo como Devuelto");
        System.out.println("4. Volver");
        System.out.print("Ingrese su opción: ");
        int opcion = Integer.parseInt(scanner.nextLine());
        switch (opcion) {
            case 1:
                ArrayList<Prestamo> prestamos = prestamoService.requestAll();
                for (Prestamo prestamo : prestamos) {
                    System.out.println(prestamo);
                }
                break;
            case 2:
                System.out.print("ID del usuario: ");
                long usuarioId = Long.parseLong(scanner.nextLine());
                System.out.print("ID del libro: ");
                long libroId = Long.parseLong(scanner.nextLine());
                prestamoService.create(new Prestamo(usuarioId, libroId, new java.sql.Date(System.currentTimeMillis()), null));
                System.out.println("Préstamo registrado.");
                break;
            case 3:
                System.out.print("ID del préstamo a devolver: ");
                long prestamoId = Long.parseLong(scanner.nextLine());
                Prestamo prestamo = prestamoService.requestById(prestamoId);
                if (prestamo != null) {
                    prestamo.setFechaDevolucion(new java.sql.Date(System.currentTimeMillis()));
                    prestamoService.update(prestamo);
                    System.out.println("Préstamo marcado como devuelto.");
                } else {
                    System.out.println("Préstamo no encontrado.");
                }
                break;
            case 4:
                return;
        }
    }

    private static void menuUsuario(Usuario usuario) throws SQLException {
        while (true) {
            System.out.println("\nBienvenido " + usuario.getNombre());
            System.out.println("1. Ver Libros");
            System.out.println("2. Buscar Libro por Título");
            System.out.println("3. Solicitar Préstamo");
            System.out.println("4. Ver Mis Préstamos");
            System.out.println("5. Devolver Libro");
            System.out.println("6. Modificar Datos Personales");
            System.out.println("7. Salir");
            System.out.print("Ingrese su opción: ");

            int opcion = Integer.parseInt(scanner.nextLine());
            switch (opcion) {
                case 1:
                    ArrayList<Libro> libros = bookService.requestAll();
                    for (Libro libro : libros) {
                        System.out.println(libro);
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el título del libro: ");
                    String titulo = scanner.nextLine();
                    Libro libroEncontrado = bookService.searchByTitle(titulo);
                    System.out.println(libroEncontrado);
                    break;
                case 3:
                    System.out.print("ID del libro a solicitar: ");
                    long libroId = Long.parseLong(scanner.nextLine());
                    prestamoService.create(new Prestamo(usuario.getId(), libroId, new java.sql.Date(System.currentTimeMillis()), null));
                    System.out.println("Préstamo solicitado.");
                    break;
                case 4:
                    ArrayList<Prestamo> prestamos = prestamoService.getPrestamosByUsuario(usuario.getId());
                    for (Prestamo prestamo : prestamos) {
                        System.out.println(prestamo);
                    }
                    break;
                case 5:
                    System.out.print("ID del préstamo a devolver: ");
                    long prestamoId = Long.parseLong(scanner.nextLine());
                    Prestamo prestamo = prestamoService.requestById(prestamoId);
                    if (prestamo != null && prestamo.getUsuarioId() == usuario.getId()) {
                        prestamo.setFechaDevolucion(new java.sql.Date(System.currentTimeMillis()));
                        prestamoService.update(prestamo);
                        System.out.println("Libro devuelto.");
                    } else {
                        System.out.println("No tienes este préstamo registrado.");
                    }
                    break;
                case 6:
                    System.out.print("Nuevo nombre: ");
                    String nuevoNombre = scanner.nextLine();
                    System.out.print("Nueva contraseña: ");
                    String nuevaContraseña = scanner.nextLine();
                    usuario.setNombre(nuevoNombre);
                    usuario.setPassword(nuevaContraseña);
                    userService.update(usuario);
                    System.out.println("Datos actualizados.");
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }
}
