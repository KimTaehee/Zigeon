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
import java.util.Arrays;

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
<<<<<<< HEAD
import android.view.LayoutInflater;
=======
>>>>>>> progress dialog and back key event
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BestListActivity extends Activity implements OnClickListener, OnScrollListener {
	private Intent mIntent;
	private final int SELECT_LOCATION = 1001;	//request code

	private ActivityManager activityManager = ActivityManager.getInstance();

	private SoapParser soapParser;

	private NGeoPoint myLocation;	//location from LM
	private NGeoPoint detLocation;	//location for detecting
	private boolean isTraceLocation=true;	//mode for real-time trace location

	private int detectRange = 500;	//meter for search around

	private ProgressDialog loadingDialog;
	
	private ProgressDialog loadingDialog;
	
	private GridView grdBestList;
	private ToggleButton tglBtnTraceLocation;
	private Button btnRefreshLocation;
	private TextView tvSetRange;

	private ArrayList<LandmarkDataset> mBestListArl;		//to set gridview	
	private LandmarkAdapter mBestListAdp;		//user defined Adapter. to set gridview
	private LandmarkDataset mBestListArr[];
	private int mBestListRangeOffset;	//search range. if value = 20 then search 21~40. if you want new, assign 0.
	private boolean mLockGridView;

	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //receiver 
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				mBestListArr = (LandmarkDataset[]) msg.obj;

				//calc LocationDataset.distanceFromCurrentLocation
				for(int i=0; i<mBestListArr.length; i++) {
					mBestListArr[i].getDistance(detLocation);	
				}
				if(mBestListArr.length == 0) { //if there is no more Landmark, stop calling load more.
					mLockGridView = true;
				} else {
					mLockGridView = false;
				}

				/******************** reflect on Grid*******************/
				LogUtil.i("mBestListArr.length: " + mBestListArr.length 
						+ ", mBestListArl.size: " + mBestListArl.size() 
						+ ", mBestListRangeOffset: " + mBestListRangeOffset);
				//add to arraylist
				if(mBestListRangeOffset==0) { //if new List
					mBestListArl = new ArrayList<LandmarkDataset>(Arrays.asList(mBestListArr));
					mBestListAdp = new LandmarkAdapter(BestListActivity.this, mBestListArl);
					grdBestList.setAdapter(mBestListAdp);
				} else {
					mBestListArl.addAll(Arrays.asList(mBestListArr));
					mBestListAdp.updateLandmarkList(mBestListArl);
				}
				mBestListRangeOffset += mBestListArr.length;

				if(loadingDialog!=null) {
					loadingDialog.dismiss();
					LogUtil.i("dismiss loadingDialog!!");
				}
<<<<<<< HEAD

=======
				LogUtil.i("mBestListArr.length: " + mBestListArr.length);
				mBestListAdp = new LandmarkAdapter(BestListActivity.this, mBestListArr);
				grdBestList.setAdapter(mBestListAdp);
				mBestListAdp.notifyDataSetChanged();	//TODO: is this work?
				//LogUtil.i("mLandmarkAdp.notifyDataSetChanged()");
				
				if(loadingDialog!=null) {
					loadingDialog.dismiss();
					LogUtil.i("dismiss loadingDialog!!");
				}
				
>>>>>>> progress dialog and back key event
				break;
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
			case Constants.MSG_TYPE_LOCATION:
			{
				//use NGeoPoint instead of android.location 

				myLocation = (NGeoPoint)msg.obj;
				if(isTraceLocation) {
					detLocation = myLocation;

					mBestListRangeOffset = 0;
					LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True'");
					uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
							soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
									+ detLocation.getLongitude() + "','" + detLocation.getLatitude() + "','" + detectRange
									+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK));
				} 
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

		soapParser = SoapParser.getInstance(); 

		//set default location
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

		//data request
		mBestListRangeOffset = 0;
		mBestListArl = new ArrayList<LandmarkDataset>();
		
		LogUtil.v("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
				+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
				+ "') WHERE ldmVisible = 'True'");
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData("select TOP 20 * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
						+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
						+ "') WHERE ldmVisible = 'True'", Constants.MSG_TYPE_LANDMARK),messageHandler);


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

		mLockGridView = false;
		mBestListAdp = new LandmarkAdapter(this, mBestListArl);
		grdBestList.setAdapter(mBestListAdp);
		grdBestList.setOnItemClickListener(grdBestListItemClickListener);
		grdBestList.setOnScrollListener(this);
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
			LogUtil.v("onItemClick invoked!! item: " + mBestListArl.get(position).name);
			LogUtil.v("position: "+position + ", ldmIdx: " + mBestListArl.get(position).idx);
			//TODO: SHOULD match mLandmarkArr contents == Listview contents. need to test 

			//send ldmIdx to LandmarkActivity using Intent
			mIntent = new Intent(BestListActivity.this, LandmarkActivity.class);
<<<<<<< HEAD

			mIntent.putExtra("ldmIdx",mBestListArl.get(position).idx);

=======
			mIntent.putExtra("ldmIdx",mBestListArr[position].idx);
			
			//LogUtil.i("start LandmarkActivity!");
>>>>>>> progress dialog and back key event
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
			mBestListRangeOffset = 0; //new list
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

<<<<<<< HEAD
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		//LogUtil.i("invoked!");

		int count = totalItemCount - visibleItemCount;
		if(firstVisibleItem >= count && totalItemCount != 0 && mLockGridView == false) {
			mLockGridView = true;
			LogUtil.i("load next item");
			
			uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
					soapParser.getSoapData("(select TOP " + (mBestListRangeOffset+20) 
							+ " * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True') except (select TOP " + mBestListRangeOffset 
							+ " * from UFN_WGS84_LANDMARK_DETECT_RANGE('" 
							+ myLocation.getLongitude() + "','" + myLocation.getLatitude() + "','" + detectRange
							+ "') WHERE ldmVisible = 'True')", Constants.MSG_TYPE_LANDMARK),messageHandler);
			
//			Handler handler = new Handler();
//			handler.po*
			
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	/*
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // ï¿½ï¿½ ï¿½ï¿½Æ°
			Toast.makeText(this, "BackÅ°ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½ï¿½Ì±ï¿½ï¿½ï¿½", Toast.LENGTH_SHORT).show();
=======
	/*
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // ¹é ¹öÆ°
			Toast.makeText(this, "BackÅ°¸¦ ´©¸£¼Ì±º¿ä", Toast.LENGTH_SHORT).show();
>>>>>>> progress dialog and back key event
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
