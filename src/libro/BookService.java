package libro;

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

public class BookService implements CRUD<Libro>, DataSetInterface{

	Connection conn;
    public BookService(Connection conn){
        this.conn = conn;
    }

	@Override
	public ArrayList<Libro> requestAll() throws SQLException {
		ArrayList<Libro> result = new ArrayList<>();
		String sql = "SELECT id, autor, titulo, stock FROM libros";
		try(PreparedStatement pstmt = this.conn.prepareStatement(sql);
			ResultSet querySet = pstmt.executeQuery()) {
			while (querySet.next()) {
				long id = querySet.getLong("id");
				String autor = querySet.getString("autor");
				String titulo = querySet.getString("titulo");
				int stock = querySet.getInt("stock");
				result.add(new Libro(id, autor, titulo, stock));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	//id, autor, titulo, stock --> atributos de libro
	@Override
	public Libro requestById(long id) throws SQLException {
		String sql = "SELECT autor, titulo, stock FROM libros WHERE id = ?";
		Libro result = null;
		try(PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
			pstmt.setLong(1,id);  // SE MUEVE AQUÍ ANTES DE executeQuery()
			try (ResultSet querySet = pstmt.executeQuery()) {
				if(querySet.next()){
					String autor = querySet.getString("autor");
					String titulo = querySet.getString("titulo");
					int stock = querySet.getInt("stock");
					result = new Libro(id, autor, titulo, stock); // Faltaba incluir el ID
				}
			}
		}

		return result;
	}

	public Libro searchByTitle(String tittle) throws SQLException{
		String sql = "SELECT autor, titulo, stock FROM libros WHERE titulo LIKE ?";
		Libro result = null;
		try(PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
			pstmt.setString(1, "%"+tittle+"%");
			try (ResultSet querySet = pstmt.executeQuery()) {
				if(querySet.next()){
					String autor = querySet.getString("autor");
					String titulo = querySet.getString("titulo");
					int stock = querySet.getInt("stock");
					result = new Libro(autor, titulo, stock);
				}
			}
		}
		return result;
	}

	@Override
	public long create(Libro model) throws SQLException {
		// Verificar si ya existe un libro con el mismo título y autor
		String checkSql = "SELECT id FROM libros WHERE titulo = ? AND autor = ?";
		try (PreparedStatement checkStmt = this.conn.prepareStatement(checkSql)) {
			checkStmt.setString(1, model.getTitulo());
			checkStmt.setString(2, model.getAutor());
			try (ResultSet rs = checkStmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("Este libro ya existe.");
					return -1;
				}
			}
		}

		// Validar stock
		if (model.getStock() < 0) {
			System.out.println("El stock no puede ser negativo.");
			return -1;
		}

		// Insertar el libro
		String sql = "INSERT INTO libros (titulo, autor, stock) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, model.getTitulo());
			pstmt.setString(2, model.getAutor());
			pstmt.setInt(3, model.getStock());

			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						return generatedKeys.getLong(1); // Devolver el ID generado
					}
				}
			}
		}
		return -1;
	}

	@Override
	public int update(Libro object) throws SQLException {
		String sql = "UPDATE libros SET autor = ?, titulo = ?, stock = ? WHERE id = ?";
		int affectedRows = 0;
		try(PreparedStatement pstmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, object.getAutor());
			pstmt.setString(2, object.getTitulo());
			pstmt.setInt(3, object.getStock());
			pstmt.setLong(4, object.getId()); //id de la consulta
			affectedRows = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return affectedRows;
	}

	@Override
	public boolean delete(long id) throws SQLException {
		String sql = "DELETE FROM libros WHERE id = ?";
		try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
			pstmt.setLong(1, id);
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				System.out.println("Libro con ID " + id + " eliminado correctamente.");
				return true;
			} else {
				System.out.println("No se encontró un libro con ID " + id + ".");
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
                Libro libro = new Libro(line);
                String sql = "INSERT INTO libros (id, autor, titulo, stock) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE autor=VALUES(autor), titulo=VALUES(titulo), stock=VALUES(stock)";
                prep = this.conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                prep.setInt(1, (int)libro.getId());
                prep.setString(2, libro.getAutor());
                prep.setString(3, libro.getTitulo());
				prep.setInt(4,libro.getStock());
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
            ArrayList<Libro> libros = this.requestAll();
            for(Libro libro:libros){
                bw.write(libro.serialize()+"\n");
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

