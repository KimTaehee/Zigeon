/**
 * Class Name: BestListActivity
 * Description: Show list top 5 and more. Main Activity
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130909
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;

import com.nhn.android.maps.maplib.NGeoPoint;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BestListActivity extends Activity implements OnClickListener {
	private Intent mIntent;
	private final int SELECT_LOCATION = 1001;	//request code

	private ActivityManager activityManager = ActivityManager.getInstance();
	
	private SoapParser soapParser;
	private NGeoPoint myLocation;	//location from LM
	private NGeoPoint detLocation;	//location for detecting
	private boolean isTraceLocation=true;	//mode for real-time trace location
	private int detectRange = 500;	//meter for search around
	
	private GridView grdBestList;
	private ToggleButton tglBtnTraceLocation;
	private Button btnRefreshLocation;
	private Button btnSetRange;
	
	private ArrayList<LandmarkDataset> mBestListArl;		//to set gridtview	
	private LandmarkAdapter mBestListAdp;		//user defined Adapter. to set gridview
	private LandmarkDataset mBestListArr[];
	
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //receiver from UpdateService
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mBestListArr =(LandmarkDataset[]) msg.obj;
				/****************** LandmarkDataset -> NMapPOIdataOverlay ***************/
				//LogUtil.v("LandmarkDataset -> NMapOverlay");

//				int markerId = NMapPOIflagType.PIN;		// create marker ID to show on overlay
//				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
//				poiData.beginPOIdata(0); //TODO: what is 0?
//				for(int i=0;i<mLandmarkArr.length;i++) {
//					//TODO: what is 0?
//					poiData.addPOIitem(mLandmarkArr[i].longitude, 
//							mLandmarkArr[i].latitude, mLandmarkArr[i].name, markerId, 0); 
//				}
//				poiData.endPOIdata();	
//
//				// create overlay with location data
//				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
//				poiDataOverlay.showAllPOIdata(0);	//set center and zoom which can express all overlay where id==0


				/******************** reflect on Grid*******************/
//				mBestListArl.clear(); //reset arraylist
//				//LogUtil.v("mLandmarkArr.length : "+ mLandmarkArr.length);
//				for(int i=0;i<mBestListArr.length;i++){
//					//double->int
//					int distanceFromMe = (int)(mBestListArr[i].getDistance(myLocation));
//
//					//init string
//					mBestListArl.add(mBestListArr[i]);
//					//(mBestListArr[i].name + "\n"
//					//		+ ((distanceFromMe==Constants.INT_NULL)?"finding.. ^o^":distanceFromMe + " m"));
//				}
				for(int i=0; i<mBestListArr.length; i++) {
					mBestListArr[i].getDistance(detLocation);	//calc LocationDataset.distanceFromCurrentLocation
				}
				LogUtil.i("mBestListArr.length: " + mBestListArr.length);
				mBestListAdp = new LandmarkAdapter(BestListActivity.this, mBestListArr);
				grdBestList.setAdapter(mBestListAdp);
				mBestListAdp.notifyDataSetChanged();	//TODO: is this work?
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
//				mPostingArr =(PostingDataset[]) msg.obj;
//
//				/************ reflect Posting on listview ************/
//				mPostingArl.clear();
//
//				//LogUtil.v("mPostingArr.length : "+ mPostingArr.length);
//				for(int i=0;i<mPostingArr.length;i++){
//					mPostingArl.add(mPostingArr[i].title);
//				}
//				mPostingAdp.notifyDataSetChanged();
//				//LogUtil.i("mPostingAdp.notifyDataSetChanged()");
//				break;
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
				if(isTraceLocation) {
					detLocation = myLocation;
				
					LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'");
					uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
							soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
								+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
								+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
				} 

				//WARN: cannot use this query on UpdateService.onLocationChanged().
				//WARN: It may cause to send to other Activity.
				

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
		setContentView(R.layout.activity_best_list);
		
		LogUtil.i("onCreate invoked!");
		
		/*******add activity list********/
		activityManager.addActivity(this);
		
		/************** register handler ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);

		//request contents
		soapParser = SoapParser.getInstance(); 
		
		detLocation = new NGeoPoint();
		
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		detLocation.set(Double.parseDouble(pref.getString("lon","127.0815700"))
				, Double.parseDouble(pref.getString("lat","37.6292700"))) ; //default value
		
		
		LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
						+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
						+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
		

		/********** init UI ************/
		grdBestList = (GridView) findViewById(R.id.best_list_gridview);
		btnSetRange = (Button) findViewById(R.id.best_list_btn_set_range);
		tglBtnTraceLocation = (ToggleButton) findViewById(R.id.best_list_tglbtn_trace_location);
		btnRefreshLocation = (Button) findViewById(R.id.best_list_btn_refresh_location);
		btnSetRange.setOnClickListener(this);
		tglBtnTraceLocation.setOnClickListener(this);
		tglBtnTraceLocation.setChecked(true);
		isTraceLocation = true;
		btnRefreshLocation.setOnClickListener(this);

		//first gridview string 
		//mBestListArl = new ArrayList<LandmarkDataset>();
		//mBestListArl.add("Landmarks Loading...");
		
		mBestListAdp = new LandmarkAdapter(this, mBestListArr); 
		grdBestList.setAdapter(mBestListAdp);
		grdBestList.setOnItemClickListener(grdBestListItemClickListener);
		//mBestListAdp.setNotifyOnChange(true); //this can detect modify on ArrayList. SHOULD use ArrayList, not strArr

	}
	
	@Override
	public void onDestroy () {
		super.onDestroy();
		LogUtil.v("onDestroy called. finish()");
		
		/*********remove activity list******/
		activityManager.removeActivity(this);
		
		finish();
	}
	
	/************ actionbar & menu init, event processing  *******************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.best_list, menu);
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
		case R.id.my_profile:
		{
			startActivity(new Intent(this,UserProfileActivity.class));
			overridePendingTransition(0, 0); //no switching animation
			break;		
		}
		case R.id.preference:
		{
			startActivity(new Intent(this,PreferenceActivity.class));
			overridePendingTransition(0, 0); //no switching animation
			break;		
		}
		}
		return true;
	}
	
	/******************************* when gridview clicked *************************/
	private AdapterView.OnItemClickListener grdBestListItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position. 0~n
			LogUtil.v("onItemClick invoked!! item: " + mBestListArr[position].name);
			LogUtil.v("position: "+position + ", ldmIdx: " + mBestListArr[position].idx);
			//TODO: SHOULD match mLandmarkArr contents == Listview contents. need to test 
			
			//send ldmIdx to LandmarkActivity using Intent
			mIntent = new Intent(BestListActivity.this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx",mBestListArr[position].idx);
			startActivity(mIntent);
			
		}
	};

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.best_list_tglbtn_trace_location:
		{
			LogUtil.v("tglbtn_trace_location clicked! current state: " + tglBtnTraceLocation.isChecked());
		
			if(!tglBtnTraceLocation.isChecked()) { //previous state is trace on, let mode be turned off 
				isTraceLocation = false;
				
				//and get location manually
				mIntent = new Intent(this,MapActivity.class);
				
				mIntent.putExtra("lon", detLocation.longitude);
				mIntent.putExtra("lat", detLocation.latitude);
				startActivityForResult(mIntent, SELECT_LOCATION);
			} else { //turn on trace location mode
				isTraceLocation = true;
				
			}
			break;
		}
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch(requestCode) 
			{
			case SELECT_LOCATION:
			{
				Bundle bundle = data.getExtras();
				if (detLocation==null) {
					detLocation = new NGeoPoint();
				}
				detLocation.longitude = Double.parseDouble(bundle.getString("lon"));
				detLocation.latitude = Double.parseDouble(bundle.getString("lat"));
				LogUtil.v("location from MapActivity: " + detLocation.longitude + ", " + detLocation.latitude);
				
				LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
						+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
						+ "') WHERE ldmVisible = 'True'");
				//use manual sendMessage! cause of finish() timing on MapActivity is delayed.
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK), messageHandler);
				LogUtil.i("where am i ?"); //TODO: need to kill MapActivity
				break;
			}
			}	
		}
		
	}


}
