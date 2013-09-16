 package kr.re.ec.zigeon;

import java.util.ArrayList;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class UserProfileActivity extends Activity {

	public Activity act = this;
	public ProgressBar prBar;

	private ListView myLstComment;
	private ListView myLstPosting;
	private MemberDataset mMemberDataset;	
	private ArrayList<String> mCommentArl;		//to set listview 
	private ArrayList<String> mPostingArl;		//to set listview 
	private ArrayAdapter<String> mCommentAdp;		//to set listview 
	private ArrayAdapter<String> mPostingAdp;		//to set listview 
	private CommentDataset mCommentArr[];
	private PostingDataset mPostingArr[];
	private Intent mIntent;
	private SoapParser soapParser;
	private UIHandler uiHandler;

	private Handler messageHandler = new Handler() { //receiver from UpdateService. important!
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
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
				myLstComment.smoothScrollToPosition(mCommentArr.length-1);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

		ImageView levelImage = (ImageView)findViewById(R.id.levelImage);
		levelImage.setScaleType(ImageView.ScaleType.FIT_XY); // layout set scale

		prBar = (ProgressBar) findViewById(R.id.progressBar);
		//		prBar.setVisibility(ProgressBar.GONE);

		
		/************** register handler ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);

		/****** Data init request *****/
		//intent receive. mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx); remind !
		Bundle bundle = this.getIntent().getExtras();
		mMemberDataset = new MemberDataset();
		mMemberDataset.idx = bundle.getInt("memIdx");
		//LogUtil.v("received ldmIdx: " + mLandmarkDataset.idx);

		//data request using ldmIdx
		soapParser = SoapParser.getInstance(); 

		String query="SELECT TOP 20 * FROM tLandmark WHERE ldmIdx='"+ mMemberDataset.idx +"'";
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_LANDMARK, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_LANDMARK));

		query = "SELECT * FROM tPosting WHERE pstParentIdx='" + mMemberDataset.idx + "'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_POSTING));

		query = "SELECT * FROM tComment WHERE comParentIdx='" + mMemberDataset.idx + "' AND comParentType='L'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));
		
		/*****************TabTab***************************/
		TabHost tab_host = (TabHost) findViewById(R.id.tabhost);
		tab_host.setup(); 

		TabSpec ts1 = tab_host.newTabSpec("Alarm");
		ts1.setIndicator("Alarm");
		ts1.setContent(R.id.alarm);
		tab_host.addTab(ts1);

		TabSpec ts2 = tab_host.newTabSpec("My Postings");
		ts2.setIndicator("My Postings");
		ts2.setContent(R.id.postings);
		tab_host.addTab(ts2);
		
		TabSpec ts3 = tab_host.newTabSpec("My comments");
		ts2.setIndicator("My comments");
		ts2.setContent(R.id.comments);
		tab_host.addTab(ts3);

		tab_host.setCurrentTab(0);
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_profile, menu);
		return true;
	}
	*/
}
