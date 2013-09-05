package kr.re.ec.zigeon;

import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class TestActivity2 extends Activity{

	//private Button Btn;
	private SharedPreferences pref;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test2);
		//Btn = (Button) findViewById(R.id.button1);
		pref = PreferenceManager.getDefaultSharedPreferences(TestActivity2.this);
		
		LogUtil.v("test = " +pref.getBoolean("preference_CheckBox_notificationSound", false));
	}
}
