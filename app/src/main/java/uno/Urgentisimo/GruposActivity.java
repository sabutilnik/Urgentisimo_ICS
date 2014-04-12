package uno.Urgentisimo;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GruposActivity extends Activity implements OnLongClickListener,OnClickListener

{
	protected static final String TAG = "URGENTISIMO : GRUPOS ACTIVITY : ";
	private static gruposInfo grupoGlobal = new gruposInfo();
	private DataBaseHelper db;
	private ArrayList<gruposInfo> grupos;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		db = new DataBaseHelper(this);

		setContentView(R.layout.grupos_activity);
		
//		final ProgressDialog pd = ProgressDialog.show(this,
//				"Title",
//				"Message",
//				true, false);
//
//				new Thread(new Runnable(){
//				public void run(){
//				fillGrupos();
//				pd.dismiss();
//				}
//				}).start();
//		
		fillGrupos();

	}

	
	public void fillGrupos() {
		
		LinearLayout gruposListado = (LinearLayout)findViewById(R.id.gruposListado);
		LayoutInflater lgruposListado = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		gruposInfo grupo = new gruposInfo();
		grupos = new ArrayList<gruposInfo>();
		
//		final ProgressDialog pd = ProgressDialog.show(this,
//				"Title",
//				" ...... ",
//				true, false);
//
//				new Thread(new Runnable(){
//				public void run(){
					grupos = db.getGruposInfo();
//				pd.dismiss();
//				}
//				}).start();

		
//		ArrayList<gruposInfo> grupos = db.getGruposInfo();

		for (int i = 0; i < grupos.size() ; i++ ) {
			grupo = grupos.get(i);
//			grupo.printData();
			final String nombre = grupo.getNombre();
			final String id = grupo.getIdgrupo();
			final String descripcion = grupo.getDesscripcion();
			ArrayList<String[]> actividades = grupo.getActividades();
			ArrayList<String[]> integrantes = grupo.getIntegrantes();

			View customView = lgruposListado.inflate(R.layout.grupo_item, null);
			final LinearLayout datosGrupo = (LinearLayout) customView.findViewById(R.id.datosGrupo);
			datosGrupo.setVisibility(View.GONE);

			if (i % 2 == 0) {
				customView.setBackgroundColor(0xABEEEEEE );
			}

			final TextView tvGrupoTitulo = (TextView) customView.findViewById(R.id.titleGrupo);		
			tvGrupoTitulo.setText("+ "+nombre);
			tvGrupoTitulo.setClickable(true);
			tvGrupoTitulo.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					String c = String.valueOf(datosGrupo.getVisibility());
					Log.v(TAG,"Expande "+nombre+" con id "+id+" visibility "+c);
					if (datosGrupo.getVisibility() != View.VISIBLE) {
						datosGrupo.setVisibility(View.VISIBLE);
						tvGrupoTitulo.setText("- "+nombre);
					} else {
						datosGrupo.setVisibility(View.GONE);
						tvGrupoTitulo.setText("+ "+nombre);
					}	
				} 
			});	
			tvGrupoTitulo.setOnLongClickListener(new View.OnLongClickListener() {

				public boolean onLongClick(View v) {
					editGrupo(nombre, descripcion, id);
					return true;

				}

			});

			final TextView tvMiembrosExpand = (TextView) customView.findViewById(R.id.miembros_text);
			LayoutInflater ilistadoMiembros = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final LinearLayout listadoMiembros = (LinearLayout) customView.findViewById(R.id.listadoMiembros);
			listadoMiembros.setVisibility(View.GONE);

			tvMiembrosExpand.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.v(TAG,"Expande integrantes " + String.valueOf(listadoMiembros.getVisibility()));
					if (listadoMiembros.getVisibility() != View.VISIBLE) {
						listadoMiembros.setVisibility(View.VISIBLE);
						tvMiembrosExpand.setText("- Integrantes");
					} else {
						listadoMiembros.setVisibility(View.GONE);
						tvMiembrosExpand.setText("+ Integrantes");
					}
				}
			});


			for (int j = 0; j < integrantes.size(); j++) {
				final String elid = integrantes.get(j)[0];
				final String salumno = integrantes.get(j)[1];
				View miembrosView = ilistadoMiembros.inflate(R.layout.miembro_grupo_item, null);
				final TextView alumno = (TextView) miembrosView.findViewById(R.id.nombreMiembro);
				TextView fhalta = (TextView) miembrosView.findViewById(R.id.fh_alta_grupos);
				TextView fhbaja = (TextView) miembrosView.findViewById(R.id.fh_baja_grupos);
				final LinearLayout fechasMiembroGrupo = (LinearLayout) miembrosView.findViewById(R.id.fechasMiembroGrupo);
				alumno.setText("+ "+integrantes.get(j)[1]);
				fhalta.setText("      * fecha alta : "+integrantes.get(j)[2]);
				fhbaja.setText("      * fecha baja : "+integrantes.get(j)[3]);
				fechasMiembroGrupo.setVisibility(View.GONE);

				alumno.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						Log.v(TAG,"Expande "+alumno+" con id "+elid+" visibility ");
						if (fechasMiembroGrupo.getVisibility() != View.VISIBLE) {
							fechasMiembroGrupo.setVisibility(View.VISIBLE);
							alumno.setText("- "+salumno);
						} else {
							fechasMiembroGrupo.setVisibility(View.GONE);
							alumno.setText("+ "+salumno);
						}
					}
				});	
				miembrosView.setTag(elid);
				//				alumno.setOnLongClickListener( integrantesListener );

				alumno.setOnLongClickListener(new View.OnLongClickListener() {	
					public boolean onLongClick(final View v) {
						TextView nombreMiembro = (TextView) v.findViewById(R.id.nombreMiembro);
						//						final String al = nombreMiembro.getText().toString();
						Log.v("CUSTOM CLICK LISTENER","nuevo click en "+salumno);
						Builder alertDialog = new AlertDialog.Builder(GruposActivity.this);
						alertDialog.setTitle("Quitar alumno del grupo");
						alertDialog.setMessage("Eliminar al alumno "+salumno+" del grupo "+nombre+"?");
						alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								db.deleteFromTables("grupos", elid);
								v.setVisibility(View.GONE);
							} });
						alertDialog.show();			
						return true;	
					}
				});

				listadoMiembros.addView(miembrosView);
			}	

			final LinearLayout datosActividades = (LinearLayout) customView.findViewById(R.id.datosActividades);
			datosActividades.setVisibility(View.GONE);
			final TextView tvActividades = (TextView) customView.findViewById(R.id.actividadesGrupo);
			tvActividades.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					String c = String.valueOf(datosActividades.getVisibility());
					Log.v(TAG,"Expande "+nombre+" con id "+id+" visibility "+c);
					if (datosActividades.getVisibility() != View.VISIBLE) {
						datosActividades.setVisibility(View.VISIBLE);
						tvActividades.setText("- Actividades");
					} else {
						datosActividades.setVisibility(View.GONE);
						tvActividades.setText("+ Actividades");
					}	
				} 
			});	

			for (int j = 0 ; j < actividades.size() ; j++) {
				final String actividad = actividades.get(j)[1];
				final String idActividad = actividades.get(j)[0];
				final String descActividad = actividades.get(j)[2];
				TextView tv = new TextView(this);
				tv.setText(actividad+" : "+descActividad);
				datosActividades.addView(tv);
			}

			TextView dscripcion = (TextView) customView.findViewById(R.id.dsgrupo);
			dscripcion.setText("+ Descripción: "+descripcion);
			final EditText dscripcionEdit = (EditText) customView.findViewById(R.id.dsgrupoEdit);
			dscripcionEdit.setVisibility(View.GONE);

			dscripcion.setOnLongClickListener(this);
			dscripcion.setOnClickListener(this);
			gruposListado.addView(customView);
		}
		
	}

	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		int elid = v.getId();
		Log.v("CLICKS ","long click en "+elid);
		return false;
	}
	public void onClick(View v) {
		int elid = v.getId();
		Log.v("CLIDKS","click normal en "+elid);
	}
	public OnLongClickListener integrantesListener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			TextView nombreMiembro = (TextView) v.findViewById(R.id.nombreMiembro);
			final String al = nombreMiembro.getText().toString();
			final Object elid = nombreMiembro.getTag();
			Log.v("CUSTOM CLICK LISTENER","nuevo click en "+al);
			Builder alertDialog = new AlertDialog.Builder(GruposActivity.this);
			alertDialog.setTitle("Sacar del grupo a"+al);
			alertDialog.setMessage("en serio?");
			alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Log.v("CUSTOM CLICK","se va a eliminar al alumno "+al+"del grupo "+elid); 
				} });
			alertDialog.show();			
			//			addUser();
			return true;	
		}	
	};
	public void addUser(View v) {
		Intent i = new Intent(GruposActivity.this, EditarGruposActivity.class);
		startActivity(i);	
	}
	public void addGrupo(View v) {
		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.meta_grupo);
		dialog.setTitle("Grupo nuevo");

		TextView tvNombre = (TextView) dialog.findViewById(R.id.tvNombre);
		tvNombre.setText("Nombre:");
		TextView tvDescripcion = (TextView) dialog.findViewById(R.id.tvDescripcion);
		tvDescripcion.setText("Descripción:");

		final CheckBox ckMeta = (CheckBox) dialog.findViewById(R.id.ckMetaGrupo);
		final EditText etMeta = (EditText) dialog.findViewById(R.id.etMeta);


		ckMeta.setOnCheckedChangeListener( new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (ckMeta.isChecked()) {
					etMeta.setVisibility(View.VISIBLE);
				} else {
					etMeta.setVisibility(View.GONE);
				}
			}
		});

		final EditText etNombre = (EditText) dialog.findViewById(R.id.etNobmre);
		final EditText etDescripcion = (EditText) dialog.findViewById(R.id.etDescripcion);

		Button b1 = (Button) dialog.findViewById(R.id.button1);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v("TAG", " click en OK"+ etNombre.getText().toString()+ " "+etDescripcion.getText().toString() );
				String grupo = etNombre.getText().toString();
				String dscripcion = etDescripcion.getText().toString();
				String filtro = etMeta.getText().toString();
				String meta = "0";
				if (ckMeta.isChecked()) {
					meta = "1";
				}
				String stemp = "insert into grupos (grupo,descripcion,metagrupo,filtro)  values ('"+grupo+"','"+dscripcion+"','"+meta+"','"+filtro+"')";
				db.insertSql(stemp);

				dialog.dismiss();
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			} 
		});
		Button b2 = (Button) dialog.findViewById(R.id.button2);
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v("TAG", " click en CANCELAR"+ etNombre.getText().toString()+ " "+etDescripcion.getText().toString() );
				dialog.dismiss();
			} 
		});

		dialog.show();		
	}

	private void editGrupo(String nombre, String descripcion, final String id) {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(this);


		dialog.setContentView(R.layout.dos_et_borra);
		dialog.setTitle("Editar Grupo");


		TextView tvNombre = (TextView) dialog.findViewById(R.id.tvNombre1);
		tvNombre.setText("Nombre:");
		EditText et1 = (EditText) dialog.findViewById(R.id.etNobmre1);
		et1.setText(nombre);
		TextView tvDescripcion = (TextView) dialog.findViewById(R.id.tvDescripcion1);
		tvDescripcion.setText("Descripción:");
		EditText et2 = (EditText) dialog.findViewById(R.id.etDescripcion1);
		et2.setText(descripcion);
		final EditText etNombre = (EditText) dialog.findViewById(R.id.etNobmre1 );
		final EditText etDescripcion = (EditText) dialog.findViewById(R.id.etDescripcion1);

		Button b1 = (Button) dialog.findViewById(R.id.bOK);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v("TAG", " click en OK"+ etNombre.getText().toString()+ " "+etDescripcion.getText().toString() );
				String grupo = etNombre.getText().toString();
				String dscripcion = etDescripcion.getText().toString();
				Log.v(TAG,"editando "+grupo+" "+dscripcion+" con id:"+id);
				Log.v(TAG,"update from alumnos_grupo where grupoid = '"+id+"' y delete from grupos where _id = "+id);
				String stemp = "update grupos set grupo = '"+grupo+"', descripcion = '"+dscripcion+"' where _id = '"+id+"'";
				db.updateTableValue(stemp);
				dialog.dismiss();
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			} 
		});
		Button b2 = (Button) dialog.findViewById(R.id.bCancel);
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v("TAG", " click en CANCELAR"+ etNombre.getText().toString()+ " "+etDescripcion.getText().toString() );
				dialog.dismiss();
			} 
		});

		Button b3 = (Button) dialog.findViewById(R.id.bBorrar);
		//		b3.setVisibility(View.VISIBLE);
		b3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String stemp = "delete from alumnos_grupos where grupoid = '"+id+"'";
				Log.v("TAG", " click en BORRAR, borramos de grupos y alumnos_grupos donde idgrupo = "+id);
				db.deleteGrupo(id);
				stemp = "delete from meta_grupos where idgrupo = '"+id+"'";
				db.delete(stemp);
				dialog.dismiss();
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}

		});

		dialog.show();
		// TODO Auto-generated method stub
		//		return false;

	}
}

