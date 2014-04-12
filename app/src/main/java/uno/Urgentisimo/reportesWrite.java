package uno.Urgentisimo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jxl.write.WriteException;



import uno.Urgentisimo.UtilsLib.evalCriterio;
import uno.Urgentisimo.UtilsLib.evalCriterioAlumno;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class reportesWrite extends Fragment {
	protected static final String TAG = "URGENTISIMO : REPORTES : WRITE : ";
	protected static final Integer ASIGNATURAS = 1;
	protected static final Integer ACTIVIDADES = 2;
	protected static final Integer CRITERIOS = 3;
	private Context context;
	private DataBaseHelper db;
	public View contentView;
	public Integer tipo;
	public UtilsLib utilslib;
	private ArrayList<String> ids;
	private ArrayList<Map<String,String>> listado;	
	private LinearLayout lllistado;
    private GridLayout glistado;
	private ArrayList<grupoInfo> grupos;
	private reportFragment izquierda;
    private Map<String,Boolean> losids;
    private TextView titulo;
    private NotificationsFragment notifications;
    private ProgressBar progress;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
		db = new DataBaseHelper(getActivity());	
        this.context = getActivity();
        this.utilslib = new UtilsLib(context);        
        this.listado = db.getMapList("select * from grupos order by grupo");
        grupos = new ArrayList<grupoInfo>();
        for (int i = 0; i < listado.size(); i++) {
        	grupoInfo grupo = new grupoInfo(db,listado.get(i).get("_id"));
        	grupos.add(grupo);
        }       
        izquierda = (reportFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        notifications = (NotificationsFragment) getFragmentManager().findFragmentById(R.id.notifications);
        notifications.hideBotones();
        notifications.showNotificciones();
        this.losids = new HashMap<String, Boolean>();
        refresh();
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		contentView = inflater.inflate(R.layout.reportes_write, container,false);
        titulo = (TextView) contentView.findViewById(R.id.textView1);
		glistado = (GridLayout) contentView.findViewById(R.id.grupos);
		for (int i = 0; i<grupos.size(); i++) {
			final grupoInfo grupo = grupos.get(i);			
			LayoutInflater inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View cv = inf.inflate(R.layout.simple_imageview, null);
			final TextView element = (TextView) cv.findViewById(R.id.texto);
			cv.setOnClickListener(new View.OnClickListener() {					
				public void onClick(View v) {
					ids = izquierda.getids();
					Log.v(TAG,"save excel");
//                    startProgress(grupo);
					guardaExcel(grupo);
					element.setCompoundDrawablesWithIntrinsicBounds(R.drawable.grupo_m, 0, 0, 0);
				}
			});

		    element.setText(grupo.getnombre());		 
		    glistado.addView(cv);
		    element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.excel2_m, 0);
			
		}
		
		return contentView;
	}

	
	private void guardaExcel(grupoInfo grupo) {
		String strfecha = utilslib.getstrfecha();
		ArrayList<ArrayList<String>> reporte = new ArrayList<ArrayList<String>>();
		ArrayList<String> separador = new ArrayList<String>(); separador.add("");separador.add("");separador.add("");
        notifications.mensaje("Generando reporte ...");
		this.ids = izquierda.getids();
        this.losids = izquierda.getlosids();
		for (int i = 0; i < grupo.getalumnos().size(); i++) {
			Map<String,String> alumnoinfo =  grupo.getalumnos().get(i);
			String elid = alumnoinfo.get("_id");
			String checked = alumnoinfo.get("checked");
			if (checked.equalsIgnoreCase("1")) {
				tipo = izquierda.gettipo();
				alumnoInfo alumno = new alumnoInfo(db,elid);
//                notifications.mensaje("Añadiendo alumno "+alumno.getnombre());
				reporte.addAll(alumno.saveExcel(tipo, this.losids,1,false));
				reporte.add(separador);
			}
		}
		String reportefile = utilslib.getdir("reportes")+"reporte_"+grupo.getdata("grupo")+"_"+strfecha+".xls";
		WriteExcel guardarExcel = new WriteExcel(reporte, reportefile);
        notifications.setarchivo(reportefile);
        notifications.mensaje("Reporte guardado en "+reportefile);
		try {
			guardarExcel.write();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	private void fillTextView(TextView tv,final Integer i) {
		tv.setClickable(true);
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tipo = i;
			}
		});		
	}
	 
	public void refresh() {
		this.ids = izquierda.getids();
//		this.tipo = izquierda.gettipo();		
	}

    public void insertId(String id) {
        this.losids.put(id,true);
        notifications.mensaje("añadido " + id + ", total: " + this.losids.size());
        this.titulo.setText("añado el "+id);
    }
    public void deleteId(String id) {
        this.losids.put(id,false);
        notifications.mensaje("borrando "+id+", total: "+this.losids.size());
//        Log.v(TAG,"quitando el "+id);
    }

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "en pausa");
	}
	
	public void settipo(Integer i) {
		this.tipo = i;
	}
	
	public void replaceFragment() {
		this.contentView.setVisibility(View.GONE);
		Fragment report = new studentReportFragment("9");
		FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				.add(R.id.fragment_container, report)
				.show(report)
				    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				    .addToBackStack(null)
				    .commit();
	}

    public void startProgress(final grupoInfo grupo) {
    // do something long
    Runnable runnable = new Runnable() {
//      @Override
      public void run() {
        for (int i = 0; i <= 10; i++) {
          final int value = i;
           guardaExcel(grupo);
          progress.post(new Runnable() {
//            @Override
            public void run() {
              notifications.mensaje("Updating");
              progress.setProgress(value);
            }
          });
        }
      }
    };
    new Thread(runnable).start();
  }
}
