/*
 * Class for actividades information.  It will handle all activity information, such as id, description, dates
 * etc.
 */

package uno.Urgentisimo;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uno.Urgentisimo.AddActividadActivity.OnReadyListener;

import uno.Urgentisimo.WidgetsUtils.TextViewSubList;

import android.app.AlertDialog;
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

public class actividadInfo {
	private static final String TAG = "URGENTISIMO : ACTIVIDAD INFO : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private String idactividad;
	private Map<String,String > data;
	private Map<String,String> dataText = new HashMap();
	private Map<String,String> text;
	public ArrayList<nombreId> grupos;
	private DataBaseHelper db;
	private UtilsLib utils;
	private WidgetsUtils customWidget;
	public ArrayList<nombreId> criterios;
	public ArrayList<nombreId> tocs;
	
	public ArrayList<nombreId> materiales;
	private ArrayList<nombreId> tmp;
	private Boolean edicion;
	private WidgetsUtils.TextViewSubList customView;
	private Context c;
	private TableLayout tldata;
	private LinearLayout lldata;
	private ArrayList<WidgetsUtils.EditableRow> basic;
	private ArrayList<WidgetsUtils.TextViewSubList> grouped;
	private ArrayList<nombreId> actividades_generales;
	private ArrayList<nombreId> tipos_actividades;
	private ArrayList<nombreId> agrupamientos;
	private Boolean hidden;

	public actividadInfo(Context c) {
		idactividad = "";
		data = new HashMap();
		grupos = new ArrayList<nombreId>();
		criterios = new ArrayList<nombreId>();
		materiales = new ArrayList<nombreId>();
		tocs = new ArrayList<nombreId>();
		text = new HashMap();
		db = new DataBaseHelper(c.getApplicationContext());

	}

	/*
	 * Initializes actividadInfo object with idactividad = id, gets data and groups info.
	 */

	public actividadInfo(Context c, String id) {
		this.c = c;
		db = new DataBaseHelper(c.getApplicationContext());
		actividades_generales = db.getNombreId("actividades_generales");
		tipos_actividades = db.getNombreId("tipos_actividades");
		agrupamientos = db.getNombreId("agrupamientos");
		
		this.hidden = true;
		utils = new UtilsLib(c);
		Log.v(TAG,"inicializnado widgetutils");
		customWidget = new WidgetsUtils(c);
		idactividad = id;
		data = db.getActividadData(idactividad);
		data.remove("_id");
		customView = customWidget.new TextViewSubList(c);
		customView.hidden=false;
//		customView.
		String stmp = "select * from grupos where _id in (select idgrupo from actividades_grupos where idactividad = '"+idactividad+"')";
		grupos = db.getNombreIdSql(stmp, "grupo");
//		stmp = "select * from criterios where _id in ( select idcriterio from criterios_actividad where idactividad = '"+idactividad+"')";
		stmp = "select criterios.criterio,criterios._id,criterios_actividad.toc from criterios_actividad join criterios on criterios_actividad.idcriterio = criterios._id where idactividad = '"+idactividad+"'";
		criterios = db.getNombreIdSql(stmp,"criterio");
		stmp = "select * from materiales where _id in ( select idmaterial from materiales_actividad where idactividad = '"+idactividad+"')";
		materiales = db.getNombreIdSql(stmp, "material");	
		stmp = "select criterios._id,criterios_actividad.toc from criterios_actividad join criterios on criterios_actividad.idcriterio = criterios._id where idactividad = '"+idactividad+"'";
		tocs = db.getNombreIdSql(stmp,"toc");
		this.edicion = false;
		basic = new ArrayList<WidgetsUtils.EditableRow>();
		grouped = new ArrayList<WidgetsUtils.TextViewSubList>();
	}

	public void setIdactividad(String s) {
		idactividad = s;
	}
	public String getIdactividad() {
		return idactividad;
	}
	/* 
	 * set empty string as value when key is not found (easier to initialize values )
	 */
	public String getData(String d) {
		if (data.containsKey(d) == false) {
			Log.v(TAG,"vacio "+d);
			return "";
		}
		return data.get(d);
	}
	
	/*******************************************************************************************
	 * 
	 * getView : returns Actividad view ( name + field-value + grupos/materiales/criterios
	 * 
	 */
	public void initView() {	
		customView = customWidget.new TextViewSubList(c, data.get("actividad"), 0, new OnDelClickListener(c, data.get("actividad")),this.edicion, false);
		tldata = (TableLayout)customView.findViewById(R.id.sublista);
		lldata = (LinearLayout)customView.findViewById(R.id.sublista1);
		getExtras();
		tldata.setShrinkAllColumns(true);
		tldata.removeAllViews();
		for (int i = 0; i<basic.size(); i++) {
			tldata.addView(basic.get(i));
		}
	}
	public void initViewHidden() {
		LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cv = inflater.inflate(R.layout.actividad_main_item, null);
		TextView tv = (TextView) cv.findViewById(R.id.texto);
		tv.setText(" + "+data.get("actividad"));
		cv.findViewById(R.id.edit).setVisibility(View.GONE);
		cv.findViewById(R.id.etTexto).setVisibility(View.GONE);
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hidden = false;
			}
		});
	}
	public View getView() {
		
		if (customView.getChildCount() < 1) {
//			if (this.hidden == true) {
//				initViewHidden();
//			} else {
				initView();
//			}
		}
		return customView;
	}

	public void getExtras() {
		basic.add(customWidget.new EditableRow(c,"Descripción", data.get("descripcion"),"descripcion",this.edicion,utils.constants.ONE_FIELD_DIALOG));
		basic.add(customWidget.new EditableRow(c,"Actividad General", data.get("idactividad_general"),"idactividad_general",this.edicion,utils.constants.SPINNER_DIALOG,this.actividades_generales));
		basic.add(customWidget.new EditableRow(c,"Tipo de Actividad", data.get("idtipo_actividad"),"idtipo_actividad",this.edicion,utils.constants.SPINNER_DIALOG,this.tipos_actividades));
		basic.add(customWidget.new EditableRow(c,"Agrupamiento", data.get("idagrupamiento"),"idagrupamiento",this.edicion,utils.constants.SPINNER_DIALOG,this.agrupamientos));
		basic.add(customWidget.new EditableRow(c,"Duración", data.get("duracion"),"duracion",this.edicion,utils.constants.ONE_FIELD_DIALOG));
		basic.add(customWidget.new EditableRow(c,"Fecha de Inicio", data.get("fecha_inicio"),"fecha_inicio",this.edicion,utils.constants.DATE_DIALOG));
		basic.add(customWidget.new EditableRow(c,"Fecha de Fin", data.get("fecha_fin"),"fecha_fin",this.edicion,utils.constants.DATE_DIALOG));
	}
	
	public String getDataText(String d) {
		if (dataText.containsKey(d)  == false )  {
			return "";
		}
		return dataText.get(d);
	}
	public String getText(String d) {
		if (text.containsKey(d) == false) {
			return "";
		}
		return text.get(d);
	}
	public void setData(String k, String v) {
		if (data.containsKey(k) == true) {
			data.remove(k);
		}
		data.put(k,v);
	}

	public void updateValues() {
		for (int i = 0;  i< basic.size()  ; i++) {
			Log.v(TAG,"basic   "+basic.get(i).text);
			
		}
	}
	
	public String saveActividad(int tipo) {
		String values = "";
		String sentence = "";
		String stemp = "";
		String resultado = data.get("actividad");
		Iterator it = data.entrySet().iterator();
		Log.v(TAG,"save : tipo= "+tipo);
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			if (pairs.getKey().toString().equals("idgrupo") == false ) {
				switch (tipo) {
				case INSERT:
					sentence = sentence+pairs.getKey()+",";
					values = values+pairs.getValue()+"','";
					Log.v(TAG," insert : "+sentence+" : "+values);
					break;
				case UPDATE:
					sentence = sentence+pairs.getKey()+"='"+pairs.getValue()+"',";
					values = ",'";
					break;
				}
			}
		}
		sentence = sentence.substring(0, sentence.length()-1);
		values = values.substring(0,values.length()-2);
		switch (tipo) {
		case INSERT:
			stemp = "insert into actividades("+sentence+") values ('"+values+")";
			break;
		case UPDATE:
			stemp = "update actividades set "+sentence+" where _id = '"+idactividad+"'";
			break;
		}
		Log.v(TAG,"INSERT/UPDATE : "+stemp);
		db.insertSql(stemp);
		resultado = db.getId("actividades", data.get("actividad"));
		Log.v(TAG," nueva actividad  :"+resultado);
		idactividad = resultado;
		db.delete("delete from actividades_grupos where idactividad = '"+idactividad+"'");
		for (int i = 0 ; i < grupos.size(); i++) {
			db.insertSql("insert into actividades_grupos (idactividad, idgrupo) values('"+idactividad+"','"+grupos.get(i).getId()+"')");
		}
		db.delete("delete from criterios_actividad where idactividad = '"+idactividad+"'");
		for (int i = 0 ; i < criterios.size(); i++) {
			db.insertSql("insert into criterios_actividad (idactividad, idcriterio, toc) values('"+idactividad+"','"+criterios.get(i).getId()+"','"+tocs.get(i).getNombre()+"')");
		}
		return resultado;

	}

	public void addGrupoItem(nombreId item) {
		if (grupos.contains(item) == false ) {
			grupos.add(item);
		}
	}

	public void delGrupoItemAtPosition(int position) {
		grupos.remove(position);
	}

	public void delCriterioAtPosition(int j) {
		criterios.remove(j);

	}
	
	public void setCriterioToc(int j,String newtoc) {
		String idcrit = criterios.get(j).getId();
		String stmp = String.format("update criterios_actividad set toc = '%s' where idactividad = '%s' and idcriterio = '%s'",newtoc, idactividad,idcrit);
		Log.v(TAG,stmp);
		db.updateTableValue(stmp);	
	}

	public String getMiembros(ArrayList<nombreId> datos) {
		String resultado = "";
		if (datos.size() == 0) {
			return "";
		}
		for (int i = 0 ; i < datos.size() ; i++) {
			resultado = resultado + ", "+ datos.get(i).getNombre();
		}
		return resultado.substring(1);
	}

	public void addCriterio(nombreId item) {
		criterios.add(item);
//		item.setNombre(item.getNombre());
		tocs.add(new nombreId());
	}
	public void setEdicion(Boolean edicion ) {
		this.edicion = edicion;
		customView.edicion = this.edicion;
		customView.setEdicion();
		for (int i = 0; i< basic.size(); i++) {
			basic.get(i).edicion = this.edicion;
			basic.get(i).setEdicion();
		}
	}
	public void update() {
		for (int i = 0; i< basic.size(); i++) {
			basic.get(i).update();
			data.put(basic.get(i).elemento, basic.get(i).value);
		}
		customView.text = customView.et.getText().toString();
		customView.tv.setText(customView.text);
	}
	
	
	
	/*
	 * Imnplements listener for customDialog
	 * this.elemento is the current edited field
	 * salida is the string returned by customDialog
	 */

	public class OnSpinnerListener implements customDialog.ReadyListener {
		private static final String TAG = "URGENTISIMO : OnReadyListener ";
		private String elemento;
		private TextView valor;
		private WidgetsUtils.TextViewSubList tvl;

		public OnSpinnerListener (String elemento, TextView valor) {
			this.elemento = elemento;
			this.valor = valor;
		}

		public OnSpinnerListener (String elemento, WidgetsUtils.TextViewSubList cv ) {
			this.elemento = elemento;
			this.tvl = cv;
		}

		public void ready(nombreId item) {
			if (elemento.equalsIgnoreCase("grupos") == true ) {
				grupos.add(item);
			}
			else {
				if (elemento.equalsIgnoreCase("criterios") == true ) {

					criterios.add(item);
					item.setNombre("");
					tocs.add(item);
				} else {
					if (elemento.equalsIgnoreCase("materiales") == true ) {
						materiales.add(item);
					}else {
										data.put(elemento, item.getId());
										this.valor.setText(item.getNombre());
										saveActividad(UPDATE);
					}
				}
			}
			tvl.display();
			saveActividad(UPDATE);
		}

		public void ready(String salida) {
			data.put(elemento, salida);
			this.valor.setText(salida);
			saveActividad(UPDATE);
		}
	}
	public class OnDelClickListener implements WidgetsUtils.MyHandlerInterface {
		private static final String TAG = "URGENTISIMO : OnReadyListener ";
		private nombreId item;
		private String elemento;
		private ArrayList<nombreId> miembros;
		private Context c;
		private WidgetsUtils.TextViewSubList tvl;

		public OnDelClickListener (nombreId item, String elemento, WidgetsUtils.TextViewSubList tvl) {
			this.item = item;
			this.elemento = elemento;
			this.tvl = tvl;
			if (elemento.equalsIgnoreCase("grupos") == true ) {
				this.miembros = grupos;
			}
		}

		public OnDelClickListener (Context context, String elemento) {
			this.c = context;
			this.elemento = elemento;
			this.tvl = tvl;
		}
		public void delete(String salida) {
			Log.v(TAG,"delete from "+elemento+" :"+item.getNombre()+" elid "+item.getId());
			if (elemento.equalsIgnoreCase("grupos")==true) {
				grupos.remove(item);
				saveActividad(UPDATE);
			}
			
		}
		public void add(String tabla) {
			if (elemento.equalsIgnoreCase("grupos") == true) {
				Log.v(TAG,"add new grupo "+tabla);
			}
			customDialog myDialog = new customDialog(c,
					"bla bla:", 
					elemento, db.getNombreId(elemento),
					new OnSpinnerListener(elemento, tvl),
					utils.constants.SPINNER_DIALOG);
			myDialog.show();

			Log.v(TAG,"add newss "+elemento);
		}
		public void editar(String salida) {
			Log.v(TAG,"editar ..."+salida);
			setEdicion(true);
		}
		public void save(String salida) {
			Log.v(TAG, "guardar ..."+salida);
			setEdicion(false);
			customView.hidden = false;
			update();
			saveActividad(UPDATE); 		
		}
	}



}

