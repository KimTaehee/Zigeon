package kr.re.ec.zigeon;

import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

	private Intent intent;
	private Button testBtn;
	private Button testBtn2;
	private SharedPreferences pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		/*
		 * getFragmentManager() .beginTransaction()
		 * .replace(android.R.id.content, new
		 * kr.re.ec.zigeon.PreferenceFrag()).commit();
		 */

		PreferenceManager.setDefaultValues(TestActivity.this, R.xml.preference,
				false);
		testBtn = (Button) findViewById(R.id.test_btn1);
		testBtn2 = (Button) findViewById(R.id.test_btn2);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getBoolean("preference_CheckBox_notificationSound", false)) {
			testBtn.setText("Checked");
		} else {
			testBtn.setText("not Checked");
		}
		testBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(TestActivity.this, PreferenceActivity.class);
				startActivity(intent);

			}
		});
		
		testBtn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(TestActivity.this, TestActivity2.class);
				startActivity(intent);
				
			}
		});

	}
	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * addPreferencesFromResource(R.xml.activity_preference); }
	 */
}
