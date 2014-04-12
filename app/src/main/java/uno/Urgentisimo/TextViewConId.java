package uno.Urgentisimo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

/**
 * helper for Prompt-Dialog creation
 */
public abstract class TextViewConId extends TextView  {
	private static final String TAG = "PROMPT HELPER";
	private String alumnoId;
	private String grupoId;

	/**
	 * @param context
	 * @param title resource id
	 * @param message resource id
	 */
	public TextViewConId(Context context, String elId) {
		super(context);
		setAlumnoId(elId);
	}

	public void setAlumnoId(String s) {
		alumnoId = s;		
	}

	public String getAlumnoId () {
		return alumnoId;
	}
	public String getGrupoId(){
		return grupoId;
	}
	public void setGrupoId(String s) {
		grupoId = s;
	}
}
