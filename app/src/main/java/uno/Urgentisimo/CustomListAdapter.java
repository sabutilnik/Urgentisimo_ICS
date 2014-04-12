package uno.Urgentisimo;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import uno.Urgentisimo.checkboxAdapter.ViewHolder;

public class CustomListAdapter extends BaseAdapter {

	protected static final String TAG = "URGENTISIMO : CUSTOMLISTADAPTER : ";
	private Context context;
	private LayoutInflater inflater;

	private ArrayList<Map<String, String>> items;
	private String filtro;
	private String nombre;
	boolean[] checkBoxState;

	ViewHolder viewHolder;

	private ArrayList<Map<String, String>> allitems;

	public CustomListAdapter(Context context,
			ArrayList<Map<String, String>> todos, String nombre) {
		this.items = todos;
		this.allitems = todos;
		this.context = context;
		this.nombre = nombre;
		checkBoxState = new boolean[items.size()];
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for (int j = 0; j < items.size(); j++) {
			if (items.get(j).get("checked").equalsIgnoreCase("1")) {
				checkBoxState[j]=true; 
			} else {
				checkBoxState[j]=false;
			}
		}
		Log.v(TAG,"inicializo adapter");
	}

	// @Override
	// counts the number of group/parent items so the list knows how many times
	// calls getGroupView() method
	public int getGroupCount() {
		return items.size();
	}

	public long getGroupId(int i) {
		return i;
	}

	// @Override
	public long getChildId(int i, int i1) {
		return i1;
	}

	// @Override
	public boolean hasStableIds() {
		return true;
	}

	private class ViewHolder {
		TextView tv;
		CheckBox ck;
		CheckableLinearLayout ll;
	}

	// @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.simple_text_ck, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.tv = (TextView) convertView.findViewById(R.id.tvCampo);
			viewHolder.ck = (CheckBox) convertView.findViewById(R.id.ckmiembro);
			viewHolder.ll = (CheckableLinearLayout) convertView
					.findViewById(R.id.layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> temporal = items.get(position);
		String texto = items.get(position).get(nombre).toString();
		viewHolder.tv.setText(texto);
		
		
		viewHolder.tv.setBackgroundColor(this.getBackgroundColor(position));
		
		if (items.get(position).get("checked").equalsIgnoreCase("1")) {
			viewHolder.ll.setBackgroundColor(R.drawable.white_border);
		}
		


		viewHolder.ck.setChecked(checkBoxState[position]);

		viewHolder.ck.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					checkBoxState[position] = true;
					items.get(position).put("checked","1");
				}
				else {
					checkBoxState[position] = false;
					items.get(position).put("checked","0");
				}
				notifyDataSetChanged();
			}
		});

		return convertView;

	}

	// @Override
	public boolean isChildSelectable(int i, int i1) {
		return true;
	}

	// @Override
	public void registerDataSetObserver(DataSetObserver observer) {
		/* used to make the notifyDataSetChanged() method work */
		super.registerDataSetObserver(observer);
	}

	public void filterResults(String filter) {
		ArrayList<Map<String, String>> filtered = new ArrayList<Map<String, String>>();
		for (int i = 0; i < allitems.size(); i++) {
			Map<String, String> elemento = allitems.get(i);

			if (elemento.get(nombre).toLowerCase()
					.contains(filtro.toLowerCase())
					|| filtro.equals("")) {
				filtered.add(elemento);
			}
		}
		this.items = filtered;
		notifyDataSetChanged();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getBackgroundColor(int k) {
		Log.v(TAG," get back "+k+" " + items.get(k).get("checked"));
		if (items.get(k).get("checked").equalsIgnoreCase("")) {
			return R.drawable.white_border;
		} else {
		return R.drawable.add_s;
		}
	}

}