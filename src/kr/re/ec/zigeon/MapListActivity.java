<<<<<<< HEAD
/* 작성자: 서주리
 * 수정자: 김태희
<<<<<<< HEAD
 * gitgit
=======
>>>>>>> origin/KTHWorking
=======
/* Author: SeoJuri
 * Modifier: KimTaehee

>>>>>>> origin/Jool
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

<<<<<<< HEAD
public class MapListActivity extends NMapActivity implements OnMapStateChangeListener, OnCalloutOverlayListener {
	public static final String API_KEY="3aa5ca39d123f5448faff118a4fd9528";	//API-KEY
	
	private NMapView mMapView = null;	//Naver map 객체
=======
public class MapListActivity extends NMapActivity 
	implements OnMapStateChangeListener, OnCalloutOverlayListener {
	
	public static final String API_KEY="3aa5ca39d123f5448faff118a4fd9528";	//API-KEY
	
	private NMapView mMapView = null;	//Naver map object
>>>>>>> origin/Jool
	private ListView lstLandmark;
	private ListView lstPosting;
	private TabHost tabHost;
		
<<<<<<< HEAD
	private NMapController mMapController = null;	// 맵 컨트롤러
	private LinearLayout MapContainer;	//맵을 추가 할 레이아웃
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	// 오버레이의 리소스를 제공하기 위한 객체
	private NMapOverlayManager mOverlayManager = null;	// 오버레이 관리자
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;
	
	private NMapMyLocationOverlay mMyLocationOverlay; //130816 김태희 추가
	public static NMapLocationManager mMapLocationManager; //UpdateService.onCreate으로부터 강제 초기화 받음.
	private NMapCompassManager mMapCompassManager; //130816 김태희 추가 
	//private MapContainerView mMapContainerView; //130816 김태희 추가
=======
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
>>>>>>> origin/Jool

	private Intent mIntent;

	private SoapParser soapParser;
	
<<<<<<< HEAD
	private ArrayList<String> mLandmarkArl;		//listview 세팅용	
	private ArrayList<String> mPostingArl;		//listview 세팅용
	private ArrayAdapter<String> mLandmarkAdp;		//listview 세팅용
	private ArrayAdapter<String> mPostingAdp;		//listview 세팅용
=======
	private ArrayList<String> mLandmarkArl;		//to set listview	
	private ArrayList<String> mPostingArl;		//to set listview
	private ArrayAdapter<String> mLandmarkAdp;		//to set listview
	private ArrayAdapter<String> mPostingAdp;		//to set listview
>>>>>>> origin/Jool
	private LandmarkDataset mLandmarkArr[];
	private PostingDataset mPostingArr[];
	
	private UIHandler uiHandler;
<<<<<<< HEAD
	private Handler messageHandler = new Handler() { //UpdateService로부터의 수신부! 중요함
=======
	private Handler messageHandler = new Handler() { //receiver from UpdateService
>>>>>>> origin/Jool
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mLandmarkArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");
				
<<<<<<< HEAD
				int markerId = NMapPOIflagType.PIN;		// 오버레이에 표시하기 위한 마커 이미지의 id값 생성
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: 여기서 0은 뭘까?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: ?에 알맞은 말을 구하시오(longitude, latitude, String, NMapPOIFlagtype, ?)
=======
				int markerId = NMapPOIflagType.PIN;		// create marker ID to show on overlay
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: what is 0?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: what is 0?
>>>>>>> origin/Jool
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	
			
<<<<<<< HEAD
				// 위치 데이터를 사용하여 오버레이 생성
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				poiDataOverlay.showAllPOIdata(0);	//id값이 0으로 지정된 모든 오버레이가 표시되고 있는 위치로 지도의 중심과 Zoom을 재설정
				
				
				/********************List에 반영*******************/
				mLandmarkArl.clear(); //원래 있던거 지우고
				//LogUtil.v("mLandmarkArr.length : "+ mLandmarkArr.length);
				for(int i=0;i<mLandmarkArr.length;i++){
					//소수점이 길어서 지저분하니까
					int distanceFromMe = (int)(mLandmarkArr[i].getDistance(myLocation));
					
					//위치 정보가 아직 나오지 않았을 때 메시지 출력
					mLandmarkArl.add(mLandmarkArr[i].name + "\n"
							+ ((distanceFromMe==Constants.INT_NULL)?"찾는중.. 잠시만 기다려봐^o^":distanceFromMe + " m"));
=======
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
>>>>>>> origin/Jool
				}
				mLandmarkAdp.notifyDataSetChanged();
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
<<<<<<< HEAD
				/************ Posting을 listview에 반영한다 ************/
=======
				/************ reflect Posting on listview ************/
>>>>>>> origin/Jool
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
<<<<<<< HEAD
				//일단은 android.location 대신 NGeoPoint를 쓰기로 한다.
				myLocation = (NGeoPoint)msg.obj;
				
				//UpdateService의 onLocationChanged에서 아래의 select문을 발동시키면 다른 Activity로 전달될 우려가 있다.
=======
				//use NGeoPoint instead of android.location 
				myLocation = (NGeoPoint)msg.obj;
				
				//WARN: cannot use this query on UpdateService.onLocationChanged().
				//WARN: It may cause to send to other Activity.
>>>>>>> origin/Jool
				LogUtil.v("select * from tLandmark");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				
				//String str = myLocation.getLatitude() + "\n" + myLocation.getLongitude() + "\n";

<<<<<<< HEAD
				//이거 살려놓으면 Location 수신할 때마다 현재 위치로 지도 옮김 => 빡침
=======
				//WARN: It may cause you angry. map trace myLocation always.
>>>>>>> origin/Jool
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

<<<<<<< HEAD
		/************** 핸들러 등록 ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/*************지도 초기화 시작**************/
		MapContainer = (LinearLayout)findViewById(R.id.map);		//네이버 지도를 넣기 위한 LinearLayout
		mMapView = new NMapView(this);		//네이버 지도 객체 생성
		mMapController = mMapView.getMapController();		//지도 객체로부터 컨트롤러 추출
		mMapView.setApiKey(API_KEY);		//네이버 지도 객체에 API키 지정
		MapContainer.addView(mMapView);		//생성된 네이버 지도 객체를 LinearLayout에 추가시킴
		mMapView.setClickable(true);		//지도를 터치할 수 있도록 옵션 활성화
		mMapView.setBuiltInZoomControls(true, null);		//확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
		mMapView.setOnMapStateChangeListener(this);		//지도에 대한 상태 변경 이벤트 연결

		/************지도 초기화 끝*******************/

		/****************오버레이************************/
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// 오버레이 리소스 관리객체 할당
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);		// 오버레이 관리자 추가
		
		//TODO: 이건 뭐지? 이게 있으면 CalloutOverlayListener 작동 안하는 것 같기도
		//poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  		
		mOverlayManager.setOnCalloutOverlayListener(this);		// 오버레이 이벤트 등록
		
		
		//여기부터 오버레이 끝까지 130816 김태희 추가
=======
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
		
>>>>>>> origin/Jool
		mMapCompassManager = new NMapCompassManager(this);
		if(mMapLocationManager != null) {
			mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
				
			startMyLocation();
		} else {
			LogUtil.e("LocationManager is null!");
		}
<<<<<<< HEAD
		/******************오버레이 끝********************/


		/***********************탭탭 및 리스트뷰 초기화***************************/
		//내용 요청하기
=======

		/*********************** tab and listview init***************************/
		//request contents
>>>>>>> origin/Jool
		soapParser = SoapParser.getInstance(); 
		LogUtil.v("data request. select * from tLandmark");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		LogUtil.v("data request. select * from tPosting");
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
		
<<<<<<< HEAD
		//탭 등록
=======
		//resigter tab
>>>>>>> origin/Jool
		tabHost = (TabHost) findViewById(R.id.map_list_tabhost);
        lstLandmark = (ListView) findViewById(R.id.landmarkList);
        lstPosting = (ListView) findViewById(R.id.postingList);
		
<<<<<<< HEAD
        //초기 listview 문구 지정.
=======
        //first listview string 
>>>>>>> origin/Jool
        mLandmarkArl = new ArrayList<String>();
        mLandmarkArl.add("Landmarks Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
<<<<<<< HEAD
        //listview가 아닌 layout이 들어감에 유의
=======
        //WARN: no listview, but layout
>>>>>>> origin/Jool
        mLandmarkAdp = new ArrayAdapter<String>(this, R.layout.listview_item_landmark , mLandmarkArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl); 
		
        lstLandmark.setAdapter(mLandmarkAdp);
        lstLandmark.setOnItemClickListener(lstLandmarkItemClickListener);
<<<<<<< HEAD
        mLandmarkAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
=======
        mLandmarkAdp.setNotifyOnChange(true); //this can detect modify on ArrayList. SHOULD use ArrayList, not strArr
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //this can detect modify on ArrayList. SHOULD use ArrayList.
>>>>>>> origin/Jool
        
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
<<<<<<< HEAD
	 * 지도가 초기화 된 후 호출됨
	 * 정상적으로 초기화 되면 errorInfo객체는 null이 전달되며,
	 * 초기화 실패 시 errorInfo객체에 에러 원인이 전달된다.
=======
	 * called after map init. 
	 * when no error, =>null
	 * else errorInfo => cause 
>>>>>>> origin/Jool
	 */

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
<<<<<<< HEAD
			//경도, 위도, 확대 정도
=======
			//lon, lat, zoom level
>>>>>>> origin/Jool
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	
	}

<<<<<<< HEAD
	//지도레벨 변경시 호출되며 변경된 지도 레벨이 파라미터로 전달됨
	@Override
	public void onZoomLevelChange(NMapView mapview, int level) {
		//LogUtil.v("onZoomLevelChange invoked!");
		// TODO Auto-generated method stub
	}


	//지도 중심 변경 시 호출되며 변경된 중심 좌표가 파라미터로 전달된다.
	@Override
	public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
		//LogUtil.v("onMapCenterChange invoked!");
		// TODO Auto-generated method stub

	}


	//지도 애니메이션 상태 변경 시 호출된다.
=======
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
>>>>>>> origin/Jool
	// animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
	// animState : ANIMATION_STATE)STARTED or ANIMATION_STATE_FINISHED
	@Override
	public void onAnimationStateChange(NMapView arg0, int animType, int animState) {
		//LogUtil.v("onAnimationStateChange invoked!");
<<<<<<< HEAD
		// TODO Auto-generated method stub
=======
>>>>>>> origin/Jool
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		//LogUtil.v("onMapCenterChangeFine invoked!");
<<<<<<< HEAD
		// TODO Auto-generated method stub
	}

	/************ 오버레이가 클릭되었을 때의 이벤트?? 아닌듯 *************/
=======
	}

	/************ what is this? *************/
>>>>>>> origin/Jool
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
<<<<<<< HEAD
	/************ 오버레이가 클릭되었을 때의 이벤트 *************/
=======
	/************ event when clicked overlay *************/
>>>>>>> origin/Jool
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		//Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		LogUtil.v("onCreateCalloutOverlay invoked!");
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
//		
<<<<<<< HEAD
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   //오버레이가 선택된 모습을 지도에 표시해준다.
	}

//	/************* 여기부터 130816 김태희 작성 **********************/  
=======
		//show overlay selected effect
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   
	}

 
>>>>>>> origin/Jool
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
	
<<<<<<< HEAD
	/************ 액션바 및 메뉴 초기화 및 이벤트 처리 *******************/
=======
	/************ actionbar & menu init, event processing  *******************/
>>>>>>> origin/Jool
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
<<<<<<< HEAD
			finish(); //TODO: 지도가 종료되지 않게 하고 싶어... 재로딩하잖아 ㅠㅠ 
=======
			finish(); //TODO: need to reduce MAP Loading again.. TT 
>>>>>>> origin/Jool
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
	
<<<<<<< HEAD
	/******************************* 리스트뷰 클릭시 *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 몇 번째 것을 눌렀는지.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: mLandmarkArr와 Listview에 올라간 사항의 일치를 보장시켜야 한다. 아직 확인되지 않음.
			
			//Intent를 이용하여 LandmarkActivity에 ldmIdx를 전달한다.
=======
	/******************************* when listview clicked *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position. 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: SHOULD match mLandmarkArr contents == Listview contents. need to test 
			
			//send ldmIdx to LandmarkActivity using Intent
>>>>>>> origin/Jool
			mIntent = new Intent(MapListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);
			startActivity(mIntent);
			
		}
	};
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
<<<<<<< HEAD
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 몇 번째 것을 눌렀는지.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArr와 Listview에 올라간 사항의 일치를 보장시켜야 한다. 아직 확인되지 않음.
			
=======
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: SHOULD match mPostingArr contents == Listview contents. need to test 
			
			//send ldmIdx to PostingActivity using Intent
>>>>>>> origin/Jool
			mIntent = new Intent(MapListActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};
	
}
