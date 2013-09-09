package kr.re.ec.zigeon;

import kr.re.ec.zigeon.dataset.PreferenceDataset;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PreferenceActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

	public class PrefsFragment extends PreferenceFragment implements OnPreferenceClickListener{

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference);
			
			// Get the custom preference
			Preference Pref_myInfo = (Preference) findPreference(PreferenceDataset.MY_INFO);
			Preference Pref_PwChange = (Preference) findPreference(PreferenceDataset.PW_CHANGE);
			// preference set ClickListncer
			Pref_myInfo.setOnPreferenceClickListener(this);
			Pref_PwChange.setOnPreferenceClickListener(this);
		}
		
		public boolean onPreferenceClick(Preference preference) {
			  LogUtil.v("PW test= " + preference.getKey().equals(PreferenceDataset.PW_CHANGE));
			//preference.getKey().equals("key")
	        if (preference.getKey().equals(PreferenceDataset.MY_INFO)) {
	        	Toast.makeText(getBaseContext(),
						"The custom preference_My_info has been clicked",
						Toast.LENGTH_LONG).show();

	        } else if (preference.getKey().equals(PreferenceDataset.PW_CHANGE)) {
	        	Toast.makeText(getBaseContext(),
						"The custom preference_PW_Change has been clicked",
						Toast.LENGTH_LONG).show();
	        	DialogSimple();
	        } else if (preference.getKey().equals("priority")) {

	        } else {

	        }
	        return false;
	    }
	}
	
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
	}
	
}