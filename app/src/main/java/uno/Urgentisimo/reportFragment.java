package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//import uno.Urgentisimo.UtilsLib.evalCriterio;
//import uno.Urgentisimo.UtilsLib.evalCriterioAlumno;
//import uno.Urgentisimo.UtilsLib.ordenarpor;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;

import org.w3c.dom.Text;

public class reportFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : REPORTES : ";
	protected static final Integer ASIGNATURAS = 1;
	protected static final Integer ACTIVIDADES = 2;
	protected static final Integer CRITERIOS = 3;
	
	private UtilsLib utilslib;
	
	public Map<String,Map<String,String>> listado;
	private Context context;
	private DataBaseHelper db;
	public View contentView;
	public Integer tipo;
    public String eltitulo;
	public String buscar;
	public String campo;
	
	private String fecha1;
	private String fecha2;
//	private TextView tvasignaturas;
//	private TextView tvactividades;
//	private TextView tvcriterios;
	
	private LinearLayout lllistado;
	private ImageView ivbuscar;
	private EditText etbuscar;
	private reportesWrite derecha;
    private FragmentTransaction transaction1;

    private TextView titulo;
	
	private Map<String,String> ids;
    private Map<String,Boolean> losids;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        utilslib = new UtilsLib(context);
        this.tipo = ACTIVIDADES;
        this.buscar = "";
        this.context = getActivity();
        this.fecha1 = "2013-12-01";
        this.fecha2 = "2014-04-01";
        this.eltitulo = "Actividades";
        this.derecha = new reportesWrite();
        transaction1 = getFragmentManager().beginTransaction();
        transaction1.replace(R.id.fragment_container2, this.derecha);
        transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction1.commit();
        this.losids = new HashMap<String, Boolean>();
 
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reportes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.criterios:
            this.tipo = CRITERIOS;
            this.eltitulo="Criterios";
            refresh();
            return true;
        case R.id.actividades:
            this.eltitulo = "Actividades";
            this.tipo = ACTIVIDADES;
            refresh();
            return true;
        default:
            // Not one of ours. Perform default menu processing
            return super.onOptionsItemSelected(item);
        }
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		db = new DataBaseHelper(getActivity());
		contentView = inflater.inflate(R.layout.reportes_fragment, container,false);
//		tvasignaturas = (TextView)contentView.findViewById(R.id.tvasignaturas);
//		tvactividades = (TextView)contentView.findViewById(R.id.tvactividades);
//		tvcriterios = (TextView)contentView.findViewById(R.id.tvcriterios);
//		fillTextView(tvasignaturas,1);
//		fillTextView(tvactividades,2);
//		fillTextView(tvcriterios,3);
        titulo = (TextView) contentView.findViewById(R.id.titulo);
		lllistado = (LinearLayout) contentView.findViewById(R.id.lllistado);
		etbuscar = (EditText) contentView.findViewById(R.id.etbuscar);
		ivbuscar = (ImageView) contentView.findViewById(R.id.ivbuscar);
		ivbuscar.setClickable(true);
		ivbuscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buscar");
                buscar = etbuscar.getText().toString();
                refresh();
            }
        });
        ivbuscar.setVisibility(View.GONE);
		etbuscar.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                buscar = etbuscar.getText().toString();
                refresh();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        refresh();
		
		return contentView;
	}

	private void fillTextView(TextView tv,final Integer i) {
		tv.setClickable(true);
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG,"tipo "+tipo);
				tipo = i;
				Log.v(TAG,"tipo "+tipo);
//				derecha.settipo(i);
				refresh();
			}
		});
		
	}
	
	public void refresh() {
		listado = this.getlistado();
		lllistado.removeAllViews();
		titulo.setText(this.eltitulo);
		ArrayList<String> listaux = utilslib.ordenarmap(listado, campo);
		
		for (int i = 0; i<listaux.size(); i++) {
			final String lakey = listaux.get(i);
			
			final Map<String,String> value = listado.get(lakey);
			String texto = value.get(campo).toString();
			if (texto.toUpperCase().contains(buscar.toUpperCase()) || buscar.length() == 0) {
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View cv = inflater.inflate(R.layout.textview2, null);
				final TextView element = (TextView) cv.findViewById(R.id.texto);
				if (value.get("checked").equalsIgnoreCase("0")) {
					element.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.unchecked_m,0);		
				} else {
					element.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check_m,0);
				}
				cv.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						if (value.get("checked").equalsIgnoreCase("0")) {
							value.put("checked", "1");
                            losids.put(lakey,true);
							element.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check_m,0);
                            derecha.insertId(lakey);
							
						} else {
							value.put("checked", "0");
                            losids.put(lakey,false);
							element.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.unchecked_m,0);
                            derecha.deleteId(lakey);
						}
						Log.v(TAG,"la key "+lakey);
						listado.put(lakey, value);
						Log.v(TAG,"cambio a "+value.get("checked"));
					}
				});
                Boolean miembro = losids.get(lakey);
                if (miembro == null) {
                    miembro = false;
                }
                if (miembro) {
                    value.put("checked","1");
                    element.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check_m,0);
                } else {
                    value.put("checked","0");
                }
                listado.put(lakey,value);
			    element.setText(value.get(campo));		 
			    lllistado.addView(cv);
			}
		}
	}

	private Map<String, Map<String, String>> getlistado() {
		Map<String, Map<String,String>> resp = new HashMap<String,Map<String,String>>();
		String query = "";
		switch(tipo) {
		case 1:
			query = "select * from asignaturas order by asignatura";
			campo = "asignatura";
			break;
		case 2:
			query = "select * from actividades where order by actividad";
			query = "select * from actividades where  strftime('%s',fecha_inicio) >= strftime('%s','"+fecha1+"') and strftime('%s',fecha_fin) < strftime('%s','"+fecha2+"') order by actividad";
			campo = "actividad";
			break;
		case 3:
			query = "select * from criterios order by criterio";
			campo = "criterio";
			break;
		}
		resp = db.getMap(query);
		return resp;
	}
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "en pausa");
	}
	
	public ArrayList<String> getids(){
		ArrayList<String> ids = new ArrayList<String>();
		for (Map.Entry<String, Map<String,String>> entry : listado.entrySet())
		{
			if (entry.getValue().get("checked").equalsIgnoreCase("1")) {
				ids.add(entry.getKey().toString());
			}	
		}
		return ids;
	}
	
	public Map<String,Boolean> getlosids() {
        Map<String,Boolean > resp = new HashMap<String, Boolean>();
        for (Map.Entry<String, Map<String,String>> entry : listado.entrySet())
        {
            if (entry.getValue().get("checked").equalsIgnoreCase("1")) {
                resp.put(entry.getKey().toString(), true);
            }
        }
        return resp;
	}

	public Integer gettipo() {
		return this.tipo;
	}
	
}
