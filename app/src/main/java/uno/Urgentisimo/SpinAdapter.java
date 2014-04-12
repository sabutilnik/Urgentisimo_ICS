package uno.Urgentisimo;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinAdapter extends ArrayAdapter<nombreId>{
    private static final String TAG = "URGENTISIMO : SPINADAPTER ";
	private Context context;
    private ArrayList<nombreId> values;
    

    public SpinAdapter(Context context, int textViewResourceId,
            ArrayList<nombreId> val) {
        super(context, textViewResourceId, val);
        this.context = context;
        this.values = val;
//        Log.v(TAG,"new spinadapter "+val.size());
    }

    public int getCount(){
//    	Log.v(TAG,"getcount "+values.size());
       return values.size();
    }

    public nombreId getItem(int position){
    	nombreId tmp = values.get(position);
    	Log.v(TAG,tmp.getNombre()+" "+tmp.getId()+" at position "+position);
       return values.get(position);
    }

    public long getItemId(int position){
       return position;
    }
    public String getDataId(int position) {
    	Log.v(TAG,"getData "+position);
    	for (int i = 0; i< values.size(); i++ ) {
    		Log.v(TAG,"values at "+i+" has value "+values.get(i).getNombre()+" con id "+values.get(i).getId());
    	}
//    	nombreId tmp = values.get(position);    	
//    	Log.v(TAG,"getdataid")
    	return values.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	TextView label = (TextView) inflater.inflate(R.layout.spinner_text_item, null);
//        TextView label = new TextView(context);
        label.setText(values.get(position).getNombre());
//        Log.v(TAG,"entro en getview en posicion "+position);
        return label;
    }
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
//        TextView label = new TextView(context); 
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	TextView label = (TextView) inflater.inflate(R.layout.spinner_text_item, null);
        label.setText(values.get(position).getNombre());
        return label;
    }

	public int getIdPosition(String data) {
		for (int i = 0; i < values.size(); i++) {
			Log.v(TAG,"getIdPosition "+data+", tet at position "+i);
			if (values.get(i).getId().equalsIgnoreCase(data)) {
				return i;
			}
		}
		return 0;
	}
}