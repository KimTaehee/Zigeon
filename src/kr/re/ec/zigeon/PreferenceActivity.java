package kr.re.ec.zigeon;

import kr.re.ec.zigeon.dataset.PreferenceDataset;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;


@SuppressLint("ValidFragment")
public class PreferenceActivity extends Activity{
	private Intent intent;
	private ActivityManager activityManager = ActivityManager.getInstance();
	public static boolean isBalloonNotificationOn;

	@SuppressLint("ValidFragment")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		/*******add activity list********/
		activityManager.addActivity(this);

		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();

		PrefsFragment mPrefsFragment = new PrefsFragment();
		mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
		mFragmentTransaction.commit();

		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new PrefsFragment()).commit();
		PreferenceManager.setDefaultValues(PreferenceActivity.this, R.xml.preference, 
				false);



	}

	public class PrefsFragment extends PreferenceFragment implements
	OnPreferenceClickListener, OnPreferenceChangeListener {

		private CheckBoxPreference Pref_BalloonNotification;
		private Preference Pref_Logout;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference);

			// Get the custom preference
			//			Preference Pref_myInfo = (Preference) findPreference(PreferenceDataset.MY_INFO);
			//			Preference Pref_PwChange = (Preference) findPreference(PreferenceDataset.PW_CHANGE);
			Pref_BalloonNotification = (CheckBoxPreference) findPreference(PreferenceDataset.CHECKBOX_BUBBLEHEAD);
			Pref_Logout = (Preference) findPreference(PreferenceDataset.LOGOUT);

			// preference set ClickListncer
			//			Pref_myInfo.setOnPreferenceClickListener(this);
			//			Pref_PwChange.setOnPreferenceClickListener(this);
			Pref_BalloonNotification.setOnPreferenceChangeListener(this);
			Pref_Logout.setOnPreferenceClickListener(this);
			
			SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
			Pref_BalloonNotification.setChecked(pref.getBoolean("isBalloonNotificationOn", true)); //default true
		}

		public boolean onPreferenceClick(Preference preference) {

			LogUtil.v("PW test= " + preference.getKey().equals(PreferenceDataset.PW_CHANGE));
			//preference.getKey().equals("key")
			//	        if (preference.getKey().equals(PreferenceDataset.MY_INFO)) {
			//	        	Toast.makeText(getBaseContext(),
			//						"The custom preference_My_info has been clicked",
			//						Toast.LENGTH_LONG).show();
			//
			//	        } else if (preference.getKey().equals(PreferenceDataset.PW_CHANGE)) {
			//	        	Toast.makeText(getBaseContext(),
			//						"The custom preference_PW_Change has been clicked",
			//						Toast.LENGTH_LONG).show();
			//	        	//DialogSimple();
			//	        } else 
			if (preference.getKey().equals(PreferenceDataset.LOGOUT)) {
				DialogLogout();
			}
			
			return false;
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference.getKey().equals(PreferenceDataset.CHECKBOX_BUBBLEHEAD)) {
				//save status
				SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
				SharedPreferences.Editor editor = pref.edit(); //load Editor
				
				isBalloonNotificationOn = Boolean.valueOf(newValue.toString());
				editor.putBoolean("isBalloonNotificationOn", isBalloonNotificationOn);
				Pref_BalloonNotification.setChecked(isBalloonNotificationOn);
				
				LogUtil.v("newValue is: " + newValue + isBalloonNotificationOn);
				
				editor.commit();	//save
			}
			return false;
		}
	}
	/*
	private void DialogSimple(){
	    AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
	    alt_bld.setMessage("Do you want to close this window ?").setCancelable(
	        false).setPositiveButton("Yes",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            // Action for 'Yes' Button
	        }
	        }).setNegativeButton("No",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            // Action for 'NO' Button
	            dialog.cancel();
	        }
	        });
	    AlertDialog alert = alt_bld.create();
	    // Title for AlertDialog
	    alert.setTitle("Title");
	    // Icon for AlertDialog
	    alert.setIcon(R.drawable.icon);
	    alert.show();
	}*/

	private void DialogLogout(){
		DialogInterface.OnClickListener dialogListner = new DialogInterface.OnClickListener() { //click yes
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Action for 'Yes' Button
				intent = new Intent(PreferenceActivity.this,
						LoginActivity.class);
				startActivity(intent);
				activityManager.finishAllActivity();
			}
		};

		LogUtil.v("dialoglogout() called");
		new AlertManager().show(this, "Logout. Continue?", "Confirm"
				, Constants.ALERT_YES_NO, dialogListner);		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		/*********remove activity list******/
		activityManager.removeActivity(this);
	}

}