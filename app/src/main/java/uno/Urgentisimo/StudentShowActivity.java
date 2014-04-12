package uno.Urgentisimo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

public class StudentShowActivity extends Activity {

	private static int TAKE_PICTURE = 1;
	private Uri outputFileUri;

	protected static final String TAG = "URGENTISIMO : STUDENTS SHOW ACTIVITY : ";
	private DataBaseHelper db;
	static final int DATE_DIALOG_ID = 1;
	private static final int ALERT_DIALOG1 = 1;
	private static final int ALERT_DIALOG2 = 2;
	private String idalumno;
	private ArrayList<String> listado;
	private int posicion;
	private String picFile;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Bundle extras = getIntent().getExtras();
		idalumno = extras.getString("idalumno");
		posicion = extras.getInt("position");
		Intent i = getIntent();
		listado = i.getStringArrayListExtra("listado");

		setContentView(R.layout.student_detail);
		final LinearLayout data_alumno_list = (LinearLayout) findViewById(R.id.data_alumnos_list);
		final LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// SET BUTTONS
		ImageButton nuevo_alumno = (ImageButton) findViewById(R.id.agrega_alumno);
		nuevo_alumno.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.v(TAG, "click nuevo alumno");
				String nuevoid = db.crea_alumno();
				fillAlumno(nuevoid, data_alumno_list, linflater);
			}
		});

		fillAlumno(idalumno, data_alumno_list, linflater);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	private void fillAlumno(final String idalumno,
			final LinearLayout data_alumno_list, LayoutInflater linflater) {
		// showDialog(DATE_DIALOG_ID);
		Log.v(TAG, "get student data");
		db = new DataBaseHelper(this);
		Cursor alumnoData = db.getAlumnoData(idalumno);

		final studentInfo alumnoInfo = new studentInfo();
		
		int i = 0;
		if (alumnoData.moveToFirst()) {
			Log.v(TAG, "recorriendo el cursor");
			do {

				// alumnoInfo.setAlumnoInfo("sss ","yyyy");
				// String xp = alumnoInfo.getAlumnoInfo("sss ");
				// Log.v(TAG,"infooo  "+xp);
				int length = alumnoData.getColumnCount();
				for (i = 0; i < length; i++) {
					String colname = alumnoData.getColumnName(i);
					String value = alumnoData.getString(i);
					Log.v(TAG, colname + ":" + value);
					alumnoInfo.setAlumnoInfo(colname, value); // Saves database
																// info.
				}
				final String alumnoId = alumnoInfo.getAlumnoInfo(idalumno);
				Log.v(TAG,idalumno);
				picFile = db.SDCARD+db.PIC_PATH +idalumno+".jpg";
				ImageButton pic = (ImageButton) findViewById(R.id.takePic);
				pic.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						db.createDirIfNotExists(db.PIC_PATH);
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						File file = new File(picFile);

						outputFileUri = Uri.fromFile(file);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
						startActivityForResult(intent, TAKE_PICTURE);

					}
				});
				
				fillPic();

				final TextView alumnoNombre = (TextView) findViewById(R.id.alumno_nombre);
				final TextView claseText = (TextView) findViewById(R.id.alumno_clase);
				final TextView fh_nac = (TextView) findViewById(R.id.fecha_nac_v);

				final EditText al_edit = (EditText) findViewById(R.id.nombre);
				final EditText ap_edit = (EditText) findViewById(R.id.apellidos);
				final EditText fh_edit = (EditText) findViewById(R.id.fecha_nac);
				final EditText cl_edit = (EditText) findViewById(R.id.clase);
				final EditText num_edit = (EditText) findViewById(R.id.numero_clase);

				fh_edit.setVisibility(View.GONE);
				ap_edit.setVisibility(View.GONE);
				al_edit.setVisibility(View.GONE);
				cl_edit.setVisibility(View.GONE);
				num_edit.setVisibility(View.GONE);

				al_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						String salida = al_edit.getText().toString();
						Log.v(TAG, salida);
						// fh_nac.setText(salida);
						al_edit.setVisibility(View.GONE);
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						String update = "update alumnos set nombre ='" + salida
								+ "' where _id=" + idalumno;
						alumnoInfo.setAlumnoInfo("nombre", salida);
						Log.v(TAG, update);
						db.updateTableValue(update);
						db.close();
						alumnoNombre.setText(alumnoInfo.getNumNombre());
						return true;
					}
				});

				ap_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						String salida = ap_edit.getText().toString();
						Log.v(TAG, salida);
						// fh_nac.setText(salida);
						ap_edit.setVisibility(View.GONE);
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						String update = "update alumnos set apellidos ='"
								+ salida + "' where _id=" + idalumno;
						alumnoInfo.setAlumnoInfo("apellidos", salida);
						alumnoNombre.setText(alumnoInfo.getNumNombre());
						Log.v(TAG, update);
						db.updateTableValue(update);
						db.close();
						return true;
					}
				});

				cl_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						String salida = cl_edit.getText().toString();
						Log.v(TAG, salida);
						// fh_nac.setText(salida);
						cl_edit.setVisibility(View.GONE);
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						String update = "update alumnos set clase ='" + salida
								+ "' where _id=" + idalumno;
						alumnoInfo.setAlumnoInfo("clase", salida);
						Log.v(TAG, update);
						db.updateTableValue(update);
						db.close();
						claseText.setText(alumnoInfo.getAlumnoInfo("clase"));
						return true;
					}
				});

				num_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						String salida = num_edit.getText().toString();
						Log.v(TAG, salida);
						// .setText(salida);
						num_edit.setVisibility(View.GONE);
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						String update = "update alumnos set numero_clase ='"
								+ salida + "' where _id=" + idalumno;
						alumnoInfo.setAlumnoInfo("numero_clase", salida);
						alumnoNombre.setText(alumnoInfo.getNumNombre());
						// alumnoInfo.printData();
						Log.v(TAG, update);
						db.updateTableValue(update);
						db.close();
						return true;
					}
				});

				// final LinearLayout lclase =
				// (LinearLayout)findViewById(R.id.clase_combo);
				final LinearLayout lnom = (LinearLayout) findViewById(R.id.nombre_combo);

				alumnoNombre
						.setOnLongClickListener(new View.OnLongClickListener() {
							public boolean onLongClick(View v) {
								Log.v(TAG, "nombre long");
								lnom.setVisibility(View.VISIBLE);
								ap_edit.setVisibility(View.VISIBLE);
								al_edit.setVisibility(View.VISIBLE);
								num_edit.setVisibility(View.VISIBLE);
								return false;
							}
						});

				String al = alumnoData.getString(alumnoData
						.getColumnIndex("nombre"));
				String apellidos = alumnoData.getString(alumnoData
						.getColumnIndex("apellidos"));
				String nclase = alumnoData.getString(alumnoData
						.getColumnIndex("numero_clase"));
				String alumno_nombre = nclase + " - " + al + " " + apellidos;

				final String cl = alumnoData.getString(alumnoData
						.getColumnIndex("clase"));
				final String fecha_nac = alumnoData.getString(alumnoData
						.getColumnIndex("fecha_nac"));

				claseText.setText(cl);
				alumnoNombre.setText(alumno_nombre);
				fh_nac.setText(fecha_nac);

				claseText
						.setOnLongClickListener(new View.OnLongClickListener() {
							public boolean onLongClick(View v) {
								Log.v(TAG, "clase long");
								PromptDialog dlg = new PromptDialog(
										StudentShowActivity.this, "Clase:", cl) {
									@Override
									public boolean onOkClicked(String salida) {
										// do something
										Log.v(TAG, "se supone que hago algo "
												+ salida);
										String update = "update alumnos set clase ='"
												+ salida
												+ "' where _id="
												+ idalumno;
										Log.v(TAG, update);
										db.updateTableValue(update);
										// studentInfo alumnoInfo =
										// ((studentInfo)getApplicationContext());
										// alumnoInfo.setAlumnoInfo("apellidos",
										// salida);

										return true; // true = close dialog
									}
								};
								dlg.show();
								return false;
							}

						});

				fh_nac.setOnLongClickListener(new View.OnLongClickListener() {
					public boolean onLongClick(View v) {
						Log.v(TAG, "clase long");
						PromptDialog dlg = new PromptDialog(
								StudentShowActivity.this,
								"Fecha de Nacimiento:", fecha_nac) {
							@Override
							public boolean onOkClicked(String salida) {
								// do something
								showDialog(1);
								Log.v(TAG, "se supone que hago algo " + salida);
								String update = "update alumnos set fecha_nac ='"
										+ salida + "' where _id=" + idalumno;
								Log.v(TAG, update);
								db.updateTableValue(update);
								// studentInfo alumnoInfo =
								// ((studentInfo)getApplicationContext());
								// alumnoInfo.setAlumnoInfo("apellidos",
								// salida);

								return true; // true = close dialog
							}
						};
						dlg.show();
						return false;
					}

				});

				al_edit.setText(al);
				ap_edit.setText(apellidos);

				String datos_basicos[] = { "numero_clase", "clase", "nombre",
						"apellidos", "fecha_nac" };
				for (int k = 0; k < datos_basicos.length; k++) {

				}

				String[] datos_alumno = { "direccion", "telefono_alum",
						"correo_alum", "padre", "correo_pad", "telefono_pad",
						"madre", "correo_mad", "telefono_mad" };
				final String[] datos_alumno_texto = { "Dirección: ",
						"Teléfono: ", "E-mail: ", "Padre: ", "        correo:",
						"      teléfono:", "Madre: ", "        correo:",
						"      teléfono:" };
				data_alumno_list.removeAllViews();
				for (int j = 0; j < datos_alumno.length; j++) {
					Log.v("TAG",
							"recorriendo datos_alumno, j = "
									+ String.valueOf(j));
					View customView = linflater.inflate(
							R.layout.student_data_1, null);
					final String etiqueta_string = datos_alumno[j];
					final String etiqueta_texto = datos_alumno_texto[j];
					Log.v(TAG, etiqueta_string);
					final String dato_string = alumnoInfo
							.getAlumnoInfo(etiqueta_string);
					Log.v(TAG, dato_string);

					TextView etiqueta = (TextView) customView
							.findViewById(R.id.etiqueta);
					final TextView dato = (TextView) customView
							.findViewById(R.id.dato);
					final EditText dato_edit = (EditText) customView
							.findViewById(R.id.dato_edit);

					etiqueta.setText(etiqueta_texto);
					dato.setText(dato_string);
					dato_edit.setText(dato_string);

					dato_edit
							.setOnEditorActionListener(new TextView.OnEditorActionListener() {

								public boolean onEditorAction(TextView v,
										int actionId, KeyEvent event) {
									// TODO Auto-generated method stub
									String salida = dato_edit.getText()
											.toString();
									Log.v(TAG, salida);
									dato.setText(salida);
									dato_edit.setVisibility(View.GONE);
									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											v.getWindowToken(), 0);
									String update = "update alumnos set "
											+ etiqueta_string + "='" + salida
											+ "' where _id=" + idalumno;
									Log.v(TAG, update);
									db.updateTableValue(update);
									db.close();
									return true;
								}
							});

					data_alumno_list.addView(customView);

					ImageView image = (ImageView) customView
							.findViewById(R.id.alumno_action);
					Pattern p = Pattern.compile("@");
					Matcher m = p.matcher(dato_string);
					if (!m.find()) {
						Log.v(TAG, dato_string + "no es email");
						image.setVisibility(View.GONE);
					} else {
						image.setVisibility(View.VISIBLE);
					}
					image.setClickable(true);
					dato_edit.setVisibility(View.GONE);
					image.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							// TODO Auto-generated method stub
							Log.v(TAG, "email " + dato_string);
						}
					});

					customView.setClickable(true);
					customView
							.setOnLongClickListener(new View.OnLongClickListener() {

								public boolean onLongClick(View v) {
									String salida = "item onclick "
											+ dato_string + " "
											+ etiqueta_texto + " "
											+ String.valueOf(idalumno);
									Log.v(TAG, salida);
									dato_edit.setVisibility(View.VISIBLE);
									return false;
								}
							});
				}

				String[] datos_extra = { "comedor", "compensatoria", "acnee",
						"extraescolares_l", "extraescolares_m",
						"extraescolares_x", "extraescolares_j",
						"extraescolares_v", "permiso_blog" };

			} while (alumnoData.moveToNext());
		} else {
			Log.v(TAG, "no existe el puto alumno");
		}
		alumnoData.close();

		// LinearLayout grupoLayout =
		// (LinearLayout)findViewById(R.id.grupoList);
		// LayoutInflater ginflater = (LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Cursor grupos = db.getAlumnoGrupos(idalumno);
		String grupos_texto = "";
		TextView grupos_tv = (TextView) findViewById(R.id.grupos_texto);

		try {
			grupos.moveToFirst();
			do {
				// TextView tv1 = new TextView(this);
				if (grupos_texto == "") {
					grupos_texto = grupos.getString(1);
				} else {
					grupos_texto += ", " + grupos.getString(1);
				}
				// grupoLayout.addView(tv1);
			} while (grupos.moveToNext());
			grupos.close();
		} catch (Exception e) {
			Log.e(TAG, "excepcion snif :", e);
		}
		grupos_tv.setText(grupos_texto);

		LinearLayout gruposLayout = (LinearLayout) findViewById(R.id.grupoList);
		gruposLayout.setClickable(true);

		gruposLayout.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View arg0) {
				Log.v(TAG, "click");
				Intent in = new Intent(StudentShowActivity.this,
						GruposAlumnoActivity.class);
				in.putExtra("elnombre", alumnoInfo.getNombreCompleto());
				in.putExtra("idalumno", idalumno);
				startActivity(in);
				return true;
			}

		});

		fillExtra(this, alumnoInfo, idalumno);

	}

	private void fillPic() {
		ImageView alumnofoto = (ImageView) findViewById(R.id.alumno_foto);
		 
		File file = new File(picFile);
		if(file.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(picFile, options);
			alumnofoto.setImageBitmap(bm);
		}
		
	}

	private void fillExtra(Context _Context, studentInfo alumnodata,
			final String idalumno) {
		CheckBox ckAcnee = (CheckBox) findViewById(R.id.ckAcnee);
		CheckBox ckComedor = (CheckBox) findViewById(R.id.ckComedor);
		CheckBox ckCompensatoria = (CheckBox) findViewById(R.id.ckCompensatoria);
		CheckBox ckSoloacasa = (CheckBox) findViewById(R.id.ckSoloacasa);
		CheckBox ckRefuerzo = (CheckBox) findViewById(R.id.ckRefuerzo);
		CheckBox ckExtral = (CheckBox) findViewById(R.id.ckExtral);
		CheckBox ckExtram = (CheckBox) findViewById(R.id.ckExtram);
		CheckBox ckExtraj = (CheckBox) findViewById(R.id.ckExtraj);
		CheckBox ckExtrav = (CheckBox) findViewById(R.id.ckExtrav);
		CheckBox ckExtrax = (CheckBox) findViewById(R.id.ckExtrav);
		CheckBox ckBlog = (CheckBox) findViewById(R.id.ckBlog);

		// String [] checkbuttons =
		// {"comedor","solo_a_casa","compensatoria","refuerzo","acnee","extraescolares_l","extraescolares_m","extraescolares_x","extraescolares_j","extraescolares_v","permiso_blog"};

		ckAcnee.setChecked(alumnodata.getChecked("acnee"));
		ckComedor.setChecked(alumnodata.getChecked("comedor"));
		ckCompensatoria.setChecked(alumnodata.getChecked("compensatoria"));
		ckSoloacasa.setChecked(alumnodata.getChecked("solo_a_casa"));
		ckRefuerzo.setChecked(alumnodata.getChecked("refuerzo"));
		ckExtral.setChecked(alumnodata.getChecked("extraescolares_l"));
		ckExtram.setChecked(alumnodata.getChecked("extraescolares_m"));
		ckExtraj.setChecked(alumnodata.getChecked("extraescolares_j"));
		ckExtrav.setChecked(alumnodata.getChecked("extraescolares_v"));
		ckExtrax.setChecked(alumnodata.getChecked("extraescolares_x"));
		ckBlog.setChecked(alumnodata.getChecked("permiso_blog"));

		ckAcnee.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set acnee = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set acnee = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckComedor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set comedor = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set comedor = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckCompensatoria
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							db.updateTableValue("update alumnos set compensatoria = '1' where _id='"
									+ idalumno + "'");
						} else {
							db.updateTableValue("update alumnos set compensatoria = '0' where _id='"
									+ idalumno + "'");
						}
					}
				});

		ckSoloacasa.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set solo_a_casa = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set solo_a_casa = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckRefuerzo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set refuerzo = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set refuerzo = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckBlog.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set permiso_blog = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set permiso_blog = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckExtral.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set extraescolares_l = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set extraescolares_l = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckExtram.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set extraescolares_m = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set extraescolares_m = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckExtrax.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set extraescolares_x = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set extraescolares_x = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckExtraj.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set extraescolares_j = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set extraescolares_j = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
		ckExtrav.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					db.updateTableValue("update alumnos set extraescolares_v = '1' where _id='"
							+ idalumno + "'");
				} else {
					db.updateTableValue("update alumnos set extraescolares_v = '0' where _id='"
							+ idalumno + "'");
				}
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder;
		switch (id) {
		case ALERT_DIALOG1:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("My first alert dialog example!")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Do something here
								}
							});
			dialog = builder.create();
			break;
		case ALERT_DIALOG2:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("My second alert dialog example!")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Do something here
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Do something here
								}
							});

			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;

	}

	public void delAlumno(View v) {
		db.deleteFromTables("alumnos", idalumno);
		finish();

	}

//	private void TakePhoto() {
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		File file = new File(Environment.getExternalStorageDirectory(),
//				"test.jpg");
//
//		outputFileUri = Uri.fromFile(file);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//		startActivityForResult(intent, TAKE_PICTURE);
//
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == TAKE_PICTURE) {
			Log.v(TAG, "foto tomada");
			fillPic();
		}

	}
}