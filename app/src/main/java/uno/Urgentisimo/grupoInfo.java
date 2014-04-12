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

public class grupoInfo {
	private static final String TAG = "URGENTISIMO : GRUPOINFO CLASS : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private Map<String,String> data;
	private String id;
	private DataBaseHelper db;
	private ArrayList<Map<String,String>> alumnos;
	private ArrayList<Map<String,String>> actividades;
	private UtilsLib utils;
	private Context c;

	public grupoInfo(Context c, DataBaseHelper db) {
		id = "nuevo";
		data = new HashMap();
		alumnos = new ArrayList<Map<String,String>>();
		data.put("grupo", "");
		data.put("descripcion", "");
		data.put("metagrupo", "0");
		data.put("filtro", "");
		this.db = db;
	}

	/*
	 * Initializes grupoInfo object with idgrupo = id, gets data and
	 * groups info.
	 */

	public grupoInfo(DataBaseHelper db, String id) {
		this.c = c;
		this.db = db;
		this.id = id;
		this.refresh();
	}

	public ArrayList<Map<String,String>> getalumnos() {
		return alumnos;
	}
	
	public void setId(String s) {
		id = s;
	}

	public String getId() {
		return id;
	}
	
	public String getnombre() {
		return this.getdata("grupo");
	}

	public void setdata(String field, String value) {
		data.remove(field);
		data.put(field, value);
	}

	public String getdata(String field) {
		return data.get(field);
	}

	public void addAlumno(String alumnoid) {
		ArrayList<Map<String,String> > temp = new ArrayList<Map<String,String>>();
		for (int i = 0; i < alumnos.size(); i++) {
			Map<String, String> alumno = alumnos.get(i);
			if (alumno.get("_id") == alumnoid) {
				alumno.put("checked", "1");
			}
			temp.add(alumno);
		}
		this.alumnos = temp;
	}

	public void refresh() {
		this.data = db.getMapId(String.format("select * from grupos where _id = '%s'", id));
		if (this.data.get("metagrupo").equals("1")) {
			this.alumnos = db.getMapList(this.data.get("filtro"));
			this.alumnos = utils.mergelists(this.alumnos, db.getMapList("select * from alumnos"), "idalumno", "_id");
			
		} else {
			this.alumnos = db.getMergedMapList("select * from alumnos",String.format("select * from alumnos_grupos where idgrupo = '%s'", id), "idalumno");
		}
		this.actividades = db.getMergedMapList(String.format("select * from actividades",id), String.format("select * from actividades_grupos where idgrupo = '%s'", id), "idactividad");
	}

	public String save(Integer tipo) {
		String resp = "";
		String sql = "";
		String keys = "";
		String values = "";
		if (id.equalsIgnoreCase("nuevo")) {			
			for (Map.Entry<String, String> entry : data.entrySet())
			{
				values = values+"'"+entry.getValue()+"',";
				keys = keys+entry.getKey()+",";
			}
			keys = keys.substring(0,keys.length()-1);
			values = values.substring(0,values.length()-1);
			sql = "insert into grupos("+keys+") values ("+values+")"; 
		} else {
			sql = "update grupos set ";
			for (Map.Entry<String, String> entry : data.entrySet())
			{
				sql = sql+entry.getKey()+"='"+entry.getValue()+"',";
			}
			sql = sql.substring(0, sql.length()-1);
			sql = sql + " where _id ='"+data.get("_id")+"'"; 
		}
		Log.v(TAG,sql);
		db.insertSql(sql);
		this.id = db.getString("select MAX(_id) from grupos");
		sql = String.format("delete from alumnos_grupos where idgrupo = '%s'", id);
		db.insertSql(sql);
		for (int i = 0; i< alumnos.size(); i++) {
			Map<String,String> alumno = alumnos.get(i);
				sql = String.format("insert into alumnos_grupos(idgrupo,idalumno,fecha_alta,fecha_baja) values('%s','%s','%s','%s')", alumno.get("_id"),this.getId(),alumno.get("fecha_alta"),alumno.get("fecha_baja"));
				db.insertSql(sql);
				Log.v(TAG,sql);	
		}
		sql = String.format("delete from actividades_grupos where idgrupo = '%s'", id);
		db.insertSql(sql);
		for (int i = 0; i< actividades.size(); i++) {
			Map<String,String> actividad = actividades.get(i);
				sql = String.format("insert into actividades_grupos(idgrupo,idactividad,hecho) values('%s','%s','%s')", actividad.get("_id"),this.getId(),actividad.get("hecho"));
				db.insertSql(sql);
				Log.v(TAG,sql);	
		}
		return this.id;
	}
	
	public void delete() {
		String sql = String.format("delete from actividades_grupos where  idgrupo ='%s'",id);
		db.insertSql(sql);
		sql = String.format("delete from alumnos_grupos where  idgrupo ='%s'",id);
		db.insertSql(sql);
		sql = String.format("delete from grupos where  _id ='%s'",id);
		db.insertSql(sql);
	}
}
