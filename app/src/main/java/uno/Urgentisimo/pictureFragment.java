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
import android.os.Environment;
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

public class pictureFragment extends Fragment
{
	protected static final String TAG = "URGENTISIMO : PICTURES FRAGMENT : ";
	GridView MyGrid;
	private View contentView;
	private View containerView;
	private alumnoDetail izquierda;
	private String elid;

	
	private DataBaseHelper db;
	
	public pictureFragment(String elid){
		this.elid = elid;
		  Log.v(TAG,"init DetailFragment");
			db = new DataBaseHelper(getActivity());
		 }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		izquierda = (alumnoDetail) getFragmentManager().findFragmentById(R.id.fragment_container);
		containerView = container;
	    contentView = inflater.inflate(R.layout.pictures_fragment, container, false);
	    populate();
	    return contentView;
	}

	@Override
	public void onDestroy() {        
		super.onDestroy();
		db.close();
	}
		
	public void populate() {
		Log.v(TAG,"metiendo ... ");
		ImageAdapter AlumAdapter = new ImageAdapter(getActivity());
		GridView grid = (GridView) contentView.findViewById(R.id.grid);
		grid.setAdapter(AlumAdapter);

	}
	public void setText(String texto) {
		texto= "aa";
	}
	public class ImageAdapter extends BaseAdapter
	{
		Context MyContext;
		private ArrayList<String> filelist;
		public ImageAdapter(Context _MyContext)
		{
			MyContext = _MyContext;
			filelist = new ArrayList<String>();
			getData();
		}

		//   @Override
		public int getCount() 
		{
			/* Set the number of element we want on the grid */
			return filelist.size();
		}

		private void getData() {
			File urgdir = new File(Environment.getExternalStorageDirectory(), "/Urgentisimo/");
			File[] files = urgdir.listFiles();
			
			
			
			
			for (File lafile : files ) {
				lafile.getAbsolutePath();
				if (lafile.isDirectory() && lafile.getPath().contains("pics")) {
					File picdir = new File(lafile.getAbsolutePath());
					File[] files2 = picdir.listFiles();
					for (File lapic : files2) {
						if (lapic.isFile() && lapic.getPath().endsWith(".png")) {
							filelist.add(lapic.getAbsolutePath());
						}
					}
				}
			}
		}

		//   @Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			View v;
			if(convertView==null) {
				LayoutInflater inflater = (LayoutInflater)MyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.simple_image, parent, false);
			}
			else {
				v = convertView;
			}
			final String picfile = filelist.get(position);
			ImageView ivpic = (ImageView)v.findViewById(R.id.pic);

				File file = new File(picfile);
				if(file.exists()) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bm = BitmapFactory.decodeFile(picfile, options);
					ivpic.setImageBitmap(bm);
				}
				else {
					ivpic.setImageResource(R.drawable.alumno_foto);
				}
		    
		    
			ivpic.setClickable(true);
			ivpic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {   
				Log.v(TAG,"click en file "+picfile);
				izquierda.updatePic(picfile);
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
	
}

