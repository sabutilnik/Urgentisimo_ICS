package uno.Urgentisimo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CriteriosActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : CRITERIOS ACTIVITY : ";
	private DataBaseHelper db;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		db = new DataBaseHelper(this);
		Log.v(TAG, "");
		setContentView(R.layout.criterios_act);
		fillCriterios(this);

	}

	@Override    
	protected void onDestroy() {        
		super.onDestroy();
		db.close();
	}

	private void fillCriterios(Context context) {
		ArrayList<criterioInfo>  criterios = new ArrayList<criterioInfo>();
		LinearLayout lCriterios = (LinearLayout)findViewById(R.id.criteriosList);
		LayoutInflater iCriterios = (LayoutInflater) getSystemService(context.LAYOUT_INFLATER_SERVICE);
		ImageView im = (ImageView) findViewById(R.id.add_criterio);
		im.setClickable(true);
		criterios = db.getCriteriosInfo();

		for (int j = 0 ; j <  criterios.size() ; j++ ) {
			final criterioInfo criterio = criterios.get(j);
			View vCriterios = iCriterios.inflate(R.layout.criterio_item, null);
			TextView critText = (TextView) vCriterios.findViewById(R.id.criterioItemText);
			critText.setText(criterio.getNombre());
			critText.setClickable(true);
				
			final TextView descripcion = (TextView) vCriterios.findViewById(R.id.criterioDescripcion);
			descripcion.setText(criterio.getDescripcion());
			
			final LinearLayout criterioData = (LinearLayout) vCriterios.findViewById(R.id.criterioData);
			criterioData.setVisibility(View.GONE);
				
			final LinearLayout lContenidos = (LinearLayout) vCriterios.findViewById(R.id.contenidosList);
			LayoutInflater iContenidos = (LayoutInflater) getSystemService(context.LAYOUT_INFLATER_SERVICE);
			for (int k = 0 ; k < criterio.getContenidosCount() ; k++) {
				String contenido = criterio.getContenido(k); 
				TextView t1 = new TextView(context);
				t1.setText(contenido);
				lContenidos.addView(t1);
			}
			
			final LinearLayout lActividades = (LinearLayout) vCriterios.findViewById(R.id.actividadesList);
			LayoutInflater iActividades = (LayoutInflater) getSystemService(context.LAYOUT_INFLATER_SERVICE);
			for (int k = 0 ; k < criterio.getActividadesCount() ; k++) {
				String actividad = criterio.getActividad(k);
				TextView t2 = new TextView(context);
				t2.setText(actividad);
				lActividades.addView(t2);
			}
			lCriterios.addView(vCriterios);
			
			critText.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (criterioData.getVisibility() == View.GONE) {
						criterioData.setVisibility(View.VISIBLE);
					} else {
						criterioData.setVisibility(View.GONE);
					}
				}
			});
			
		}

	}

	public void addCriterio(View v) {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(this);

		ArrayList<nombreId> alContenido = db.getNombreId("contenidos");

		dialog.setContentView(R.layout.dialog_criterios);
		dialog.setTitle("Añadir criterio");

		final EditText etNombre = (EditText) dialog.findViewById(R.id.etContenido );	
		final EditText etDescripcion = (EditText) dialog.findViewById(R.id.etDescripcion);
		final Spinner objetivos = (Spinner) dialog.findViewById(R.id.spinner2);

		TextView tv1 = (TextView) dialog.findViewById(R.id.textView1);
		TextView tv3 = (TextView) dialog.findViewById(R.id.textView2);
		TextView tv2 = (TextView) dialog.findViewById(R.id.textView2);
		final SpinAdapter objetivosAdapter = new SpinAdapter(CriteriosActivity.this,
				android.R.layout.simple_spinner_item,
				alContenido);	
		objetivos.setAdapter(objetivosAdapter);

		Button b1 = (Button) dialog.findViewById(R.id.bAdd);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				int pos = objetivos.getSelectedItemPosition();
				String myId = objetivosAdapter.getItem(pos).getId();
				String myName = objetivosAdapter.getItem(pos).getNombre();
			    String criterio = etNombre.getText().toString();
			    String descripcion = etDescripcion.getText().toString();
			    
				if (etValida("criterios","criterio",criterio) == false) {
					etNombre.setError("el criterio "+criterio+" ya existe");
					
				} else {
					String stemp = "insert into criterios(criterio, descripcion, idcontenido) values ( '"+criterio+"','"+descripcion+"','"+myId+"')";
					Log.v(TAG,stemp);
					db.insertSql(stemp);
				}
			} 
		});
		Button b2 = (Button) dialog.findViewById(R.id.bClose);
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v("TAG", " click en CANCELAR"+objetivos.getSelectedItem().toString() );
				dialog.dismiss();
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			} 
		});

		dialog.show();

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


//	public void addCriterio(View v) {
//		Log.v(TAG,"button criterios ... ");
//		Intent i = new Intent(CriteriosActivity.this, EditarCriterios.class);
//		startActivity(i);
//	}
}
