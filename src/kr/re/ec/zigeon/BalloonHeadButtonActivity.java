package kr.re.ec.zigeon;

import kr.re.ec.zigeon.handler.BalloonService;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BalloonHeadButtonActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balloon_head_button);
		
		Button launch = (Button)findViewById(R.id.button1);
		launch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtil.v("Ŭ�����??");
				startService(new Intent(BalloonHeadButtonActivity.this, BalloonService.class));
			}
		});

		Button stop = (Button)findViewById(R.id.button2);
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopService(new Intent(BalloonHeadButtonActivity.this, BalloonService.class));
			}
		});

	}

	@Override
	protected void onResume() {
		Bundle bundle = getIntent().getExtras();

		if(bundle != null && bundle.getString("LAUNCH").equals("YES")) {
			startService(new Intent(BalloonHeadButtonActivity.this, BalloonService.class));
		}
		super.onResume();
	}
}
