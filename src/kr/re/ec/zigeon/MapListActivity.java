<<<<<<< HEAD
/* ÀÛ¼ºÀÚ: ¼­ÁÖ¸®
 * ¼öÁ¤ÀÚ: ±èÅÂÈñ
 * gitgit
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

public class MapListActivity extends NMapActivity implements OnMapStateChangeListener, OnCalloutOverlayListener {
	public static final String API_KEY="3aa5ca39d123f5448faff118a4fd9528";	//API-KEY
	
	private NMapView mMapView = null;	//Naver map °´Ã¼
	private ListView lstLandmark;
	private ListView lstPosting;
	private TabHost tabHost;
		
	private NMapController mMapController = null;	// ¸Ê ÄÁÆ®·Ñ·¯
	private LinearLayout MapContainer;	//¸ÊÀ» Ãß°¡ ÇÒ ·¹ÀÌ¾Æ¿ô
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	// ¿À¹ö·¹ÀÌÀÇ ¸®¼Ò½º¸¦ Á¦°øÇÏ±â À§ÇÑ °´Ã¼
	private NMapOverlayManager mOverlayManager = null;	// ¿À¹ö·¹ÀÌ °ü¸®ÀÚ
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;
	
	private NMapMyLocationOverlay mMyLocationOverlay; //130816 ±èÅÂÈñ Ãß°¡
	public static NMapLocationManager mMapLocationManager; //UpdateService.onCreateÀ¸·ÎºÎÅÍ °­Á¦ ÃÊ±âÈ­ ¹ŞÀ½.
	private NMapCompassManager mMapCompassManager; //130816 ±èÅÂÈñ Ãß°¡ 
	//private MapContainerView mMapContainerView; //130816 ±èÅÂÈñ Ãß°¡

	private Intent mIntent;

	private SoapParser soapParser;
	
	private ArrayList<String> mLandmarkArl;		//listview ¼¼ÆÃ¿ë	
	private ArrayList<String> mPostingArl;		//listview ¼¼ÆÃ¿ë
	private ArrayAdapter<String> mLandmarkAdp;		//listview ¼¼ÆÃ¿ë
	private ArrayAdapter<String> mPostingAdp;		//listview ¼¼ÆÃ¿ë
	private LandmarkDataset mLandmarkArr[];
	private PostingDataset mPostingArr[];
	
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService·ÎºÎÅÍÀÇ ¼ö½ÅºÎ! Áß¿äÇÔ
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mLandmarkArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");
				
				int markerId = NMapPOIflagType.PIN;		// ¿À¹ö·¹ÀÌ¿¡ Ç¥½ÃÇÏ±â À§ÇÑ ¸¶Ä¿ ÀÌ¹ÌÁöÀÇ id°ª »ı¼º
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: ¿©±â¼­ 0Àº ¹»±î?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: ?¿¡ ¾Ë¸ÂÀº ¸»À» ±¸ÇÏ½Ã¿À(longitude, latitude, String, NMapPOIFlagtype, ?)
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	
			
				// À§Ä¡ µ¥ÀÌÅÍ¸¦ »ç¿ëÇÏ¿© ¿À¹ö·¹ÀÌ »ı¼º
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				poiDataOverlay.showAllPOIdata(0);	//id°ªÀÌ 0À¸·Î ÁöÁ¤µÈ ¸ğµç ¿À¹ö·¹ÀÌ°¡ Ç¥½ÃµÇ°í ÀÖ´Â À§Ä¡·Î ÁöµµÀÇ Áß½É°ú ZoomÀ» Àç¼³Á¤
				
				
				/********************List¿¡ ¹İ¿µ*******************/
				mLandmarkArl.clear(); //¿ø·¡ ÀÖ´ø°Å Áö¿ì°í
				//LogUtil.v("mLandmarkArr.length : "+ mLandmarkArr.length);
				for(int i=0;i<mLandmarkArr.length;i++){
					//¼Ò¼öÁ¡ÀÌ ±æ¾î¼­ ÁöÀúºĞÇÏ´Ï±î
					int distanceFromMe = (int)(mLandmarkArr[i].getDistance(myLocation));
					
					//À§Ä¡ Á¤º¸°¡ ¾ÆÁ÷ ³ª¿ÀÁö ¾Ê¾ÒÀ» ¶§ ¸Ş½ÃÁö Ãâ·Â
					mLandmarkArl.add(mLandmarkArr[i].name + "\n"
							+ ((distanceFromMe==Constants.INT_NULL)?"Ã£´ÂÁß.. Àá½Ã¸¸ ±â´Ù·ÁºÁ^o^":distanceFromMe + " m"));
				}
				mLandmarkAdp.notifyDataSetChanged();
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ PostingÀ» listview¿¡ ¹İ¿µÇÑ´Ù ************/
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
				//ÀÏ´ÜÀº android.location ´ë½Å NGeoPoint¸¦ ¾²±â·Î ÇÑ´Ù.
				myLocation = (NGeoPoint)msg.obj;
				
				//UpdateServiceÀÇ onLocationChanged¿¡¼­ ¾Æ·¡ÀÇ select¹®À» ¹ßµ¿½ÃÅ°¸é ´Ù¸¥ Activity·Î Àü´ŞµÉ ¿ì·Á°¡ ÀÖ´Ù.
				LogUtil.v("select * from tLandmark");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				
				//String str = myLocation.getLatitude() + "\n" + myLocation.getLongitude() + "\n";

				//ÀÌ°Å »ì·Á³õÀ¸¸é Location ¼ö½ÅÇÒ ¶§¸¶´Ù ÇöÀç À§Ä¡·Î Áöµµ ¿Å±è => ºıÄ§
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

		/************** ÇÚµé·¯ µî·Ï ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/*************Áöµµ ÃÊ±âÈ­ ½ÃÀÛ**************/
		MapContainer = (LinearLayout)findViewById(R.id.map);		//³×ÀÌ¹ö Áöµµ¸¦ ³Ö±â À§ÇÑ LinearLayout
		mMapView = new NMapView(this);		//³×ÀÌ¹ö Áöµµ °´Ã¼ »ı¼º
		mMapController = mMapView.getMapController();		//Áöµµ °´Ã¼·ÎºÎÅÍ ÄÁÆ®·Ñ·¯ ÃßÃâ
		mMapView.setApiKey(API_KEY);		//³×ÀÌ¹ö Áöµµ °´Ã¼¿¡ APIÅ° ÁöÁ¤
		MapContainer.addView(mMapView);		//»ı¼ºµÈ ³×ÀÌ¹ö Áöµµ °´Ã¼¸¦ LinearLayout¿¡ Ãß°¡½ÃÅ´
		mMapView.setClickable(true);		//Áöµµ¸¦ ÅÍÄ¡ÇÒ ¼ö ÀÖµµ·Ï ¿É¼Ç È°¼ºÈ­
		mMapView.setBuiltInZoomControls(true, null);		//È®´ë/Ãà¼Ò¸¦ À§ÇÑ ÁÜ ÄÁÆ®·Ñ·¯ Ç¥½Ã ¿É¼Ç È°¼ºÈ­
		mMapView.setOnMapStateChangeListener(this);		//Áöµµ¿¡ ´ëÇÑ »óÅÂ º¯°æ ÀÌº¥Æ® ¿¬°á

		/************Áöµµ ÃÊ±âÈ­ ³¡*******************/

		/****************¿À¹ö·¹ÀÌ************************/
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// ¿À¹ö·¹ÀÌ ¸®¼Ò½º °ü¸®°´Ã¼ ÇÒ´ç
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);		// ¿À¹ö·¹ÀÌ °ü¸®ÀÚ Ãß°¡
		
		//TODO: ÀÌ°Ç ¹¹Áö? ÀÌ°Ô ÀÖÀ¸¸é CalloutOverlayListener ÀÛµ¿ ¾ÈÇÏ´Â °Í °°±âµµ
		//poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  		
		mOverlayManager.setOnCalloutOverlayListener(this);		// ¿À¹ö·¹ÀÌ ÀÌº¥Æ® µî·Ï
		
		
		//¿©±âºÎÅÍ ¿À¹ö·¹ÀÌ ³¡±îÁö 130816 ±èÅÂÈñ Ãß°¡
		mMapCompassManager = new NMapCompassManager(this);
		if(mMapLocationManager != null) {
			mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
				
			startMyLocation();
		} else {
			LogUtil.e("LocationManager is null!");
		}
		/******************¿À¹ö·¹ÀÌ ³¡********************/


		/***********************ÅÇÅÇ ¹× ¸®½ºÆ®ºä ÃÊ±âÈ­***************************/
		//³»¿ë ¿äÃ»ÇÏ±â
		soapParser = SoapParser.getInstance(); 
		LogUtil.v("data request. select * from tLandmark");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		LogUtil.v("data request. select * from tPosting");
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
		
		//ÅÇ µî·Ï
		tabHost = (TabHost) findViewById(R.id.map_list_tabhost);
        lstLandmark = (ListView) findViewById(R.id.landmarkList);
        lstPosting = (ListView) findViewById(R.id.postingList);
		
        //ÃÊ±â listview ¹®±¸ ÁöÁ¤.
        mLandmarkArl = new ArrayList<String>();
        mLandmarkArl.add("Landmarks Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //listview°¡ ¾Æ´Ñ layoutÀÌ µé¾î°¨¿¡ À¯ÀÇ
        mLandmarkAdp = new ArrayAdapter<String>(this, R.layout.listview_item_landmark , mLandmarkArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl); 
		
        lstLandmark.setAdapter(mLandmarkAdp);
        lstLandmark.setOnItemClickListener(lstLandmarkItemClickListener);
        mLandmarkAdp.setNotifyOnChange(true); //ÀÌ ¿É¼ÇÀÌ ÀÖÀ¸¸é ArrayList°¡ ¼öÁ¤µÉ ¶§ ÀÚµ¿À¸·Î ¹İ¿µµÈ´Ù. strArr´ë½Å ArrayList¸¦ ½á¾ß ÇÏ´Â ÀÌÀ¯
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //ÀÌ ¿É¼ÇÀÌ ÀÖÀ¸¸é ArrayList°¡ ¼öÁ¤µÉ ¶§ ÀÚµ¿À¸·Î ¹İ¿µµÈ´Ù. strArr´ë½Å ArrayList¸¦ ½á¾ß ÇÏ´Â ÀÌÀ¯
        
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
	 * Áöµµ°¡ ÃÊ±âÈ­ µÈ ÈÄ È£ÃâµÊ
	 * Á¤»óÀûÀ¸·Î ÃÊ±âÈ­ µÇ¸é errorInfo°´Ã¼´Â nullÀÌ Àü´ŞµÇ¸ç,
	 * ÃÊ±âÈ­ ½ÇÆĞ ½Ã errorInfo°´Ã¼¿¡ ¿¡·¯ ¿øÀÎÀÌ Àü´ŞµÈ´Ù.
	 */

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//°æµµ, À§µµ, È®´ë Á¤µµ
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	
	}

	//Áöµµ·¹º§ º¯°æ½Ã È£ÃâµÇ¸ç º¯°æµÈ Áöµµ ·¹º§ÀÌ ÆÄ¶ó¹ÌÅÍ·Î Àü´ŞµÊ
	@Override
	public void onZoomLevelChange(NMapView mapview, int level) {
		//LogUtil.v("onZoomLevelChange invoked!");
		// TODO Auto-generated method stub
	}


	//Áöµµ Áß½É º¯°æ ½Ã È£ÃâµÇ¸ç º¯°æµÈ Áß½É ÁÂÇ¥°¡ ÆÄ¶ó¹ÌÅÍ·Î Àü´ŞµÈ´Ù.
	@Override
	public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
		//LogUtil.v("onMapCenterChange invoked!");
		// TODO Auto-generated method stub

	}


	//Áöµµ ¾Ö´Ï¸ŞÀÌ¼Ç »óÅÂ º¯°æ ½Ã È£ÃâµÈ´Ù.
	// animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
	// animState : ANIMATION_STATE)STARTED or ANIMATION_STATE_FINISHED
	@Override
	public void onAnimationStateChange(NMapView arg0, int animType, int animState) {
		//LogUtil.v("onAnimationStateChange invoked!");
		// TODO Auto-generated method stub
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		//LogUtil.v("onMapCenterChangeFine invoked!");
		// TODO Auto-generated method stub
	}

	/************ ¿À¹ö·¹ÀÌ°¡ Å¬¸¯µÇ¾úÀ» ¶§ÀÇ ÀÌº¥Æ®?? ¾Æ´Ñµí *************/
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
	/************ ¿À¹ö·¹ÀÌ°¡ Å¬¸¯µÇ¾úÀ» ¶§ÀÇ ÀÌº¥Æ® *************/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		//Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		LogUtil.v("onCreateCalloutOverlay invoked!");
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
//		
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   //¿À¹ö·¹ÀÌ°¡ ¼±ÅÃµÈ ¸ğ½ÀÀ» Áöµµ¿¡ Ç¥½ÃÇØÁØ´Ù.
	}

//	/************* ¿©±âºÎÅÍ 130816 ±èÅÂÈñ ÀÛ¼º **********************/  
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
	
	/************ ¾×¼Ç¹Ù ¹× ¸Ş´º ÃÊ±âÈ­ ¹× ÀÌº¥Æ® Ã³¸® *******************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_list, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.landmark_tabhost:
		{
			LogUtil.v("action_bubble clicked. ");
			finish(); //TODO: Áöµµ°¡ Á¾·áµÇÁö ¾Ê°Ô ÇÏ°í ½Í¾î... Àç·ÎµùÇÏÀİ¾Æ ¤Ğ¤Ğ 
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
	
	/******************************* ¸®½ºÆ®ºä Å¬¸¯½Ã *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: ¸î ¹øÂ° °ÍÀ» ´­·¶´ÂÁö.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: mLandmarkArr¿Í Listview¿¡ ¿Ã¶ó°£ »çÇ×ÀÇ ÀÏÄ¡¸¦ º¸Àå½ÃÄÑ¾ß ÇÑ´Ù. ¾ÆÁ÷ È®ÀÎµÇÁö ¾ÊÀ½.
			
			//Intent¸¦ ÀÌ¿ëÇÏ¿© LandmarkActivity¿¡ ldmIdx¸¦ Àü´ŞÇÑ´Ù.
			mIntent = new Intent(MapListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);
			startActivity(mIntent);
			
		}
	};
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: ¸î ¹øÂ° °ÍÀ» ´­·¶´ÂÁö.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArr¿Í Listview¿¡ ¿Ã¶ó°£ »çÇ×ÀÇ ÀÏÄ¡¸¦ º¸Àå½ÃÄÑ¾ß ÇÑ´Ù. ¾ÆÁ÷ È®ÀÎµÇÁö ¾ÊÀ½.
			
			mIntent = new Intent(MapListActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};
	
}
=======
/* ì‘ì„±ì: ì„œì£¼ë¦¬
 * ìˆ˜ì •ì: ê¹€íƒœí¬
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

public class MapListActivity extends NMapActivity implements OnMapStateChangeListener, OnCalloutOverlayListener {
	public static final String API_KEY="3aa5ca39d123f5448faff118a4fd9528";	//API-KEY
	
	private NMapView mMapView = null;	//Naver map ê°ì²´
	private ListView lstLandmark;
	private ListView lstPosting;
	private TabHost tabHost;
		
	private NMapController mMapController = null;	// ë§µ ì»¨íŠ¸ë¡¤ëŸ¬
	private LinearLayout MapContainer;	//ë§µì„ ì¶”ê°€ í•  ë ˆì´ì•„ì›ƒ
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	// ì˜¤ë²„ë ˆì´ì˜ ë¦¬ì†ŒìŠ¤ë¥¼ ì œê³µí•˜ê¸° ìœ„í•œ ê°ì²´
	private NMapOverlayManager mOverlayManager = null;	// ì˜¤ë²„ë ˆì´ ê´€ë¦¬ì
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;
	
	private NMapMyLocationOverlay mMyLocationOverlay; //130816 ê¹€íƒœí¬ ì¶”ê°€
	public static NMapLocationManager mMapLocationManager; //UpdateService.onCreateìœ¼ë¡œë¶€í„° ê°•ì œ ì´ˆê¸°í™” ë°›ìŒ.
	private NMapCompassManager mMapCompassManager; //130816 ê¹€íƒœí¬ ì¶”ê°€ 
	//private MapContainerView mMapContainerView; //130816 ê¹€íƒœí¬ ì¶”ê°€

	private Intent mIntent;

	private SoapParser soapParser;
	
	private ArrayList<String> mLandmarkArl;		//listview ì„¸íŒ…ìš©	
	private ArrayList<String> mPostingArl;		//listview ì„¸íŒ…ìš©
	private ArrayAdapter<String> mLandmarkAdp;		//listview ì„¸íŒ…ìš©
	private ArrayAdapter<String> mPostingAdp;		//listview ì„¸íŒ…ìš©
	private LandmarkDataset mLandmarkArr[];
	private PostingDataset mPostingArr[];
	
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateServiceë¡œë¶€í„°ì˜ ìˆ˜ì‹ ë¶€! ì¤‘ìš”í•¨
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mLandmarkArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");
				
				int markerId = NMapPOIflagType.PIN;		// ì˜¤ë²„ë ˆì´ì— í‘œì‹œí•˜ê¸° ìœ„í•œ ë§ˆì»¤ ì´ë¯¸ì§€ì˜ idê°’ ìƒì„±
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: ì—¬ê¸°ì„œ 0ì€ ë­˜ê¹Œ?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: ?ì— ì•Œë§ì€ ë§ì„ êµ¬í•˜ì‹œì˜¤(longitude, latitude, String, NMapPOIFlagtype, ?)
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	
			
				// ìœ„ì¹˜ ë°ì´í„°ë¥¼ ì‚¬ìš©í•˜ì—¬ ì˜¤ë²„ë ˆì´ ìƒì„±
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				poiDataOverlay.showAllPOIdata(0);	//idê°’ì´ 0ìœ¼ë¡œ ì§€ì •ëœ ëª¨ë“  ì˜¤ë²„ë ˆì´ê°€ í‘œì‹œë˜ê³  ìˆëŠ” ìœ„ì¹˜ë¡œ ì§€ë„ì˜ ì¤‘ì‹¬ê³¼ Zoomì„ ì¬ì„¤ì •
				
				
				/********************Listì— ë°˜ì˜*******************/
				mLandmarkArl.clear(); //ì›ë˜ ìˆë˜ê±° ì§€ìš°ê³ 
				//LogUtil.v("mLandmarkArr.length : "+ mLandmarkArr.length);
				for(int i=0;i<mLandmarkArr.length;i++){
					//ì†Œìˆ˜ì ì´ ê¸¸ì–´ì„œ ì§€ì €ë¶„í•˜ë‹ˆê¹Œ
					int distanceFromMe = (int)(mLandmarkArr[i].getDistance(myLocation));
					
					//ìœ„ì¹˜ ì •ë³´ê°€ ì•„ì§ ë‚˜ì˜¤ì§€ ì•Šì•˜ì„ ë•Œ ë©”ì‹œì§€ ì¶œë ¥
					mLandmarkArl.add(mLandmarkArr[i].name + "\n"
							+ ((distanceFromMe==Constants.INT_NULL)?"ì°¾ëŠ”ì¤‘.. ì ì‹œë§Œ ê¸°ë‹¤ë ¤ë´^o^":distanceFromMe + " m"));
				}
				mLandmarkAdp.notifyDataSetChanged();
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ Postingì„ listviewì— ë°˜ì˜í•œë‹¤ ************/
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
				//ì¼ë‹¨ì€ android.location ëŒ€ì‹  NGeoPointë¥¼ ì“°ê¸°ë¡œ í•œë‹¤.
				myLocation = (NGeoPoint)msg.obj;
				
				//UpdateServiceì˜ onLocationChangedì—ì„œ ì•„ë˜ì˜ selectë¬¸ì„ ë°œë™ì‹œí‚¤ë©´ ë‹¤ë¥¸ Activityë¡œ ì „ë‹¬ë  ìš°ë ¤ê°€ ìˆë‹¤.
				LogUtil.v("select * from tLandmark");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				
				//String str = myLocation.getLatitude() + "\n" + myLocation.getLongitude() + "\n";

				//ì´ê±° ì‚´ë ¤ë†“ìœ¼ë©´ Location ìˆ˜ì‹ í•  ë•Œë§ˆë‹¤ í˜„ì¬ ìœ„ì¹˜ë¡œ ì§€ë„ ì˜®ê¹€ => ë¹¡ì¹¨
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

		/************** í•¸ë“¤ëŸ¬ ë“±ë¡ ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/*************ì§€ë„ ì´ˆê¸°í™” ì‹œì‘**************/
		MapContainer = (LinearLayout)findViewById(R.id.map);		//ë„¤ì´ë²„ ì§€ë„ë¥¼ ë„£ê¸° ìœ„í•œ LinearLayout
		mMapView = new NMapView(this);		//ë„¤ì´ë²„ ì§€ë„ ê°ì²´ ìƒì„±
		mMapController = mMapView.getMapController();		//ì§€ë„ ê°ì²´ë¡œë¶€í„° ì»¨íŠ¸ë¡¤ëŸ¬ ì¶”ì¶œ
		mMapView.setApiKey(API_KEY);		//ë„¤ì´ë²„ ì§€ë„ ê°ì²´ì— APIí‚¤ ì§€ì •
		MapContainer.addView(mMapView);		//ìƒì„±ëœ ë„¤ì´ë²„ ì§€ë„ ê°ì²´ë¥¼ LinearLayoutì— ì¶”ê°€ì‹œí‚´
		mMapView.setClickable(true);		//ì§€ë„ë¥¼ í„°ì¹˜í•  ìˆ˜ ìˆë„ë¡ ì˜µì…˜ í™œì„±í™”
		mMapView.setBuiltInZoomControls(true, null);		//í™•ëŒ€/ì¶•ì†Œë¥¼ ìœ„í•œ ì¤Œ ì»¨íŠ¸ë¡¤ëŸ¬ í‘œì‹œ ì˜µì…˜ í™œì„±í™”
		mMapView.setOnMapStateChangeListener(this);		//ì§€ë„ì— ëŒ€í•œ ìƒíƒœ ë³€ê²½ ì´ë²¤íŠ¸ ì—°ê²°

		/************ì§€ë„ ì´ˆê¸°í™” ë*******************/

		/****************ì˜¤ë²„ë ˆì´************************/
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// ì˜¤ë²„ë ˆì´ ë¦¬ì†ŒìŠ¤ ê´€ë¦¬ê°ì²´ í• ë‹¹
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);		// ì˜¤ë²„ë ˆì´ ê´€ë¦¬ì ì¶”ê°€
		
		//TODO: ì´ê±´ ë­ì§€? ì´ê²Œ ìˆìœ¼ë©´ CalloutOverlayListener ì‘ë™ ì•ˆí•˜ëŠ” ê²ƒ ê°™ê¸°ë„
		//poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  		
		mOverlayManager.setOnCalloutOverlayListener(this);		// ì˜¤ë²„ë ˆì´ ì´ë²¤íŠ¸ ë“±ë¡
		
		
		//ì—¬ê¸°ë¶€í„° ì˜¤ë²„ë ˆì´ ëê¹Œì§€ 130816 ê¹€íƒœí¬ ì¶”ê°€
		mMapCompassManager = new NMapCompassManager(this);
		if(mMapLocationManager != null) {
			mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
				
			startMyLocation();
		} else {
			LogUtil.e("LocationManager is null!");
		}
		/******************ì˜¤ë²„ë ˆì´ ë********************/


		/***********************íƒ­íƒ­ ë° ë¦¬ìŠ¤íŠ¸ë·° ì´ˆê¸°í™”***************************/
		//ë‚´ìš© ìš”ì²­í•˜ê¸°
		soapParser = SoapParser.getInstance(); 
		LogUtil.v("data request. select * from tLandmark");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		LogUtil.v("data request. select * from tPosting");
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
		
		//íƒ­ ë“±ë¡
		tabHost = (TabHost) findViewById(R.id.map_list_tabhost);
        lstLandmark = (ListView) findViewById(R.id.landmarkList);
        lstPosting = (ListView) findViewById(R.id.postingList);
		
        //ì´ˆê¸° listview ë¬¸êµ¬ ì§€ì •.
        mLandmarkArl = new ArrayList<String>();
        mLandmarkArl.add("Landmarks Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //listviewê°€ ì•„ë‹Œ layoutì´ ë“¤ì–´ê°ì— ìœ ì˜
        mLandmarkAdp = new ArrayAdapter<String>(this, R.layout.listview_item_landmark , mLandmarkArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl); 
		
        lstLandmark.setAdapter(mLandmarkAdp);
        lstLandmark.setOnItemClickListener(lstLandmarkItemClickListener);
        mLandmarkAdp.setNotifyOnChange(true); //ì´ ì˜µì…˜ì´ ìˆìœ¼ë©´ ArrayListê°€ ìˆ˜ì •ë  ë•Œ ìë™ìœ¼ë¡œ ë°˜ì˜ëœë‹¤. strArrëŒ€ì‹  ArrayListë¥¼ ì¨ì•¼ í•˜ëŠ” ì´ìœ 
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //ì´ ì˜µì…˜ì´ ìˆìœ¼ë©´ ArrayListê°€ ìˆ˜ì •ë  ë•Œ ìë™ìœ¼ë¡œ ë°˜ì˜ëœë‹¤. strArrëŒ€ì‹  ArrayListë¥¼ ì¨ì•¼ í•˜ëŠ” ì´ìœ 
        
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
	 * ì§€ë„ê°€ ì´ˆê¸°í™” ëœ í›„ í˜¸ì¶œë¨
	 * ì •ìƒì ìœ¼ë¡œ ì´ˆê¸°í™” ë˜ë©´ errorInfoê°ì²´ëŠ” nullì´ ì „ë‹¬ë˜ë©°,
	 * ì´ˆê¸°í™” ì‹¤íŒ¨ ì‹œ errorInfoê°ì²´ì— ì—ëŸ¬ ì›ì¸ì´ ì „ë‹¬ëœë‹¤.
	 */

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//ê²½ë„, ìœ„ë„, í™•ëŒ€ ì •ë„
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	
	}

	//ì§€ë„ë ˆë²¨ ë³€ê²½ì‹œ í˜¸ì¶œë˜ë©° ë³€ê²½ëœ ì§€ë„ ë ˆë²¨ì´ íŒŒë¼ë¯¸í„°ë¡œ ì „ë‹¬ë¨
	@Override
	public void onZoomLevelChange(NMapView mapview, int level) {
		//LogUtil.v("onZoomLevelChange invoked!");
		// TODO Auto-generated method stub
	}


	//ì§€ë„ ì¤‘ì‹¬ ë³€ê²½ ì‹œ í˜¸ì¶œë˜ë©° ë³€ê²½ëœ ì¤‘ì‹¬ ì¢Œí‘œê°€ íŒŒë¼ë¯¸í„°ë¡œ ì „ë‹¬ëœë‹¤.
	@Override
	public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
		//LogUtil.v("onMapCenterChange invoked!");
		// TODO Auto-generated method stub

	}


	//ì§€ë„ ì• ë‹ˆë©”ì´ì…˜ ìƒíƒœ ë³€ê²½ ì‹œ í˜¸ì¶œëœë‹¤.
	// animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
	// animState : ANIMATION_STATE)STARTED or ANIMATION_STATE_FINISHED
	@Override
	public void onAnimationStateChange(NMapView arg0, int animType, int animState) {
		//LogUtil.v("onAnimationStateChange invoked!");
		// TODO Auto-generated method stub
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		//LogUtil.v("onMapCenterChangeFine invoked!");
		// TODO Auto-generated method stub
	}

	/************ ì˜¤ë²„ë ˆì´ê°€ í´ë¦­ë˜ì—ˆì„ ë•Œì˜ ì´ë²¤íŠ¸?? ì•„ë‹Œë“¯ *************/
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
	/************ ì˜¤ë²„ë ˆì´ê°€ í´ë¦­ë˜ì—ˆì„ ë•Œì˜ ì´ë²¤íŠ¸ *************/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		//Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		LogUtil.v("onCreateCalloutOverlay invoked!");
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
//		
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   //ì˜¤ë²„ë ˆì´ê°€ ì„ íƒëœ ëª¨ìŠµì„ ì§€ë„ì— í‘œì‹œí•´ì¤€ë‹¤.
	}

//	/************* ì—¬ê¸°ë¶€í„° 130816 ê¹€íƒœí¬ ì‘ì„± **********************/  
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
	
	/************ ì•¡ì…˜ë°” ë° ë©”ë‰´ ì´ˆê¸°í™” ë° ì´ë²¤íŠ¸ ì²˜ë¦¬ *******************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_list, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.landmark_tabhost:
		{
			LogUtil.v("action_bubble clicked. ");
			finish(); //TODO: ì§€ë„ê°€ ì¢…ë£Œë˜ì§€ ì•Šê²Œ í•˜ê³  ì‹¶ì–´... ì¬ë¡œë”©í•˜ì–ì•„ ã… ã…  
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
	
	/******************************* ë¦¬ìŠ¤íŠ¸ë·° í´ë¦­ì‹œ *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: ëª‡ ë²ˆì§¸ ê²ƒì„ ëˆŒë €ëŠ”ì§€.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: mLandmarkArrì™€ Listviewì— ì˜¬ë¼ê°„ ì‚¬í•­ì˜ ì¼ì¹˜ë¥¼ ë³´ì¥ì‹œì¼œì•¼ í•œë‹¤. ì•„ì§ í™•ì¸ë˜ì§€ ì•ŠìŒ.
			
			//Intentë¥¼ ì´ìš©í•˜ì—¬ LandmarkActivityì— ldmIdxë¥¼ ì „ë‹¬í•œë‹¤.
			mIntent = new Intent(MapListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);
			startActivity(mIntent);
			
		}
	};
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: ëª‡ ë²ˆì§¸ ê²ƒì„ ëˆŒë €ëŠ”ì§€.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArrì™€ Listviewì— ì˜¬ë¼ê°„ ì‚¬í•­ì˜ ì¼ì¹˜ë¥¼ ë³´ì¥ì‹œì¼œì•¼ í•œë‹¤. ì•„ì§ í™•ì¸ë˜ì§€ ì•ŠìŒ.
			
			mIntent = new Intent(MapListActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};
	
}
>>>>>>> UTF-8
