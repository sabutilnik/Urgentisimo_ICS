//package uno.Urgentisimo;
//
//import java.sql.Date;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.Map;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//public class EvalAlumnosActivity extends Activity {
//	protected static final String TAG = "URGENTISIMO : EVALUACION OBJETIVO ACTIVITY : ";
//	public static final int ONE_FIELD_DIALOG = 1;
//	public static final int DATE_DIALOG = 2;
//	public static final int INFO_DIALOG = 3;
//	private static final int YES_NO_DIALOG = 4;
//	private static final int SPINNER_DIALOG = 5;
//
//	private static final int INSERT = 0;
//	private static final int UPDATE = 1;
//	private DataBaseHelper db;
//
//	private Spinner spGrupos;
//	private Spinner spActividades;
//	private nombreId actividad;
//	private nombreId grupo;
//	ArrayList<nombreId> alumnos;
//	private SpinAdapter actividadesAdapter;
//	private SpinAdapter gruposAdapter;
//	private ArrayList<criterioInfo> criterios;
//	private Map<String, String> alumnosNombres;
//	private String fecha;
//
//
//	public void onCreate(Bundle icicle) {
//		super.onCreate(icicle);
//		Bundle extras = getIntent().getExtras(); 
//		db = new DataBaseHelper(this);
//		setContentView(R.layout.evaluacion_act);
//		criterios = db.getCriteriosInfo();
//		alumnosNombres = db.getMapSql("select * from alumnos", "_id", "nombre:apellidos");
//		fillActividades();
//		fecha = "2012-02-01";
//
//	}
//
//	private void fillActividades() {
//		actividad = new nombreId();
//		spActividades = (Spinner)findViewById(R.id.spActividades);
//		actividadesAdapter = new SpinAdapter(this, android.R.layout.simple_spinner_item, db.getNombreId("actividades"));
//		spActividades.setOnItemSelectedListener(new OnItemSelectedListener() {
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int pos, long arg3) {
//				Log.v(TAG," actividad actual "+ actividad.getId());
//				actividad = actividadesAdapter.getItem(spActividades.getSelectedItemPosition());
//				Log.v(TAG, "pasa a "+actividad.getId());
//				fillGrupos();
//			}
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});
//		spActividades.setAdapter(actividadesAdapter);
//	}
//	private void fillGrupos() {
//		spGrupos = (Spinner)findViewById(R.id.spGrupos);
//		String stemp = "select * from grupos where _id in (select idgrupo from actividades_grupos where idactividad = '"+actividad.getId()+"')";
//		//		if ( gruposAdapter.getCount()>0 ) { 
//		//			gruposAdapter.clear();
//		//		
//		//		}
//		Log.v(TAG," get grupos "+stemp);
//		gruposAdapter = new SpinAdapter(this, android.R.layout.simple_spinner_item, db.getNombreIdSql(stemp, "grupo"));
//		spGrupos.setAdapter(gruposAdapter);
//		spGrupos.setOnItemSelectedListener(new OnItemSelectedListener() {
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int pos, long arg3) {
//				grupo = gruposAdapter.getItem(spGrupos.getSelectedItemPosition());
//				alumnos = db.getAlumnosGrupo(grupo.getId());
//				fillAlumnos();
//			}
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});
//	}
//
//	private void fillAlumnos() {
//		LinearLayout alumnosList = (LinearLayout) findViewById(R.id.lista);
//		alumnosList.removeAllViews();
//		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		for (int i = 0; i < alumnos.size(); i++ ) {
//			View customView = inflater.inflate(R.layout.eval_subitem, null);
//			final TextView item = (TextView) customView.findViewById(R.id.itemName);
//			final LinearLayout subitem = (LinearLayout) customView.findViewById(R.id.sublista);
//			item.setText(alumnos.get(i).getNombre()+" "+alumnos.get(i).getDescripcion());		
//			final alumnoEvalInfo alumnoEval = new alumnoEvalInfo(this, alumnos.get(i).getId(),actividad.getId(), alumnosNombres.get(alumnos.get(i).getId()));	
//			item.setClickable(true);
//			item.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					Log.v(TAG," click en alumno "+item.getText().toString());
//					if (subitem.getVisibility()== View.GONE) {
//						subitem.setVisibility(View.VISIBLE);
//					} else {
//						subitem.setVisibility(View.GONE);
//					}
//				}
//			} );
//			Map<String, String> criteriosAlumnoActividad = alumnoEval.getCriterios();
//			Iterator it = criteriosAlumnoActividad.entrySet().iterator();
//			while (it.hasNext()) {
//				final Map.Entry pairs = (Map.Entry)it.next();
//				View cv = inflater.inflate(R.layout.eval_criterio_item, null);
//				TextView criterio = (TextView) cv.findViewById(R.id.tvCriterio);
//				final String critid = pairs.getKey().toString();
//				criterio.setText(getCriterioInfo(critid).getNombre());
//				final String dbtoc = db.getMapSql("select * from criterios_actividad where idactividad = '"+actividad.getId()+"'","idcriterio","toc").get("toc");
//				final TextView peso = (TextView) cv.findViewById(R.id.etPeso);
//				peso.setText(dbtoc);
//				final EditText eval = (EditText) cv.findViewById(R.id.etEvaluacion);
//				eval.setText( pairs.getValue().toString() );
//				peso.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View v) {
//						
//					}
//				});
//				
//				
//				eval.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//					public void onFocusChange(View v, boolean hasFocus) {
//						String elpeso = getCriterioInfo(critid).getPeso();
//						int toc = 0;
//						if (elpeso.equals("") == false) {
//							toc = Integer.valueOf(elpeso);
//						}
//						String newEval = eval.getText().toString();
//						String newPeso = peso.getText().toString();
//						alumnoEval.setEvalCriterio(pairs.getKey().toString() , newEval, fecha);
//						if (newEval.equals("") == false) {
//							if (toc > 0) {
//								if (Integer.valueOf(newEval)*100/toc > 0.8 ) {
//									item.setTextColor(0xffbdbdbd);
//								} else {
//									if ( Integer.valueOf(newEval) < 3 ) {
//										item.setTextColor(0xffbAadbd);
//									}
//								}
//							}
//							else {
//								Log.v(TAG,"no hay toc");
//								item.setTextColor(0xffbdbdbd);
//							}
//						}
//					}
//					
//				});				
//				subitem.addView(cv);
//
//			}
//			alumnosList.addView(customView);
//		}
//	}
//
//	private criterioInfo getCriterioInfo(String id) {
//		criterioInfo resultado = new criterioInfo();
//		for (int k = 0; k < criterios.size(); k++) {
//			if (criterios.get(k).getCriterioid().equalsIgnoreCase(id)) {
//				return criterios.get(k);
//			}
//		}
//		return resultado;
//	}
//
//	private LinearLayout getCriteriosEvaluacionAlumno(alumnoEvalInfo alumnoInfo) {
//
//
//		return null;
//	}
//}