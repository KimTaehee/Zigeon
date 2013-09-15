<<<<<<< HEAD
<<<<<<< HEAD
package kr.re.ec.zigeon.service;

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
public class ServiceFloating extends Service implements Runnable{

	public static  int ID_NOTIFICATION = 2018;

	private WindowManager windowManager;
	private ImageView chatHead;
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
    private static final int COUNT = 2;
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
		chatHead = new ImageView(this);
		quit = new ImageView(this);
		wordBubble = new Button(this);
		chatHead.setImageResource(R.drawable.balloonhead);
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
		wordBubbleParams.x = 160;
		wordBubbleParams.y = 135; 
		windowManager.addView(wordBubble, wordBubbleParams);
			
		final WindowManager.LayoutParams quitParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		quitParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
		quitParams.x = 0;
		quitParams.y = 0;

		windowManager.addView(quit, quitParams);
		
		
		
		final WindowManager.LayoutParams chatHeadParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		chatHeadParams.gravity = Gravity.TOP | Gravity.LEFT;
		chatHeadParams.x = 0;
		chatHeadParams.y = 100;

		windowManager.addView(chatHead, chatHeadParams);

		
		
		
		
		//		LogUtil.v("chatHead: " + chatHeadParams.x + ", " + chatHeadParams.y);
		//		LogUtil.v("wordbubble: " + wordBubbleParams.x + ", " + wordBubbleParams.y);



		try {
			LogUtil.i("try invoked!!");
			
			quit.setVisibility(View.INVISIBLE);

			chatHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = chatHeadParams;
				private WindowManager.LayoutParams paramsG = wordBubbleParams;
				private WindowManager.LayoutParams paramsH = quitParams;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;



				int moveCheck = 0;

				@Override public boolean onTouch(View v, MotionEvent event) {
					
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

						if(moveCheck<3){
							LogUtil.v("onTouch_ACTION_UP -> click invoked!!");
							Intent intent = new Intent(getApplicationContext(), BalloonHeadButtonActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							getApplicationContext().startActivity(intent);
						}
						quit.setVisibility(View.INVISIBLE);
						if(paramsF.x >270 && paramsF.y > 90 
								&& paramsF.x < 290 && paramsF.y < 120){
							chatHead.setVisibility(View.INVISIBLE);
							wordBubble.setVisibility(View.INVISIBLE);
						}
						moveCheck = 0;
						//						LogUtil.v("ACTION_UP: movecheck: " + moveCheck);
						break;

					case MotionEvent.ACTION_MOVE:
						//LogUtil.v("onTouch_ACTION_MOVE invoked!!");
						paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
						paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
						paramsG.x = paramsF.x + 160;
						paramsG.y = paramsF.y + 35;
						
						LogUtil.v("ParamsF: " + paramsF.x + ", "+ paramsF.y);
						windowManager.updateViewLayout(chatHead, paramsF);
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
     //onStart is called multiple times,therefore used as an identifier

	public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.v("Service startId = " + startId);
        super.onStartCommand(intent, flags, startId);
        mStartId = startId;
        mCounter = COUNT;
        
        
        if (!mRunning) {
              // postAtTime : Method calls a specific time
             mHandler.postAtTime(this, TIMER_PERIOD);
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
            LogUtil.v("stop Service id = "+mStartId);
            stopSelf(mStartId);
        } else {
            // Require the operation again
            LogUtil.v("mCounter : " + mCounter);
            mHandler.postAtTime(this, TIMER_PERIOD);
        }
    }
    


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
		if (wordBubble != null) windowManager.removeView(wordBubble);
		if (quit != null) windowManager.removeView(quit);
		
        mRunning = false;
        
	}

=======
=======
>>>>>>> no_hangle?
package kr.re.ec.zigeon.service;

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
public class ServiceFloating extends Service implements Runnable{

	public static  int ID_NOTIFICATION = 2018;

	private WindowManager windowManager;
	private ImageView chatHead;
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
    private static final int COUNT = 2;
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
		chatHead = new ImageView(this);
		quit = new ImageView(this);
		wordBubble = new Button(this);
		chatHead.setImageResource(R.drawable.balloonhead);
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
		wordBubbleParams.x = 160;
		wordBubbleParams.y = 135; 
		windowManager.addView(wordBubble, wordBubbleParams);
			
		final WindowManager.LayoutParams quitParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		quitParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
		quitParams.x = 0;
		quitParams.y = 0;

		windowManager.addView(quit, quitParams);
		
		
		
		final WindowManager.LayoutParams chatHeadParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		chatHeadParams.gravity = Gravity.TOP | Gravity.LEFT;
		chatHeadParams.x = 0;
		chatHeadParams.y = 100;

		windowManager.addView(chatHead, chatHeadParams);

		
		
		
		
		//		LogUtil.v("chatHead: " + chatHeadParams.x + ", " + chatHeadParams.y);
		//		LogUtil.v("wordbubble: " + wordBubbleParams.x + ", " + wordBubbleParams.y);



		try {
			LogUtil.i("try invoked!!");
			
			quit.setVisibility(View.INVISIBLE);

			chatHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = chatHeadParams;
				private WindowManager.LayoutParams paramsG = wordBubbleParams;
				private WindowManager.LayoutParams paramsH = quitParams;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;



				int moveCheck = 0;

				@Override public boolean onTouch(View v, MotionEvent event) {
					
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

						if(moveCheck<3){
							LogUtil.v("onTouch_ACTION_UP -> click invoked!!");
							Intent intent = new Intent(getApplicationContext(), BalloonHeadButtonActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							getApplicationContext().startActivity(intent);
						}
						quit.setVisibility(View.INVISIBLE);
						if(paramsF.x >270 && paramsF.y > 90 
								&& paramsF.x < 290 && paramsF.y < 120){
							chatHead.setVisibility(View.INVISIBLE);
							wordBubble.setVisibility(View.INVISIBLE);
						}
						moveCheck = 0;
						//						LogUtil.v("ACTION_UP: movecheck: " + moveCheck);
						break;

					case MotionEvent.ACTION_MOVE:
						//LogUtil.v("onTouch_ACTION_MOVE invoked!!");
						paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
						paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
						paramsG.x = paramsF.x + 160;
						paramsG.y = paramsF.y + 35;
						
						LogUtil.v("ParamsF: " + paramsF.x + ", "+ paramsF.y);
						windowManager.updateViewLayout(chatHead, paramsF);
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
     //onStart is called multiple times,therefore used as an identifier

	public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.v("Service startId = " + startId);
        super.onStartCommand(intent, flags, startId);
        mStartId = startId;
        mCounter = COUNT;
        
        
        
        // 동작중이 아니면 run 메소드를 일정 시간 후에 시작
        if (!mRunning) {
              // postAtTime : Method calls a specific time
             mHandler.postAtTime(this, TIMER_PERIOD);
             mRunning = true;
        }
        
        return START_NOT_STICKY;
    }

	// 서비스 처리
    public void run() {
        if (!mRunning) {
            // 서비스 종료 요청이 들어온 경우 그냥 종료
            LogUtil.v("run after destory");
            return;
        } else if (--mCounter <= 0) {
            // 지정한 횟수 실행하면 스스로 종료
            LogUtil.v("stop Service id = "+mStartId);
            stopSelf(mStartId);
        } else {
            // 다음 작업을 다시 요구
            LogUtil.v("mCounter : " + mCounter);
            mHandler.postDelayed(this, TIMER_PERIOD);
        }
    }
    


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
		if (wordBubble != null) windowManager.removeView(wordBubble);
		if (quit != null) windowManager.removeView(quit);
		
		// postDelayed는 바로 정지되지 않고 다음 번 run 메소드를 호출.
        mRunning = false;
        
	}

<<<<<<< HEAD
>>>>>>> ServiceFloating auto service stop
=======
>>>>>>> no_hangle?
}