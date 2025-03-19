package usuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;

import crud.CRUD;
import dataset.DataSetInterface;

public class UserService implements CRUD<Usuario>, DataSetInterface{

	Connection conn;
    public UserService(Connection conn){
        this.conn = conn;
    }

	@Override
	public ArrayList<Usuario> requestAll() throws SQLException {
		ArrayList<Usuario> result = new ArrayList<>();
		String sql = "SELECT id, nombre, correo, password, rol FROM usuarios";
		try(PreparedStatement pstmt = this.conn.prepareStatement(sql);
			ResultSet querySet = pstmt.executeQuery(sql)) {
			while (querySet.next()) {
				long id = querySet.getLong("id");
				String nombre = querySet.getString("nombre");
				String correo = querySet.getString("correo");
				String password = querySet.getString("password");
				String rol = querySet.getString("rol");
				result.add(new Usuario(id,nombre,correo,password,rol));
			}
		} catch (SQLException e) {
			System.out.println(e.getErrorCode());
		}
		return result;
	}

	@Override
    public Usuario requestById(long id) throws SQLException {
        String sql = "SELECT nombre, correo, password, rol FROM usuarios WHERE id=?";
        Usuario result = null;

        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet querySet = pstmt.executeQuery()) {
                if (querySet.next()) {
                    String nombre = querySet.getString("nombre");
                    String correo = querySet.getString("correo");
                    String password = querySet.getString("password");
                    String rol = querySet.getString("rol");
                    result = new Usuario(id, nombre, correo, password, rol);
                }
            }
        }
        return result;
    }

	public Usuario getUserByEmail(String email) throws SQLException {
		String sql = "SELECT id, nombre, correo, password, rol FROM usuarios WHERE correo = ?";
		Usuario usuario = null;

		try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					usuario = new Usuario(
						rs.getLong("id"),
						rs.getString("nombre"),
						rs.getString("correo"),
						rs.getString("password"),
						rs.getString("rol")
					);
				}
			}
		}
		return usuario;
	}

	@Override
    // Crear un usuario
    public long create(Usuario model) throws SQLException {
        if (requestById(model.getId()) != null) {
            System.out.println("Este usuario ya existe.");
            return -1; // Indicar que no se pudo crear
        }

        String sql = "INSERT INTO usuarios (nombre, correo, password, rol) VALUES (?,?,?,?)";
        try (PreparedStatement pstmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, model.getNombre());
            pstmt.setString(2, model.getCorreo());
            pstmt.setString(3, model.getPassword());
            pstmt.setString(4, model.getRol());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
            }
        }
        return -1;
    }

	@Override
	public int update(Usuario object) throws SQLException {
		String sql = "UPDATE usuarios SET nombre = ?, correo = ?, password = ? , rol = ? WHERE id = ?";
		int affectedRows = 0;
		try(PreparedStatement pstmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, object.getNombre());
			pstmt.setString(2, object.getCorreo());
			pstmt.setString(3, object.getPassword());
			pstmt.setString(4, object.getRol());
			pstmt.setLong(5, object.getId()); //id de la consulta
			affectedRows = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return affectedRows;
	}

	@Override
	public boolean delete(long id) throws SQLException {
		String sql = "DELETE FROM usuarios WHERE id = ?";
		try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
			pstmt.setLong(1, id);
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				System.out.println("Usuario con ID " + id + " eliminado correctamente.");
				return true;
			} else {
				System.out.println("No se encontró un usuario con ID " + id + ".");
				return false;
			}
		}
	}

	@Override
	public void importFromCSV(String file) throws Exception {
		BufferedReader br = null;
        PreparedStatement prep = null;
        try {
            br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
            String line = "";
            while((line=br.readLine())!= null){
                Usuario user = new Usuario(line);
                String sql = "INSERT INTO usuarios (id, nombre, correo, password, rol) VALUES (?, ?, ?, ?,?) ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), correo=VALUES(correo), password=VALUES(password), rol=VALUES(correo)";
                prep = this.conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                prep.setInt(1, (int)user.getId());
                prep.setString(2, user.getNombre());
                prep.setString(3, user.getCorreo());
				prep.setString(4,user.getPassword());
				prep.setString(5,user.getRol());
                prep.execute();
            }
        } catch (IOException e) {
            throw new Exception("Ocurrión un error de E/S"+ e.toString());
        } catch (SQLTimeoutException e){
            throw new Exception("Ocurrión un error al acceder a la base de datos"+ e.toString());
        } catch (SQLException e){
            throw new Exception("Ocurrión un error al acceder a la base de datos"+ e.toString());
        } catch (Exception e){
            throw new Exception("Ocurrión un error "+ e.toString());
        } finally {
            if(prep != null)
                prep.close();
            if(br != null)
                br.close();
        }
	}

	@Override
	public void exportToCSV(String file) throws Exception {
		BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
            ArrayList<Usuario> usuarios = this.requestAll();
            for(Usuario user:usuarios){
                bw.write(user.serialize()+"\n");
            }
            bw.close();
        } catch(IOException e){
            throw new Exception("Ocurrión un error de E/S "+ e.toString());
        } catch(SQLException e){
            throw new Exception("Ocurrión un error al acceder a la base de datos "+ e.toString());
        }catch (Exception e) {
            throw new Exception("Ocurrión un error "+ e.toString());
        } finally {
            if(bw!=null)
                bw.close();
        }
	}

}

