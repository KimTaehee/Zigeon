package kr.re.ec.zigeon;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingAcitivity extends PreferenceFragment{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_preference);
	}
}
