package kr.re.ec.zigeon;

import kr.re.ec.zigeon.dataset.PreferenceDataset;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
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
			
			// preference set ClickListncer
			Pref_myInfo.setOnPreferenceClickListener(this);

		}
		
		public boolean onPreferenceClick(Preference preference) {
			  
			//preference.getKey().equals("key")
	        if (preference.getKey().equals(PreferenceDataset.MY_INFO)) {
	        	Toast.makeText(getBaseContext(),
						"The custom preference has been clicked",
						Toast.LENGTH_LONG).show();

	        } else if (preference.getKey().equals(PreferenceDataset.CHECKBOX_BUBBLEHEAD)) {

	        } else if (preference.getKey().equals("priority")) {

	        } else {

	        }
	        return false;
	    }
	}
	
	
}