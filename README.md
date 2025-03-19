# 📚 Biblioteca CRUD

## Descripción
Un sistema de gestión de biblioteca con funcionalidad CRUD para libros, usuarios y préstamos. Permite a los administradores gestionar la biblioteca y a los usuarios solicitar préstamos de libros.

## Características
- Gestión de usuarios (registro, eliminación, autenticación).
- Gestión de libros (añadir, modificar, eliminar, buscar).
- Gestión de préstamos (solicitar, devolver, listar préstamos).
- Uso de JDBC para la conexión con MySQL.
- Patrón de diseño DAO para la persistencia de datos.

## Tecnologías utilizadas
- Java 11+
- MySQL
- JDBC

## Instalación
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/biblioteca-crud.git
   ```
2. Importa el proyecto en tu IDE preferido.
3. Asegúrate de tener MySQL instalado y ejecutando.
4. Crea la base de datos usando los archivos SQL proporcionados.
5. Configura las credenciales de la base de datos en `ConnectionPool.java`.
6. Ejecuta la aplicación.

## Uso
1. Inicia sesión como administrador o usuario.
2. Como administrador, puedes gestionar usuarios, libros y préstamos.
3. Como usuario, puedes buscar libros y solicitar préstamos.

## Contribución
Si deseas contribuir:
1. Haz un fork del repositorio.
2. Crea una rama (`git checkout -b nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m "Añadir nueva funcionalidad"`).
4. Sube los cambios (`git push origin nueva-funcionalidad`).
5. Abre un Pull Request.

---

# 📚 Library CRUD

## Description
A library management system with CRUD functionality for books, users, and loans. It allows administrators to manage the library and users to request book loans.

## Features
- User management (registration, deletion, authentication).
- Book management (add, modify, delete, search).
- Loan management (request, return, list loans).
- Uses JDBC for MySQL connection.
- DAO pattern for data persistence.

## Technologies Used
- Java 11+
- MySQL
- JDBC

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-user/library-crud.git
   ```
2. Import the project into your preferred IDE.
3. Ensure MySQL is installed and running.
4. Create the database using the provided SQL files.
5. Configure database credentials in `ConnectionPool.java`.
6. Run the application.

## Usage
1. Log in as an admin or user.
2. As an admin, you can manage users, books, and loans.
3. As a user, you can search for books and request loans.

## Contribution
If you want to contribute:
1. Fork the repository.
2. Create a branch (`git checkout -b new-feature`).
3. Make your changes and commit (`git commit -m "Add new feature"`).
4. Push changes (`git push origin new-feature`).
5. Open a Pull Request.
