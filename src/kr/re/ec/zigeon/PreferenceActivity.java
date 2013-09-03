package kr.re.ec.zigeon;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenceActivity extends PreferenceFragment{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_preference);
	}
}
