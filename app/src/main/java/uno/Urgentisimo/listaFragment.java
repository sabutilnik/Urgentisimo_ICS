package uno.Urgentisimo;

import java.util.ArrayList;
import java.util.Map;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//import uno.Urgentisimo.EditarTabla.Parent;

public class listaFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : LISTA FRAGMENT : ";

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
	private EditText etfiltro;
	private LinearLayout sublistll;
	private LinearLayout llnuevo;

	
	
	private LayoutInflater inf;
	
	private ArrayList<Map<String,String>> sublista;
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

	public listaFragment(String title,Integer tipo, ArrayList<Map<String,String>> sublist, String campo) {
		Log.v(TAG, "init lista FRAGMENT... ");
		this.tipo = tipo;
		this.titulo = title;
		this.sublista = sublist;
		this.campo = campo;
		db = new DataBaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		contentView = inflater.inflate(R.layout.lista_fragment,container,
				false);
		etfiltro = (EditText) contentView.findViewById(R.id.etfiltro);
		
		TextView tvtitle = (TextView)contentView.findViewById(R.id.tvtitulo);
		tvtitle.setText(titulo);
		
		sublistll = (LinearLayout) contentView.findViewById(R.id.sublist);
		
		inf = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		
		fillLayout();
		
		
		ImageView filtrar = (ImageView) contentView.findViewById(R.id.buscar);
		filtrar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				fillLayout();				
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
		for (int i = 0; i < sublista.size(); i++) {
			final Map<String,String> elitem = sublista.get(i);
			if (elitem.get("checked").equalsIgnoreCase("1")) {
				Log.v(TAG," este cerote ya está "+elitem.get(campo));
				continue;
			}
			String eltexto = elitem.get(campo);
			if (tipo == 1) {
				eltexto = elitem.get("nombre")+" "+elitem.get("apellidos");
			}
			
			if (!eltexto.toLowerCase().contains(etfiltro.getText().toString().toLowerCase())){
				continue;
			}
 			View customView = inf.inflate(R.layout.simple_text_item, null);
			TextView tvtext = (TextView) customView.findViewById(R.id.text);
			tvtext.setText(eltexto);
			Drawable img = context.getResources().getDrawable( R.drawable.anterior_s );
			tvtext.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null );
			customView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG," le dio el cerote "+elitem.get("_id"));
						editRecordFragment fr = (editRecordFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
						fr.switchChecked(elitem.get("_id"));
						fillLayout();
						
					}
			});
			sublistll.addView(customView);	
		}
	}

	public void updateData(String elid) {
		Log.v(TAG,"update a "+elid);
		switchChecked(elid);
	}

	private void switchChecked(String elid) {
		ArrayList<Map<String,String>> temp = new ArrayList<Map<String,String>>();
		for (int i=0; i<sublista.size(); i++) {
			Map<String,String> item = sublista.get(i);

			if (item.get("_id").equalsIgnoreCase(elid)) {
				Log.v(TAG,"antes "+item.get("checked"));
				if (item.get("checked").equalsIgnoreCase("0")) {
					item.put("checked", "1");
				} else {
					item.put("checked", "0");
				}
				Log.v(TAG,"Cambiando al cerote con id "+item.get("_id")+" "+item.get("nombre")+" "+item.get("checked"));
			}
			temp.add(item);

		}
		this.sublista = temp;
		fillLayout();
	}
}