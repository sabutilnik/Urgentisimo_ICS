/*
 * Class for alumno information.  It will handle all activity information, such as id, description, dates
 * etc.
 */

package uno.Urgentisimo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jxl.write.WriteException;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
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
import android.widget.Toast;

public class alumnoInfo {
	private static final String TAG = "URGENTISIMO : ALUMNO CLASS : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private static final int ASIGNATURAS = 1;
	private static final int ACTIVIDADES = 2;
	private static final int CRITERIOS = 3;
	private Map<String,String> data;
	private String id;
	private DataBaseHelper db;
	private ArrayList<Map<String, String>> grupos;
	private Map<String,Map<String,String>> gruposmap;
	private ArrayList<Map<String,String>> criterios;
	private Map<String,Map<String,String>> criteriosmap;
	private ArrayList<Map<String,String>> actividades;
	private Map<String,Map<String,String>> actividadesmap;
	
	private ArrayList<Map<String,String>> asignaturas;
	private Map<String,Map<String,String>> asignaturasmap;
	private ArrayList<Map<String,String>> evaluaciones;
	private Map<String,String> evaluacionesActividad;
	private Map<String,String> evaluacionesCriterio;	

	private UtilsLib utils;
	private Context c;
	
	private String fecha1;
	private String fecha2;

	public alumnoInfo(Context c, DataBaseHelper db) {
		id = "nuevo";
		data = new HashMap();
		data.put("nombre", "");
		data.put("apellidos", "");
		data.put("clase", "");
		data.put("numero_clase", "");
		data.put("apellidos", "");
		data.put("direccion", "");
		data.put("telefono_alum", "");
		data.put("correo_alum", "");
		data.put("padre", "");
		data.put("telefono_pad", "");
		data.put("correo_pad", "");
		data.put("madre", "");
		data.put("telefono_mad", "");
		data.put("correo_mad", "");
		data.put("observaciones", "");
		data.put("comedor", "0");
		data.put("solo_a_casa", "0");
		data.put("refuerzo", "0");
		data.put("compensatoria", "0");
		data.put("acnee", "0");
		data.put("extraescolares_l", "0");
		data.put("extraescolares_m", "0");
		data.put("extraescolares_x", "0");
		data.put("extraescolares_j", "0");
		data.put("extraescolares_v", "0");	
		data.put("permiso_blog", "0");
		data.put("fecha_nac", "");
		data.put("pic", "");		

		this.db = db;
		this.utils = new UtilsLib(c);
		this.refreshalumno();
	}

	/*
	 * Initializes alumnoInfo object with idalumno = id, gets data and
	 * groups info.
	 */

	public alumnoInfo(DataBaseHelper db, String id) {
		this.c = c;
		this.db = db;
		this.id = id;
		this.utils = new UtilsLib(c);
		this.fecha1 = "2013-12-01";
		this.fecha2 = "2014-04-01";
		this.evaluacionesActividad = new HashMap<String,String>();
		this.evaluacionesCriterio = new HashMap<String,String>();
		this.refreshalumno();
	}

	public void setId(String s) {
		id = s;
	}

	public String getId() {
		return id;
	}
	
	public void setfechas(String f1, String f2) {
		this.fecha1 = f1;
		this.fecha2 = f2;
	}
	
	public String getnombre() {
		return this.getdata("nombre")+" "+this.getdata("apellidos");
	}

	public void setdata(String field, String value) {
		data.remove(field);
		data.put(field, value);
	}

	public ArrayList<Map<String,String>> getevaluaciones() {
		return this.evaluaciones;
	}
	public String getdata(String field) {
		return data.get(field);
	}
	
	public String getalumnodata(String texto) {
		String resp = "";
		if (data.get(texto) == null) {
			if (texto == "numero_clase") {
				resp = "No";
			} else {
			 resp = texto;
			}
		} else {
			resp = data.get(texto);
		}
		return resp;
	}

	public void addGrupo(String grupoid) {
		ArrayList<Map<String,String> > temp = new ArrayList<Map<String,String>>();
		for (int i = 0; i < grupos.size(); i++) {
			Map<String, String> grupo = grupos.get(i);
			if (grupo.get("_id") == grupoid) {
				grupo.put("checked", "1");
			}
			temp.add(grupo);
		}
		this.grupos = temp;
	}

	public void removeGrupo(String grupoid) {
		ArrayList<Map<String,String> > temp = new ArrayList<Map<String,String>>();
		for (int i = 0; i < grupos.size(); i++) {
			Map<String, String> grupo = grupos.get(i);
			if (grupo.get("_id") == grupoid) {
				grupo.put("checked", "0");
			}
			temp.add(grupo);
		}
		this.grupos=temp;
	}

	public ArrayList<Map<String, String>> getgrupos() {
		for (int i = 0; i < grupos.size(); i++) {
			Map<String,String> grp = grupos.get(i);
			if (grp.get("checked") == "1") {
				Log.v(TAG,"es miembro "+grp.get("grupo"));
			}
		}
		return grupos;
	}

	public ArrayList<Map<String, String>> getcriterios() {
		if (this.criterios == null) {
			this.refreshcriterios();
		}
		return this.criterios;
	}

	
	public void showcriterio(String elid) {
		ArrayList<Map<String,String>> temp = new ArrayList<Map<String,String>>();
		for (int i = 0; i < this.criterios.size(); i++) {
			Map<String,String> item = criterios.get(i);
			if (item.get("idcriterio").equalsIgnoreCase(elid)) {
				if (item.get("show").equalsIgnoreCase("1")) {
					item.put("show","0");
				} else {
					item.put("show","1");
				}
			}
			temp.add(item);
		}
		this.criterios = temp;
	}
	
	public void showactividad(String elid) {
		ArrayList<Map<String,String>> temp = new ArrayList<Map<String,String>>();
		for (int i = 0; i < this.actividades.size(); i++) {
			Map<String,String> item = actividades.get(i);
			if (item.get("idactividad").equalsIgnoreCase(elid)) {
				if (item.get("show").equalsIgnoreCase("1")) {
					item.put("show","0");
				} else {
					item.put("show","1");
				}
			}
			temp.add(item);
		}
	}
	
	public void showasignatura(String criterioid) {
		ArrayList<Map<String,String>> temp = new ArrayList<Map<String,String>>();
		for (int i = 0; i < this.asignaturas.size(); i++) {
			Map<String,String> item = asignaturas.get(i);
			if (item.get("_id").equalsIgnoreCase(criterioid)) {
				if (item.get("show").equalsIgnoreCase("1")) {
					item.put("show","0");
				} else {
					item.put("show","1");
				}
			}
			temp.add(item);
		}
	}
	

	public void removeCriterio(String criterioid) {
		for (int i = 0; i < criterios.size(); i++) {
			Map<String, String> criterio = criterios.get(i);
			if (criterio.get("_id") == criterioid) {
				criterio.put("checked", "0");
				return;
			}
		}
	}

	public void addCriterio(String criterioid) {
		for (int i = 0; i < criterios.size(); i++) {
			Map<String, String> criterio = criterios.get(i);
			if (criterio.get("_id") == criterioid) {
				criterio.put("checked", "1");
				return;
			}
		}
	}

	public void setToc(String idcriterio, String toc) {
		for (int i = 0; i<criterios.size(); i++) {
			Map<String,String> criterio = criterios.get(i);
			if (criterio.get("_id").equalsIgnoreCase(idcriterio)) {
				criterio.put("toc", toc);
				return;
			}
		}	
	}
	
	public void refreshGrupos() {
		this.grupos = db.getMergedMapList("select * from grupos",String.format("select * from alumnos_grupos where idalumno = '%s'",id) ,"idalumno");
	}

	public void refreshevaluaciones() {
		if (this.grupos == null) {
			this.refreshGrupos();
		}
		if (this.evaluaciones == null){
			this.evaluaciones = utils.andlist(db.getMapList("select * from actividades_grupos"), this.grupos, "idgrupo", "_id",1);
		}
	}
	
	public void refreshcriterios(){
		if (this.actividades == null) {
			this.getactividades();
		}
		this.criteriosmap = db.getMap("select * from criterios");
		Map<String,Map<String,String>> critactividad = utils.listToMap(db.getMapList("select * from criterios_actividad"), "idcriterio");
		this.criteriosmap = utils.mergeMap(this.criteriosmap,critactividad, false);
		this.criterios = utils.mapToList(this.criteriosmap, "criterio");
	}
	
	
	
	public Map<String,Map<String,String>> getevaluacionesActividad(String idactividad) {
		if (this.actividades.size()<1) {
			this.getactividades();
		}
		this.refreshcriterios();
		ArrayList<Integer> notas = new ArrayList<Integer>();
		ArrayList<Map<String,String>>  sublista2 = db.getMapList("select * from criterios_actividad where idactividad = '"+idactividad+"'");
		Map<String,Map<String,String>> resp = utils.listToMap(sublista2,"idcriterio" );
        Map<String,Map<String,String>> criteriosactividad = utils.listToMap(sublista2,"idactividad");
		resp = utils.mergeMap(resp, this.criteriosmap, false);
		
		sublista2 = db.getMapList(String.format("select evaluacion.*,actividades.actividad, criterios.criterio from evaluacion join criterios on criterios._id = evaluacion.idcriterio join actividades on actividades._id = evaluacion.idactividad where idactividad = '%s' and idalumno = '%s'", idactividad,this.getId()));
		resp = utils.mergeMap(resp, utils.listToMap(sublista2,"idcriterio"), true);
		resp = utils.checkAll(resp);
		
		for (int i= 0; i<sublista2.size(); i++) {
			Map<String,String> crit = this.actividadesmap.get(idactividad);
			Map<String,String> item = sublista2.get(i);
            Map<String,String> critactividad = criteriosactividad.get(idactividad);
			Integer nota = 0;
			try {
			  nota = Integer.valueOf(item.get("evaluacion"));
			} catch (Exception e) {
				Log.v(TAG,"raro "+this.getnombre());
			}
            Integer toc = nota;
			try {
			toc = Integer.valueOf(critactividad.get("toc"));
			} catch (Exception e) {
				Log.v(TAG,"mmm, no hay toc ");
			}
			
			Integer pct = 0;
			if (toc > 0) {
				pct = nota*100/toc;
			} else {
				pct = 100;
			}
			item.put("pct",String.valueOf(pct));
			notas.add(pct);
		}
		this.evaluacionesActividad.put(idactividad, this.getNota(notas));
		return resp;
	}
	
	
	public Map<String,Map<String,String>> getevaluacionesCriterio(String idcriterio) {
//		if(this.criterios.size()<1) {
//			this.refreshcriterios();
//		}
		this.refreshcriterios();
		this.getactividades();
		ArrayList<Map<String,String>>  sublista2 = db.getMapList("select * from criterios_actividad where idcriterio = '"+idcriterio+"'");
		Map<String,Map<String,String>> resp = utils.listToMap(sublista2,"idactividad" );
		resp = utils.mergeMap(resp, this.actividadesmap, false);
		sublista2 = db.getMapList(String.format("select evaluacion.*,actividades.actividad, criterios.criterio from evaluacion join criterios on criterios._id = evaluacion.idcriterio join actividades on actividades._id = evaluacion.idactividad where idcriterio = '%s' and idalumno = '%s'", idcriterio,this.getId()));
		resp = utils.mergeMap(resp, utils.listToMap(sublista2,"idactividad"), true);
		resp = utils.checkAll(resp);

			ArrayList<Integer> notas = new ArrayList<Integer>();
			for (int i= 0; i<sublista2.size(); i++) {
				Map<String,String> item = sublista2.get(i);
				Integer nota = 0;
				try {
					nota = Integer.valueOf(item.get("evaluacion"));			
				} catch(Exception e) {
					Log.v(TAG,"nota no 0 criterioid = "+idcriterio+" : alumnoid ="+this.getId());
					nota = 0;
				}
				Map<String,String> criterio = this.criteriosmap.get(idcriterio);
				Integer toc = Integer.valueOf(this.criteriosmap.get(idcriterio).get("toc"));
				Integer pct = 0;
				if (toc > 0) {
					pct = nota*100/toc;
				} else { 
					pct = 100;
				}
				item.put("pct",String.valueOf(pct));
					notas.add(pct);
			}
//			Log.v(TAG,"evaluacion  "+this.getNota(notas));
			this.evaluacionesCriterio.put(idcriterio, this.getNota(notas));	
			return resp;
	}
	
	public String saveAlumno(Integer tipo) {
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
			sql = "insert into alumnos("+keys+") values ("+values+")"; 
		} else {
			sql = "update alumnos set ";
			for (Map.Entry<String, String> entry : data.entrySet())
			{
				sql = sql+entry.getKey()+"='"+entry.getValue()+"',";
			}
			sql = sql.substring(0, sql.length()-1);
			sql = sql + " where _id ='"+data.get("_id")+"'"; 
		}
		Log.v(TAG,sql); 
		db.insertSql(sql);
		this.id = db.getString("select MAX(_id) from alumnos");
		sql = String.format("delete from alumnos_grupos where idalumno = '%s'", id);
		db.insertSql(sql);
		for (int i = 0; i< grupos.size(); i++) {
			Map<String,String> grupo = grupos.get(i);
			if (grupo.get("metagrupo").equalsIgnoreCase("0")) {
				sql = String.format("insert into alumnos_grupos(idgrupo,idalumno,fecha_alta,fecha_baja) values('%s','%s','%s','%s')", grupo.get("_id"),this.getId(),grupo.get("fecha_alta"),grupo.get("fecha_baja"));
				db.insertSql(sql);
				Log.v(TAG,sql);	
			}
		}
		
		return this.id;
	}
	
	
	public void refreshalumno() {
		if (!id.equalsIgnoreCase("nuevo")) {
			data = db.getMapId(String.format("select * from alumnos where _id = '%s'", id) );
		}		
		grupos = db.getGrupAlumno(id);
	}

	public void delete() {
		String sql = String.format("delete from evaluacion where  idalumno ='%s'",id);
		db.insertSql(sql);
		sql = String.format("delete from eval_criterios_alumno where  idalumno ='%s'",id);
		db.insertSql(sql);
		sql = String.format("delete from alumnos_grupos where  idalumno ='%s'",id);
		db.insertSql(sql);
		sql = String.format("delete from asistencia where  idalumno ='%s'",id);
		db.insertSql(sql);
		sql = String.format("delete from alumnos where  _id ='%s'",id);
		db.insertSql(sql);
	}

	public ArrayList<Map<String, String>> getasignaturas() {
		if (this.asignaturas == null) {
			this.asignaturas = new ArrayList<Map<String,String>>();
		} else {
			this.asignaturas.clear();
		}
		ArrayList<Map<String,String>> temp = db.getMapList("select * from asignaturas");
		for (int i = 0; i<temp.size();i++ ) {
			Map<String,String > as = temp.get(i);
			as.put("checked", "1");
			as.put("show", "1");
			this.asignaturas.add(as);
		}
		return this.asignaturas;
	}
	
	public ArrayList<Map<String,String>> getactividades() {
		if (this.actividades == null) {
			if (this.grupos == null) {
				this.getgrupos();
			}
			Map<String,Map<String,String>> maptemp = utils.listToMap(db.getMapList("select * from actividades_grupos"),"idgrupo,idactividad");
			this.gruposmap = utils.listToMap(utils.removeunchecked(this.grupos), "_id");		
			this.actividadesmap = utils.conditionalMap(maptemp, this.gruposmap, "idgrupo", true);
			this.actividades = utils.mapToList(this.actividadesmap, "idactividad");
            Map<String,Map<String,String>> mtmp1 = db.getMap("select * from actividades where  strftime('%s',fecha_inicio) >= strftime('%s','"+fecha1+"') and strftime('%s',fecha_fin) < strftime('%s','"+fecha2+"') ");
			this.actividadesmap = utils.mergeMap(utils.listToMap(this.actividades, "idactividad"), mtmp1 , false);
			this.actividades = utils.mapToList(this.actividadesmap, "actividad");
		}
		return this.actividades;
	}

	
	public void check(String tipo, String campo) {
		ArrayList<Map<String,String>> temp = new ArrayList<Map<String,String>>();
		if (tipo.equalsIgnoreCase("asignatura")) {
			temp = this.asignaturas;
		}
		if (tipo.equalsIgnoreCase("actividad")) {
			temp = this.actividades;
		}
		if (tipo.equalsIgnoreCase("criterios")) {
			temp = this.criterios;
		}
	}

	public String getgruposstring() {
		String resp = "";
		for (int i = 0; i<this.getgrupos().size(); i++) {
			Map<String,String> grupo = this.grupos.get(i);
			resp = resp+","+grupo.get("grupo");
		}
		resp = resp.substring(1,resp.length());
		// TODO Auto-generated method stub
		return resp;
	}
	public String getNota(ArrayList<Integer> notas) {
		String resp = "Insuficiente";
		Integer x80 = 0;
		Integer x60 = 0;
		Integer x100 = 0;
		Integer x30 = 0;
		Integer x20 = 0;
		Integer x = 0;
		if (notas.size() == 0) {
			return "no notas";
		}
		
		for (int j = 0; j<notas.size(); j++) {
			Integer nota = notas.get(j);
			if (nota >= 80) {
				x80++;
			} else if(nota >=60) {
				x60++;
			} else if (nota >= 30) {
				x30++;
			} else if ( nota>=20 ) {
				x20++;
			} else {
				x++;
			}
		}
		
		if (x80/notas.size() ==1 ) {
			resp = "Sobresaliente";
		} else if (x80/notas.size() >= 0.8 && (x60+x30+x20)/notas.size()>=0.2) {
			resp = "Notable";
		} else if (x == 0) {
			resp = "Bien";
		} else if ( (x30+x60+x80)/notas.size() >= 0.6 && (x20+x)/notas.size()>0.4) {
			resp = "Suficiente";
		}
// 		Log.v(TAG,"nota del tio "+resp);
		return resp;
	}

	public String getevaluacionnota(Integer tipo, String string) {
		switch(tipo) {
		case CRITERIOS:
			return this.evaluacionesCriterio.get(string);
		case ACTIVIDADES:
			return this.evaluacionesActividad.get(string);
		
		}
		return "";
	}
	
	public ArrayList<ArrayList<String>> saveExcel(Integer tipo, Map<String,Boolean> ids, Integer tipo2, Boolean savefile) {
		String campo = "";
		String campo2 = "";
		String campoid = "_id";
		String fname = "";
		String fecha = utils.getstrfecha();
		String resp = String.valueOf(db.gettimestamp());
		ArrayList<ArrayList<String>> reporte = new ArrayList<ArrayList<String>>();
		ArrayList<String> linea = new ArrayList<String>();
		ArrayList<Map<String, String>> listado = new ArrayList<Map<String, String>>();
		if (tipo == 2) {
			listado = this.getactividades();
			campo = "criterio";
			campo2 = "actividad";
			fname = "_actividades.xls";
			campoid = "idactividad";

		}
		if (tipo == 3) {
			listado = this.getcriterios();
			campo = "actividad";
			campo2 = "criterio";
			fname = "_criterios.xls";
			campoid = "idcriterio";
		}
		
		String reportefile = utils.getdir("reportes")+getnombre().replaceAll(" ", "_")
				+ fecha + fname;
		linea.add(this.getnombre());
		reporte.add(linea);
		
		ArrayList<Map<String,String>> sublist = new ArrayList<Map<String,String>>();
		
		for (int i = 0; i < listado.size(); i++) {
			Map<String, String> elemento = listado.get(i);
			String id = elemento.get(campoid);
            Boolean valido = ids.get(id);
            if (valido == null) {
                valido = false;
            }

			if (valido || tipo2 == 0) {
				String nota = "";
				if (tipo == 2) {
					sublist = utils.mapToList(this.getevaluacionesActividad(id),campo);
					nota = this.evaluacionesActividad.get(id);
				}
				if (tipo == 3) {
					sublist = utils.mapToList(this.getevaluacionesCriterio(id),campo);
					nota = this.evaluacionesCriterio.get(id);
				}
				linea = new ArrayList<String>();
				linea.add("");
				linea.add(elemento.get(campo2));
				linea.add(nota);
				if (elemento.get("show").equalsIgnoreCase("1") || savefile == false) {
					reporte.add(linea);
					if (valido || tipo2 == 0) {
						for (int j = 0; j < sublist.size(); j++) {
							linea = new ArrayList<String>();
							linea.add("");
							linea.add("");
							linea.add(sublist.get(j).get(campo));
							String pct =  this.getvalor(sublist.get(j).get("pct"));
							String eval = this.getvalor(sublist.get(j).get("evaluacion"));
							String toc =  this.getvalor(sublist.get(j).get("toc"));						
							linea.add(eval);
							linea.add(toc);
							reporte.add(linea);
						}
					}
				}
			}		
		}
		if (savefile == true) {
			WriteExcel guardarExcel = new WriteExcel(reporte, reportefile);
			try {
				guardarExcel.write();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}		
		return reporte;
	}

	private String getvalor(String val) {
		if (val == null) {
			return "'00";
		}
		else {
			return val;
		}
	}
	
}
