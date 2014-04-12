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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : STUDENTS ACTIVITY : ";
	GridView MyGrid;
	private ArrayList<Map<String, String>> alumnos;
	private View containerView;
	private View contentView;
	private ImageAdapter AlumAdapter;

	private DataBaseHelper db;

	public DetailFragment() {
		Log.v(TAG, "init DetailFragment");
		db = new DataBaseHelper(getActivity());
		alumnos = db.getMapList("select * from alumnos");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		getActivity().setTitle("Urgentisimo -> Alumnos");
		Fragment empty = new EmptyFragment();
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		transaction1.replace(R.id.fragment_container2, empty);
		transaction1.addToBackStack(null);
		transaction1.commit();

		containerView = container;
		contentView = inflater.inflate(R.layout.students, container, false);
		populate();
		return contentView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	public void populate() {
		// setContentView(R.layout.students);
		Log.v(TAG, "populando");
		AlumAdapter = new ImageAdapter(getActivity());
		ListView grid = (ListView) contentView.findViewById(R.id.grid);
		grid.setAdapter(AlumAdapter);
		
	}

	public void setText(String texto) {
		texto = "aa";
	}

	public class ImageAdapter extends BaseAdapter {
		Context MyContext;

		// Log.v(TAG," ccc ");
		public ImageAdapter(Context _MyContext) {
			MyContext = _MyContext;
		}

		// @Override
		public int getCount() {
			/* Set the number of element we want on the grid */
			return alumnos.size();
		}

		public String getId(int position) {
			return alumnos.get(position).get("_id");
		}

		public String getNombre(int position) {
			return alumnos.get(position).get("nombre");
		}

		// @Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) MyContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater
						.inflate(R.layout.curso_list_alumnos, parent, false);
			} else {
				v = convertView;
			}
			final alumnoInfo alumno = new alumnoInfo(db,alumnos.get(position).get("_id"));
			TextView tvnombre = (TextView) v.findViewById(R.id.nombre_alumno);
			tvnombre.setText(alumno.getnombre());

			ImageView alumnofoto = (ImageView) v.findViewById(R.id.foto_alumno);
			
			
			String picFile = alumno.getdata("pic");
			if (picFile == null) {
				picFile = "";
			}
			Log.v(TAG, "foto en " + picFile);
			File file = new File(picFile);
			if (file.exists()) {
				Log.v(TAG, "alumnofoto " + picFile);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(picFile, options);
				alumnofoto.setImageBitmap(bm);
			} else {
				alumnofoto.setImageResource(R.drawable.alumno_foto);
			}
			
			ImageView editar = (ImageView)v.findViewById(R.id.edit);
			v.setClickable(true);
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					editaralumno(alumno.getId());
				}
			});

			editar.setClickable(true);
			editar.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					editaralumno(alumno.getId());
				}
			});
			
			TextView email = (TextView) v.findViewById(R.id.email);
			email.setText(alumno.getdata("email"));			
			TextView padres = (TextView) v.findViewById(R.id.padres);
			padres.setText(alumno.getdata("madre"));
			TextView grupos = (TextView) v.findViewById(R.id.grupos);
			grupos.setText(alumno.getgruposstring());
			
			
			return v;
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

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "en pausa");

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.alumnos_list_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int ordenar = item.getItemId();
		Log.v(TAG, "seleccionada opcion" + ordenar);
		// this.populate();

		switch (item.getItemId()) {

		case R.id.edit_tabla_editar:
			refresh();
			break;
		case R.id.edit_tabla_nuevo:
			refresh();
			break;
		}
		return false;
	}

	public void refresh() {

	}

	public void updatePicture(String elid, String picfile) {
		Log.v(TAG,"recibido "+elid+" picfile "+picfile);
		for (int i=0; i<alumnos.size(); i++) {
			Map<String,String> alumno = alumnos.get(i);
			if (alumno.get("_id")==elid) {
				alumno.put("pic",picfile);
			}
		}
		AlumAdapter.notifyDataSetChanged();
	}
	
	public void reporte(String idalumno) {
		Fragment reportes = new alumnoReportFragment(idalumno);
		FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
		transaction1.replace(R.id.fragment_container, reportes);
		transaction1
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	transaction1.addToBackStack(null);
		transaction1.commit();
	}
	
	public void editaralumno(String idalumno) {
		Log.v(TAG, "click en alumno");

		Fragment editalumno = new alumnoDetail(idalumno, 0, alumnos);
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		Log.v(TAG, "metiendo reportes ");
		transaction1.replace(R.id.fragment_container, editalumno);
		transaction1
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction1.addToBackStack(null);
		transaction1.commit();
	}
}
