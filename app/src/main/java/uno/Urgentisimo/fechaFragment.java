package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import uno.Urgentisimo.AddActividadActivity.OnReadyListener;
//import uno.Urgentisimo.EditarTabla.Parent;

import java.util.Map;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class fechaFragment extends Fragment implements OnDateChangedListener {
	protected static final String TAG = "URGENTISIMO : FECHA FRAGMENT : ";

	private Context context;

	// public ArrayList<actividadInfo> actividades;
	private Integer year;
	private Integer month;
	private Integer dayOfMonth;
	private String fecha;
	private String title;
	private View contentView;
	private DatePicker dp;
	private DataBaseHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();

	}

	public fechaFragment(String title, String fecha) {
		Log.v(TAG, "init Fecha FRAGMENT... ");
		this.fecha = fecha;
		this.title = title;
		try {
			String[] separated = fecha.split("-");
			this.year = Integer.valueOf(separated[0]);
			this.month = Integer.valueOf(separated[1]) - 1;
			this.dayOfMonth = Integer.valueOf(separated[2]);
		} catch (Exception e) {
			this.year = 2013;
			this.month = 1;
			this.dayOfMonth = 1;
		}
		db = new DataBaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		contentView = inflater.inflate(R.layout.fecha_fragment, container,
				false);
		dp = (DatePicker) contentView.findViewById(R.id.fecha);
		// dp.init(year, month, dayOfMonth, onDateChangedListener)
		dp.init(year, month, dayOfMonth, this);

		// dp.updateDate(year, month, dayOfMonth);

		TextView titulo = (TextView) contentView.findViewById(R.id.title);
		titulo.setText(this.title);

//		ImageView set = (ImageView) contentView.findViewById(R.id.accept);
		ImageView cancel = (ImageView) contentView.findViewById(R.id.cancel);

//		set.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				year = dp.getYear();
//				month = dp.getMonth() + 1;
//				dayOfMonth = dp.getDayOfMonth();
//				fecha = String.format("%4d-%02d-%02d", year, month, dayOfMonth);
//				Log.v(TAG, title + " " + fecha);
//				ActividadFragment fr = (ActividadFragment) getFragmentManager()
//						.findFragmentById(R.id.fragment_container);
//				fr.updateFragment(title, fecha);
//			}
//		});

		cancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Fragment frag = new EmptyFragment();
				FragmentTransaction transaction1 = getFragmentManager()
						.beginTransaction();

				transaction1.replace(R.id.fragment_container2, frag);
				transaction1.addToBackStack(null);
				transaction1.commit();
				ActividadFragment fr = (ActividadFragment) getFragmentManager()
						.findFragmentById(R.id.fragment_container);
				// TODO Auto-generated method stub

			}
		});
		return contentView;
	}

	public void clearFragment() {
		Fragment frag = new EmptyFragment();
		FragmentTransaction transaction1 = getFragmentManager()
				.beginTransaction();
		transaction1.replace(R.id.fragment_container2, frag);
		transaction1.addToBackStack(null);
		transaction1.commit();
		ActividadFragment fr = (ActividadFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		year = dp.getYear();
		month = dp.getMonth() + 1;
		dayOfMonth = dp.getDayOfMonth();
		fecha = String.format("%4d-%02d-%02d", year, month, dayOfMonth);
		Log.v(TAG, title + " " + fecha);
		ActividadFragment fr = (ActividadFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container);
		fr.updateFragment(title, fecha);

		// TextView txt=(TextView)findViewById(R.id.txt);

		Log.v(TAG,
				"You selected " + view.getDayOfMonth() + "/"
						+ (view.getMonth() + 1) + "/" + view.getYear());
	}

}