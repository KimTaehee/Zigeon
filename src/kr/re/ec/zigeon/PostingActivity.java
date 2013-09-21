/**
 * Author ChoDeokjoo 130816
 * Modified KimTaehee 130819 
 */

package kr.re.ec.zigeon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import kr.re.ec.zigeon.util.PhotoUploader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class PostingActivity extends Activity implements OnClickListener, ImageLoadingListener {

	private ActivityManager activityManager = ActivityManager.getInstance();
	
	private TextView tvTitle;
	private TextView tvWrittenTime;
	private TextView tvWriter;
	private TextView tvContents;
	private TextView tvLike;
	private TextView tvDislike;
	private ListView lstComment;
	private ImageButton ibtUploadPhoto;
	private EditText edtInputComment;
	private Button btnInputComment;
	private ImageView imgPosting;

	private CommentAdapter mCommentAdp;		//to set listview 

	private PostingDataset mPostingDataset;
	private CommentDataset mCommentArr[];
	private SoapParser soapParser;
	private UIHandler uiHandler;
	
	/******** AUIL init ********/
	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_auil_stub)	
	.showImageForEmptyUri(R.drawable.ic_auil_empty)
	.showImageOnFail(R.drawable.ic_auil_error)
	.build();
	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton
	
	private Handler messageHandler = new Handler() { //recieving to UpdateService
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_POSTING:
			{
				PostingDataset[] postingDataArr = (PostingDataset[]) msg.obj; 
				mPostingDataset = postingDataArr[0];

				/******************** print info  *******************/
				tvTitle.setText(mPostingDataset.title);
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_PRINT);
				tvWrittenTime.setText("written date: " + sdf.format(mPostingDataset.writtenTime));
				tvWriter.setText("writer: " + mPostingDataset.writerName); //TODO: tMember query proceeing
				//TODO: test line separator. what is it?
				tvContents.setText("contents: \n"  + mPostingDataset.contents.replaceAll("\\\\n", "\\\n"));
//				tvLike.setText(mPostingDataset.like);
//				tvDislike.setText(mPostingDataset.dislike);
				LogUtil.v("image load start! uri: " + mPostingDataset.getImageUrl());
				imgLoader.loadImage(mPostingDataset.getImageUrl(), PostingActivity.this); //load landmark image

				break;
			}
			case Constants.MSG_TYPE_COMMENT:
			{
				mCommentArr =(CommentDataset[]) msg.obj;

				/************ print Comment to listview ************/
				LogUtil.i("mCommentArr.length: " + mCommentArr.length);
				mCommentAdp = new CommentAdapter(PostingActivity.this, mCommentArr);
				lstComment.setAdapter(mCommentAdp);
				mCommentAdp.notifyDataSetChanged();	//TODO: is this work?
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting);

		/*******add activity list********/
		activityManager.addActivity(this);
		
		/************** register handler ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);

		/****** Data init request *****/
		Bundle bundle = this.getIntent().getExtras();
        mPostingDataset = new PostingDataset();
        mPostingDataset.idx = bundle.getInt("pstIdx");

        soapParser = SoapParser.getInstance();

        String query = "SELECT * FROM tPosting WHERE pstIdx='" + mPostingDataset.idx + "' " +
				"ORDER BY pstWrittenTime desc";  
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_POSTING));
        
		query = "SELECT * FROM tComment WHERE comParentIdx='" + mPostingDataset.idx + "' " +
					"AND comParentType='P' ORDER BY comWrittenTime desc"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));
		
        
		/****** UI init *****/
		tvTitle = (TextView) findViewById(R.id.posting_tv_title);
		tvWrittenTime = (TextView) findViewById(R.id.posting_tv_writedate);
		tvWriter = (TextView) findViewById(R.id.posting_tv_writer);
		tvContents = (TextView) findViewById(R.id.posting_tv_contents);
		lstComment = (ListView) findViewById(R.id.posting_commentlist);
		btnInputComment = (Button) findViewById(R.id.posting_btn_input_comment);
		btnInputComment.setOnClickListener(this);
		edtInputComment = (EditText) findViewById(R.id.posting_edit_input_comment);
		imgPosting = (ImageView) findViewById(R.id.posting_img_posting);
		imgPosting.setOnClickListener(this);

		//TODO: if no item on listview, SHOULD put other msg;
		mCommentAdp = new CommentAdapter(this, mCommentArr);
        lstComment.setAdapter(mCommentAdp);
        //mCommentAdp.setNotifyOnChange(true); //when ArrayList modified, ArrayList is reflected automatically. SHOULD USE ArrayList
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.posting, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		/*********remove activity list******/
		activityManager.removeActivity(this);
		
	}

	@Override
	public void onClick(View v) { //input comment
		switch(v.getId()) {
		case R.id.posting_btn_input_comment:
		{
			if(edtInputComment.getText().toString().compareTo("") == 0) { //if blank, force to return and alert.
				new AlertManager(this,"Blank Comment? ^^","Confirm");
				return;
			} else {
				String str = soapParser.sendQuery("SELECT MAX(comIdx) FROM tComment"); //+1 idx insertion.
				int maxComIdx = Integer.parseInt(str);
				str = soapParser.sendQuery(
						"INSERT INTO tComment (comIdx,comParentType,comParentIdx,comContents,comLike,comDislike" +
								",comWriterIdx,comWrittenTime,comPicturePath)" +
								" values ('" +
								(maxComIdx + 1) + //comIdx
								"','P','" + //comParentType
								mPostingDataset.idx + //comParentIdx
								"','"+ edtInputComment.getText() + //comContents
								"','0','0','" + //comLike, comDislike
								MemberDataset.getLoginInstance().idx + //comWriterIdx
								"',GETDATE()," + //comWrittenTime
								"NULL" + //TODO: temp comPicturePath 
						")");
				LogUtil.i("server return : "+str);

				edtInputComment.setText("");

				String query = "SELECT * FROM tComment WHERE comParentIdx='"
						+ mPostingDataset.idx + "' AND comParentType='P' ORDER BY comWrittenTime desc"; 
				LogUtil.v("data request. " + query);
				uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
						soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));

				//LogUtil.v("SELECT MAX(comIdx) FROM tComment=====>" + str);
				//String str = soapParser.sendQuery("insert into tComment (col,col,col) values (val,valval..)");
			}
			break;
		}
		
		case R.id.posting_img_posting:
		{
			if(mPostingDataset.getImageUrl()!=null) {
				Intent intent = new Intent(PostingActivity.this, PhotoViewActivity.class);
				intent.putExtra("imgPath", mPostingDataset.getImageUrl());
				startActivity(intent);
			} else {
				LogUtil.w("imgPath is null. cancel calling");
			}
			

			break;

		}
		}

	}

	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
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
	public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub
		imgPosting.setImageResource(R.drawable.ic_auil_stub);
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub
		imgPosting.setImageResource(R.drawable.ic_auil_error);
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		// TODO Auto-generated method stub
		LogUtil.v("Image onLoadingComplete!");
		imgPosting.setImageBitmap(arg2);
	}

	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub
		imgPosting.setImageResource(R.drawable.ic_auil_error);
	}
}
