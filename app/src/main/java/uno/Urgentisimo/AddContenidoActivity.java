package uno.Urgentisimo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddContenidoActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : ADD OBJETIVO ACTIVITY : ";
	public static final int ONE_FIELD_DIALOG = 1;
	public static final int DATE_DIALOG = 2;
	private DataBaseHelper db;
	private ArrayList<nombreId> criteriosList;
	private Spinner criteriosSpinner;
	private SpinAdapter criteriosAdapter;
	private EditText etContenido;
	private Spinner bloquesSpinner;
	private SpinAdapter bloquesAdapter;
	private String idcontenido1;
	private String contenido1;
	private String idbloque1;
	private boolean edicion;
	private UtilsLib utils;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Bundle extras = getIntent().getExtras();
		edicion = false;
		db = new DataBaseHelper(this);
		utils = new UtilsLib(this);
		try {
		idcontenido1 = extras.getString("idcontenido");
		contenido1 = extras.getString("contenido");
		} catch (Exception e) {
			Log.e(TAG,"exc ",e);
		}
		criteriosList = new ArrayList<nombreId>();
		
		setContentView(R.layout.add_contenido_act);
		etContenido = (EditText) findViewById(R.id.texto);
		if (idcontenido1 != null ) {
			edicion = true;
			String stemp = "select * from criterios where _id in (select idcriterio from criterioscontenido where idcontenido = '"+idcontenido1+"')";
			criteriosList = db.getNombreIdSql(stemp, "criterio");
			etContenido.setText(contenido1);
			idbloque1 = db.getDato(idcontenido1, "contenidos", "idbloque");
			Log.v(TAG,"bloque1 "+idbloque1);
		}
//		this.fillCriterios(this);
		this.fillBloques(this);
		this.refreshCriterios(this);

	}

	@Override    
	protected void onRestart() {        
		super.onRestart();
		this.fillCriterios(this);
		this.refreshCriterios(this);
	}

	private void fillCriterios(Context context) {
		ArrayList<nombreId> alCriterios = db.getNombreId("criterios");
		criteriosSpinner = (Spinner) findViewById(R.id.criteriosSpinner);
		criteriosAdapter = new SpinAdapter(AddContenidoActivity.this,
				android.R.layout.simple_spinner_item,
				alCriterios);	
		criteriosSpinner.setAdapter(criteriosAdapter);	
	}
	private void fillBloques(Context context) {
		ArrayList<nombreId> alBloques = db.getNombreId("bloques");
		bloquesSpinner = (Spinner) findViewById(R.id.bloquesSpinner);
		bloquesAdapter = new SpinAdapter(AddContenidoActivity.this,
				android.R.layout.simple_spinner_item,
				alBloques);	
		bloquesSpinner.setAdapter(bloquesAdapter);	
		
		int posicion = 0;
		if (edicion == true) {
			for (int i = 0 ; i < bloquesAdapter.getCount(); i++ ) {
				nombreId tmp = bloquesAdapter.getItem(i);
				Log.v(TAG,"bloquesAdapter "+tmp.getId()+":"+tmp.getNombre()+"--"+idbloque1);
				if (tmp.getId().equalsIgnoreCase(idbloque1)) {
					posicion = i;
				}
			}
			bloquesSpinner.setSelection(posicion);
		}

		
	}

	private void refreshCriterios(final Context context) {
		LinearLayout lCriterios = (LinearLayout) findViewById(R.id.criteriosList);
		lCriterios.removeAllViewsInLayout();
		LayoutInflater iContenidos = (LayoutInflater) getSystemService( context.LAYOUT_INFLATER_SERVICE);
		ArrayList<nombreId> criterios = db.getNombreId("criterios");
		for ( int i = 0; i < criterios.size() ; i++ ) {
			final nombreId criterio = criterios.get(i);
			View vContenido = iContenidos.inflate(R.layout.grupos_select_alumno, null);
			TextView texto = (TextView) vContenido.findViewById(R.id.alumnoNombre);
			CheckBox ck = (CheckBox) vContenido.findViewById(R.id.ckMiembro);
			texto.setText(criterio.getNombre());
			ck.setChecked(utils.member(criteriosList, criterio.getId()));
			ck.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						criteriosList.add(criterio);
					} else {
						criteriosList.remove(criterio);
					}
					
				}
			});
			lCriterios.addView(vContenido);
		}
	}	


	public void addCriterio(final View v) {
		boolean existe = false;
		criteriosSpinner.getSelectedItemPosition();
		String idcontenido = criteriosAdapter.getItem(criteriosSpinner.getSelectedItemPosition()).getId();
		String contenido = criteriosAdapter.getItem(criteriosSpinner.getSelectedItemPosition()).getNombre();
		nombreId datos = new nombreId();
		datos.setId(idcontenido);
		datos.setNombre(contenido);
		for (int i = 0 ; i < criteriosList.size() ; i++) {
			Log.v(TAG," contenido "+criteriosList.get(i).toString()+":"+idcontenido );
			if (criteriosList.get(i).getId().equals(idcontenido)) {
				Log.v(TAG,"ya existe el contenido");
				Toast msg = Toast.makeText(AddContenidoActivity.this, "ya existe el criterio", Toast.LENGTH_LONG);
				msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
				msg.show();			
				existe = true;
				break;
			} 		
		}
		if (existe == false) {
			Log.v(TAG,"adding "+idcontenido+" existe :"+existe);
			criteriosList.add(datos);
		}	
		refreshCriterios(v.getContext());
	}

	public void saveContenido(View v) {
		boolean guardar = true;
		String idbloque =  bloquesAdapter.getItem( bloquesSpinner.getSelectedItemPosition()).getId();
//		nombreId xyz = (nombreId) bloquesSpinner.getSelectedItem();
//		Log.v(TAG,"BLOQUES : "+xyz.getId()+":"+xyz.getNombre());
		ArrayList<nombreId> contenidos = db.getNombreId("contenidos");
		String contenidoNombre = etContenido.getText().toString();
		for (int i = 0; i < contenidos.size(); i++) {
			if (contenidos.get(i).getNombre().equalsIgnoreCase(contenidoNombre) && edicion == false) {
				etContenido.setError("el contenido "+contenidoNombre+" ya existe");
				guardar = false;
				break;
			}
		}
		if (guardar == true ) {
			String stemp = new String();
			if (edicion == false ) {
				stemp = "insert into contenidos (contenido, idbloque) values ('"+contenidoNombre+"',"+idbloque+")";
			} else {
				stemp = "update contenidos set contenido = '"+contenidoNombre+"', idbloque = '"+idbloque+ "'  where _id = '"+idcontenido1+"'";
				Log.v(TAG,stemp);
				db.insertSql(stemp);
				stemp = "delete from criterioscontenido where idcontenido = '"+idcontenido1+"'";
				Log.v(TAG,stemp);
				db.delete(stemp);
			}
			Log.v(TAG,stemp);
			db.insertSql(stemp);
			String idcontenido = db.getId("contenidos",contenidoNombre);
			for (int i = 0; i < criteriosList.size() ; i++) {
				String idcriterio = criteriosList.get(i).getId(); 
				stemp = "insert into criterioscontenido (idcriterio, idcontenido) values ('"+idcriterio+"','"+idcontenido+"')";
				db.insertSql(stemp);
				Log.v(TAG,stemp);
			}
			finish();
		}
	}



	public void newCriterio(View v) {
		Intent i = new Intent(this, AddCriteriosActivity.class);
		startActivity(i);	

	}
	
	public void newBloque(View v) {
		customDialog myDialog = new customDialog(this, "Añadir bloque", "Bloque :", db.getNombreId("bloques"), new OnReadyListener(), ONE_FIELD_DIALOG);
        myDialog.show();
	}
	 private class OnReadyListener implements customDialog.ReadyListener {
	        public void ready(String texto) {
	            Toast.makeText(AddContenidoActivity.this, "añadiendo :"+texto, Toast.LENGTH_LONG).show();
	            db.insertRegistro("bloques", texto);
	            fillBloques(null);
	        }

			public void ready(nombreId resp) {
				// TODO Auto-generated method stub
				
			}
	    }

	private boolean etValida(String tabla, String campo, String valor) {
		boolean resultado = false;
		if (valor.length() == 0 )  {
			resultado = false;
		} else {
			if (db.existe(tabla, campo, valor) ) {
				resultado = false;
			}
			else {
				resultado = true;
			}
		}
		return resultado;
	}
}

