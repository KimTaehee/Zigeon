/**
 * Class Name: MapActivity
 * Description: MapMapMap~~
 * Author: Seo,Ju-ri jooool2@daum.net
 * Version: 0.0.1
 * Created Date: 130829
 * Modified Date: 
 */
package kr.re.ec.zigeon;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

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
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

public class MapActivity extends NMapActivity implements OnClickListener, OnMapStateChangeListener, OnCalloutOverlayListener{
	public static final String API_KEY="3aa5ca39d123f5448faff118a4fd9528";	//API-KEY

	private NMapView mMapView = null;	//Naver map 객체

	private NMapController mMapController = null;	// 맵 컨트롤러
	private RelativeLayout MapContainer;	//맵을 추가 할 레이아웃
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	// 오버레이의 리소스를 제공하기 위한 객체
	private NMapOverlayManager mOverlayManager = null;	// 오버레이 관리자
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;

	private NMapMyLocationOverlay mMyLocationOverlay; //130816 김태희 추가
	public static NMapLocationManager mMapLocationManager; //UpdateService.onCreate으로부터 강제 초기화 받음.
	private NMapCompassManager mMapCompassManager; //130816 김태희 추가 
	//private MapContainerView mMapContainerView; //130816 김태희 추가

	private SoapParser soapParser;
	private LandmarkDataset mLandmarkArr[];

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
				LogUtil.v("myLocation is " + myLocation.getLatitude() + ", " + myLocation.getLongitude());
				
				//UpdateService의 onLocationChanged에서 아래의 select문을 발동시키면 다른 Activity로 전달될 우려가 있다.
//				LogUtil.v("select * from tLandmark");
//				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
//						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
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
		setContentView(R.layout.activity_map);

		/************** 핸들러 등록 ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		
		Button btn = (Button) findViewById(R.id.map_btn_gps);
		btn.setOnClickListener(this);
		

		/*************지도 초기화 시작**************/
		LogUtil.v("map init start");
		MapContainer = (RelativeLayout)findViewById(R.id.mapmap);		//네이버 지도를 넣기 위한 RelativeLayout
		mMapView = new NMapView(this);		//네이버 지도 객체 생성
		mMapController = mMapView.getMapController();		//지도 객체로부터 컨트롤러 추출
		mMapView.setApiKey(API_KEY);		//네이버 지도 객체에 API키 지정
		MapContainer.addView(mMapView);		//생성된 네이버 지도 객체를 RelativeLayout에 추가시킴
		mMapView.setClickable(true);		//지도를 터치할 수 있도록 옵션 활성화
		mMapView.setBuiltInZoomControls(true, null);		//확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
		mMapView.setOnMapStateChangeListener(this);		//지도에 대한 상태 변경 이벤트 연결

		/************지도 초기화 끝*******************/

		/****************오버레이************************/
		LogUtil.v("overlay init start");
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

		
		
		soapParser = SoapParser.getInstance(); 
//		LogUtil.v("data request. select * from tLandmark");
//		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
//				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		
	
		
	}


	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		// TODO Auto-generated method stub
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   //오버레이가 선택된 모습을 지도에 표시해준다.
	}

	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 지도가 초기화 된 후 호출됨
	 * 정상적으로 초기화 되면 errorInfo객체는 null이 전달되며,
	 * 초기화 실패 시 errorInfo객체에 에러 원인이 전달된다.
	 */
	@Override
	public void onMapInitHandler(NMapView arg0, NMapError errorInfo) {
		// TODO Auto-generated method stub

		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//경도, 위도, 확대 정도
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
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
		finish();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.map_btn_gps : 
			break;
		}
	}

}