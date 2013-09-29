package kr.re.ec.zigeon;

import java.io.File;

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
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.dataset.PhotoUploadDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.nmaps.NMapPOIflagType;
import kr.re.ec.zigeon.nmaps.NMapViewerResourceProvider;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import kr.re.ec.zigeon.util.PhotoUploader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LandmarkWriteActivity extends NMapActivity implements OnClickListener
	, OnMapStateChangeListener, OnCalloutOverlayListener {
	private ActivityManager activityManager = ActivityManager.getInstance();
	private EditText edtTitle;
	private EditText edtContents;
	private ImageView imgInput;
	private RelativeLayout layoutMap;	//map on layout
	private InputMethodManager imm; 

	private SoapParser soapParser;

	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
	private final int SELECT_PICTURE = 1000;
	private final int SELECT_LOCATION = 1001;
	private String selectedImagePath;
	private String fileManagerString;
	
	private NGeoPoint myLocation;
	private Intent mIntent;
	private boolean isLocationSelected;
	
	public static final String API_KEY = Constants.NMAP_API_KEY;	//API-KEY
	private NMapView mMapView = null;	//Naver map object

	private NMapController mMapController = null;	// map controller
	private NMapMyLocationOverlay mMyLocationOverlay; 
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	 //Overlay Resource Provider
	private NMapOverlayManager mOverlayManager = null;	
	private NMapLocationManager mMapLocationManager; //TODO: test
	private NMapCompassManager mMapCompassManager; 

	/******** AUIL init ********/
	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_auil_stub)	
	.showImageForEmptyUri(R.drawable.ic_auil_empty)
	.showImageOnFail(R.drawable.ic_auil_error)
	.build();
	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(kr.re.ec.zigeon.R.layout.activity_landmark_write);
		LogUtil.v("onCreate invoked!");
		
		/********* get default location from sharedpref ***********/
		isLocationSelected = false;
		
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		
		myLocation = new NGeoPoint();
		myLocation.set(Double.parseDouble(pref.getString("lon",String.valueOf(Constants.NMAP_DEFAULT_LON)))
				, Double.parseDouble(pref.getString("lat",String.valueOf(Constants.NMAP_DEFAULT_LAT)))) ; //default value
		

		/*******add activity list********/
		activityManager.addActivity(this);

		/******** Init UI ********/
		edtTitle = (EditText) findViewById(R.id.landmark_write_edit_title);
		edtContents = (EditText) findViewById(R.id.landmark_write_edit_uniqueness);
		layoutMap = (RelativeLayout) findViewById(R.id.landmark_write_map);
		imgInput = (ImageView) findViewById(R.id.landmark_write_img_input);
		imgInput.setOnClickListener(this);
		layoutMap.setOnClickListener(this);

		/************* map init **************/
		LogUtil.v("map init start");
		layoutMap = (RelativeLayout)findViewById(R.id.landmark_write_map);		// Layout for show map
		mMapView = new NMapView(this);		//create map object
		mMapController = mMapView.getMapController();		//extract controller from map object
		mMapView.setApiKey(API_KEY);		
		layoutMap.addView(mMapView);		//map->layout
		//mMapView.setClickable(true);		//can click map
		mMapView.setBuiltInZoomControls(false, null);		//zoom controller for +/- enable
		mMapLocationManager = new NMapLocationManager(this);
		mMapController.setMapCenter(myLocation);
		mMapController.setZoomLevel(12);
		
		/**************** overlay init ************************/
		LogUtil.v("overlay init start");
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// create overlay resource provider
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);	//add overlay manager
		

		/******** Init Handler *******/
		soapParser = SoapParser.getInstance();

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landmark_write, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.landmark_write_action_write:
		{
			LogUtil.v("action_write_landmark clicked");
			
			LandmarkDataset ldm = new LandmarkDataset();
			MemberDataset mem = MemberDataset.getLoginInstance();
			//String strArr[] = new String[Constants.DATASET_FIELD[Constants.MSG_TYPE_POSTING].length];
			//strArr[0] = 
			LogUtil.v("create ldmDataset and get memDataset success!");
			
			//landmark's name
			if(edtTitle.getText().toString().compareTo("")==0) {
				new AlertManager().show(this,"Blank Title? ^^","Confirm",Constants.ALERT_OK_ONLY);
				return false;
			} else {
				ldm.name = edtTitle.getText().toString();
			}

			//location
			if(isLocationSelected) {
				ldm.latitude = myLocation.latitude;
				ldm.longitude = myLocation.longitude;
			} else {
				new AlertManager().show(this,"Select Location ^^","Confirm",Constants.ALERT_OK_ONLY);
				return false;
			}
			
			//contents
			if(edtContents.getText().toString().compareTo("")==0) {
				new AlertManager().show(this,"Blank Contents? ^^","Confirm",Constants.ALERT_OK_ONLY);
				return false;
			} else {
				ldm.contents = edtContents.getText().toString();
			}

			ldm.writerIdx = mem.idx;
			LogUtil.v("memidx: " + mem.idx);

			if(selectedImagePath==null) { 
				ldm.picturePath = null;
			} else {
				//save only filename(not dir. ex: gootmorning.jpg)
				ldm.picturePath = selectedImagePath.substring(selectedImagePath.lastIndexOf("/")+1);
				LogUtil.v("pst.picturePath: " + ldm.picturePath);
			}
			LogUtil.v("data input to ldm success");

			ldm.idx = soapParser.insertDatasetUsingQuery(Constants.MSG_TYPE_LANDMARK, ldm);

			//upload photo
			new PhotoUploader().execute(new PhotoUploadDataset(Constants.MSG_TYPE_LANDMARK,ldm.idx,selectedImagePath));

			mIntent = new Intent(this, LandmarkActivity.class);
			mIntent.putExtra("ldmIdx", ldm.idx);
			startActivity(mIntent);
			
			finish();
			
			break;
		}
//		case R.id.my_profile:
//		{
//			startActivity(new Intent(this,UserProfileActivity.class));
//			overridePendingTransition(0, 0); //no switching animation
//			break;		
//		}
//		case R.id.preference:
//		{
//			startActivity(new Intent(this,PreferenceActivity.class));
//			overridePendingTransition(0, 0); //no switching animation
//			break;		
//		}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.landmark_write_img_input:
		{
			LogUtil.v("img_input clicked.");

			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent,
					"Select Picture"), SELECT_PICTURE);

			break;
		}
		case R.id.landmark_write_map:
		{
			//hide keyboard
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtTitle.getWindowToken(), 0);
			
			mIntent = new Intent(this,MapActivity.class);
			
			mIntent.putExtra("lon", myLocation.longitude);
			mIntent.putExtra("lat", myLocation.latitude);
			startActivityForResult(mIntent, SELECT_LOCATION);
			
			//overridePendingTransition(0, 0); //no switching animation

			break;
		}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			
			switch(requestCode)
			{
			case SELECT_PICTURE:
			{
				Uri selectedImageUri = data.getData();

				//OI FILE Manager
				fileManagerString = selectedImageUri.getPath();

				//MEDIA GALLERY
				selectedImagePath = getPath(selectedImageUri);

				//DEBUG PURPOSE - you can delete this if you want
				if(selectedImagePath!=null) {
					LogUtil.v("selectedImagePath: " + selectedImagePath);
				} else {
					LogUtil.v("selectedImagePath is null");
				}
				if(fileManagerString!=null) {
					LogUtil.v("fileManagerString: " + fileManagerString);
				} else {
					LogUtil.v("filemanagerstring is null");
				}

				//NOW WE HAVE OUR WANTED STRING
				if(selectedImagePath!=null) {
					LogUtil.v("selectedImagePath is the right one for you!");
				} else {
					LogUtil.v("filemanagerstring is the right one for you!");
				}

				//path to imageview
				File imgFile = new File(selectedImagePath);
				
				//Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				String uri = "file://" + imgFile.getAbsolutePath();
				LogUtil.v("uri: " + uri);
				imgLoader.displayImage(uri, imgInput, imgOption);

				break;
			}
			case SELECT_LOCATION:
			{
				Bundle bundle = data.getExtras();
				myLocation.longitude = Double.parseDouble(bundle.getString("lon"));
				myLocation.latitude = Double.parseDouble(bundle.getString("lat"));
				LogUtil.v("location from MapActivity: " + myLocation.longitude + ", " + myLocation.latitude);
				
				mMapController.setMapCenter(myLocation);
				int markerId = NMapPOIflagType.PIN;		// create marker ID to show on overlay
				NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
				poiData.beginPOIdata(0); //TODO: what is 0?
				poiData.addPOIitem(myLocation.longitude, myLocation.latitude, "User Choosed", markerId, 0); 
				poiData.endPOIdata();
				
				// create overlay with location data
				mOverlayManager.clearOverlays();
				NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
				
				isLocationSelected = true;
				break;
			}
			}
			
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		/*********remove activity list******/
		activityManager.removeActivity(this);
	}
	
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null); 	//deprecated func used!
		if(cursor!=null)
		{
			//HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			//THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		else return null;
	}
	
	/**
	 * called after map init. 
	 * when no error, =>null
	 * else errorInfo => cause 
	 */
	@Override
	public void onMapInitHandler(NMapView arg0, NMapError errorInfo) {
		LogUtil.v("onMapInitHandler invoked! myLocation is: " + myLocation.longitude + ", " + myLocation.latitude);
		if (errorInfo == null) { // success
			//lon, lat, zoom level
			mMapController.setMapCenter(myLocation, 12); 	//TODO: test phrase
		} else { // fail
			LogUtil.e("onMapInitHandler: error=" + errorInfo.toString());
		}	

	}

	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		return null;
	}

	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		
	}

	@Override
	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
		
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		
	}

	@Override
	public void onZoomLevelChange(NMapView arg0, int arg1) {
		
	}


	
}
