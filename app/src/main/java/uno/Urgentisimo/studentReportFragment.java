package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import uno.Urgentisimo.UtilsLib.evalCriterio;
import uno.Urgentisimo.UtilsLib.evalCriterioAlumno;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class studentReportFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : STUDENTS REPORTS : ";
	private UtilsLib utils;
	private evalCriterioAlumno listado;
	private View contentView;
	private ListView datos;
	private DatosAdapter datosAdapter;
	private String alumnoid;
	private Map<String,String> alumno;
	private DataBaseHelper db;

	public studentReportFragment(String elid) {
		alumnoid = elid;
		Log.v(TAG," eo entrando en el alumno cerote "+alumnoid);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		db = new DataBaseHelper(getActivity());
		this.utils = new UtilsLib(getActivity());
		
		Map<String,Map<String,String >> alumnos = db.getMap("select * from alumnos");
		
		alumno = alumnos.get(alumnoid);
		
		
		
		listado = utils.new evalCriterioAlumno(Integer.valueOf(alumnoid));
		contentView = inflater.inflate(R.layout.student_report, container,
				false);
		TextView tv = (TextView) contentView.findViewById(R.id.nombre);
		Log.v(TAG," el alumno es "+alumno.get("nombre"));
		tv.setText(alumno.get("nombre")+" "+alumno.get("apellidos"));
		listado = db.getEvalCriterios(Integer.valueOf(alumnoid), "9");
		
		
		
		ImageView alumnofoto = (ImageView) contentView.findViewById(R.id.alumno_foto);
		String picFile = "/sdcard/Urgentisimo/pics/basepic_"+alumno.get("_id")+".png";
		Log.v(TAG," picfile "+picFile);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(picFile, options);
//		bm = Bitmap.createScaledBitmap(bm, width, height, false);
		alumnofoto.setImageBitmap(bm);
		
		datos = (ListView) contentView.findViewById(R.id.lista);
		datosAdapter = new DatosAdapter(getActivity(), R.layout.line_sublist,
				listado.getcriterios());
		datos.setAdapter(datosAdapter);

		String notaf = listado.getMediaPonderada();
		tv.setText(tv.getText().toString()+" : "+notaf);
		
		return contentView;
	}

	private class DatosAdapter extends BaseAdapter {
		private ArrayList items;
		Context MyContext;
		private LayoutInflater mInflater;

		public DatosAdapter(Context context, int textViewResourceId,
				ArrayList items) {
			super();
			MyContext = context;
			this.items = items;
			mInflater = (LayoutInflater) MyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			Integer idcriterio = (Integer) items.get(position);
			ViewHolder holder = null;		
			if (convertView == null) {
				idcriterio = (Integer) items.get(position);
				convertView = mInflater.inflate(R.layout.line_sublist, null);
				holder = new ViewHolder();
				holder.campo = (TextView) convertView.findViewById(R.id.tvCampo);
				holder.valor = (TextView) convertView.findViewById(R.id.tvValor);
				holder.nota = (EditText) convertView.findViewById(R.id.tvValor2);
				holder.sl = (LinearLayout) convertView.findViewById(R.id.slist);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.sl.removeAllViews();
			ArrayList<evalCriterio> datos = listado.getEvaluaciones(idcriterio);
			holder.campo.setText(String.valueOf(listado.getCriterio(idcriterio)));
			holder.valor.setText(String.valueOf(listado.getEvaluacion(idcriterio)));
			holder.nota.setText(String.valueOf(listado.getEvaluacionAlumno(idcriterio,1))  );
			for (int i = 0; i< datos.size(); i++) {
				evalCriterio dato = datos.get(i);
				LayoutInflater vi1 = (LayoutInflater) MyContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View sView = vi1.inflate(R.layout.simple_subline, null);
				TextView t1 = (TextView) sView.findViewById(R.id.tv1);
				t1.setText(dato.actividad);
				TextView t2 = (TextView) sView.findViewById(R.id.tv2);
				String xx= t2.getText().toString();
				t2.setText(String.valueOf(dato.evaluacion)+"/"+String.valueOf(dato.toc));
				holder.sl.addView(sView);
			}
			
			holder.nota.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					 if(!hasFocus) {
						TextView tv = (TextView) v;
						int idcriterio = (Integer) items.get(position);
						String nota = tv.getText().toString();
						if (nota == null) {
							nota ="0";
						}
						Log.v(TAG,"Guarda nota "+alumnoid+":"+idcriterio+" : "+tv.getText().toString());
						listado.updateEvalCriterioAlumno(idcriterio,nota,"","","");
						db.updateEvalCriterioAlumno(Integer.valueOf(alumnoid),idcriterio,nota,"","","");
					 }
				}
			});
			return convertView;
		}

		// @Override
		public Integer getItem(int arg0) {
			// TODO Auto-generated method stub
			Integer res = (Integer) items.get(arg0);
			Log.v(TAG,"getitem "+arg0+": crit :"+res);
			
			return res;
		}

		// @Override
		public long getItemId(int arg0) {
			Log.v(TAG,"getitemid "+arg0+" : "+items.get(arg0));
			return arg0;
		}

		// @Override
		public int getCount() {
			Log.v(TAG,"get count "+items.size());
			return items.size();
		}
		
		public void addItem(int item) {
	         items.add(item);
	         notifyDataSetChanged();
//	         listado
		}
		
		public void updateValItem(int item, String val, String index) {
			
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "en pausa");

	}
	
	public static class ViewHolder {
        public TextView campo;
        public TextView valor;
        public EditText nota;
        public LinearLayout sl;
    }
}
