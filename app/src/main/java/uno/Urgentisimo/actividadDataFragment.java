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
import android.widget.Toast;

public class actividadDataFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : ACTIVIDAD DATA FRAGMENT : ";

	private static final Integer GRUPOS = 15;
	
	
	private static final int ACTIVIDADESGENERALES=3;
	private static final int TIPOSACTIVIDAD=4;
	private static final int AGRUPAMIENTOS=5;

	private static final Integer CRITERIOS = 7;
	private static final Integer MATERIALES = 8;
	

	private Context context;
	private Integer tipo;
	private String titulo;
	private String campo;
	private EditText filtro;
	private LinearLayout sublistll;
	private LinearLayout llnuevo;
	
	private String tabla;
	
	private EditText texto;
	private EditText etdesco;
	private EditText etpeso;
	
	private LayoutInflater inf;
	
	private ArrayList<Map<String,String>> sublist;
	private View contentView;
	private DataBaseHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();

	}

	public actividadDataFragment(String title,Integer tipo, ArrayList<Map<String,String>> sublist, String campo, String tabla) {
		Log.v(TAG, "init actividad data FRAGMENT... ");
		this.tipo = tipo;
		this.titulo = title;
		this.sublist = sublist;
		this.campo = campo;
		this.tabla = tabla;
		db = new DataBaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		contentView = inflater.inflate(R.layout.actividad_data_fragment,container,
				false);
		filtro = (EditText) contentView.findViewById(R.id.filter);
		
		TextView tvtitle = (TextView)contentView.findViewById(R.id.title);
		tvtitle.setText(titulo);
		
		sublistll = (LinearLayout) contentView.findViewById(R.id.sublist);
		
		inf = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		llnuevo = (LinearLayout) contentView.findViewById(R.id.llnuevo);
		llnuevo.setVisibility(View.GONE);
		
		texto = (EditText)contentView.findViewById(R.id.etnuevo);
		etdesco = (EditText)contentView.findViewById(R.id.etdesc);
		etpeso = (EditText)contentView.findViewById(R.id.etpeso);
		
		if (tipo!=CRITERIOS) {
			etdesco.setVisibility(View.GONE);
			etpeso.setVisibility(View.GONE);
			texto.setHint("criterio");	
		} 
		fillLayout();
		
		
		ImageView filtrar = (ImageView) contentView.findViewById(R.id.buscar);
		filtrar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				fillLayout();				
			}
		});
		
		ImageView ivnuevo = (ImageView)contentView.findViewById(R.id.ivnuevo);
		ivnuevo.setClickable(true);
		ivnuevo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				llnuevo.setVisibility(View.VISIBLE);
			}
		});
		
		
		ImageView ivguardar = (ImageView)contentView.findViewById(R.id.ivguardar);
		ivguardar.setClickable(true);
		ivguardar.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				String valor = texto.getText().toString();
				Log.v(TAG,valor);
				Log.v(TAG,String.valueOf(tipo));
				Toast.makeText(context, "Guardando "+campo+" : "+valor, Toast.LENGTH_LONG).show();
				if (tipo == CRITERIOS ) {
					String desc = etdesco.getText().toString();
					String peso = etpeso.getText().toString();
					Boolean  existe = db.existe(tabla, campo, valor);
					Log.v(TAG,String.valueOf(existe));
					if (!db.existe(tabla, campo, valor)) {
						String sql = String.format("insert into criterios (criterio,descripcion,peso) values ('%s','%s','%s')", valor,desc,peso);
						Log.v(TAG,sql);
						db.insertSql(sql);
						String elnuevoid = db.getId(tabla, valor);
						Log.v(TAG,sql);
						Map <String,String> nuevodato = db.getMapId(String.format("select * from %s where %s = '%s'",tabla,campo,valor));
						nuevodato.put("checked", "0");
						sublist.add(nuevodato);
						fillLayout();
					}
				} 

				if (tipo < 7 || tipo == 15 ) {
					Boolean  existe = db.existe(tabla, campo, valor);
					Log.v(TAG,String.valueOf(existe));
					if (!db.existe(tabla, campo, valor)) {
						String sql = String.format("insert into %s ( %s ) values ('%s')", tabla,campo,valor);
						db.insertSql(sql);
						String elnuevoid = db.getId(tabla, valor);
						Log.v(TAG,sql);
						Map <String,String> nuevodato = db.getMapId(String.format("select * from %s where %s = '%s'",tabla,campo,valor));
						nuevodato.put("checked", "0");
						if (tabla.equalsIgnoreCase("grupos")) {
							nuevodato.put("metagrupo", "0");
//							nuevodato.put(grupo, value)
						}
						sublist.add(nuevodato);
						fillLayout();
					}
				
				}
			}
		});
		
		return contentView;
	}
	
	public void  clearFragment() {
			Fragment frag = new EmptyFragment();
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			transaction1.replace(R.id.fragment_container2, frag);
			transaction1.addToBackStack(null);
			transaction1.commit();
			ActividadFragment fr = (ActividadFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
	}
	
	public void fillLayout() {
		sublistll.removeAllViews();
		for (int i = 0; i < sublist.size(); i++) {
			final Map<String,String> elitem = sublist.get(i);
			if (elitem.get("checked").equalsIgnoreCase("1")) {
				continue;
			}
			if (!elitem.get(campo).toLowerCase().contains(filtro.getText().toString().toLowerCase()) && !filtro.getText().toString().equals("")) {
				continue;
			}
			if (tipo == GRUPOS) {
				if (elitem.get("metagrupo").equalsIgnoreCase("1")) {
					continue;
				}
			}
			
			View customView = inf.inflate(R.layout.simple_text_item, null);
			TextView tvtext = (TextView) customView.findViewById(R.id.text);
			tvtext.setText(elitem.get(campo));
			Drawable img = context.getResources().getDrawable( R.drawable.anterior_s );
			tvtext.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null );
			customView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG," le dio el cerote "+elitem.get("_id"));
					if (tipo == GRUPOS ) {
						alumnoDetail fr = (alumnoDetail) getFragmentManager().findFragmentById(R.id.fragment_container);
						fr.updateData(titulo, tipo, elitem.get("_id"));
					} else {
						ActividadFragment fr = (ActividadFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
						fr.updateData(titulo, tipo, elitem.get("_id"));
						
					}
					
					if (tipo < 6) {
						clearFragment();
					} else {
						fillLayout();
					}
				 	
				}

			});
			sublistll.addView(customView);	
		}
	}

	public void updateData(String elid) {
		switchChecked(elid);
		
	}

	private void switchChecked(String elid) {
		for (int i=0; i<sublist.size(); i++) {
			Map<String,String> item = sublist.get(i);
			if (item.get("_id").equalsIgnoreCase(elid)) {
				if (item.get("checked").equalsIgnoreCase("0")) {
					item.put("checked", "1");
				} else {
					item.put("checked", "0");
				}
				return;
			}
		}
	}
	
	private void fillEditText(final EditText et, String texto) {
		et.addTextChangedListener(new TextWatcher() {
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		    }

		    public void onTextChanged(CharSequence s, int start, int before, int count) {

		}
		public void afterTextChanged(Editable s) {				
		    	db.getId(tabla, campo);
			}
			});
	}
}