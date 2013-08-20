/* �ۼ���: ���ָ�
 * ������: ������
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
	
	private NMapView mMapView = null;	//Naver map ��ü
	private ListView lstLandmark;
	private ListView lstPosting;
	private TabHost tabHost;
		
	private NMapController mMapController = null;	// �� ��Ʈ�ѷ�
	private LinearLayout MapContainer;	//���� �߰� �� ���̾ƿ�
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	// ���������� ���ҽ��� �����ϱ� ���� ��ü
	private NMapOverlayManager mOverlayManager = null;	// �������� ������
	//private OnStateChangeListener onPOIdataStateChangeListener = null;
	private NGeoPoint myLocation;
	
	private NMapMyLocationOverlay mMyLocationOverlay; //130816 ������ �߰�
	public static NMapLocationManager mMapLocationManager; //UpdateService.onCreate���κ��� ���� �ʱ�ȭ ����.
	private NMapCompassManager mMapCompassManager; //130816 ������ �߰� 
	//private MapContainerView mMapContainerView; //130816 ������ �߰�

	private Intent mIntent;

	private SoapParser soapParser;
	
	private ArrayList<String> mLandmarkArl;		//listview ���ÿ�	
	private ArrayList<String> mPostingArl;		//listview ���ÿ�
	private ArrayAdapter<String> mLandmarkAdp;		//listview ���ÿ�
	private ArrayAdapter<String> mPostingAdp;		//listview ���ÿ�
	private LandmarkDataset mLandmarkArr[];
	private PostingDataset mPostingArr[];
	
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService�κ����� ���ź�! �߿���
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mLandmarkArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");
				
				int markerId = NMapPOIflagType.PIN;		// �������̿� ǥ���ϱ� ���� ��Ŀ �̹����� id�� ����
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: ���⼭ 0�� ����?
				for(int i=0;i<mLandmarkArr.length;i++) {
					//TODO: ?�� �˸��� ���� ���Ͻÿ�(longitude, latitude, String, NMapPOIFlagtype, ?)
					poiData.addPOIitem(mLandmarkArr[i].longitude, 
							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
				}
				poiData.endPOIdata();	
			
				// ��ġ �����͸� ����Ͽ� �������� ����
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				poiDataOverlay.showAllPOIdata(0);	//id���� 0���� ������ ��� �������̰� ǥ�õǰ� �ִ� ��ġ�� ������ �߽ɰ� Zoom�� �缳��
				
				
				/********************List�� �ݿ�*******************/
				mLandmarkArl.clear(); //���� �ִ��� �����
				//LogUtil.v("mLandmarkArr.length : "+ mLandmarkArr.length);
				for(int i=0;i<mLandmarkArr.length;i++){
					//�Ҽ����� �� �������ϴϱ�
					int distanceFromMe = (int)(mLandmarkArr[i].getDistance(myLocation));
					
					//��ġ ������ ���� ������ �ʾ��� �� �޽��� ���
					mLandmarkArl.add(mLandmarkArr[i].name + "\n"
							+ ((distanceFromMe==Constants.INT_NULL)?"ã����.. ��ø� ��ٷ���^o^":distanceFromMe + " m"));
				}
				mLandmarkAdp.notifyDataSetChanged();
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ Posting�� listview�� �ݿ��Ѵ� ************/
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
				//�ϴ��� android.location ��� NGeoPoint�� ����� �Ѵ�.
				myLocation = (NGeoPoint)msg.obj;
				
				//UpdateService�� onLocationChanged���� �Ʒ��� select���� �ߵ���Ű�� �ٸ� Activity�� ���޵� ����� �ִ�.
				LogUtil.v("select * from tLandmark");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
				
				//String str = myLocation.getLatitude() + "\n" + myLocation.getLongitude() + "\n";

				//�̰� ��������� Location ������ ������ ���� ��ġ�� ���� �ű� => ��ħ
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

		/************** �ڵ鷯 ��� ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/*************���� �ʱ�ȭ ����**************/
		MapContainer = (LinearLayout)findViewById(R.id.map);		//���̹� ������ �ֱ� ���� LinearLayout
		mMapView = new NMapView(this);		//���̹� ���� ��ü ����
		mMapController = mMapView.getMapController();		//���� ��ü�κ��� ��Ʈ�ѷ� ����
		mMapView.setApiKey(API_KEY);		//���̹� ���� ��ü�� APIŰ ����
		MapContainer.addView(mMapView);		//������ ���̹� ���� ��ü�� LinearLayout�� �߰���Ŵ
		mMapView.setClickable(true);		//������ ��ġ�� �� �ֵ��� �ɼ� Ȱ��ȭ
		mMapView.setBuiltInZoomControls(true, null);		//Ȯ��/��Ҹ� ���� �� ��Ʈ�ѷ� ǥ�� �ɼ� Ȱ��ȭ
		mMapView.setOnMapStateChangeListener(this);		//������ ���� ���� ���� �̺�Ʈ ����

		/************���� �ʱ�ȭ ��*******************/

		/****************��������************************/
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// �������� ���ҽ� ������ü �Ҵ�
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);		// �������� ������ �߰�
		
		//TODO: �̰� ����? �̰� ������ CalloutOverlayListener �۵� ���ϴ� �� ���⵵
		//poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  		
		mOverlayManager.setOnCalloutOverlayListener(this);		// �������� �̺�Ʈ ���
		
		
		//������� �������� ������ 130816 ������ �߰�
		mMapCompassManager = new NMapCompassManager(this);
		if(mMapLocationManager != null) {
			mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
				
			startMyLocation();
		} else {
			LogUtil.e("LocationManager is null!");
		}
		/******************�������� ��********************/


		/***********************���� �� ����Ʈ�� �ʱ�ȭ***************************/
		//���� ��û�ϱ�
		soapParser = SoapParser.getInstance(); 
		LogUtil.v("data request. select * from tLandmark");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select * from tLandmark", Constants.MSG_TYPE_LANDMARK));
		LogUtil.v("data request. select * from tPosting");
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData("select * from tPosting", Constants.MSG_TYPE_POSTING));
		
		//�� ���
		tabHost = (TabHost) findViewById(R.id.map_list_tabhost);
        lstLandmark = (ListView) findViewById(R.id.landmarkList);
        lstPosting = (ListView) findViewById(R.id.postingList);
		
        //�ʱ� listview ���� ����.
        mLandmarkArl = new ArrayList<String>();
        mLandmarkArl.add("Landmarks Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //listview�� �ƴ� layout�� ���� ����
        mLandmarkAdp = new ArrayAdapter<String>(this, R.layout.listview_item_landmark , mLandmarkArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl); 
		
        lstLandmark.setAdapter(mLandmarkAdp);
        lstLandmark.setOnItemClickListener(lstLandmarkItemClickListener);
        mLandmarkAdp.setNotifyOnChange(true); //�� �ɼ��� ������ ArrayList�� ������ �� �ڵ����� �ݿ��ȴ�. strArr��� ArrayList�� ��� �ϴ� ����
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //�� �ɼ��� ������ ArrayList�� ������ �� �ڵ����� �ݿ��ȴ�. strArr��� ArrayList�� ��� �ϴ� ����
        
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
	 * ������ �ʱ�ȭ �� �� ȣ���
	 * ���������� �ʱ�ȭ �Ǹ� errorInfo��ü�� null�� ���޵Ǹ�,
	 * �ʱ�ȭ ���� �� errorInfo��ü�� ���� ������ ���޵ȴ�.
	 */

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		//LogUtil.v("onMapInitHandler invoked!");
		if (errorInfo == null) { // success
			//�浵, ����, Ȯ�� ����
			//	mMapController.setMapCenter(new NGeoPoint(LonLatScan.getLon(),LonLatScan.getLat()), 12);
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	
	}

	//�������� ����� ȣ��Ǹ� ����� ���� ������ �Ķ���ͷ� ���޵�
	@Override
	public void onZoomLevelChange(NMapView mapview, int level) {
		//LogUtil.v("onZoomLevelChange invoked!");
		// TODO Auto-generated method stub
	}


	//���� �߽� ���� �� ȣ��Ǹ� ����� �߽� ��ǥ�� �Ķ���ͷ� ���޵ȴ�.
	@Override
	public void onMapCenterChange(NMapView mapview, NGeoPoint center) {
		//LogUtil.v("onMapCenterChange invoked!");
		// TODO Auto-generated method stub

	}


	//���� �ִϸ��̼� ���� ���� �� ȣ��ȴ�.
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

	/************ �������̰� Ŭ���Ǿ��� ���� �̺�Ʈ?? �ƴѵ� *************/
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
	/************ �������̰� Ŭ���Ǿ��� ���� �̺�Ʈ *************/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		//Toast.makeText(this, arg1.getTitle(), Toast.LENGTH_SHORT).show();
		LogUtil.v("onCreateCalloutOverlay invoked!");
//		mIntent = new Intent(this, LandmarkActivity.class);
//		startActivity(mIntent);
//		overridePendingTransition(0, 0); //no switching animation
//		
		return new NMapCalloutCustomOverlay(arg0, arg1, arg2, mMapViewerResourceProvider);   //�������̰� ���õ� ����� ������ ǥ�����ش�.
	}

//	/************* ������� 130816 ������ �ۼ� **********************/  
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
	
	/************ �׼ǹ� �� �޴� �ʱ�ȭ �� �̺�Ʈ ó�� *******************/
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
			finish(); //TODO: ������ ������� �ʰ� �ϰ� �;�... ��ε����ݾ� �Ф� 
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
	
	/******************************* ����Ʈ�� Ŭ���� *************************/
	private AdapterView.OnItemClickListener lstLandmarkItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: �� ��° ���� ��������.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mLandmarkArr[position].idx);
			//TODO: mLandmarkArr�� Listview�� �ö� ������ ��ġ�� ������Ѿ� �Ѵ�. ���� Ȯ�ε��� ����.
			
			//Intent�� �̿��Ͽ� LandmarkActivity�� ldmIdx�� �����Ѵ�.
			mIntent = new Intent(MapListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);
			startActivity(mIntent);
			
		}
	};
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position: �� ��° ���� ��������.0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArr�� Listview�� �ö� ������ ��ġ�� ������Ѿ� �Ѵ�. ���� Ȯ�ε��� ����.
			
			mIntent = new Intent(MapListActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};
	
}
