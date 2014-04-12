package uno.Urgentisimo;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
 
import java.util.ArrayList;

public class MyCustomAdapter extends BaseExpandableListAdapter {
 
	protected static final String TAG = "URGENTISIMO : MYCUSTOMADAPTER : ";
	private Context context;
    private LayoutInflater inflater;
    private ArrayList<Parent> mParent;
 
    public MyCustomAdapter(Context context, ArrayList<Parent> parent){
        mParent = parent;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
  
//    @Override
    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
    public int getGroupCount() {
        return mParent.size();
    }

    public long getGroupId(int i) {
        return i;
    }
 
//    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }
 
//    @Override
    public boolean hasStableIds() {
        return true;
    }
 
//    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
    	View v;
        if (view == null) {
            v = inflater.inflate(R.layout.list_item_parent, viewGroup,false);
        }  else {
        	v = view;
        }

        TextView tv = (TextView) v.findViewById(R.id.title1);        
        TextView tv1 = (TextView) v.findViewById(R.id.list_item_text_view);

        tv.setText(getGroup(i).toString());
        tv1.setText(getTitle2(i).toString()); 

        return v;
    }
 
//    @Override
    //in this method you must set the text to see the children on the list
    public View getChildView(final int i, final int i1, final boolean b,  View view, final ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_child, viewGroup,false);
        }
 
        TextView textView = (TextView) view.findViewById(R.id.list_item_text_child);

        textView.setText(mParent.get(i).getArrayChildren().get(i1));
        
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Parent tempo = mParent.get(i);
				tempo.setTitle(tempo.getTitle(),tempo.getArrayChildren().get(i1) );
				Log.v(TAG,"le dio a "+mParent.get(i).getArrayChildren().get(i1));
				notifyDataSetChanged();
			}
		});
        
        

        return view;
    }
 
//    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
 
//    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        /* used to make the notifyDataSetChanged() method work */
        super.registerDataSetObserver(observer);
    }


	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}


	public int getChildrenCount(int i) {
		// TODO Auto-generated method stub
		return mParent.get(i).getArrayChildren().size();
	}


	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mParent.get(groupPosition).getTitle();

	}
	public Object getTitle2(int groupPosition) {
		return mParent.get(groupPosition).getDesc();
	}

}