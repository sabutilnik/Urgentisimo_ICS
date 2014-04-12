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

public class GruposAlumnoActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : EDITAR GRUPOS ACTIVITY : ";
	private DataBaseHelper db;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Bundle extras = getIntent().getExtras(); 
		final String idalumno = extras.getString("idalumno");
		final String elnombre = extras.getString("elnombre");


		db = new DataBaseHelper(this);
		Log.v(TAG, "entrando ...");
		setContentView(R.layout.edita_grupos_act);
		
		TextView alumno = (TextView) findViewById(R.id.eltexto);
		alumno.setText(elnombre);

		final ArrayList<alumnosEnGrupo> listadoGrupos= db.getGruposAlumno(idalumno);
		LinearLayout gruposAlumno = (LinearLayout) findViewById(R.id.listaAlumnosEditaGrupo);

		LayoutInflater iGrupoList = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int j = 0; j < listadoGrupos.size(); j++) {
			View customView = iGrupoList.inflate(
					R.layout.grupos_select_alumno, null);
			alumnosEnGrupo grupo = listadoGrupos.get(j);
			final String idgrupo = grupo.getAlumnoid();
			String alnombre = grupo.getNombre();
			String miembro = grupo.getMiembro();
			TextView nom = (TextView) customView
					.findViewById(R.id.alumnoNombre);
			CheckBox ck = (CheckBox) customView
					.findViewById(R.id.ckMiembro);
			Log.v(TAG, "rellenando lv " + idgrupo + " : " + alnombre
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
						db.altaAlumnoGrupo(idalumno, idgrupo);
					} else {
						db.bajaAlumnoGrupo(idalumno, idgrupo);
					}
				}
			});
			gruposAlumno.addView(customView);

		}
	}

	@Override    
	protected void onDestroy() {        
		super.onDestroy();
		db.close();
	}

}
