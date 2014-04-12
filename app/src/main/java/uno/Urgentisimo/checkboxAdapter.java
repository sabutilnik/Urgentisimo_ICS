package uno.Urgentisimo;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

	class checkboxAdapter extends BaseAdapter {
		private static final String TAG = "URGENTISIMO : CHECKBOXADAPTER : ";
		private ArrayList<nombreId> items;
		Context MyContext;
		private LayoutInflater mInflater;

		public checkboxAdapter(Context context, int textViewResourceId,
				ArrayList<nombreId> items) {
			super();
			MyContext = context;
			this.items = items;
			mInflater = (LayoutInflater) MyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			final nombreId item = items.get(position);
			String text = items.get(position).getNombre();
			Boolean checked = false;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.text_checkbox_item  , null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.ckbox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			    {
			        if ( isChecked )
			        {
			            item.setDescripcion("1");
			        } else {
			        	item.setDescripcion("0");
			        }
			    }
			});

			if (item.getDescripcion() == "1") {
				checked = true;
			}			
			holder.text.setText(text);
			holder.checkbox.setChecked(checked);
			return convertView;
		}

		// @Override
		public nombreId getItem(int arg0) {
			return items.get(arg0);
		}

		// @Override
		public long getItemId(int arg0) {
			return Integer.valueOf(items.get(arg0).getId());
		}

		// @Override
		public int getCount() {
//			Log.v(TAG,"get count "+items.size());
			return items.size();
		}
		
		public void addItem(nombreId item) {
	         items.add(item);
	         notifyDataSetChanged();
		}

		public void updateValItem(int item, String val, String index) {
			
		}
		
		public ArrayList<nombreId> getSelected() {
			ArrayList<nombreId> result = new ArrayList<nombreId>();
			for (int i = 0; i< items.size(); i++) {
				nombreId element = items.get(i);
				if (element.getDescripcion() == "1") {
					result.add(element);
				}
			}
			return result;
		}
		
		public static class ViewHolder {
	        public TextView text;
	        public CheckBox checkbox;
	    }
		
	}
	

