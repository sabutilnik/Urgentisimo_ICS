package uno.Urgentisimo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class alumnoEvalInfo {
	private static final String TAG = "URGENTISIMO : ALUMNO EVALINFO : ";
	private String idalumno;
	private String nombre;
	private String apellidos;
	private String idactividad;
	private Map<String,Map<String,String>> criteriosEvaluacion;
	private DataBaseHelper db;
	

	public alumnoEvalInfo(Context c, String idalumno, String idactividad, String nombre) {
		db = new DataBaseHelper(c.getApplicationContext());
		Long ini = db.gettimestamp();
		Log.v(TAG," Inicializa alumnoEvalInfo "+ini);
	    Log.v(TAG,"alumno "+idalumno+" idactividad "+idactividad);

		this.idactividad = idactividad;
		this.idalumno = idalumno;
		this.nombre = nombre;
		criteriosEvaluacion = db.getCriteriosAlumnoActividad(idalumno, idactividad);
		Long fin = db.gettimestamp();
		Log.v(TAG,"Termino de inicializar alumnoEvalInfo "+fin+" duracion : "+(fin-ini));
	}
	
	public String getNombre() {
		return nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public String getId() {
		return idalumno;
	}
	public Map<String, Map<String, String>> getCriterios() {
		return criteriosEvaluacion;
	}





	public String getEvalCriterio(String crit) {
		return criteriosEvaluacion.get(crit).get("evaluacion");
	}
	public void setEvalCriterio(String crit, String eval, String fecha) {
		String evalOld = getEvalCriterio(crit);
//		Log.v(TAG,"eval:"+getEvalCriterio(crit)+"="+eval);
		if (eval == null ) {
			Log.v(TAG,"eval es null");
		}
		if (evalOld == null ) {
			Log.v(TAG,"oldEval es null");
		}
		if (eval.equalsIgnoreCase(evalOld) == false) {
			Log.v(TAG,idalumno+":"+idactividad+":eval:"+getEvalCriterio(crit)+"="+eval+" el criterio "+crit);
			Map<String, String> temporal = criteriosEvaluacion.get(crit);
			temporal.put("evaluacion", eval);
			db.updateEvalCriterio(idalumno, idactividad, crit, eval,fecha);
		}
	}
	public float getMedia()  {
		float nota = 0;
		Iterator it = criteriosEvaluacion.entrySet().iterator();
		int i = 0;
		
		for (Map.Entry entry: criteriosEvaluacion.entrySet()){
			float toc = 100;
			final Map<String,String> criterio = (Map<String,String>) entry.getValue();
			String creval = criterio.get("evaluacion");
			float evaluacion = 0;
			if (!creval.equalsIgnoreCase("")) {
				evaluacion = Integer.valueOf(criterio.get("evaluacion"));
			} else {
				Log.v(TAG,"evaluacion vacia snif ");
			}
			try {
				toc = Float.valueOf(criterio.get("toc"));
			} catch(Exception e) {
				Log.v(TAG,"no tiene toc ");
			}
	        float media = evaluacion*100/toc;
	        nota = nota + media;
	        i = i+1;
	    }
		return nota/i;
	}
}
