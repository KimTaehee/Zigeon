
/* Author: SeoJuri
 * Modifier: KimTaehee

 */

package kr.re.ec.zigeon;

import java.util.ArrayList;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.nmaps.NMapPOIflagType;
import kr.re.ec.zigeon.nmaps.NMapViewerResourceProvider;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
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
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;

public class MapListActivity extends NMapActivity 
	implements OnMapStateChangeListener, OnCalloutOverlayListener {
	
	public static final String API_KEY="3aa5ca39d123f5448faff118a4fd9528";	//API-KEY
	
	private NMapView mMapView = null;	//Naver map object

	private ListView lstLandmark;
	private ListView lstPosting;
	private TabHost tabHost;

	private NMapController mMapController = null;	
	private LinearLayout MapContainer;	//map on layout
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	// Overlay Resource Provider
	private NMapOverlayManager mOverlayManager = null;	// Overlay manager
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;
	
	private NMapMyLocationOverlay mMyLocationOverlay; 
	public static NMapLocationManager mMapLocationManager; //forced init from UpdateService.onCreate()
	private NMapCompassManager mMapCompassManager; 
	//private MapContainerView mMapContainerView; 

	private Intent mIntent;

	private SoapParser soapParser;
	
	private ArrayList<String> mLandmarkArl;		//to set listview	
	private ArrayList<String> mPostingArl;		//to set listview
	private ArrayAdapter<String> mLandmarkAdp;		//to set listview
	private ArrayAdapter<String> mPostingAdp;		//to set listview
	private LandmarkDataset mLandmarkArr[];
	private PostingDataset mPostingArr[];
	
	private UIHandler uiHandler;
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
					//TODO: what is 0?
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	
			
				// create overlay with location data
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				poiDataOverlay.showAllPOIdata(0);	//set center and zoom which can express all overlay where id==0
				
				
				/******************** reflect on List*******************/
				mLandmarkArl.clear(); //reset arraylist
				//LogUtil.v("mLandmarkArr.length : "+ mLandmarkArr.length);
				for(int i=0;i<mLandmarkArr.length;i++){
					//double->int
					int distanceFromMe = (int)(mLandmarkArr[i].getDistance(myLocation));
					
					//init string
					mLandmarkArl.add(mLandmarkArr[i].name + "\n"
							+ ((distanceFromMe==Constants.INT_NULL)?"finding.. ^o^":distanceFromMe + " m"));
				}
				mLandmarkAdp.notifyDataSetChanged();
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ reflect Posting on listview ************/
				mPostingArl.clear();
				 
				//LogUtil.v("mPostingArr.length : "+ mPostingArr.length);
				for(int i=0;i<mPostingArr.length;i++){
					mPostingArl.add(mPostingArr[i].title);
				}
				mPostingAdp.notifyDataSetChanged();
				//LogUtil.i("mPostingAdp.notifyDataSetChanged()");
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
				
				//WARN: cannot use this query on UpdateService.onLocationChanged().
				//WARN: It may cause to send to other Activity.
				LogUtil.v("select * from tLandmark");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_list);

		/************** register handler ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/************* map init **************/
		MapContainer = (LinearLayout)findViewById(R.id.map);		// LinearLayout for show map
		mMapView = new NMapView(this);		//create map object
		mMapController = mMapView.getMapController();		//extract controller from map object
		mMapView.setApiKey(API_KEY);		
		MapContainer.addView(mMapView);		//map->layout
		mMapView.setClickable(true);		//can click map
		mMapView.setBuiltInZoomControls(true, null);		//zoom controller for +/- enable
		mMapView.setOnMapStateChangeListener(this);		//event listener

		/**************** overlay init ************************/
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// create overlay resource provider 
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider); 	//add overlay manager
		
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

		/*********************** tab and listview init***************************/
		//request contents
		soapParser = SoapParser.getInstance(); 
		LogUtil.v("data request. select * from tLandmark");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		LogUtil.v("data request. select * from tPosting");
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
		
		//resigter tab
		tabHost = (TabHost) findViewById(R.id.map_list_tabhost);
        lstLandmark = (ListView) findViewById(R.id.landmarkList);
        lstPosting = (ListView) findViewById(R.id.postingList);
		
        //first listview string 
        mLandmarkArl = new ArrayList<String>();
        mLandmarkArl.add("Landmarks Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //WARN: no listview, but layout
        mLandmarkAdp = new ArrayAdapter<String>(this, R.layout.listview_item_landmark , mLandmarkArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl); 
		
        lstLandmark.setAdapter(mLandmarkAdp);
        lstLandmark.setOnItemClickListener(lstLandmarkItemClickListener);
        mLandmarkAdp.setNotifyOnChange(true); //this can detect modify on ArrayList. SHOULD use ArrayList, not strArr
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //this can detect modify on ArrayList. SHOULD use ArrayList.
        
        tabHost.setup(); 
        
        TabSpec ts1 = tabHost.newTabSpec("Landmark");
	    ts1.setIndicator("Landmark");
	    ts1.setContent(R.id.landmarkList);
	    tabHost.addTab(ts1);
	
	    TabSpec ts2 = tabHost.newTabSpec("Posting");
	    ts2.setIndicator("Posting");
	    ts2.setContent(R.id.postingList);
	    tabHost.addTab(ts2);
	
	    tabHost.setCurrentTab(0);
	}

	/**
	 * called after map init. 
	 * when no error, =>null
	 * else errorInfo => cause 
	 */

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//lon, lat, zoom level
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	
	}

	//called when zoom level changed.
	@Override
	public void onZoomLevelChange(NMapView mapview, int level) {
		//LogUtil.v("onZoomLevelChange invoked!");

	}


	//called when map center changed
	@Override
	public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
		//LogUtil.v("onMapCenterChange invoked!");
	}


	// called when map animation status changed.
	// animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
	// animState : ANIMATION_STATE)STARTED or ANIMATION_STATE_FINISHED
	@Override
	public void onAnimationStateChange(NMapView arg0, int animType, int animState) {
		//LogUtil.v("onAnimationStateChange invoked!");
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		//LogUtil.v("onMapCenterChangeFine invoked!");
	}

	/************ what is this? *************/
	public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item){
		LogUtil.i("onCalloutClick invoked!");
		Toast.makeText(MapListActivity.this,"onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
		
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
	}
	
	/************ onFocusChanged ********************/
	public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item){
		if(item != null){
			LogUtil.v("onFocusChanged: " + item.toString());
		}else{

			LogUtil.v("onFocusChanged: ");
		}
	}
	/************ event when clicked overlay *************/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		//Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		LogUtil.v("onCreateCalloutOverlay invoked!");
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
//		
		//show overlay selected effect
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   
	}

 
//	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener 
//	= new NMapLocationManager.OnLocationChangeListener() {
//		@Override
//		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
//			if (mMapController != null) {
//				mMapController.animateTo(myLocation);
//			}
//			return true;
//		}
//
//		@Override
//		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
//			// stop location updating
//			//			Runnable runnable = new Runnable() {
//			//				public void run() {										
//			//					stopMyLocation();
//			//				}
//			//			};
//			//			runnable.run();	
//			LogUtil.v("Your current location is temporarily unavailable.");
//			//Toast.makeText(this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
//		}
//
//		@Override
//		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
//			LogUtil.v("Your current location is unavailable area.");
//			//Toast.makeText(this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
//			stopMyLocation();
//		}
//	};

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
		finish();
	}
	
	/************ actionbar & menu init, event processing  *******************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_list, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.map_list_action_bubble:
		{
			LogUtil.v("action_bubble clicked. ");
			finish(); //TODO: need to reduce MAP Loading again.. TT 
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		case R.id.map_list_action_landmark_write:
		{
			startActivity(new Intent(this,LandmarkWriteActivity.class));
			overridePendingTransition(0, 0); //no switching animation
			break;			
		}
		}
		return true;
	}
	
	/******************************* when listview clicked *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position. 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: SHOULD match mLandmarkArr contents == Listview contents. need to test 
			
			//send ldmIdx to LandmarkActivity using Intent
			mIntent = new Intent(MapListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);
			startActivity(mIntent);
			
		}
	};
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: SHOULD match mPostingArr contents == Listview contents. need to test 
			
			//send ldmIdx to PostingActivity using Intent
			mIntent = new Intent(MapListActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};
	
}
