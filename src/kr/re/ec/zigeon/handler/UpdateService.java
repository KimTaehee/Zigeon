/**
 * Class Name: UpdateService
 * Description: Thread Service. Manage periodical Task (Location, UI, SoapParser)
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.3
 * Created Date: 
 * Modified Date: 130911
 */

package kr.re.ec.zigeon.handler;

import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

import kr.re.ec.zigeon.BalloonHeadButtonActivity;
import kr.re.ec.zigeon.MapActivity;
import kr.re.ec.zigeon.PreferenceActivity;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class UpdateService extends Service implements Runnable{
	private boolean threadLoop=false;
	private int count = 0; 		//thread loop count
	private final static int THREAD_INTERVAL_MS = 60000; 	//thread loop delay
	//private static final int MIN_DISTANCE = 1; 		//min distance(m) to recognize gps updates
	
	private Thread mThread;
	private ActivityManager am;
	
	private LandmarkDataset mBestLandmark;
	private final int bestDetectRange = 500;
	private boolean isLocationChanged;
	
//	private static LocationManager locationManager;
//	private Criteria criteria;
//	private boolean gpsEnabled;
//	private Location location; 
//	

	/*************nMap ****************/
	private NMapLocationManager mMapLocationManager; 
	private NGeoPoint mLocation;	//Location for detection
	
	private UIHandler uiHandler; 	//UIHandler
	private SoapParser soapParser;	//SoapParser
	
	@Override
	public void onCreate(){
		LogUtil.v("oncreate called");
		super.onCreate();
	
		isLocationChanged = false;
		
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
		
		//MapListActivity.mMapLocationManager =  this.mMapLocationManager; //TODO: send LM forcely. test phrase
		MapActivity.mMapLocationManager =  this.mMapLocationManager; //TODO: send LM forcely. test phrase
		
		/************ mLocation Init ************/
		LogUtil.v("mLocation setting");
		mLocation = new NGeoPoint();
		
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		mLocation.set(Double.parseDouble(pref.getString("lon",String.valueOf(Constants.NMAP_DEFAULT_LON)))
				, Double.parseDouble(pref.getString("lat",String.valueOf(Constants.NMAP_DEFAULT_LAT)))) ; //default value
		
		
		
		/********************** create Thread *********************/
		if(mThread == null) {
			LogUtil.v("creating new thread");
			mThread = new Thread(this);
			threadLoop=true;
			
			soapParser = SoapParser.getInstance(); //singleton
			am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE); //what is current Activity?
			
			mThread.start();
			
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.v("ondestroy called. stop thread and remove locationUpdates");
		threadLoop=false;	//thread loop exit
		
		mMapLocationManager.disableMyLocation();
		
		if(mLocation==null) {
			SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("lat", String.valueOf(Constants.NMAP_DEFAULT_LAT));
			editor.putString("lon", String.valueOf(Constants.NMAP_DEFAULT_LON)); //default
			editor.commit();
		}

		//locationManager.removeUpdates(listener);	
	}
	
	/**
	 * monitor status of app
	 */
	public void run() {
		while(threadLoop){
			try {
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
				
				//mLocation = mMapLocationManager.getMyLocation();
				if(mLocation != null) {
					LogUtil.i("myLocation searching OK: Lat: " + mLocation.getLatitude() + ", Lon: " + mLocation.getLongitude());
				} else {
					LogUtil.i("Location is null!");
					isLocationChanged = false;
					
				}
				
				Thread.sleep(THREAD_INTERVAL_MS);
				
				if(isLocationChanged) {
					LogUtil.v("location changed detected");
					//get Top Activity (for appropriate action)
					String topActivityName = am.getRunningTasks(1).get(0).topActivity.getClassName(); 
					LogUtil.v("updateService thread #" + count + " / current: " + topActivityName); 
					count++;
					if (!topActivityName.contains("kr.re.ec.zigeon")) {
						LandmarkDataset[] bestLandmarkArr;
						bestLandmarkArr = (LandmarkDataset[]) soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
								+ mLocation.getLongitude() + "','" + mLocation.getLatitude() + "','" + bestDetectRange
								+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK);
						if(bestLandmarkArr.length != 0) {
							mBestLandmark = bestLandmarkArr[0];
						}

						if(PreferenceActivity.isBalloonNotificationOn) {
							Intent intent = new Intent(UpdateService.this, BalloonService.class);
							intent.putExtra("ldmIdx", mBestLandmark.idx);
							intent.putExtra("ldmName", mBestLandmark.name);
							stopService(intent);
							startService(intent);
	
							isLocationChanged = false;
							LogUtil.i("Condition OK: Start BalloonService");
						} else {
							LogUtil.i("BalloonNotification Mode off. ignore starting balloonservice");
						}
					} else {
						LogUtil.w("timer occered, but my activity is on top: " + topActivityName);
					}
				} else {
					LogUtil.w("timer occered, but no location change detected");
					
				}
				
				
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
			//send location info to current activity.
			LogUtil.v("onLocationChanged invoked!");
			uiHandler.sendMessage(Constants.MSG_TYPE_LOCATION, "", myLocation);
//			LogUtil.v("select * from tLandmark");
//			uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
//					soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
//			LogUtil.v("select * from tPosting");
//			uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
//					soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
			
			mLocation = myLocation;
			
			SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("lat", String.valueOf(mLocation.getLatitude()));
			editor.putString("lon", String.valueOf(mLocation.getLongitude()));
			editor.commit();			
			
			isLocationChanged = true;
			
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
