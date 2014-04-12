package uno.Urgentisimo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddCriteriosActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : ADD OBJETIVO ACTIVITY : ";
	private DataBaseHelper db;
	private ArrayList<nombreId> actividadesList;
	private Spinner actividadesSpinner;
	private SpinAdapter actividadesAdapter;
	private EditText etCriterio;	
	private EditText etDescripcion;
	private String idcriterio1;
	private String criterio1;
	private String dscripcion;
	private EditText etPeso;
	private String peso1;
	private boolean edicion;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Bundle extras = getIntent().getExtras(); 
		db = new DataBaseHelper(this);
		try {
		idcriterio1 = extras.getString("idcriterio");
		criterio1 = extras.getString("criterio");
		dscripcion = extras.getString("descripcion");
		peso1 = db.getDato(idcriterio1,"criterios","peso");
		
		} catch (Exception e) {
			Log.e(TAG,"exception ",e);
		}
		actividadesList = new ArrayList<nombreId>();
		
		Log.v(TAG, "");
		setContentView(R.layout.new_criterio_act);
		etCriterio = (EditText) findViewById(R.id.etCriterio);
		etDescripcion = (EditText) findViewById(R.id.etDescripcion);
		etPeso = (EditText) findViewById(R.id.etPeso);
		if (idcriterio1 != null ) {
			String stemp = "select * from actividades where _id in (select idactividad from criterios_actividad where idcriterio = '"+idcriterio1+"')";
			actividadesList = db.getNombreIdSql(stemp, "actividad");
			edicion = true;
			etCriterio.setText(criterio1);
			etDescripcion.setText(dscripcion);
			etPeso.setText(peso1);
			
		}
		this.fillActividades(this);
		refreshCriterios(this);
	}

	@Override    
	protected void onDestroy() {        
		super.onDestroy();
		db.close();
	}

	private void fillActividades(Context context) {
		ArrayList<nombreId> alActividades = db.getNombreId("actividades");
		actividadesSpinner = (Spinner) findViewById(R.id.actividadesSpinner);
		actividadesAdapter = new SpinAdapter(AddCriteriosActivity.this,
				android.R.layout.simple_spinner_item,
				alActividades);	
		actividadesSpinner.setAdapter(actividadesAdapter);	
	}


	private void refreshCriterios(final Context context) {
		LinearLayout lCriterios = (LinearLayout) findViewById(R.id.actividadesList);
		lCriterios.removeAllViewsInLayout();
		LayoutInflater iContenidos = (LayoutInflater) getSystemService( context.LAYOUT_INFLATER_SERVICE);
		for ( int i = 0; i < actividadesList.size() ; i++ ) {
			final int j = i;
			Log.v(TAG,"contador "+i);
			View vContenido = iContenidos.inflate(R.layout.texto_icono_item, null);
			TextView texto = (TextView) vContenido.findViewById(R.id.texto);
			ImageView removeCriterio = (ImageView) vContenido.findViewById(R.id.icono);
			LinearLayout noway = (LinearLayout) vContenido.findViewById(R.id.lista);
			noway.setVisibility(View.GONE);
			texto.setText(actividadesList.get(i).getNombre());
			removeCriterio.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					Log.v(TAG,"borrando al "+j+" de "+actividadesList.size());
					Log.v(TAG,"borrando contenido:"+actividadesList.get(j).getId());
					actividadesList.remove(actividadesList.get(j));
					refreshCriterios(context);
				}
			});
			lCriterios.addView(vContenido);
		}
	}	


	public void addActividad(final View v) {
		boolean existe = false;
		actividadesSpinner.getSelectedItemPosition();
		String idcontenido = actividadesAdapter.getItem(actividadesSpinner.getSelectedItemPosition()).getId();
		String contenido = actividadesAdapter.getItem(actividadesSpinner.getSelectedItemPosition()).getNombre();
		nombreId datos = new nombreId();
		datos.setId(idcontenido);
		datos.setNombre(contenido);
		for (int i = 0 ; i < actividadesList.size() ; i++) {
			Log.v(TAG," contenido "+actividadesList.get(i).toString()+":"+idcontenido );
			if (actividadesList.get(i).getId().equals(idcontenido)) {
				Log.v(TAG,"ya existe el contenido");
				Toast msg = Toast.makeText(AddCriteriosActivity.this, "ya existe la actividad", Toast.LENGTH_LONG);
				msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
				msg.show();			
				existe = true;
				break;
			} 		
		}
		if (existe == false) {
			Log.v(TAG,"adding "+idcontenido+" existe :"+existe);
			actividadesList.add(datos);
		}	
		refreshCriterios(v.getContext());
	}

	public void saveCriterio(View v) {
		boolean guardar = true;
		String descripcion =  etDescripcion.getText().toString();
		String peso = etPeso.getText().toString();
		ArrayList<nombreId> criterios = db.getNombreId("contenidos");
		String criterioNombre = etCriterio.getText().toString();
		for (int i = 0; i < criterios.size(); i++) {
			if (criterios.get(i).getNombre().equalsIgnoreCase(criterioNombre) && edicion == false) {
				etCriterio.setError("el criterio "+criterioNombre+" ya existe");
				guardar = false;
				break;
			}
		}
		if (guardar == true ) {
			String stemp = new String();
			String idcriterio = new String();
			if ( edicion == false )  {
				stemp = "insert into criterios (criterio, descripcion,  peso) values ('"+criterioNombre+"','"+descripcion+"','"+peso+"')";
				db.insertSql(stemp);
				idcriterio = db.getId("contenidos",criterioNombre);
			} else {
				stemp = "update criterios set criterio = '"+criterioNombre+"', descripcion = '"+descripcion+"', peso = '"+peso+"' where _id = '"+idcriterio1+"'";
				idcriterio = idcriterio1;
				db.insertSql(stemp);
				stemp = "delete from criterios_actividad where idcriterio = '"+idcriterio+"'";
				db.delete(stemp);
			}
			Log.v(TAG,stemp);

			for (int i = 0; i < actividadesList.size() ; i++) {
				String idactividad = actividadesList.get(i).getId(); 
				stemp = "insert into criterios_actividad (idcriterio, idactividad) values ('"+idcriterio+"','"+idactividad+"')";
				db.insertSql(stemp);
				Log.v(TAG,stemp);
			}
			finish();
		}
	}
	
	public String getDateString(DatePicker d) {
		Date fecha =(Date) new Date(d.getYear()-1900, d.getMonth(), d.getDayOfMonth());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(fecha);
		return date;
	}
	
	public void newActividad(View v) {
		// TODO Auto-generated method stub
		
		Intent i = new Intent(this, AddActividadActivity.class);
		i.putExtra("action", "new");
		startActivity(i);
		
		
//		final Dialog dialog = new Dialog(this);
//
//		dialog.setContentView(R.layout.dialog_actividad);
//		dialog.setTitle("Añadir actividad");
//
//		final EditText etActividad = (EditText) dialog.findViewById(R.id.etActividad );
//		final EditText etDescripcion = (EditText) dialog.findViewById(R.id.etDescripcion);
//		final DatePicker dpInicio = (DatePicker) dialog.findViewById(R.id.dpInicio);
//		final DatePicker dpFin = (DatePicker) dialog.findViewById(R.id.dpFin);
//
//		Button b1 = (Button) dialog.findViewById(R.id.bAdd);
//		b1.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View arg0) {
//				String actividad = etActividad.getText().toString();
//				String descripcion = etDescripcion.getText().toString();
//				if (db.existe("actividades", "actividad", actividad)) {
//					etActividad.setError("ya existe");
//				} else {
//					String stemp = "insert into actividades(actividad, descripcion, fecha_inicio, fecha_fin) values ( '"+actividad+"','"+descripcion+"','"+getDateString(dpInicio)+"','"+getDateString(dpFin)+"')";
//					db.insertSql(stemp);
//					Log.v(TAG,stemp);
//				}
//				
////				dialog.dismiss();
//				
//			} 
//		});
//
//		Button b2 = (Button) dialog.findViewById(R.id.bClose);
//		b2.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.v("TAG", " click en CANCELAR");
//				dialog.dismiss();
//				fillActividades(v.getContext());
//			} 
//		});
//
//		final TextView fhInicio = (TextView) dialog.findViewById(R.id.fhInicio);
//		fhInicio.setText("Fecha de Inicio : "+getDateString(dpInicio));
//		fhInicio.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View arg0) {
//				if (dpInicio.getVisibility() == View.VISIBLE ){
//					fhInicio.setText("Fecha de Inicio : "+getDateString(dpInicio));
//					dpInicio.setVisibility(View.GONE);
//				} else {
//					fhInicio.setText("Fecha de Inicio : "+getDateString(dpInicio));
//					dpInicio.setVisibility(View.VISIBLE);
//				}
//			}
//		});
//		final TextView fhFin = (TextView) dialog.findViewById(R.id.fhFin);
//		fhFin.setText("Fecha de Fin : "+getDateString(dpFin));
//		fhFin.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View arg0) {
//				if (dpFin.getVisibility() == View.VISIBLE ){
//					fhFin.setText("Fecha de Fin : "+getDateString(dpFin));
//					dpFin.setVisibility(View.GONE);
//				} else {
//					fhFin.setText("Fecha de Fin : "+getDateString(dpFin));
//					dpFin.setVisibility(View.VISIBLE);
//				}
//			}
//		});
//		dialog.show();
	}


}

