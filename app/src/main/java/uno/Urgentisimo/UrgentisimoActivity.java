package uno.Urgentisimo;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class UrgentisimoActivity extends Activity {
	private static final String TAG = "URGENTISIMO : MAIN ";
	public static final int ONE_FIELD_DIALOG = 1;
	public static final int DATE_DIALOG = 2;
	public static final int INFO_DIALOG = 3;

	/** Called when the activity is first created. */
	@Override
	
	
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ImageButton b = (ImageButton) findViewById(R.id.ButtonAlumnos);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(UrgentisimoActivity.this, studentsActivity.class);
				startActivity(i);
			} 
		});

		ImageButton gr = (ImageButton) findViewById(R.id.ButtonGrupo);
		gr.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v(TAG,"button Grupo ... ");
				Intent i = new Intent(UrgentisimoActivity.this, GruposActivity.class);
				startActivity(i);	
			} 
		});
		
		ImageButton cr = (ImageButton) findViewById( R.id.ButtonProgramacion); 
		cr.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v(TAG,"button programacion ... ");
				Intent i = new Intent(UrgentisimoActivity.this, ProgramacionActivity.class);
				startActivity(i);	
			} 
		});
		
		
		
		DataBaseHelper db = new DataBaseHelper(this);
		try {
			db.createDataBase();
			db.upgradeDB();
		} catch (IOException e) {
			Log.v(TAG,"Unable to create db");
		}
		
		ArrayList<nombreId> actividadesHoy = db.getActividadesHoy();
		LinearLayout activity_list = (LinearLayout) findViewById(R.id.today_activity_list);
		LayoutInflater iActivityList = (LayoutInflater) getSystemService( this.LAYOUT_INFLATER_SERVICE);
		
		if (actividadesHoy.size() > 0  )
		{
			for ( int i = 0 ; i < actividadesHoy.size() ; i++   )  {
				View customView = iActivityList.inflate(R.layout.actividad_main_item, null);
				TextView tv = (TextView) customView.findViewById(R.id.texto);
				final nombreId act = actividadesHoy.get(i);
				tv.setText((Html.fromHtml("<b>" + act.getNombre()) + "</b>"));
				tv.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						customDialog descripcion = new customDialog(
								UrgentisimoActivity.this, 
								act.getNombre(),act.getDescripcion() , INFO_DIALOG);
						descripcion.show();		
					}
				});
//				Log.v(TAG," act hoy :"+act.getNombre());
				activity_list.addView(customView);
			}
		}        
		else {
			TextView tv = new TextView(this);
			tv.setText("no hay nada programado para hoy");
			activity_list.addView(tv);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.reset_database:
			try {
				resetDatabase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.backup_database:
			try {
				backup_database();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void backup_database() throws IOException {
		// TODO Auto-generated method stub
		DataBaseHelper db = new DataBaseHelper(this);
		db.backupDatabase();

	}
	private void resetDatabase() throws IOException {
		// TODO Auto-generated method stub
		DataBaseHelper db = new DataBaseHelper(this);
		db.resetDatabase();
	}

	public void addAlumno(View v) {
		DataBaseHelper db = new DataBaseHelper(this);
		String elid = db.crea_alumno();
		db.close();
		Intent in = new Intent(UrgentisimoActivity.this, StudentShowActivity.class);
		Log.v(TAG,"intent :"+elid);
		in.putExtra("idalumno", elid);
		startActivity(in); 	
	}
	
	public void newActividad(View v) {
		Intent in = new Intent(this, AddActividadActivity.class);
		in.putExtra("action", "new");
		startActivity(in);
	}
	
	public void addActividad(View v) {
		Intent in = new Intent(this, AddActividadActivity.class);
		in.putExtra("action", "show");
		startActivity(in);
	}
	public void showActividades(View v) {
		Intent in = new Intent(this, ActividadActivity.class);
		in.putExtra("action", "show");
		startActivity(in);
	}
	public void evaluarActividades (View v) {
//		Intent in = new Intent(this, EvalAlumnosActivity.class);
//		startActivity(in);
	}


}

