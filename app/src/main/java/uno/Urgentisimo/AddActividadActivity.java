package uno.Urgentisimo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class AddActividadActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : ADD ACTIVIDAD ACTIVITY : ";
	public static final int ONE_FIELD_DIALOG = 1;
	public static final int DATE_DIALOG = 2;
	public static final int INFO_DIALOG = 3;
	private static final int YES_NO_DIALOG = 4;
	private static final int SPINNER_DIALOG = 5;

	private static final int INSERT = 0;
	private static final int UPDATE = 1;


	public actividadInfo actividad;


	private DataBaseHelper db;
	private SpinAdapter sptemp;

//	private ArrayList<nombreId> actividadesList;
	private Spinner spActividades;
//	private SpinAdapter actividadesAdapter;

	private CheckBox ckHecha;
	private CheckBox ckCorregida;


	private Spinner spActividadesGenerales;
	private SpinAdapter actividadesGeneralesAdapter;
	private ImageButton actividadesGeneralesAdd;

	private Spinner spTiposActividades;
	private SpinAdapter tiposActividadesAdapter;
	private ImageButton tiposActividadesAdd;

	private Spinner spAgrupamientos;
	private SpinAdapter agrupamientosAdapter;
	private ImageButton agrupamientosAdd;

	private Spinner spGrupos;
	private SpinAdapter gruposAdapter;
	private ImageButton gruposAdd;
	private ImageButton gruposNew;

	private TextView fhInicio;
	private TextView fhFin;

	private TextView tvDescripcion;
	private EditText etDescripcion;
	private LinearLayout layoutDescripcion;

	private LinearLayout layoutMateriales;
	private TextView tvMateriales;
	private ArrayList<nombreId> materialesList;

	private LinearLayout llCriterios;
	private TextView tvCriterios;
	private LinearLayout layoutCriterios;
	private ArrayList<nombreId> criteriosList;

	private EditText etDuracion;

	private LinearLayout llGrupos;

	private EditText etActividad;
	private String idactividad1;
	private boolean edicion;
	private boolean nuevo;
	private boolean show;
	protected nombreId selectedGroup;

	private String currentDate;
	private ImageView btEdit;
	private ImageView btDelete;
	private ImageView btSave;

	private ScrollView svEdit;
	private ScrollView svShow;
	private LinearLayout llActividad;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Bundle extras = getIntent().getExtras(); 
		db = new DataBaseHelper(this);
		setContentView(R.layout.new_actividad_act);

		btEdit = (ImageView) findViewById(R.id.btEdit);
		btDelete = (ImageView) findViewById(R.id.btDelete);
		btSave = (ImageView) findViewById(R.id.btSave);
		svEdit = (ScrollView) findViewById(R.id.actividadEdit);
		svShow = (ScrollView) findViewById(R.id.actividadShow);

		try {
			String action = extras.getString("action");
			if (action.equalsIgnoreCase("edit")) {
				idactividad1 = extras.getString("idactividad");
				actividad = new actividadInfo(this, idactividad1);
//				actividadesList =  db.getNombreIdSql("select * from actividades where idactividad = '"+actividad+"'","actividad");
				nuevo = false;
				edicion = true;
			}
			if (action.equalsIgnoreCase("new")) {
				Log.v(TAG,"nueva actividad");
				idactividad1 = "";
				actividad = new actividadInfo(this);
				nuevo = true;
				edicion = true;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				currentDate = sdf.format(new Date(System.currentTimeMillis()));
				actividad.setData("fecha_inicio", currentDate);
				actividad.setData("fecha_fin", currentDate);
				actividad.setData("idactividad_general", "0");
				actividad.setData("idtipo_actividad", "0");
				actividad.setData("idagrupamiento", "0");
				actividad.setData("idgrupo", "0");
			}
		} catch (Exception e) {
			Log.e(TAG,"exception ",e);
		}
		if ( edicion == true )  {
			fillActividadEdit(idactividad1);
		}
	}

	private View DatosView (String campo, String valor ){
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customView = inflater.inflate(R.layout.simple_line, null);
		TextView tvCampo = (TextView) customView.findViewById(R.id.tvCampo);
		TextView tvValor = (TextView) customView.findViewById(R.id.tvValor);
		tvCampo.setText(campo);
		tvValor.setText(" "+valor);
		if ( llActividad.getChildCount() %2 == 0) {
			customView.setBackgroundColor(0xABEEEEEE );
		}
		return customView;

	}


	private void fillActividadEdit(String act) {
		if (edicion == false  ) {
			return ;
		}
		this.idactividad1 = act;
		btEdit.setVisibility(View.GONE); btSave.setVisibility(View.VISIBLE); btDelete.setVisibility(View.VISIBLE);
		svEdit.setVisibility(View.VISIBLE);
		svShow.setVisibility(View.GONE);


		etActividad = (EditText) findViewById(R.id.etActividad);
		spActividades = (Spinner) findViewById(R.id.spActividades);
		spActividades.setVisibility(View.GONE);
		etActividad.setVisibility(View.VISIBLE);
		etDescripcion = (EditText) findViewById(R.id.etDescripcion);

		etActividad.setText(actividad.getData("actividad"));
		etDescripcion.setText(actividad.getData("descripcion"));

		ckHecha = (CheckBox)findViewById(R.id.ckHecha);
		ckCorregida = (CheckBox)findViewById(R.id.ckCorregida);


		fillSpinners();

		fhInicio = (TextView)findViewById(R.id.fhInicio);
		fhFin = (TextView)findViewById(R.id.fhFin);

		fillFechas();

		tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
		tvDescripcion.setClickable(true);
		tvDescripcion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG," click oooo");
				etDescripcion.setVisibility(toggleVisibility(etDescripcion));
			}
		});
 

		tvCriterios = (TextView) findViewById(R.id.tvCriterios);
		layoutCriterios = (LinearLayout)findViewById(R.id.layoutCriterios);
		fillLayouts(tvCriterios, layoutCriterios, "criterios", actividad.criterios);

		etDuracion = (EditText)findViewById(R.id.etDuracion);

		llGrupos = (LinearLayout) findViewById(R.id.gruposList);
		fillGrupos();
	}

	private void fillFechas() {
		Log.v(TAG,"fecha "+currentDate);
		fhInicio.setText(actividad.getData("fecha_inicio"));
		fhFin.setText(actividad.getData("fecha_fin"));
		if (actividad.getData("hecha").equalsIgnoreCase("1")) { 
			ckHecha.setChecked(true);
		}
		if (actividad.getData("corregida").equalsIgnoreCase("1")) {
			ckCorregida.setChecked(true);
		}
	}

	private void fillLayouts (TextView  tv, final LinearLayout ll,  String elemento, ArrayList<nombreId> datos) {
		if (elemento != "descripcion") {
			Log.v(TAG,"fill layout "+elemento);
			ll.removeAllViews();
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (int i = 0 ; i < datos.size() ; i++) {
				View customView = inflater.inflate(R.layout.objetivo_progra_item, null);
				if (elemento == "criterios") {
					customView = inflater.inflate(R.layout.criterio_progra_item, null);
				}
				TextView texto = (TextView) customView.findViewById(R.id.texto);
				ImageView del = (ImageView) customView.findViewById(R.id.delItem);
				ImageView editar = (ImageView) customView.findViewById(R.id.editItem);
				final EditText etToc = (EditText) customView.findViewById(R.id.etToc);
				etToc.setText(actividad.tocs.get(i).getNombre());
				texto.setText(actividad.criterios.get(i).getNombre());
				final int j = i;
				//no.setVisibility(View.GONE);
				del.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						actividad.delCriterioAtPosition(j);
						fillLayouts(tvCriterios, layoutCriterios, "criterios", actividad.criterios);
					}

				});
				editar.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Log.v(TAG,"eooo "+j+":"+etToc.getText().toString());
						actividad.setCriterioToc(j,etToc.getText().toString());
						Log.v(TAG,"save el puto toc");
					}

				});
				ll.addView(customView);

			}
		}
		else {
			if (edicion == true ) {
				EditText et1 = (EditText) findViewById(R.id.etDescripcion);
				et1.setVisibility(View.VISIBLE);
			}
		}
		tv.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if ( ll.getVisibility() == View.GONE  ) {
					ll.setVisibility(View.VISIBLE);
				} else {
					ll.setVisibility(View.GONE);
				}


			}
		});

	}

	private void fillGrupos() {
		llGrupos.removeAllViews();
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0 ; i < actividad.grupos.size() ; i++) {
			View customView = inflater.inflate(R.layout.objetivo_progra_item, null);
			TextView texto = (TextView) customView.findViewById(R.id.texto);
			ImageView del = (ImageView) customView.findViewById(R.id.delItem);
			ImageView no = (ImageView) customView.findViewById(R.id.editItem);
			texto.setText(actividad.grupos.get(i).getNombre());
			final int j = i;
			del.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					actividad.delGrupoItemAtPosition(j);
					fillGrupos();
				}
			});
			llGrupos.addView(customView);
		}
	}



	public void addGrupo(View v) {
		actividad.addGrupoItem( selectedGroup );
		fillGrupos();
	}
	private void fillSpinners() {
		Log.v(TAG,"entro en fillspinners");
		spActividadesGenerales = (Spinner) findViewById(R.id.spActGen);
		actividadesGeneralesAdd = (ImageButton) findViewById(R.id.btActGen);
		actividadesGeneralesAdapter =new SpinAdapter(this, android.R.layout.simple_spinner_item, db.getNombreId("actividades_generales"));
		actividadesGeneralesAdd.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View arg0) {
				Log.v(TAG," añado actividad general... o eso creo...");
					customDialog myDialog = new customDialog(AddActividadActivity.this, 
							"Añadir ", "Actividad General :", 
							db.getNombreId("actividades_generales"), 
							new OnReadyListener("actividades_generales"),ONE_FIELD_DIALOG);
					myDialog.show();
			}
		});
		spActividadesGenerales.setAdapter(actividadesGeneralesAdapter);
		String tempo = actividad.getData("idactividad_general");
		Log.v(TAG,"PARA CAMBIAR actividad_general "+tempo);
		Log.v(TAG,"CAMBIANDO actividad_general a posicion "+ actividadesGeneralesAdapter.getIdPosition(actividad.getData("idactividad_general")));
        spActividadesGenerales.setSelection( actividadesGeneralesAdapter.getIdPosition(actividad.getData("idactividad_general")) );
		
		spTiposActividades = (Spinner) findViewById(R.id.spTipoActividad);
//		tiposActividadesAdd = (ImageButton) findViewById(R.id.btTipoAct);
		tiposActividadesAdd = (ImageButton) findViewById(R.id.btTipoAct);
		tiposActividadesAdapter =new SpinAdapter(this, android.R.layout.simple_spinner_item, db.getNombreId("tipos_actividades"));
		tiposActividadesAdd.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View arg0) {
					customDialog myDialog = new customDialog(AddActividadActivity.this, 
							"Añadir ", "Tipos de Actividades :", 
							db.getNombreId("tipos_actividades"), 
							new OnReadyListener("tipos_actividades"),ONE_FIELD_DIALOG);
					myDialog.show();
			}
		});
		spTiposActividades.setAdapter(tiposActividadesAdapter);
		spTiposActividades.setSelection( tiposActividadesAdapter.getIdPosition(actividad.getData("idtipo_actividade")) );

		spAgrupamientos = (Spinner) findViewById(R.id.spAgrupamiento);
		agrupamientosAdd = (ImageButton) findViewById(R.id.btAgrupa);
		agrupamientosAdapter =new SpinAdapter(this, android.R.layout.simple_spinner_item, db.getNombreId("agrupamientos"));
		agrupamientosAdd.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View arg0) {
					customDialog myDialog = new customDialog(AddActividadActivity.this, 
							"Añadir ", "Agrupamiento :", 
							db.getNombreId("agrupamientos"), 
							new OnReadyListener("agrupamientos"),ONE_FIELD_DIALOG);
					myDialog.show();
			}
		});
		spAgrupamientos.setAdapter(agrupamientosAdapter);
		spAgrupamientos.setSelection( agrupamientosAdapter.getIdPosition(actividad.getData("idagrupamiento")) );
		
		spGrupos = (Spinner) findViewById(R.id.spGrupos);
		gruposAdd = (ImageButton) findViewById(R.id.btGruposAdd);
		gruposNew = (ImageButton) findViewById(R.id.btGruposNew);
		
		fillSpinner(spGrupos, gruposAdapter, gruposNew, "grupos","Grupo", "Grupo","idgrupo");

	}

	/* SpinAdapter and spinner should be initialized before using them in method fillSpinner
	 * If tabla = grupos, addgrupos activity should be started instead of normal customDialog.
	 */
	private void fillSpinner(Spinner sp, SpinAdapter spa, ImageView saveButton, final String tabla, final String titulo, final String campo, final String idcampo) {
		spa = new SpinAdapter(this, android.R.layout.simple_spinner_item, db.getNombreId(tabla));
		sptemp = spa;
		sp.setAdapter(spa);
		final Spinner sp1 = sp;
		saveButton.setClickable(true);
		String x = actividad.getData(idcampo);
		if (tabla.equalsIgnoreCase("grupos") == false ) {
			Log.v(TAG," para la tabla "+tabla);
			int posicion = spa.getIdPosition(actividad.getData(idcampo));
			Log.v(TAG,"cambio spinner "+tabla+" : icampo : "+idcampo+" : posicion : "+posicion);
			sp.setSelection(posicion);

			if (actividad.getData(idcampo) != "") {
				String elcampo = actividad.getData(idcampo);
				sp.setSelection(spa.getIdPosition(actividad.getData(idcampo)));
			}					
		}
		saveButton.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View arg0) {
				if (tabla.equalsIgnoreCase("grupos")) {
					addGrupo();
				} else {

					customDialog myDialog = new customDialog(AddActividadActivity.this, 
							"Añadir "+titulo, campo+" :", 
							db.getNombreId(tabla), 
							new OnReadyListener(tabla),ONE_FIELD_DIALOG);
					myDialog.show();
				}
			}
		});

		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				Log.v(TAG,"cambio spinner a pos"+pos+" y tabla "+tabla);
				actividad.setData(idcampo, sptemp.getDataId(pos));
				if (tabla.equalsIgnoreCase("grupos")) {
					selectedGroup = sptemp.getItem(pos);
				}
				Log.v(TAG," cambio en "+pos+":"+tabla);

			}
			public void onNothingSelected(AdapterView<?> arg0) {
				Log.v(TAG," cambio en "+sp1.getSelectedItemPosition()+":"+tabla);
			}
		});

	}

	protected void addGrupo()  {
		Intent i = new Intent(this, EditarGruposActivity.class);
		i.putExtra("action", "new");
		startActivity(i);
	}

	public void setFhInicio (View v) {
		customDialog myDialog = new customDialog(AddActividadActivity.this, "fecha",  new OnDateListener(fhInicio), fhInicio.getText().toString(), DATE_DIALOG );
		myDialog.show();
	}

	public void setFhFin(View v) {
		customDialog myDialog = new customDialog(AddActividadActivity.this, "fecha",  new OnDateListener(fhFin), fhFin.getText().toString(), DATE_DIALOG );
		myDialog.show();
	}


	/**********************************************************************************************************
	 * Implement OnReadyListener and OnDateListener methods, one for adding records to spinners and the other
	 * one to set dates
	 * @author ricardo
	 *
	 */

	public class OnReadyListener implements customDialog.ReadyListener {
		private static final String TAG = "OnReadyListener";
		private String tabla;
		public OnReadyListener(String tabla) {
			this.tabla = tabla;
		}

		public void ready(String texto) {
			Log.v(TAG,tabla+" : "+texto);
			if ( tabla.equalsIgnoreCase("actividades")) {
				db.borraIdDeTabla("actividades", actividad.getIdactividad());
				finish();
			} else {
				db.insertRegistro(tabla, texto);
				fillSpinners();
			}
		}

		public void ready(nombreId resp) {
			// TODO Auto-generated method stub

		}
	}
	public class OnDateListener implements customDialog.ReadyListener {
		private static final String TAG = "OnReadyListener";
		private String date;
		private TextView tv;

		public OnDateListener(TextView fecha) {
			this.tv = fecha;
		}
		public void ready(String texto) {
			tv.setText(texto);
			Log.v(TAG," : "+texto);
		}
		public void ready(nombreId resp) {
			// TODO Auto-generated method stub

		}
	}

	public class OnSpinnerListener implements customDialog.ReadyListener {
		private static final String TAG = "OnSpinnerListener";
		private String elemento;

		public OnSpinnerListener (String elemento) {
			this.elemento = elemento;
		}

		public void ready(nombreId item) {
			if (elemento.equalsIgnoreCase("criterios")) {
				Log.v(TAG,"SPINNER LISTENEEER " + item.getNombre());
				actividad.addCriterio(item);
				fillLayouts(tvCriterios, layoutCriterios, "criterios", actividad.criterios);
			}
			if (elemento.equalsIgnoreCase("materiales")) {
				fillLayouts(tvMateriales, layoutMateriales, "materiales", actividad.materiales);
			}
		}

		public void ready(String salida) {
			// TODO Auto-generated method stub

		}
	}

	public void saveActividad(View v) {
		boolean guardar = true;
		actividad.setData("actividad", etActividad.getText().toString());
		if (ckHecha.isChecked()) { actividad.setData("hecha","1"); } else { actividad.setData("hecha","0"); } 
		if (ckCorregida.isChecked()) { actividad.setData("corregida", "1") ; } else { actividad.setData("corregida", "0");} 
		actividad.setData("descripcion", etDescripcion.getText().toString());
		actividad.setData("fecha_inicio", fhInicio.getText().toString());
		actividad.setData("fecha_fin", fhFin.getText().toString());
		actividad.setData("duracion", etDuracion.getText().toString());
		nombreId ag = (nombreId) spActividadesGenerales.getSelectedItem();
//		Log.v(TAG,"ACTIVIDAD GENERAL :"+ag.getNombre());
		actividad.setData("idactividad_general", ag.getId());
		nombreId tipact = (nombreId) spTiposActividades.getSelectedItem();
		actividad.setData("idtipo_actividad", tipact.getId());
		nombreId agrupa = (nombreId) spAgrupamientos.getSelectedItem();
		actividad.setData("idagrupamiento", agrupa.getId());
//		actividad.setData("idgrupo", "0");

		if (idactividad1 == "") {
			if ( db.existe("actividades", "actividad", actividad.getData("actividad")) == true ) {
				guardar = false;
				etActividad.setError("ya existe");
			} 
		}
		if (actividad.getData("actividad") == "") {
			guardar = false;
			etActividad.setError("vacío");
		}
		if (guardar == true ) {
			if (idactividad1 == "") {
				actividad.saveActividad(INSERT);
			} else {
				actividad.saveActividad(UPDATE);
			}
			Intent i = getIntent();
			i.putExtra("action", "edit");
			finish();
			startActivity(i);
		}
	}
	public void saveasnew(View v) {
		boolean guardar = true;
		actividad.setData("actividad", etActividad.getText().toString());
		if (ckHecha.isChecked()) { actividad.setData("hecha","1"); } else { actividad.setData("hecha","0"); } 
		if (ckCorregida.isChecked()) { actividad.setData("corregida", "1") ; } else { actividad.setData("corregida", "0");} 
		actividad.setData("descripcion", etDescripcion.getText().toString());
		actividad.setData("fecha_inicio", fhInicio.getText().toString());
		actividad.setData("fecha_fin", fhFin.getText().toString());
		actividad.setData("duracion", etDuracion.getText().toString());
		nombreId ag = (nombreId) spActividadesGenerales.getSelectedItem();
//		Log.v(TAG,"ACTIVIDAD GENERAL :"+ag.getNombre());
		actividad.setData("idactividad_general", ag.getId());
		nombreId tipact = (nombreId) spTiposActividades.getSelectedItem();
		actividad.setData("idtipo_actividad", tipact.getId());
		nombreId agrupa = (nombreId) spAgrupamientos.getSelectedItem();
		actividad.setData("idagrupamiento", agrupa.getId());

		if (idactividad1 == "") {
			if ( db.existe("actividades", "actividad", actividad.getData("actividad")) == true ) {
				guardar = false;
				etActividad.setError("ya existe");
			} 
		}
		if (actividad.getData("actividad") == "") {
			guardar = false;
			etActividad.setError("vacío");
		}
		if (guardar == true ) {

		String idact = actividad.saveActividad(INSERT);

		Intent i = getIntent();
		i.putExtra("action", "edit");
		i.putExtra("idactividad",idact);
		finish();
		startActivity(i);
		}
	}
	public void editActividad (View v) {
		Log.v(TAG,"editar actividad "+actividad.getData("actividad"));
		edicion = true;
		fillActividadEdit(actividad.getIdactividad());
	}
	public void deleteActividad (View v) {
		customDialog myDialog = new customDialog(AddActividadActivity.this, db.getNombreId("criterios"),
				"Borrar actividad", 	new OnReadyListener("actividades"),YES_NO_DIALOG);
		myDialog.show();
	}
	private int toggleVisibility(View v) {
		if (v.getVisibility()== View.GONE) {
			return View.VISIBLE;
		} else {
			return View.GONE;
		}
	}

	public void  addCriterio(View v) {
		customDialog myDialog = new customDialog(AddActividadActivity.this, db.getNombreId("criterios"),
				"Añadir criterio", 	new OnSpinnerListener("criterios"),SPINNER_DIALOG);
		myDialog.show();
	}
	public void newCriterio(View v) {
		Intent i = new Intent(this, AddCriteriosActivity.class);
		startActivity(i);
	}
	public void newActividad(View v) {
		Intent i = getIntent();
		i.putExtra("action","new");
		startActivity(i);
	}
	public class MyLinearLayout extends LinearLayout {
		private String texto = "";
		

	    public MyLinearLayout(Context context) {
	            super(context);
	    }

	    public MyLinearLayout(Context context, AttributeSet attrs) {
	            super(context, attrs);
	    }

	}
	
	public void addActividadGeneral() {
		Log.v(TAG," añado actividad general... o eso creo...");
		customDialog myDialog = new customDialog(AddActividadActivity.this, 
				"Añadir ", "Actividad General :", 
				db.getNombreId("actividades_generales"), 
				new OnReadyListener("actividades_generales"),ONE_FIELD_DIALOG);
		myDialog.show();
	}
}





