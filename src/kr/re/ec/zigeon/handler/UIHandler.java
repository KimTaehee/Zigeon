package kr.re.ec.zigeon.handler;

/* 작성자: 김태희. UI를 수정하는 Message를 각 Activity로 발송하는 Handler
 * 
 */
import kr.re.ec.zigeon.util.LogUtil;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class UIHandler {
	/* Target Activity Handler definition */ 
	public static Handler bubbleActivityHandler;
	public static Handler mapListActivityHandler;
	public static Handler landmarkActivityHandler;
	public static Handler postingActivityHandler;
	public static Handler mapActivityHandler;
	
	private Context mContext; //for use topActivity
	private ActivityManager am;
	
	private static UIHandler instance; //for singleton
	private Message msg;
	
	/* 구조- 각각의 Activity.onCreate에서 messageHandler를 UIHandler에 등록한다. 
	 * 이후 UIHandler는 자체 TopActivity 판별을 통해 어느 messageHandler로 메시지를 전송할지 결정한다.
	 * : 김태희
	 */
	private UIHandler(Context context) {
		mContext = context;
		LogUtil.v("uihandler instance created");
	}	
	
	public static UIHandler getInstance(Context context){
		if(instance==null) {
			instance = new UIHandler(context); 
		}
		return instance;
	}
	
	/***** activity가 변경될 때 handler를 재설정해준다. *****/ //TODO: 백그라운드 작업일 때의 처리 과정 확인하기
	public void setHandler(Handler handler) { //activity 옮겨질 때 oncreate에서 handler 변경을 해 주어야  해당 UI값이 바뀐다.
		String str = getTopActivityName(mContext);
		
		if (str.compareTo("kr.re.ec.zigeon.BubbleActivity") == 0)
		{
			bubbleActivityHandler = handler;
			LogUtil.v("top activity is BubbleActivity. set handler.");
		} 
		else if (str.compareTo("kr.re.ec.zigeon.MapListActivity") == 0) 
		{
			mapListActivityHandler = handler;
			LogUtil.v("top activity is MapListActivity. set handler.");
		}
		else if (str.compareTo("kr.re.ec.zigeon.LandmarkActivity") == 0)
		{
			landmarkActivityHandler = handler;
			LogUtil.v("top activity is LandmarkActivity. set handler.");
		}
		else if (str.compareTo("kr.re.ec.zigeon.PostingActivity") == 0)
		{
			postingActivityHandler = handler;
			LogUtil.v("top activity is PostingActivity. set handler.");
		}
		else if (str.compareTo("kr.re.ec.zigeon.MapActivity") == 0)
		{
			mapActivityHandler = handler;
			LogUtil.v("top activity is MapActivity. set handler.");
		}
		else 
		{
			LogUtil.e("cannot set handler. Other TopActivity detected.");
		}
	
	}
	
	/*** 현재 UIHandler에 세팅된 handler를 판별한다. ***/
	private Handler getCurrentHandler()
	{
		String str = getTopActivityName(mContext);
		
		if (str.compareTo("kr.re.ec.zigeon.BubbleActivity") == 0)
		{
			if(bubbleActivityHandler!=null) {
				LogUtil.v("bubbleActivityHandler selected.");
				return bubbleActivityHandler;
			} else {
				LogUtil.e("Cannot return handler. no bubbleActivityHandler detected.");
				return null;
			}
		} 
		else if (str.compareTo("kr.re.ec.zigeon.MapListActivity") == 0) 
		{
			if(mapListActivityHandler!=null) {
				LogUtil.v("mapListActivityHandler selected.");
				return mapListActivityHandler;
			} else {
				LogUtil.e("Cannot return handler. no mapListActivityHandler detected.");
				return null;
			}
		}
		else if (str.compareTo("kr.re.ec.zigeon.LandmarkActivity") == 0)
		{
			if(landmarkActivityHandler!=null) {
				LogUtil.v("landmarkActivityHandler selected.");
				return landmarkActivityHandler;
			} else {
				LogUtil.e("Cannot return handler. no landmarkActivityhandler detected.");
				return null;
			}
		}
		else if (str.compareTo("kr.re.ec.zigeon.PostingActivity") == 0)
		{
			if(postingActivityHandler!=null) {
				LogUtil.v("postingActivityHandler selected.");
				return postingActivityHandler;
			} else {
				LogUtil.e("Cannot return handler. no postingActivityHandler detected.");
				return null;
			}
		}
		else if (str.compareTo("kr.re.ec.zigeon.MapActivity") == 0) 
		{
			if(mapActivityHandler!=null) {
				LogUtil.v("mapActivityHandler selected.");
				return mapActivityHandler;
			} else {
				LogUtil.e("Cannot return handler. no mapActivityHandler detected.");
				return null;
			}
		}
		else 
		{
			LogUtil.e("Cannot return handler. Other TopActivity detected.");
			return null;
		}
	}
	
	/************ 현재 topactivity에 알맞은 handler로 msg 발송 **********/
	public void sendMessage(int _what, String _value, Object _obj){ //what은 Constants.java에 정의되어 있다.
		LogUtil.v("sendmsg called.");
		Bundle bundle = new Bundle();
		bundle.putString("msg", _value);
		msg = new Message(); //이미 사용했던 message 객체는 재사용할 수 없다.
		msg.what = _what; 
		msg.obj = _obj;
		msg.setData(bundle);
		
		Handler handler = getCurrentHandler();
		if(handler!=null) {
			handler.sendMessage(msg); //handler가 null이라면 오류.
		} else {
			LogUtil.e("no handler set! cannot run sendMessage()");
		}
	}
	
	/* 현재 Activity 알기 위함 */
	public String getTopActivityName(Context context) {
		am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
		String topActivityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
				
		return topActivityName;
		
	}
}
