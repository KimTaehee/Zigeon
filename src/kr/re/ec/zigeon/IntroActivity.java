/**
 * Class Name: IntroActivity
 * Description: Start of Application
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130828
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class IntroActivity extends Activity {
	private final int LOADING_DELAY_MS = 2500; //loading time. (ms)
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(((String)msg.obj).compareTo("login")==0) {
				//LogUtil.v("handleMessage invoked. msg: " + (String)msg.obj + ". call finish()");
				startActivity(new Intent(IntroActivity.this,LoginActivity.class)); //call Login 
				finish();	//cannot load Intro again.
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		LogUtil.v("onCreate invoked!");
		
		Message msg = new Message();
		msg.obj = new String("login");
		handler.sendMessageDelayed(msg, LOADING_DELAY_MS); //send msg after N seconds.

	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
}
