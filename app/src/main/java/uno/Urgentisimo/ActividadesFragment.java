package uno.Urgentisimo;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActividadesFragment extends Fragment
{
	protected static final String TAG = "URGENTISIMO : ACTIVIDADES FRAGMENT : ";
	ListView MyGrid;
	private ArrayList<nombreId> listado;
	private View containerView;
	private View contentView;
	private String laquery;
	private UtilsLib utilidades;

	private DataBaseHelper db;
	
	public ActividadesFragment(){
		  Log.v(TAG,"init Actividades Fragmentoooooo");
			db = new DataBaseHelper(getActivity());
			listado = new ArrayList<nombreId>();
			utilidades = new UtilsLib(getActivity());
		 }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		containerView = container;
	    contentView = inflater.inflate(R.layout.actividades, container, false);    
	    populate();
	    return contentView;
	}

	@Override
	public void onDestroy() {        
		super.onDestroy();
		db.close();
	}
		
	public void populate() {
        laquery = "select actividades._id,actividades.actividad,actividades_generales.actividad_general   from actividades join  actividades_generales on  actividades_generales._id = actividades.idactividad_general  order by 3,2,1";
		ImageAdapter ActividadesAdapter = new ImageAdapter();
		ListView grid = (ListView) contentView.findViewById(R.id.grid);
		grid.setAdapter(ActividadesAdapter);

	}
	public void setText(String texto) {
		texto= "aa";
	}
	public class ImageAdapter extends BaseAdapter
	{
		private Context ctx;
		private ArrayList<Map<String,String>> actividades = new ArrayList<Map<String,String>>();

		public ImageAdapter()
		{	
			actividades = db.getMapList(laquery);
			actividades = utilidades.mergelists(db.getMapList("select * from actividades"), actividades, "_id", "_id");
			
		}

		//   @Override
		public int getCount() 
		{
			/* Set the number of element we want on the grid */
			return actividades.size();
		}

		public String getId (int position) {
			return actividades.get(position).get("_id");
		}

//		   @Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			if (actividades.size() == 0) {
				actividades = db.getMapList("select * from actividades");
			}
//			
			View v;
			if(convertView==null) {
				Context ctx ;
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.simple_subline_2, parent, false);
			}
			else {
				v = convertView;
			}

			TextView tv = (TextView)v.findViewById(R.id.tv1);
			TextView tv2 = (TextView)v.findViewById(R.id.tv2);
			tv.setText(actividades.get(position).get("actividad"));
			tv.setClickable(true);
			tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v(TAG,"click en actividad");
				String idactividad = actividades.get(position).get("_id");
				Fragment evaluacion = new EvalFragment();
				FragmentTransaction transaction1 = getFragmentManager()
						.beginTransaction();
				Log.v(TAG,"metiendo reportes ");
				transaction1.replace(R.id.fragment_container, evaluacion);
				transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction1.addToBackStack(null);
				transaction1.commit();
			}
			});
			
			
			
			tv2.setText(actividades.get(position).get("descripcion"));
			v.setClickable(true);
			v.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {   
				Integer idactividad = Integer.valueOf(actividades.get(position).get("_id"));
				Intent in = new Intent(getActivity(), AddActividadActivity.class);
				in.putExtra("action", "edit");
				in.putExtra("idactividad", actividades.get(position).get("_id"));
				startActivity(in);
			} 
		});
			return v;
		}

		//  @Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		//  @Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG,"en pausa");
		
	}
	@Override
	public void onResume() {
		Log.v(TAG,"Resume eeee... ");
		populate();
//		super.onResume();
		super.onResume();

		
	}
}

