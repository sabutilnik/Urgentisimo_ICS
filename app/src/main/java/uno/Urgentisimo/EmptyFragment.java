package uno.Urgentisimo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import uno.Urgentisimo.AddActividadActivity.OnReadyListener;
import uno.Urgentisimo.DetailFragment.ImageAdapter;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EmptyFragment extends Fragment {
	protected static final String TAG = "URGENTISIMO : EVAL FRAGMENT : ";
	private static final int OPT1 = 0;
	private static final int OPT2 = 1;
	GridView MyGrid;
	private ArrayList<nombreId> listado;
	private ArrayList<nombreId> grupos;
	private Map<String, Map<String, String>> criteriosactividad;
	private View containerView;
	private View contentView;
	private String idactividad;
	private Map<String, String> actividad;
	private String actividadname;
	private String laquery;
	private MenuInflater mi;
	private String ordenar;
	private ArrayList<String> ordenarpor;
	private String subtabla;
	private String fecha;
	private UtilsLib utils;
	private int elcolor;

	private DataBaseHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		this.utils = new UtilsLib(getActivity());

	}

	public EmptyFragment() {
		Log.v(TAG, "init EvalFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater
				.inflate(R.layout.empty_fragment, container, false);
	
		return contentView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void populate(String idgrupo) {
		// this.getlistado();


	}



}
