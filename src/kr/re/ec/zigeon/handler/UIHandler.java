package kr.re.ec.zigeon.handler;

/* �ۼ���: ������. UI�� �����ϴ� Message�� �� Activity�� �߼��ϴ� Handler
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
	
	private Context mContext; //for use topActivity
	private ActivityManager am;
	
	private static UIHandler instance; //for singleton
	private Message msg;
	
	/* ����- ������ Activity.onCreate���� messageHandler�� UIHandler�� ����Ѵ�. 
	 * ���� UIHandler�� ��ü TopActivity �Ǻ��� ���� ��� messageHandler�� �޽����� �������� �����Ѵ�.
	 * : ������
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
	
	/***** activity�� ����� �� handler�� �缳�����ش�. *****/ //TODO: ��׶��� �۾��� ���� ó�� ���� Ȯ���ϱ�
	public void setHandler(Handler handler) { //activity �Ű��� �� oncreate���� handler ������ �� �־��  �ش� UI���� �ٲ��.
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
		else 
		{
			LogUtil.e("cannot set handler. Other TopActivity detected.");
		}
	
	}
	
	/*** ���� UIHandler�� ���õ� handler�� �Ǻ��Ѵ�. ***/
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
		else 
		{
			LogUtil.e("Cannot return handler. Other TopActivity detected.");
			return null;
		}
	}
	
	/************ ���� topactivity�� �˸��� handler�� msg �߼� **********/
	public void sendMessage(int _what, String _value, Object _obj){ //what�� Constants.java�� ���ǵǾ� �ִ�.
		LogUtil.v("sendmsg called.");
		Bundle bundle = new Bundle();
		bundle.putString("msg", _value);
		msg = new Message(); //�̹� ����ߴ� message ��ü�� ������ �� ����.
		msg.what = _what; 
		msg.obj = _obj;
		msg.setData(bundle);
		
		Handler handler = getCurrentHandler();
		if(handler!=null) {
			handler.sendMessage(msg); //handler�� null�̶�� ����.
		} else {
			LogUtil.e("no handler set! cannot run sendMessage()");
		}
	}
	
	/* ���� Activity �˱� ���� */
	public String getTopActivityName(Context context) {
		am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
		String topActivityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
				
		return topActivityName;
		
	}
}
