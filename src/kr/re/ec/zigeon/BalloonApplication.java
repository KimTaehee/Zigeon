/**
 * Class Name: BalloonApplication
 * Description: init Application
 * Author: KimTaehee
 * Created Date: 130831
 */

package kr.re.ec.zigeon;

import com.google.android.gcm.GCMRegistrar;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import kr.re.ec.zigeon.handler.UpdateService;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

public class BalloonApplication extends Application {
	Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		
		LogUtil.i("\n======================== Start of App =========================");
		LogUtil.v("WOW! onCreated invoked. let's init AUIL! and updateService!");
		
		//init AUIL
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.ic_auil_stub)
			.showImageOnFail(R.drawable.ic_auil_error)
			.showImageForEmptyUri(R.drawable.ic_auil_empty)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT) //or ImageScaleType.EXACTLY
			.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.writeDebugLogs() // Remove for release app
			.threadPoolSize(2)
			.memoryCache(new WeakMemoryCache())
			.defaultDisplayImageOptions(defaultOptions)
			.build();
		LogUtil.v("image loader init!!!");
		ImageLoader.getInstance().init(config);
		
		//service start: to reduce db connection delay and quick location return
		LogUtil.v("start updateService");
		startService(new Intent(this, UpdateService.class)); 		//updateservice service start
		
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

	}	
}
