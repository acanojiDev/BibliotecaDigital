package prestamo;

import java.sql.Date;

public class Prestamo {
	private long id;
    private long usuarioId;
    private long libroId;
    private Date fechaPrestamo;
    private Date fechaDevolucion;

    // Constructor completo
    public Prestamo(long id, long usuarioId, long libroId, Date fechaPrestamo, Date fechaDevolucion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    // Constructor sin ID (para nuevos registros)
    public Prestamo(long usuarioId, long libroId, Date fechaPrestamo, Date fechaDevolucion) {
        this(0, usuarioId, libroId, fechaPrestamo, fechaDevolucion);
    }

    public long getId() {
		return id;
	}
    public long getUsuarioId() {
		return usuarioId;
	}
    public long getLibroId() {
		return libroId;
	}
    public Date getFechaPrestamo() {
		return fechaPrestamo;
	}
    public Date getFechaDevolucion() {
		return fechaDevolucion;
	}

    public void setId(long id) {
		this.id = id;
	}
    public void setUsuarioId(long usuarioId) {
		this.usuarioId = usuarioId;
	}
    public void setLibroId(long libroId) {
		this.libroId = libroId;
	}
    public void setFechaPrestamo(Date fechaPrestamo) {
		this.fechaPrestamo = fechaPrestamo;
	}
    public void setFechaDevolucion(Date fechaDevolucion) {
		this.fechaDevolucion = fechaDevolucion;
	}

    @Override
    public String toString() {
        return "Prestamo [id=" + id + ", usuarioId=" + usuarioId + ", libroId=" + libroId +
            ", fechaPrestamo=" + fechaPrestamo + ", fechaDevolucion=" + fechaDevolucion + "]";
    }
}

