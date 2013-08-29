/* 
 * Author KimTaehee
 * description: send msg to each activities
 * each Activity.onCreate() register messageHandler to UIHandler
 * after this, UIHandler recognize TopActivity() and decide which 
 * activity's messageHandler to send msg.
 */

package kr.re.ec.zigeon.handler;

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
	
	/***** when TopActivity change, reset handler. *****/ //TODO: how it works on background?
	public void setHandler(Handler handler) { //MUST set handler in each Activity.onCreate()
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
			LogUtil.i("cannot set handler. Other TopActivity detected.");
		}
	
	}
	
	/*** recognize current handler on UIHandler ***/
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
	
	/************ send msg to current TopActivity **********/
	public void sendMessage(int _what, String _value, Object _obj){ //what defined on Constants.java
		LogUtil.v("sendmsg called.");
		Bundle bundle = new Bundle();
		bundle.putString("msg", _value);
		msg = new Message(); //SHOULD use new Message(cannot use again)

		msg.what = _what; 
		msg.obj = _obj;
		msg.setData(bundle);
		
		Handler handler = getCurrentHandler();
		if(handler!=null) {
			handler.sendMessage(msg); //if handler==null, error occur
		} else {
			LogUtil.e("no handler set! cannot run sendMessage()");
		}
	}
	
	/* which activity on Top? */
	public String getTopActivityName(Context context) {
		am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
		String topActivityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
				
		return topActivityName;
		
	}
}
