/*
 * Class for grupos information.  It will handle all activity information, such as id, description, dates
 * etc.
 */

package uno.Urgentisimo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class actividad {
	private static final String TAG = "URGENTISIMO : ALUMNO CLASS : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	public Map<String,String> data;
	public String id;
	public DataBaseHelper db;
	public Map<String, Map<String,String>> grupos;
	public Map<String,Map<String,String>> criterios;
	public Map<String, Map<String,String>> asignaturas;
	private UtilsLib utils;
	private Context c;

	public actividad(Context c, DataBaseHelper db) {
		id = "nuevo";
		data = new HashMap();
		data.put("actividad", "");
		data.put("descripcion", "");
		data.put("duracion", "");
		data.put("fecha_inicio", "");
		data.put("fecha_fin", "");
		data.put("hecha", "");
		data.put("corregida", "");
		data.put("idactividad_general", "");
		data.put("idtipo_actividad", "");
		data.put("idagrupamiento", "");
		this.db = db;
	}

	/*
	 * Initializes actividad object with idactividad = id, gets data and
	 * groups info.
	 */

	public actividad(DataBaseHelper db, String id) {
		this.c = c;
		this.db = db;
		this.id = id;
		this.refresh();
	}

    public actividad() {

    }
	public void setId(String s) {
		id = s;
	}

	public String getId() {
		return id;
	}
	
	public String getnombre() {
		return this.getdata("actividad");
	}

	public void setdata(String field, String value) {
		data.remove(field);
		data.put(field, value);
	}

	public String getdata(String field) {
		return data.get(field);
	}

	public void refresh() {
		this.data = db.getMapId(String.format("select * from actividades where _id = '%s'", id));
		String sql = String.format("select actividades.*,criterios.*,criterios_actividad.* from criterios_actividad join criterios on criterios._id = criterios_actividad.idcriterio join actividades on criterios_actividad.idactividad where idactividad = '%s'", id);
		this.criterios = db.getMapField(sql, "idcriterio");
	}
	
	public Map<String,Map<String,String>> getgrupos() {
		if (this.grupos.size() < 1 ) {
			this.grupos = db.getMap(String.format("select * from grupos where _id in (select idgrupo from actividades_grupos where idactividad = %s)",this.id));
		}
		return this.grupos;
	}
	
	public Map<String, Map<String,String>> getCriterios() {
		if (this.criterios.size() < 1) {
			this.refresh();
		}
		return this.criterios;
	}
}

