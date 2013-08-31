/**
 * Class Name: AlertManager
 * Description: show AlertDialog
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130831
 * Modified Date: 
 */

package kr.re.ec.zigeon.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * @author KimTaehee
 * usage: new AlertManager(this,"Blank Comment? ^^","Confirm");
 */
public class AlertManager {
	public AlertManager(Context context, String contents, String title) { 
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setPositiveButton(title, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();	
			}
		});
		alert.setMessage(contents);
		alert.show();
	}
}
