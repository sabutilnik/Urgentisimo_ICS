package uno.Urgentisimo;

import java.util.ArrayList;

import android.util.Log;

public class criterioInfo {
	private static final String TAG = "URGENTISIMO : CRITINFO :";
	private String criterioid;
	private String nombre;
	private String descripcion;
	private String peso;
	private String toc;
	private ArrayList<String> contenidos;
	private ArrayList<String> actividades;
	private int numCriterios = 0;
	private int id = 0;
	
	public criterioInfo() {
		criterioid = new String();
		nombre = new String();
		descripcion = new String();
		peso = "";
		toc = "";
		contenidos = new ArrayList<String>();
		actividades = new ArrayList<String>();	
		id = ++numCriterios;
	}
	

	public String getToc() {
		return toc;
	}
	public String getPeso() {
		return peso;
	}
	public void setToc(String c) {
		toc = c;
	}
	public void setPeso(String c) {
		peso = c;
	}
	public int getId() {
		return id;
	}
	public String getCriterioid () {
		return criterioid;
	}
	public void setCriterioid (String i) {
		criterioid = i;
	}
	public String getAlumnoid () {
		return criterioid;
	}
	public void setNombre (String s) {
		nombre = s;
	}
	public String getNombre () {
		return nombre;
	}
	public void addContenido (String s) {
		Log.v(TAG,"eoooo "+s);
		contenidos.add(s);
	}
	public ArrayList<String> getContenidos () {
		return contenidos;
	}
	public void addActividad (String s) {
		actividades.add(s);
	}
	public ArrayList<String> getActividades () {
		return actividades;
	}

	public int getContenidosCount() {
		return contenidos.size();
	}

	public int getActividadesCount() {
		return actividades.size();
	}

	public String getContenido(int k) {
		return contenidos.get(k);
	}
	public String getActividad(int k) {
		return actividades.get(k);
	}

	public void setDescripcion(String criteriodescripcion) {
		descripcion = criteriodescripcion;
	}

	public CharSequence getDescripcion() {
		return descripcion;
	}
}
