package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class alumnoDetail extends Fragment {
	protected static final String TAG = "URGENTISIMO : ALUMNO DETAIL FRAGMENT : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private static final int YES_NO_DIALOG = 4;
	private static final Integer GRUPOS = 15;
	private Context context;
	private Integer tipo;
	private ArrayList<Map<String, String>> listado;
	private String elid;
	private LayoutInflater inf;
	private alumnoInfo alumno;
	private Boolean nuevo;
	private String idalumno;
	private View contentView;

	private DataBaseHelper db;

	private LinearLayout llgrupos;


	private ImageView pic;
	private ImageView guardar;
	private ImageView reportes;
	private ImageView borrar;
	private ImageView foto;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();
	}

	public alumnoDetail(String id, Integer tipo,
			ArrayList<Map<String, String>> listado) {
		Log.v(TAG, "init Fecha FRAGMENT... ");
		this.tipo = tipo;
		this.listado = listado;
		this.idalumno = id;
		db = new DataBaseHelper(getActivity());
		alumno = new alumnoInfo(db, id);
	}

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.alumno_detail_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		
	case R.id.alumnonuevo:
		Log.v(TAG," nuevo ");
		alumno = new alumnoInfo(context,db);
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
		this.clearFragment();
		contentView = inflater.inflate(R.layout.alumno_detail, container, false);
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
	
	public void fillLayout(LinearLayout ll, ArrayList<Map<String,String>> listado, Integer logica, final String campo) {
		ll.removeAllViews();
 
		for (int i = 0; i < listado.size(); i++) {
			View v = new View(context);
			
			inf = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf
					.inflate(R.layout.simple_text_item, ll, false);
			 final Map<String,String> item = listado.get(i);
			 TextView tv = (TextView)v.findViewById(R.id.text);
			 
			 if (logica == 1) {
				 if (item.get("checked")=="1"){
					 tv.setText(item.get(campo));
					 ImageView delete = (ImageView)v.findViewById(R.id.borrar);
					 if (item.get("metagrupo").equalsIgnoreCase("0")) {
						 delete.setVisibility(View.VISIBLE);
					 } else {
						 delete.setVisibility(View.GONE);	 
					 }			 
					 delete.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Log.v(TAG," borrando grupo "+item.get(campo));
							alumno.removeGrupo(item.get("_id"));
							fillLayout(llgrupos,alumno.getgrupos(),1,"grupo");
						}
					});
					 
					 
					 ll.addView(v);
				 }  
			 }

			 
			 Log.v(TAG,item.get(campo)+":"+alumno.getdata("clase")+":");
		}
	}
	
	private void fillpermisos() {
		fillpermiso((TextView) contentView.findViewById(R.id.tvcomedor),"comedor");
		fillpermiso((TextView) contentView.findViewById(R.id.tvsoloacasa),"solo_a_casa");
		fillpermiso((TextView) contentView.findViewById(R.id.tvrefuerzo),"refuerzo");
		fillpermiso((TextView) contentView.findViewById(R.id.tvcompensatoria),"compensatoria");
		fillpermiso((TextView)contentView.findViewById(R.id.tvacnee),"acnee");
		fillpermiso((TextView)contentView.findViewById(R.id.tvblog),"permiso_blog");		
		fillpermiso((TextView)contentView.findViewById(R.id.tvl),"extraescolares_l");
		fillpermiso((TextView)contentView.findViewById(R.id.tvm),"extraescolares_m");
		fillpermiso((TextView)contentView.findViewById(R.id.tvx),"extraescolares_x");
		fillpermiso((TextView)contentView.findViewById(R.id.tvj),"extraescolares_j");
		fillpermiso((TextView)contentView.findViewById(R.id.tvv),"extraescolares_v");
	}
	private void fillpadres() {
		fillEditText((EditText)contentView.findViewById(R.id.etpadre),"padre");
		fillEditText((EditText)contentView.findViewById(R.id.etpadretel),"telefono_pad");
		fillEditText((EditText)contentView.findViewById(R.id.etpadremail),"correo_pad");
		
		fillEditText((EditText)contentView.findViewById(R.id.etmadre),"madre");
		fillEditText((EditText)contentView.findViewById(R.id.etmadretel),"telefono_mad");
		fillEditText((EditText)contentView.findViewById(R.id.etmadremail),"correo_mad");
	}
	
	private void fillpermiso(final TextView tv, final String elcampo) {
		tv.setClickable(true);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,geticon(alumno.getdata(elcampo)), 0);
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				Log.v(TAG," le dio a "+elcampo);
				if (alumno.getalumnodata(elcampo).equalsIgnoreCase("0")) {
					alumno.setdata(elcampo, "1");
				} else {
					alumno.setdata(elcampo, "0");
				}
				tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,geticon(alumno.getdata(elcampo)), 0);	
			}
		});
	}
	
	private void fillbasiclayouts(LinearLayout parent, final LinearLayout child) {
		child.setVisibility(View.GONE);
		parent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switchDisplay(child);				
			}
		});
	}
	
	private void fillEditText(final EditText et, final String elcampo) {
		et.setText(alumno.getalumnodata(elcampo));
		et.addTextChangedListener(new TextWatcher() {
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		    }

		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	alumno.setdata(elcampo, et.getText().toString());
		}
		public void afterTextChanged(Editable s) {				
		    	alumno.setdata(elcampo, et.getText().toString());
			}
			});
	}

	public String guardar() {
		if (idalumno == "nuevo") {
			alumno.saveAlumno(INSERT);
		} else {
			alumno.saveAlumno(UPDATE);
		}
		return null;
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
		int resp =R.drawable.delete_s;
		if (texto.equalsIgnoreCase("0")) {
		resp = R.drawable.check_s;
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
		pic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Fragment picturelist = new pictureFragment(alumno
						.getdata("_id"));
				FragmentTransaction transaction1 = getFragmentManager()
						.beginTransaction();
				transaction1.replace(R.id.fragment_container2, picturelist);
				transaction1
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction1.addToBackStack(null);
				transaction1.commit();
			}
		});

		guardar = (ImageView) contentView.findViewById(R.id.guardar);
		guardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				guardar();
			}
		});

		reportes = (ImageView) contentView.findViewById(R.id.reportes);
		reportes.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Fragment report = new alumnoReportFragment(alumno.getdata("_id"));
				FragmentTransaction transaction1 = getFragmentManager()
						.beginTransaction();
				transaction1.replace(R.id.fragment_container, report);
				transaction1
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction1.addToBackStack(null);
				transaction1.commit();
			}
		});
		
		borrar = (ImageView) contentView.findViewById(R.id.borrar);
		borrar.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				borrar();
			}
		});
 
		fillEditText((EditText)contentView.findViewById(R.id.etnombre),"nombre");
		fillEditText((EditText)contentView.findViewById(R.id.etapellido),"apellidos");
		fillEditText((EditText)contentView.findViewById(R.id.etnumclase),"numero_clase");
		fillEditText((EditText)contentView.findViewById(R.id.etdireccion),"direccion");
		fillEditText((EditText)contentView.findViewById(R.id.ettelefono),"telefono_alum");
		fillEditText((EditText)contentView.findViewById(R.id.etemail),"correo_alum");
		fillEditText((EditText)contentView.findViewById(R.id.clase),"clase");
		
		fillbasiclayouts((LinearLayout)contentView.findViewById(R.id.permisos), (LinearLayout)contentView.findViewById(R.id.llpermisos));
		fillbasiclayouts((LinearLayout)contentView.findViewById(R.id.padres), (LinearLayout)contentView.findViewById(R.id.llpadres));
		fillbasiclayouts((LinearLayout)contentView.findViewById(R.id.grupostitle), (LinearLayout)contentView.findViewById(R.id.llgrupos));

		
		fillpermisos();

		fillpadres();


		llgrupos = (LinearLayout)contentView.findViewById(R.id.llgrupos);

		ImageView addgrupo = (ImageView) contentView.findViewById(R.id.addgrupo);
		addgrupo.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				llgrupos.setVisibility(View.VISIBLE);
				Fragment frag = new EmptyFragment();
				frag = new actividadDataFragment("Grupos", GRUPOS, alumno.getgrupos(),"grupo","grupos");
				FragmentTransaction transaction1 = getFragmentManager()
						.beginTransaction();		
				transaction1.replace(R.id.fragment_container2, frag);
				transaction1.addToBackStack(null);
				transaction1.commit();			
			}
		});
		
		fillLayout(llgrupos,alumno.getgrupos(),1,"grupo");
	}
}
