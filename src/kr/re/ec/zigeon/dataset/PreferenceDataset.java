/**
 * 130910 kim ji hong
 * */

package kr.re.ec.zigeon.dataset;

import kr.re.ec.zigeon.R;
import kr.re.ec.zigeon.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**use Example(in Activity)
 * 
 * preference = PreferenceDataset.getInstance(this);
	preference.setPreference(this);
		
		if(preference.sharedPref.getBoolean(PreferenceDataset.CHECKBOX_SOUND,false))
		{
			testBtn.setText("Checked");
		} else {
			testBtn.setText("not Checked");	
		}
 **/
public class PreferenceDataset {
	public static String MY_INFO = "preference_myInfo";
	public static String PW_CHANGE = "preference_PWchange";
	public static String CHECKBOX_SOUND = "preference_CheckBox_notificationSound";
	public static String CHECKBOX_VIBRATION = "preference_CheckBox_notificationVibration";
	public static String CHECKBOX_BUBBLEHEAD = "preference_CheckBox_notification_bubblehead";
	public static String CLREA_CACHE_IMAGE = "preference_clearCacheImage";
	public static String VERSION = "preference_version";
	public static String IS_BALLON = "preference_whatIsBallon";
	public static String LOGOUT = "preference_logout";
	public static PreferenceDataset instance;
	public SharedPreferences sharedPref;
	public Context mContext;
	
	private PreferenceDataset(Context context) {
		mContext = context;
		LogUtil.v("uihandler instance created");
	}

	public static PreferenceDataset getInstance(Context context) {
		if (instance == null) {
			instance = new PreferenceDataset(context);
		}
		return instance;
	}
	
	public void setPreference(Context context) { 
		PreferenceManager.setDefaultValues(context, R.xml.preference,
					false);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
	}
}
