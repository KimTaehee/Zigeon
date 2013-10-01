/**
 * Class Name: PostingWriteActivity
 * Description: Write Posting. Can attach one picture. 
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130828
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import java.io.File;

import javax.crypto.spec.IvParameterSpec;

import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import kr.re.ec.zigeon.dataset.PhotoUploadDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.nmaps.NMapPOIflagType;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import kr.re.ec.zigeon.util.PhotoUploader;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class PostingWriteActivity extends Activity implements OnClickListener {
	private ActivityManager activityManager = ActivityManager.getInstance();
	private EditText edtTitle;
	private EditText edtContents;
	private ImageView imgInput;

	private int mLdmIdx;
	private int mPstIdxOnEdit;
	private boolean mIsNewPosting;
	private boolean mIsImageChangedOnEdit;
	private Intent mIntent;
	private PostingDataset[] mPostingArr;
	private PostingDataset mPostingData;

	private SoapParser soapParser;
	private UIHandler uiHandler;

	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
	private final int SELECT_PICTURE = 1;
	private String selectedImagePath;
	private String fileManagerString;

	/******** AUIL init ********/
	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_auil_stub)	
	.showImageForEmptyUri(R.drawable.ic_auil_empty)
	.showImageOnFail(R.drawable.ic_auil_error)
	.build();
	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton

	private Handler messageHandler = new Handler() { //receiver from UpdateService. important!
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_POSTING:
			{
				LogUtil.v("load posting data start");
				mPostingArr =(PostingDataset[]) msg.obj;
				mPostingData = mPostingArr[0];
				
				edtTitle.setText(mPostingData.title);
				edtContents.setText(mPostingData.contents);
				imgLoader.displayImage(mPostingData.getImageUrl(), imgInput, imgOption);
				break;
			}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting_write);
		LogUtil.v("onCreate invoked!");

		/*******add activity list********/
		activityManager.addActivity(this);

		/************ get landmark's index *************/
		Bundle bundle = this.getIntent().getExtras();
		mLdmIdx = bundle.getInt("ldmIdx");

		/******** get this activity type : new or edit *********/
		mIsImageChangedOnEdit = false;
		mIsNewPosting = !(bundle.getBoolean(Constants.INTENT_TYPE_NAME_EDIT));
		if(!mIsNewPosting) {
			mPstIdxOnEdit = bundle.getInt("pstIdx");
		}

		/******** Init UI ********/
		edtTitle = (EditText) findViewById(R.id.posting_write_edt_title);
		edtContents = (EditText) findViewById(R.id.posting_write_edt_contents);
		imgInput = (ImageView) findViewById(R.id.posting_write_img_input);
		imgInput.setOnClickListener(this);


		/******** Init Handler *******/
		uiHandler = UIHandler.getInstance(this);
		
		soapParser = SoapParser.getInstance();
		
		if(!mIsNewPosting) {
			String query = "SELECT * FROM tPosting WHERE pstIdx='" + mPstIdxOnEdit + "'"; 
			LogUtil.v("data request. " + query);
			uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
					soapParser.getSoapData(query, Constants.MSG_TYPE_POSTING),messageHandler);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.posting_write, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.posting_write_action_write:
		{
			LogUtil.v("action_write clicked.");

			PostingDataset pst = new PostingDataset();
			MemberDataset loginMem = MemberDataset.getLoginInstance();
			//String strArr[] = new String[Constants.DATASET_FIELD[Constants.MSG_TYPE_POSTING].length];
			//strArr[0] = 
			LogUtil.v("create pstDataset and get memDataset success!");
			//title
			if(edtTitle.getText().toString().compareTo("")==0) {
				new AlertManager().show(this,"Blank Title? ^^","Confirm",Constants.ALERT_OK_ONLY);
				return false;
			} else {
				pst.title = edtTitle.getText().toString();
			}

			//parentIdx
			pst.parentIdx = mLdmIdx;

			//contents
			if(edtContents.getText().toString().compareTo("")==0) {
				new AlertManager().show(this,"Blank Contents? ^^","Confirm",Constants.ALERT_OK_ONLY);
				return false;
			} else {
				pst.contents = edtContents.getText().toString();
			}

			pst.writerIdx = loginMem.idx;
			LogUtil.v("loginmemidx: " + loginMem.idx);
			
			if(selectedImagePath==null) { 
				pst.picturePath = null;
			} else {
				//save only filename(not dir. ex: gootmorning.jpg)
				pst.picturePath = selectedImagePath.substring(selectedImagePath.lastIndexOf("/")+1);
				LogUtil.v("pst.picturePath: " + pst.picturePath);
			}
			LogUtil.v("data input to pst success");

			if(mIsNewPosting) {
				pst.readedCount = 0;
				pst.idx = soapParser.insertDatasetUsingQuery(Constants.MSG_TYPE_POSTING, pst);
			} else {
				pst.idx = mPstIdxOnEdit;
				pst.idx = soapParser.updateDatasetUsingQuery(Constants.MSG_TYPE_POSTING, pst);
			}
				
			//upload photo
			new PhotoUploader().execute(new PhotoUploadDataset(Constants.MSG_TYPE_POSTING,pst.idx,selectedImagePath));
			//TODO: if upload photo failed, do exception

			mIntent = new Intent(this, PostingActivity.class);
			mIntent.putExtra("pstIdx",pst.idx);
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
		case R.id.posting_write_img_input:
		{
			LogUtil.v("img_input clicked.");

			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent,
					"Select Picture"), SELECT_PICTURE);

			break;
		}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();

				//FILE Manager
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

				mIsImageChangedOnEdit = true;
				
				//			bitmap = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter)
				//			imgInput.setImageBitmap(bitmap);
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
		Cursor cursor = managedQuery(uri, projection, null, null, null); 	//TODO: deprecated func used!
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