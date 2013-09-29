/**
 * Class Name: AlertManager
 * Description: show AlertDialog
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130831
 * Modified Date: 
 */

package kr.re.ec.zigeon.util;

import kr.re.ec.zigeon.LoginActivity;
import kr.re.ec.zigeon.PreferenceActivity;
import kr.re.ec.zigeon.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * @author KimTaehee
 * usage: new AlertManager(this,"Blank Comment? ^^","Confirm");
 */
public class AlertManager {
	//int returnType=0;

	public AlertManager() {

	}

	public void show(Context context, String contents, String title, int alertType) {
		switch (alertType) {
		case Constants.ALERT_OK_ONLY:
		{
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
	}

	public void show(Context context, String contents, String title, int alertType
			, DialogInterface.OnClickListener dialogListener) {
		switch(alertType) {
		case Constants.ALERT_YES_NO:
		{	
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
			alt_bld.setMessage(contents).setCancelable(false)
			.setPositiveButton(R.string.alert_yes, dialogListener)
			.setNegativeButton(R.string.alert_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// Action for 'NO' Button
					dialog.cancel();
				}
			});
			
			AlertDialog alert = alt_bld.create();
			// Title for AlertDialog
			// Icon for AlertDialog
			alert.show();
		}
		}
	}
}


