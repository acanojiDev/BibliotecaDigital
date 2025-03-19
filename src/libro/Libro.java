package libro;

import mySerializer.MySerializer;

public class Libro implements MySerializer{
	private long id;
	private String autor;
	private String titulo;
	private int stock;

	public Libro(long id, String autor, String titulo, int stock){
		this.id = id;
		this.autor = autor;
		this.titulo = titulo;
		this.stock = stock;
	}

	public Libro(String autor, String titulo, int stock){
		this.autor = autor;
		this.titulo = titulo;
		this.stock = stock;
	}

	public Libro(){
		this(0, "", "", 0);
	}
	public Libro(String data){
		deserialize(data);
	}
	public Libro(Libro libro){
		this.id = libro.id;
		this.autor = libro.autor;
		this.titulo = libro.titulo;
		this.stock = libro.stock;
	}

	public String getAutor() {
		return autor;
	}
	public long getId() {
		return id;
	}
	public int getStock() {
		return stock;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setStock(int stock) {
		if (stock < 0) {
			throw new IllegalArgumentException("El stock no puede ser negativo.");
		}
		this.stock = stock;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Override
	public String serialize() {
		return String.format("\"%d\";\"%s\";\"%s;%s", this.id, this.autor, this.titulo, this.stock);
	}
	private String substractQuotes(String data){
        return data.substring(1, data.length()-1);
    }
	@Override
    public void deserialize(String data) {
        String[] datos = data.split(";");

        this.id = Integer.parseInt(datos[0].substring(1, datos[0].length()-1));
        this.autor = this.substractQuotes(datos[1]);
        this.titulo = this.substractQuotes(datos[2]);
		this.stock = Integer.parseInt(datos[3]);
    }
	@Override
    public String toString() {
        return String.format("ID: %d, Autor: %s, Titulo: %s, Stock: %d", this.id, this.autor, this.titulo, this.stock);
    }
}
