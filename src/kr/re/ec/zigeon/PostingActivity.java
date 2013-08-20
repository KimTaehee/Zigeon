/*
 * 130816 조덕주 작성
 * 130819 김태희 수정
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
import android.content.Intent;
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
	
	private ArrayList<String> mCommentArl;		//listview 세팅용
	private ArrayAdapter<String> mCommentAdp;		//listview 세팅용
	
	private PostingDataset mPostingDataset;
	private CommentDataset mCommentArr[];
	
	private SoapParser soapParser;
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService로부터의 수신부! 중요함
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			
			case Constants.MSG_TYPE_POSTING:
			{
				PostingDataset[] postingDataArr = (PostingDataset[]) msg.obj; 
				mPostingDataset = postingDataArr[0];
				
				/******************** info 출력 *******************/
				tvTitle.setText(mPostingDataset.title);
				tvWrittenTime.setText(mPostingDataset.writtenTime.toString());
				tvWriter.setText("서듈님. memIdx: " + mPostingDataset.writerIdx); //TODO: tMember쿼리처리해야함.
				//TODO: test line separator. 아직 어떤 식으로 저장되는지 모름.
				tvContents.setText(mPostingDataset.contents.replaceAll("\\\\n", "\\\n"));
//				tvLike.setText(mPostingDataset.like);
//				tvDislike.setText(mPostingDataset.dislike);
				
				break;
			}
			case Constants.MSG_TYPE_COMMENT:
			{
				mCommentArr =(CommentDataset[]) msg.obj;
				
				/************ Comment를 listview에 반영한다 ************/
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
	
		/************** 핸들러 등록 ***************/
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
        
		/****** UI 초기화 *****/
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
		ibtUploadPhoto = (ImageButton) findViewById(R.id.posting_camera_button);
		ibtUploadPhoto.setOnClickListener(this);
		
		mCommentArl = new ArrayList<String>();
        mCommentArl.add("Comments Loading...");
        
        mCommentAdp = new ArrayAdapter<String>(this, R.layout.listview_item_comment , mCommentArl);
        lstComment.setAdapter(mCommentAdp);
        mCommentAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
        
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
	public void onClick(View v) { //파워 댓글달기
		
		switch(v.getId()) {
		case R.id.posting_btn_input_comment:
		{
			if(edtInputComment.getText().toString().compareTo("") == 0) { //내용없으면 에러띄우고 강제 return
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
					}
				});
				alert.setMessage("내용을 입력해야지? ^^");
				alert.show();
				return;
			} else {
				String str = soapParser.sendQuery("SELECT MAX(comIdx) FROM tComment"); //정상작동 확인. +1한 idx로 insert한다.
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
			break;
		}
			
		case R.id.posting_camera_button:
		{
			startActivity(new Intent(this,PhotoUploadActivity.class));
			break;
		}
		}
		
	}

}
