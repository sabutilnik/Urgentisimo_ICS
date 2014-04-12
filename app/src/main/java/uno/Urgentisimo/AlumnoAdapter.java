package uno.Urgentisimo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

	public class AlumnoAdapter extends BaseAdapter {
		 private Context mContext;
		 private String[] nombres;

		 // Gets the context so it can be used later
		 public AlumnoAdapter(Context c) {
		  mContext = c;
		 }

		 // Total number of things contained within the adapter
		 public int getCount() {
		  return nombres.length;
		 }

		  // Require for structure, not really used in my code.
		 public Object getItem(int position) {
		  return null;
		 }

		 // Require for structure, not really used in my code. Can
		 // be used to get the id of an item in the adapter for
		 // manual control.
		 public long getItemId(int position) {
		  return position;
		 }

		 public View getView(int position,
		                           View convertView, ViewGroup parent) {
			 
//	          TextView tv = (TextView) customView.findViewById(R.id.nombre_alumno);
//	          
//	          tv.setText(nombre);
//
//	          ImageView image = (ImageView) findViewById(R.id.alumno_foto);
//	          customView.setClickable(true);
//	          
//	          customView.setOnClickListener(new View.OnClickListener() {
//	              public void onClick(View arg0) {
//	            	  
//	                  Intent in = new Intent(studentsActivity.this, StudentShowActivity.class);
//	                  in.putExtra("idalumno", String.valueOf(i));
//	                  startActivity(in);
//	            	  Log.v(TAG," click en id "+ String.valueOf(i)); 
//	              } 
//	           }); 
//			 
//		  Button btn;
//		  if (convertView == null) {
//		   // if it's not recycled, initialize some attributes
//		   btn = new Button(mContext);
//		   btn.setLayoutParams(new GridView.LayoutParams(100, 55));
//		   btn.setPadding(8, 8, 8, 8);
//		   }
//		  else {
//		   btn = (Button) convertView;
//		  }
//		  exus
//		  btn.setText(filesnames[position]);
//		  // filenames is an array of strings
//		  btn.setTextColor(Color.WHITE);
//		  btn.setBackgroundResource(R.drawable.button);
//		  btn.setId(position);

		  return null;
		 }
		}
