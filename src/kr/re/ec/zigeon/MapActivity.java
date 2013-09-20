/**
 * Class Name: MapActivity
 * Description: MapMapMap~~
 * Author: Seo,Ju-ri jooool2@daum.net
 * Version: 0.0.1
 * Created Date: 130829
 * Modified Date: 
 */
package kr.re.ec.zigeon;

import org.apache.http.entity.mime.MinimalField;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.nmaps.NMapPOIflagType;
import kr.re.ec.zigeon.nmaps.NMapViewerResourceProvider;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

public class MapActivity extends NMapActivity implements OnClickListener
, OnMapStateChangeListener, OnCalloutOverlayListener {
	public static final String API_KEY = Constants.NMAP_API_KEY;	//API-KEY

	private ActivityManager activityManager = ActivityManager.getInstance();
	
	private NMapView mMapView = null;	//Naver map object

	private NMapController mMapController = null;	// map controller
	private RelativeLayout MapContainer;	//map on layout
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	 //Overlay Resource Provider
	private NMapOverlayManager mOverlayManager = null;	
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;
	private NGeoPoint locationToReturn;

	private NMapMyLocationOverlay mMyLocationOverlay; 
	public static NMapLocationManager mMapLocationManager; //forced init from UpdateService.onCreate()
	private NMapCompassManager mMapCompassManager;  
	//private MapContainerView mMapContainerView; 

	private SoapParser soapParser;
	private LandmarkDataset mLandmarkArr[];
	
	private NGeoPoint mMapCenter;
	
	private UIHandler uiHandler;
	private int detectRange = 500; //TODO: test phrase 
	
	private Handler messageHandler = new Handler() { //receiver from UpdateService
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mLandmarkArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");

				int markerId = NMapPOIflagType.PIN;		// create marker ID to show on overlay
				
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: what is 0?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: what is 0?(longitude, latitude, String, NMapPOIFlagtype, ?)
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	

				// recreate overlay with location data
				mOverlayManager.clearOverlays();
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				//poiDataOverlay.showAllPOIdata(0);	//set center and zoom which can express all overlay where id==0
				break;
			}
			case Constants.MSG_TYPE_COMMENT:
			{
				//tv Test.setText(msg.getData().getString("msg"));
				break;
			}
			case Constants.MSG_TYPE_MEMBER:
			{
				//tvPostingTest.setText(msg.getData().getString("msg"));
				break;
			}
			case Constants.MSG_TYPE_TEST:
			{	

				break;
			}
			case Constants.MSG_TYPE_LOCATION:
			{
				//use NGeoPoint instead of android.location 
				myLocation = (NGeoPoint)msg.obj;
				LogUtil.v("myLocation is " + myLocation.getLatitude() + ", " + myLocation.getLongitude());

				////WARN: cannot use this query on UpdateService.onLocationChanged().
				//WARN: It may cause to send to other Activity.
				//				LogUtil.v("select * from tLandmark");
				//				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				//						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				//String str = myLocation.getLatitude() + "\n" + myLocation.getLongitude() + "\n";

				//WARN: It may cause you angry. map trace myLocation always.
				//				if (mMapController != null) {
				//					mMapController.animateTo(myLocation);
				//				}

				break;
			}
			}
		}
	};


	private OnDataProviderListener onDataProviderListener;	
	private NMapPlacemark nMapPlacemark;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		/*******add activity list********/
		activityManager.addActivity(this);

		/************** register handler ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		soapParser = SoapParser.getInstance();

		/************ UI init ***********/
		Button btnMoveToMyLocation = (Button) findViewById(R.id.map_btn_move_to_mylocation);
		btnMoveToMyLocation.setOnClickListener(this);
		Button btnSelectLocation = (Button) findViewById(R.id.map_btn_select_location);
		btnSelectLocation.setOnClickListener(this);

		/************ myLocation init *******/
		Bundle bundle = this.getIntent().getExtras(); 
		myLocation = new NGeoPoint();
		myLocation.latitude = bundle.getDouble("lat");
		myLocation.longitude = bundle.getDouble("lon");
		
		/************* map init **************/
		LogUtil.v("map init start");
		MapContainer = (RelativeLayout)findViewById(R.id.mapmap);		// Layout for show map
		mMapView = new NMapView(this);		//create map object
		mMapController = mMapView.getMapController();		//extract controller from map object
		mMapView.setApiKey(API_KEY);		
		MapContainer.addView(mMapView);		//map->layout
		mMapView.setClickable(true);		//can click map
		mMapView.setBuiltInZoomControls(true, null);		//zoom controller for +/- enable
		mMapView.setOnMapStateChangeListener(this);		//event listener

		/**************** overlay init ************************/
		LogUtil.v("overlay init start");
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// create overlay resource provider
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);	//add overlay manager

		//TODO: what is it? it seem to stop working this: CalloutOverlayListener
		//poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  		
		mOverlayManager.setOnCalloutOverlayListener(this);		// register overlay eventlistener


		mMapCompassManager = new NMapCompassManager(this);
		if(mMapLocationManager != null) {
			mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
			startMyLocation();
		} else {
			LogUtil.e("LocationManager is null!");
		} 
		
		/************ mapcenter init ************/
		
		mMapCenter = mMapController.getMapCenter();
		LogUtil.v("mMapCenter: lat: " + mMapCenter.latitude + ", lon: " + mMapCenter.longitude);

		/************ data request ***********/
<<<<<<< HEAD
<<<<<<< HEAD
		LogUtil.v("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
				+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
				+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
=======
		LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
				+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
				+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
>>>>>>> catching bug
=======
		LogUtil.v("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
				+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
				+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
>>>>>>> modified chong che jeok nan gook
					+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
					+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
	
			
			// set data provider listener
		super.setMapDataProviderListener(onDataProviderListener);

	}


	/************ event when clicked overlay *************/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		LogUtil.v("overlay clicked!: " + arg1.getHeadText());
		//show overlay selected effect
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   
	}

	// called when map animation status changed.
	// animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
	// animState : ANIMATION_STATE)STARTED or ANIMATION_STATE_FINISHED
	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	//called when map center changed
	@Override
	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
		// TODO Auto-generated method stub
		LogUtil.v("onMapCenterChange invoked!!!!! oh yeah\nlat: " + arg1.latitude + ", lon: " + arg1.longitude);
		locationToReturn = arg1;
		
<<<<<<< HEAD
<<<<<<< HEAD
		LogUtil.v("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
				+ locationToReturn.getLongitude() + "','" + locationToReturn.getLatitude() + "','" + detectRange
				+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
=======
		LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
				+ locationToReturn.getLongitude() + "','" + locationToReturn.getLatitude() + "','" + detectRange
				+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
>>>>>>> catching bug
=======
		LogUtil.v("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
				+ locationToReturn.getLongitude() + "','" + locationToReturn.getLatitude() + "','" + detectRange
				+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 50 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
>>>>>>> modified chong che jeok nan gook
					+ locationToReturn.getLongitude() + "','" + locationToReturn.getLatitude() + "','" + detectRange
					+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
		
	}


	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		// TODO Auto-generated method stub

	}



	/**
	 * called after map init. 
	 * when no error, =>null
	 * else errorInfo => cause 
	 */
	@Override
	public void onMapInitHandler(NMapView arg0, NMapError errorInfo) {
		// TODO Auto-generated method stub

		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//lon, lat, zoom level
			mMapController.setMapCenter(myLocation, 12); 	//TODO: test phrase
			locationToReturn = myLocation;
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	

	}

	@Override
	public void onZoomLevelChange(NMapView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	private void startMyLocation() {
		if (mMyLocationOverlay != null) {
			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
				mOverlayManager.addOverlay(mMyLocationOverlay);
			}
			if (mMapLocationManager.isMyLocationEnabled()) {
				if (!mMapView.isAutoRotateEnabled()) {
					mMyLocationOverlay.setCompassHeadingVisible(true);
					//mMapCompassManager.enableCompass();
					mMapView.setAutoRotateEnabled(true, false);
					//mMapContainerView.requestLayout();
				} else {
					stopMyLocation();
				}
				mMapView.postInvalidate();
			} else {
				boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
				if (!isMyLocationEnabled) {
					LogUtil.v("Please enable a My Location source in system settings");
					//Toast.makeText(NMapViewer.this, "Please enable a My Location source in system settings",
					//		Toast.LENGTH_LONG).show();
					Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(goToSettings);
					return;
				}
			}
		}
	}

	private void stopMyLocation() {
		if (mMyLocationOverlay != null) {
			mMapLocationManager.disableMyLocation();
			if (mMapView.isAutoRotateEnabled()) {
				mMyLocationOverlay.setCompassHeadingVisible(false);
				mMapCompassManager.disableCompass();
				mMapView.setAutoRotateEnabled(false, false);
				//mMapContainerView.requestLayout();
			}
		}
	}
	@Override
	public void onDestroy () {
		super.onDestroy();
		LogUtil.v("onDestroy called. finish()");
		
		/*********remove activity list******/
		activityManager.removeActivity(this);

		LogUtil.i("removeActivity called");
		finish();
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.map_btn_move_to_mylocation :
		{
			
			startMyLocation();
			if (mMapController != null && myLocation != null) {
				mMapController.animateTo(myLocation);
			} else {
				LogUtil.e("myLocation is null or mMapController is null!");
			}

			break;
		}
		case R.id.map_btn_select_location:
		{
			LogUtil.v("return data: " + locationToReturn.longitude + ", " + locationToReturn.latitude);
			Intent intent = new Intent();
			intent.putExtra("lon", String.valueOf(locationToReturn.longitude));
			intent.putExtra("lat", String.valueOf(locationToReturn.latitude));
					
			setResult(RESULT_OK, intent);
			LogUtil.i("setResult called");
			
			finish();
			break;
		}
		}


	}

//
//	public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {
//
//		if (errInfo != null) {
//			LogUtil.v("Failed to findPlacemarkAtLocation: error=" + errInfo.toString());
//			return;
//		}
//
//		LogUtil.v("onReverseGeocoderResponse: placeMark=" + placeMark.toString());
//		Toast.makeText(MapActivity.this,
//				"onReverseGeocoderResponse: placeMark=" + placeMark.toString(),
//				Toast.LENGTH_LONG).show();
//	}
//
//
//	private NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
//
//		@Override
//		public boolean onLocationChanged(NMapLocationManager locationManager,
//				NGeoPoint myLocation) {
//			findPlacemarkAtLocation(myLocation.getLongitude(), myLocation.getLatitude());
//			//lat,lon -> address
//
//			onReverseGeocoderResponse(nMapPlacemark, null);
//
//			String strFormat = getResources().getString(R.string.map_address);
//			String strResult = String.format(strFormat, nMapPlacemark.toString());
//
//			TextView text = (TextView) findViewById(R.id.map_txt_address);
//			text.setText(strResult);
//
//			return true;
//		}
//
//		@Override
//		public void onLocationUnavailableArea(NMapLocationManager arg0,
//				NGeoPoint arg1) {
//			// TODO Auto-generated method stub
//			Toast.makeText(MapActivity.this,
//					"Your current location is unavailable area.",
//					Toast.LENGTH_LONG).show();
//
//			stopMyLocation();
//		}
//
//		@Override
//		public void onLocationUpdateTimeout(NMapLocationManager arg0) {
//			// TODO Auto-generated method stub
//			Toast.makeText(MapActivity.this,
//					"Your current location is temporarily unavailable.",
//					Toast.LENGTH_LONG).show();
//		}
//	};

}