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
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
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
import android.widget.Toast;
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
	
	private ProgressDialog loadingDialog;
	
	private GridView grdBestList;
	private ToggleButton tglBtnTraceLocation;
	private Button btnRefreshLocation;
	private TextView tvSetRange;
		
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
				
				/******************** reflect on Grid*******************/
				for(int i=0; i<mBestListArr.length; i++) {
					mBestListArr[i].getDistance(detLocation);	//calc LocationDataset.distanceFromCurrentLocation
				}
				LogUtil.i("mBestListArr.length: " + mBestListArr.length);
				mBestListAdp = new LandmarkAdapter(BestListActivity.this, mBestListArr);
				grdBestList.setAdapter(mBestListAdp);
				mBestListAdp.notifyDataSetChanged();	//TODO: is this work?
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				
				if(loadingDialog!=null) {
					loadingDialog.dismiss();
					LogUtil.i("dismiss loadingDialog!!");
				}
				
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
			case Constants.MSG_TYPE_REFRESH:
			{
				LogUtil.v("refresh: select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
						+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
						+ "') WHERE ldmVisible = 'True'");
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK),this);
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

//				LogUtil.v("select TOP 20 * from tLandmark WHERE ldmVisible = 'True'");
//				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
//						soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
//							+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
//							+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
				break;
			}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadingDialog = ProgressDialog.show(this, "Connecting", "Loading. Please wait...", true, false);
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
		detLocation.set(Double.parseDouble(pref.getString("lon",String.valueOf(Constants.NMAP_DEFAULT_LON)))
				, Double.parseDouble(pref.getString("lat",String.valueOf(Constants.NMAP_DEFAULT_LAT)))) ; //default value
		
		//search nearby location
//		LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
//							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
//							+ "') WHERE ldmVisible = 'True'");
//		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
//				soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
//						+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
//						+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));

//		LogUtil.v("data request. select TOP 20 * from tLandmark WHERE ldmVisible = 'True'");
//		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
//				soapParser.getSoapData("select TOP 20 * from tLandmark WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));

/*		LogUtil.v("data request. select TOP 20 * from tLandmark WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 20 * from tLandmark WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
	*/	

		myLocation = new NGeoPoint();
		
		myLocation.set(Double.parseDouble(pref.getString("lon",String.valueOf(Constants.NMAP_DEFAULT_LON)))
				, Double.parseDouble(pref.getString("lat",String.valueOf(Constants.NMAP_DEFAULT_LAT)))) ; //default value
				
		LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
						+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
						+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));


		/********** init UI ************/
		grdBestList = (GridView) findViewById(R.id.best_list_gridview);
		tvSetRange = (TextView) findViewById(R.id.best_list_btn_set_range);
		tglBtnTraceLocation = (ToggleButton) findViewById(R.id.best_list_tglbtn_trace_location);
		btnRefreshLocation = (Button) findViewById(R.id.best_list_btn_refresh_location);
		//btnSetRange.setOnClickListener(this);
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
	
	@Override
	public void onResume() {
		super.onResume();
		uiHandler.sendMessage(Constants.MSG_TYPE_REFRESH, "",null,messageHandler);
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
		case R.id.map_list_action_landmark_write:
		{
			mIntent = new Intent(this, LandmarkWriteActivity.class);
			mIntent.putExtra(Constants.INTENT_TYPE_NAME_EDIT, false); //new landmark
			startActivity(mIntent);
			
			//overridePendingTransition(0, 0); //no switching animation
			break;			
		}
//		case R.id.my_profile:
//		{
//			startActivity(new Intent(this,UserProfileActivity.class));
//			overridePendingTransition(0, 0); //no switching animation
//			break;		
//		}
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
			
			//LogUtil.i("start LandmarkActivity!");
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
				uiHandler.sendMessage(Constants.MSG_TYPE_LOCATION, "", 
						MapActivity.mMapLocationManager.getMyLocation());
			}
			break;
		}
		case R.id.best_list_btn_refresh_location:
		{
			LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
					+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
					+ "') WHERE ldmVisible = 'True'");
			uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
					soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
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
				//use manual sendMessage!!!! cause of finish() timing on MapActivity is delayed.
				uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
						soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK), messageHandler);
				//LogUtil.i("where am i ?"); //TODO: need to kill MapActivity
				break;
			}
			}	
		}
		
	}

	/*
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // 백 버튼
			Toast.makeText(this, "Back키를 누르셨군요", Toast.LENGTH_SHORT).show();
			DialogInterface.OnClickListener dialogListner = new DialogInterface.OnClickListener() { //click yes

				@Override
				public void onClick(DialogInterface dialog, int which) {

					System.exit(0);
					finish();
					
					
				}
			};
			new AlertManager().show(this, "Delete Landmark. Continue?", "Confirm"
					, Constants.ALERT_YES_NO, dialogListner);
		}
		return true;
	}
	*/
	
        // Back key status
        boolean m_close_flag = false;
   
        // status initialization
        Handler m_close_handler = new Handler() {
            public void handleMessage(Message msg) {
                m_close_flag = false;
            }
        };
 

        // Back key thouch
        public void onBackPressed () 
        {
            if(m_close_flag == false) { // Back key first
 
                Toast.makeText(this, "Press the Cancel key again will end.", Toast.LENGTH_LONG).show();
 
                m_close_flag = true;
 
                m_close_handler.sendEmptyMessageDelayed(0, 3000);
 
            } else { // Back key press  in 3 sec
                super.onBackPressed();
            }
        }
 
        protected void onStop()
        {
            super.onStop();
    
            // remove '0'message in handler
            m_close_handler.removeMessages(0);
        }
    

}
