/**
 * Class Name: BalloonApplication
 * Description: init Application
 * Author: KimTaehee
 * Created Date: 130831
 */

package kr.re.ec.zigeon;

import kr.re.ec.zigeon.util.LogUtil;
import android.app.Application;
import android.content.Context;

public class BalloonApplication extends Application {
	Context mContext;
	
	public BalloonApplication() {
		super();
		LogUtil.v("constructor called!");
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.v("onCreated invoked!");
		
	}	
}
