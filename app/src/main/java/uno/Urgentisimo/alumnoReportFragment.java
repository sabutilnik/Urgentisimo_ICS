package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//import uno.Urgentisimo.EditarTabla.Parent;

public class alumnoReportFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : ALUMNO REPORT FRAGMENT : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private static final int YES_NO_DIALOG = 4;
	private static final int ASIGNATURAS = 1;
	private static final int ACTIVIDADES = 2;
	private static final int CRITERIOS = 3;
	private static final Integer GRUPOS = 15;
	private Context context;
	private Integer tipo;
	private ArrayList<Map<String, String>> listado;

	
	private String elid;
	private LayoutInflater inf;
	private alumnoInfo alumno;
	private View contentView;
	private UtilsLib utilslib;

	private DataBaseHelper db;

	private LinearLayout llgrupos;


	private ImageView pic;
	private ImageView borrar;
	private ImageView foto;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();
		this.utilslib = new UtilsLib(context);
	}

//	@Override
	public alumnoReportFragment(String id, Integer tipo, ArrayList<Map<String, String>> listado) {
		Log.v(TAG, "init alumnoreport FRAGMENT... ");
		this.tipo = tipo;
		this.listado = listado;
//		this.idalumno = id;
		db = new DataBaseHelper(getActivity());
		alumno = new alumnoInfo(db, id);
	}
	public  alumnoReportFragment(String id) {
//		this.clearFragment();
		Log.v(TAG, "init alumnoreport FRAGMENT... ");
		this.tipo = tipo;
		this.listado = new ArrayList<Map<String,String>>();
		this.tipo = 0;
//		this.idalumno = id;
		db = new DataBaseHelper(getActivity());
		alumno = new alumnoInfo(db, id);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.alumnos_list_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.alumnonuevo:
			Log.v(TAG, " nuevo ");
			alumno = new alumnoInfo(context, db);
			refreshAlumno(contentView);

			return false;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		
		contentView = inflater.inflate(R.layout.alumno_report_fragment, container, false);

		this.refreshAlumno(contentView);
		
		return contentView;
	}

	public void clearFragment() {
		Fragment frag = new EmptyFragment();
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		transaction1.replace(R.id.fragment_container2, frag);
		transaction1.addToBackStack(null);
		transaction1.commit();
	}

	public void updatePic(String picfile) {
		alumno.setdata("pic", picfile);
		if (picfile == null) {
			picfile = "";
		}
		File file = new File(picfile);
		if (file.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(picfile, options);
			pic.setImageBitmap(bm);
		} else {
			pic.setImageResource(R.drawable.alumno_foto_1);
		}
	}
	
	public void fillLayout(LinearLayout ll, ArrayList<Map<String,String>> listado1, Integer logica, final String campo) {
		ll.removeAllViews();
//		ArrayList<Map<String,String>> listado = utilslib.ordenarArrayList(listado1, campo);
		for (int i = 0; i < listado1.size(); i++) {
			final Map<String,String> item = listado1.get(i);
			 if (logica == 1) {
				 if (item.get("checked")=="0"){
					 continue;
				 }  
			 } else {
				 if (item.get("checked")=="1"){
					 continue;
				 }  
			 }
			
			View v = new View(context);
			
			inf = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf
					.inflate(R.layout.simple_text_item, ll, false);
			 final TextView tv = (TextView)v.findViewById(R.id.text);
			 tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,geticon(item.get("show")), 0);
			 tv.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (campo.equalsIgnoreCase("asignatura")) {
						alumno.showasignatura(item.get("_id"));
					}
					if (campo.equalsIgnoreCase("criterio")) {
						alumno.showcriterio(item.get("idcriterio"));
						showdetail(CRITERIOS);
					}
					if (campo.equalsIgnoreCase("actividad")) {
						alumno.showactividad(item.get("idactividad"));
						showdetail(ACTIVIDADES);
					}
					tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,geticon(item.get("show")), 0);
				}
			});
			 tv.setText(item.get(campo));
			 ll.addView(v);
		}
	}
	

	
	private void fillbasiclayouts(LinearLayout parent, final LinearLayout child, final Integer tipo) {
		child.setVisibility(View.GONE);
		parent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch(tipo) {
				case ACTIVIDADES:
					showdetail(ACTIVIDADES);
					break;
				case CRITERIOS:
					showdetail(CRITERIOS);
					break;
				}
				switchDisplay(child);				
			}
		});
	}
	
	private void fillTextView(final TextView tv, final String elcampo) {
		tv.setText(alumno.getalumnodata(elcampo));

	}

	public void borrar() {
			customDialog myDialog = new customDialog(context,
				"Borrar " + alumno.getnombre(),
				new OnReadyListener(alumno.getId()), alumno.getId(), YES_NO_DIALOG);
			myDialog.show();
	}
	
	public void switchDisplay(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
		}
	}
	
	public class OnReadyListener implements customDialog.ReadyListener {
		private static final String TAG = "OnReadyListener";
		private String elid;

		public OnReadyListener(String elid) {
			this.elid = elid;
		}

		public void ready(String texto) {
			alumno.delete();
			Fragment derecha = new DetailFragment();
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.fragment_container, derecha);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			transaction.addToBackStack(null);
			transaction.commit();
		}

		public void ready(nombreId resp) {
			// TODO Auto-generated method stub

		}

	}

	public void updateData(String titulo, Integer eltipo, String elid) {
//		Log.v(TAG,"añado gruposs "+elid+" eltipo "+eltipo);
		if (eltipo == GRUPOS) {
//			Log.v(TAG,"añado grupo "+elid);
			alumno.addGrupo(elid);
			fillLayout(llgrupos,alumno.getgrupos(),1,"grupo");
		}
	}
	private int geticon(String texto) {
		int resp =R.drawable.check_s;
		if (texto.equalsIgnoreCase("0")) {
		resp = R.drawable.delete_s;
		}
		if (texto.equalsIgnoreCase("delete")) {
			return R.drawable.delete_m;
			}
		if (texto.equalsIgnoreCase("Evaluación")) {
			return R.drawable.evaluacion_m;
			}
		if (texto.equalsIgnoreCase("Actividades")) {
			return R.drawable.act_hecha_m;
			}
		if (texto.equalsIgnoreCase("Asistencia")) {
			return R.drawable.asistencia_m;
			}
		if (texto.equalsIgnoreCase("Grupos")) {
			return R.drawable.grupo_s;
			}
		if (texto.equalsIgnoreCase("Reportes")) {
			return R.drawable.reportes_m;
			}
		
		
		return resp;
	}
	
	private void refreshAlumno(View contentView) {
		pic = (ImageView) contentView.findViewById(R.id.alumno_foto);
		this.updatePic(alumno.getdata("pic"));
		borrar = (ImageView) contentView.findViewById(R.id.reportes);
		pic.setClickable(true);
		pic.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				showdetail(ASIGNATURAS);
			}
		});
		fillTextView((TextView)contentView.findViewById(R.id.tvnombre),alumno.getnombre());

		fillbasiclayouts((LinearLayout)contentView.findViewById(R.id.asignaturas), (LinearLayout)contentView.findViewById(R.id.llasignaturas),ASIGNATURAS);
		fillLayout((LinearLayout)contentView.findViewById(R.id.llasignaturas), alumno.getasignaturas(),1,"asignatura");
		
		fillbasiclayouts((LinearLayout)contentView.findViewById(R.id.actividades), (LinearLayout)contentView.findViewById(R.id.llactividades),ACTIVIDADES);
		fillLayout((LinearLayout)contentView.findViewById(R.id.llactividades), alumno.getactividades(),1,"actividad");
		
		fillbasiclayouts((LinearLayout)contentView.findViewById(R.id.criterios), (LinearLayout)contentView.findViewById(R.id.llcriterios),CRITERIOS);
		fillLayout((LinearLayout)contentView.findViewById(R.id.llcriterios), alumno.getcriterios(),1,"criterio");
		
	}
	
	
	
	private void showdetail(Integer eltipo) {
		Fragment reportes = new Fragment();
		FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
		switch(eltipo) {
		case ACTIVIDADES:
			reportes = new alumnoReportDetail(alumno,eltipo);
			transaction1 = getFragmentManager().beginTransaction();
			transaction1.replace(R.id.fragment_container2, reportes);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			transaction1.addToBackStack(null);
			transaction1.commit();
			break;
		case CRITERIOS:
			reportes = new alumnoReportDetail(alumno,eltipo);
			transaction1 = getFragmentManager().beginTransaction();
			transaction1.replace(R.id.fragment_container2, reportes);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			transaction1.addToBackStack(null);
			transaction1.commit();
			break;

		
		case ASIGNATURAS:
//			Fragment reportes = new alumnoReportDetail(alumno,ACTIVIDADES);
//			FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
//			transaction1.replace(R.id.fragment_container2, reportes);
//			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//			transaction1.addToBackStack(null);
//			transaction1.commit();
//			break;
		
		
		}
	}
	
}
