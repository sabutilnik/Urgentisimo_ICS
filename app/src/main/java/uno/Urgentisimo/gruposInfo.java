package uno.Urgentisimo;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

class gruposInfo extends Application {
	private static final String TAG = "URGENTISIMO : GRUPOSINFO ";
	private  String grupoid = new String();
	private  String nombre = new String();
	private  String descripcion = new String();
	private String metagrupo;
	private String filtro;
	private  String[] integrantes = new String[4];
	private  ArrayList<String[]> miembros = new ArrayList<String[]>();
	private  ArrayList<String[]>actividades = new ArrayList<String[]>();
	private static int numOfGrupos = 0;
	private int id;
	
	public gruposInfo() {
		grupoid = new String();
		nombre = new String();
		descripcion = new String();
		metagrupo = "0";
		filtro = new String();	
		integrantes = new String[4];
		miembros = new ArrayList<String[]>();
		actividades = new ArrayList<String[]>();
		id = ++numOfGrupos;
	}
	
	public int getId() {
		return id;
	}
	
	public String getIdgrupo(){
		if (grupoid != null ) {
			return grupoid;
		}
		else {
			return "";
		}
	}
	
	public void setId(String c){
		grupoid = c;
	}
	public String getNombre() {
		if (nombre != null) {
			return nombre;
		}else {
			return "";
		}
	}
	public void setNombre(String c) {
		nombre = c;
	}
	public void  setDescripcion (String c ){
		descripcion = c;
	}
	public String getDesscripcion () {
		if (descripcion != null ) {
			return descripcion;
		} else {
			return "";
		}
	}
	public void setFiltro(String s) {
		filtro = s;
	}
	public String getFiltro () {
		if (filtro != null ) {
			return filtro;
		} else {
			return "";
		}
	}
	public void setMetagrupo(String s) {
		metagrupo = s;
	}
	
	public void addIntegrante (String id, String nombre, String fh_alta, String fh_baja ) {
//		Log.v(TAG," insertando "+id+" "+nombre+" "+fh_alta+" "+fh_baja);
//		Log.v(TAG,"antes ... ");
//		this.printData();
		String[] integrante = new String[4];
		integrante[0] = id;
		integrante[1] = nombre;
		integrante[2] = fh_alta;
		integrante[3] = fh_baja;
		miembros.add(integrante);
//		Log.v(TAG,"despues");
		
//		this.printData();
	}
	
	public ArrayList<String[]> getIntegrantes() {
		return miembros;
	}
	public void addActividades (String id, String act, String desc) {
		String[] actividad = new String[3];
		actividad[0] = id;
		actividad[1] = act;
		actividad[2] = desc;
		actividades.add(actividad);
	}
	public ArrayList<String[]> getActividades() {
		return actividades;
	}
	
	public boolean isMetagrupo() {
		boolean resultado = false;
		if (metagrupo.equalsIgnoreCase("1")) { resultado = true; }
		return resultado;
	}
	public void printData() {
		Log.v(TAG,"nombre :"+nombre);
		Log.v(TAG,"descripcion :"+descripcion);
		Log.v(TAG,"ID :"+grupoid);
		Log.v(TAG,"meta :"+this.isMetagrupo());
		Log.v(TAG,"filtro : "+filtro);
		
		for (int j = 0; j < actividades.size(); j++) {
			String[] actividad = actividades.get(j);
			Log.v(TAG,"actividad :"+actividad[0]+":"+actividad[1]+":"+actividad[2]);
		}
		for (int j = 0; j < miembros.size(); j++) {
			String[] actividad = miembros.get(j);
			Log.v(TAG,"integrantes :"+actividad[0]+":"+actividad[1]+":"+actividad[2]+":"+actividad[3]);
		}
	}
	public void clean() {
		nombre = "";
		descripcion = "";
		grupoid= "";
		actividades.clear();
		miembros.clear();
		filtro = "";
		metagrupo = "";
	}
	public class CreateView extends LinearLayout {
	    public CreateView(Context context) {
	        super(context);
	        setOrientation(LinearLayout.VERTICAL);

	        TextView mTestText = new TextView(context);
	        mTestText.setText(id+" "+nombre+" "+descripcion);

	        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	                LinearLayout.LayoutParams.FILL_PARENT,
	                LinearLayout.LayoutParams.WRAP_CONTENT);
	        lp.setMargins(10, 10, 10, 10);
	        addView(mTestText, lp);
	    }
	}
}