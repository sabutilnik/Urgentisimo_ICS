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

public class EditarCriterios extends Activity {
	protected static final String TAG = "URGENTISIMO : EDITAR GRUPOS ACTIVITY : ";
	private DataBaseHelper db;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		db = new DataBaseHelper(this);
		Log.v(TAG, "entrando ...");
		setContentView(R.layout.editar_criterios_act);

		final Cursor CriteriosList = db.getCriterios();
		String[] gruposArray = new String[CriteriosList.getCount()];
		int i = 0;
		if (CriteriosList.moveToFirst()) {
			do {
				gruposArray[i] = CriteriosList.getString(1);
				Log.v(TAG, "criterio " + CriteriosList.getString(1) + " "
						+ CriteriosList.getString(2));
			} while (CriteriosList.moveToNext());
		} else {
			Log.v(TAG, "no criterios");
		}

		Spinner GruposSpinner = (Spinner) findViewById(R.id.criteriosSpinner);

		GruposSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView parent, View vw, int pos,
					long id) {

				LinearLayout alumnosList = (LinearLayout) findViewById(R.id.listaCriterios);

				alumnosList.removeAllViews();

				LayoutInflater ialumnoList = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				CriteriosList.moveToPosition(pos);
				final String idgrupo = CriteriosList.getString(0);
				ArrayList<alumnosEnGrupo> listadoAlumnos = db.getActividadesGrupo(idgrupo);
				

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
					Log.v(TAG, "rellenando lv " + alumnoid + " : " + alnombre
							+ " : " + miembro);
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
					        	db.altaActividadCriterio(alumnoid, idgrupo);
					        } else {
					        	db.bajaActividadCriterio(alumnoid, idgrupo);
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

		startManagingCursor(CriteriosList);
		String[] from = new String[] { "criterio" };
		int[] to = new int[] { R.id.grupoRow };
		SimpleCursorAdapter gruposAdapter = new SimpleCursorAdapter(this,
				R.layout.spinner_grupo_item, CriteriosList, from, to);
		GruposSpinner.setAdapter(gruposAdapter);
	}
	
	   @Override    
	   protected void onDestroy() {        
	       super.onDestroy();
	       db.close();
	   }
	   
}
