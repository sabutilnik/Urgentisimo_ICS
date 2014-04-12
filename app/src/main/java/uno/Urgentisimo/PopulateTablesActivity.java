package uno.Urgentisimo;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PopulateTablesActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : RELLENAR TABLAS : ";
	private DataBaseHelper db;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		db = new DataBaseHelper(this);
		Log.v(TAG, "");
		setContentView(R.layout.populate_tables);

		fillList(this);

	}

	@Override    
	protected void onDestroy() {        
		super.onDestroy();
		db.close();
	}

	private void fillList(Context context) {
		String elcampo = new String();
		final HashMap<String, String > tablasTextos =  new HashMap<String, String>();
		tablasTextos.put("asignaturas", "Asignaturas");
		tablasTextos.put("competencias","Competencias");
		tablasTextos.put("objetivos","Objetivos");
		tablasTextos.put("bloques","Bloques de contenido");
		tablasTextos.put("contenidos","Contenidos");
		String[] tablas = {"asignaturas", "competencias", "objetivos", "bloques", "contenidos"};
		LinearLayout lTabla = (LinearLayout)findViewById(R.id.listaTabla);
		LayoutInflater iTabla = (LayoutInflater) getSystemService(context.LAYOUT_INFLATER_SERVICE);
		lTabla.setVisibility(View.VISIBLE);

		for (int j = 0 ; j <  tablas.length ; j++ ) {
			final String latabla = tablas[j];
			Log.v(TAG," entrando en fill "+latabla);
			View vTabla = iTabla.inflate(R.layout.tabla_item, null);
			final TextView tablaNombre = (TextView) vTabla.findViewById(R.id.tablaNombre);
			final String tNombre = tablasTextos.get(tablas[j]);  
			tablaNombre.setText(" + "+ tNombre);
			vTabla.setVisibility(View.VISIBLE);

			ImageView ivmas = (ImageView) vTabla.findViewById(R.id.icono);
			ivmas.setClickable(true);
			ivmas.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					addRegistro(latabla, tablasTextos);
				}
			});

			final LinearLayout lista  = (LinearLayout) vTabla.findViewById(R.id.listaRegistros);

			LayoutInflater linflater = (LayoutInflater) getSystemService(context.LAYOUT_INFLATER_SERVICE);
			ArrayList<String[]> elementos = db.getIdTexto(latabla);

			for (int i = 0; i < elementos.size() ; i++) {
				String[] tmp = elementos.get(i);
				View customView = linflater.inflate(R.layout.campos_item, null);
				customView.setClickable(true);
				TextView tv = (TextView) customView.findViewById(R.id.texto);
				tv.setText("     "+tmp[1]);
				final String elid = tmp[0];
				ImageView iv = (ImageView) customView.findViewById(R.id.icono);
				iv.setClickable(true);
				iv.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Log.v(TAG,"borar de tabla "+latabla+" el id "+elid);
						db.borraIdDeTabla(latabla, elid);
						lista.setVisibility(View.GONE);
					}
				});
				TextView stv1 = (TextView) customView.findViewById(R.id.subTexto1);
				TextView stv2 = (TextView) customView.findViewById(R.id.subTexto2);
				if (!tmp[2].equals("")) {
					stv1.setText(tmp[2]);
					stv2.setText(tmp[3]);
				} else {
					stv1.setVisibility(View.GONE);
					stv2.setVisibility(View.GONE);
				}
				lista.addView(customView);
				lista.setVisibility(View.GONE);
			}

			tablaNombre.setClickable(true);
			tablaNombre.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (lista.getVisibility() == View.VISIBLE) {
						lista.setVisibility(View.GONE);
						tablaNombre.setText(" + "+tNombre);
					} else {
						lista.setVisibility(View.VISIBLE);
						tablaNombre.setText(" - "+tNombre);
					}
				}
			}  );
			tablaNombre.setOnLongClickListener( new View.OnLongClickListener() {
				public boolean onLongClick(View v) {
					addRegistro(latabla, tablasTextos);
					return true;
				}
			}    );
			lTabla.addView(vTabla);
		}

	}

	protected void fillContenidos(Context context ){

	}
	protected void addRegistro(final String latabla, HashMap<String, String> tablasTextos) {
		if (!latabla.equals("contenidos")) {
			Log.v(TAG,"clase long");
			PromptDialog dlg = new PromptDialog(PopulateTablesActivity.this, "Añadir registro a "+latabla, tablasTextos.get(latabla) ) {
				@Override
				public boolean onOkClicked(String salida) {
					Log.v(TAG,"nuevo elemento en tabla "+latabla);
					db.insertRegistro(latabla,salida);
					Intent intent = getIntent();
					finish();
					startActivity(intent);
					return true; 
				}
			};
			dlg.show();		
		}  else {
			Log.v(TAG,"añade contenido");
			addContenido();


		}
	}

	private void addContenido() {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(this);

		ArrayList<nombreId> alObjetivos = db.getNombreId("objetivos");
		ArrayList<nombreId> alBloques = db.getNombreId("bloques");

		dialog.setContentView(R.layout.dialog_contenido);
		dialog.setTitle("Añadir contenido");

		final EditText etNombre = (EditText) dialog.findViewById(R.id.etContenido );
		final Spinner objetivos = (Spinner) dialog.findViewById(R.id.spinner2);
		final Spinner bloques = (Spinner) dialog.findViewById(R.id.spinner1);

		final SpinAdapter objetivosAdapter = new SpinAdapter(PopulateTablesActivity.this,
				android.R.layout.simple_spinner_item,
				alObjetivos);	
		objetivos.setAdapter(objetivosAdapter);
		
		final SpinAdapter bloquesAdapter = new SpinAdapter(PopulateTablesActivity.this,
				android.R.layout.simple_spinner_item,
				alBloques);	
		bloques.setAdapter(bloquesAdapter);

		Button b1 = (Button) dialog.findViewById(R.id.bAdd);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				int pos = objetivos.getSelectedItemPosition();
				String myId = objetivosAdapter.getItem(pos).getId();
				String myName = objetivosAdapter.getItem(pos).getNombre();
				String myId2 = bloquesAdapter.getItem(pos).getId();
				String myName2 = bloquesAdapter.getItem(pos).getNombre();
			    String contenido = etNombre.getText().toString();
			    String stemp = "insert into contenidos(contenido, idobjetivo, idbloque) values ( '"+contenido+"','"+myId+"','"+myId2+"')";
			    Log.v(TAG,stemp);
			    db.insertSql(stemp);
				Log.v("TAG", " click en OK "+myId+" "+myName+" "+myId2+" "+myName2+ " : "+contenido);

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


	public void addCriterio(View v) {
		Log.v(TAG,"button criterios ... ");
		Intent i = new Intent(PopulateTablesActivity.this, CriteriosActivity.class);
		startActivity(i);
	}
}
