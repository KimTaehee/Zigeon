/**
 * Class Name: MapActivity
 * Description: MapMapMap~~
 * Author: Seo,Ju-ri jooool2@daum.net
 * Version: 0.0.1
 * Created Date: 130829
 * Modified Date: 
 */
package kr.re.ec.zigeon;

import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.nmaps.NMapViewerResourceProvider;
import kr.re.ec.zigeon.util.LogUtil;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;

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
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;

public class MapActivity extends NMapActivity implements OnMapStateChangeListener, OnCalloutOverlayListener{
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


	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService로부터의 수신부! 중요함


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
		}


		@Override
		public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
				NMapOverlayItem arg1, Rect arg2) {
			// TODO Auto-generated method stub
			return null;
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

	}
