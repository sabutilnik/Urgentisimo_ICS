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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddObjetivoActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : ADD OBJETIVO ACTIVITY : ";
	public static final int ONE_FIELD_DIALOG = 1;
	public static final int DATE_DIALOG = 2;
	private DataBaseHelper db;
	private ArrayList<nombreId> contenidosList;
	private Spinner contenidos;
	private SpinAdapter contenidosAdapter;
	private Spinner asignaturas;
	private SpinAdapter asignaturasAdapter;
	private EditText objetivoTexto;
	private String idasignatura1;
	private String asignaturaText;
	private String editIdObjetivo;
	private nombreId asignaturaData;
	private String idobjetivo;
	private String objetivo1;
	private boolean edicion;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Bundle extras = getIntent().getExtras(); 
		idasignatura1 = extras.getString("idasignatura");
		asignaturaText = extras.getString("asignatura");
		idobjetivo = extras.getString("idobjetivo");
		objetivo1 = extras.getString("objetivo");

		contenidosList = new ArrayList<nombreId>();
		db = new DataBaseHelper(this);
		Log.v(TAG, "");
		setContentView(R.layout.add_objetivo);
		objetivoTexto = (EditText) findViewById(R.id.texto);
		if (idobjetivo == null ) {
			edicion = false;
			fillAsignatura(this);
			fillContenidos(this);
			refreshContenidos(this);
		} else {
			String stemp = "select * from asignaturas where _id in (select idasignatura from objetivosasignatura where idobjetivo = '"+idobjetivo+"')";
			ArrayList<nombreId> tmp1 = db.getNombreIdSql(stemp, "asignatura");
			idasignatura1 = tmp1.get(0).getId();
			stemp = "select * from contenidos where _id in (select idcontenido from contenidosobjetivo where idobjetivo = '"+idobjetivo+"')";
			contenidosList = db.getNombreIdSql(stemp,"contenido");
			objetivoTexto.setText(objetivo1);
			edicion = true;
			this.fillAsignatura(this);
			this.fillContenidos(this);
			this.refreshContenidos(this);
		}

	}

	@Override    
	protected void onRestart() {        
		super.onRestart();
		this.fillContenidos(this);
		this.refreshContenidos(this);
	}

	private void fillAsignatura(Context context) {
		ArrayList<nombreId> alAsignaturas = db.getNombreId("asignaturas");
		asignaturas = (Spinner) findViewById(R.id.asignaturas);
		asignaturasAdapter = new SpinAdapter(AddObjetivoActivity.this,
				android.R.layout.simple_spinner_item,
				alAsignaturas);	
		asignaturas.setAdapter(asignaturasAdapter);
		int posicion = 0;
		if (idasignatura1 != null) {
			for (int i = 0 ; i < asignaturasAdapter.getCount(); i++ ) {
				nombreId tmp = asignaturasAdapter.getItem(i);
				if (tmp.getId().equalsIgnoreCase(idasignatura1)) {
					posicion = i;
				}
			}
		}

		nombreId tmp = new nombreId();
		tmp.setNombre(asignaturaText);
		tmp.setId(idasignatura1);
		Log.v(TAG,"asignatura : "+asignaturaText+":"+idasignatura1);
		//		Log.v(TAG," as  :"+asignaturaData.getNombre()+":"+asignaturaData.getId());
		int spinnerPosition = asignaturasAdapter.getPosition(asignaturaData);
		Log.v(TAG,"posiciion asig :"+posicion);
		//set the default according to value
		asignaturas.setSelection(posicion);
	}

	private void fillContenidos(Context context) {
		ArrayList<nombreId> alContenidos = db.getNombreId("contenidos");
		contenidos = (Spinner) findViewById(R.id.contenidos);
		contenidosAdapter = new SpinAdapter(AddObjetivoActivity.this,
				android.R.layout.simple_spinner_item,
				alContenidos);	
		contenidos.setAdapter(contenidosAdapter);
	}

	private void refreshContenidos(final Context context) {
		LinearLayout lContenidos = (LinearLayout) findViewById(R.id.contenidosList);
		lContenidos.removeAllViewsInLayout();
		LayoutInflater iContenidos = (LayoutInflater) getSystemService( context.LAYOUT_INFLATER_SERVICE);
		for ( int i = 0; i < contenidosList.size() ; i++ ) {
			final int j = i;
			Log.v(TAG,"contador "+i);
			View vContenido = iContenidos.inflate(R.layout.texto_icono_item, null);
			TextView texto = (TextView) vContenido.findViewById(R.id.texto);
			ImageView delContenido = (ImageView) vContenido.findViewById(R.id.icono);
			LinearLayout noway = (LinearLayout) vContenido.findViewById(R.id.lista);
			noway.setVisibility(View.GONE);
			texto.setText(contenidosList.get(i).getNombre());
			delContenido.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					Log.v(TAG,"borrando al "+j+" de "+contenidosList.size());
					Log.v(TAG,"borrando contenido:"+contenidosList.get(j).getId());
					contenidosList.remove(contenidosList.get(j));
					refreshContenidos(context);
				}
			});
			lContenidos.addView(vContenido);
		}
	}	


	public void addContenido(final View v) {
		boolean existe = false;
		contenidos.getSelectedItemPosition();
		String idcontenido = contenidosAdapter.getItem(contenidos.getSelectedItemPosition()).getId();
		String contenido = contenidosAdapter.getItem(contenidos.getSelectedItemPosition()).getNombre();
		nombreId datos = new nombreId();
		datos.setId(idcontenido);
		datos.setNombre(contenido);
		for (int i = 0 ; i < contenidosList.size() ; i++) {
			Log.v(TAG," contenido "+contenidosList.get(i).toString()+":"+idcontenido );
			if (contenidosList.get(i).getId().equals(idcontenido)) {
				Log.v(TAG,"ya existe el contenido");
				Toast msg = Toast.makeText(AddObjetivoActivity.this, "ya existe el contenido", Toast.LENGTH_LONG);
				msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
				msg.show();			
				existe = true;
				break;
			} 		
		}
		if (existe == false) {
			Log.v(TAG,"adding "+idcontenido+" existe :"+existe);
			contenidosList.add(datos);
		}	
		refreshContenidos(v.getContext());
	}

	public void saveObjetivo(View v) {
		boolean guardar = true;
		ArrayList<nombreId> objetivos = db.getNombreId("objetivos");
		String objetivoNombre = objetivoTexto.getText().toString();
		for (int i = 0; i < objetivos.size(); i++) {
			if (objetivos.get(i).getNombre().equalsIgnoreCase(objetivoNombre) && edicion == false) {
				objetivoTexto.setError("el objetivo "+objetivoNombre+" ya existe");
				guardar = false;
				break;
			}
			if (objetivoNombre.length() <= 0) {
				objetivoTexto.setError("no puede ser vacío");
				guardar = false;
			}
		}
		if (guardar == true) {
			String stemp = new String();
			if (edicion == false) {
				stemp = "insert into objetivos (objetivo) values ('"+objetivoNombre+"')";			
				db.insertSql(stemp);
				idobjetivo = db.getId("objetivos",objetivoNombre);
				
			} else {
				stemp = "update objetivos set objetivo = '"+objetivoNombre+"' where _id = '"+idobjetivo+"'";
				db.insertSql(stemp);
			}
			Log.v(TAG,"idobjetivo "+idobjetivo);
			for (int i = 0; i < contenidosList.size() ; i++) {
				String idcontenido = contenidosList.get(i).getId(); 
				stemp = "insert into contenidosobjetivo (idcontenido, idobjetivo) values ('"+idcontenido+"','"+idobjetivo+"')";
				db.insertSql(stemp);
				Log.v(TAG,stemp);
			}
			String idasignatura =  asignaturasAdapter.getItem( asignaturas.getSelectedItemPosition()).getId();
			Log.v(TAG,"salvando : "+ asignaturas.getSelectedItemPosition());
			Log.v(TAG," .... "+asignaturasAdapter.getItem( asignaturas.getSelectedItemPosition()).getId()   );
			stemp = "insert into objetivosasignatura (idobjetivo, idasignatura) values ('"+idobjetivo+"','"+idasignatura+"')";
			Log.v(TAG,stemp);
			db.insertSql(stemp);
			finish();
		}
	}

	public void newContenido(View v) {

		Intent i = new Intent(this, AddContenidoActivity.class);
		startActivity(i);	
	}
	
	public void newAsignatura (View v) {
		customDialog myDialog = new customDialog(this, "Añadir asignatura", "Asignatura :", db.getNombreId("asignaturas"), new OnReadyListener(),ONE_FIELD_DIALOG);
        myDialog.show();
	}

	  private class OnReadyListener implements customDialog.ReadyListener {
	        public void ready(String texto) {
	            Toast.makeText(AddObjetivoActivity.this, "añadiendo :"+texto, Toast.LENGTH_LONG).show();
	            db.insertRegistro("asignaturas", texto);
	            fillAsignatura(null);
	        }

			public void ready(nombreId resp) {
				// TODO Auto-generated method stub
				
			}
	    }
}

