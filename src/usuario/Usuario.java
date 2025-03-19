package usuario;

import mySerializer.MySerializer;

public class Usuario implements MySerializer{
	private long id;
    private String nombre;
    private String correo;
    private String password;
    private String rol;

	public Usuario(long id, String nombre, String correo, String password, String rol){
		this.id = id;
		this.nombre = nombre;
		this.correo = correo;
		this.password = password;
		this.rol = rol;
	}

	public Usuario(String correo, String password){
		this.correo = correo;
		this.password = password;
	}


	public Usuario(String data){
		deserialize(data);
	}
	public Usuario(Usuario user){
        this.id = user.id;
        this.nombre = user.nombre;
        this.correo = user.correo;
        this.password = user.password;
		this.rol = user.rol;
    }

	public String getCorreo() {
		return correo;
	}

	public long getId() {
		return id;
	}
	public String getNombre() {
		return nombre;
	}
	public String getPassword() {
		return password;
	}
	public String getRol() {
		return rol;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}
	@Override
    public String serialize() {
        return String.format("\"%d\";\"%s\";\"%s;%s", this.id, this.nombre, this.correo, this.password,this.rol);
    }
	private String substractQuotes(String data){
        return data.substring(1, data.length()-1);
    }
	@Override
    public void deserialize(String data) {
        String[] datos = data.split(";");

        this.id = Integer.parseInt(datos[0].substring(1, datos[0].length()-1));
        this.nombre = this.substractQuotes(datos[1]);
        this.correo = this.substractQuotes(datos[2]);
		this.password = this.substractQuotes(datos[3]);
		this.rol = this.substractQuotes(datos[4]);

    }
	@Override
    public String toString() {
        return String.format("ID: %d, Nombre: %s, Gmail: %s, Contraseña: %s, Rol: %s", this.id, this.nombre, this.correo, this.password, this.rol);
    }
}
