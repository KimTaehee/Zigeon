/**
 * Ŭ���� �̸� : BubbleActivity
 * Ŭ���� ���� : ������ ȭ�鿡 ���� �ִϸ��̼� ȿ���� �ݴϴ�.
 * �ۼ��� (Ȥ�� ��) : ����ȫ 
 * ���� ���� :
 * �ۼ� ���� : 8�� 15�� ���� 6:35
 * ���� �̷� : 8�� 16 ���� 3:01
 */

package kr.re.ec.zigeon;

/* �ۼ���: ������. �ӽ� MainActivity
 * 
 */
import com.nhn.android.maps.maplib.NGeoPoint;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.handler.UpdateService;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
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
	
//	private TextView tvGPSTest;
//	private TextView tvLandmarkTest;
//	private TextView tvPostingTest;
//	private Button btnServiceStopTest;
	
	private SoapParser soapParser; //TODO: test. Ȯ���� ������ �������.
	
	private UIHandler uiHandler; 
	private Handler messageHandler = new Handler() { //UpdateService�κ����� ���ź�! �߿���
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
				//�ϴ��� android.location ��� NGeoPoint�� ����� �Ѵ�.
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
		
		/*************** UI �ʱ�ȭ ************/
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
		
		/************* �ִϸ��̼Ǻ� ****************/
		// �̹��� ũ�⸦ �����ϱ� ���� �ڵ�
		LayoutParams params = (LayoutParams) bubbleImage.getLayoutParams();
		params.width = 120;
		bubbleImage.setLayoutParams(params);

		// �ִϸ��̼� ȿ��
		bubblemoveAnimation = AnimationUtils.loadAnimation(this,
				R.anim.bubblemove);
		bubbleImage.setAnimation(bubblemoveAnimation);

		/**
		 * ����� �̹��� �ϳ��� �ϳ��� �ִϸ��̼ǰ� imageview�� ���� ����߽��ϴ�. 1~7������ ���� �̹����� �Ἥ 1����
		 * imageview�� ǥ���Ҽ� ������ ������ �װ� ���� �����غ��� �Ұ� �����ϴ�.
		 */

		LayoutParams params2 = (LayoutParams) bubbleImage2.getLayoutParams();
		params2.width = 100;
		/**
		 * width�ƹ��� ũ���ص� ���� imageview�� ������ ũ�� �̻����δ� Ŀ���� �ʳ׿� ó�� ���鶧 ����ũ��� ����� �θ�
		 * ������ ������ �����ϴ�.
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

		/********************** ��ư Ŭ���� ���� ���� �����ڱ� ********************************/

		RelativeLayout.LayoutParams img_param = (LayoutParams) bubbleImage4
				.getLayoutParams();
		
		final Button btn = new Button(this);
		// ����(�߸Ź�ư)�� Ŭ��������
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
		btn.setBackgroundColor(0); /* ��ư�����ϰ� */
		btn.setY(1587); /* ��ư������ġ */

		// ��ư ��ġ ������ �ڵ鷯
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
						LogUtil.v("i = " + i + "getY = " + btn.getY());
						msg.arg1 = i % 18 + 1; /*
												 * 18�� �� ������ bubble�ִϸ��̼� �ֱ⸦
												 * 18�ʷ� �ؼ� �������� �ҷ��� �߽��ϴ�. ���� ����
												 * �����Ƽ� i�� 100�Ǹ� ������ ���߰�.......
												 */
						i += 1;
						handler.sendMessage(msg);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}
		}).start();

		/*************** �Ϲ� �ʱ�ȭ *****************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);

		LogUtil.v("onCreate: start updateService");
		startService(new Intent(this, UpdateService.class)); 		//updateservice service start

		
		//����� test����
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
			stopService(new Intent(this, UpdateService.class));	//TODO: �׽�Ʈ�غ��� ��. ���� ����
			break;
		}
		}
		return true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.v("onDestroy: stop updateService");
		stopService(new Intent(this, UpdateService.class));	//TODO: �׽�Ʈ�غ��� ��. ���� ����
		
	}
}