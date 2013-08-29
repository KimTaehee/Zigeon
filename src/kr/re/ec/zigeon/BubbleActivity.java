/**
 * ClassName : BubbleActivity
 * Class explain : bubble in 
 * */

package kr.re.ec.zigeon;


import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.handler.UpdateService;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.gcm.GCMRegistrar;
import com.nhn.android.maps.maplib.NGeoPoint;

public class BubbleActivity extends Activity {
	public static final int BUBBLETEST_BUTTON_START_Y = 1487; /**/
	public static final int BUBBLETEST_BUTTON_INTERVAL = 150;

	private ImageView bubbleImage;
	private ImageView bubbleImage2;
	private ImageView bubbleImage3;
	private ImageView bubbleImage4;
	private RelativeLayout rootLayout;
	private Animation bubblemoveAnimation;

	private Intent mIntent;
	/** The m register task. (GCM) */
		
	private SoapParser soapParser; //TODO: test. will be deleted.
	
	private UIHandler uiHandler; 
	private Handler messageHandler = new Handler() { //receiver from UpdateService
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				LogUtil.v("switched to MSG_TYPE_LANDMARK");
				String str = "";
				LandmarkDataset landmarkArr[] =(LandmarkDataset[]) msg.obj; 
				//LogUtil.v("landmarkArr.length : "+ landmarkArr.length);
				for(int i=0;i<landmarkArr.length;i++){
					str += landmarkArr[i].name + "\n";
				}
				//tvLandmarkTest.setText(str);
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				//tvPostingTest.setText(msg.getData().getString("msg"));
				break;
			}
			case Constants.MSG_TYPE_COMMENT:
			{
				//tv Test.setText(msg.getData().getString("msg"));
				break;
			}
			case Constants.MSG_TYPE_MEMBER:
			{
				//tvPostingTest.setText(msg.getData().getString("msg"));
				break;
			}
			case Constants.MSG_TYPE_TEST:
			{	

				break;
			}
			case Constants.MSG_TYPE_LOCATION:
			{
				//NGeoPoint instead of android.location 
				NGeoPoint location = (NGeoPoint)msg.obj;
				String str = location.getLatitude() + "\n" + location.getLongitude() + "\n";
				//						+ "\n" + location.getAccuracy() + "\n" + location.getProvider();
				//tvGPSTest.setText(str);
				break;
			}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubble);
		
		/*********** MemberDataset Global test ***********/
		MemberDataset mem = MemberDataset.getInstance();
		LogUtil.v("MEMBERDATASET GLOBAL ID: " + mem.id);
				
		/*************** GCM registration **************/
		LogUtil.v("push register start");
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			LogUtil.v("regId " + regId);
			GCMRegistrar.register(this, Constants.GCM_PROJECT_ID);
            // Automatically registers application on startup.
			
        // if have regId but not registered on server, retry. 
		} else {
			LogUtil.i("already registered device! ready to receive msg!");
			LogUtil.v("regId " + regId);
		}
		
		/*************** UI init  ************/
//		tvGPSTest = (TextView)findViewById(R.id.bubble_tvGPSTest);
//		tvLandmarkTest = (TextView)findViewById(R.id.bubble_tvLandmarkTest);
//		tvPostingTest = (TextView)findViewById(R.id.bubble_tvPostingTest);
//		btnServiceStopTest = (Button)findViewById(R.id.btnServiceStopTest);
//		btnServiceStopTest.setOnClickListener(this);

		bubbleImage = (ImageView) findViewById(R.id.bubbleImage);
		bubbleImage2 = (ImageView) findViewById(R.id.bubbleImage2);
		bubbleImage3 = (ImageView) findViewById(R.id.bubbleImage3);
		bubbleImage4 = (ImageView) findViewById(R.id.bubbleImage4);
		rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
		
		/************* anim part ****************/
		// to resize image
		LayoutParams params = (LayoutParams) bubbleImage.getLayoutParams();
		params.width = 120;
		bubbleImage.setLayoutParams(params);

		// anim effect
		bubblemoveAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bubblemove);
		bubbleImage.setAnimation(bubblemoveAnimation);

		

		LayoutParams params2 = (LayoutParams) bubbleImage2.getLayoutParams();
		params2.width = 100;
		

		bubbleImage2.setLayoutParams(params2);
		bubblemoveAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bubblemove2);
		bubbleImage2.setAnimation(bubblemoveAnimation);

		LayoutParams params3 = (LayoutParams) bubbleImage3.getLayoutParams();
		params3.width = 200;
		bubbleImage3.setLayoutParams(params3);
		bubblemoveAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bubblemove3);
		bubbleImage3.setAnimation(bubblemoveAnimation);
		/*
		 * LayoutParams params4 = (LayoutParams) bubbleImage4.getLayoutParams();
		 * params4.width = 500; bubbleImage4.setLayoutParams(params4);
		 */
		bubblemoveAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bubblemove4);
		bubbleImage4.setAnimation(bubblemoveAnimation);

		/********************** to click button ********************************/

		RelativeLayout.LayoutParams img_param = (LayoutParams) bubbleImage4
				.getLayoutParams();

		final Button btn = new Button(this);
		// when button clicked
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIntent = new Intent(BubbleActivity.this, MapListActivity.class); //TODO: test phrase
				startActivity(mIntent);
				overridePendingTransition(0, 0); //no switching animation
			}
		});
		;

		rootLayout.addView(btn);
		btn.setLayoutParams(img_param);
		btn.setBackgroundColor(0); /* transparent btn */
		btn.setY(1587); /* btn start location */

		// to set btn location
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				btn.setY(BUBBLETEST_BUTTON_START_Y - BUBBLETEST_BUTTON_INTERVAL
						* msg.arg1);
			}
		};

		new Thread(new Runnable() {
			public void run() {
				int i = 0;
				while (i != 100) {
					try {
						Message msg = handler.obtainMessage();
						Thread.sleep(1000);
						//LogUtil.v("i = " + i + "getY = " + btn.getY());
						msg.arg1 = i % 18 + 1; /*
												 * 
												 */
						i += 1;
						handler.sendMessage(msg);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}
		}).start();

		/*************** init other *****************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);

		LogUtil.v("onCreate: start updateService");
		startService(new Intent(this, UpdateService.class)); 		//updateservice service start

		//test phrase
		LogUtil.v("test phrase. select * from tLandmark");
		soapParser = SoapParser.getInstance();
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK)); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bubble, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.action_map_list:
		{
			LogUtil.v("action_map_list clicked");
			mIntent = new Intent(this, MapListActivity.class);
			startActivity(mIntent);
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		case R.id.action_test_stop_service:
		{
			LogUtil.i("action_test_stop_service clicked");
			stopService(new Intent(this, UpdateService.class));	//TODO: test. stop service
			break;
		}

		}
		return true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.v("onDestroy: stop updateService");
		stopService(new Intent(this, UpdateService.class));	//TODO: test. stop service
	}
}