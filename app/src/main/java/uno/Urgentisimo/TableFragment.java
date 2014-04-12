package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;

import uno.Urgentisimo.AddActividadActivity.OnReadyListener;

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

public class TableFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : TABLE FRAGMENT : ";
	private static final int OPT1 = 0;
	private static final int OPT2 = 1;
	GridView MyGrid;
	private ArrayList<nombreId> listado;
	private ArrayList<nombreId> filtered;
	private View containerView;
	private View contentView;
	private String tabla;
	private String laquery;
	private String laquery2;
	private MenuInflater mi;
	private int ordenar;
	private ArrayList<String> ordenarpor;
	private String subtabla;
	private String filtro;
	private EditText etfilter;
	private ImageAdapter ItemAdapter;
	private String texto1;
	private String texto2;
	private Integer tablatipo;
	
	private TextView tvtexto2;

	private DataBaseHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Fragment empty = new EmptyFragment();
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		transaction1.replace(R.id.fragment_container2, empty);
		transaction1.addToBackStack(null);
		transaction1.commit();

	}

	public TableFragment() {
		Log.v(TAG, "init TablaFragment");

	}

	public TableFragment(String tabla, String subtabla,
			ArrayList<String> ordenarpor,String texto1) {
		Log.v(TAG, "init TablaFragment");

		this.tabla = tabla;
		this.subtabla = subtabla;
		this.ordenarpor = ordenarpor;
		this.filtro = "";
		ordenar = 0;
		this.texto1 = texto1;
		this.texto2 = this.getordenarpor();
		db = new DataBaseHelper(getActivity());
		if (tabla.equalsIgnoreCase("criterios")) {
			this.tablatipo=2;
		}
		if (tabla.equalsIgnoreCase("contenidos")) {
			this.tablatipo = 3;
		}
		if (tabla.equalsIgnoreCase("grupos")) {
			this.tablatipo = 1;
		}
	}

	private String getordenarpor() {
		String resp  = ordenarpor.get(ordenar);
		if (resp == null) {
			return "";
		} else {
			return resp;
		}
	}

	private void getlistado() {
		if (tabla.equalsIgnoreCase("criterios")) {

			switch (ordenar) {
			case R.id.contenidos:
				laquery = "select criterios.*,contenidos.contenido from criterioscontenido join criterios on criterioscontenido.idcriterio = criterios._id join contenidos on contenidos._id = criterioscontenido.idcontenido order by contenido, criterio";
				break;
			
			default:
				laquery = "select criterios._id,criterios.criterio,criterios.descripcion from criterios order by criterio";
			}
			listado = db.getNombreId("criterios");
			return;
		}
		if (tabla.equalsIgnoreCase("contenidos")) {
			switch (ordenar) {
			case R.id.criterio_sort:
				laquery = "select contenidos._id,contenidos.contenido,criterios.criterio from contenidos join criterios on criterios._id = contenidos.idbloque order by  bloque";
				break;
			case R.id.asignatura_sort:
				laquery = "select contenidos._id,contenidos.contenido,";
				break;
			default:
				laquery = "select contenidos._id,contenidos.contenido from contenidos order by 2";
			}

			listado = db.getNombreIdDescSql(laquery);
			return;
		}
		if (tabla.equalsIgnoreCase("actividades")) {
			
			Log.v(TAG," ordenar por "+ordenar);
			Log.v(TAG,"idactividad_sort "+R.id.idactividad_sort);
			Log.v(TAG,"idagrupamiento_sort "+R.id.idagrupamiento_sort);
			Log.v(TAG,"idtipoactividad_sort "+R.id.idtipoactividad_sort);
			laquery2 = "select * from actividades";
			switch (ordenar) {
			case R.id.idtipoactividad_sort:
				laquery = "select actividades._id,actividades.actividad,tipos_actividades.tipo_actividad,actividades.hecha,actividades.corregida   from actividades join  tipos_actividades on  tipos_actividades._id = actividades.idtipo_actividad  order by 3,2,1";
				break;
			case R.id.idagrupamiento_sort:
				laquery = "select actividades._id,actividades.actividad,agrupamientos.agrupamiento,actividades.hecha,actividades.corregida   from actividades join  agrupamientos on  agrupamientos._id = actividades.idagrupamiento  order by 3,2,1";
				break;
			case R.id.fechafin_sort:
				laquery = "select actividades._id,actividades.actividad,actividades.fecha_inicio,actividades.hecha,actividades.corregida from actividades order by 3 desc ,2,1";
				break;
			default:
				laquery = "select actividades._id,actividades.actividad,actividades_generales.actividad_general,actividades.hecha,actividades.corregida   from actividades join  actividades_generales on  actividades_generales._id = actividades.idactividad_general  order by 3,2,1";
			}
			Log.v(TAG, "query :" + laquery);
			listado = db.getNombreIdDescSql(laquery);
			return;
		}

		listado = db.getNombreId("grupos");
		return;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		if (tabla.equalsIgnoreCase("actividades")) {
			getActivity().getMenuInflater().inflate(
					R.menu.menu_actividades_lista, menu);
		}
		if (tabla.equalsIgnoreCase("contenidos")) {
			getActivity().getMenuInflater().inflate(R.menu.menu_contenidos,
					menu);
		}
		if (tabla.equalsIgnoreCase("criterios")) {
			getActivity().getMenuInflater().inflate(R.menu.menu_criterios,
					menu);
		}

		// getActivity().getMenuInflater().inflate(R.menu.menu_actividades_lista,
		// menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		try {
			Log.v(TAG, "ordenando por " + item.getItemId());
			ordenar = item.getItemId();
//			texto2 = ordenarpor.get(ordenar);
//			tvtexto2.setText(texto2);
		} catch (Exception e) {

		}
		this.populate();
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		getActivity().setTitle("URGENTISIMO -> "+tabla);
		containerView = container;

			contentView = inflater.inflate(R.layout.table_fragment, container, false);
			final EditText etfilter = (EditText)contentView.findViewById(R.id.searchfilter);
			ImageView buscar = (ImageView)contentView.findViewById(R.id.buscar);
			buscar.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG,"filtrando ");
					filtro = etfilter.getText().toString();
					ItemAdapter.filterResults(filtro);
				}
			});
			
			ImageView nuevo = (ImageView)contentView.findViewById(R.id.nuevo);
			nuevo.setClickable(true);
			nuevo.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					if (tabla.equalsIgnoreCase("actividades")) {
						Fragment editartabla = new ActividadFragment();
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
				}
			});
			
			TextView tvtexto1 = (TextView)contentView.findViewById(R.id.texto);
			tvtexto1.setText(texto1);
			tvtexto2 = (TextView)contentView.findViewById(R.id.texto2);
			tvtexto2.setText(texto2);
			
		populate();
		return contentView;
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	public void populate() {
		this.getlistado();
		ItemAdapter = new ImageAdapter(getActivity());
		ListView grid = (ListView) contentView.findViewById(R.id.sublista);
		grid.setAdapter(ItemAdapter);

	}

	public void setText(String texto) {
		texto = tabla;
	}

	public class ImageAdapter extends BaseAdapter {
		protected static final int YES_NO_DIALOG = 4;
		Context MyContext;
		private ArrayList<nombreId> items;

		public ImageAdapter(Context _MyContext) {
			MyContext = _MyContext;
			this.items = listado;
		}

		// @Override
		public int getCount() {
			return items.size();
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v;
			final nombreId elemento = items.get(position);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) MyContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

					v = inflater.inflate(R.layout.texto_imgbutton_item, parent,
							false);

			} else {
				v = convertView;
			}

			final TextView tv = (TextView) v.findViewById(R.id.texto);

			tv.setText(elemento.getNombre());
			LinearLayout elitem = (LinearLayout) v.findViewById(R.id.elitem);

			Drawable img = getActivity().getResources().getDrawable(
					R.drawable.siguiente_m);
			Drawable lefticon = geticon(elemento.getDescripcion(), tabla);
			tv.setCompoundDrawablesWithIntrinsicBounds(lefticon, null, img,
					null);

			TextView tv2 = (TextView) v.findViewById(R.id.texto2);
			tv2.setText(elemento.getDescripcion());
			ImageView borrar = (ImageView) v.findViewById(R.id.icono);
			borrar.setClickable(true);
			borrar.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					customDialog myDialog = new customDialog(getActivity(),
							"Borrar " + elemento.getNombre(),
							new OnReadyListener(elemento.getId()), elemento
									.getId(), YES_NO_DIALOG);
					myDialog.show();
					Log.v(TAG, "borrar elemento de la tabla " + tabla
							+ " con id " + elemento.getId());
				}
			});

			elitem.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					Log.v(TAG, "EDITAR elemento de la tabla " + tabla
							+ " con id " + elemento.getId());
					Fragment editartabla = new EditarTabla(tabla, elemento
							.getId());
					FragmentTransaction transaction1 = getFragmentManager()
							.beginTransaction();
					transaction1.setCustomAnimations(R.anim.left_to_right,
							R.anim.right_to_left);

					transaction1.replace(R.id.fragment_container2, editartabla);

					transaction1
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					transaction1.addToBackStack(null);
					transaction1.commit();

					// }
				}
			});
			if (tabla.equalsIgnoreCase("actividades")) {
				// elitem.setClickable(true);
				elitem.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						String idactividad = listado.get(position).getId();
						Log.v(TAG, "click en actividad" + idactividad + " "
								+ db.gettimestamp());
						Fragment evaluacion = new EvalFragment(idactividad,
								ordenarpor);
						FragmentTransaction transaction1 = getFragmentManager()
								.beginTransaction();

						transaction1.setCustomAnimations(R.anim.left_to_right,
								R.anim.right_to_left);
						transaction1.replace(R.id.fragment_container2,
								evaluacion);
						transaction1
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						transaction1.addToBackStack(null);
						transaction1.commit();
					}
				});
			}
			if (tabla.equalsIgnoreCase("grupos") || tabla.equalsIgnoreCase("criterios") || tabla.equalsIgnoreCase("contenidos")) {
				elitem.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						Integer tipo = 1;
						if (tabla.equalsIgnoreCase("criterios")) {
							tipo = 3;
						}
						if (tabla.equalsIgnoreCase("contenidos")) {
							tipo = 2;
						}
						if (tabla.equalsIgnoreCase("objetivos")) {
							tipo = 4;
						}
						if (tabla.equalsIgnoreCase("asignaturas")) {
							tipo = 5;
						}
						String elido = listado.get(position).getId();
						Fragment grupofragment = new editRecordFragment(elido,tipo);
						FragmentTransaction transaction1 = getFragmentManager()
								.beginTransaction();

						transaction1.setCustomAnimations(R.anim.left_to_right,
								R.anim.right_to_left);
						transaction1.replace(R.id.fragment_container,
								grupofragment);
						transaction1
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						transaction1.addToBackStack(null);
						transaction1.commit();
					}
				});
			}
			LinearLayout sublista = (LinearLayout) v.findViewById(R.id.lista);
			sublista.removeAllViews();
			if (tabla.equalsIgnoreCase("criterios")) {
//				Log.v(TAG, " le dio a criterios el cerote");
				String q = "select actividades._id, actividades.actividad,actividades.descripcion from criterios_actividad join actividades on actividades._id = criterios_actividad.idactividad where idcriterio = '"
						+ elemento.getId() + "' order by actividad";
				ArrayList<nombreId> sublistitems = db.getNombreIdDescSql(q);
				for (int j = 0; j < sublistitems.size(); j++) {
					nombreId item = sublistitems.get(j);
					TextView tvitem = new TextView(getActivity());
					tvitem.setText(item.getNombre());
					sublista.addView(tvitem);
					Log.v(TAG, "metiendo un elemento de sublista criterios "
							+ item.getNombre());
				}
			}

			return v;

		}
		
		 public void filterResults(String filter)
		    {
			 filtered = new ArrayList<nombreId>();
			 for (int i = 0; i < listado.size(); i++) {
				 nombreId elemento = listado.get(i);
				 
				 if (elemento.getNombre().toLowerCase().contains(filtro.toLowerCase()) || filtro.equals(""))  {
					 filtered.add(elemento);
				 }
			 }
		     this.items = filtered;
		     notifyDataSetChanged();
		    }
		

		// @Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		// @Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	    public void registerDataSetObserver(DataSetObserver observer) {
	        /* used to make the notifyDataSetChanged() method work */
	        super.registerDataSetObserver(observer);
	    }
	}

	public class OnReadyListener implements customDialog.ReadyListener {
		private static final String TAG = "OnReadyListener";
		private String elid;

		public OnReadyListener(String elid) {
			this.elid = elid;
		}

		public void ready(String texto) {
			Log.v(TAG, "borrar de " + tabla + " : " + texto + " elid " + elid);
			db.borraIdDeTabla(tabla, elid);
			populate();
		}

		public void ready(nombreId resp) {
			// TODO Auto-generated method stub

		}

	}

	public void ready(nombreId resp) {
		// TODO Auto-generated method stub

	}

	public Drawable geticon(String elicono, String eltipo) {
		Drawable resp = getActivity().getResources().getDrawable(
				R.drawable.act_hecha_m);
		if (eltipo.contains("actividades")) {
			if (elicono.equalsIgnoreCase("00")) {
				resp = getActivity().getResources().getDrawable(
						R.drawable.act_no_m);
			}
			if (elicono.equalsIgnoreCase("10")
					|| elicono.equalsIgnoreCase("01")) {
				resp = getActivity().getResources().getDrawable(
						R.drawable.act_medio_m);
			}
		} else {
			resp = getActivity().getResources().getDrawable(
					R.drawable.atencion_s);
		}
		return resp;
	}
}
