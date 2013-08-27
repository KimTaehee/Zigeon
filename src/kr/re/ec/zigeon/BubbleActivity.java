/**
 * 클래스 이름 : BubbleActivity
 * 클래스 설명 : 버블을 화면에 띄우고 애니메이션 효과를 줍니다.
 * 작성자 (혹은 팀) : 김지홍 
 * 버전 정보 :
 * 작성 일자 : 8월 15일 오후 6:35
 * 수정 이력 : 8월 16 오전 3:01
 */

package kr.re.ec.zigeon;


import com.google.android.gcm.GCMRegistrar;
import com.nhn.android.maps.maplib.NGeoPoint;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.handler.UpdateService;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
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
		
	private SoapParser soapParser; //TODO: test. 확인한 다음에 빼줘야함.
	
	private UIHandler uiHandler; 
	private Handler messageHandler = new Handler() { //UpdateService로부터의 수신부! 중요함
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
				//일단은 android.location 대신 NGeoPoint를 쓰기로 한다.
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
		
		/*************** GCM registration **************/
		LogUtil.v("push register start");
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			LogUtil.v("regId " + regId);
			GCMRegistrar.register(this, Constants.GCM_PROJECT_ID);
            // Automatically registers application on startup.
			
        // regId 는 있지만 서버에 등록이 안돼어 있다면 아래 로직으로 서버 재 등록 시작 
		} else {
			LogUtil.i("already registered device! ready to receive msg!");
			LogUtil.v("regId " + regId);
        }

		
		
		/*************** UI 초기화 ************/
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
		
		/************* 애니메이션부 ****************/
		// 이미지 크기를 조정하기 위한 코드
		LayoutParams params = (LayoutParams) bubbleImage.getLayoutParams();
		params.width = 120;
		bubbleImage.setLayoutParams(params);

		// 애니메이션 효과
		bubblemoveAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bubblemove);
		bubbleImage.setAnimation(bubblemoveAnimation);

		/**
		 * 현재는 이미지 하나당 하나의 애니메이션과 imageview를 만들어서 사용했습니다. 1~7개까지 같은 이미지를 써서 1개의
		 * imageview로 표현할수 있을것 같은데 그건 좀더 생각해봐야 할것 같습니다.
		 */

		LayoutParams params2 = (LayoutParams) bubbleImage2.getLayoutParams();
		params2.width = 100;
		/**
		 * width아무리 크게해도 원래 imageview에 설정된 크기 이상으로는 커지지 않네요 처음 만들때 일정크기로 만들어 두면
		 * 문제는 없을것 같습니다.
		 */

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

		/********************** 버튼 클릭을 위한 숫자 때려박기 ********************************/

		RelativeLayout.LayoutParams img_param = (LayoutParams) bubbleImage4
				.getLayoutParams();
		
		final Button btn = new Button(this);
		// 버블(야매버튼)을 클릭했을때
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
		btn.setBackgroundColor(0); /* 버튼투명하게 */
		btn.setY(1587); /* 버튼시작위치 */

		// 버튼 위치 설정용 핸들러
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
												 * 18로 한 이유는 bubble애니메이션 주기를
												 * 18초로 해서 같아지게 할려고 했습니다. 따로 끄기
												 * 귀찮아서 i가 100되면 쓰레드 멈추게.......
												 */
						i += 1;
						handler.sendMessage(msg);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}
		}).start();

		/*************** 일반 초기화 *****************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);

		LogUtil.v("onCreate: start updateService");
		startService(new Intent(this, UpdateService.class)); 		//updateservice service start

		
		//여기는 test구문
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
			stopService(new Intent(this, UpdateService.class));	//TODO: 테스트해봐야 함. 서비스 종료
			break;
		}
		}
		return true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.v("onDestroy: stop updateService");
		stopService(new Intent(this, UpdateService.class));	//TODO: 테스트해봐야 함. 서비스 종료
		
	}
}