package co.qualitysolutions.tecniamsa;

public class Item {
	private long id;
	private String rutaImagen;
	private String nombre;
	private String tipo;
    private String address;
    private String peso;


	public Item(long id, String nombre,String tipo ,String peso,String address, String rutaImagen) {
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
		this.rutaImagen = rutaImagen;
        this.setPeso(peso);
        this.address=address;
	}
    public Item(long id, String nombre,String tipo ,String address, String rutaImagen) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.rutaImagen = rutaImagen;
        this.setPeso(peso);
        this.address=address;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRutaImagen() {
		return rutaImagen;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }
}
