/*
 * Class for actividades information.  It will handle all activity information, such as id, description, dates
 * etc.
 */

package uno.Urgentisimo;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Actividad {
	private static final String TAG = "URGENTISIMO : ACTIVIDAD CLASS : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private DataBaseHelper db;
	private String idactividad;
	private Map<String, String> data;
	private ArrayList<Map<String, String>> grupos;
	private ArrayList<Map<String, String>> criterios;
	private ArrayList<Map<String, String>> materiales;
	private UtilsLib utils;
	private Context c;
	private Map<String,String> extras;

	public Actividad(Context c) {
		idactividad = "nuevo";
		data = new HashMap();
		data.put("actividad", "");
		data.put("duracion", "");
		data.put("fecha_inicio", "");
		data.put("fecha_fin", "");
		data.put("descripcion", "");
		data.put("hecha", "0");
		data.put("corregida", "0");
		data.put("idactividad_general", "0");
		data.put("idtipo_actividad", "0");
		data.put("idagrupamiento", "0");
		data.put("actividad_general", "");
		data.put("tipo_actividad", "");
		data.put("agrupamiento", "");
		extras = new HashMap();
		extras.put("actividad_general","");
		extras.put("tipo_actividad","");
		extras.put("agrupamiento","");
		extras.put("_id","");

		db = new DataBaseHelper(c.getApplicationContext());

		grupos = db.getMergedMapList("select * from grupos order by grupo",
				"select * from actividades_grupos where idactividad = '"
						+ idactividad + "'", "idgrupo");
		criterios = db.getMergedMapList(
				"select * from criterios order by criterio",
				"select * from criterios_actividad where idactividad = '"
						+ idactividad + "'", "idcriterio");
		materiales = db.getMergedMapList(
				"select * from materiales order by material",
				"select * from materiales_actividad where idactividad = '"
						+ idactividad + "'", "idmaterial");
	}

	/*
	 * Initializes actividadInfo object with idactividad = id, gets data and
	 * groups info.
	 */

	public Actividad(Context c, String id) {
		this.c = c;
		db = new DataBaseHelper(c.getApplicationContext());
		Log.v(TAG, "Initializing actividad " + id + " " + db.gettimestamp());
		idactividad = id;
		data = db.getMap(
				"select * from actividades where _id='" + idactividad + "'")
				.get(idactividad);
		grupos = db.getMergedMapList("select * from grupos order by grupo",
				"select * from actividades_grupos where idactividad = '"
						+ idactividad + "'", "idgrupo");
		criterios = db.getMergedMapList(
				"select * from criterios order by criterio",
				"select * from criterios_actividad where idactividad = '"
						+ idactividad + "'", "idcriterio");
		materiales = db.getMergedMapList(
				"select * from materiales order by material",
				"select * from materiales_actividad where idactividad = '"
						+ idactividad + "'", "idmaterial");

		data.put("actividad_general", db.getDato(
				data.get("idactividad_general"), "actividades_generales",
				"actividad_general"));
		data.put("actividad_general", db.getDato(data.get("idagrupamiento"),
				"agrupamientos", "agrupamiento"));
		data.put("actividad_general", db.getDato(data.get("idtipo_actividad"),
				"tipos_actividades", "tipo_actividad"));
		
		extras = new HashMap();
		extras.put("actividad_general","");
		extras.put("tipo_actividad","");
		extras.put("agrupamiento","");
		extras.put("_id","");
		Log.v(TAG, "Finish initialization " + db.gettimestamp());
	}

	public void setIdactividad(String s) {
		idactividad = s;
	}

	public String getIdactividad() {
		return idactividad;
	}

	public void setinfo(String field, String value) {
		data.remove(field);
		data.put(field, value);
	}

	public String getdata(String field) {
		String tmp = "";
		if (field.equalsIgnoreCase("tipo_actividad")
				|| field.equalsIgnoreCase("actividad_general")
				|| field.equalsIgnoreCase("agrupamiento")) {

		} else {
			tmp = data.get(field);
		}
		if (tmp == null) {
			return "";
		}
		return data.get(field);
	}

	public void addGrupo(String grupoid) {
		ArrayList<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		for (int i = 0; i < grupos.size(); i++) {
			Map<String, String> grupo = grupos.get(i);
			if (grupo.get("_id").equalsIgnoreCase(grupoid)) {
				grupo.put("checked", "1");
			}
			temp.add(grupo);
		}
		this.grupos = temp;
	}

	public void removeGrupo(String grupoid) {
		ArrayList<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		for (int i = 0; i < grupos.size(); i++) {
			Map<String, String> grupo = grupos.get(i);
			if (grupo.get("_id").equalsIgnoreCase(grupoid)) {
				grupo.put("checked", "0");
			}
			temp.add(grupo);
		}
		this.grupos = temp;
	}

	public ArrayList<Map<String, String>> getgrupos() {
		return grupos;
	}

	public ArrayList<Map<String, String>> getcriterios() {
		return criterios;
	}

	public ArrayList<Map<String, String>> getmateriales() {
		return materiales;
	}

	public void removeCriterio(String criterioid) {
		ArrayList<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		for (int i = 0; i < criterios.size(); i++) {
			Map<String, String> criterio = criterios.get(i);
			if (criterio.get("_id").equalsIgnoreCase(criterioid)) {
				criterio.put("checked", "0");
			}
			temp.add(criterio);
		}
		this.criterios = temp;
	}

	public void addCriterio(String criterioid) {
		ArrayList<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		for (int i = 0; i < criterios.size(); i++) {
			Map<String, String> criterio = criterios.get(i);
			if (criterio.get("_id").equalsIgnoreCase(criterioid)) {
				criterio.put("checked", "1");
			}
			temp.add(criterio);
		}
		this.criterios = temp;
	}

	public void removeMaterial(String materialid) {
		ArrayList<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		for (int i = 0; i < materiales.size(); i++) {
			Map<String, String> material = materiales.get(i);
			if (material.get("_id").equalsIgnoreCase(materialid)) {
				material.put("checked", "0" + "");
			}
			temp.add(material);
		}
		this.materiales = temp;
	}

	public void addMaterial(String materialid) {
		ArrayList<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		for (int i = 0; i < materiales.size(); i++) {
			Map<String, String> material = materiales.get(i);
			if (material.get("_id").equalsIgnoreCase(materialid)) {
				material.put("checked", "1");
			}
			temp.add(material);
		}
		this.materiales = temp;
	}

	public void setToc(String idcriterio, String toc) {
		for (int i = 0; i < criterios.size(); i++) {
			Map<String, String> criterio = criterios.get(i);
			if (criterio.get("_id").equalsIgnoreCase(idcriterio)) {
				criterio.put("toc", toc);
				return;
			}
		}

	}

	public String saveActividad(int tipo) {
		String values = "";
		String sentence = "";
		String stemp = "";
		String resultado = data.get("actividad");
		ArrayList<String> loscampos = new ArrayList<String>();
		loscampos.add("idagrupamiento");
		loscampos.add("idtipo_actividad");
		loscampos.add("idactividad_general");loscampos.add("corregida");loscampos.add("descripcion");loscampos.add("hecha");loscampos.add("fecha_inicio");
		loscampos.add("fecha_fin");loscampos.add("actividad"); loscampos.add("duración");
		
		Iterator it = data.entrySet().iterator();

		if (this.idactividad.equalsIgnoreCase("nuevo")) {
			tipo = INSERT;
		}
		Log.v(TAG, "save : tipo= " + tipo);
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String lakey = pairs.getKey().toString();
			if (lakey.equalsIgnoreCase("actividad")) {
				Log.v(TAG,"actividad ");
			}
			if (extras.get(lakey) == null) { 
				if (pairs.getKey().toString().equals("idgrupo") == false && pairs.getKey().toString().equals("idactividad") == false && pairs.getKey().toString().equals("checked") == false) {
					switch (tipo) {
					case INSERT:
						sentence = sentence + pairs.getKey() + ",";
						values = values + pairs.getValue() + "','";
						Log.v(TAG, " insert : " + sentence + " : " + values);
						break;
					case UPDATE:
						sentence = sentence + pairs.getKey() + "='"
								+ pairs.getValue() + "',";
						values = ",'";
						break;
					}
				}
			}
		}
		sentence = sentence.substring(0, sentence.length() - 1);
		values = values.substring(0, values.length() - 2);
		switch (tipo) {
		case INSERT:
			stemp = "insert into actividades(" + sentence + ") values ('"
					+ values + ")";
			break;
		case UPDATE:
			stemp = "update actividades set " + sentence + " where _id = '"
					+ idactividad + "'";
			break;
		}
		Log.v(TAG, "INSERT/UPDATE : " + stemp);
		db.insertSql(stemp);
		resultado = db.getId("actividades", data.get("actividad"));
		Log.v(TAG, " nueva actividad  :" + resultado);
		idactividad = resultado;
		db.delete("delete from actividades_grupos where idactividad = '"
				+ idactividad + "'");
		for (int i = 0; i < grupos.size(); i++) {
			if (grupos.get(i).get("checked").equalsIgnoreCase("0")) {
				continue;
			}
			db.insertSql("insert into actividades_grupos (idactividad, idgrupo) values('"
					+ idactividad + "','" + grupos.get(i).get("_id") + "')");
		}
		db.delete("delete from criterios_actividad where idactividad = '"
				+ idactividad + "'");
		for (int i = 0; i < criterios.size(); i++) {
			if (criterios.get(i).get("checked").equalsIgnoreCase("0")) {
				continue;
			}
			db.insertSql("insert into criterios_actividad (idactividad, idcriterio, toc) values('"
					+ idactividad
					+ "','"
					+ criterios.get(i).get("_id")
					+ "','"
					+ criterios.get(i).get("toc") + "')");
		}
		db.delete("delete from materiales_actividad where idactividad = '"
				+ idactividad + "'");
		for (int i = 0; i < materiales.size(); i++) {
			if (criterios.get(i).get("checked").equalsIgnoreCase("0")) {
				continue;
			}
			db.insertSql("insert into materiales_actividad (idactividad, idmaterial) values('"
					+ idactividad + "','" + materiales.get(i).get("_id") + "')");
		}
		return resultado;
	}

	public void setdata(String elcampo, String string) {
		Log.v(TAG,"set data "+elcampo+" : "+string);
		data.put(elcampo, string);

	}

	public void deleteActividad() {
		// TODO Auto-generated method stub
		
	}

	public String copiarActividad() {
		data.put("actividad",data.get("actividad")+"_1");
//		data.put("_id", "nuevo");
		this.idactividad = "nuevo";
		this.idactividad = this.saveActividad(INSERT);
		data.put("idactividad", this.idactividad);
		return this.idactividad;
	}

}
