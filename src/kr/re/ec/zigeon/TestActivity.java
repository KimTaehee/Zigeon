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

public class TestActivity extends Activity{

	private Intent intent;
	private Button testBtn;
	private SharedPreferences pref;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		/*
		getFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content,
						new kr.re.ec.zigeon.PreferenceFrag()).commit();
*/
		
		
		PreferenceManager.setDefaultValues(TestActivity.this, R.xml.preference,
				false);
		  LogUtil.v("1");
		  testBtn = (Button)findViewById(R.id.test_btn1);
		  LogUtil.v("2");
		  pref = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
		  LogUtil.v("test=" + pref.getBoolean("preference_CheckBox_notificationSound", false));
		  if(pref.getBoolean("preference_CheckBox_notificationSound", false))
		  {
			  LogUtil.v("3");
			  testBtn.setText("Checked");
		  }else
		  {
			  testBtn.setText("not Checked");
		  }
		  LogUtil.v("5");
		  testBtn.setOnClickListener(new OnClickListener() {
		  
		  @Override public void onClick(View v) {
			  intent = new Intent(TestActivity.this,
						PreferenceActivity.class);
				startActivity(intent);
		 
		  } });
		 
	}
/*	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_preference);
	}*/
}
