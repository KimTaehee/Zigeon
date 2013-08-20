<<<<<<< HEAD
/*hjkljlk
 * 130816 Á¶´öÁÖ ÀÛ¼º
 * 130819 ±èÅÂÈñ ¼öÁ¤
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;
import java.util.Date;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class PostingActivity extends Activity implements OnClickListener {

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
	
	private ArrayList<String> mCommentArl;		//listview ¼¼ÆÃ¿ë
	private ArrayAdapter<String> mCommentAdp;		//listview ¼¼ÆÃ¿ë
	
	private PostingDataset mPostingDataset;
	private CommentDataset mCommentArr[];
	
	private SoapParser soapParser;
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService·ÎºÎÅÍÀÇ ¼ö½ÅºÎ! Áß¿äÇÔ
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			
			case Constants.MSG_TYPE_POSTING:
			{
				PostingDataset[] postingDataArr = (PostingDataset[]) msg.obj; 
				mPostingDataset = postingDataArr[0];
				
				/******************** info Ãâ·Â *******************/
				tvTitle.setText(mPostingDataset.title);
				tvWrittenTime.setText(mPostingDataset.writtenTime.toString());
				tvWriter.setText("¼­µâ´Ô. memIdx: " + mPostingDataset.writerIdx); //TODO: tMemberÄõ¸®Ã³¸®ÇØ¾ßÇÔ.
				//TODO: test line separator. ¾ÆÁ÷ ¾î¶² ½ÄÀ¸·Î ÀúÀåµÇ´ÂÁö ¸ğ¸§.
				tvContents.setText(mPostingDataset.contents.replaceAll("\\\\n", "\\\n"));
//				tvLike.setText(mPostingDataset.like);
//				tvDislike.setText(mPostingDataset.dislike);
				
				break;
			}
			case Constants.MSG_TYPE_COMMENT:
			{
				mCommentArr =(CommentDataset[]) msg.obj;
				
				/************ Comment¸¦ listview¿¡ ¹İ¿µÇÑ´Ù ************/
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting);
	
		/************** ÇÚµé·¯ µî·Ï ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/****** Data init request *****/
		Bundle bundle = this.getIntent().getExtras();
        mPostingDataset = new PostingDataset();
        mPostingDataset.idx = bundle.getInt("pstIdx");
		
        soapParser = SoapParser.getInstance();
		
        String query = "SELECT * FROM tPosting WHERE pstIdx='" + mPostingDataset.idx + "'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_POSTING));
        
		query = "SELECT * FROM tComment WHERE comParentIdx='" + mPostingDataset.idx + "' AND comParentType='P'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));
        
		/****** UI ÃÊ±âÈ­ *****/
		tvTitle = (TextView) findViewById(R.id.posting_tv_title);
		tvWrittenTime = (TextView) findViewById(R.id.posting_tv_writedate);
		tvWriter = (TextView) findViewById(R.id.posting_tv_writer);
		tvContents = (TextView) findViewById(R.id.posting_tv_contents);
		tvLike = (TextView) findViewById(R.id.posting_tv_like);
		tvDislike = (TextView) findViewById(R.id.posting_tv_dislike);
		lstComment = (ListView) findViewById(R.id.posting_commentlist);
		btnInputComment = (Button) findViewById(R.id.posting_btn_input_comment);
		btnInputComment.setOnClickListener(this);
		edtInputComment = (EditText) findViewById(R.id.posting_edit_input_comment);
		
		mCommentArl = new ArrayList<String>();
        mCommentArl.add("Comments Loading...");
        
        mCommentAdp = new ArrayAdapter<String>(this, R.layout.listview_item_comment , mCommentArl);
        lstComment.setAdapter(mCommentAdp);
        mCommentAdp.setNotifyOnChange(true); //ÀÌ ¿É¼ÇÀÌ ÀÖÀ¸¸é ArrayList°¡ ¼öÁ¤µÉ ¶§ ÀÚµ¿À¸·Î ¹İ¿µµÈ´Ù. strArr´ë½Å ArrayList¸¦ ½á¾ß ÇÏ´Â ÀÌÀ¯
        
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
	}

	@Override
	public void onClick(View v) { //ÆÄ¿ö ´ñ±Û´Ş±â
		if(edtInputComment.getText().toString().compareTo("") == 0) { //³»¿ë¾øÀ¸¸é ¿¡·¯¶ç¿ì°í °­Á¦ return
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setPositiveButton("È®ÀÎ", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();	
				}
			});
			alert.setMessage("³»¿ëÀ» ÀÔ·ÂÇØ¾ßÁö? ^^");
			alert.show();
			return;
		} else {
			String str = soapParser.sendQuery("SELECT MAX(comIdx) FROM tComment"); //Á¤»óÀÛµ¿ È®ÀÎ. +1ÇÑ idx·Î insertÇÑ´Ù.
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
							"1" + //TODO: temp comWriterIdx
							"',GETDATE()," + //comWrittenTime
							"NULL" + //TODO: temp comPicturePath 
					")");
			LogUtil.i("server return : "+str);
			
			edtInputComment.setText("");
			
			String query = "SELECT * FROM tComment WHERE comParentIdx='"
					+ mPostingDataset.idx + "' AND comParentType='P'"; 
			LogUtil.v("data request. " + query);
			uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
					soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));
			
			//LogUtil.v("SELECT MAX(comIdx) FROM tComment=====>" + str);
			//String str = soapParser.sendQuery("insert into tComment (col,col,col) values (val,valval..)");
		}
	}

}
=======
/*
 * 130816 ì¡°ë•ì£¼ ì‘ì„±
 * 130819 ê¹€íƒœí¬ ìˆ˜ì •
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;
import java.util.Date;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class PostingActivity extends Activity implements OnClickListener {

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
	
	private ArrayList<String> mCommentArl;		//listview ì„¸íŒ…ìš©
	private ArrayAdapter<String> mCommentAdp;		//listview ì„¸íŒ…ìš©
	
	private PostingDataset mPostingDataset;
	private CommentDataset mCommentArr[];
	
	private SoapParser soapParser;
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateServiceë¡œë¶€í„°ì˜ ìˆ˜ì‹ ë¶€! ì¤‘ìš”í•¨
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			
			case Constants.MSG_TYPE_POSTING:
			{
				PostingDataset[] postingDataArr = (PostingDataset[]) msg.obj; 
				mPostingDataset = postingDataArr[0];
				
				/******************** info ì¶œë ¥ *******************/
				tvTitle.setText(mPostingDataset.title);
				tvWrittenTime.setText(mPostingDataset.writtenTime.toString());
				tvWriter.setText("ì„œë“ˆë‹˜. memIdx: " + mPostingDataset.writerIdx); //TODO: tMemberì¿¼ë¦¬ì²˜ë¦¬í•´ì•¼í•¨.
				//TODO: test line separator. ì•„ì§ ì–´ë–¤ ì‹ìœ¼ë¡œ ì €ì¥ë˜ëŠ”ì§€ ëª¨ë¦„.
				tvContents.setText(mPostingDataset.contents.replaceAll("\\\\n", "\\\n"));
//				tvLike.setText(mPostingDataset.like);
//				tvDislike.setText(mPostingDataset.dislike);
				
				break;
			}
			case Constants.MSG_TYPE_COMMENT:
			{
				mCommentArr =(CommentDataset[]) msg.obj;
				
				/************ Commentë¥¼ listviewì— ë°˜ì˜í•œë‹¤ ************/
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting);
	
		/************** í•¸ë“¤ëŸ¬ ë“±ë¡ ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
		
		/****** Data init request *****/
		Bundle bundle = this.getIntent().getExtras();
        mPostingDataset = new PostingDataset();
        mPostingDataset.idx = bundle.getInt("pstIdx");
		
        soapParser = SoapParser.getInstance();
		
        String query = "SELECT * FROM tPosting WHERE pstIdx='" + mPostingDataset.idx + "'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_POSTING, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_POSTING));
        
		query = "SELECT * FROM tComment WHERE comParentIdx='" + mPostingDataset.idx + "' AND comParentType='P'"; 
		LogUtil.v("data request. " + query);
		uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
				soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));
        
		/****** UI ì´ˆê¸°í™” *****/
		tvTitle = (TextView) findViewById(R.id.posting_tv_title);
		tvWrittenTime = (TextView) findViewById(R.id.posting_tv_writedate);
		tvWriter = (TextView) findViewById(R.id.posting_tv_writer);
		tvContents = (TextView) findViewById(R.id.posting_tv_contents);
		tvLike = (TextView) findViewById(R.id.posting_tv_like);
		tvDislike = (TextView) findViewById(R.id.posting_tv_dislike);
		lstComment = (ListView) findViewById(R.id.posting_commentlist);
		btnInputComment = (Button) findViewById(R.id.posting_btn_input_comment);
		btnInputComment.setOnClickListener(this);
		edtInputComment = (EditText) findViewById(R.id.posting_edit_input_comment);
		
		mCommentArl = new ArrayList<String>();
        mCommentArl.add("Comments Loading...");
        
        mCommentAdp = new ArrayAdapter<String>(this, R.layout.listview_item_comment , mCommentArl);
        lstComment.setAdapter(mCommentAdp);
        mCommentAdp.setNotifyOnChange(true); //ì´ ì˜µì…˜ì´ ìˆìœ¼ë©´ ArrayListê°€ ìˆ˜ì •ë  ë•Œ ìë™ìœ¼ë¡œ ë°˜ì˜ëœë‹¤. strArrëŒ€ì‹  ArrayListë¥¼ ì¨ì•¼ í•˜ëŠ” ì´ìœ 
        
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
	}

	@Override
	public void onClick(View v) { //íŒŒì›Œ ëŒ“ê¸€ë‹¬ê¸°
		if(edtInputComment.getText().toString().compareTo("") == 0) { //ë‚´ìš©ì—†ìœ¼ë©´ ì—ëŸ¬ë„ìš°ê³  ê°•ì œ return
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setPositiveButton("í™•ì¸", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();	
				}
			});
			alert.setMessage("ë‚´ìš©ì„ ì…ë ¥í•´ì•¼ì§€? ^^");
			alert.show();
			return;
		} else {
			String str = soapParser.sendQuery("SELECT MAX(comIdx) FROM tComment"); //ì •ìƒì‘ë™ í™•ì¸. +1í•œ idxë¡œ insertí•œë‹¤.
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
							"1" + //TODO: temp comWriterIdx
							"',GETDATE()," + //comWrittenTime
							"NULL" + //TODO: temp comPicturePath 
					")");
			LogUtil.i("server return : "+str);
			
			edtInputComment.setText("");
			
			String query = "SELECT * FROM tComment WHERE comParentIdx='"
					+ mPostingDataset.idx + "' AND comParentType='P'"; 
			LogUtil.v("data request. " + query);
			uiHandler.sendMessage(Constants.MSG_TYPE_COMMENT, "", 
					soapParser.getSoapData(query, Constants.MSG_TYPE_COMMENT));
			
			//LogUtil.v("SELECT MAX(comIdx) FROM tComment=====>" + str);
			//String str = soapParser.sendQuery("insert into tComment (col,col,col) values (val,valval..)");
		}
	}

}
>>>>>>> UTF-8
