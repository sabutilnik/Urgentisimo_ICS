package uno.Urgentisimo;

import java.util.ArrayList;

import uno.Urgentisimo.customDialog.ReadyListener;

//import uno.Urgentisimo.WidgetsUtils.LinearLayoutSublist;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WidgetsUtils {
	private static final String TAG = "URGENTISIMO : WIDGETUTILS : ";
	private ArrayList<String> actividadesCampos;
	private Context context; 
	private UtilsLib utils;

	private Boolean edicion;

	public WidgetsUtils(Context c) {
		Log.v(TAG,"entro en init0");
		this.context = c;
		this.utils = new UtilsLib(context);
	}
	private ReadyListener readyListener;


	/* *********************************************************************************************
	 *  
	 * 
	 ***********************************************************************************************/
	public View enhacedTextView(Context context, final String text, int fontSize) {
		this.context = context;
		Log.v(TAG,"get enhaced text view ");
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View customView = inflater.inflate(R.layout.actividad_main_item, null);
		TextView tv = (TextView) customView.findViewById(R.id.texto);
		tv.setText(text);
		LinearLayout llsublist = (LinearLayout)customView.findViewById(R.id.sublista);

		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LinearLayout sublist =  (LinearLayout) customView.findViewById(R.id.sublista); 
				LinearLayout sublist1 =  (LinearLayout) customView.findViewById(R.id.sublista1);
				sublist.setVisibility(utils.toggleVisivility(sublist));
				sublist1.setVisibility(utils.toggleVisivility(sublist1));
				TextView plusminus = (TextView) customView.findViewById(R.id.plusminus);
				plusminus.setText(utils.toggleText(sublist.getVisibility()));
			}
		} );
		return customView;
	}

	public View normalListMember(final nombreId data, final MyHandlerInterface readyListener) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View customView = inflater.inflate(R.layout.texto_icono_item, null);
		TextView tv = (TextView) customView.findViewById(R.id.texto);
		tv.setText(data.getNombre());

		ImageView iv = (ImageView) customView.findViewById(R.id.icono);
		iv.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Log.v(TAG,"borrando "+data.getId()+" con id "+data.getId());
				readyListener.delete("blll");
				customView.setVisibility(View.GONE);
			}
		});
		return customView;
	}

	public class TextViewSubList extends LinearLayout {
		public String text;
		private int tipo;
		public Boolean edicion;
		public Boolean hidden;
		private View customView;
		public TableLayout subtabla;
		public LinearLayout sublista;
		public TextView tv;
		public EditText et;

		MyHandlerInterface elListener;

		public TextViewSubList(Context context) {
			super(context);
		}
		public TextViewSubList(Context context, String text, int tipo, MyHandlerInterface readyListener, Boolean edicion, Boolean hidden ) {
			super(context);
			this.text = text;
			this.tipo = tipo;
			this.edicion = edicion;
			this.hidden = false;
			elListener = readyListener;
			this.display();
		}
		public void setEdicion() {
			if (edicion == true ) {
				customView.findViewById(R.id.save).setVisibility(View.VISIBLE);
				customView.findViewById(R.id.edit).setVisibility(View.GONE);
				customView.findViewById(R.id.texto).setVisibility(View.GONE);
				customView.findViewById(R.id.etTexto).setVisibility(View.VISIBLE);
				hidden = false;
			} else {
				customView.findViewById(R.id.save).setVisibility(View.GONE);
				customView.findViewById(R.id.edit).setVisibility(View.VISIBLE);
				customView.findViewById(R.id.texto).setVisibility(View.VISIBLE);
				customView.findViewById(R.id.etTexto).setVisibility(View.GONE);
				hidden = true;
			}
			setSubListVisibility();
		}
		public void setSubListVisibility() {
			Log.v(TAG," hidden "+hidden);
			LinearLayout sublist =  (LinearLayout) customView.findViewById(R.id.sublista); 
			LinearLayout sublist1 =  (LinearLayout) customView.findViewById(R.id.sublista1);
			TextView plusminus = (TextView) customView.findViewById(R.id.plusminus);
			if (hidden == true) {
				sublist.setVisibility(View.GONE);
				sublist1.setVisibility(View.GONE);

			} else {
				sublist.setVisibility(View.VISIBLE);
				sublist1.setVisibility(View.VISIBLE);
			}
			plusminus.setText(utils.toggleText(sublist.getVisibility()));
		}

		public void display() {
			this.removeAllViews();
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			customView = inflater.inflate(R.layout.actividad_main_item, null);
			tv = (TextView) customView.findViewById(R.id.texto);
			tv.setText(text);
			et = (EditText) customView.findViewById(R.id.etTexto);
			et.setText(text);
			this.setEdicion();
			this.setSubListVisibility();
			tv.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG,"changehidden");
					hidden = !hidden;
					setSubListVisibility();
				}
			} );	
			if (edicion == true) {
				tv.setVisibility(View.GONE);
				et.setVisibility(View.VISIBLE);
			} else {
				tv.setVisibility(View.VISIBLE);
				et.setVisibility(View.GONE);
			}

			tv.setOnLongClickListener(new View.OnLongClickListener() {		
				public boolean onLongClick(View v) {
					Log.v(TAG,"add new "+text);
					elListener.add("eoos");
					return false;
				}
			});
			customView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					elListener.editar("blaaa ");
				}
			});
			customView.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					elListener.save("blu");
				}
			});	
			this.addView(customView);
		}

		public void setEdit(boolean b) {
			this.edicion = b;
		}
	}
	public class EditableRow extends TableRow {
		public String text;
		public String value;
		public Boolean edicion;
		public TableRow campovalor;
		public TextView valor;
		public TextView campo;
		public EditText etvalor;
		public DatePicker dpvalor;
		public Spinner spvalor;
		public String elemento;
		public ArrayList<nombreId> data;
		public SpinAdapter spa;
		public nombreId selectedItem;

		public int tipo;

		public EditableRow(Context context, String text, String value, String elemento, Boolean edicion, int tipo) {
			super(context);
			this.text = text;
			this.value = value;
			this.edicion = edicion;
			this.tipo = tipo;
			this.elemento = elemento;
			this.data = new ArrayList<nombreId>();
			this.display();
		}

		public EditableRow(Context context, String text, String value, String elemento, Boolean edicion, int tipo, ArrayList<nombreId> data) {
			super(context);
			this.text = text;
			this.value = value;
			this.edicion = edicion;
			this.tipo = tipo;
			this.elemento = elemento;
			this.data = data;
			this.display();
		}

		public void  update() {
			if (tipo == utils.constants.ONE_FIELD_DIALOG) {
				this.value = etvalor.getText().toString();
			}
			if (tipo == utils.constants.DATE_DIALOG) {
				this.value = getDate(dpvalor);
			}
			if (tipo == utils.constants.SPINNER_DIALOG) {
				nombreId x =(nombreId) spvalor.getSelectedItem();
				this.value = x.getNombre();
			}
			this.valor.setText(this.value);
		}
		public void display() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			campovalor = (TableRow) inflater.inflate(R.layout.campo_valor, null);
			campo = (TextView)campovalor.findViewById(R.id.campo);
			valor = (TextView)campovalor.findViewById(R.id.valor);
			campo.setText(text);
			valor.setText(value);
			etvalor = (EditText)campovalor.findViewById(R.id.etvalor);
			dpvalor = (DatePicker)campovalor.findViewById(R.id.dpvalor);
			spvalor = (Spinner)campovalor.findViewById(R.id.spvalor);
			spa = new SpinAdapter(getContext(), android.R.layout.simple_spinner_item, data);
			spvalor.setAdapter(spa);
			setValor(value);
			this.setEdicion();
			this.addView(campovalor);
		}
		public void setEdicion() {
			if (edicion == true ) {
				if (tipo == utils.constants.ONE_FIELD_DIALOG) {
					etvalor.setVisibility(View.VISIBLE);
				}
				if (tipo == utils.constants.DATE_DIALOG) {
					dpvalor.setVisibility(View.VISIBLE);
				}
				if (tipo == utils.constants.SPINNER_DIALOG) {
					spvalor.setVisibility(View.VISIBLE);
				}
				valor.setVisibility(View.GONE);
			} else {
				etvalor.setVisibility(View.GONE);
				dpvalor.setVisibility(View.GONE);
				spvalor.setVisibility(View.GONE);
				valor.setVisibility(View.VISIBLE);
			}
		}
		public void setValor(String val) {
			switch(tipo) {
			case 1: //ONE_FIELD_DIALOG
				etvalor.setText(val);
				break;
			case 2: //DATE_DIALOG
				int y = Integer.valueOf(val.split("-")[0]);
				int m = Integer.valueOf(val.split("-")[1])-1;
				int d = Integer.valueOf(val.split("-")[2]);
				try {
					dpvalor.init(y,m,d,null);
				}
				catch (Exception e) {
					Log.e(TAG, " exception ",e);
				}
				break;
			case 5://SPINNER_DIALOG
				int pos = spa.getIdPosition(val);
				spvalor.setSelection(pos);
				//				Log.v(TAG,"set spinner at position "+pos);
				selectedItem = spa.getItem(pos);
				valor.setText(selectedItem.getNombre());
				break;
			}
		}
	}
	
//	public class ListViewGroup {
//		private Context context;
//		
//		public ListViewGroup(Context context) {
//			super(context);
//		}
//		public ListViewGroup(Context context, String element,  <ArrayList> data) {
//			super(context);
//			this
//		}
//		
//	}

	public interface MyHandlerInterface
	{
		public void delete(String salida);
		public void add(String salida);
		public void editar(String salida);
		public void save(String salida);
	}
	public String getDate(DatePicker dp) {
		String date = String.format("%02d", dp.getYear());
		date = date+"-"+String.format("%02d",dp.getMonth()+1);
		date = date+"-"+String.format("%02d", dp.getDayOfMonth());
		return date;
	}
}