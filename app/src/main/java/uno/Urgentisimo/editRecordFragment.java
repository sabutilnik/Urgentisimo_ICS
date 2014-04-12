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

public class editRecordFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : 	EDIT RECORD FRAGMENT : ";
	
	private static final Integer GRUPOS = 1;
	private static final Integer CONTENIDOS = 2;
	private static final Integer CRITERIOS = 3;
	private static final Integer OBJETIVOS = 4;
	private static final Integer ASIGNATURAS = 5;
	private static final Integer ACTIVIDADES = 6;
	private static final Integer ALUMNOS = 10;
	
	
	private  final Integer NOMBRE = 1;
	private  final Integer DESCRIPCION = 2;
	private  final Integer FILTRO = 3;

	private Context context;
	private Integer tipo;
	private String id;
	private Integer subelement;
	
	private String subtitulo;
	private String subelemento;
	
	private Map<String,String> data;
	private Map<Integer,String> nombres;
	private Map<Integer,String> filtros;
	private Map<Integer,String> descripciones;
	
	private EditText etfiltro;
	private EditText etnombre;
	private EditText etdescripcion;
	
	private LinearLayout sublistll;
	private LinearLayout llnuevo;

	
	private EditText texto;
	private LayoutInflater inf;
	
	private ArrayList<Map<String,String>> sublist;
	private View contentView;
	private DataBaseHelper db;
	private UtilsLib utilidades;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();
	}

	public editRecordFragment(String id, Integer tipo) {
		nombres = new HashMap<Integer,String>();
		nombres.put(GRUPOS, "grupo");
		nombres.put(CRITERIOS, "criterio");
		nombres.put(OBJETIVOS,"objetivo");
		nombres.put(CONTENIDOS,"contenido");
		
		filtros = new HashMap<Integer,String>();
		filtros.put(GRUPOS, "filtro");
		filtros.put(CRITERIOS, "peso");
		filtros.put(OBJETIVOS,"");
		filtros.put(CONTENIDOS,"");
		
		
		descripciones = new HashMap<Integer,String>();
		descripciones.put(GRUPOS, "descripcion");
		descripciones.put(CRITERIOS, "descripcion");
		descripciones.put(OBJETIVOS,"");
		descripciones.put(CONTENIDOS,"");
		
		utilidades = new UtilsLib(this.context);
		this.db = new DataBaseHelper(this.context);
		Log.v(TAG, "init edit record fragment");
		this.tipo = tipo;
		this.id = id;
		switch (tipo) {
		case 1://grupos
			this.data = db.getMapId(String.format(
					"select * from grupos where _id = '%s'", this.id));
			if (data.get("metagrupo").equalsIgnoreCase("0")) {
				getsublist(this.tipo,ALUMNOS);
			} else {
				this.sublist = db.getMapList(data.get("filtro"));
			}
			break;
		case 2://contenidos
			this.data = db.getMapId(String.format("select * from contenidos where _id = '%s'",this.id));
			getsublist(this.tipo,CRITERIOS);
			break;
		case 3://criterios
			this.data = db.getMapId(String.format("select * from criterios where _id = '%s'",this.id));
			this.getsublist(this.tipo, CONTENIDOS);
			break;
		}
		
		db = new DataBaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		contentView = inflater.inflate(R.layout.edit_record_fragment,container,
				false);
		
		etfiltro = (EditText) contentView.findViewById(R.id.etfiltro);
		etnombre = (EditText) contentView.findViewById(R.id.etnombre);
		etdescripcion = (EditText)contentView.findViewById(R.id.etdescripcion);
		
		fillEditText(etfiltro,FILTRO);
		fillEditText(etnombre,NOMBRE);
		fillEditText(etdescripcion,DESCRIPCION);
		
		
		sublistll = (LinearLayout) contentView.findViewById(R.id.sublist);
		
		inf = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		fillLayout();

		ImageView ivguardar = (ImageView)contentView.findViewById(R.id.guardar);
		ivguardar.setClickable(true);

		Fragment frag = new listaFragment(this.subtitulo,this.tipo,this.sublist,this.subelemento);
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		transaction1.replace(R.id.fragment_container2, frag);
		transaction1.addToBackStack(null);
		transaction1.commit();
		
		ImageView guardar = (ImageView)contentView.findViewById(R.id.guardar);
		guardar.setClickable(true);
		guardar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				guardar();
				
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

	}
	
	public void fillLayout() {
		sublistll.removeAllViews();
		for (int i = 0; i < sublist.size(); i++) {
			final Map<String,String> elitem = sublist.get(i);
			Boolean show = false;
			View customView = inf.inflate(R.layout.simple_text_item, null);
			TextView tvtext = (TextView) customView.findViewById(R.id.text);
			String nombre = "";
			if (this.tipo == 1) {
				nombre = elitem.get("nombre")+" "+elitem.get("apellidos");
				if (data.get("metagrupo")!=null) {
					if (data.get("metagrupo").equalsIgnoreCase("1")) {
						show = true;
					}	
				} 
			}
			if (this.tipo == 2) {
				nombre = elitem.get("criterio");
				show = false;
			}
			if (this.tipo == 3) {
				nombre = elitem.get("contenido");
				show = false;
			}
			if (show == false) {
				if (elitem.get("checked").equalsIgnoreCase("1")) {
					show = true;
				}
			}

			if (show == true) { 
				
			tvtext.setText(nombre);
			Drawable img = context.getResources().getDrawable( R.drawable.delete_s );
			tvtext.setCompoundDrawablesWithIntrinsicBounds( null, img, null, null );
			customView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					listaFragment fr = (listaFragment) getFragmentManager().findFragmentById(R.id.fragment_container2);
					fr.updateData(elitem.get("_id"));
					fr.fillLayout();
					fillLayout();

				}

			});
			sublistll.addView(customView);
			}
		}
	}

	public void updateData(String elid) {
//		switchChecked(elid);

	}

	public Boolean switchChecked(String elid) {
		ArrayList<Map<String,String>> temp = new ArrayList<Map<String,String>>();
		for (int i=0; i<this.sublist.size(); i++) {
			Map<String,String> item = this.sublist.get(i);
			if (item.get("_id").equalsIgnoreCase(elid)) {
//				Log.v(TAG,"antes "+item.get("checked"));
				if (item.get("checked").equalsIgnoreCase("0")) {

					item.put("checked", "1");
				} else {
					item.put("checked", "0");
				}
//				Log.v(TAG,"Cambiando al cerote con id "+item.get("_id")+" "+item.get("nombre")+" "+item.get("checked"));
			}
			temp.add(item);
		}
		this.sublist = temp;
		fillLayout();
		return true;
		
	}
	
	private void fillEditText(final EditText et, Integer cual) {
		Boolean show = false;
		String campo = "";
		switch(cual) {
		case 1:
			campo = nombres.get(this.tipo);
			break;
		case 2:
			campo = descripciones.get(this.tipo);
			break;
		case 3:
			campo = filtros.get(this.tipo);
			break;
		}
		if (campo.equalsIgnoreCase("")) {
			et.setVisibility(View.GONE);
		} else {
			et.setVisibility(View.VISIBLE);
			et.setText(data.get(campo));
		}
		
		final String elcampo = campo;

		et.addTextChangedListener(new TextWatcher() {
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		    }

		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		public void afterTextChanged(Editable s) {				
		    	data.put(elcampo, et.getText().toString());
			}
			});
	}
	 
	
	private void guardar() {
		String query = "";
		Boolean insertarelementos = true;
		switch(this.tipo) {
		case 1://grupos
			query = String.format("update grupos set grupo='%s',descripcion='%s',filtro='%s',metagrupo='%s' where _id='%s'",data.get("grupo"),data.get("descripcion"),data.get("filtro"),data.get("metagrupo"),data.get("_id"));
			Log.v(TAG,query);
			db.insertSql(query);
			query = String.format("delete from alumnos_grupos where idgrupo = '%s'", data.get("_id"));
			db.insertSql(query);
			Log.v(TAG,query);
			if (data.get("metagrupo").equalsIgnoreCase("1")) {
				insertarelementos = false;
//				query = String.format("insert into alumnos_grupos(idgrupo,idalumno,fecha_alta,fecha_baja) values ('%s','%s','%s','%s')",this.id,elitem.get("_id"),"","" );
			}
			break;
		case 3://criterios
			query = String.format("update criterios set criterio='%s',descripcion='%s',peso='%s',TOC='' where _id='%s'",data.get("criterio"),data.get("descripcion"),data.get("peso"),data.get("_id"));
			db.insertSql(query);
			query = String.format("delete from criterioscontenido where idcriterio = '%s'", data.get("_id"));
			db.insertSql(query);
			break;
		
		case 2://contenidos
			query = String.format("update contenidos set contenido='%s',idbloque='' where _id='%s'",data.get("contenido"),data.get("_id"));
			db.insertSql(query);
			query = String.format("delete from criterioscontenido where idcontenido = '%s'", data.get("_id"));
			db.insertSql(query);
			break;
		}	
		
		if (insertarelementos == true) {
			for (int i = 0; i<sublist.size(); i++) {
				Map<String,String> elitem = sublist.get(i);
				if (elitem.get("checked").equalsIgnoreCase("1")) {
					switch(this.tipo) {
					case 1:
						query = String.format("insert into alumnos_grupos(idgrupo,idalumno,fecha_alta,fecha_baja) values ('%s','%s','%s','%s')",this.id,elitem.get("_id"),"","" );
						break;
					case 2:
						query = String.format("insert into criterioscontenido(idcontenido,idcriterio) values ('%s','%s')", this.id,elitem.get("_id"));
						break;
						
					case 3:
						query = String.format("insert into criterioscontenido(idcriterio,idcontenido) values ('%s','%s')", this.id,elitem.get("_id"));
						break;
					}
	
					db.insertSql(query);
					Log.v(TAG,query);
				}
			}
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		switch(this.tipo) {
//		case 1://grupos
//			getActivity().getMenuInflater().inflate(R.menu.menu_grupos,
//					menu);
//			break;
		case 3://contenidos
		getActivity().getMenuInflater().inflate(R.menu.menu_contenidos,
				menu);
			break;
			
		case 2: //criterios
			getActivity().getMenuInflater().inflate(R.menu.menu_criterios,
				menu);
			break;

		
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(this.tipo) {
		case 1://grupos
			
			
			break;
		case 2://contenidos
			
			break;
			
		case 3://criterios
			switch(item.getItemId()) {
			case R.id.contenidos_sort:
				getsublist(this.tipo,CONTENIDOS);
				break;
			case R.id.actividades_sort:
				getsublist(this.tipo,ACTIVIDADES);
			}
			
			break;
		}
		

		return false;
	}
	
	public void getsublist(Integer element, Integer subelement) {
		if (element == CRITERIOS && subelement == CONTENIDOS) {
			this.subelemento = "contenido";
			this.subtitulo = "Contenidos";
			this.sublist = db.getMapList(String.format("select criterioscontenido.*,contenidos.contenido from criterioscontenido join contenidos on criterioscontenido.idcontenido = contenidos._id where idcriterio = '%s' order by contenido",this.id));
			this.sublist = utilidades.mergelists(db.getMapList("select * from contenidos order by contenido") , this.sublist, "_id", "idcontenido");
			return;
		}
		if (element == CRITERIOS && subelement == ACTIVIDADES) {
			this.subelemento = "actividad";
			this.subtitulo = "Actividades";
			this.sublist = db.getMapList(String.format("select actividades.actividad,criterios_actividad.* from criterios_actividad join actividades on actividades._id == criterios_actividad.idactividad where idcriterio = '%s' order by actividad", this.id));
			this.sublist = utilidades.mergelists(db.getMapList("select * from actividades order by actividad"), this.sublist, "_id", "idactividad");
			return;
		}
		if (element == GRUPOS) {
			this.subelemento = "nombre";
			this.subtitulo = "Alumnos";
			this.sublist = db
					.getMapList(String.format("select alumnos.nombre, alumnos.apellidos,alumnos_grupos.* from alumnos_grupos join alumnos on alumnos._id = alumnos_grupos.idalumno where idgrupo = '%s' order by 1,2 ",this.id));
			this.sublist = utilidades.mergelists(db.getMapList("select * from alumnos"), this.sublist,"_id", "idalumno");
			return;
		}
		if (element == CONTENIDOS && subelement == CRITERIOS) {
			this.subelemento = "criterio";
			this.subtitulo = "Criterios";
			this.sublist = db.getMapList(String.format("select criterioscontenido.*,criterios.criterio from criterioscontenido join criterios on criterioscontenido.idcriterio = criterios._id where idcontenido = '%s' order by criterio",this.id));
			this.sublist = utilidades.mergelists(db.getMapList("select * from criterios order by criterio"), this.sublist, "_id", "idcriterio");
			return;
		}
		if (element == CONTENIDOS && subelement == OBJETIVOS) {
			this.subelemento = "objetivo";
			this.subtitulo = "Objetivos";
			this.sublist = db.getMapList(String.format("select contenidosobjetivo.*,contenidos.contenido from contenidosobjetivo join contenidos on contenidosobjetivo.idcontenido = contenidos._id where idcontenido = '%s' order by objetivo",this.id));
			this.sublist = utilidades.mergelists(db.getMapList("select * from objetivos order by objetivo"), this.sublist, "_id", "idobjetivo");
			return;
		}

	}
}