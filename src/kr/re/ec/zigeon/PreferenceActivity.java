package kr.re.ec.zigeon;

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

public class PreferenceActivity extends Activity {
	private Intent intent;
	
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

	public class PrefsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			LogUtil.v("4");
			addPreferencesFromResource(R.xml.preference);

			// Get the custom preference
			Preference Pref_myInfo = (Preference) findPreference("preference_myInfo");
			Pref_myInfo
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							Toast.makeText(getBaseContext(),
									"The custom preference has been clicked",
									Toast.LENGTH_LONG).show();
							/*
							SharedPreferences customSharedPreference = getSharedPreferences(
									"myCustomSharedPrefs",
									Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = customSharedPreference
									.edit();
							editor.putString("myCustomPref",
									"The preference has been clicked");
							editor.commit();*/
							intent = new Intent(PreferenceActivity.this,
									TestActivity.class);
							startActivity(intent);
							
							return true;
						}

					});

		}

	}
}