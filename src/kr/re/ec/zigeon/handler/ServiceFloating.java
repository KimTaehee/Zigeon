package kr.re.ec.zigeon.handler;

import kr.re.ec.zigeon.BalloonHeadButtonActivity;
import kr.re.ec.zigeon.R;
import kr.re.ec.zigeon.util.LogUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("ResourceAsColor")
public class ServiceFloating extends Service implements Runnable {

	public static  int ID_NOTIFICATION = 2018;

	private WindowManager windowManager;
	private ImageView balloonHead;
	private ImageView quit;
	private Button wordBubble;

	// Start ID
	private int mStartId;
	// Service thread Handler. use to repeat by timer
	private Handler mHandler;
	// Service working flag
	private boolean mRunning;
	// timer set (5sec)
	private static final int TIMER_PERIOD = 5 * 1000;
	private static final int COUNT = 1;
	private int mCounter;


	Activity imsi = new Activity();
	long lastPressTime;
	long textFloatTime;



	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override 
	public void onCreate() {
		super.onCreate();
		
		mHandler = new Handler();
		mRunning = false;


		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		balloonHead = new ImageView(this);
		quit = new ImageView(this);
		wordBubble = new Button(this);

		balloonHead.setImageResource(R.drawable.balloonhead);
		//		wordBubble.setImageResource(R.drawable.wordbubble);
		wordBubble.setBackgroundResource(R.drawable.wordbubble);
		wordBubble.setText(R.string.wordbubble_text);
		wordBubble.setTextColor(R.color.wordbubble_text);
		quit.setImageResource(R.drawable.quit);


		final WindowManager.LayoutParams wordBubbleParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);

		wordBubbleParams.gravity = Gravity.TOP | Gravity.LEFT;
//		wordBubbleParams.x = (int) balloonHead.getWidth();
//		wordBubbleParams.y = (int) balloonHead.getHeight() + (balloonHead.getHeight()/4); 
		wordBubbleParams.x = 150;
		wordBubbleParams.y = 0;

		windowManager.addView(wordBubble, wordBubbleParams);


		final WindowManager.LayoutParams quitParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		quitParams.gravity = Gravity.BOTTOM | Gravity.CENTER;

		windowManager.addView(quit, quitParams);



		final WindowManager.LayoutParams balloonHeadParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		balloonHeadParams.gravity = Gravity.TOP | Gravity.LEFT;
		balloonHeadParams.x = 0;
		balloonHeadParams.y = 0;


		windowManager.addView(balloonHead, balloonHeadParams);



		//		LogUtil.v("balloonHead: " + balloonHeadParams.x + ", " + balloonHeadParams.y);
		//		LogUtil.v("wordBubble: " + wordBubbleParams.x + ", " + wordBubbleParams.y);




		try {
			LogUtil.i("try invoked!!");

			quit.setVisibility(View.INVISIBLE);


			balloonHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = balloonHeadParams;
				private WindowManager.LayoutParams paramsG = wordBubbleParams;
				//				private WindowManager.LayoutParams paramsH = quitParams;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;
				private float x;
				private float y;
				private int quitWidth;
				private int quitHeight;
				private int balloonHeadWidth;
				private int balloonHeadHeight;



				int moveCheck = 0;

				@Override public boolean onTouch(View v, MotionEvent event) {

					quitWidth = quit.getWidth();
					quitHeight = quit.getHeight();

					balloonHeadWidth = balloonHead.getWidth();
					balloonHeadHeight = balloonHead.getHeight();


					if(moveCheck == 3){
						LogUtil.v("quit visible!");
						quit.setVisibility(View.VISIBLE);
					}

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						LogUtil.v("onTouch_ACTION_DOWN invoked!! movecheck: " + moveCheck);
						initialX = paramsF.x;
						initialY = paramsF.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						break;

					case MotionEvent.ACTION_UP:
						LogUtil.v("onTouch_ACTION_UP invoked!!");

						x=event.getRawX();
						y=event.getRawY();
						LogUtil.i("x= "+x+"   y = "+y);

						int quitCoord[] = new int[2];
						quit.getLocationOnScreen(quitCoord);
						LogUtil.i("quit \n x : " + quitCoord[0] + "   y = "+ quitCoord[1]);
						//						LogUtil.i("\nqC-qW = " +(quitCoord[0]-quitWidth) +"\nqC+2qW = "+(quitCoord[0]+(2*quitWidth)) +
						//								"\nqC+qH = "+(quitCoord[1]-(quitWidth/2)));


						if(moveCheck<3){
							LogUtil.v("onTouch_ACTION_UP -> click invoked!!");
							Intent intent = new Intent(getApplicationContext(), BalloonHeadButtonActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							getApplicationContext().startActivity(intent);
						}
						quit.setVisibility(View.INVISIBLE);
						if(x > (quitCoord[0]-(quitWidth/6)) && x < (quitCoord[0]+quitWidth+(quitWidth/6))
								&& y > (quitCoord[1]-(quitHeight/6))){
							stopSelf();
						}
						moveCheck = 0;
						//						LogUtil.v("ACTION_UP: movecheck: " + moveCheck);
						break;

					case MotionEvent.ACTION_MOVE:
						//LogUtil.v("onTouch_ACTION_MOVE invoked!!");
						paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
						paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
						paramsG.x = paramsF.x + balloonHeadWidth;
						paramsG.y = paramsF.y + (balloonHeadHeight/4);

						//LogUtil.v("ParamsF: " + paramsF.x + ", "+ paramsF.y);
						windowManager.updateViewLayout(balloonHead, paramsF);
						windowManager.updateViewLayout(wordBubble, paramsG);

						moveCheck++;

						break;
					}
					return false;
				}


			}


					);

		} catch (Exception e) {
			// TODO: handle exception
		}


	}
	// call when service start . started in background processing.
	// startId : Service start request id. stopSelf used to exit from.
	// onStart is called multiple times,therefore used as an identifier

	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.v("Service startId = " + startId);
		super.onStartCommand(intent, flags, startId);
		mStartId = startId;
		mCounter = COUNT;

		if (!mRunning) {
			// postAtTime : Method calls a specific time
			mHandler.postDelayed(this, TIMER_PERIOD);
			mRunning = true;
		}

		return START_NOT_STICKY;
	}

	// Service processing
	public void run() {
		if (!mRunning) {
			// a service stop request
			LogUtil.v("run after destory");
			return;
		} else if (--mCounter <= 0) {
			// connter=0 stopSelf
			LogUtil.v("stop Service id = " + mStartId);
			wordBubble.setVisibility(View.INVISIBLE);
		} else {
			// Require the operation again
			LogUtil.v("mCounter : " + mCounter);
			mHandler.postDelayed(this, TIMER_PERIOD);
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (balloonHead != null) windowManager.removeView(balloonHead);
		if (wordBubble != null) windowManager.removeView(wordBubble);
		if (quit != null) windowManager.removeView(quit);
		
		mRunning = false;
	}

}