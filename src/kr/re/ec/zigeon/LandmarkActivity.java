<<<<<<< HEAD
/* djdjdj
 * 130816 ������ �ۼ�
 * 130819 ������ ����
 * 
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

//TODO: DO NOT USE deprecated Class Ȥ�� function
public class LandmarkActivity extends Activity implements OnClickListener {
	private TabHost tabHost;
	private ListView lstComment;
	private ListView lstPosting;
	private ImageButton ibtUploadPhoto;
	private EditText edtInputComment;
	private Button btnInputComment;
	private TextView tvName;
	private TextView tvContents;
	
	private ArrayList<String> mCommentArl;		//listview ���ÿ�	
	private ArrayList<String> mPostingArl;		//listview ���ÿ�
	private ArrayAdapter<String> mCommentAdp;		//listview ���ÿ�
	private ArrayAdapter<String> mPostingAdp;		//listview ���ÿ�
	private LandmarkDataset mLandmarkDataset;		
	private CommentDataset mCommentArr[];
	private PostingDataset mPostingArr[];
	
	private Intent mIntent;
	
	private SoapParser soapParser;
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService�κ����� ���ź�! �߿���
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				LandmarkDataset[] landmarkDataArr = (LandmarkDataset[]) msg.obj; //PK�� �˻��ϹǷ� Arr.length==1�̴�. 
				mLandmarkDataset = landmarkDataArr[0];
				
				/******************** info ���� *******************/
				tvName.setText(mLandmarkDataset.name);
				tvContents.setText(mLandmarkDataset.contents);
				
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ Posting�� listview�� �ݿ��Ѵ� ************/
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
				
				/************ Comment�� listview�� �ݿ��Ѵ� ************/
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
        
        /************** �ڵ鷯 ���� ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
        
		/****** Data init request *****/
		//intent����. mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);�� intent�� ������ �����϶�
        Bundle bundle = this.getIntent().getExtras();
        mLandmarkDataset = new LandmarkDataset();
        mLandmarkDataset.idx = bundle.getInt("ldmIdx");
        //LogUtil.v("received ldmIdx: " + mLandmarkDataset.idx);
		
		//ldbIdx�� ������û
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
        
        /****** UI �ʱ�ȭ *****/
        tabHost = (TabHost) findViewById(R.id.landmark_tabhost);
        lstComment = (ListView) findViewById(R.id.landmark_commentlist);
        lstPosting = (ListView) findViewById(R.id.landmark_postinglist);
        ibtUploadPhoto = (ImageButton) findViewById(R.id.landmark_camera_button);
        edtInputComment = (EditText) findViewById(R.id.landmark_edit_input_comment);
        btnInputComment = (Button) findViewById(R.id.landmark_btn_input_comment);
        tvName = (TextView) findViewById(R.id.landmark_tv_name);
        tvContents = (TextView) findViewById(R.id.landmark_tv_contents);
        btnInputComment.setOnClickListener(this);
        
        //�ʱ� listview ���� ����.
        mCommentArl = new ArrayList<String>();
        mCommentArl.add("Comments Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //listview�� �ƴ� layout�� ����� ����
        mCommentAdp = new ArrayAdapter<String>(this, R.layout.listview_item_comment , mCommentArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl);
        
        lstComment.setAdapter(mCommentAdp);
        //lstComment.setOnItemClickListener(lstCommentItemClickListener);
        mCommentAdp.setNotifyOnChange(true); //�� �ɼ��� ������ ArrayList�� ������ �� �ڵ����� �ݿ��ȴ�. strArr���� ArrayList�� ���� �ϴ� ����
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //�� �ɼ��� ������ ArrayList�� ������ �� �ڵ����� �ݿ��ȴ�. strArr���� ArrayList�� ���� �ϴ� ����
        
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
    
    /************** ����Ʈ�� Ŭ���� ****************/
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position�� �� ��° ���� ��������. 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArr�� Listview�� �ö��� ������ ��ġ�� �������Ѿ� �Ѵ�. ���� Ȯ�ε��� ����.
			
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
    public void onClick(View v) { //�Ŀ����� ��
    	if(edtInputComment.getText().toString().compareTo("") == 0) { //���������� ���������� ���� return
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    		alert.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.dismiss();	
    			}
    		});
    		alert.setMessage("������ �Է��ؾ���? ^^");
    		alert.show();
    		return;
    	} else {
    		String str = soapParser.sendQuery("SELECT MAX(comIdx) FROM tComment"); //�����۵� Ȯ��.
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
    }
}
=======
/*
 * 130816 조덕주 작성
 * 130819 김태희 수정
 * 
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

//TODO: DO NOT USE deprecated Class 혹은 function
public class LandmarkActivity extends Activity implements OnClickListener {
	private TabHost tabHost;
	private ListView lstComment;
	private ListView lstPosting;
	private ImageButton ibtUploadPhoto;
	private EditText edtInputComment;
	private Button btnInputComment;
	private TextView tvName;
	private TextView tvContents;
	
	private ArrayList<String> mCommentArl;		//listview 세팅용	
	private ArrayList<String> mPostingArl;		//listview 세팅용
	private ArrayAdapter<String> mCommentAdp;		//listview 세팅용
	private ArrayAdapter<String> mPostingAdp;		//listview 세팅용
	private LandmarkDataset mLandmarkDataset;		
	private CommentDataset mCommentArr[];
	private PostingDataset mPostingArr[];
	
	private Intent mIntent;
	
	private SoapParser soapParser;
	private UIHandler uiHandler;
	private Handler messageHandler = new Handler() { //UpdateService로부터의 수신부! 중요함
		@Override
		public void handleMessage(Message msg){
			LogUtil.v("msg receive success!");
			switch (msg.what) {
			case Constants.MSG_TYPE_LANDMARK:
			{
				LandmarkDataset[] landmarkDataArr = (LandmarkDataset[]) msg.obj; //PK로 검색하므로 Arr.length==1이다. 
				mLandmarkDataset = landmarkDataArr[0];
				
				/******************** info 출력 *******************/
				tvName.setText(mLandmarkDataset.name);
				tvContents.setText(mLandmarkDataset.contents);
				
				break;
			}
			case Constants.MSG_TYPE_POSTING:
			{
				mPostingArr =(PostingDataset[]) msg.obj;
				
				/************ Posting을 listview에 반영한다 ************/
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);  
        
        /************** 핸들러 등록 ***************/
		uiHandler = UIHandler.getInstance(this);
		uiHandler.setHandler(messageHandler);
        
		/****** Data init request *****/
		//intent수신. mIntent.putExtra("ldmIdx",mLandmarkArr[position].idx);로 intent를 받음을 상기하라
        Bundle bundle = this.getIntent().getExtras();
        mLandmarkDataset = new LandmarkDataset();
        mLandmarkDataset.idx = bundle.getInt("ldmIdx");
        //LogUtil.v("received ldmIdx: " + mLandmarkDataset.idx);
		
		//ldbIdx로 내용요청
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
        
        /****** UI 초기화 *****/
        tabHost = (TabHost) findViewById(R.id.landmark_tabhost);
        lstComment = (ListView) findViewById(R.id.landmark_commentlist);
        lstPosting = (ListView) findViewById(R.id.landmark_postinglist);
        ibtUploadPhoto = (ImageButton) findViewById(R.id.landmark_camera_button);
        edtInputComment = (EditText) findViewById(R.id.landmark_edit_input_comment);
        btnInputComment = (Button) findViewById(R.id.landmark_btn_input_comment);
        tvName = (TextView) findViewById(R.id.landmark_tv_name);
        tvContents = (TextView) findViewById(R.id.landmark_tv_contents);
        btnInputComment.setOnClickListener(this);
        
        //초기 listview 문구 지정.
        mCommentArl = new ArrayList<String>();
        mCommentArl.add("Comments Loading...");
        mPostingArl = new ArrayList<String>();
        mPostingArl.add("Postings Loading...");
        
        //listview가 아닌 layout이 들어감에 유의
        mCommentAdp = new ArrayAdapter<String>(this, R.layout.listview_item_comment , mCommentArl); 
        mPostingAdp = new ArrayAdapter<String>(this, R.layout.listview_item_posting , mPostingArl);
        
        lstComment.setAdapter(mCommentAdp);
        //lstComment.setOnItemClickListener(lstCommentItemClickListener);
        mCommentAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
        
        lstPosting.setAdapter(mPostingAdp);
        lstPosting.setOnItemClickListener(lstPostingItemClickListener);
        mPostingAdp.setNotifyOnChange(true); //이 옵션이 있으면 ArrayList가 수정될 때 자동으로 반영된다. strArr대신 ArrayList를 써야 하는 이유
        
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
    
    /************** 리스트뷰 클릭시 ****************/
	private AdapterView.OnItemClickListener lstPostingItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position은 몇 번째 것을 눌렀는지. 0~n
			LogUtil.v("onItemClick invoked!! item: " + ((TextView)view).getText());
			LogUtil.v("position: "+position + ", ldmIdx: " + mPostingArr[position].idx);
			//TODO: mPostingArr와 Listview에 올라간 사항의 일치를 보장시켜야 한다. 아직 확인되지 않음.
			
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
    }
}
>>>>>>> UTF-8
