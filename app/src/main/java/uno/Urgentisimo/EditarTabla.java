package uno.Urgentisimo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import uno.Urgentisimo.AddActividadActivity.OnReadyListener;
import uno.Urgentisimo.DetailFragment.ImageAdapter;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditarTabla extends Fragment {
	protected static final String TAG = "URGENTISIMO : EDITAR TABLA : ";
	private static final int OPT1 = 0;
	private static final int OPT2 = 1;
	
	
	
	 private static final int SWIPE_MIN_DISTANCE = 120;
	    private static final int SWIPE_MAX_OFF_PATH = 250;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	    private GestureDetector gestureDetector;
	    View.OnTouchListener gestureListener;
	
	
	private ArrayList<String> submenu;
	private Context context;
	
	private String tabla;
	private String elid;
	private String subtabla;
	private String concatena;
	private String nombre;
	private String nametag;
	
	private Map<String, Map<String,String>> datostabla;
	private Map<String,String> elemento;
	
	private ArrayList< Map<String,String>> datossubtabla;
	
	private ArrayList<String> selected;
	private String subquery;
	private String subquery2;
	private String campo;
	
	
	private EditText name;
	private EditText descripcion;
	
	private TextView tvname;
	private TextView tvdesc;
	
	private LinearLayout sublista;
	private ImageView editar;
	private ImageView guardar;
	private ImageView nuevo;
	
	
	
	private View containerView;
	private View contentView;
	
	private UtilsLib utils;
	private int elcolor;
	
	private CustomListAdapter adapter;

	private DataBaseHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.utils = new UtilsLib(getActivity());

		this.datostabla = db.getMap("select * from "+tabla);
		
		
		 gestureDetector = new GestureDetector(new MyGestureDetector());
	        gestureListener = new View.OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                return gestureDetector.onTouchEvent(event);
	            }
	        };
	}

	public EditarTabla() {
		Log.v(TAG, "init editar tabla ");
	}

	public EditarTabla(String tabla, String elid) {
		Log.v(TAG, "init EvalFragment");
		db = new DataBaseHelper(getActivity());
		this.tabla = tabla;
		this.elid = elid;
		this.nombre = "_id";
		if (tabla.equalsIgnoreCase("criterios")) {
			this.nametag = "criterio";
		}
		if (tabla.equalsIgnoreCase("contenidos")) {
			this.nametag = "contenido";
		}

		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.menu_editartabla,	menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int ordenar = item.getItemId();
		Log.v(TAG,"seleccionada opcion"+ordenar);
		// this.populate();
		
		switch(item.getItemId()) {
		
		case R.id.edit_tabla_editar:
			edit();
			break;
		case R.id.edit_tabla_nuevo:
			nuevo();
			break;
			
		
		}
		
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				"Loading. Please wait...", true);
		Long tstamp = db.gettimestamp();
		Log.v(TAG, "empezando ..." + tstamp);
		
		elemento = datostabla.get(elid);
		
		containerView = container;
		contentView = inflater
				.inflate(R.layout.editar_tabla, container, false);
		ListView lv = (ListView) contentView.findViewById(R.id.sublista);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		name = (EditText) contentView.findViewById(R.id.name);
		descripcion = (EditText) contentView.findViewById(R.id.descripcion);
		
		tvname =(TextView) contentView.findViewById(R.id.tvname);
		tvdesc =(TextView) contentView.findViewById(R.id.tvdesc);

		subquery = "select * from criterios order by 2";
		subquery2 = "select * from criterioscontenido where idcontenido = "+elid;
		campo ="idcriterio";
		nombre = "criterio";
		
		if (tabla.equalsIgnoreCase("criterios")) {
			subquery = "select * from contenidos order by 2";
			subquery2 = "select * from criterioscontenido where idcriterio = "+elid;
			campo = "idcontenido";
			nombre = "contenido";
		} 
		
		if (tabla.equalsIgnoreCase("grupos")) {
			subquery = "select * from alumnos order by 2";
			subquery2 = "select * from alumnos_grupos where idgrupo = "+elid;
			campo = "idalumno";
			nombre = "nombre";
		}
		
		name.setText(elemento.get(nametag));
		tvname.setText(elemento.get(nametag));

		descripcion.setText(elemento.get("descripcion"));
		tvdesc.setText(elemento.get("descripcion"));
	
		editar = (ImageView) contentView.findViewById(R.id.editar);
		guardar = (ImageView) contentView.findViewById(R.id.guardar);
		nuevo = (ImageView) contentView.findViewById(R.id.nuevo);
		
		editar.setVisibility(View.GONE);

		editar.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				edit();
			}
		});
		
		guardar.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				save();			
			}
		});
		
		nuevo.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				nuevo();
				
			}
		});
		
		name.setVisibility(View.GONE);
		descripcion.setVisibility(View.GONE);
		
		
//		datossubtabla = db.getMapList(subquery);
		
		datossubtabla = db.getMergedMapList(subquery,subquery2,campo);
		adapter = new CustomListAdapter(context, datossubtabla, nombre);
		
		lv.setAdapter(adapter);
		lv.setItemChecked(1, true);
		lv.setItemChecked(3,true);
		
		
		
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
//			@Override
			public void onItemClick(AdapterView arg0, View view,
                                           int position, long id) {
				// user clicked a list item, make it "selected"
				Log.v(TAG," ss "+position);
			}

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.v(TAG, " long "+arg2);
				return false;
			}
        });
		
		
		
		
		
		pd.cancel();
		
//		contentView.setOnClickListener(this); 
		contentView.setOnTouchListener(gestureListener);
		
		return contentView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	public void populate(String idgrupo) {
		// this.getlistado();


	}

	public void setText(String texto) {
		texto = "eo";
	}

	public class OnReadyListener implements customDialog.ReadyListener {
		private static final String TAG = "OnReadyListener";
		private String elid;

		public OnReadyListener(String elid) {
			this.elid = elid;
		}

		public void ready(String texto) {
			Log.v(TAG, "borrar de " + "1" + " : " + texto + " elid "
					+ elid);
			db.borraIdDeTabla("1", elid);
			// populate();
		}

		public void ready(nombreId resp) {
			// TODO Auto-generated method stub

		}

	}

	public void ready(nombreId resp) {
		// TODO Auto-generated method stub

	}
	
	public String getSublistQuery(String tabla1, String tabla2) {
		String resp = "";
		if (tabla.equalsIgnoreCase("criterios")) {
			resp = "select * from criterioscontenido";
		}
		if (tabla.equalsIgnoreCase("contenidos")) {
			if (tabla2.equalsIgnoreCase("criterios")) {
				resp = "select * from criterioscontenido";
			}
			if (tabla2.equalsIgnoreCase("objetivos")) {
				resp = "select * from contenidosobjetivo";
			}
		}

		return resp;
		
	}
	
	public void save() {
		Log.v(TAG,"le dio a guardar");
		
		elemento.put(nametag, name.getText().toString());
		elemento.put("descripcion", descripcion.getText().toString());
		
		tvname.setText(elemento.get(nametag));
		tvdesc.setText(elemento.get("descripcion"));
		
//		guardar.setVisibility(View.GONE);
		editar.setVisibility(View.VISIBLE);
		name.setVisibility(View.GONE);
		descripcion.setVisibility(View.GONE);
		tvname.setVisibility(View.VISIBLE);
		tvdesc.setVisibility(View.VISIBLE);
		
		guardar();
	}
	
	public void edit() {
		Log.v(TAG,"le dio a editar ");
		guardar.setVisibility(View.VISIBLE);
		editar.setVisibility(View.GONE);
		name.setVisibility(View.VISIBLE);
		descripcion.setVisibility(View.VISIBLE);
		tvname.setVisibility(View.GONE);
		tvdesc.setVisibility(View.GONE);
	}
	
	public void nuevo() {
		Log.v(TAG, "nuevo ");
		elemento.put("_id","");
		elemento.put(nametag, "");
		elemento.put("descripcion","");
		name.setText("");
		descripcion.setText("");
		elid = "9999";

	}
	
	public void guardar() {
		Log.v(TAG,"guardando ");
		String query = "";
		
		if (tabla.equalsIgnoreCase("criterios")) {
			query = "delete from criterioscontenido where idcriterio = "+elid;
			db.insertSql(query);
			if (elid.equalsIgnoreCase("9999")) {
				query= "insert into criterios(criterio,descripcion) valuesu ('"+elemento.get(nametag)  +"','"+elemento.get("descripcion")+"')";
				
				if (db.existe("criterios", "criterio", elemento.get(nametag))) {
					return;
				}
				
			} else {
				query = "update criterios set criterio = '"+elemento.get(nametag)+"', descripcion = '"+elemento.get("descripcion")+"' where _id = "+elid;
			}
			db.insertSql(query);
			elid = db.getString("select _id from criterios where criterio = '"+elemento.get(nametag)+"'");
		}
		if (tabla.equalsIgnoreCase("contenidos")) {

			query = "delete from criterioscontenido where idcontenido = "+elid;
			
			db.insertSql(query);
			if (elid.equalsIgnoreCase("9999")) {
				query= "insert into contenidos(contenido) values ('"+elemento.get(nametag)+"')";
				
				if (db.existe("criterios", "criterio", elemento.get(nametag))) {
					return;
				}
				
			} else {
				query = "update contenidos set contenido = '"+elemento.get(nametag)+"' where _id = "+elid;
			}
			db.insertSql(query);
			elid = db.getString("select _id from contenidos where contenido = '"+elemento.get(nametag)+"'");
		}
		for (int i = 0; i<datossubtabla.size(); i++ ) {
			Map<String, String > temporal = datossubtabla.get(i);
			if (temporal.get("checked").equalsIgnoreCase("1")) {
				Log.v(TAG,temporal.get(nametag)+" con el id "+temporal.get("_id")+" checked");
				if (tabla.equalsIgnoreCase("criterios")) {
					query = "insert into criterioscontenido (idcriterio,idcontenido) values ('"+elid+"','"+temporal.get("_id") +"')";
				}
				if (tabla.equalsIgnoreCase("contenidos")) {
					query = "insert into criterioscontenido (idcontenido,idcriterio) values ('"+elid+"','"+temporal.get("_id") +"')";
				}
				Log.v(TAG," añadiendo datos en tabla  "+tabla+"  laquery : "+query);
				db.insertSql(query);				
			}				
		}	
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) adapter.getItem(position);
		Log.v(TAG,"position "+position+" item "+item);
	}

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(getActivity(), "Left Swipe", Toast.LENGTH_SHORT).show();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(getActivity(), "Right Swipe", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }
}
