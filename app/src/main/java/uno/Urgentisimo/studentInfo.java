package uno.Urgentisimo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Application;
import android.util.Log;

class studentInfo extends Application {
	private static final String TAG = "URGENTISIMO : STUDENTINFO ";
	private Map<String, String> alumno_info = new HashMap<String, String>();

	public String getAlumnoInfo(String s){
		Log.v(TAG,"get info "+s);
		if (alumno_info.get(s) == null) {
			return "";
		}
		else {
			return alumno_info.get(s);
		}
	}
	public void setAlumnoInfo(String c, String v){
		Log.v(TAG,"setalumno "+c+" "+v);
		try {
			alumno_info.remove(c);
		} catch (Exception e) {
			
		}
		alumno_info.put(c, v);
	}
	public void nueva() {
		alumno_info.put("eo", "doo");
	}
	public String getNombreCompleto() {
		String nombre = alumno_info.get("nombre")+" "+alumno_info.get("apellidos"); 
		return nombre;
	}
	public String getNumNombre() {
		String dato = alumno_info.get("numero_clase")+" - "+getNombreCompleto();
		Log.v(TAG,"numnom "+dato);
		return dato;
	}
	public boolean getChecked(String string) {
		String dato = alumno_info.get(string);
		if (dato.equals("1")) {
			return true;
		} 
		return false;
	}
	public void printData() {
		Collection c = alumno_info.values();
		Iterator itr = c.iterator();
		String s = new String();
	    while(itr.hasNext())
	    	s = itr.next().toString();
	    	if (s != null ) {
	    		Log.v(TAG,itr.next().toString());
	    	}
	    }
	}
//}