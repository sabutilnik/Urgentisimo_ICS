package uno.Urgentisimo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jxl.write.WriteException;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

public class alumnoReportDetail extends Fragment {
	protected static final String TAG = "URGENTISIMO : ALUMNO REPORT DETAIL : ";
	private static final int INSERT = 0;
	private static final int UPDATE = 1;
	private static final int ASIGNATURAS = 1;
	private static final int ACTIVIDADES = 2;
	private static final int CRITERIOS = 3;
	
	private static final int YES_NO_DIALOG = 4;
	private static final Integer GRUPOS = 15;
	private Context context;
	private Integer tipo;
	private ArrayList<Map<String, String>> listado;
	

	
	private String elid;
	private LayoutInflater inf;
	private alumnoInfo alumno;
	private View contentView;

	private DataBaseHelper db;

	private LinearLayout llgrupos;


	private ImageView pic;
	private ImageView borrar;
	private ImageView foto;
	private UtilsLib utilidades;
	private ArrayList<ArrayList<String>> reporte;
	private ArrayList<String> fila;
	private String reportefile;
	private WriteExcel guardarExcel;
	private String fecha;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();
		this.utilidades = new UtilsLib(this.context);
		reporte = new ArrayList<ArrayList<String>>();
		fila = new ArrayList<String>();
		reportefile = "/mnt/sdcard/Urgentisimo/reporte.xls";
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		fecha = df.format(c.getTime());
	}

//	@Override
	public alumnoReportDetail(alumnoInfo alumno, Integer eltipo) {
		Log.v(TAG, "init alumnoreport FRAGMENT... ");

		this.alumno = alumno;
		this.tipo = eltipo;
		db = new DataBaseHelper(getActivity());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.alumnos_list_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		
	case R.id.alumnonuevo:
		Log.v(TAG," nuevo ");
		alumno = new alumnoInfo(context,db);
//		refreshAlumno(contentView);

		return false;
    default:
        return super.onOptionsItemSelected(item);
    }

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		Log.v(TAG,String.valueOf(db.gettimestamp()));
		contentView = inflater.inflate(R.layout.alumno_eval_detail, container, false);
		
		if (this.tipo == ACTIVIDADES ) {
			this.refreshAlumno(contentView,alumno.getactividades(),alumno.getevaluaciones(),"actividad","criterio","_id","idactividad");
		}
		if (this.tipo == CRITERIOS) {
			this.refreshAlumno(contentView,alumno.getcriterios(),alumno.getevaluaciones(),"criterio","actividad","_id","idcriterio");
		}
		ImageView impReporte = (ImageView) contentView.findViewById(R.id.reportes);
		impReporte.setClickable(true);
//		impReporte.setBackground(R.drawable.excel2_m);
		impReporte.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(context, "Guardando excel ", Toast.LENGTH_LONG).show();
				alumno.saveExcel(tipo, new HashMap<String, Boolean>(),0,true);
				Toast.makeText(context, "hecho  "+reportefile, Toast.LENGTH_SHORT).show();
			}
		});
		
		Log.v(TAG,String.valueOf(db.gettimestamp()));
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
			 tv.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			 if (logica == 1) {
				 if (item.get("checked")=="1"){
					 tv.setText(item.get(campo));
					 ll.addView(v);
				 }  
			 }
		}
	}
	

	
	private void fillbasiclayouts(LinearLayout parent, final LinearLayout child) {
		child.setVisibility(View.GONE);
		parent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
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
		if (eltipo == GRUPOS) {
			alumno.addGrupo(elid);
			fillLayout(llgrupos,alumno.getgrupos(),1,"grupo");
		}
	}
	private int geticon(String texto) {
		int resp =R.drawable.unchecked_m;
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
	
	private void refreshAlumno(View contentView, ArrayList<Map<String,String>> list1, ArrayList<Map<String,String>> list2, String campo1, String campo2,String campo3, String campo4 ) {
		pic = (ImageView) contentView.findViewById(R.id.alumno_foto);
		this.updatePic(alumno.getdata("pic"));
		fillTextView((TextView)contentView.findViewById(R.id.tvnombre),alumno.getnombre());
		borrar = (ImageView) contentView.findViewById(R.id.borrar);
		ArrayList<Map<String,String>> sublista = new ArrayList<Map<String,String>>();
		LinearLayout lllistado = (LinearLayout)contentView.findViewById(R.id.evaldata);
		lllistado.removeAllViews(); 
		
		reporte.clear();
		fila.add(alumno.getnombre());
		reporte.add(fila);
				
		
		
		for (int i = 0; i<list1.size(); i++) {
			Map<String,String> item1 = list1.get(i);
			if (item1.get("checked").equalsIgnoreCase("0")) {
				continue;
			}
			String cmp = "";
			if (item1.get("show").equalsIgnoreCase("1")) {
				switch (tipo) {
				case ACTIVIDADES:
					sublista = utilidades.mapToList(alumno.getevaluacionesActividad(item1.get("idactividad")),"criterio");
					cmp = "idactividad"; 
					break;
				case CRITERIOS:
					sublista = utilidades.mapToList(alumno.getevaluacionesCriterio(item1.get("idcriterio")),"actividad");
					cmp = "idcriterio";
					break;
				}

				View v = new View(context);
				inf = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inf
						.inflate(R.layout.simple_textview_sublist, lllistado, false);
				
				TextView titulo = (TextView)v.findViewById(R.id.titulo);
				String lanota = alumno.getevaluacionnota(this.tipo,item1.get(cmp));
				titulo.setText(item1.get(campo1)+":"+lanota);
				LinearLayout llsublist = (LinearLayout) v.findViewById(R.id.sublista);
				llsublist.removeAllViews();

				fila = new ArrayList<String>();
				fila.add("");
				fila.add(item1.get(campo1));
				String nta = alumno.getevaluacionnota(this.tipo,item1.get(cmp));
				fila.add(nta);
				reporte.add(fila);
				fila = new ArrayList<String>();
				fila.add("");
				fila.add("");
				fila.add("Evaluación");
				fila.add("TOC");
				reporte.add(fila);
				for (int j = 0; j<sublista.size(); j++) {
					Map<String,String > item2 = sublista.get(j);					
					View llsub = inf.inflate(R.layout.eval_detail_item, null);
						TextView tvtitulo = (TextView) llsub.findViewById(R.id.texto);
						TextView tveval = (TextView) llsub.findViewById(R.id.eval);
						TextView tvtoc = (TextView) llsub.findViewById(R.id.toc);
						
						String eval = item2.get("evaluacion");
						String show = item2.get("show");
						String check = item2.get("check");
						
						
						String toc = item2.get("toc");
						if (eval != null) {
							tveval.setText(item2.get("evaluacion"));
						} else {
							Log.v(TAG," meto 00");
							tveval.setText("00");
							Log.v(TAG," y estos son el check y el show "+check+":"+show);
						}
						
						Log.v(TAG,"Aquí no meto 00  y estos son el check y el show "+check+":"+show);
						tvtitulo.setText(item2.get(campo2));
						if (toc != null) {
							tvtoc.setText(item2.get("toc"));
						} else {
							
							tvtoc.setText("00");
						}
						Log.v(TAG,item1.get(campo1)+":"+item2.get(campo2)+" -> "+item2.get("evaluacion")+"/"+item2.get("toc"));
						fila = new ArrayList<String>();
						fila.add("");
						fila.add(item2.get(campo2));
						fila.add(tveval.getText().toString());
						fila.add(tvtoc.getText().toString());
						reporte.add(fila);
						llsublist.addView(llsub);
//					}
				}
			lllistado.addView(v);	
			}
		}
	}

	public void refreshlayouts() {
		// TODO Auto-generated method stub
		db.gettimestamp();
	}
	
}
