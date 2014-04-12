package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import uno.Urgentisimo.AddActividadActivity.OnReadyListener;
//import uno.Urgentisimo.EditarTabla.Parent;

import java.util.Map;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActividadFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : ACTIVIDAD FRAGMENT : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private Context context;
	private static final int OPT1 = 0;
	private static final int OPT2 = 1;

	private static final int ACTIVIDADESGENERALES=3;
	private static final int TIPOSACTIVIDAD=4;
	private static final int AGRUPAMIENTOS=5;
	private static final Integer GRUPOS = 6;
	private static final Integer CRITERIOS = 7;
	private static final Integer MATERIALES = 8;

	// public ArrayList<actividadInfo> actividades;

	private String idactividad;
	private Map<String, Map<String, String>> actividades;

	private Map<String, String> grupos;
	private Map<String, String> materiales;
	private ArrayList<Map <String, String>> agenerales;
	private ArrayList<Map <String, String>> tactividades;
	private ArrayList<Map <String, String>> arrayagrupamientos;
	private Map<String, Map<String, String>> actividadesgenerales;
	private Map<String, Map<String, String>> tiposactividades;
	private Map<String, Map<String, String>> agrupamientos;
	
	private UtilsLib utilidades;

	private ArrayList<Map<String, String>> criterios;

	private Map<String, Map<String, String>> extrainfo;
	private ExpandableListView mExpandableList;
	private MyCustomAdapter adapter;

	GridView MyGrid;
	private ArrayList<nombreId> listado;
	private View containerView;
	private View contentView;
	private String tabla;
	private String laquery;
	private MenuInflater mi;
	private String ordenar;
	private ArrayList<String> ordenarpor;
	private String subtabla;
	private FragmentTransaction transaction1;
	private Fragment derecha;
	private Fragment empty;
	private Actividad actividad;
	private DataBaseHelper db;
	
	private TextView fhinicio;
	private TextView fhfin;
	private TextView tvactgeneral;
	private TextView tvtactividad;
	private TextView tvagrupamiento;
	private TextView tvgrupos;
	private TextView tvcriterios;
	private TextView tvmateriales;
	private LinearLayout llgrupos;
	private LinearLayout llcriterios;
	private LinearLayout llmateriales;
	private ImageView guardar;

	private LayoutInflater inf;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();
		empty = new EmptyFragment();
		transaction1 = getFragmentManager()
				.beginTransaction();
		cleanFragment();
	}

	private void cleanFragment() {
		transaction1.replace(R.id.fragment_container2, empty);
		transaction1.addToBackStack(null);
		transaction1.commit();
	}

	public ActividadFragment(String idactividad) {
		Log.v(TAG, "init Actividad FRAGMENT... que pex");
		this.idactividad = idactividad;
		this.utilidades = new UtilsLib(getActivity());
		db = new DataBaseHelper(getActivity());
	}
	public ActividadFragment() {
		Log.v(TAG, "init Actividad FRAGMENT... que pex");
		this.idactividad = "nuevo";
		db = new DataBaseHelper(getActivity());
		this.utilidades = new UtilsLib(getActivity());
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.menu_actividades, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.saveActividad:
			actividad.saveActividad(UPDATE);
			break;
		case R.id.borrarActividad:
			actividad.deleteActividad();
			break;
//		case R.id.nuevaActividad:
//			this.nuevaActividad();
//			break;
			
		case R.id.copiarActividad:
			Log.v(TAG," copiar actividad ");
			this.idactividad = actividad.copiarActividad();
			
			break;
		}

		// this.populate();
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);

		contentView = inflater.inflate(R.layout.actividad_fragment, container,
				false);
		this.initdata();
		this.refreshdata();
		
		fhinicio = (TextView) contentView.findViewById(R.id.fhinicio);
		fhfin = (TextView) contentView.findViewById(R.id.fhfin);
		fhinicio.setText(actividad.getdata("fecha_inicio"));
		fhfin.setText(actividad.getdata("fecha_fin"));	
		fhinicio.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				useFragment(1);
			}
		});
		fhfin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				useFragment(2);
				
			}
		});
		getActivity().setTitle("Urgentisimo -> Editar Actividad -> "+actividad.getdata("actividad"));
		
		
		this.fillEditText((EditText)contentView.findViewById(R.id.actividadname),"actividad");
		this.fillEditText((EditText)contentView.findViewById(R.id.descripcion),"descripcion");
		
		
//		EditText actname = (EditText) contentView
//				.findViewById(R.id.actividadname);
//		EditText desc = (EditText) contentView.findViewById(R.id.descripcion);
//
//		
//		
//		
		
		Log.v(TAG, "la actividad " + actividad.getIdactividad());
	
//		String tempo = actividad.getdata("actividad");
//		actname.setText(tempo);
//		desc.setText(actividad.getdata("descripcion"));

		ImageView save = (ImageView) contentView.findViewById(R.id.guarda);

		save.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.v(TAG, "guardando la actividad cerota");

			} 
			
		});
		String tmp = "";		
		LinearLayout llactgen = (LinearLayout)contentView.findViewById(R.id.llactgeneral);
		tvactgeneral = (TextView) contentView.findViewById(R.id.tvactgen);
		if (!idactividad.equalsIgnoreCase("nuevo") && actividadesgenerales.size()>0) {
			tmp = actividadesgenerales.get(actividad.getdata("idactividad_general")).get("actividad_general");
		} 

		tvactgeneral.setText(tmp);
		llactgen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				useFragment(3);
				
			}
		});
		
		LinearLayout lltactividad = (LinearLayout)contentView.findViewById(R.id.lltactividad);
		tvtactividad = (TextView) contentView.findViewById(R.id.tvtactividad);
		tmp = "";
		if (!idactividad.equalsIgnoreCase("nuevo") && tiposactividades.size()>0) {
			actividad.getdata("tipo_actividad");
		}
		tvtactividad.setText(tmp);
		lltactividad.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				useFragment(4);
				
			}
		});
	
		LinearLayout llagrupamiento = (LinearLayout)contentView.findViewById(R.id.llagrupamiento);
		tvagrupamiento = (TextView) contentView.findViewById(R.id.tvagrupamiento);
		tmp = "";
		if (!idactividad.equalsIgnoreCase("nuevo") && agrupamientos.size()>0) {
			tmp = agrupamientos.get(actividad.getdata("idagrupamiento")).get("agrupamiento");
		}
		tvagrupamiento.setText(tmp);
		llagrupamiento.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				useFragment(5);
				
			}
		});
		
		inf = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		llgrupos = (LinearLayout) contentView.findViewById(R.id.gruposlist);
		toggleVisibility(llgrupos);
		fillLayout(llgrupos, actividad.getgrupos(),"grupo",6);
		tvgrupos = (TextView)contentView.findViewById(R.id.tvgrupos);
		tvgrupos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleVisibility(llgrupos);
				useFragment(6);
			}
		});
		
		llcriterios = (LinearLayout) contentView.findViewById(R.id.criterioslist);
		toggleVisibility(llcriterios);
		fillLayout(llcriterios, actividad.getcriterios(),"criterio",7);
		tvcriterios = (TextView)contentView.findViewById(R.id.tvcriterios);
		tvcriterios.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleVisibility(llcriterios);
				useFragment(7);
			}
		});
		
		llmateriales = (LinearLayout) contentView.findViewById(R.id.materialeslist);
		toggleVisibility(llmateriales);
		fillLayout(llmateriales, actividad.getmateriales(),"material",8);
		tvmateriales = (TextView)contentView.findViewById(R.id.tvmateriales);
		tvmateriales.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleVisibility(llmateriales);
				useFragment(8);
			}
		});
		
		guardar = (ImageView) contentView.findViewById(R.id.guarda);
		guardar.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Toast.makeText(context, "Guardando actividad", Toast.LENGTH_LONG).show();
				actividad.saveActividad(UPDATE);
				
			}
		});

		return contentView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();

	}
	
	
	private void fillEditText(final EditText et, final String elcampo) {
		et.setText(actividad.getdata(elcampo));
		et.addTextChangedListener(new TextWatcher() {
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		    }

		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	actividad.setdata(elcampo, et.getText().toString());
		}
		public void afterTextChanged(Editable s) {				
		    	actividad.setdata(elcampo, et.getText().toString());
			}
			});
	}
	
	
	public String saveActividad(Integer tipo) {
		
		return actividad.saveActividad(tipo);
	}

	public class OnReadyListener implements customDialog.ReadyListener {
		private static final String TAG = "OnReadyListener";
		private String elid;

		public OnReadyListener() {
			this.elid = elid;
		}

		public void ready(String texto) {
			Log.v(TAG, "borrar de " + tabla + " : " + texto + " elid " + elid);
			db.borraIdDeTabla(tabla, elid);
			// populate();
		}

		public void ready(nombreId resp) {
			// TODO Auto-generated method stub

		}

	}

	public void ready(nombreId resp) {
		// TODO Auto-generated method stub

	}

	public Drawable geticon(String elicono) {
		Drawable resp = getActivity().getResources().getDrawable(
				R.drawable.act_hecha_m);
		if (elicono.equalsIgnoreCase("00")) {
			resp = getActivity().getResources()
					.getDrawable(R.drawable.act_no_m);
		}
		if (elicono.equalsIgnoreCase("10") || elicono.equalsIgnoreCase("01")) {
			resp = getActivity().getResources().getDrawable(
					R.drawable.act_medio_m);
		}
		return resp;
	}

	private void collapseAll() {
		int count = adapter.getGroupCount();
		for (int k = 0; k < count; k++) {
			mExpandableList.collapseGroup(k);
		}
	}
	
	 private OnReadyListener mExpandableListClicked =  new OnReadyListener() {
		 
		  public boolean onChildClick(ExpandableListView parent, View v,
		    int groupPosition, int childPosition, long id) {
			Log.v(TAG,"entro en el clicl");
		    collapseAll();
//		    
//			  
//		   //get the group header
//		   HeaderInfo headerInfo = deptList.get(groupPosition);
//		   //get the child info
//		   DetailInfo detailInfo =  headerInfo.getProductList().get(childPosition);
//		   //display it or do something with it
//		   Toast.makeText(getBaseContext(), "Clicked on Detail " + headerInfo.getName() 
//		     + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();
		   return false;
		  }
		   
		 };
		 
		 
	private void useFragment(Integer tipo) {
//		Log.v(TAG,"entrando a meter nuevo fragment ... ");
		Fragment frag = new EmptyFragment();
		switch (tipo) {
		case 1:
			frag = new fechaFragment("Fecha de inicio",actividad.getdata("fecha_inicio"));
			break;
		case 2:
			frag = new fechaFragment("Fecha de fin",actividad.getdata("fecha_fin"));
			break;
		case 3:
			frag = new actividadDataFragment("Actividades Generales", 3, agenerales,"actividad_general","actividades_generales");
			break;
		case 4:
			frag = new actividadDataFragment("Tipos de Actividad", 4, tactividades,"tipo_actividad","tipos_actividades");
			break;
		case 5:
			frag = new actividadDataFragment("Agrupamientos", 5, arrayagrupamientos,"agrupamiento","agrupamientos");
			break;
			
		case 6:
			frag = new actividadDataFragment("Grupos", 6, actividad.getgrupos(),"grupo","grupos");
			break;
		case 7:
			frag = new actividadDataFragment("Criterios", 7, actividad.getcriterios(),"criterio","criterios");
			break;
		case 8:
			frag = new actividadDataFragment("Materiales", 8, actividad.getmateriales(),"material","materiales");
			break;
		}
			
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		
		transaction1.replace(R.id.fragment_container2, frag);
		transaction1.addToBackStack(null);
		transaction1.commit();
	
	}
	
	public void updateFragment(String title, String fecha) {
		if (title.equalsIgnoreCase("fecha de inicio")) {
			fhinicio.setText(fecha);
			actividad.setinfo("fecha_inicio", fecha);
		} else {
			fhfin.setText(fecha);
			actividad.setinfo("fecha_fin", fecha);
		}
		Log.v(TAG," update fragment "+fecha);
	}
	
	public void initdata() {
		
		if (idactividad.equalsIgnoreCase("nuevo")) {
			actividad = new Actividad(context);
		} else {
			actividad = new Actividad(context,idactividad);
		}
	}
	
	public void refreshdata() {
		actividades = db.getMap("select * from actividades");
//		actividad = actividades.get(idactividad);	
		actividadesgenerales = db.getMap("select * from actividades_generales");
		tiposactividades = db.getMap("select * from tipos_actividades");
		agrupamientos = db.getMap("select * from agrupamientos");
//
//		if (idactividad.equalsIgnoreCase("nuevo")) {
//			actividad = new Actividad(context);
//		} else {
//			actividad = new Actividad(context,idactividad);
//		}
//		
//		agenerales = utilidades.mergelists(actividadesgenerales, db.getMapList("select * from actividades where _id='"+idactividad+"'"), "_id", "idactividad_general");
		agenerales = db.getMergedMapList("select * from actividades_generales", "select * from actividades where _id ='"+idactividad+"'", "idactividad_general");
		tactividades = db.getMergedMapList("select * from tipos_actividades", "select * from actividades where _id ='"+idactividad+"'", "idtipo_actividad");
		arrayagrupamientos = db.getMergedMapList("select * from agrupamientos", "select * from actividades where _id ='"+idactividad+"'", "idagrupamiento");
	}

//	public void nueva() {
//	
//	}
	
	public void updateData(String titulo, Integer eltipo, String elid) {
		Log.v(TAG,"recibido "+eltipo+ " con id "+elid);
		this.refreshdata();
		switch(eltipo){
		case 3:
			actividad.setinfo("idactividad_general", elid);
			updateMap(agenerales,elid);
			tvactgeneral.setText(actividadesgenerales.get(actividad.getdata("idactividad_general")).get("actividad_general"));
			break;
		case 4:
			actividad.setinfo("idtipo_actividad", elid);
			tvtactividad.setText(tiposactividades.get(actividad.getdata("idtipo_actividad")).get("tipo_actividad"));
			updateMap(tactividades,elid);
			break;
		case 5:
			actividad.setinfo("idagrupamiento", elid);
			tvagrupamiento.setText(agrupamientos.get(actividad.getdata("idagrupamiento")).get("agrupamiento"));
			updateMap(arrayagrupamientos,elid);
			break;
		case 6:
			actividad.addGrupo(elid);
			fillLayout(llgrupos, actividad.getgrupos(),"grupo",6);
			break;
		case 7:
			actividad.addCriterio(elid);
			fillLayout(llcriterios, actividad.getcriterios(),"criterio",7);
			break;
		case 8:
			actividad.addMaterial(elid);
			fillLayout(llmateriales, actividad.getmateriales(),"material",8);
			break;	
		}
	}
	
	public void updateMap(ArrayList<Map<String,String>> elmap, String elid) {
	   for (int i = 0; i<elmap.size(); i++) {
		   Map<String,String> item = elmap.get(i);
		   if (item.get("_id").equalsIgnoreCase(elid)) {
			   item.put("checked", "0");
		   } 
	   }
	   fillLayout(llgrupos, actividad.getgrupos(),"grupo",6);
	   fillLayout(llcriterios, actividad.getcriterios(),"criterio",7);
	   fillLayout(llmateriales, actividad.getmateriales(),"material",8);
	}
	
	public void fillLayout(LinearLayout ll, ArrayList<Map<String, String>> slist, String campo, final Integer eltipo) {
		ll.removeAllViews();
		for (int i = 0; i < slist.size(); i++) {
			if (eltipo == 7) {
//				Log.v(TAG,"metiendo criterios ");
			}
			Map<String,String> elitem = slist.get(i);
			if (elitem.get("checked").equalsIgnoreCase("0")) {
				continue;
			}
			final String itemid = elitem.get("_id");
			View customView = new View(context);
			if (eltipo == 7) {
				customView = inf.inflate(R.layout.text_value_criterio, null);
				final EditText ettoc = (EditText) customView.findViewById(R.id.toc);
				ettoc.setText(elitem.get("toc"));
				ettoc.addTextChangedListener(new TextWatcher() {
			          public void afterTextChanged(Editable s) {
			        	  actividad.setToc(itemid,ettoc.getText().toString());
			          }
			          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			          public void onTextChanged(CharSequence s, int start, int before, int count) {}
			       });
			} else {
				customView = inf.inflate(R.layout.simple_text_item, null);
			}
			if (eltipo == 7) {
				Log.v(TAG,"metiendo criterios "+elitem.get(campo));
			}
			TextView tvtext = (TextView) customView.findViewById(R.id.text);
			tvtext.setText(elitem.get(campo));
			Drawable img = context.getResources().getDrawable( R.drawable.delete_s );
			tvtext.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null );
			customView.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					Log.v(TAG,"remove  "+itemid);
					switch (eltipo) {
					case 6:
						updateMap(actividad.getgrupos(),itemid);
						break;
					case 7:
						updateMap(actividad.getcriterios(),itemid);
						break;
					case 8:
						updateMap(actividad.getmateriales(),itemid);
						break;	
					}
					useFragment(eltipo);
				}
			});
			ll.addView(customView);	
		}
	}
	
	private void toggleVisibility(View v) {
		if (v.getVisibility() == View.GONE) {
			v.setVisibility(View.VISIBLE);
		} else {
			v.setVisibility(View.GONE);
			
		}
	}
}