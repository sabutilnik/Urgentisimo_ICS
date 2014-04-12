package uno.Urgentisimo;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditarGruposActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : EDITAR GRUPOS ACTIVITY : ";
	private DataBaseHelper db;
	private gruposInfo grupo;
	private SpinAdapter gruposAdapter;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		db = new DataBaseHelper(this);
		Log.v(TAG, "entrando ...");
		setContentView(R.layout.edita_grupos_act);
		final ArrayList<gruposInfo> GruposList = db.getGruposData();
		
		ArrayList<nombreId> gruposNombreId = new ArrayList<nombreId>();

		for (int i = 0 ; i<GruposList.size(); i++) {
			nombreId grupoItem = new nombreId();
			grupoItem.setDescripcion(GruposList.get(i).getDesscripcion());
			grupoItem.setId(GruposList.get(i).getIdgrupo());
			grupoItem.setNombre(GruposList.get(i).getNombre());
			gruposNombreId.add(grupoItem);
		}
		Spinner GruposSpinner = (Spinner) findViewById(R.id.gruposSpinner);

		GruposSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView parent, View vw, int pos,
					long id) {

				LinearLayout alumnosList = (LinearLayout) findViewById(R.id.listaAlumnosEditaGrupo);
				alumnosList.removeAllViews();
				LayoutInflater ialumnoList = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				grupo = GruposList.get(pos);
				final String idgrupo = grupo.getNombre();
				ArrayList<alumnosEnGrupo> listadoAlumnos = new ArrayList<alumnosEnGrupo>();

				listadoAlumnos = db.getAlumnosYGrupo(grupo);
				for (int j = 0; j < listadoAlumnos.size(); j++) {
					View customView = ialumnoList.inflate(
							R.layout.grupos_select_alumno, null);
					alumnosEnGrupo alumno = listadoAlumnos.get(j);
					final String alumnoid = alumno.getAlumnoid();
					String alnombre = alumno.getNombre();
					String miembro = alumno.getMiembro();
					TextView nom = (TextView) customView
							.findViewById(R.id.alumnoNombre);
					CheckBox ck = (CheckBox) customView
							.findViewById(R.id.ckMiembro);
					if (grupo.isMetagrupo() == true) {
						ck.setVisibility(View.GONE);
					}
//					Log.v(TAG, "rellenando lv " + alumnoid + " : " + alnombre
//							+ " : " + miembro);
					nom.setText(alnombre);
					if (miembro != "0") {
						ck.setChecked(true);
					} else {
						ck.setChecked(false);
					}
					ck.setOnCheckedChangeListener(new OnCheckedChangeListener()
					{
					    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					    {
					        if ( isChecked )
					        {
					        	db.altaAlumnoGrupo(alumnoid, grupo.getIdgrupo());
					        } else {
					        	db.bajaAlumnoGrupo(alumnoid, grupo.getIdgrupo());
					        }
					    }
					});
					alumnosList.addView(customView);

				}

			}

			public void onNothingSelected(AdapterView arg0) {
				// Do nothing..
			}
		});

//		startManagingCursor(GruposList);
		
		gruposAdapter = new SpinAdapter(this, android.R.layout.simple_spinner_item, gruposNombreId );
		
//		
//		String[] from = new String[] { "grupo" };
//		int[] to = new int[] { R.id.grupoRow };
//		SimpleCursorAdapter gruposAdapter = new SimpleCursorAdapter(this,
//				R.layout.spinner_grupo_item, GruposList, from, to);
		GruposSpinner.setAdapter(gruposAdapter);
	}
	
	   @Override    
	   protected void onDestroy() {        
	       super.onDestroy();
	       db.close();
	   }
	   
}
