package uno.Urgentisimo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CalendarView;
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

public class calendarFragment extends Fragment implements OnDateChangedListener {
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
    private CalendarView calendario;
    private UtilsLib utilslib;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.context = getActivity();
        this.utilslib = new UtilsLib(this.context);

	}

	public calendarFragment() {
		Log.v(TAG, "init CALENDAR FRAGMENT... ");
		this.fecha = fecha;
		this.title = title;
		db = new DataBaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		contentView = inflater.inflate(R.layout.calendario, container,
				false);
        calendario = (CalendarView) contentView.findViewById(R.id.calendario);
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                Log.v(TAG,"le dio a "+dayOfMonth);
                Log.v(TAG,"le dio a "+year);
                Log.v(TAG,"le dio a "+month);
                utilslib.insertActividad(year, month, dayOfMonth);
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