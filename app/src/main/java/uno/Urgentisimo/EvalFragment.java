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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EvalFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : EVAL FRAGMENT : ";
	private static final int OPT1 = 0;
	private static final int OPT2 = 1;
	GridView MyGrid;
	private ArrayList<nombreId> listado;
	private ArrayList<nombreId> grupos;
	private Map<String, Map<String, String>> criteriosactividad;
	private View containerView;
	private View contentView;
	private String idactividad;
	private Map<String, String> actividad;
	private String actividadname;
	private String laquery;
	private MenuInflater mi;
	private String ordenar;
	private ArrayList<String> ordenarpor;
	private String subtabla;
	private String fecha;
	private UtilsLib utils;
	private int elcolor;
    private NotificationsFragment notificaciones;
    private String eltag;
    private TextView viewtemp;

	private DataBaseHelper db;

    private TextView seleccionado;
    private String idalumno;
//    private String idactividad;
    private String idcriterio;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.utils = new UtilsLib(getActivity());
        this.notificaciones = (NotificationsFragment) getFragmentManager().findFragmentById(R.id.notifications);

	}

	public EvalFragment() {
		Log.v(TAG, "init EvalFragment");
	}

	public EvalFragment(String idactividad, ArrayList<String> ordenarpor) {
		Log.v(TAG, "init EvalFragmentooo");
		this.idactividad = idactividad;
		this.ordenarpor = ordenarpor;
		ordenar = "";
		fecha = "2012-02-01";
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
		fecha = df.format(c.getTime());
		db = new DataBaseHelper(getActivity());
		// actividadname = db.getDato(idactividad, "actividades", "actividad");
		ArrayList<Map<String, String>> temporal = db
				.getMapList("select * from actividades where _id = '"
						+ idactividad + "'");
		actividad = temporal.get(0);
	}

	private void getgrupos() {
		laquery = "select grupos._id,grupos.grupo,actividades.actividad from actividades_grupos join grupos on actividades_grupos.idgrupo = grupos._id  join actividades on actividades_grupos.idactividad = actividades._id where idactividad = "
				+ idactividad;
		grupos = db.getNombreIdDescSql(laquery);
		return;
	}

	private void getlistado() {
		return;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		for (int i = 0; i < ordenarpor.size(); i++) {
//			menu.add(0, i, i, ordenarpor.get(i));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
		ordenar = ordenarpor.get(item.getItemId());
		} catch(Exception e) {
			Log.v(TAG,"eooo raro");
		}
		// this.populate();
		return false;    
	}
	
	
	
	

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				"Loading. Please wait...", true);
		Long tstamp = db.gettimestamp();

        notificaciones.hideNotificaciones();
        notificaciones.showBotones();
        notificaciones.initBotones();

		Log.v(TAG, "empezando ..." + tstamp);
		containerView = container;
		contentView = inflater
				.inflate(R.layout.eval_fragment, container, false);
		TextView actname = (TextView) contentView
				.findViewById(R.id.actividadname);
		actname.setText(actividad.get("actividad"));
		TextView temp = (TextView) contentView.findViewById(R.id.descripcion);
		temp.setText(actividad.get("descripcion"));
		temp = (TextView) contentView.findViewById(R.id.fhinicio);
		temp.setText(actividad.get("fecha_inicio"));
		temp = (TextView) contentView.findViewById(R.id.fhfin);
		temp.setText(actividad.get("fecha_fin"));

		ImageView editar = (ImageView)contentView.findViewById(R.id.editar);
		
		editar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				Fragment editartabla = new ActividadFragment(idactividad);
				FragmentTransaction transaction1 = getFragmentManager()
						.beginTransaction();
				transaction1.setCustomAnimations(R.anim.left_to_right, R.anim.right_to_left);
				transaction1.replace(R.id.fragment_container,
						editartabla);
				transaction1
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction1.addToBackStack(null);
				transaction1.commit();
			}
		});
		
		
		LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.lista);
		getgrupos();
		final LayoutInflater inf = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < grupos.size(); i++) {
			final nombreId grupo = grupos.get(i);
			final ArrayList<nombreId> listado = db.getAlumnosGrupo(grupo
					.getId());
			final View customView = inf.inflate(R.layout.eval_fragment_item, null);
			TextView grp = (TextView) customView.findViewById(R.id.texto);
			grp.setText(grupo.getNombre());

			LinearLayout title = (LinearLayout) customView
					.findViewById(R.id.title);

			ImageButton save = (ImageButton) customView
					.findViewById(R.id.showlist);

			final LinearLayout alumnos = (LinearLayout) customView
					.findViewById(R.id.sublista);
			alumnos.setVisibility(View.GONE);

			title.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					alumnos.setVisibility(utils.toggleVisibility(alumnos
							.getVisibility()));
					if (alumnos.getChildCount() < 2) {

						TextView loading = (TextView) customView.findViewById(R.id.loading);
						loading.setVisibility(View.VISIBLE);
						for (int j = 0; j < listado.size(); j++) {
							final nombreId alumno = listado.get(j);
							LinearLayout alumnoslist = (LinearLayout) alumnos
									.findViewById(R.id.sublista);
							View v1 = inf.inflate(R.layout.eval_subitem, null);

                            final ImageView ivnota = (ImageView) v1.findViewById(R.id.ivnota);

							final TextView item = (TextView) v1
									.findViewById(R.id.itemName);
							
							final alumnoEvalInfo alumnoEval = new alumnoEvalInfo(
									getActivity(), listado.get(j).getId(),
									idactividad, listado.get(j).getNombre());
							
							item.setText(alumnoEval.getNombre()+", "+alumno.getDescripcion());
							
							
							final TextView lamedia = (TextView) v1.findViewById(R.id.media);
							lamedia.setText(String.valueOf(alumnoEval.getMedia()));
							if (alumnoEval.getMedia()<51 ) {
								lamedia.setTextColor(Color.RED);
								lamedia.setText(String.valueOf(alumnoEval.getMedia()));
							}

                            ivnota.setBackgroundResource(geticono((int) alumnoEval.getMedia()));

							LinearLayout lvcriteriosalumno = (LinearLayout) v1
									.findViewById(R.id.sublista);
							Iterator ito = alumnoEval.getCriterios().entrySet()
									.iterator();


							for (Map.Entry entry: alumnoEval.getCriterios().entrySet()){
								
								final Map<String,String> criterio = (Map<String,String>) entry.getValue();

								View critevalalumno = inf.inflate(
										R.layout.eval_criterio_item, null);
								TextView crit = (TextView) critevalalumno
										.findViewById(R.id.tvCriterio);
								final EditText eval = (EditText) critevalalumno
										.findViewById(R.id.etEvaluacion);
								final EditText toc = (EditText) critevalalumno
										.findViewById(R.id.etToc);

								final String critid = (String)entry.getKey();
								final String criteval = criterio.get("evaluacion").toString();
								final String eltoc = criterio.get("toc").toString();
								final String nombre = criterio.get("criterio");

                                final TextView tvtemp = (TextView) critevalalumno.findViewById(R.id.tvEval);

								crit.setText(nombre);
								eval.setText(criteval);
								elcolor = eval.getCurrentTextColor();
								
								if (aprobado(criteval,eltoc,"75")) {
									eval.setTextColor(elcolor);
								} else { 
									eval.setTextColor(Color.RED);

								}

								toc.setText(eltoc);
								lvcriteriosalumno.addView(critevalalumno);			
								eval.setOnFocusChangeListener(new View.OnFocusChangeListener() {

									public void onFocusChange(View v2, boolean hasFocus) {
										String critevaluacion =  criterio.get("evaluacion").toString();

										String critido = criterio.get("_id").toString();

										String newEval = eval.getText().toString();
										if (!eval.getText().toString().equalsIgnoreCase(critevaluacion)) {
											Log.v(TAG,"eval cambiado "+critevaluacion+" a "+ newEval + "criterio "+critido+" antes era  "+critid+" y tenia "+criteval);
											Log.v(TAG,"voy a meter evaluacion "+newEval+" en el criterio "+critido );
											alumnoEval.setEvalCriterio(critid, newEval, fecha);
											if (aprobado(newEval,criterio.get("toc"),"75")) {
//												eval.setTextColor("0xffbdbdbd");
												eval.setTextColor(elcolor);
											} else {
												eval.setTextColor(Color.RED);

											}
											if (alumnoEval.getMedia()<51 ) {
												lamedia.setTextColor(Color.RED);
											}
										}

									}
								});
                                eval.setVisibility(View.GONE);
                                tvtemp.setText(criterio.get("evaluacion"));
                                critevalalumno.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        seleccionado = tvtemp;
                                        idcriterio = criterio.get("_id");
                                        idalumno = alumno.getId();
                                        eltag = idalumno+","+idactividad+","+idcriterio;
                                        Log.v(TAG,"seleccionado "+idalumno+" "+idcriterio+" "+idactividad);
                                        viewtemp = tvtemp;
                                    }
                                });
								tvtemp.setTag(idalumno+","+idactividad+","+idcriterio);
                                viewtemp = tvtemp;
							}

							alumnos.addView(v1);
						}
						Log.v(TAG,"termino ..."+db.gettimestamp());
						loading.setVisibility(View.GONE);
					}

				}
			});
			ll.addView(customView);
		}

		Long fin = db.gettimestamp();

		Log.v(TAG, "termino..." + fin);
		Log.v(TAG, fin - tstamp + " seconds ");
		pd.cancel();
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
		texto = idactividad;
	}

	

	public class OnReadyListener implements customDialog.ReadyListener {
		private static final String TAG = "OnReadyListener";
		private String elid;

		public OnReadyListener(String elid) {
			this.elid = elid;
		}

		public void ready(String texto) {
			Log.v(TAG, "borrar de " + idactividad + " : " + texto + " elid "
					+ elid);
			db.borraIdDeTabla(idactividad, elid);
			// populate();
		}

		public void ready(nombreId resp) {
			// TODO Auto-generated method stub

		}

	}

	public void ready(nombreId resp) {
		// TODO Auto-generated method stub

	}
	
	public Boolean aprobado(String nota, String toc, String pct) {
		Boolean resp = false;
		if (nota.equalsIgnoreCase("")) {
			nota = "1";
		}
		if (toc.equalsIgnoreCase("")){
			toc = "1";
		}
		if (Integer.valueOf(nota)*100/Integer.valueOf(toc) > Integer.valueOf(pct)) {
			resp = true;
		}
		
		return resp;
		
		
	}

    public int geticono(int media) {
        int elicono = R.drawable.uno;
        if (media > 10) {
            elicono = R.drawable.dos;
        }
        if (media > 20) {
            elicono = R.drawable.tres;
        }
        if (media > 30) {
            elicono = R.drawable.cuatro;
        }
        if (media > 40) {
            elicono = R.drawable.cinco;
        }
        return elicono;
    }
    public void send(int val) {
        int a = 1;
        Log.v(TAG,"recibido "+val);
//        seleccionado.setText(val);
//        TextView temporal = (TextView) contentView.findViewWithTag(eltag);
//        temporal.setText(val);
        viewtemp.setText(val);

    }

}
