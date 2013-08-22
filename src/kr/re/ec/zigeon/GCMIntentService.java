/**
 * GCMIntentService
 * Writer: KimTaehee slhyvaa@nate.com
 * First write Date: 130822
 */

package kr.re.ec.zigeon;

import java.util.Iterator;

import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
    private static final String tag = "GCMIntentService";
    
    //구글 api 페이지 주소 [https://code.google.com/apis/console/#project:긴 번호]
   //#project: 이후의 숫자가 위의 PROJECT_ID 값에 해당한다
   
    //public 기본 생성자를 무조건 만들어야 한다.
    public GCMIntentService(){ 
    	this(Constants.GCM_PROJECT_ID); 
    }
   
    public GCMIntentService(String project_id) { 
    	super(project_id); 
    	LogUtil.i("GCMIntentService start!");
    }

    /** 푸시로 받은 메시지 */
    @Override
    protected void onMessage(Context context, Intent intent) {
    	LogUtil.i("GCM onMessage invoked!!!!!!");
    	Bundle b = intent.getExtras();
    	Iterator<String> iterator = b.keySet().iterator();
    	while(iterator.hasNext()) {
    		String key = iterator.next();
    		String value = b.get(key).toString();
    		LogUtil.i("onMessage. "+key+" : "+value);
    	}
    }

	@Override
	protected void onError(Context context, String errorId) {
		// Auto-generated method stub
		LogUtil.d("onError invoked!: errId" + errorId);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		// Auto-generated method stub
		LogUtil.d("onRegistered invoked!: regId" + registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		// Auto-generated method stub
		LogUtil.d("onUnregistered invoked!: regId" + registrationId);
	}
}
