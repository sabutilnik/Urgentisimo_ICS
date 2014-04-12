package uno.Urgentisimo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import uno.Urgentisimo.UtilsLib.evalCriterioAlumno;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class UtilsLib {

	private  final String TAG = "URGENTISIMO : UTILS : ";

	
	public static HashMap<String, String> tablasId;
	public constants constants;
//	public class evalCriterioAlumno;
	private ArrayList<String> actividadesCampos; 
	private Context context;
	public UtilsLib(Context c) {
		actividadesCampos = setActividadesCampos();
		tablasId = setTablasId();
		constants = new constants() ;
        this.context = c;
	}
	
	public ArrayList<String> setActividadesCampos() {
		actividadesCampos = new ArrayList<String>();
		actividadesCampos.add("descripcion");
		actividadesCampos.add("idactividad_general");
		actividadesCampos.add("idtipo_actividad");
		actividadesCampos.add("idagrupamiento");
		actividadesCampos.add("duracion");
		actividadesCampos.add("fecha_inicio");
		actividadesCampos.add("fecha_fin");
		return actividadesCampos;
	}
	public HashMap<String, String> setTablasId() {
		tablasId = new HashMap<String, String>();
		tablasId.put("idactividad", "actividades");
		tablasId.put("idactividad_general","actividades_generales");
		tablasId.put("idtipo_actividad","tipos_actividades");
		tablasId.put("idagrupamiento","agrupamientos");
		return tablasId;
	}
	
	public String getTablaId(String key) {
		if (tablasId.containsKey(key)== false ) {
			return "";
		}
		return tablasId.get(key);
	}
	public ArrayList<String> getActividadesCampos() {
		return actividadesCampos;
	}
	public int getActividadesCamposSize() {
		return actividadesCampos.size();
	}
	public String getActividadesCamposText(int i) {
		return actividadesCampos.get(i);
	}
	
	public int toggleVisivility(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			return View.GONE;
		} else {
			return View.VISIBLE;
		}
	}
	
	public Boolean matches(String texto, EditText et, Boolean filter) {
		String t1 = texto.toLowerCase();
		String t2 = et.getText().toString().toLowerCase();
		if (filter == false) {
			return true;
		}
		if (t2.equals("")) {
			return true;
		}
//		Log.v(TAG,"search "+texto+" contains :"+et.getText().toString()+":");
		if (t1.contains (t2) == true) {
//			Log.v(TAG,"true");
			return true;
		} else {
			return false;
		}	
	}
	public String toggleText(int visibility) {
		if (visibility == View.VISIBLE ) {
			return " - ";
		} else {
			return " + ";
		}
	}
	public String capitalize(String s) {
		return s.substring(0,1).toUpperCase() + s.substring(1);
	}
	
	public class constants  {
		public final int FONT_MEDIUM = 0;
		public final int FONT_SMALL = 1;
		public  final int ONE_FIELD_DIALOG = 1;
		public  final int DATE_DIALOG = 2;
		public  final int INFO_DIALOG = 3;
		public final int YES_NO_DIALOG = 4;
		public final int SPINNER_DIALOG = 5;
		private Context c;
		public void constants( Context c) {
			this.c = c;
		}
		
	}

	public int toggleVisibility(int visibility) {
		if (visibility == View.VISIBLE) {
			return View.GONE;
		} else {
			return View.VISIBLE;
		}
	}
	
	public class evalCriterioAlumno {
		public int idalumno;
		public ArrayList<evalCriterio> evalCriterios;
	    public HashMap<Integer,String> evalCheck;
		
		public evalCriterioAlumno(Integer idalumno) {
			this.idalumno = idalumno;
			this.evalCriterios = new ArrayList<evalCriterio>();
		}
		
		public void addEvaluacion(evalCriterio ev) {
			Integer elid = ev.idcriterio;
			for (int i = 0; i<evalCriterios.size(); i++) {
				if (evalCriterios.get(i).idcriterio == elid) {
					evalCriterios.add(i, ev);
					return;
				}
				
			}
			evalCriterios.add(ev);
		}
		
		public long getEvaluacion(Integer idcriterio) {
			int resp = 0;
			int divisor = 0;
			for (int i = 0; i < evalCriterios.size(); i++) {
				evalCriterio dato = evalCriterios.get(i);
				if (dato.idcriterio == idcriterio) {
					divisor++;
					if (dato.toc > 0) {
						resp = resp+(dato.evaluacion*100/dato.toc);
					} else {
						resp = resp+(100);
					}
				}		
			}
			if (divisor == 0) {
				return 9999;
			}
			return resp/divisor;
		}

		public ArrayList<Integer> getcriterios() {
			ArrayList<Integer> resp = new ArrayList<Integer>() ;
			for  (int i = 0; i<evalCriterios.size(); i++) {
				if (!resp.contains(evalCriterios.get(i).idcriterio)) {
					Log.v(TAG,"meto criterio "+evalCriterios.get(i));
					resp.add(evalCriterios.get(i).idcriterio);
				}
			}
			return resp;
		}

		public String getCriterio(Integer idcriterio) {
			String resp = new String();
			for (int i = 0; i < evalCriterios.size(); i++) {
				evalCriterio dato = evalCriterios.get(i);
				if (dato.idcriterio == idcriterio) {
					resp = dato.criterio;
				}
			}
			return resp;
		}

		public ArrayList<evalCriterio> getEvaluaciones(Integer idcriterio) {
			ArrayList<evalCriterio> resp = new ArrayList<evalCriterio>();
			for (int i = 0; i<evalCriterios.size(); i++) {
				evalCriterio dato = evalCriterios.get(i);
				if (dato.idcriterio == idcriterio) {
					resp.add(dato);
				}
			}
			return resp;
		}

		public String getEvaluacionAlumno(Integer idcriterio, int eval) {
			String resp = "0";
			for (int i = 0; i< evalCriterios.size(); i++) {
				evalCriterio dato = evalCriterios.get(i);
				if (dato.idcriterio == idcriterio) {
					switch(eval) {
						case 1: return dato.eval1;
						case 2: return dato.eval2;
						case 3: return dato.eval3;
						case 4: return dato.eval4;
					}
				}
			}		
			return resp;
			
		}

		public void updateEvalCriterioAlumno(int idcriterio, String eval1,String eval2, String eval3, String eval4) {
			for (int i = 0; i< evalCriterios.size(); i++) {
				evalCriterio dato = evalCriterios.get(i);
				if (dato.idcriterio == idcriterio) {
					dato.eval1 = eval1;
					dato.eval2 = eval2;
					dato.eval3 = eval3;
					dato.eval4 = eval4;
				}
			}
		}

		public String getMediaPonderada() {
			// TODO Auto-generated method stub
			long evf = 0;
			long pp  = 0;
			for (int i = 0; i< evalCriterios.size(); i++) {
				String elpeso = evalCriterios.get(i).peso;
				Log.v(TAG,"e pe so "+elpeso);
				if (evalCriterios.get(i).peso != null) {
					pp = pp+Integer.valueOf(evalCriterios.get(i).peso);
				} else {
					pp = pp;
				}
				Log.v(TAG," evalcriterios get i "+evalCriterios.get(i).idcriterio);
				long eval1 = Integer.valueOf(evalCriterios.get(i).eval1);
//				long eval2 = Integer.valueOf(evalCriterios.get(i).eval2);
//				long eval3 = Integer.valueOf(evalCriterios.get(i).eval3);
//				long eval4 = Integer.valueOf(evalCriterios.get(i).eval4);
				int criterioid = evalCriterios.get(i).idcriterio;
				if (eval1 < 1) { eval1 = this.getEvaluacion(criterioid);}
				evf = evf+eval1;
			}
			if (pp<1) {pp = evalCriterios.size();}
			
			long res = evf/pp;
			
			return String.valueOf(res);
		}	
	}
	
	public class evalCriterio {
		public int idactividad;
		public String actividad;
		public int evaluacion;
		public int toc;
		public int idcriterio;
		public String criterio;
		public String eval1;
		public String eval2;
		public String eval3;
		public String eval4;
		public String peso;
		
		public evalCriterio() {
			this.actividad="";
			this.evaluacion=0;
			this.idactividad=0;
			this.toc = 1;
			this.idcriterio = 0;
			this.criterio = "";
			this.eval1 = "0";
			this.eval2 = "0";
			this.eval3 = "0";
			this.eval4 = "0";
			this.peso = "";
			
		}
		public evalCriterio(String actividad, int evaluacion, int toc, int idactividad, int idcriterio, String criterio, String eval1, String eval2, String eval3, String eval4, String peso) {
			this.evaluacion = evaluacion;
			this.actividad = actividad;
			this.toc = toc;
			this.idactividad = idactividad;
			this.idcriterio = idcriterio;
			this.criterio = criterio;
			this.eval1 = eval1;
			this.eval2 = eval2;
			this.eval3 = eval3;
			this.eval4 = eval4;
			this.peso = peso;
		}
	}
	
	public boolean member(ArrayList<nombreId> lista, String id) {
		boolean resp = false;
		for (int i = 0; i< lista.size() ; i++) {
			if (lista.get(i).getId().equals(id)) {
				return true;
			}
		}
		return resp;
		
	}
	
		public ArrayList<Map<String, String>> mergelists(ArrayList<Map<String, String>> list1,ArrayList<Map<String, String>> list2, String campo1, String campo2) {
		ArrayList<Map<String, String>> resp = new ArrayList<Map<String, String>>();
		Boolean checked = false;
		String key = "";
		String val = "";

		Boolean miembro = false;
		for (int i = 0; i < list1.size(); i++) {
			Map<String,String> item1 = new HashMap<String,String>();
			Map<String,String> item2 = new HashMap<String,String>();
			item1 = list1.get(i);
			Map<String,String> item3 = new HashMap<String,String>(item1);
			for (int j = 0; j < list2.size(); j++) {
				checked = false;
				item2 = list2.get(j);
				String[] f1 = campo1.split(":");
				String[] f2 = campo2.split(":");
				Boolean iguales = true;
				for (int k = 0; k < f1.length ; k++) {
					String val1 = item1.get(f1[k]);
					String val2 = item2.get(f2[k]);
					if (val1.equalsIgnoreCase(val2)) {
						iguales = true;
					} else {
						iguales = false;
						break;
					}
				}
				
				if (iguales) {
					Iterator it = item2.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry e = (Map.Entry) it.next();
						if (!e.getKey().toString().equals("_id")) {
							val = (String) e.getValue();
							if (val != null) {
								item3.put((String) e.getKey(), (String) e.getValue());
							} else {
								item3.put((String) e.getKey(), "");
							}
						}
					}
					miembro = true;
					break;
				} 
//				else {
//					Iterator it = item2.entrySet().iterator();
//					while (it.hasNext()) {
//						Map.Entry e = (Map.Entry) it.next();
//						if (!e.getKey().toString().equals("_id")) {
//							val = (String) e.getValue();
//								item1.put((String) e.getKey(), "");
//						}
//					}
//				}
			}
			if (miembro) {
				item3.put("checked", "1");
				item3.put("show", "1");
				resp.add(item3);
			} else {
				item3.put("checked", "0");
				item3.put("show", "1");
				resp.add(item3);
			}
		}
		return resp;
	
	}
	
	
	public ArrayList<Map<String,String>> checklist(ArrayList<Map<String,String>> list1, ArrayList<Map<String,String>> list2, String campo1, String campo2, Integer logica) {
		Map<String,String > item1;
		Map<String,String > item2;	
		ArrayList <Map<String,String>> resp = new ArrayList<Map<String,String>>();
		for (int i = 0; i<list1.size(); i++) {
			item1 = list1.get(i);
			for (int j=0; j<list2.size(); j++) {
				item2 = list2.get(j);
//				Log.v(TAG," COMPARANDO :"+campo1+":"+campo2+":"+item1.get(campo1)+":"+item2.get(campo2));
				String c1 = item1.get(campo1);
				String c2 = item2.get(campo2);
				
				if (item1.get(campo1).equalsIgnoreCase(item2.get(campo2))) {
//					Log.v(TAG,"SON IGUALES "+campo1+" y "+campo2);
					if (logica == 1) {
						if (item2.get("checked").equalsIgnoreCase("1")) {
							item1.put("checked", "1");
							item1.put("show", "1");
							resp.add(item1);
//							if (campo1.equals("idgrupo")) {
//								Log.v(TAG,"check en actividad "+item1.get("idactividad")+" y grupo "+item2.get("grupo"));
//							}
							break;
						} 
					} 
						else {
						item1.put("checked", "0");
						item1.put("show", "1");
						resp.add(item1);
						break;
					}
				}
			}
		}
		return resp;
	}
	
	public ArrayList<Map<String,String>> andlist(ArrayList<Map<String,String>> list1, ArrayList<Map<String,String>> list2, String campo1, String campo2, Integer logica) {
		Map<String,String > item1;
		Map<String,String > item2;	
		ArrayList <Map<String,String>> resp = new ArrayList<Map<String,String>>();
		for (int i = 0; i<list1.size(); i++) {
			item1 = list1.get(i);
			for (int j=0; j<list2.size(); j++) {
				item2 = list2.get(j);
//				Log.v(TAG," COMPARANDO :"+campo1+":"+campo2+":"+item1.get(campo1)+":"+item2.get(campo2));
				String c1 = item1.get(campo1);
				String c2 = item2.get(campo2);
				if (c1 == null) {

					Log.v(TAG,"campo 1 "+campo1+" is null");
				}
				if (c2 == null) {
					Log.v(TAG,"campo 2 "+campo2+" is null");
				}
				if (c1.equalsIgnoreCase(c2)) {
//					Log.v(TAG,"SON IGUALES "+campo1+" y "+campo2);
					if (logica == 1) {
						if (item2.get("checked").equalsIgnoreCase("1")) {
							item1.put("checked", "1");
							item1.put("show", "1");
							resp.add(item1);
							break;
						} 
					} 
				}
			}
		}
		return resp;
	}
	
	public ArrayList<Map<String,String>> removeunchecked(ArrayList<Map<String,String>> listado) {
		ArrayList<Map<String,String>> resp = new ArrayList<Map<String,String>>();
		for (int i = 0; i<listado.size(); i++) {
			Map<String,String>item = listado.get(i);
			if (item.get("checked").equalsIgnoreCase("0")) {
				continue;
			}
			resp.add(item);
		}
		
		return resp;
		
	}
	
	public Boolean esMiembro(ArrayList<String> listado, String elemento) {
		Boolean resp = false;
		for (int i = 0; i<listado.size(); i++) {
			if (listado.get(i).equalsIgnoreCase(elemento)) {
				return true;
			}
		}		
		return resp;
	}
	
	public Map<String, Map<String,String>> listToMap (ArrayList<Map<String,String>> listado, String campos) {
		Map<String,Map<String,String>> resp = new HashMap<String,Map<String,String>>();
		for (int i = 0; i<listado.size(); i++) {
			Map<String,String> elemento = listado.get(i);
			String key = "";
			String[] keytemp = campos.split(",");
			for (String keyn : keytemp ) {
				String k = elemento.get(keyn);
				if (key.length() == 0) {
					key = k;
				} else {
					key = key+","+k;
				}
			}
			if (key.length() == 0) {
				key = "null";
			}
			resp.put(key, elemento);
		}
		return resp;
	}
	
	public ArrayList<Map<String,String>> mapToList(Map<String,Map<String,String>> listado, String campo) {
		ArrayList<Map<String,String>> resp = new ArrayList<Map<String,String>>();
		ArrayList<String> tempkeys = new ArrayList<String>();
		Map<String,Map<String,String>> temp = new HashMap<String,Map<String,String>>();
		for (Map.Entry<String, Map<String,String>> entry : listado.entrySet())
		{
			String ktemp = entry.getValue().get(campo);
			temp.put(ktemp,entry.getValue());
			tempkeys.add(ktemp);
		}
		try {
		Collections.sort(tempkeys);
		} catch(Exception e) {
			Log.v(TAG,"me muero");
		}
		for (int i = 0; i<tempkeys.size(); i++) {
			resp.add(temp.get(tempkeys.get(i)));
		}
		return resp;
	}

	
//	if empty == true, return elements that are not in listado2 
	public Map<String,Map<String,String>> mergeMap(Map<String,Map<String,String>> listado1, Map<String,Map<String,String>> listado2, Boolean empty) {
		Map<String,Map<String,String>> resp = new HashMap<String,Map<String,String>>();
		for (Map.Entry<String, Map<String,String>> entry : listado1.entrySet()) {
			String k = entry.getKey();
			Map<String,String> v = entry.getValue();
			Map<String,String> v2 = new HashMap<String,String>();
			try {
			v2 = listado2.get(k);
			} catch (Exception e) {
				Log.v(TAG,"mierdaaaa");
			}
			if (v2 != null ) {
				v2.remove("_id");
				v.putAll(v2);
				v.put("checked", "1");
				if (v.get("show") == null) {
					v.put("show", "0");	
				}
				resp.put(k, v);
			} else {
				if (empty) {
					v.put("checked", "0");
					v.put("show","0");
					resp.put(k, v);
				}
			}	
		}	
		return resp;
	}
	
	//ligica = true, devuelve el valor de listado1 solamente si el valor de campo en listado1 == key de listado2 y logica = false lo contrario
	public Map<String,Map<String,String>> conditionalMap(Map<String,Map<String,String>> listado1, Map<String,Map<String,String>> listado2, String campo, Boolean logica) {
		Map<String,Map<String,String>> resp = new HashMap<String,Map<String,String>>();
		for (Map.Entry<String, Map<String,String>> entry : listado1.entrySet()) {
			String k = entry.getKey();
			Map<String,String> v = entry.getValue();
			String k2 = v.get(campo);
			if (listado2.get(k2) != null ) {
				if (logica) {
					resp.put(k, v);
				}
			} else {
				if (!logica) {
					resp.put(k,v);
				}	
			}
		}	
		return resp;
	}
	
	public Map<String,Map<String,String>> filterMap (Map<String,Map<String,String>> listado, String campo, String valor) {
		Map<String,Map<String,String>> resp = new HashMap<String,Map<String,String>>();
		for (Map.Entry<String, Map<String,String>> entry : listado.entrySet()) {
			String k = entry.getKey();
			Map<String,String> v = entry.getValue();
			if (v.get(campo).equals(valor)) {
				resp.put(k, v);
			}
		}
			return resp;
	}
	
	public Map<String,Map<String,String>> checkAll(Map<String,Map<String,String>> listado) {
		Map<String,Map<String,String>> resp = new HashMap<String,Map<String,String>>();
		for (Map.Entry<String, Map<String,String>> entry : listado.entrySet()) {
			String k = entry.getKey();
			Map<String,String> v = entry.getValue();
			v.put("checked","1");
			v.put("show", "1");
			resp.put(k, v);
		}
			return resp;
	}
	
	public String getstrfecha() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(c.getTime());
	}
	public String getdir(String dir) {
		String  resp = Environment.getExternalStorageDirectory()+"/Urgentisimo/";
		return resp;
	}
	
	public ArrayList<String> ordenarmap(Map<String,Map<String,String>> listado, String campo) {
		ArrayList<String> resp = new ArrayList<String>();
		ArrayList<String> tempkeys = new ArrayList<String>();
		Map<String,String> temp = new HashMap<String,String>();
		for (Map.Entry<String, Map<String,String>> entry : listado.entrySet())
		{
			String ktemp = entry.getValue().get(campo);
			String vtemp = entry.getKey().toString();
			temp.put(ktemp,vtemp);
			tempkeys.add(entry.getValue().get(campo));
		}
		Collections.sort(tempkeys);
		for (int i = 0; i<tempkeys.size(); i++) {
			resp.add(temp.get(tempkeys.get(i)));
		}
		return resp;
	}
	
	public ArrayList<Map<String,String>> ordenarArrayList(ArrayList<Map<String,String>> listado1, String campo) {
		ArrayList<Map<String,String>> resp = new ArrayList<Map<String,String>>();
		ArrayList<String> tempkeys = new ArrayList<String>();
		Map<String,Map<String,String>> listado = new HashMap<String,Map<String,String>>();
			
		Map<String,String> temp = new HashMap<String,String>();
		
		for (int j = 0; j<listado1.size(); j++) {
			tempkeys.add(listado1.get(j).get(campo));
			listado.put(listado1.get(j).get(campo),listado1.get(j));
		}	
		for (Map.Entry<String, Map<String,String>> entry : listado.entrySet())
		{
			String ktemp = entry.getValue().get(campo);
			String vtemp = entry.getKey().toString();
			temp.put(ktemp,vtemp);
			tempkeys.add(entry.getValue().get(campo));
		}
		Collections.sort(tempkeys);
		for (int i = 0; i<tempkeys.size(); i++) {
			resp.add(listado.get(tempkeys.get(i)));
		}
		return resp;
	}

    public void insertActividad(int year, int month, int dayOfMonth) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, "Urgentisimo Activity");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Alberto Alcocer 11, 5b");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Test de actividad, puro pelex realmente");

// Setting dates
        GregorianCalendar calDate = new GregorianCalendar(year, month, dayOfMonth);

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis());

// make it a full day event
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

// make it a recurring Event
//        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

// Making it private and shown as busy
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        this.context.startActivity(intent);
    }
}