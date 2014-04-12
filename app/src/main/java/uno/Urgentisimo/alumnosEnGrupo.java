package uno.Urgentisimo;

import java.util.ArrayList;

public class alumnosEnGrupo {
	private String alumnoid = new String();
	private String nombre = new String();
	private String esmiembro = new String();
	private static int numAlumnos = 0;
	private int id = 0;
	
	public alumnosEnGrupo() {
		alumnoid = new String();
		nombre = new String();
		esmiembro = new String();
		id = ++numAlumnos;
	}
	
	public int getId() {
		return id;
	}
	public void setAlumnoid (String i) {
		alumnoid = i;
	}
	public String getAlumnoid () {
		return alumnoid;
	}
	public void setNombre (String s) {
		nombre = s;
	}
	public String getNombre () {
		return nombre;
	}
	public void setMiembro (String s) {
		esmiembro = s;
	}
	public String getMiembro () {
		return esmiembro;
	}
}
