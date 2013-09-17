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
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;

import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.nmaps.NMapViewerResourceProvider;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.LogUtil;
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

public class LandmarkWriteActivity extends Activity implements OnClickListener{
	private ActivityManager activityManager = ActivityManager.getInstance();
	EditText edtTitle;
	EditText edtContents;
	ImageView imgInput;
	RelativeLayout mapContainer;
	InputMethodManager imm; 
//	public static final String API_KEY="3aa5ca39d123f5448faff118a4fd9528";	//API-KEY
//
//	private NMapView mMapView = null;	//Naver map object
//
//	private NMapController mMapController = null;	// map controller
//	private RelativeLayout MapContainer;	//map on layout
//	private NMapViewerResourceProvider mMapViewerResourceProvider = null;	 //Overlay Resource Provider
//	private NMapOverlayManager mOverlayManager = null;

	SoapParser soapParser;

	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
	private final int SELECT_PICTURE = 1000;
	private final int SELECT_LOCATION = 1001;
	private String selectedImagePath;
	private String fileManagerString;
	
	private NGeoPoint myLocation;
	private Intent mIntent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(kr.re.ec.zigeon.R.layout.activity_landmark_write);
		LogUtil.v("onCreate invoked!");
		
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		
		myLocation = new NGeoPoint();
		myLocation.set(Double.parseDouble(pref.getString("lon","127.0815700"))
				, Double.parseDouble(pref.getString("lat","37.6292700"))) ; //default value
		

		/*******add activity list********/
		activityManager.addActivity(this);


		/******** Init UI ********/
		edtTitle = (EditText) findViewById(R.id.landmark_write_edit_title);
		edtContents = (EditText) findViewById(R.id.landmark_write_edit_uniqueness);
		mapContainer = (RelativeLayout) findViewById(R.id.landmark_write_map);
		imgInput = (ImageView) findViewById(R.id.landmark_write_img_input);
		imgInput.setOnClickListener(this);

//		/************* map init **************/
//		LogUtil.v("map init start");
//		MapContainer = (RelativeLayout)findViewById(R.id.mapmap);		// Layout for show map
//		mMapView = new NMapView(this);		//create map object
//		mMapController = mMapView.getMapController();		//extract controller from map object
//		mMapView.setApiKey(API_KEY);		
//		MapContainer.addView(mMapView);		//map->layout
//		mMapView.setClickable(true);		//can click map
//		mMapView.setBuiltInZoomControls(true, null);		//zoom controller for +/- enable
//		
//		
//		/**************** overlay init ************************/
//		LogUtil.v("overlay init start");
//		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);		// create overlay resource provider
//		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);	//add overlay manager
		
//		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.showSoftInput(edtTitle, imm.SHOW_FORCED);

		/******** Init Handler *******/
		soapParser = SoapParser.getInstance();

		mapContainer.setOnClickListener(this);
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
			//TODO: here is error part. need to fix 
			Intent intent = new Intent(this, LandmarkActivity.class); 
			startActivity(intent);
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
				Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				imgInput.setImageBitmap(bitmap);

				break;
			}
			case SELECT_LOCATION:
			{
				Bundle bundle = data.getExtras();
				myLocation.longitude = Double.parseDouble(bundle.getString("lon"));
				myLocation.latitude = Double.parseDouble(bundle.getString("lat"));
				LogUtil.v("location from MapActivity: " + myLocation.longitude + ", " + myLocation.latitude);
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
}
