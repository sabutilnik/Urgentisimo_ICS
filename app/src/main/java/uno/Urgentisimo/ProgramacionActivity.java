package uno.Urgentisimo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import uno.Urgentisimo.AddObjetivoActivity.OnReadyListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProgramacionActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : PROGRAMACION ACTIVITY : ";
	public static final int ONE_FIELD_DIALOG = 1;
	public static final int DATE_DIALOG = 2;
	//	private DatosAdapter objetivosAdapter;
	private ListView lv;
	private DataBaseHelper db;
	private String idasignatura;
	private String asignaturaText;
	private static HashMap<Integer, String> tablasData;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		db = new DataBaseHelper(this);
		tablasData = new HashMap<Integer, String>();
		tablasData.put(0,"objetivos");
		tablasData.put(1,"contenidos");
		tablasData.put(2,"criterios");
		tablasData.put(3,"actividades");
		Log.v(TAG, "");
		setContentView(R.layout.programacion_activity);
		fillAsignatura(this);
	}

	@Override    
	protected void onRestart() {        
		super.onRestart();
		this.fillAsignatura(this);
	}

	private void fillAsignatura(Context context) {
		ArrayList<nombreId> alAsignaturas = db.getNombreId("asignaturas");
		final Spinner asignaturas = (Spinner) findViewById(R.id.spinner1);
		final SpinAdapter objetivosAdapter = new SpinAdapter(ProgramacionActivity.this,
				android.R.layout.simple_spinner_item,
				alAsignaturas);	
		asignaturas.setAdapter(objetivosAdapter);
		asignaturas.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View vw,
					int position, long id) {
				String asignatura = objetivosAdapter.getItem(position).getNombre();
				String elid = objetivosAdapter.getItem(position).getId();
				idasignatura = elid;
				asignaturaText = asignatura;
				Log.v(TAG,"CLICK EN "+asignatura+":"+elid);
				fillObjetivos(elid);
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void fillObjetivos(String idasignatura) {
		String stemp = "select * from objetivos where _id in (select idobjetivo from objetivosasignatura where idasignatura = '"+idasignatura+"') order by 2";
		Log.v(TAG,"fillObjetivos "+stemp);
		ArrayList<nombreId> objetivos = db.getNombreIdSql(stemp,"objetivo");
		LinearLayout sublista = fillBranch ( findViewById(android.R.id.content), objetivos, "objetivos" );
	}

	public LinearLayout fillBranch ( View v1, final ArrayList<nombreId> listado, final String tabla ) {
		LinearLayout lista = (LinearLayout) v1.findViewById(R.id.sublista);
		lista.removeAllViews();
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0 ; i < listado.size(); i++){
			final int j = i;
			View customView = inflater.inflate(R.layout.objetivo_progra_item, null);
			final TextView tv1 = (TextView) customView.findViewById(R.id.texto);
			final String texto = listado.get(i).getNombre();
			final String desc = listado.get(i).getDescripcion();
			if (tabla.equalsIgnoreCase("actividades")) {
				tv1.setText(texto);
			} else {
				tv1.setText(" + "+texto);
			}
			String stemp = new String();
			ArrayList<nombreId> elementos = new ArrayList<nombreId>();
			String subelementos = new String();

			if (tabla.equals("objetivos")) {
				stemp = "select * from contenidos where _id in (select idcontenido from contenidosobjetivo where idobjetivo = '"+ listado.get(i).getId() +"')";
				Log.v(TAG,"ahora contenidos "+stemp);
				elementos = db.getNombreIdSql(stemp, "contenido");
				setColors(customView,i);
				subelementos = "contenidos";
			} 
			if (tabla.equals("contenidos")) {
				stemp = "select * from criterios where _id in (select idcriterio from criterioscontenido where idcontenido = '"+ listado.get(i).getId() +"')";
				Log.v(TAG,"ahora contenidos "+stemp);
				elementos = db.getNombreIdSql(stemp, "criterio");
				subelementos = "criterios";
			} 
			if (tabla.equals("criterios")) {
				stemp = "select * from actividades where _id in (select idactividad from criterios_actividad  where idcriterio = '"+ listado.get(i).getId() +"')";
				Log.v(TAG,"ahora criterios "+stemp);
				elementos = db.getNombreIdSql(stemp, "actividad");
				subelementos = "actividades";
			}

			final LinearLayout sublista = fillBranch(customView, elementos, subelementos);
			tv1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (tabla.equalsIgnoreCase("actividades")) {
						tv1.setText(texto);
						tv1.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								AlertDialog alertDialog = new AlertDialog.Builder(ProgramacionActivity.this).create();
								alertDialog.setMessage(desc);
								alertDialog.show();
							}
						});
					} else {
						if (sublista.getVisibility() == View.VISIBLE ) {
							sublista.setVisibility(View.GONE);
							tv1.setText(" + "+texto);
						} else {
							sublista.setVisibility(View.VISIBLE);
							tv1.setText(" - "+texto);
						}
						if (tabla.equalsIgnoreCase("actividades")) {
							tv1.setText(texto);
							tv1.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									AlertDialog alertDialog = new AlertDialog.Builder(ProgramacionActivity.this).create();
									alertDialog.setMessage(desc);
									alertDialog.show();
								}
							});
						}
					}
				}
			});

			final ImageView iv = (ImageView) customView.findViewById(R.id.editItem);
			iv.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					editField(tabla, listado.get(j));
				}
			});

			final ImageView delIv = (ImageView) customView.findViewById(R.id.delItem);
			delIv.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					delete(tabla, listado.get(j));
				}
			});
			lista.addView(customView);
		}
		return lista;
	}

	public LinearLayout fillObjetivos ( View v1, ArrayList<nombreId> contenidos, String tabla ) {
		LinearLayout lista = (LinearLayout) v1.findViewById(R.id.sublista);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0 ; i < contenidos.size(); i++){
			View customView = inflater.inflate(R.layout.objetivo_progra_item, null);
			TextView tv1 = (TextView) customView.findViewById(R.id.texto);
			tv1.setText(contenidos.get(i).getNombre());
			Log.v(TAG,"... "+contenidos.get(i).getNombre());
			lista.addView(customView);
		}
		return lista;
	}

	public void addObjetivo(View v) {
		Intent i = new Intent(ProgramacionActivity.this, AddObjetivoActivity.class);
		i.putExtra("idasignatura", idasignatura);
		i.putExtra("asignatura", asignaturaText);
		startActivity(i);	

	}
	public void addAsignatura (View v) {
		customDialog myDialog = new customDialog(this, "Añadir asignatura", "Asignatura :", db.getNombreId("asignaturas"), new OnReadyListener(),ONE_FIELD_DIALOG);
        myDialog.show();
	}

	  private class OnReadyListener implements customDialog.ReadyListener {
	        public void ready(String texto) {
	            Toast.makeText(ProgramacionActivity.this, "añadiendo :"+texto, Toast.LENGTH_LONG).show();
	            db.insertRegistro("asignaturas", texto);
	            fillAsignatura(null);
	        }

			public void ready(nombreId resp) {
				// TODO Auto-generated method stub
				
			}
	    }

	public void setColors(View v, int position) { 

		if (position % 2 == 0) {
			v.setBackgroundColor(0xABEEEEEE );
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
	public void delete (String tabla, nombreId campo) {
		String elid = campo.getId();
		db.borraIdDeTabla(tabla, elid);
		this.fillAsignatura(this);
	}

	public void toggleView (View v, String text, TextView tv) {
		LinearLayout v1 = (LinearLayout) v.getParent();
		View v2 = v1.findViewById(R.id.sublista);
		Log.v(TAG,"entrando ... ");
		if (v2.getVisibility() == View.VISIBLE) {
			v2.setVisibility(View.GONE);
			Log.v(TAG," visible "+text);
			tv.setText(" + "+ text);
		}else {
			v2.setVisibility(View.VISIBLE);
			tv.setText(" - "+ text);
		}
	}

	private void editField(String tabla, nombreId registro) {
		Log.v(TAG,"edita  "+ tabla);
		int caso = 0;
		Intent in = new Intent();
		if (tabla.equals("objetivos")) { caso = 0; }
		if (tabla.equals("contenidos")) { caso = 1; }
		if (tabla.equals("criterios")) { caso = 2; }
		if (tabla.equals("actividades")) { caso = 3; }

		switch (caso) {
		case 0:
			in = new Intent(ProgramacionActivity.this, AddObjetivoActivity.class);
			in.putExtra("idobjetivo", registro.getId());
			in.putExtra("objetivo", registro.getNombre() );
			break;
		case 1:
			in = new Intent(ProgramacionActivity.this, AddContenidoActivity.class);
			in.putExtra("idcontenido", registro.getId());
			in.putExtra("contenido", registro.getNombre() );
			break;
		case 2:
			in = new Intent(ProgramacionActivity.this, AddCriteriosActivity.class);
			in.putExtra("idcriterio", registro.getId());
			in.putExtra("criterio", registro.getNombre() );
			in.putExtra("descripcion", registro.getDescripcion());
			registro.printData();
			break;
		case 3:
			in = new Intent(ProgramacionActivity.this, AddActividadActivity.class);
			in.putExtra("idactividad", registro.getId());
			in.putExtra("action", "edit");
			break;
		}
		startActivity(in);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.programacion_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Log.v(TAG," id "+item.getItemId());
		Intent i = new Intent();
		switch (item.getItemId()) {
		case R.id.addAsignaturaMenu :
			addAsignatura(null);
			return true;
		case R.id.addObjetivoMenu:
			addObjetivo(null);
			return true;
		case R.id.addActividadMenu:
			Log.v(TAG,"add actividad");
			i = new Intent(this, AddActividadActivity.class);
			i.putExtra("action", "new");
			startActivity(i);
			return true;
			
		case R.id.addContenidoMenu:
			i = new Intent(this, AddContenidoActivity.class);
			startActivity(i);	
			return true;
		case R.id.showActividadesMenu:
			i = new Intent(this, AddActividadActivity.class);
			i.putExtra("action", "show");
			startActivity(i);
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	
}

