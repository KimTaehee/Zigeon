package kr.re.ec.zigeon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandmarkWriteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(kr.re.ec.zigeon.R.layout.activity_landmark_write);

		final Button btn = (Button) findViewById(R.id.landmark_write_btn_map);

		btn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				startActivity(new Intent(LandmarkWriteActivity.this,MapActivity.class));
				overridePendingTransition(0, 0); //no switching animation
			}
		});
	}
}
