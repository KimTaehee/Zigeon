/*
<<<<<<< HEAD
<<<<<<< HEAD
 * author 130816 newcho 
 * modified 130830 newcho (image click added)
=======
 * 130816 조덕주 작성
 * 130819 김태희 수정
>>>>>>> commited.
=======
 * author 130816 newcho 
 * modified 130830 newcho (image click added)
>>>>>>> no hangul conflict solved
 * 
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;

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
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> no hangul conflict solved
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
<<<<<<< HEAD
=======
>>>>>>> commited.
=======
>>>>>>> no hangul conflict solved
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
<<<<<<< HEAD
<<<<<<< HEAD
import android.widget.ImageView;
=======
>>>>>>> commited.
=======
import android.widget.ImageView;
>>>>>>> no hangul conflict solved
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

<<<<<<< HEAD
<<<<<<< HEAD
//TODO: DO NOT USE deprecated Class or function

=======
//TODO: DO NOT USE deprecated Class 혹은 function
>>>>>>> commited.
=======
//TODO: DO NOT USE deprecated Class or function

>>>>>>> no hangul conflict solved
public class LandmarkActivity extends Activity implements OnClickListener {
	private TabHost tabHost;
	private ListView lstComment;
	private ListView lstPosting;
	private ImageButton ibtUploadPhoto;
	private EditText edtInputComment;
	private Button btnInputComment;
	private TextView tvName;
	private TextView tvContents;
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> no hangul conflict solved
	private ImageView imgLandmarkPicture;
	private ArrayList<String> mCommentArl;		//to set listview 
	private ArrayList<String> mPostingArl;		//to set listview 
	private ArrayAdapter<String> mCommentAdp;		//to set listview 
	private ArrayAdapter<String> mPostingAdp;		//to set listview 
<<<<<<< HEAD
=======
	private ArrayList<String> mCommentArl;		//listview 세팅용	
	private ArrayList<String> mPostingArl;		//listview 세팅용
	private ArrayAdapter<String> mCommentAdp;		//listview 세팅용
	private ArrayAdapter<String> mPostingAdp;		//listview 세팅용
>>>>>>> commited.
=======
>>>>>>> no hangul conflict solved
	private LandmarkDataset mLandmarkDataset;		
	private CommentDataset mCommentArr[];
	private PostingDataset mPostingArr[];

	private Intent mIntent;

	private SoapParser soapParser;
	private UIHandler uiHandler;
<<<<<<< HEAD
<<<<<<< HEAD

	private Handler messageHandler = new Handler() { //receiver from UpdateService. important!
=======
	private Handler messageHandler = new Handler() { //UpdateService로부터의 수신부! 중요함
>>>>>>> commited.
=======

	private Handler messageHandler = new Handler() { //receiver from UpdateService. important!
>>>>>>> no hangul conflict solved
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
<<<<<<< HEAD
<<<<<<< HEAD
				LandmarkDataset[] landmarkDataArr = (LandmarkDataset[]) msg.obj; //selected by PK. so Arr.length==1
				mLandmarkDataset = landmarkDataArr[0];

				/******************** print info *******************/
=======
				LandmarkDataset[] landmarkDataArr = (LandmarkDataset[]) msg.obj; //PK로 검색하므로 Arr.length==1이다. 
				mLandmarkDataset = landmarkDataArr[0];

				/******************** info 출력 *******************/
>>>>>>> commited.
=======
				LandmarkDataset[] landmarkDataArr = (LandmarkDataset[]) msg.obj; //selected by PK. so Arr.length==1
				mLandmarkDataset = landmarkDataArr[0];

				/******************** print info *******************/
>>>>>>> no hangul conflict solved
				tvName.setText(mLandmarkDataset.name);
				tvContents.setText(mLandmarkDataset.contents);

				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;

<<<<<<< HEAD
<<<<<<< HEAD
				/************ Posting to listview ************/
=======
				/************ Posting을 listview에 반영한다 ************/
>>>>>>> commited.
=======
				/************ Posting to listview ************/
>>>>>>> no hangul conflict solved
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

<<<<<<< HEAD
<<<<<<< HEAD

				/************ Comment to listview ************/

=======
				/************ Comment를 listview에 반영한다 ************/
>>>>>>> commited.
=======

				/************ Comment to listview ************/

>>>>>>> no hangul conflict solved
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
        
<<<<<<< HEAD
<<<<<<< HEAD
        /************** register handler ***************/
=======
        /************** 핸들러 등록 ***************/
>>>>>>> commited.
=======
        /************** register handler ***************/
>>>>>>> no hangul conflict solved
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
        
		/****** Data init request *****/
<<<<<<< HEAD
<<<<<<< HEAD
		//intent receive. mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx); remind !
=======
		//intent수신. mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);로 intent를 받음을 상기하라
>>>>>>> commited.
=======
		//intent receive. mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx); remind !
>>>>>>> no hangul conflict solved
        Bundle bundle = this.getIntent().getExtras();
        mLandmarkDataset = new LandmarkDataset();
        mLandmarkDataset.idx = bundle.getInt("ldmIdx");
        //LogUtil.v("received ldmIdx: " + mLandmarkDataset.idx);

<<<<<<< HEAD
<<<<<<< HEAD
		//data request using ldmIdx
=======
		//ldbIdx로 내용요청
>>>>>>> commited.
=======
		//data request using ldmIdx
>>>>>>> no hangul conflict solved
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
        
<<<<<<< HEAD
<<<<<<< HEAD
        /****** UI init *****/
=======
        /****** UI 초기화 *****/
>>>>>>> commited.
=======
        /****** UI init *****/
>>>>>>> no hangul conflict solved
        tabHost = (TabHost) findViewById(R.id.landmark_tabhost);
        lstComment = (ListView) findViewById(R.id.landmark_commentlist);
        lstPosting = (ListView) findViewById(R.id.landmark_postinglist);
        ibtUploadPhoto = (ImageButton) findViewById(R.id.landmark_camera_button);
        edtInputComment = (EditText) findViewById(R.id.landmark_edit_input_comment);
        btnInputComment = (Button) findViewById(R.id.landmark_btn_input_comment);
        tvName = (TextView) findViewById(R.id.landmark_tv_name);
        tvContents = (TextView) findViewById(R.id.landmark_tv_contents);
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> no hangul conflict solved
        imgLandmarkPicture = (ImageView)findViewById(R.id.image);
        btnInputComment.setOnClickListener(this);
        imgLandmarkPicture.setOnClickListener(this);
        
        //initial listview string.
<<<<<<< HEAD
=======
        btnInputComment.setOnClickListener(this);
        
        //초기 listview 문구 지정.
>>>>>>> commited.
=======
>>>>>>> no hangul conflict solved
        mCommentArl = new ArrayList<String>();
        mCommentArl.add("Comments Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
<<<<<<< HEAD
<<<<<<< HEAD
        //warn: no listview, SHOULD input layout
=======
        //listview가 아닌 layout이 들어감에 유의
>>>>>>> commited.
=======
        //warn: no listview, SHOULD input layout
>>>>>>> no hangul conflict solved
        mCommentAdp = new ArrayAdapter<String>(this, R.layout.listview_item_comment , mCommentArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl);
        
        lstComment.setAdapter(mCommentAdp);
        //lstComment.setOnItemClickListener(lstCommentItemClickListener);
<<<<<<< HEAD
<<<<<<< HEAD
        mCommentAdp.setNotifyOnChange(true); //ArrayList auto reflect. SHOULD USE ArrayList(no strArr)
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //ArrayList auto reflect. SHOULD USE ArrayList(no strArr)
=======
        mCommentAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
>>>>>>> commited.
=======
        mCommentAdp.setNotifyOnChange(true); //ArrayList auto reflect. SHOULD USE ArrayList(no strArr)
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //ArrayList auto reflect. SHOULD USE ArrayList(no strArr)
>>>>>>> no hangul conflict solved
        
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
    
<<<<<<< HEAD
<<<<<<< HEAD
    /************** when listview clicked ****************/
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position is 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: MUST BE mPostingArr == Listview. (test phrase)
=======
    /************** 리스트뷰 클릭시 ****************/
=======
    /************** when listview clicked ****************/
>>>>>>> no hangul conflict solved
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position은 몇 번째 것을 눌렀는지. 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
<<<<<<< HEAD
			//TODO: mPostingArr와 Listview에 올라간 사항의 일치를 보장시켜야 한다. 아직 확인되지 않음.
>>>>>>> commited.
=======
			//TODO: MUST BE mPostingArr == Listview. (test phrase)
>>>>>>> no hangul conflict solved

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
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> no hangul conflict solved
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
    		LogUtil.i("hi i'm here");
    		mIntent = new Intent(LandmarkActivity.this, PhotoViewActivity.class);
		mIntent.putExtra("imgPath","/sdcard/Download/kang.jpg");
		startActivity(mIntent);
    	
    		break;
    	
    	}
    	}
    	
    	
<<<<<<< HEAD
=======
    public void onClick(View v) { //파워댓글 ㅋ
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
    		String str = soapParser.sendQuery("SELECT MAX(comIdx) FROM tComment"); //정상작동 확인.
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
>>>>>>> commited.
=======
>>>>>>> no hangul conflict solved
    }
    
    @Override
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
			startActivity(mIntent);
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
}
