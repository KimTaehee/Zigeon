package kr.re.ec.zigeon.handler;
/* 작성자: 김태희. Sleep이 포함된 Thread Service로, 각종 주기적인 작업을 관장
 * 내용: GPS, UI, SoapParser를 포함한다.
 */
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

import kr.re.ec.zigeon.MapListActivity;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;

public class UpdateService extends Service implements Runnable{
	private boolean threadLoop=false;
	private int count = 0; 		//thread loop count
	private final static int THREAD_INTERVAL_MS = 5000; 	//thread loop delay
	//private static final int MIN_DISTANCE = 1; 		//min distance(m) to recognize gps updates
	
	private Thread mThread;
	private ActivityManager am;
	
//	private static LocationManager locationManager;
//	private Criteria criteria;
//	private boolean gpsEnabled;
//	private Location location; 
//	

	/*************nMap test****************/
	private NMapLocationManager mMapLocationManager; //130816 김태희 추가
	private NGeoPoint mLocation;
	
	private UIHandler uiHandler; 	//UIHandler
	private SoapParser soapParser;	//SoapParser
	
	@Override
	public void onCreate(){
		LogUtil.v("oncreate called");
		super.onCreate();
	
		
		/**********NMapLocationManager Init************/
		mMapLocationManager = new NMapLocationManager(this);
		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
		if (mMapLocationManager.isMyLocationEnabled()) {
			LogUtil.v("isMyLocationEnabled(): true");
		} else {
			boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
			if (!isMyLocationEnabled) {
				LogUtil.v("Please enable a My Location source in system settings");
				//Toast.makeText(NMapViewer.this, "Please enable a My Location source in system settings",
				//		Toast.LENGTH_LONG).show();
				Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(goToSettings);
			}
		}
		
		MapListActivity.mMapLocationManager =  this.mMapLocationManager; //TODO: LM강제전달. 더 좋은 방법이 없을까?
		
		//nMapLM 쓰기전
//		/********* LocationManager Init ******************/ 
//		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
//		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//		criteria=new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);  		//GPS update frequency 
//        String provider = locationManager.getBestProvider(criteria, true);
//        LogUtil.v("location provider: " + provider);
//        
//        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);	//gps 사용 
//		LogUtil.v("GPSEnabled: "+ gpsEnabled);
//		if(gpsEnabled) {
//			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE, 0, listener);
//		} //requestLocationUpdates를 Thread 안에서 요청할 수 없다.
		
		
		/********************** Thread 생성 *********************/
		if(mThread == null) {
			LogUtil.v("creating new thread");
			mThread = new Thread(this);
			threadLoop=true;
			mThread.start();
			
			am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE); //현재 Activity 알기위함
			
			soapParser = SoapParser.getInstance(); //soapParser 초기화(Singleton. 없으면 생성함)
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.v("ondestroy called. stop thread and remove locationUpdates");
		threadLoop=false;	//thread loop exit
		
		mMapLocationManager.disableMyLocation();
		//nMapLM 쓰기전
		//locationManager.removeUpdates(listener);	
	}
	
	/**
	 * 스레드의 실행 부분. 현재 프로그램의 상태를 모니터링한다.
	 */
	public void run() {
		while(threadLoop){
			try {
				//현재 Activity 알기(Activity에 알맞은 행동하기위함)
				String topActivityName = am.getRunningTasks(1).get(0).topActivity.getClassName(); 
				LogUtil.v("updateService thread #" + count + " / current: " + topActivityName); 
				count++;

				
				if((uiHandler = UIHandler.getInstance(this)) != null ) {
				} else {
					LogUtil.e("Err: UIHandler has no instance. service and LM stopping...");
					stopSelf();		//stop thread
					mMapLocationManager.disableMyLocation(); //location tracking disable;
				}
				
				//test query check
				String str = ((String)soapParser.getSoapData("select test from test" ,Constants.MSG_TYPE_TEST)); 
				if(str!=null) {
					if(str.compareTo("test") == 0) { 
						LogUtil.i("DB Connection OK");
					}
				} else {
					LogUtil.e("DB Connection Test Failed!");
				}
				
				if(mLocation != null) {
					LogUtil.i("myLocation searching OK: Lat: " + mLocation.getLatitude() + ", Log: " + mLocation.getLongitude());
				} else {
					LogUtil.e("Location is null!");
				}
				
				Thread.sleep(THREAD_INTERVAL_MS);
			} catch(Exception ex) {
				LogUtil.e(ex.toString());
			}
		}
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener 
	= new NMapLocationManager.OnLocationChangeListener() {
		@Override
		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
			//어디론가 발싸! 어디로 갈지는 모름 ㅋ
			LogUtil.v("onLocationChanged invoked!");
			uiHandler.sendMessage(Constants.MSG_TYPE_LOCATION, "", myLocation);
//			LogUtil.v("select * from tLandmark");
//			uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
//					soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
//			LogUtil.v("select * from tPosting");
//			uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
//					soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
			
			mLocation = myLocation;
			
			return true;
		}

		@Override
		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
			// stop location updating
			//			Runnable runnable = new Runnable() {
			//				public void run() {										
			//					stopMyLocation();
			//				}
			//			};
			//			runnable.run();	
			LogUtil.v("Your current location is temporarily unavailable.");
			//Toast.makeText(this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
			LogUtil.v("Your current location is unavailable area.");
			//Toast.makeText(this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
			//stopMyLocation();
		}
	};
	
	//nMapLM 쓰기 전.
//	private LocationListener listener = new LocationListener() {
//		@Override
//		public void onLocationChanged(Location location) {
//			LogUtil.v("onLocationChanged called! Lat: " + location.getLatitude() + ", Log" + location.getLongitude());
//			uiHandler.sendMessage(Constants.MSG_TYPE_LOCATION,null,location);
//		}
//
//		@Override
//		public void onProviderDisabled(String arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//		@Override
//		public void onProviderEnabled(String arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//		@Override
//		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//			// TODO Auto-generated method stub
//			
//		}
//	};
	
}
