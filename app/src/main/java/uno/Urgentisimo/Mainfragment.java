package uno.Urgentisimo;

import java.io.IOException;
import java.util.ArrayList;

import jxl.write.WriteException;

import uno.Urgentisimo.TableFragment.OnReadyListener;




import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Mainfragment extends ListFragment {


	private static final String TAG = "URGENTISIMO : MainFragment : ";
    public NotificationsFragment notifications;
    public String fecha_inicio;
    public String fecha_fin;

    public void setFechas(String trimestre) {
        if (trimestre.equalsIgnoreCase("1")) {
            fecha_inicio = "2013-09-01";
            fecha_fin = "2013-12-01'";
        }
        if (trimestre.equalsIgnoreCase("2")) {
            fecha_inicio = "2013-09-01";
            fecha_fin = "2013-12-01";
        }
        if (trimestre.equals("3")) {
            fecha_inicio = "2013-09-01";
            fecha_fin = "2013-12-01";
        }
    }
	
	  public interface onFragmentChange {
	        public void onArticleSelected(int position);
	    }

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataBaseHelper db = new DataBaseHelper(getActivity());
		try {
			db.createDataBase();
			db.upgradeDB();
		} catch (IOException e) {
			Log.v(TAG, "Unable to create db");
		}

		Fragment calendario = new calendarFragment();
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		Log.v(TAG,"reportes a la 1");
		transaction1.replace(R.id.fragment_container, calendario);
		transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction1.addToBackStack(null);
		transaction1.commit();

        this.notifications = new NotificationsFragment();
        FragmentTransaction transaction2 = getFragmentManager()
				.beginTransaction();
        transaction2.replace(R.id.notifications, this.notifications);
        transaction2.commit();
	}


	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ArrayList<String> values =  new ArrayList<String>();
		values.add("Alumnos");
		values.add("Programación");
		values.add("Evaluación");
		values.add("Actividades");
		values.add("Asistencia");
		values.add("Grupos");
		values.add("Criterios");
		values.add("Contenidos");
		values.add("Reportes");
		values.add("test");
		ImageAdapter ItemAdapter = new ImageAdapter(this.getActivity(), values);
		setListAdapter(ItemAdapter);
	}

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Log.v(TAG,"position "+position+" item "+item);
		if (position == 0) {
			Fragment derecha = new DetailFragment();
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.fragment_container, derecha);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//transaction.addToBackStack(null);
			transaction.commit();
            this.notifications.mensaje("click en alumnos ...");

		}
		if (position == 1) {
			Fragment progra = new ProgramacionFragment();
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			//transaction1.addToBackStack(null);
			transaction1.replace(R.id.fragment_container, progra);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			
			transaction1.commit();
            this.notifications.mensaje("click en programación ...");
		}

//		if (position == 2) {
//
//			Log.v(TAG," voy de nuevo llll");
//			Fragment progra = new EvaluacionFragment();
//			FragmentTransaction transaction1 = getFragmentManager()
//					.beginTransaction();
//			
//			transaction1.replace(R.id.fragment_container, progra);
//			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//			transaction1.addToBackStack(null);
//			transaction1.commit();
//		}

		if (position == 3) {
			ArrayList<String> ordenarpor = new ArrayList<String>();
			ordenarpor.add("Actividad general");
			ordenarpor.add("Tipo de Actividad");
			ordenarpor.add("Agrupamiento");
			ordenarpor.add("Fecha");
			
			Fragment criterios = new TableFragment("actividades","",ordenarpor,"Actividad");
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			
			transaction1.replace(R.id.fragment_container, criterios);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//transaction1.addToBackStack(null);
			transaction1.commit();
            this.notifications.mensaje("click en actividades ...");
		}
		
		if (position == 5) {
			ArrayList<String> ordenarpor = new ArrayList<String>();
			ordenarpor.add("grupo");
//			ordenarpor.add("Tipo de Actividad");
//			ordenarpor.add("Agrupamiento");
//			ordenarpor.add("Fecha");
			
			Fragment criterios = new TableFragment("grupos","alumnos",ordenarpor,"grupo");
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			
			transaction1.replace(R.id.fragment_container, criterios);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//transaction1.addToBackStack(null);
			transaction1.commit();
            this.notifications.mensaje("click en grupos");
		}
		
		if (position == 6) {
			ArrayList<String> ordenarpor = new ArrayList<String>();
			ordenarpor.add("Asignatura");
			ordenarpor.add("Bloque");
			ordenarpor.add("Contenidos");
			Fragment criterios = new TableFragment("criterios","contenidos",ordenarpor,"Criterio");
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			
			transaction1.replace(R.id.fragment_container, criterios);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//transaction1.addToBackStack(null);
			transaction1.commit();
            this.notifications.mensaje("click en criterios");
		}
		if (position == 7) {
			ArrayList<String> ordenarpor = new ArrayList<String>();
			ordenarpor.add("Asignatura");
			ordenarpor.add("Bloque");
			ordenarpor.add("Criterios");
			Fragment criterios = new TableFragment("contenidos","bloques",ordenarpor,"Contenido");
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			
			transaction1.replace(R.id.fragment_container, criterios);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//transaction1.addToBackStack(null);
			transaction1.commit();
            this.notifications.mensaje("click en contenidos");
		}
		
		if (position == 9) {
			Log.v(TAG," ods ");
			ArrayList<String> ordenarpor = new ArrayList<String>();
			ordenarpor.add("Actividad general");
			ordenarpor.add("Tipo de Actividad");
			ordenarpor.add("Agrupamiento");
			ordenarpor.add("Fecha");
			
			Fragment criterios = new TableFragment("actividades2","",ordenarpor,"actividad");
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			
			transaction1.replace(R.id.fragment_container, criterios);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		//	transaction1.addToBackStack(null);
			transaction1.commit();
            this.notifications.mensaje("click en criterios2 - raro");

		}
		if (position == 8) {
			Fragment report = new reportFragment();
			FragmentTransaction transaction1 = getFragmentManager()
					.beginTransaction();
			Log.v(TAG,"reportes a la 1");
			transaction1.replace(R.id.fragment_container, report);
			transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			//transaction1.addToBackStack(null);
			transaction1.commit();
            this.notifications.mensaje("click en reportes");
		}
	}
	
	
	public class ImageAdapter extends BaseAdapter {
		protected static final int YES_NO_DIALOG = 4;
		Context MyContext;
		private ArrayList<String> listado ;
		

		public ImageAdapter(Context _MyContext, ArrayList<String> listado) {
			MyContext = _MyContext;
			this.listado = listado;
		}

		// @Override
		public int getCount() {
			return listado.size();
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v;


			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) MyContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				v = inflater.inflate(R.layout.textview, null);
				
			} else {
				v = convertView;
			}
			TextView tv = (TextView) v.findViewById(R.id.texto);
			String texto = listado.get(position).toString();
			tv.setText(texto);
			
			ImageView img = (ImageView) v.findViewById(R.id.icono);
			img.setImageResource(geticon(texto));
	
			return v;

			
		}

		private int geticon(String texto) {
			int resp =R.drawable.atencion_m;
			if (texto.equalsIgnoreCase("Alumnos")) {
			return R.drawable.alumno_m;
			}
			if (texto.equalsIgnoreCase("Programación")) {
				return R.drawable.programacion_m;
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
	}
	

}