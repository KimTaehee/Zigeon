/**
 * Class Name: BalloonApplication
 * Description: init Application
 * Author: KimTaehee
 * Created Date: 130831
 */

package kr.re.ec.zigeon;

import com.google.android.gms.internal.c;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import kr.re.ec.zigeon.handler.UpdateService;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class BalloonApplication extends Application {
	Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		
		LogUtil.i("\n======================== Start of App =========================");
		LogUtil.v("WOW! onCreated invoked. let's init AUIL! and updateService!");
		
		//init AUIL
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.writeDebugLogs() // Remove for release app
			.build();
		ImageLoader.getInstance().init(config);
		
		//service start: to reduce db connection delay and quick location return
		LogUtil.v("start updateService");
		startService(new Intent(this, UpdateService.class)); 		//updateservice service start
	}	
}
