package uno.Urgentisimo;

import java.io.File;
//import UtilsLib.evalCriterio;
//import UtilsLib.evalCriterio;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import uno.Urgentisimo.UtilsLib.evalCriterio;
import uno.Urgentisimo.UtilsLib.evalCriterioAlumno;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "URGENTISIMO:DBHELPER";

	private static final int ALL = 1;

	private static final int ACTIVE_MEMBERS = 2;

	private static final String FECHA_FIN = "2012-09-01";

	public static String SDCARD = Environment.getExternalStorageDirectory()
			+ "/";
	private static String DB_PATH = "/Urgentisimo/database/";
	private static String DB_NAME = "urgentisimo.db";
	private static String BKP_PATH = "/Urgentisimo/bkp/";
	public static String PIC_PATH = "/Urgentisimo/pics/";
	private static String DB_VERSION = "8";
	private HashMap<String, String[]> tablas;
	private UtilsLib utils;
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		this.utils = new UtilsLib(myContext);
		tablas = new HashMap<String, String[]>();
		String[] objetivos = { "objetivosasignatura", "objetivoscompetencia",
				"contenidosobjetivo" };
		tablas.put("objetivos", objetivos);
		String[] grupos = { "alumnos_grupos", "actividades_grupos" };
		tablas.put("grupos", grupos);
		String[] alumno = { "alumnos_grupos", "evaluacion", "asistencia" };
		tablas.put("alumnos", alumno);
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
			Log.v(TAG, "Checking if db exists: " + SDCARD + DB_PATH + DB_NAME);
		} else {

			// Copy initial database from assets directory
			Log.v(TAG, "Database nonexistent, copying a dafault one");
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = SDCARD + DB_PATH + DB_NAME;
//			Log.v(TAG, "db : " + myPath);
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = SDCARD + DB_PATH + DB_NAME;
//		Log.v(TAG, "copying db " + DB_NAME + " to outFilename");
		File dbdir = new File(SDCARD + DB_PATH);
		dbdir.mkdirs();
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void resetDatabase() throws IOException {
//		Log.v(TAG, "resetDatabase pressed");
		String myPath = DB_PATH + DB_NAME;
		copyDataBase();
	}

	public void upgradeDB() {
		boolean copiar = false;
		this.openDataBase();
		try {
			Cursor c = myDataBase.rawQuery("select version from version", null);
			if (c.moveToFirst()) {
				if (!c.getString(0).equalsIgnoreCase(DB_VERSION)) {
					copiar = true;
				}
			} else {
				copiar = true;
			}
			c.close();

		} catch (Exception e) {
			Log.e(TAG, "excepcion", e);
			copiar = true;
		}
		copiar = false;
		if (copiar) {
			try {
				this.copyDataBase();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.close();
	}

	public static boolean createDirIfNotExists(String path) {
		boolean ret = true;

		File file = new File(SDCARD, path);
//		Log.v(TAG, "file " + file);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e(TAG, "Problem creating directory : " + file);
				ret = false;
			}
		}
		return ret;
	}

	public void backupDatabase() throws IOException {
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH) + 1;
		int dmonth = c.get(Calendar.DAY_OF_MONTH);
		int year = c.get(Calendar.YEAR);
		String sdate = dmonth + "_" + month + "_" + year;

		File dbdir = new File(SDCARD + BKP_PATH);
		dbdir.mkdirs();

		String bk_file = SDCARD + "/" + BKP_PATH + "/db_" + sdate + ".db";
//		Log.v(TAG, "bkfile " + bk_file);

		InputStream myInput = new FileInputStream(SDCARD + DB_PATH + DB_NAME);

		// Path to the just created empty db
		String outFileName = bk_file;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		// Log.v(TAG,"Open DB : "+ SDCARD+ DB_PATH + DB_NAME);
		String myPath = SDCARD + DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	public void openDataBaseWrite() throws SQLException {

		// Open the database
//		Log.v(TAG, "Open DB : " + SDCARD + DB_PATH + DB_NAME);
		String myPath = SDCARD + DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.

	public ArrayList<nombreId> getActividadesHoy() {
		ArrayList<nombreId> resultado = new ArrayList<nombreId>();
		String sdate = "select * from actividades where fecha_inicio = (select date(\"now\")) ";
//		Log.v(TAG, sdate);
		this.openDataBase();
		try {
			Cursor c = myDataBase.rawQuery(sdate, null);
			if (c.moveToFirst()) {
				do {
					nombreId act = new nombreId();
					act.setId(c.getString(c.getColumnIndex("_id")));
					String grupos = new String();
					sdate = "select grupo from grupos join actividades_grupos on grupos._id = actividades_grupos.idgrupo where actividades_grupos.idactividad = '"
							+ act.getId() + "'";
					Cursor c1 = myDataBase.rawQuery(sdate, null);
					if (c1.moveToFirst()) {
						do {
							grupos = grupos + c1.getString(0) + ", ";
//							Log.v(TAG, c1.getString(0));
						} while (c1.moveToNext());
						grupos = grupos.substring(0, grupos.length() - 2);
//						Log.v(TAG, grupos);
					}
					c1.close();
					act.setNombre(c.getString(c.getColumnIndex("actividad"))
							+ " - " + grupos);
					act.setDescripcion(c.getString(c
							.getColumnIndex("descripcion")));
					resultado.add(act);
//					Log.v(TAG,
//							"hoy toca " + act.getNombre() + " - "
//									+ act.getDescripcion());
				} while (c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			Log.e(TAG, "exception ", e);
		}
		this.close();
		return resultado;
	}

	public ArrayList<nombreId> getActividadesRetrasadas() {
		ArrayList<nombreId> datos = new ArrayList<nombreId>();
		String stemp = "select * from actividades where strftime(\"%s\",\"fecha_fin\") < strftime(\"%s\",\"now\") and hecho = '0'";
		this.openDataBase();
		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			do {
				nombreId tmp = new nombreId();
				tmp.setId(c.getString(c.getColumnIndex("_id")));
				tmp.setNombre(c.getString(c.getColumnIndex("actividad")));
				tmp.setDescripcion(c.getString(c.getColumnIndex("descripcion")));
			} while (c.moveToNext());
		}
//		Log.v(TAG, stemp);
		return datos;
	}

	public Cursor getClases() {
		String stemp = "select _id from clases";
		Cursor clases = myDataBase.rawQuery(stemp, null);
		return clases;

	}

	public Cursor getAlumnosList() {
		String stemp = "select _id,nombre,apellidos from alumnos order by 2,3";
		this.openDataBase();
		Cursor alumnosList = myDataBase.rawQuery(stemp, null);
		// this.close();
		return alumnosList;
	}

	public Cursor getAlumnoData(String id) {
		// TODO Auto-generated method stub
		String stemp = "select * from alumnos where _id = '" + id + "'";
//		Log.v(TAG, stemp);
		this.openDataBase();
		Cursor alumnoData = myDataBase.rawQuery(stemp, null);
		// this.close();
		return alumnoData;
	}

	public Cursor getAlumnoGrupos(String id) {
		String stemp = " select *  from grupos where _id in (select idgrupo from alumnos_grupos where idalumno = '"
				+ id + "')";
		// Log.v(TAG,"entrando "+stemp);
		// stemp =
		// "select idgrupo from alumnos_grupos where idalumno = '"+id+"'";
//		Log.v(TAG, "query2 " + stemp);
		Cursor idgrupos = myDataBase.rawQuery(stemp, null);
		return idgrupos;
	}

	public Cursor getGrupos() {
		this.openDataBase();
		String stemp = "select _id,grupo,descripcion from grupos";
//		Log.v(TAG, stemp);
		Cursor grupos = myDataBase.rawQuery(stemp, null);
		if (grupos.moveToFirst()) {
			do {
				Log.v(TAG, grupos.getString(1));
			} while (grupos.moveToNext());
		}
		// this.close();
		return grupos;
	}

	public ArrayList<nombreId> getAlumnosGrupo(String idgrupo) {
		ArrayList<nombreId> alumnosGrupo = new ArrayList<nombreId>();
		String stemp = "select * from grupos where _id = '" + idgrupo + "'";
		this.openDataBase();
		Cursor ctemp = myDataBase.rawQuery(stemp, null);
		if (ctemp.moveToFirst()) {
			String elfiltro = ctemp.getString(ctemp.getColumnIndex("filtro"));
			String metagrupo = ctemp.getString(ctemp
					.getColumnIndex("metagrupo"));
//			Log.v(TAG, " filtro " + elfiltro + " metagrupo " + metagrupo);
			if (elfiltro != null && metagrupo.equalsIgnoreCase("1")) {
				stemp = "select _id,nombre,apellidos from alumnos where _id in ("
						+ elfiltro + ")";
			} else {
				stemp = "select _id,nombre,apellidos from alumnos where _id in (select idalumno from alumnos_grupos where idgrupo = '"
						+ idgrupo + "')";
			}
		}
		ctemp.close();
//		Log.v(TAG, "grupos : " + stemp);
		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			do {
				nombreId item = new nombreId(c.getString(c
						.getColumnIndex("_id")), c.getString(c
						.getColumnIndex("nombre")), c.getString(c
						.getColumnIndex("apellidos")));
				alumnosGrupo.add(item);
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		return alumnosGrupo;
	}

	public boolean updateTableValue(String query) {
//		Log.v(TAG, query);
		try {
			this.openDataBaseWrite();
			myDataBase.execSQL(query);
			this.close();

		} catch (Exception e) {
			Log.e(TAG, "Error updating data, Exception :", e);
			return false;
		}
		return true;
	}

	public void delete(String stemp) {
//		Log.v(TAG, stemp);
		try {
			this.openDataBaseWrite();
			myDataBase.execSQL(stemp);
			this.close();
		} catch (Exception e) {
			Log.e(TAG, "Error al borrar :", e);
		}
	}

	public String crea_alumno() {
//		Log.v(TAG, "insert alumno");
		String query = "insert into alumnos(nombre) values ('dummy')";
		String elid;
		try {
			this.openDataBaseWrite();
			myDataBase.execSQL(query);
			Cursor temp = myDataBase.rawQuery(
					"select _id from alumnos where nombre='dummy'", null);
			temp.moveToFirst();
			elid = temp.getString(0);
			temp.close();
			this.close();
		} catch (Exception e) {
			Log.e(TAG, "Error updating data, Exception :", e);
			return "99999";
		}
		return elid;
	}

	/*
	 * Get groups information, first get idgrupo. For every group use getQuery
	 * to get the students query
	 */
	public ArrayList<gruposInfo> getGruposInfo() {
		ArrayList<gruposInfo> grupos = new ArrayList<gruposInfo>();
		this.openDataBase();
		String stemp = "select _id,grupo,descripcion,metagrupo,filtro from grupos";
		// Log.v(TAG,stemp);
		try {
			Cursor temp = myDataBase.rawQuery(stemp, null);
			if (temp.moveToFirst()) {
				do {
					gruposInfo grupo = new gruposInfo();
					String idgrupo = temp.getString(0);
					grupo.setNombre(temp.getString(1));
					grupo.setId(temp.getString(0));
					grupo.setDescripcion(temp.getString(2));
					grupo.setFiltro(temp.getString(temp
							.getColumnIndex("filtro")));
					grupo.setMetagrupo(temp.getString(temp
							.getColumnIndex("metagrupo")));
					stemp = getQuery(grupo, ALL);
//					Log.v(TAG, "la select :" + stemp);
					Cursor c2 = myDataBase.rawQuery(stemp, null);
					if (c2.moveToFirst()) {
						do {
							String id = c2.getString(0);
							String nombre = c2.getString(1) + " "
									+ c2.getString(2);
							String fh_alta = "";
							String fh_baja = "";
							if (grupo.isMetagrupo() == false) {
								fh_alta = c2.getString(3);
								fh_baja = c2.getString(4);
							}
							grupo.addIntegrante(id, nombre, fh_alta, fh_baja);
							// Log.v(TAG,"Adding alumno "+nombre);
						} while (c2.moveToNext());
					}
					c2.close();
					stemp = "select _id,actividad,descripcion from actividades where _id in (select _id from actividades_grupos where idgrupo = '"
							+ idgrupo + "')";
//					Log.v(TAG, stemp);
					Cursor c3 = myDataBase.rawQuery(stemp, null);
					if (c3.moveToFirst()) {
						do {
							String id = c3.getString(0);
							String nombre = c3.getString(1);
							String desc = c3.getString(2);
							grupo.addActividades(id, nombre, desc);
							// Log.v(TAG,"actividad "+nombre+" : "+desc);
						} while (c3.moveToNext());
					}
					// Log.v(TAG,"closing cursor C3");
					c3.close();
					grupos.add(grupo);
				} while (temp.moveToNext());
				temp.close();
			}

		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
		this.close();
		return grupos;
	}

	/* ***********************************************************************************************************
	 * getQuery: returns the query used to get alumnos who belong to grupo
	 * "idgrupo", if grupo is a meta group then the field "filtro" is used to
	 * get data from database, otherwise alumnos from alumnos_grupo table are
	 * retrived
	 * ******************************************************************
	 * ****************************************
	 */
	private String getQuery(gruposInfo grupo, int tipo) {
		String stemp = "";
		switch (tipo) {
		case ALL:
			if (grupo.isMetagrupo() == true) {
				stemp = "select _id,nombre,apellidos from alumnos where _id in ("
						+ grupo.getFiltro() + ")";
			} else {
				stemp = "select alumnos._id,alumnos.nombre,alumnos.apellidos,alumnos_grupos.fecha_alta,alumnos_grupos.fecha_baja from alumnos,alumnos_grupos where alumnos._id = alumnos_grupos.idalumno and alumnos_grupos.idgrupo = '"
						+ grupo.getIdgrupo() + "'";
			}
			break;
		case ACTIVE_MEMBERS:
			if (grupo.isMetagrupo() == true) {
				stemp = "select * from alumnos where _id in ("
						+ grupo.getFiltro() + ")";
			} else {
				stemp = "select  _id,idalumno,fecha_alta,fecha_baja from alumnos_grupos where (strftime(\"%s\",fecha_alta) < strftime(\"%s\",\"now\") and strftime(\"%s\", \"now\" ) <  strftime(\"%s\",fecha_baja)) and idgrupo = '"
						+ grupo.getIdgrupo() + "'";
			}

			break;
		}

		return stemp;
	}

	/*
	 * Get students and groups information, if grupo = metagrupo then get all
	 * students from that group otherwise show only students in alumnos_grupos
	 * table
	 */

	public ArrayList<alumnosEnGrupo> getAlumnosYGrupo(gruposInfo grupo) {
		// Log.v(TAG,"entrando a getAlumnosYGrupo");
		ArrayList<alumnosEnGrupo> resultado = new ArrayList<alumnosEnGrupo>();
		try {
			this.openDataBase();
			Cursor c = myDataBase.rawQuery(
					"select _id,nombre,apellidos from alumnos", null);
			if (c.moveToFirst()) {
				do {
					alumnosEnGrupo listadoAlumnos = new alumnosEnGrupo();
					listadoAlumnos.setAlumnoid(c.getString(0));
					listadoAlumnos.setNombre(c.getString(1) + " "
							+ c.getString(2));
					listadoAlumnos.setMiembro("0");
					resultado.add(listadoAlumnos);
				} while (c.moveToNext());
			}
			c.close();

		} catch (Exception e) {
			return resultado;
		}
		String stemp = this.getQuery(grupo, ACTIVE_MEMBERS);
//		Log.v(TAG, stemp);
		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			do {
				String idalumno = "";
				if (grupo.isMetagrupo() == true) {
					idalumno = c.getString(c.getColumnIndex("_id"));
				} else {
					idalumno = c.getString(c.getColumnIndex("idalumno"));
				}
				// Log.v(TAG,"entrando como miembro "+idalumno);
				// String alta = c.getString(2);
				// String baja = c.getString(3);
				// String alumnoid = c.getString(1);
				// Log.v(TAG,"convirtiendo "+alta+" "+baja+" del alumno "+alumnoid);
				for (int i = 0; i < resultado.size(); i++) {
					alumnosEnGrupo temporal = resultado.get(i);
					String elnombre = temporal.getNombre();
					// Log.v(TAG,"el alumno '" + temporal.getAlumnoid()+
					// "' es igual a '"+alumnoid+"' "+elnombre);
					if (temporal.getAlumnoid().equals(idalumno)) {
						// Log.v(TAG,"es igual!!!!");
//						Log.v(TAG, elnombre + "con id " + idalumno
//								+ "es miembro");
						// temporal.setAlumnoid(alumnoid);
						// temporal.setNombre(elnombre);
						temporal.setMiembro("1");
						resultado.set(i, temporal);
						// resultado.add( )temporal.setMiembro("1")
						// resultado.remove(i);
						// resultado.add(temporal);
					}
				}

			} while (c.moveToNext());
			c.close();

		} else {
//			Log.v(TAG, "no hay alumnos en el grupo...");
			c.close();
		}
		c.close();
		this.close();
		Collections.sort(resultado, new CustomComparator());
		return resultado;
	}

	public long getDate(String c) {
//		Log.v(TAG, "convertir " + c);
		long epoch = 0;
		if (c == "hoy") {
			Calendar cal = Calendar.getInstance();
			int month = cal.get(Calendar.MONTH) + 1;
			int dmonth = cal.get(Calendar.DAY_OF_MONTH);
			int year = cal.get(Calendar.YEAR);
			c = dmonth + "/" + month + "/" + year;
		}

		try {
//			Log.v(TAG, "aeoooo ");
			epoch = new java.text.SimpleDateFormat("dd/MM/YYYY HH:mm:ss")
					.parse(c + " 00:00:00").getTime();
//			Log.v(TAG, "fecha " + c + ":" + epoch);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "excepcion en ", e);
			e.printStackTrace();
		}
		return epoch;
	}

	public void altaAlumnoGrupo(String alumnoid, String idgrupo) {
		// TODO Auto-generated method stub;
		this.openDataBaseWrite();
		String stemp = "delete from alumnos_grupos where idalumno = '"
				+ alumnoid + "' and idgrupo = '" + idgrupo + "'";
		myDataBase.execSQL(stemp);
		stemp = ("insert into alumnos_grupos (idgrupo,idalumno,fecha_alta,fecha_baja) values ('"
				+ idgrupo + "','" + alumnoid + "',date(\"now\"),\"" + FECHA_FIN + "\")");
		myDataBase.execSQL(stemp);
		this.close();
	}

	public void bajaAlumnoGrupo(String alumnoid, String idgrupo) {
		// TODO Auto-generated method stub
		this.openDataBaseWrite();
		String stemp = "select _id from alumnos_grupos where idalumno ='"
				+ alumnoid + "' and idgrupo = '" + idgrupo + "'";

		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			stemp = "update alumnos_grupos set fecha_baja = date(\"now\") where idalumno = '"
					+ alumnoid + "' and idgrupo = '" + idgrupo + "'";
			myDataBase.execSQL(stemp);
		}
		c.close();
		this.close();

	}

	public void insertGrupo(String grupo, String descripcion) {
		this.openDataBaseWrite();
		String stemp = "insert into grupos(grupo,descripcion) values ('"
				+ grupo + "','" + descripcion + "')";
//		Log.v(TAG, stemp);
		try {
			myDataBase.execSQL(stemp);

		} catch (Exception e) {
			Log.e(TAG, "error ", e);
		}
	}

	public void deleteGrupo(String grupoid) {
		try {
			this.openDataBaseWrite();
			String stemp = "delete from alumnos_grupos where idgrupo = '"
					+ grupoid + "'";
			myDataBase.execSQL(stemp);
			stemp = "delete from grupos where _id = '" + grupoid + "'";
			myDataBase.execSQL(stemp);
			stemp = "delete from meta_grupos where idgrupo = '" + grupoid + "'";
			myDataBase.execSQL(stemp);
		} catch (Exception e) {
			Log.e(TAG, "Excepcion ", e);
		}
	}

	public Cursor getCriterios() {
		// TODO Auto-generated method stub
		this.openDataBase();
		Cursor c = myDataBase.rawQuery("select * from criterios", null);
		return c;
	}

	public ArrayList<alumnosEnGrupo> getActividadesGrupo(String idgrupo) {
		ArrayList<alumnosEnGrupo> resultado = new ArrayList<alumnosEnGrupo>();
		try {
			this.openDataBase();
			Cursor c = myDataBase.rawQuery(
					"select _id,nombre from actividades", null);
			if (c.moveToFirst()) {
				do {
					alumnosEnGrupo listadoActividades = new alumnosEnGrupo();
					listadoActividades.setAlumnoid(c.getString(0));
					listadoActividades.setNombre(c.getString(1));
					listadoActividades.setMiembro("0");
					resultado.add(listadoActividades);
				} while (c.moveToNext());
			}
			c.close();

		} catch (Exception e) {
			return resultado;
		}
		try {
			String stemp = ("select  _id,idactividad,idcriterioev from criterios_actividad where idcriterioev = '"
					+ idgrupo + "'");
//			Log.v(TAG, stemp);
			Cursor c = myDataBase.rawQuery(stemp, null);
			if (c.moveToFirst()) {
				do {
					// Log.v(TAG,"entrando?");
					// String alta = c.getString(2);
					// String baja = c.getString(3);
					String actId = c.getString(1);
					String criterioId = c.getString(2);
					// Log.v(TAG,"convirtiendoC "+alta+" "+baja+" del alumno "+alumnoid);
					for (int i = 0; i < resultado.size(); i++) {
						alumnosEnGrupo temporal = resultado.get(i);
						String elnombre = temporal.getNombre();
//						Log.v(TAG, "actividad  '" + temporal.getAlumnoid()
//								+ "' es igual a '" + actId + "' :" + elnombre);
						if (temporal.getAlumnoid().equals(actId)) {
//							Log.v(TAG, "es igual!!!!");
//							Log.v(TAG, elnombre + "con id " + actId
//									+ "es miembro");
							temporal.setAlumnoid(actId);
							temporal.setNombre(elnombre);
							temporal.setMiembro("1");
							resultado.remove(i);
							resultado.add(temporal);
						}
					}

				} while (c.moveToNext());
				c.close();

			} else {
//				Log.v(TAG, "no hay alumnos en el grupo...");
				c.close();
			}
			c.close();
		} catch (Exception e) {

		}
		this.close();

		Collections.sort(resultado, new CustomComparator());

		return resultado;

	}

	public void altaActividadCriterio(String idactividad, String idcriterioev) {
		// TODO Auto-generated method stub
		this.openDataBaseWrite();
		String stemp = "insert into criterios_actividad(idactividad,idcriterioev) values ('"
				+ idactividad + "','" + idcriterioev + "')";
//		Log.v(TAG, stemp);
		try {
			myDataBase.execSQL(stemp);

		} catch (Exception e) {
			Log.e(TAG, "error ", e);
		}
		this.close();
	}

	public void bajaActividadCriterio(String idactividad, String idcriterioev) {
		// TODO Auto-generated method stub
		this.openDataBaseWrite();
		String stemp = "delete from criterios_actividad where idactividad ='"
				+ idactividad + "' and idcriterioev = '" + idcriterioev + "'";
//		Log.v(TAG, stemp);
		try {
			myDataBase.execSQL(stemp);

		} catch (Exception e) {
			Log.e(TAG, "error ", e);
		}
		this.close();
	}

	public class CustomComparator implements Comparator<alumnosEnGrupo> {
		public int compare(alumnosEnGrupo o1, alumnosEnGrupo o2) {
			return o1.getNombre().compareTo(o2.getNombre());
		}
	}

	public ArrayList<alumnosEnGrupo> getGruposAlumno(String idalumno) {
		ArrayList<alumnosEnGrupo> resultado = new ArrayList<alumnosEnGrupo>();
		try {
			this.openDataBase();
			Cursor c = myDataBase
					.rawQuery("select _id,grupo from grupos", null);
			if (c.moveToFirst()) {
				do {
					alumnosEnGrupo listadoActividades = new alumnosEnGrupo();
					listadoActividades.setAlumnoid(c.getString(0));
					listadoActividades.setNombre(c.getString(1));
					listadoActividades.setMiembro("0");
					resultado.add(listadoActividades);
				} while (c.moveToNext());
			}
			c.close();

		} catch (Exception e) {
			return resultado;
		}
		try {
			String stemp = ("select _id,grupo from grupos where _id in (select idgrupo from alumnos_grupos where idalumno = '"
					+ idalumno + "')");
//			Log.v(TAG, stemp);
			Cursor c = myDataBase.rawQuery(stemp, null);
			if (c.moveToFirst()) {
				do {
					// Log.v(TAG,"entrando grupos alumno");
					String actId = c.getString(0);
					String criterioId = c.getString(1);
					for (int i = 0; i < resultado.size(); i++) {
						alumnosEnGrupo temporal = resultado.get(i);
						String elnombre = temporal.getNombre();
//						Log.v(TAG, "grupo  '" + temporal.getAlumnoid()
//								+ "' es igual a '" + actId + "' :" + elnombre);
						if (temporal.getAlumnoid().equals(actId)) {
//							Log.v(TAG, "es igual!!!!");
//							Log.v(TAG, elnombre + "con id " + actId
//									+ " vale para el alumno " + idalumno);
							temporal.setAlumnoid(actId);
							temporal.setNombre(elnombre);
							temporal.setMiembro("1");
							resultado.remove(i);
							resultado.add(temporal);
						}
					}

				} while (c.moveToNext());
				c.close();

			} else {
//				Log.v(TAG, "no hay alumnos en el grupo...");
				c.close();
			}
			c.close();
		} catch (Exception e) {

		}
		this.close();

		Collections.sort(resultado, new CustomComparator());

		return resultado;

	}

	public String getString(String query) {
		this.openDataBase();
		String result = new String();
		Cursor c = myDataBase.rawQuery(query, null);
		if (c.moveToFirst()) {
			do {
				result = c.getString(0);
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		return result;

	}

	public ArrayList<String[]> getIdTexto(String latabla) {
		String elcampo = latabla.substring(0, (latabla.length() - 1));
		ArrayList<String[]> resultado = new ArrayList<String[]>();
		String stemp = new String();
		this.openDataBase();
//		Log.v(TAG, "select * from " + latabla);
		if (latabla.equals("contenidos")) {
			stemp = "select contenidos._id, contenidos.contenido, objetivos.objetivo, bloques.bloque from contenidos, objetivos, bloques where contenidos.idobjetivo = objetivos._id and contenidos.idbloque = bloques._id ";
		} else {
			stemp = "select * from " + latabla;
		}
		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			do {
				String[] tmp = new String[4];
				tmp[0] = c.getString(c.getColumnIndex("_id"));
				tmp[1] = c.getString(c.getColumnIndex(elcampo));
				if (c.getColumnCount() > 2) {
					String objetivo = c.getString(c.getColumnIndex("objetivo"));
					String bloque = c.getString(c.getColumnIndex("bloque"));
					tmp[2] = "          Objetivo : " + objetivo;
					tmp[3] = "          Bloque : " + bloque;
				} else {
					tmp[2] = "";
					tmp[3] = "";
				}

//				Log.v(TAG, "para " + latabla + ": " + tmp[0] + ", " + tmp[1]);
				resultado.add(tmp);
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		return resultado;
	}

	public void borraIdDeTabla(String lt, String elid) {
		HashMap<String, String> tablasRelacionadas = new HashMap<String, String>();
		tablasRelacionadas.put("asignaturas", "objetivoasignatura");
		tablasRelacionadas.put("competencias", "objetivocompetencia");
		tablasRelacionadas.put("alumnos", "alumnos_grupos:evaluacion:eval_criterios_alumno");
		tablasRelacionadas.put("objetivos","objetivoscompetencia:objetivosasignatura:contenidos_objetivo" );
		tablasRelacionadas.put("contenidos", "criterioscontenido");
		tablasRelacionadas.put("bloques", "contenidos");
		tablasRelacionadas.put("criterios", "evaluacion:eval_criterios_alumno:criterioscontenido:criterios_actividad:");
		tablasRelacionadas
				.put("actividades",
						"actividades_grupos:criterios_actividad:materiales_actividad:evaluacion:materiales_actividad");
		
		String stemp = new String();
		this.openDataBaseWrite();
		try {
			if (tablasRelacionadas.get(lt) != null) {
				String tablaid = this.getCampo(lt);
				String[] tablas = tablasRelacionadas.get(lt).split(":");
				for (int k = 0; k < tablas.length; k++) {
					stemp = "delete from " + tablas[k] + " where id" + tablaid
							+ " = '" + elid + "'";
//					Log.v(TAG, "borrando :" + stemp);
					myDataBase.execSQL(stemp);
				}
			}
			stemp = "delete from " + lt + " where _id = '" + elid + "'";
//			Log.v(TAG, "y borrando también :" + stemp);
			myDataBase.execSQL(stemp);
		} catch (Exception e) {
			Log.d(TAG, "Excepcion  borrando de tabla" + stemp + " ", e);
		}
		this.close();
	}

	public String insertRegistro(String latabla, String valor) {
		String campo = getCampo(latabla);
		String stemp = "insert into " + latabla + " (" + campo + ") values ('"
				+ valor + "')";
		String resultado = new String();
		Log.v(TAG, "insertando " + stemp);
		this.openDataBaseWrite();
		try {
			myDataBase.execSQL(stemp);
			Cursor c = myDataBase.rawQuery("select _id from " + latabla
					+ " where " + campo + "= '" + valor + "'", null);
			c.moveToFirst();
			resultado = c.getString(0);
			c.close();
		} catch (Exception e) {
			Log.e(TAG, "error ", e);
		}
		this.close();
		return resultado;
	}

	public String getCampo(String tabla) {
		String campo = tabla.substring(0, tabla.length() - 1);
		if (tabla.equals("actividades")) {
			campo = "actividad";
		}
		if (tabla.equalsIgnoreCase("actividades_generales")) {
			campo = "actividad_general";
		}
		if (tabla.equalsIgnoreCase("tipos_actividades")) {
			campo = "tipo_actividad";
		}
		if (tabla.equalsIgnoreCase("materiales")) {
			campo = "material";
		}
		return campo;
	}

	public ArrayList<nombreId> getNombreId(String tabla) {
		ArrayList<nombreId> resultado = new ArrayList<nombreId>();
		String campo = getCampo(tabla);

		this.openDataBase();
		Cursor c = myDataBase.rawQuery("select * from " + tabla+" order by 2", null);
		if (c.moveToFirst()) {
			do {
				String elid = c.getString(c.getColumnIndex("_id"));
				String elnombre = c.getString(c.getColumnIndex(campo));
				nombreId item = new nombreId();
				item.setNombre(elnombre);
				item.setId(elid);
				resultado.add(item);

				Log.v(TAG, tabla + "- datos :" + elid + ":" + elnombre);
			} while (c.moveToNext());
		}
		c.close();
		this.close();

		Log.v(TAG, "para el adapter:size " + resultado.size());
		return resultado;
	}

	public void insertSql(String stemp) {
		this.openDataBaseWrite();
		try {
			myDataBase.execSQL(stemp);
		} catch (Exception e) {
			Log.e(TAG, "error sql " + stemp, e);
		}
		this.close();
	}

	public ArrayList<criterioInfo> getCriteriosInfo() {
		ArrayList<criterioInfo> resultado = new ArrayList<criterioInfo>();
		this.openDataBase();
		String stemp = "select * from criterios";
		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			do {
				criterioInfo criterio = new criterioInfo();
				String idcriterio = c.getString(c.getColumnIndex("_id"));
				String criterionombre = c.getString(c
						.getColumnIndex("criterio"));
				String criteriodescripcion = c.getString(c
						.getColumnIndex("descripcion"));
				String toc = c.getString(c.getColumnIndex("TOC"));
				String peso = c.getString(c.getColumnIndex("peso"));
				criterio.setCriterioid(idcriterio);
				criterio.setNombre(criterionombre);
				criterio.setDescripcion(criteriodescripcion);
				criterio.setPeso(peso);
				criterio.setToc(toc);
				stemp = "select contenido from contenidos  where  _id in (select idcontenido from criterioscontenido   where idcriterio ='"
						+ idcriterio + "')";
				Cursor c1 = myDataBase.rawQuery(stemp, null);
				if (c1.moveToFirst()) {
					do {
						criterio.addContenido(c1.getString(0));
					} while (c1.moveToNext());
				}
				c1.close();
				stemp = "select idactividad from criterios_actividad where idcriterio = '"
						+ idcriterio + "'";
				Cursor c2 = myDataBase.rawQuery(stemp, null);
				if (c2.moveToFirst()) {
					do {
						criterio.addActividad(c2.getString(0));
					} while (c2.moveToNext());
				}
				c2.close();
				resultado.add(criterio);
			} while (c.moveToNext());
			c.close();
		}
		this.close();
		return resultado;
	}

	public boolean existe(String tabla, String campo, String valor) {
		boolean resultado = false;
		this.openDataBase();
		Cursor c = myDataBase.rawQuery("select * from " + tabla + " where "
				+ campo + " = '" + valor + "'", null);
		if (c.moveToFirst()) {
			resultado = true;
		}
		c.close();
		this.close();
		return resultado;
	}

	public ArrayList<nombreId> getNombreIdSql(String query, String campo) {
		ArrayList<nombreId> resultado = new ArrayList<nombreId>();
		Log.v(TAG, "getNombreIdSQL " + query + " - campo : " + campo);
		this.openDataBase();
		Cursor c = myDataBase.rawQuery(query, null);
		if (c.moveToFirst()) {
			do {
				nombreId item = new nombreId();
				String elid = c.getString(c.getColumnIndex("_id"));
				String nombre = c.getString(c.getColumnIndex(campo));
				if (nombre == null) {
					nombre = "";
				}
				String desc = new String();
				try {
					desc = c.getString(c.getColumnIndex("descripcion"));
				} catch (Exception e) {
					desc = "";
				}
				// Log.v(TAG, "encontrado " + elid + ":" + nombre);
				item.setId(elid);
				item.setNombre(nombre);
				item.setDescripcion(desc);
				resultado.add(item);
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		Log.v(TAG, "total " + resultado.size());
		return resultado;
	}
	public ArrayList<nombreId> getNombreIdDescSql(String query) {
		ArrayList<nombreId> resultado = new ArrayList<nombreId>();
		Log.v(TAG, "getNombreIdDescSQL ");
		this.openDataBase();
		Cursor c = myDataBase.rawQuery(query, null);
		if (c.moveToFirst()) {
			do {
				nombreId item = new nombreId();
				String elid = c.getString(0);
				String nombre = c.getString(1);
				if (nombre == null) {
					nombre = "";
				}
				String desc = new String();
				try {
					desc = c.getString(2);
				} catch (Exception e) {
					desc = "";
				}
				// Log.v(TAG, "encontrado " + elid + ":" + nombre);
				item.setId(elid);
				item.setNombre(nombre);
				item.setDescripcion(desc);
				resultado.add(item);
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		Log.v(TAG, "total " + resultado.size());
		return resultado;
	}

	public String getId(String tabla, String campo) {
		String resultado = new String();
		this.openDataBase();
		String elcampo = this.getCampo(tabla);
		String stemp = "select _id from " + tabla + " where " + elcampo + "= '"
				+ campo + "'";
		Log.v(TAG, stemp);
		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			resultado = c.getString(0);
		}
		c.close();
		this.close();
		return resultado;
	}

	public void deleteFromTables(String tabla, String elid) {
		String idcampo = "id" + tabla.substring(0, tabla.length() - 1);
		String[] lasTablas = tablas.get(tabla);
		String stemp;
		for (int i = 0; i < lasTablas.length; i++) {
			stemp = "delete from " + lasTablas[i] + " where " + idcampo
					+ " = '" + elid + "'";
			this.delete(stemp);
		}
		stemp = "delete from " + tabla + " where _id = '" + elid + "'";
		this.delete(stemp);
	}

	public String getDato(String elid, String tabla, String campo) {
		String resp = new String();
		this.openDataBase();
		String stemp = "select " + campo + " from " + tabla + " where _id = '"
				+ elid + "'";
		// Log.v(TAG,stemp);
		Cursor c = myDataBase.rawQuery(stemp, null);
		if (c.moveToFirst()) {
			resp = c.getString(c.getColumnIndex(campo));
		} else {
			resp = "";
		}
		c.close();
		this.close();
		// Log.v(TAG,campo+":"+resp);
		return resp;
	}

	public ArrayList<gruposInfo> getGruposData() {
		ArrayList<gruposInfo> resultado = new ArrayList<gruposInfo>();
		this.openDataBase();
		Cursor c = myDataBase.rawQuery("select * from grupos", null);
		if (c.moveToFirst()) {
			do {
				gruposInfo item = new gruposInfo();
				item.setId(c.getString(c.getColumnIndex("_id")));
				item.setDescripcion(c.getString(c.getColumnIndex("descripcion")));
				item.setFiltro(c.getString(c.getColumnIndex("filtro")));
				item.setMetagrupo(c.getString(c.getColumnIndex("metagrupo")));
				item.setNombre(c.getString(c.getColumnIndex("grupo")));
				resultado.add(item);
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		return resultado;
	}

	public Map<String, String> getActividadData(String idactividad) {
		Map<String, String> resultado = new HashMap<String, String>();
		this.openDataBase();
		Cursor c = myDataBase.rawQuery(
				"select * from actividades where _id = '" + idactividad + "'",
				null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getColumnCount(); i++) {
				resultado.put(c.getColumnName(i), c.getString(i));
			}
		}
		c.close();
		this.close();
		return resultado;
	}

	public Map<String, Map<String, String>> getCriteriosAlumnoActividad(String idalumno,
			String idactividad) {

		Map<String, Map<String,String>> resultado = this.getMap("select criterios._id, criterios.criterio, criterios_actividad.toc,'00' from criterios_actividad join criterios on criterios._id = criterios_actividad.idcriterio where criterios_actividad.idactividad ="+idactividad); 
		
		for (Map.Entry entry: resultado.entrySet()){
			
			final Map<String,String> criterio = (Map<String,String>) entry.getValue();
			String eval = (String) criterio.get("'00'").toString();
			criterio.put("evaluacion", eval);
		}
		
		this.openDataBase();
		
		if (idalumno.equalsIgnoreCase("2")) {
			Log.v(TAG,"mamon ...");
		}

		Cursor c1 = myDataBase.rawQuery(
				"select idcriterio,evaluacion from evaluacion where idactividad ='"
						+ idactividad + "' and idalumno = '" + idalumno + "'",
				null);
		if (c1.moveToFirst()) {
			do {
				Log.v(TAG," criterios del cerote "+idalumno);
				String critid = c1.getString(c1.getColumnIndex("idcriterio"));
				Map<String, String> temp = resultado.get(				
						c1.getString(c1.getColumnIndex("idcriterio")));
				String eval = c1.getString(c1.getColumnIndex("evaluacion"));
				if (temp != null) {
					Log.v(TAG,"evaluacion  "+eval+ " en criterio "+critid);
				temp.put("evaluacion",eval);
				temp.put("''" ,eval);
				}
				
			} while (c1.moveToNext());
		}
		c1.close();
		this.close();
		return resultado;
	}

	public Map<String, String> getMapSql(String query, String key, String value) {
		Map<String, String> resultado = new HashMap<String, String>();
		this.openDataBase();
		Cursor c = myDataBase.rawQuery(query, null);
		if (c.moveToFirst()) {
			do {
				String a = c.getString(c.getColumnIndex(key));
				if (value.equalsIgnoreCase("")) {
					resultado.put(a, "");
				} else {

					String b = "";
					String[] vals = value.split(":");
//					Log.v(TAG, "vals: " + vals + " .... value: " + value);
					for (int i = 0; i < vals.length; i++) {
						b = b + " " + c.getString(c.getColumnIndex(vals[i]));
//						Log.v(TAG,"mapsql "+a+":"+b);
					}
					resultado.put(a, b.substring(1));
				}
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		return resultado;
	}
	
	public ArrayList<Map<String,String>> getMapList(String query) {
		ArrayList<Map<String,String>> resp = new ArrayList<Map<String, String>>();
		this.openDataBase();
//		Log.v(TAG," lllll "+query);
//		if (query.contains("evaluacion")) {
//			Log.v(TAG,"dllal ");
//		}
		Cursor c = myDataBase.rawQuery(query, null);
		if (c.moveToFirst()) {
			do {
				Map<String,String> temp = new HashMap<String, String>();
				for ( int i = 0; i<c.getColumnCount(); i++){
					temp.put(c.getColumnName(i), c.getString(i));
				}
				resp.add(temp);
				
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		return resp;
	}
	
	public Map<String,Map<String,String>> getMap(String query) {
		Map<String,Map<String,String>> resp = new HashMap<String,Map<String,String>>();
		this.openDataBase();
		Cursor c = myDataBase.rawQuery(query, null);
		if (c.moveToFirst()) {
			do {
				Map<String,String> temp = new HashMap<String, String>();
				for ( int i = 0; i<c.getColumnCount(); i++){
					temp.put(c.getColumnName(i), c.getString(i));
				}
				temp.put("checked","0");
				resp.put(c.getString(c.getColumnIndex("_id")), temp);		
			} while (c.moveToNext());			
		}
		c.close();
		this.close();
		return resp;
	}


    public Map<String,Map<String,String>> getMapField(String query, String field) {
        Map<String,Map<String,String>> resp = new HashMap<String,Map<String,String>>();
        this.openDataBase();
        Cursor c = myDataBase.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Map<String,String> temp = new HashMap<String, String>();
                for ( int i = 0; i<c.getColumnCount(); i++){
                    temp.put(c.getColumnName(i), c.getString(i));
                }
                temp.put("checked","0");
                resp.put(c.getString(c.getColumnIndex(field)), temp);
            } while (c.moveToNext());
        }
        c.close();
        this.close();
        return resp;
    }
	

	public void updateEvalCriterio(String idalumno, String idactividad,
			String idcriterio, String eval, String fecha) {
		this.openDataBaseWrite();
		String stemp = "delete from evaluacion where idalumno ='" + idalumno
				+ "' and idactividad='" + idactividad + "' and idcriterio='"
				+ idcriterio + "'";
		// Log.v(TAG,"delete "+stemp);
		myDataBase.execSQL(stemp);
		stemp = "insert into evaluacion(idalumno,idactividad,idcriterio,evaluacion,fecha) values ";
		stemp = stemp + "('" + idalumno + "','" + idactividad + "','"
				+ idcriterio + "','" + eval + "','" + fecha + "')";
//		Log.v(TAG, "guardando evaluación: " + stemp);
		myDataBase.execSQL(stemp);
		this.close();
	}

	public String getRawSql(String query) {
		String resultado = "";
		this.openDataBase();
		return resultado;
	}

	public evalCriterioAlumno 	getEvalCriterios(Integer idalumno, String idasignatura) {
		UtilsLib.evalCriterioAlumno resp = utils.new evalCriterioAlumno(idalumno);
		this.openDataBase();
		String query = String
				.format("select criterios_actividad.toc,criterios_actividad.idactividad,criterios_actividad.idcriterio,actividades.actividad,criterios.criterio from criterios_actividad join  criterios on criterios_actividad.idcriterio = criterios._id  join actividades on criterios_actividad.idactividad = actividades._id order by criterios.criterio",idalumno);
//		Cursor c1 = myDataBase.rawQuery(query, null);
//		if (c1.moveToFirst()) {
//			do {
//				String criterio = c1.getString(c1.getColumnIndex("criterio"));
//				Integer evaluacion = 0;
//				Integer idcriterio = c1.getInt(c1.getColumnIndex("idcriterio"));
//				Integer toc = c1.getInt(c1.getColumnIndex("toc"));
//				String  actividad = c1.getString(c1.getColumnIndex("actividad"));
//				Integer  idactividad = c1.getInt(c1.getColumnIndex("idactividad"));
//				UtilsLib.evalCriterio evalCriterio = utils.new evalCriterio(actividad,evaluacion,toc,idactividad,idcriterio,criterio); 
//				Log.v(TAG, "INICIALIZANDO: "+criterio + ":" + evaluacion + ":" + idcriterio + ":"+ toc);
//				resp.addEvaluacion(evalCriterio);
//			} while (c1.moveToNext());
//		}
//		c1.close();
//	
		query = String.format("select criterios.peso,evaluacion.idactividad, evaluacion.idalumno, actividades.actividad, criterios.criterio,evaluacion.idcriterio, evaluacion.evaluacion, criterios_actividad.toc from evaluacion join  criterios on evaluacion.idcriterio = criterios._id join criterios_actividad  on criterios_actividad.idcriterio = evaluacion.idcriterio and criterios_actividad.idactividad = evaluacion.idactividad join actividades on evaluacion.idactividad = actividades._id where idalumno = %d order by criterios.criterio",
						idalumno);
		Cursor c = myDataBase.rawQuery(query, null);
		if (c.moveToFirst()) {
			do {
				String criterio = c.getString(c.getColumnIndex("criterio"));
				Integer evaluacion = c.getInt(c.getColumnIndex("evaluacion"));
				Integer idcriterio = c.getInt(c.getColumnIndex("idcriterio"));
				Integer toc = c.getInt(c.getColumnIndex("toc"));
				String  actividad = c.getString(c.getColumnIndex("actividad"));
				Integer  idactividad = c.getInt(c.getColumnIndex("idactividad"));
				String peso = c.getString(c.getColumnIndex("peso"));
				if (peso.length()<1) {
					peso = "0";
				}
				String q= String.format("select * from eval_criterios_alumno where idalumno = %d and idcriterio = %d",
						idalumno,idcriterio);
				String eval1 = "0";
				String eval2 = "0";
				String eval3 ="0";
				String eval4 = "0";
				Cursor c1 = myDataBase.rawQuery(q,null);
				if (c1.moveToFirst()) {
					do {
						int eo = c1.getColumnIndex("eval1");
//						Log.VERBOSE(TAG,c1.getColumnIndex("eval1"));
						eval1 = c1.getString(eo);
						if (eval1.length() < 1) {
							eval1 = "0";
						}
//						eval2 =  c1.getString(c.getColumnIndex("eval2"));
//						eval3 =  c1.getString(c.getColumnIndex("eval3"));
//						eval4 =  c1.getString(c.getColumnIndex("eval4"));
					} while (c1.moveToNext());
				}
				c1.close();
				UtilsLib.evalCriterio evalCriterio = utils.new evalCriterio(actividad,evaluacion,toc,idactividad,idcriterio,criterio,eval1, eval2, eval3, eval4, peso); 
//				Log.v(TAG, criterio + ":" + evaluacion + ":" + idcriterio + ":"+ toc+": peso - "+peso);
				resp.addEvaluacion(evalCriterio);
			} while (c.moveToNext());
				//		Cursor c1 = myDataBase.rawQuery(query, null);
//				if (c1.moveToFirst()) {
//				do {
//					String criterio = c1.getString(c1.getColumnIndex("criterio"));
//					Integer evaluacion = 0;
//					Integer idcriterio = c1.getInt(c1.getColumnIndex("idcriterio"));
//					Integer toc = c1.getInt(c1.getColumnIndex("toc"));
//					String  actividad = c1.getString(c1.getColumnIndex("actividad"));
//					Integer  idactividad = c1.getInt(c1.getColumnIndex("idactividad"));
//					UtilsLib.evalCriterio evalCriterio = utils.new evalCriterio(actividad,evaluacion,toc,idactividad,idcriterio,criterio); 
//					Log.v(TAG, "INICIALIZANDO: "+criterio + ":" + evaluacion + ":" + idcriterio + ":"+ toc);
//					resp.addEvaluacion(evalCriterio);
//				} while (c1.moveToNext());
//			}
//			c1.close();
	//	;
		}
		c.close();
		this.close();
		return resp;
	}

	public void updateEvalCriterioAlumno(Integer alumnoid, int idcriterio, String eval1, String eval2, String eval3, String eval4) {
			this.openDataBaseWrite();
			String stemp = String.format("delete from eval_criterios_alumno where idalumno=%d and idcriterio = %d",alumnoid,idcriterio);
			myDataBase.execSQL(stemp);
			stemp = String.format("insert into eval_criterios_alumno(idalumno,idcriterio,eval1,eval2,eval3,eval4) values (%d,%d,'%s','%s','%s','%s')",alumnoid,idcriterio,eval1,eval2,eval3,eval4);
//			Log.v(TAG, "guardando criterio alumno: " + stemp);
			myDataBase.execSQL(stemp);
			this.close();
		
	}
	
	public Long gettimestamp() {
//		Long tsLong = System.currentTimeMillis()/1000;
//		Long tsLong = System.currentTimeMillis();
//		return  tsLong.toString();
		return System.currentTimeMillis();
	}

	public ArrayList<Map<String, String>> getMergedMapList(String subquery,
			String subquery2, String campo) {
		ArrayList<Map<String,String>> resp = new ArrayList<Map<String, String>>();
		String campo2 = campo;
		if (subquery2.contains("criterios_actividad")) {
			campo2 = "toc";
		}
		Map<String,String> subelement = this.getMapSql(subquery2, campo, campo2);
		
		
		this.openDataBase();
		Cursor c = myDataBase.rawQuery(subquery, null);
		if (c.moveToFirst()) {
			do {
				Map<String,String> temp = new HashMap<String, String>();
				for ( int i = 0; i<c.getColumnCount(); i++){
					String key = c.getColumnName(i);
					String val = c.getString(i);
					temp.put(c.getColumnName(i), c.getString(i));
				}			
				if (subelement.get(temp.get("_id")) != null) {	
					temp.put("checked","1");
					temp.put("toc", subelement.get(temp.get("_id")));
				} else {
					temp.put("checked","0"); 
					temp.put("toc","");
				}
				if (subquery2.contains("alumnos_grupos")) {
//					Log.v(TAG," fechas "+subelement.get("fecha_baja"));
					temp.put("fecha_alta", subelement.get("fecha_alta"));
					temp.put("fecha_baja", subelement.get("fecha_baja"));
				}

				resp.add(temp);
				
			} while (c.moveToNext());
		}
		c.close();
		this.close();
		return resp;

	}

	public Map<String, String> getMapId(String query) {
	Map<String,String> resp = new HashMap<String, String>();	
	this.openDataBase();
	Cursor c = myDataBase.rawQuery(query, null);
	if (c.moveToFirst()) {
		do {
			Map<String,String> temp = new HashMap<String, String>();
			for ( int i = 0; i<c.getColumnCount(); i++){
				String key = c.getColumnName(i);
				String val = c.getString(i);
				temp.put(c.getColumnName(i), c.getString(i));
			}			
			resp = temp;		
		} while (c.moveToNext());
	}
	c.close();
	this.close();
	return resp;

	}
	
//	public Drawable getimg (String eo) {
//		Drawable resp = Context.getResources().getDrawable(R.drawable.siguiente_m);
//		if (eo.equalsIgnoreCase("00")) {
//			resp =  getActivity().getResources().getDrawable(R.drawable.siguiente_m);
//		}
//		return resp;
//		
//	}

	
	public ArrayList<Map<String,String>> getGrupAlumno(String id) {
		ArrayList<Map<String,String>> grupos = this.getMapList("select * from grupos");
		ArrayList<Map<String,String>> alumnosgrupo = this.getMapList(String.format("select * from alumnos_grupos where idalumno = '%s'",id));
		ArrayList<Map<String,String>> temp = new ArrayList<Map <String,String>>();
		ArrayList<Map<String,String>> resp = new ArrayList<Map <String,String>>();
		for (int j = 0; j < grupos.size(); j++) {
			Map<String,String> grupo = grupos.get(j);
			String idgrupo = grupo.get("_id");
			grupo.put("fecha_alta", "");
			grupo.put("fecha_baja", "");
			grupo.put("checked","0");
			for (int k = 0; k<alumnosgrupo.size(); k++) {
				Map<String,String> alumnoengrupo = alumnosgrupo.get(k);
				if (alumnoengrupo.get("idgrupo").equalsIgnoreCase(idgrupo)) {
					grupo.put("fecha_alta", alumnoengrupo.get("fecha_alta"));
					grupo.put("fecha_baja", alumnoengrupo.get("fecha_baja"));
					grupo.put("checked", "1");
					break;
				}
			}
			temp.add(grupo);
		}
		
		
//		
//		ArrayList<Map<String,String>> temp = this.getMergedMapList("select * from grupos", String.format("select * from alumnos_grupos where idalumno = '%s'",id ),"idalumno");
//	
		for (int i=0; i<temp.size(); i++) {
			Boolean esmiembro = false;
			Map <String,String> grp = temp.get(i);
			if (grp.get("metagrupo").equalsIgnoreCase("1")) {
				ArrayList<Map<String,String>> mgrupo = this.getMapList(grp.get("filtro"));
				for (int j=0; j<mgrupo.size();j++) {
					if ( mgrupo.get(j).get("_id").equalsIgnoreCase(id)) {
						esmiembro = true;
					}
				}
			}
			if (esmiembro) {
				grp.put("checked", "1");
			}
			resp.add(grp);
		}
		
		
		return resp;
	}
}