package prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import crud.CRUD;

public class PrestamoService implements CRUD<Prestamo>{
	private Connection conn;

    public PrestamoService(Connection conn) {
        this.conn = conn;
    }

    // Obtener todos los préstamos
    @Override
    public ArrayList<Prestamo> requestAll() throws SQLException {
        ArrayList<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion FROM prestamos";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                prestamos.add(new Prestamo(
                    rs.getLong("id"),
                    rs.getLong("usuario_id"),
                    rs.getLong("libro_id"),
                    rs.getDate("fecha_prestamo"),
                    rs.getDate("fecha_devolucion")
                ));
            }
        }
        return prestamos;
    }

    // Obtener un préstamo por ID
    @Override
    public Prestamo requestById(long id) throws SQLException {
        String sql = "SELECT usuario_id, libro_id, fecha_prestamo, fecha_devolucion FROM prestamos WHERE id = ?";
        Prestamo prestamo = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prestamo = new Prestamo(
                        id,
                        rs.getLong("usuario_id"),
                        rs.getLong("libro_id"),
                        rs.getDate("fecha_prestamo"),
                        rs.getDate("fecha_devolucion")
                    );
                }
            }
        }
        return prestamo;
    }

    // Crear un nuevo préstamo
    @Override
    public long create(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, prestamo.getUsuarioId());
            pstmt.setLong(2, prestamo.getLibroId());
            pstmt.setDate(3, prestamo.getFechaPrestamo());
            pstmt.setDate(4, prestamo.getFechaDevolucion());

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

    // Actualizar un préstamo
    @Override
    public int update(Prestamo prestamo) throws SQLException {
        String sql = "UPDATE prestamos SET usuario_id = ?, libro_id = ?, fecha_prestamo = ?, fecha_devolucion = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, prestamo.getUsuarioId());
            pstmt.setLong(2, prestamo.getLibroId());
            pstmt.setDate(3, prestamo.getFechaPrestamo());
            pstmt.setDate(4, prestamo.getFechaDevolucion());
            pstmt.setLong(5, prestamo.getId());

            return pstmt.executeUpdate();
        }
    }

    // Eliminar un préstamo
    @Override
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

	//Prestmo de un usuario
	public ArrayList<Prestamo> getPrestamosByUsuario(long usuarioId) throws SQLException {
		ArrayList<Prestamo> prestamos = new ArrayList<>();
		String sql = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion FROM prestamos WHERE usuario_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, usuarioId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					prestamos.add(new Prestamo(
						rs.getLong("id"),
						rs.getLong("usuario_id"),
						rs.getLong("libro_id"),
						rs.getDate("fecha_prestamo"),
						rs.getDate("fecha_devolucion")
					));
				}
			}
		}
		return prestamos;
	}
}
