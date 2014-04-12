package uno.Urgentisimo;
import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.TextView;

public class studentsActivity extends Fragment
{
	protected static final String TAG = "URGENTISIMO : STUDENTS ACTIVITY : ";
	GridView MyGrid;
	private ArrayList<String> listado;
	private View contentView;

	private DataBaseHelper db;
	public void onCreate(Bundle icicle)
	{
		Log.v("eooo dos tres",null);
		super.onCreate(icicle);
		db = new DataBaseHelper(getActivity());
		listado = new ArrayList<String>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v("eooo dos tres cuatro",null);
	    contentView = inflater.inflate(R.layout.students, container, false);
	    return contentView;
	}

	@Override
	public void onDestroy() {        
		super.onDestroy();
		db.close();
	}
		
	public void populate() {
//		setContentView(R.layout.students);  
		ImageAdapter AlumAdapter = new ImageAdapter(getActivity());
		GridView grid = (GridView) contentView.findViewById(R.id.grid);
		grid.setAdapter(AlumAdapter);

	}
	public class ImageAdapter extends BaseAdapter
	{
		Context MyContext;
		private ArrayList<String> nombres = new ArrayList<String>();
		private ArrayList<String> idlist = new ArrayList<String>();

		public ImageAdapter(Context _MyContext)
		{
			MyContext = _MyContext;
			getData();
		}

		//   @Override
		public int getCount() 
		{
			/* Set the number of element we want on the grid */
			return nombres.size();
		}

		private void getData() {
			Cursor dt = db.getAlumnosList();
			if (dt.moveToFirst()) {
			do {
				nombres.add(dt.getString(dt.getColumnIndex("nombre"))+" "+dt.getString(dt.getColumnIndex("apellidos")));
				idlist.add(dt.getString(dt.getColumnIndex("_id")));
				listado.add(dt.getString(dt.getColumnIndex("_id")));
//				Log.v(TAG," metiendo "+dt.getString(1)+" "+dt.getInt(0));

			}while (dt.moveToNext());
			}
			dt.close();
		}

		public void addNombre (String n, String id) {
			nombres.add(n);
			idlist.add(id);
		}

		public String getId (int position) {
			return idlist.get(position).toString();
		}
		public String getNombre (int position) {
			return nombres.get(position).toString();
		}

		//   @Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			View v;
			if(convertView==null) {
				LayoutInflater inflater = (LayoutInflater)MyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.curso_list_alumnos, parent, false);
			}
			else {
				v = convertView;
			}

			TextView tv = (TextView)v.findViewById(R.id.nombre_alumno);
			Log.v(TAG,"inflating 2 ... "+nombres.get(position).toString()+" posicion : "+position);
			tv.setText(nombres.get(position).toString());

			ImageView alumnofoto = (ImageView)v.findViewById(R.id.foto_alumno);
//			iv.setImageResource(R.drawable.alumno_foto);
			String picFile = "/sdcard/Urgentisimo/pics/"+idlist.get(position).toString()+".jpg";
				File file = new File(picFile);
				if(file.exists()) {
					Log.v(TAG,"alumnofoto "+picFile);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bm = BitmapFactory.decodeFile(picFile, options);
//					bm = Bitmap.createScaledBitmap(bm, width, height, false);
					alumnofoto.setImageBitmap(bm);
//					alumnofoto.setImageBitmap(bm);
				}
				else {
					alumnofoto.setImageResource(R.drawable.alumno_foto);
				}

			v.setClickable(true);
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
//	@Override
//	public void onRestart() {
//		super.onRestart();
//		this.populate();
//		Log.v(TAG,"on restart");
//	}
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG,"en pausa");
		
	}
}

