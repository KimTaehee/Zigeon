<<<<<<< HEAD
/* 작성자: 서주리
 * 수정자: 김태희
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
	
	private NMapView mMapView = null;	//Naver map 객체
	private ListView lstLandmark;
	private ListView lstPosting;
	private TabHost tabHost;
		
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

	private Intent mIntent;

	private SoapParser soapParser;
	
	private ArrayList<String> mLandmarkArl;		//listview 세팅용	
	private ArrayList<String> mPostingArl;		//listview 세팅용
	private ArrayAdapter<String> mLandmarkAdp;		//listview 세팅용
	private ArrayAdapter<String> mPostingAdp;		//listview 세팅용
	private LandmarkDataset mLandmarkArr[];
	private PostingDataset mPostingArr[];
	
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService로부터의 수신부! 중요함
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mLandmarkArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");
				
				int markerId = NMapPOIflagType.PIN;		// 오버레이에 표시하기 위한 마커 이미지의 id값 생성
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: 여기서 0은 뭘까?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: ?에 알맞은 말을 구하시오(longitude, latitude, String, NMapPOIFlagtype, ?)
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	
			
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
				}
				mLandmarkAdp.notifyDataSetChanged();
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ Posting을 listview에 반영한다 ************/
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
				//일단은 android.location 대신 NGeoPoint를 쓰기로 한다.
				myLocation = (NGeoPoint)msg.obj;
				
				//UpdateService의 onLocationChanged에서 아래의 select문을 발동시키면 다른 Activity로 전달될 우려가 있다.
				LogUtil.v("select * from tLandmark");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				
				//String str = myLocation.getLatitude() + "\n" + myLocation.getLongitude() + "\n";

				//이거 살려놓으면 Location 수신할 때마다 현재 위치로 지도 옮김 => 빡침
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
		mMapCompassManager = new NMapCompassManager(this);
		if(mMapLocationManager != null) {
			mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
				
			startMyLocation();
		} else {
			LogUtil.e("LocationManager is null!");
		}
		/******************오버레이 끝********************/


		/***********************탭탭 및 리스트뷰 초기화***************************/
		//내용 요청하기
		soapParser = SoapParser.getInstance(); 
		LogUtil.v("data request. select * from tLandmark");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		LogUtil.v("data request. select * from tPosting");
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
		
		//탭 등록
		tabHost = (TabHost) findViewById(R.id.map_list_tabhost);
        lstLandmark = (ListView) findViewById(R.id.landmarkList);
        lstPosting = (ListView) findViewById(R.id.postingList);
		
        //초기 listview 문구 지정.
        mLandmarkArl = new ArrayList<String>();
        mLandmarkArl.add("Landmarks Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //listview가 아닌 layout이 들어감에 유의
        mLandmarkAdp = new ArrayAdapter<String>(this, R.layout.listview_item_landmark , mLandmarkArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl); 
		
        lstLandmark.setAdapter(mLandmarkAdp);
        lstLandmark.setOnItemClickListener(lstLandmarkItemClickListener);
        mLandmarkAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
        
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
	 * 지도가 초기화 된 후 호출됨
	 * 정상적으로 초기화 되면 errorInfo객체는 null이 전달되며,
	 * 초기화 실패 시 errorInfo객체에 에러 원인이 전달된다.
	 */

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//경도, 위도, 확대 정도
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	
	}

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

	/************ 오버레이가 클릭되었을 때의 이벤트?? 아닌듯 *************/
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
	/************ 오버레이가 클릭되었을 때의 이벤트 *************/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		//Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		LogUtil.v("onCreateCalloutOverlay invoked!");
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
//		
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   //오버레이가 선택된 모습을 지도에 표시해준다.
	}

//	/************* 여기부터 130816 김태희 작성 **********************/  
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
	
	/************ 액션바 및 메뉴 초기화 및 이벤트 처리 *******************/
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
			finish(); //TODO: 지도가 종료되지 않게 하고 싶어... 재로딩하잖아 ㅠㅠ 
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
	
	/******************************* 리스트뷰 클릭시 *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 몇 번째 것을 눌렀는지.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: mLandmarkArr와 Listview에 올라간 사항의 일치를 보장시켜야 한다. 아직 확인되지 않음.
			
			//Intent를 이용하여 LandmarkActivity에 ldmIdx를 전달한다.
			mIntent = new Intent(MapListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);
			startActivity(mIntent);
			
		}
	};
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 몇 번째 것을 눌렀는지.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArr와 Listview에 올라간 사항의 일치를 보장시켜야 한다. 아직 확인되지 않음.
			
			mIntent = new Intent(MapListActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};
	
}
=======
/* �옉�꽦�옄: �꽌二쇰━
 * �닔�젙�옄: 源��깭�씗
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
	
	private NMapView mMapView = null;	//Naver map 媛앹껜
	private ListView lstLandmark;
	private ListView lstPosting;
	private TabHost tabHost;
		
	private NMapController mMapController = null;	// 留� 而⑦듃濡ㅻ윭
	private LinearLayout MapContainer;	//留듭쓣 異붽�� �븷 �젅�씠�븘�썐
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	// �삤踰꾨젅�씠�쓽 由ъ냼�뒪瑜� �젣怨듯븯湲� �쐞�븳 媛앹껜
	private NMapOverlayManager mOverlayManager = null;	// �삤踰꾨젅�씠 愿�由ъ옄
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;
	
	private NMapMyLocationOverlay mMyLocationOverlay; //130816 源��깭�씗 異붽��
	public static NMapLocationManager mMapLocationManager; //UpdateService.onCreate�쑝濡쒕���꽣 媛뺤젣 珥덇린�솕 諛쏆쓬.
	private NMapCompassManager mMapCompassManager; //130816 源��깭�씗 異붽�� 
	//private MapContainerView mMapContainerView; //130816 源��깭�씗 異붽��

	private Intent mIntent;

	private SoapParser soapParser;
	
	private ArrayList<String> mLandmarkArl;		//listview �꽭�똿�슜	
	private ArrayList<String> mPostingArl;		//listview �꽭�똿�슜
	private ArrayAdapter<String> mLandmarkAdp;		//listview �꽭�똿�슜
	private ArrayAdapter<String> mPostingAdp;		//listview �꽭�똿�슜
	private LandmarkDataset mLandmarkArr[];
	private PostingDataset mPostingArr[];
	
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService濡쒕���꽣�쓽 �닔�떊遺�! 以묒슂�븿
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mLandmarkArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");
				
				int markerId = NMapPOIflagType.PIN;		// �삤踰꾨젅�씠�뿉 �몴�떆�븯湲� �쐞�븳 留덉빱 �씠誘몄���쓽 id媛� �깮�꽦
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: �뿬湲곗꽌 0��� 萸섍퉴?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: ?�뿉 �븣留욎�� 留먯쓣 援ы븯�떆�삤(longitude, latitude, String, NMapPOIFlagtype, ?)
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	
			
				// �쐞移� �뜲�씠�꽣瑜� �궗�슜�븯�뿬 �삤踰꾨젅�씠 �깮�꽦
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				poiDataOverlay.showAllPOIdata(0);	//id媛믪씠 0�쑝濡� 吏��젙�맂 紐⑤뱺 �삤踰꾨젅�씠媛� �몴�떆�릺怨� �엳�뒗 �쐞移섎줈 吏��룄�쓽 以묒떖怨� Zoom�쓣 �옱�꽕�젙
				
				
				/********************List�뿉 諛섏쁺*******************/
				mLandmarkArl.clear(); //�썝�옒 �엳�뜕嫄� 吏��슦怨�
				//LogUtil.v("mLandmarkArr.length : "+ mLandmarkArr.length);
				for(int i=0;i<mLandmarkArr.length;i++){
					//�냼�닔�젏�씠 湲몄뼱�꽌 吏����遺꾪븯�땲源�
					int distanceFromMe = (int)(mLandmarkArr[i].getDistance(myLocation));
					
					//�쐞移� �젙蹂닿�� �븘吏� �굹�삤吏� �븡�븯�쓣 �븣 硫붿떆吏� 異쒕젰
					mLandmarkArl.add(mLandmarkArr[i].name + "\n"
							+ ((distanceFromMe==Constants.INT_NULL)?"李얜뒗以�.. �옞�떆留� 湲곕떎�젮遊�^o^":distanceFromMe + " m"));
				}
				mLandmarkAdp.notifyDataSetChanged();
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ Posting�쓣 listview�뿉 諛섏쁺�븳�떎 ************/
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
				//�씪�떒��� android.location ����떊 NGeoPoint瑜� �벐湲곕줈 �븳�떎.
				myLocation = (NGeoPoint)msg.obj;
				
				//UpdateService�쓽 onLocationChanged�뿉�꽌 �븘�옒�쓽 select臾몄쓣 諛쒕룞�떆�궎硫� �떎瑜� Activity濡� �쟾�떖�맆 �슦�젮媛� �엳�떎.
				LogUtil.v("select * from tLandmark");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				
				//String str = myLocation.getLatitude() + "\n" + myLocation.getLongitude() + "\n";

				//�씠嫄� �궡�젮�넃�쑝硫� Location �닔�떊�븷 �븣留덈떎 �쁽�옱 �쐞移섎줈 吏��룄 �삷源� => 鍮≪묠
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

		/************** �빖�뱾�윭 �벑濡� ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/*************吏��룄 珥덇린�솕 �떆�옉**************/
		MapContainer = (LinearLayout)findViewById(R.id.map);		//�꽕�씠踰� 吏��룄瑜� �꽔湲� �쐞�븳 LinearLayout
		mMapView = new NMapView(this);		//�꽕�씠踰� 吏��룄 媛앹껜 �깮�꽦
		mMapController = mMapView.getMapController();		//吏��룄 媛앹껜濡쒕���꽣 而⑦듃濡ㅻ윭 異붿텧
		mMapView.setApiKey(API_KEY);		//�꽕�씠踰� 吏��룄 媛앹껜�뿉 API�궎 吏��젙
		MapContainer.addView(mMapView);		//�깮�꽦�맂 �꽕�씠踰� 吏��룄 媛앹껜瑜� LinearLayout�뿉 異붽���떆�궡
		mMapView.setClickable(true);		//吏��룄瑜� �꽣移섑븷 �닔 �엳�룄濡� �샃�뀡 �솢�꽦�솕
		mMapView.setBuiltInZoomControls(true, null);		//�솗���/異뺤냼瑜� �쐞�븳 以� 而⑦듃濡ㅻ윭 �몴�떆 �샃�뀡 �솢�꽦�솕
		mMapView.setOnMapStateChangeListener(this);		//吏��룄�뿉 ����븳 �긽�깭 蹂�寃� �씠踰ㅽ듃 �뿰寃�

		/************吏��룄 珥덇린�솕 �걹*******************/

		/****************�삤踰꾨젅�씠************************/
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// �삤踰꾨젅�씠 由ъ냼�뒪 愿�由ш컼泥� �븷�떦
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);		// �삤踰꾨젅�씠 愿�由ъ옄 異붽��
		
		//TODO: �씠嫄� 萸먯��? �씠寃� �엳�쑝硫� CalloutOverlayListener �옉�룞 �븞�븯�뒗 寃� 媛숆린�룄
		//poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  		
		mOverlayManager.setOnCalloutOverlayListener(this);		// �삤踰꾨젅�씠 �씠踰ㅽ듃 �벑濡�
		
		
		//�뿬湲곕���꽣 �삤踰꾨젅�씠 �걹源뚯�� 130816 源��깭�씗 異붽��
		mMapCompassManager = new NMapCompassManager(this);
		if(mMapLocationManager != null) {
			mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
				
			startMyLocation();
		} else {
			LogUtil.e("LocationManager is null!");
		}
		/******************�삤踰꾨젅�씠 �걹********************/


		/***********************�꺆�꺆 諛� 由ъ뒪�듃酉� 珥덇린�솕***************************/
		//�궡�슜 �슂泥��븯湲�
		soapParser = SoapParser.getInstance(); 
		LogUtil.v("data request. select * from tLandmark");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		LogUtil.v("data request. select * from tPosting");
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
		
		//�꺆 �벑濡�
		tabHost = (TabHost) findViewById(R.id.map_list_tabhost);
        lstLandmark = (ListView) findViewById(R.id.landmarkList);
        lstPosting = (ListView) findViewById(R.id.postingList);
		
        //珥덇린 listview 臾멸뎄 吏��젙.
        mLandmarkArl = new ArrayList<String>();
        mLandmarkArl.add("Landmarks Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //listview媛� �븘�땶 layout�씠 �뱾�뼱媛먯뿉 �쑀�쓽
        mLandmarkAdp = new ArrayAdapter<String>(this, R.layout.listview_item_landmark , mLandmarkArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl); 
		
        lstLandmark.setAdapter(mLandmarkAdp);
        lstLandmark.setOnItemClickListener(lstLandmarkItemClickListener);
        mLandmarkAdp.setNotifyOnChange(true); //�씠 �샃�뀡�씠 �엳�쑝硫� ArrayList媛� �닔�젙�맆 �븣 �옄�룞�쑝濡� 諛섏쁺�맂�떎. strArr����떊 ArrayList瑜� �뜥�빞 �븯�뒗 �씠�쑀
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //�씠 �샃�뀡�씠 �엳�쑝硫� ArrayList媛� �닔�젙�맆 �븣 �옄�룞�쑝濡� 諛섏쁺�맂�떎. strArr����떊 ArrayList瑜� �뜥�빞 �븯�뒗 �씠�쑀
        
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
	 * 吏��룄媛� 珥덇린�솕 �맂 �썑 �샇異쒕맖
	 * �젙�긽�쟻�쑝濡� 珥덇린�솕 �릺硫� errorInfo媛앹껜�뒗 null�씠 �쟾�떖�릺硫�,
	 * 珥덇린�솕 �떎�뙣 �떆 errorInfo媛앹껜�뿉 �뿉�윭 �썝�씤�씠 �쟾�떖�맂�떎.
	 */

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//寃쎈룄, �쐞�룄, �솗��� �젙�룄
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	
	}

	//吏��룄�젅踰� 蹂�寃쎌떆 �샇異쒕릺硫� 蹂�寃쎈맂 吏��룄 �젅踰⑥씠 �뙆�씪誘명꽣濡� �쟾�떖�맖
	@Override
	public void onZoomLevelChange(NMapView mapview, int level) {
		//LogUtil.v("onZoomLevelChange invoked!");
		// TODO Auto-generated method stub
	}


	//吏��룄 以묒떖 蹂�寃� �떆 �샇異쒕릺硫� 蹂�寃쎈맂 以묒떖 醫뚰몴媛� �뙆�씪誘명꽣濡� �쟾�떖�맂�떎.
	@Override
	public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
		//LogUtil.v("onMapCenterChange invoked!");
		// TODO Auto-generated method stub

	}


	//吏��룄 �븷�땲硫붿씠�뀡 �긽�깭 蹂�寃� �떆 �샇異쒕맂�떎.
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

	/************ �삤踰꾨젅�씠媛� �겢由��릺�뿀�쓣 �븣�쓽 �씠踰ㅽ듃?? �븘�땶�벏 *************/
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
	/************ �삤踰꾨젅�씠媛� �겢由��릺�뿀�쓣 �븣�쓽 �씠踰ㅽ듃 *************/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		//Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		LogUtil.v("onCreateCalloutOverlay invoked!");
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
//		
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   //�삤踰꾨젅�씠媛� �꽑�깮�맂 紐⑥뒿�쓣 吏��룄�뿉 �몴�떆�빐以��떎.
	}

//	/************* �뿬湲곕���꽣 130816 源��깭�씗 �옉�꽦 **********************/  
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
	
	/************ �븸�뀡諛� 諛� 硫붾돱 珥덇린�솕 諛� �씠踰ㅽ듃 泥섎━ *******************/
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
			finish(); //TODO: 吏��룄媛� 醫낅즺�릺吏� �븡寃� �븯怨� �떢�뼱... �옱濡쒕뵫�븯�옏�븘 �뀪�뀪 
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
	
	/******************************* 由ъ뒪�듃酉� �겢由��떆 *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 紐� 踰덉㎏ 寃껋쓣 �닃����뒗吏�.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: mLandmarkArr��� Listview�뿉 �삱�씪媛� �궗�빆�쓽 �씪移섎�� 蹂댁옣�떆耳쒖빞 �븳�떎. �븘吏� �솗�씤�릺吏� �븡�쓬.
			
			//Intent瑜� �씠�슜�븯�뿬 LandmarkActivity�뿉 ldmIdx瑜� �쟾�떖�븳�떎.
			mIntent = new Intent(MapListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);
			startActivity(mIntent);
			
		}
	};
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: 紐� 踰덉㎏ 寃껋쓣 �닃����뒗吏�.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArr��� Listview�뿉 �삱�씪媛� �궗�빆�쓽 �씪移섎�� 蹂댁옣�떆耳쒖빞 �븳�떎. �븘吏� �솗�씤�릺吏� �븡�쓬.
			
			mIntent = new Intent(MapListActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};
	
}
>>>>>>> UTF-8
