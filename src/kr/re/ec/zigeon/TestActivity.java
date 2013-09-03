package kr.re.ec.zigeon;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TestActivity extends PreferenceActivity{
/*
	private Button testBtn;
	private Intent mIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		testBtn = (Button)findViewById(R.id.test_btn1);
		testBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIntent = new Intent(TestActivity.this, PreferenceActivity.class);
				startActivity(mIntent);
			}
		});
	}*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_preference);
	}
}
