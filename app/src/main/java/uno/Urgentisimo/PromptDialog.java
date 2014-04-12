package uno.Urgentisimo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.EditText;

/**
 * helper for Prompt-Dialog creation
 */
public abstract class PromptDialog extends AlertDialog.Builder implements OnClickListener {
	private static final String TAG = "PROMPT HELPER";
	private final EditText input;

	/**
	 * @param context
	 * @param title resource id
	 * @param message resource id
	 */
	public PromptDialog(Context context, String title, String message) {
		super(context);
		setTitle(title);
		setMessage(message);
		Log.v(TAG,"");
		input = new EditText(context);
		setView(input);
		setPositiveButton(R.string.ok, this);
		setNegativeButton(R.string.cancel, this);
	}

	/**
	 * will be called when "cancel" pressed.
	 * closes the dialog.
	 * can be overridden.
	 * @param dialog
	 */
	public void onCancelClicked(DialogInterface dialog) {
		dialog.dismiss();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			if (onOkClicked(input.getText().toString())) {
				Log.v(TAG,"ok pressed "+input.getText().toString());
				dialog.dismiss();
			}
		} else {
			onCancelClicked(dialog);
		}
	}

	abstract public boolean onOkClicked(String input);
}
