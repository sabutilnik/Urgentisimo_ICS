package uno.Urgentisimo;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class customDialog extends Dialog {
	public static final String TAG = "URGENTISIMO : CUSTOM DIALOG :";
	public static final int ONE_FIELD_DIALOG = 1;
	public static final int DATE_DIALOG = 2;
	public static final int INFO_DIALOG = 3;
	private static final int YES_NO_DIALOG = 4;
	private static final int SPINNER_DIALOG = 5;
	private String titulo;
	private String texto;
	private ArrayList<nombreId> datos;
	private String salida;
	private ReadyListener readyListener;
	private EditText valor;

	private String date;
	private DatePicker dp;
	private ImageButton btOK;
	private ImageButton btCancel;
	private ImageButton imOK;
	private ImageButton imCancel;
	private String respuesta;
	private int tipo;
	
	
/* Use this to initialize a one field dialog:
 * Context
 * Title
 * Current text
 * ArrayList<nombreId> to compare user input
 * ReadyListener 
 * tipo = ONE_FIELD_DIALOG
 */
	public customDialog(Context context, String titulo, String texto, ArrayList<nombreId> datos, ReadyListener readyListener, int tipo)
	{
		super(context);
		this.titulo = titulo;
		this.texto = texto;
		this.datos = datos;
		this.readyListener = readyListener;
		this.tipo = tipo;

	}
	
	/* Use this to initialize a one field dialog:
	 * Context
	 * Title
	 * ReadyListener
	 * Current date 
	 * tipo = DATE_DIALOG
	 */
	
	public customDialog(Context context, String titulo, ReadyListener readyListener, String date, int tipo ) {
		super(context);
		this.tipo = tipo;
		this.titulo = titulo;
		this.date = date;
		this.readyListener = readyListener;
		Log.v(TAG, " get date : "+date);
	}
	
	public customDialog(Context  context, String nombre, String descripcion, int tipo) {
		super(context);
		this.titulo = nombre;
		this.texto = descripcion;
		this.tipo = tipo;
		this.datos = new ArrayList<nombreId>();

	}
	
	public customDialog(Context context, ArrayList<nombreId> lista, String titulo, ReadyListener readyListener, int tipo ) {
		super(context);
		this.titulo = titulo;
		this.datos = lista;
		this.tipo = tipo;
		this.readyListener = readyListener;
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG,"tipo: "+tipo);
		switch (tipo) {
		case ONE_FIELD_DIALOG:
			Log.v(TAG,"one field");
			setContentView(R.layout.dialog_one_field);
			setTitle(titulo);

			TextView tv1 = (TextView) findViewById(R.id.texto);
			tv1.setText(texto);
			valor = (EditText) findViewById(R.id.valor );
			btOK = (ImageButton) findViewById(R.id.btOK);
			btOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					boolean continuar = true;
					if ( !valor.getText().toString().equals("") ) {
						for (int i = 0 ; i < datos.size(); i++) {
							if (datos.get(i).getNombre().equalsIgnoreCase(valor.getText().toString())) {
								valor.setError("ya existe");
								continuar = false;
							}
						}
					} else {
						valor.setError("valor inválido");
						continuar = false;
					}
					if (continuar == true ) {
						Log.v(TAG,"click "+valor.getText().toString());
						readyListener.ready(String.valueOf(valor.getText()));
						customDialog.this.dismiss();
					}
				}
			});
			break;

		case DATE_DIALOG:
			Log.v(TAG," DATE ");
			setContentView(R.layout.date_dialog);
			setTitle(titulo);
			dp = (DatePicker) findViewById(R.id.dpDate);
			//			Log.v(TAG,"fecha recibida "+date);
			try {
				String[] fecha = date.split("-");
				int y = Integer.valueOf(fecha[0]);
				int m = Integer.valueOf(fecha[1])-1;
				int d = Integer.valueOf(fecha[2]);
				Log.v(TAG,"fecha "+y+":"+m+":"+d);
				dp.updateDate(y,m,d);
			} catch (Exception e) {


			}

			btOK = (ImageButton)findViewById(R.id.btOk);
			btOK.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int m = dp.getMonth()+1;
					int y = dp.getYear();
					int d = dp.getDayOfMonth();
					String fecha = String.valueOf(y)+"-"+String.format("%02d",m)+"-"+String.format("%02d",d);
					readyListener.ready(fecha);
					customDialog.this.dismiss();
				}
			});
			break;
		case INFO_DIALOG:
			Log.v(TAG,"INFO");
			setContentView(R.layout.info_dialog);
			setTitle(titulo);
			TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
			tvInfo.setText(texto);
			break;
			
		case YES_NO_DIALOG:
			Log.v(TAG,"YES_NO");
			setContentView(R.layout.yes_no);
			setTitle(titulo);
			respuesta = "0";
			
			imOK = (ImageButton)findViewById(R.id.btOk);
			imOK.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					respuesta = "1";
					readyListener.ready(respuesta);
					customDialog.this.dismiss();
				}
			});
			break;
			
		case SPINNER_DIALOG:
			setContentView(R.layout.spinner_dialog);
			setTitle(titulo);
			final Spinner sp = (Spinner) findViewById(R.id.spinner);
			final SpinAdapter spa = new SpinAdapter(getContext(), android.R.layout.simple_spinner_item, datos);
			sp.setAdapter(spa);
			ImageButton btOK = (ImageButton)findViewById(R.id.btOK);
			btOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					nombreId resp = spa.getItem(sp.getSelectedItemPosition());
					readyListener.ready(resp);
					customDialog.this.dismiss();
				}
			});
			
			break;

		default :
			Log.v(TAG,"no existe el dialogo");
			break;
			

		}
		imCancel = (ImageButton) findViewById(R.id.btCancel);
		imCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				customDialog.this.dismiss();
			}
		});	 
	}       

	private class OKListener implements android.view.View.OnClickListener {
		//    	@Override
		public void onClick(View v) {
			Log.v(TAG,"click "+valor.getText().toString());
			readyListener.ready(String.valueOf(valor.getText()));
			customDialog.this.dismiss();
		}
	}

	public interface ReadyListener {
		public void ready(String salida);

		public void ready(nombreId resp);
	}
}
