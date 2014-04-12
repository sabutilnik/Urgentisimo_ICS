package uno.Urgentisimo;
import java.io.IOException;
import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import uno.Urgentisimo.DataBaseHelper;
import android.app.AlertDialog;

public class editaGrupoActivity_x extends Activity implements OnLongClickListener,OnClickListener

{
	protected static final String TAG = "URGENTISIMO : GRUPOS ACTIVITY : ";
	private static gruposInfo grupoGlobal = new gruposInfo();
	private DataBaseHelper db;
	
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		db = new DataBaseHelper(this);

		setContentView(R.layout.edita_grupos_act);  
		LinearLayout gruposListado = (LinearLayout)findViewById(R.id.gruposListado);
		LayoutInflater lgruposListado = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		gruposInfo grupo = new gruposInfo();
		ArrayList<gruposInfo> grupos = db.getGruposInfo();

		for (int i = 0; i < grupos.size() ; i++ ) {
			grupo = grupos.get(i);
			grupo.printData();
			final String nombre = grupo.getNombre();
			final String id = grupo.getIdgrupo();
			final String descripcion = grupo.getDesscripcion();
			ArrayList<String[]> actividades = grupo.getActividades();
			ArrayList<String[]> integrantes = grupo.getIntegrantes();

			View customView = lgruposListado.inflate(R.layout.grupo_item, null);
			final LinearLayout datosGrupo = (LinearLayout) customView.findViewById(R.id.datosGrupo);
			datosGrupo.setVisibility(View.GONE);

			final TextView tvGrupoTitulo = (TextView) customView.findViewById(R.id.titleGrupo);		
			tvGrupoTitulo.setText("+ "+nombre);
			tvGrupoTitulo.setClickable(true);
			tvGrupoTitulo.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					String c = String.valueOf(datosGrupo.getVisibility());
					Log.v(TAG,"Expande "+nombre+" con id "+id+" visibility "+c);
					if (datosGrupo.getVisibility() != View.VISIBLE) {
						datosGrupo.setVisibility(View.VISIBLE);
						tvGrupoTitulo.setText("- "+nombre);
					} else {
						datosGrupo.setVisibility(View.GONE);
						tvGrupoTitulo.setText("+ "+nombre);
					}	
				} 
			});	

			final TextView tvMiembrosExpand = (TextView) customView.findViewById(R.id.miembros_text);
			LayoutInflater ilistadoMiembros = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final LinearLayout listadoMiembros = (LinearLayout) customView.findViewById(R.id.listadoMiembros);
			listadoMiembros.setVisibility(View.GONE);

			tvMiembrosExpand.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG,"Expande integrantes " + String.valueOf(listadoMiembros.getVisibility()));
					if (listadoMiembros.getVisibility() != View.VISIBLE) {
						listadoMiembros.setVisibility(View.VISIBLE);
						tvMiembrosExpand.setText("- Integrantes");
					} else {
						listadoMiembros.setVisibility(View.GONE);
						tvMiembrosExpand.setText("+ Integrantes");
					}
				}
			});

			for (int j = 0; j < integrantes.size(); j++) {
				final String elid = integrantes.get(j)[0];
				final String salumno = integrantes.get(j)[1];
				View miembrosView = ilistadoMiembros.inflate(R.layout.miembro_grupo_item, null);
				final TextView alumno = (TextView) miembrosView.findViewById(R.id.nombreMiembro);
				TextView fhalta = (TextView) miembrosView.findViewById(R.id.fh_alta_grupos);
				TextView fhbaja = (TextView) miembrosView.findViewById(R.id.fh_baja_grupos);
				final LinearLayout fechasMiembroGrupo = (LinearLayout) miembrosView.findViewById(R.id.fechasMiembroGrupo);
				alumno.setText("+ "+integrantes.get(j)[1]);
				fhalta.setText("      * fecha alta : "+integrantes.get(j)[2]);
				fhbaja.setText("      * fecha baja : "+integrantes.get(j)[3]);
				fechasMiembroGrupo.setVisibility(View.GONE);

				alumno.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						Log.v(TAG,"Expande "+alumno+" con id "+elid+" visibility ");
						if (fechasMiembroGrupo.getVisibility() != View.VISIBLE) {
							fechasMiembroGrupo.setVisibility(View.VISIBLE);
							alumno.setText("- "+salumno);
						} else {
							fechasMiembroGrupo.setVisibility(View.GONE);
							alumno.setText("+ "+salumno);
						}
					}
				});	
				miembrosView.setTag(elid);
//				alumno.setOnLongClickListener( integrantesListener );
				
				alumno.setOnLongClickListener(new View.OnLongClickListener() {	
					public boolean onLongClick(final View v) {
						TextView nombreMiembro = (TextView) v.findViewById(R.id.nombreMiembro);
//						final String al = nombreMiembro.getText().toString();
						Log.v("CUSTOM CLICK LISTENER","nuevo click en "+salumno);
						Builder alertDialog = new AlertDialog.Builder(editaGrupoActivity_x.this);
						alertDialog.setTitle("Quitar alumno del grupo");
						alertDialog.setMessage("Eliminar al alumno "+salumno+" del grupo "+nombre+"?");
						alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							}
							});
						alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						      public void onClick(DialogInterface dialog, int which) {
						    	  String stemp = "delete from alumnos_grupos where idalumno = '"+elid+"' and idgrupo = '"+id+"'";
						    	  Log.v(TAG,stemp);
						    	  db.delete(stemp);
						    	  v.setVisibility(View.GONE);
						    } });
						alertDialog.show();			
					return true;	
					}
				});
				
				listadoMiembros.addView(miembrosView);
			}	

			final LinearLayout datosActividades = (LinearLayout) customView.findViewById(R.id.datosActividades);
			datosActividades.setVisibility(View.GONE);
			final TextView tvActividades = (TextView) customView.findViewById(R.id.actividadesGrupo);
			tvActividades.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					String c = String.valueOf(datosActividades.getVisibility());
					Log.v(TAG,"Expande "+nombre+" con id "+id+" visibility "+c);
					if (datosActividades.getVisibility() != View.VISIBLE) {
						datosActividades.setVisibility(View.VISIBLE);
						tvActividades.setText("- Actividades");
					} else {
						datosActividades.setVisibility(View.GONE);
						tvActividades.setText("+ Actividades");
					}	
				} 
			});	

			for (int j = 0 ; j < actividades.size() ; j++) {
				final String actividad = actividades.get(j)[1];
				final String idActividad = actividades.get(j)[0];
				final String descActividad = actividades.get(j)[2];
				TextView tv = new TextView(this);
				tv.setText("      * "+actividad+": "+descActividad);
				datosActividades.addView(tv);
			}

			TextView dscripcion = (TextView) customView.findViewById(R.id.dsgrupo);
			dscripcion.setText("+ Descripción: "+descripcion);
			final EditText dscripcionEdit = (EditText) customView.findViewById(R.id.dsgrupoEdit);
			dscripcionEdit.setVisibility(View.GONE);

			dscripcion.setOnLongClickListener(this);
			dscripcion.setOnClickListener(this);
			gruposListado.addView(customView);
		}
	}

	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		int elid = v.getId();
		Log.v("CLICKS ","long click en "+elid);
		return false;
	}
	public void onClick(View v) {
		int elid = v.getId();
		Log.v("CLIDKS","click normal en "+elid);
	}
	public OnLongClickListener integrantesListener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			TextView nombreMiembro = (TextView) v.findViewById(R.id.nombreMiembro);
			final String al = nombreMiembro.getText().toString();
			final Object elid = nombreMiembro.getTag();
			Log.v("CUSTOM CLICK LISTENER","nuevo click en "+al);
			Builder alertDialog = new AlertDialog.Builder(editaGrupoActivity_x.this);
			alertDialog.setTitle("Sacar del grupo a"+al);
			alertDialog.setMessage("en serio?");
			alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				}
				});
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			       Log.v("CUSTOM CLICK","se va a eliminar al alumno "+al+"del grupo "+elid); 
			    } });
			alertDialog.show();			
		return true;	
		}	
	};

}

