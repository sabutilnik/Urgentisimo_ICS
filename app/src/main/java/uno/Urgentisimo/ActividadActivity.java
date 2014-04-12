package uno.Urgentisimo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;



public class ActividadActivity extends Activity {
	protected static final String TAG = "URGENTISIMO : ACTIVIDAD ACTIVITY : ";
	public static final int ONE_FIELD_DIALOG = 1;
	public static final int DATE_DIALOG = 2;
	public static final int INFO_DIALOG = 3;
	private static final int YES_NO_DIALOG = 4;
	private static final int SPINNER_DIALOG = 5;
	private Boolean filter;
	private EditText etFilter;
	private LinearLayout main;

	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private ArrayList<actividadInfo> alActividades;
	private ArrayList<nombreId> alactgeneral;


	public actividadInfo actividad;
	private UtilsLib utils;
	private WidgetsUtils customWidget;


	private DataBaseHelper db;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Bundle extras = getIntent().getExtras(); 
		db = new DataBaseHelper(this);
		utils = new UtilsLib(this);
		customWidget = new WidgetsUtils(this);
		setContentView(R.layout.actividad_act);
		alActividades = new ArrayList<actividadInfo>();
		filter = false;
		etFilter = (EditText)findViewById(R.id.etFilter);
		etFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					filter = true;
					fillActividades();
					return true;
				}
				return false;
			}
		});
		getActividades("2");
		fillActividades();
	}

	public void fillActividades () {
		main = (LinearLayout)findViewById(R.id.sublista);
		main.removeAllViews();
		for (int i = 0; i < alActividades.size(); i++ ){
			//			actividadInfo ac = alActividades.get(i);
			if (utils.matches( alActividades.get(i).getData("actividad"), etFilter, filter) == true ) {
				//				Log.v(TAG,"fillact ..."+i);
				//				main.addView(ac.getView());
				//			}
				TextView tv = new TextView(this);
				final int position = i;
				tv.setText(" + "+alActividades.get(i).getData("actividad"));
				tv.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						main.removeViewAt(position);
						actividadInfo ac = alActividades.get(position);
						main.addView(ac.getView(), position);
					}
				});
				main.addView(tv);
			}
		}	
	}

	public void getActividades(String idactgeneral) {
		alActividades.clear();
//		ArrayList<nombreId> actlist = db.getNombreIdSql("select * from actividades where idactividad_general ='"+idactgeneral+"'", "actividad");
		ArrayList<nombreId> actlist = db.getNombreIdSql("select * from actividades","actividad");
		for (int i = 0; i<actlist.size(); i++ ) {
			actividad = new actividadInfo(this, actlist.get(i).getId());
			alActividades.add(actividad);
		}
	}
	//
	//	public View fillActividad(actividadInfo actividad) {
	//		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//		View customView = customWidget.enhacedTextView(this,actividad.getData("actividad"),1);
	//		
	//		final TableLayout llActividadData = (TableLayout)customView.findViewById(R.id.sublista);
	//		for (int i = 0; i < utils.getActividadesCamposSize(); i++) {
	//			llActividadData.addView( actividad.getDataView(this, utils.getActividadesCamposText(i))  );
	//		}
	////		llActividadData.addView(actividad.getExtendedView(this,"grupos",actividad.grupos ));
	////		llActividadData.addView(actividad.getExtendedView(this,"criterios",actividad.criterios ));
	////		llActividadData.addView(actividad.getExtendedView(this,"materiales",actividad.materiales ));
	//		return customView;
	//	}	
}