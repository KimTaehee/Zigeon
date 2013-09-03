package kr.re.ec.zigeon;

import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity{

	private Button testBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_test);
				getFragmentManager().beginTransaction().replace(android.R.id.content, new kr.re.ec.zigeon.PreferenceActivity()).commit();
				
		/*
		testBtn = (Button)findViewById(R.id.test_btn1);
		testBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});*/
	}
/*	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_preference);
	}*/
}
