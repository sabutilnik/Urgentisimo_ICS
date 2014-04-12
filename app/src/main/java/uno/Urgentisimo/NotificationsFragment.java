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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;


public class NotificationsFragment extends Fragment {
    protected static final String TAG = "URGENTISIMO : NOTIFICATIONS FRAGMENT : ";
    private View contentView;
    private UtilsLib utils;
    private DataBaseHelper db;
    private LinearLayout historial;
    private TextView mensaje;
    private String archivo;
    private ImageView abrir;
    private LinearLayout botones;
    private LinearLayout notificaciones;

    private EvalFragment evaluacion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.utils = new UtilsLib(getActivity());
        this.archivo = "";

    }

    public NotificationsFragment() {
        Log.v(TAG, "init NotificationsFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater
                .inflate(R.layout.notifications_fragment, container, false);
        this.mensaje = (TextView)contentView.findViewById(R.id.mensaje);
        this.abrir = (ImageView) contentView.findViewById(R.id.action);
        this.abrir.setClickable(true);
        this.abrir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG,"click en abrir ");
                if (archivo.length() > 0) {
                    openfile();
                };
            }
        });
        this.botones = (LinearLayout) contentView.findViewById(R.id.botones);
        this.hideBotones();
        this.notificaciones = (LinearLayout) contentView.findViewById(R.id.notifications);
        return contentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void mensaje(String msg) {
        this.mensaje.setText(msg);
        Log.v(TAG,msg);
    }

    public void showNotificciones() {
        this.notificaciones.setVisibility(View.VISIBLE);
    }
    public void hideNotificaciones() {
        this.notificaciones.setVisibility(View.GONE);
    }
    public void showBotones() {
        this.botones.setVisibility(View.VISIBLE);
    }
    public void hideBotones() {
        this.botones.setVisibility(View.GONE);
    }

    public void setarchivo (String archivo) {
        this.archivo = archivo;
    }

    public void openfile() {
        File elarchivo = new File(archivo);
        Uri path = Uri.fromFile(elarchivo);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.v(TAG,"No Application Available to View Excel");
        }
    }

    public void initBotones() {
        ImageButton diez = (ImageButton)this.botones.findViewById(R.id.b10);
        ImageButton veinte = (ImageButton)this.botones.findViewById(R.id.b20);
        ImageButton treinta = (ImageButton)this.botones.findViewById(R.id.b30);
        ImageButton cuarenta = (ImageButton)this.botones.findViewById(R.id.b40);
        ImageButton cincuenta = (ImageButton)this.botones.findViewById(R.id.b50);
        ImageButton cinco = (ImageButton)this.botones.findViewById(R.id.b5);

        this.evaluacion = (EvalFragment) getFragmentManager().findFragmentById(R.id.fragment_container2);
        buttonExtra(diez,10);
        buttonExtra(veinte,20);
        buttonExtra(treinta,30);
        buttonExtra(cuarenta,40);
        buttonExtra(cincuenta,50);
        buttonExtra(cinco,5);
    }

    private void buttonExtra(ImageButton button,final int val) {
        button.setClickable(true);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                evaluacion.send(val);
            }
        });
    }

}
