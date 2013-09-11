/*
 * author 130816 newcho 
 * modified 130831 KimTaehee (ldmIdx -> PostingWrite)
 * 
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.handler.UpdateService;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

//TODO: DO NOT USE deprecated Class or function
public class LandmarkActivity extends Activity implements OnClickListener, ImageLoadingListener {
	private TabHost tabHost;
	private ListView lstComment;
	private ListView lstPosting;
	private ImageButton ibtUploadPhoto;
	private EditText edtInputComment;
	private Button btnInputComment;
	private TextView tvName;
	private TextView tvContents;
	private ImageView imgLandmarkPicture;
	private ArrayList<String> mCommentArl;		//to set listview 
	private ArrayList<String> mPostingArl;		//to set listview 
	private ArrayAdapter<String> mCommentAdp;		//to set listview 
	private ArrayAdapter<String> mPostingAdp;		//to set listview 
	private LandmarkDataset mLandmarkDataset;		
	private CommentDataset mCommentArr[];
	private PostingDataset mPostingArr[];

	private Intent mIntent;

	private SoapParser soapParser;
	private UIHandler uiHandler;

	//private final String sampleImgUri = "tLandmark_image/hanhyojoo_hq.jpg";

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
			case Constants.MSG_TYPE_LANDMARK:
			{
				LandmarkDataset[] landmarkDataArr = (LandmarkDataset[]) msg.obj; //selected by PK. so Arr.length==1
				mLandmarkDataset = landmarkDataArr[0];

				/******************** print info *******************/
				tvName.setText(mLandmarkDataset.name);
				tvContents.setText(mLandmarkDataset.contents);
				LogUtil.v("image load start! uri: " + mLandmarkDataset.getImageUrl());
				imgLoader.loadImage(mLandmarkDataset.getImageUrl(), LandmarkActivity.this); //load landmark image

				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;

				/************ Posting to listview ************/
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
				mCommentArr =(CommentDataset[]) msg.obj;


				/************ Comment to listview ************/

				mCommentArl.clear();

				//LogUtil.v("mCommentArr.length : "+ mCommentArr.length);
				for(int i=0;i<mCommentArr.length;i++){
					mCommentArl.add(mCommentArr[i].contents);
				}
				mCommentAdp.notifyDataSetChanged();
				lstComment.smoothScrollToPosition(mCommentArr.length-1);
				//LogUtil.i("mCommentAdp.notifyDataSetChanged()");
				break;
			}
			case Constants.MSG_TYPE_MEMBER:
			{
				//tvPostingTest.setText(msg.getData().getString("msg"));
				break;
			}
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landmark);  

        /************** register handler ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);

		/****** Data init request *****/
		//intent receive. mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx); remind !
		Bundle bundle = this.getIntent().getExtras();
		mLandmarkDataset = new LandmarkDataset();
		mLandmarkDataset.idx = bundle.getInt("ldmIdx");
		//LogUtil.v("received ldmIdx: " + mLandmarkDataset.idx);

		//data request using ldmIdx
		soapParser = SoapParser.getInstance(); 

		String query="SELECT * FROM tLandmark WHERE ldmIdx='"+ mLandmarkDataset.idx +"'";
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_LANDMARK));

		query = "SELECT * FROM tPosting WHERE pstParentIdx='" + mLandmarkDataset.idx + "'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_POSTING));

		query = "SELECT * FROM tComment WHERE comParentIdx='" + mLandmarkDataset.idx + "' AND comParentType='L'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));

		/****** UI init *****/
		tabHost = (TabHost) findViewById(R.id.landmark_tabhost);
		lstComment = (ListView) findViewById(R.id.landmark_commentlist);
		lstPosting = (ListView) findViewById(R.id.landmark_postinglist);
		ibtUploadPhoto = (ImageButton) findViewById(R.id.landmark_camera_button);
		edtInputComment = (EditText) findViewById(R.id.landmark_edit_input_comment);
		btnInputComment = (Button) findViewById(R.id.landmark_btn_input_comment);
		tvName = (TextView) findViewById(R.id.landmark_tv_name);
		tvContents = (TextView) findViewById(R.id.landmark_tv_contents);
		imgLandmarkPicture = (ImageView)findViewById(R.id.image);
		btnInputComment.setOnClickListener(this);
		imgLandmarkPicture.setOnClickListener(this);

		//initial listview string.
		mCommentArl = new ArrayList<String>();
		mCommentArl.add("Comments Loading...");
		mPostingArl = new ArrayList<String>();
		mPostingArl.add("Postings Loading...");

		//warn: no listview, SHOULD input layout
		mCommentAdp = new ArrayAdapter<String>(this, R.layout.listview_item_comment , mCommentArl); 
		mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl);

		lstComment.setAdapter(mCommentAdp);
		//lstComment.setOnItemClickListener(lstCommentItemClickListener);
		mCommentAdp.setNotifyOnChange(true); //ArrayList auto reflect. SHOULD USE ArrayList(no strArr)

		lstPosting.setAdapter(mPostingAdp);
		lstPosting.setOnItemClickListener(lstPostingItemClickListener);
		mPostingAdp.setNotifyOnChange(true); //ArrayList auto reflect. SHOULD USE ArrayList(no strArr)

		tabHost.setup(); 

		TabSpec ts1 = tabHost.newTabSpec("Comment");
		ts1.setIndicator("Comment");
		ts1.setContent(R.id.landmark_layout_commentlist);
		tabHost.addTab(ts1);

		TabSpec ts2 = tabHost.newTabSpec("Posting");
		ts2.setIndicator("Posting");
		ts2.setContent(R.id.landmark_postinglist);
		tabHost.addTab(ts2);

		tabHost.setCurrentTab(0);
	}

	/************** when listview clicked ****************/
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position is 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: MUST BE mPostingArr == Listview. (test phrase)

			mIntent = new Intent(LandmarkActivity.this, PostingActivity.class);
			mIntent.putExtra("pstIdx",mPostingArr[position].idx);
			startActivity(mIntent);
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) { 
		switch(v.getId()) {
		case R.id.landmark_btn_input_comment:
		{
			//send 
			if(edtInputComment.getText().toString().compareTo("") == 0) { //no blank allowed. force to return
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
					}
				});
				alert.setMessage("BLANK? ^^");
				alert.show();
				return;
			} else {
				String str = soapParser.sendQuery("SELECT MAX(comIdx) FROM tComment"); 
				int maxComIdx = Integer.parseInt(str);
				str = soapParser.sendQuery(
						"INSERT INTO tComment (comIdx,comParentType,comParentIdx,comContents,comLike,comDislike" +
								",comWriterIdx,comWrittenTime,comPicturePath)" +
								" values ('" +
								(maxComIdx + 1) + //comIdx
								"','L','" + //comParentType
								mLandmarkDataset.idx + //comParentIdx
								"','"+ edtInputComment.getText() + //comContents
								"','0','0','" + //comLike, comDislike
								"1" + //TODO: temp comWriterIdx
								"',GETDATE()," + //comWrittenTime
								"NULL" + //TODO: temp comPicturePath 
						")");
				LogUtil.i("server return : "+str);

				edtInputComment.setText("");

				String query = "SELECT * FROM tComment WHERE comParentIdx='" + mLandmarkDataset.idx + "' AND comParentType='L'"; 
				LogUtil.v("data request. " + query);
				uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
						soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));
			}

			break;

		}
		case R.id.image:
		{
			if(mLandmarkDataset.getImageUrl()!=null) {
				mIntent = new Intent(LandmarkActivity.this, PhotoViewActivity.class);
				mIntent.putExtra("imgPath", mLandmarkDataset.getImageUrl());
				startActivity(mIntent);
			} else {
				LogUtil.w("imgPath is null. cancel calling");
			}

			break;

		}
		}


	}
    	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landmark, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.landmark_action_posting_write:
		{
			LogUtil.v("action_posting_write clicked");

			mIntent = new Intent(this, PostingWriteActivity.class);
			mIntent.putExtra("ldmIdx", mLandmarkDataset.idx);
			startActivity(mIntent);
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
	@Override
	public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub
		imgLandmarkPicture.setImageResource(R.drawable.ic_auil_stub);
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub
		imgLandmarkPicture.setImageResource(R.drawable.ic_auil_error);
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		// TODO Auto-generated method stub
		LogUtil.v("Image onLoadingComplete!");
		imgLandmarkPicture.setImageBitmap(arg2);
	}

	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub
		imgLandmarkPicture.setImageResource(R.drawable.ic_auil_error);
	}
}