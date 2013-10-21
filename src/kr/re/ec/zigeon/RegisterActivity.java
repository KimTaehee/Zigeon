/**
 * 
 * 
 *  
 * modified date: 130929 Kim Taehee - all new registered member isAdmin = false
 *     
*/
package kr.re.ec.zigeon;

import kr.re.ec.zigeon.R;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.util.ActivityManager;
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener{
	private ActivityManager activityManager = ActivityManager.getInstance();
	private EditText id,password,nickname;
	private SoapParser soapParser;
	private String ID_check, Nickname_check; 
	private Button cancel_btn, join_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*******add activity list********/
		activityManager.addActivity(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		cancel_btn = (Button)findViewById(R.id.Cancle_btu);
		cancel_btn.setOnClickListener(this);
		join_btn = (Button)findViewById(R.id.Join_btn);
		join_btn.setOnClickListener(this);
		id = (EditText)findViewById(R.id.Join_Id);
		password = (EditText)findViewById(R.id.Join_Password);
		nickname = (EditText)findViewById(R.id.Join_Nickname);
		soapParser = SoapParser.getInstance();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.Cancle_btu:
			finish();
			return;
		case R.id.Join_btn:
			soapParser = SoapParser.getInstance();
			String idQuery = "SELECT memPW FROM tMember WHERE memID ='" + id.getText().toString() + "'";
			ID_check = soapParser.sendQuery(idQuery);
			LogUtil.v("ID check = " + ID_check);
			LogUtil.v("ID check.compareTo(null) = " + ID_check.compareTo(""));
			String nicknameQuery = "SELECT memPW FROM tMember WHERE memNick ='" + nickname.getText().toString() + "'";
			Nickname_check = soapParser.sendQuery(nicknameQuery); 
			if(id.getText().toString().compareTo("") == 0) { //if have no text, show alert and return
				new AlertManager().show(this, "아이디를 입력하세요. ^^", "확인", Constants.ALERT_OK_ONLY);	
//				AlertDialog.Builder alert = new AlertDialog.Builder(this);
//				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();	
//					}
//				});
//				alert.setMessage("Input ID.");
//				alert.show();
				return;
			} else if(ID_check.compareTo("") != 0)//duplicated id
			{
				new AlertManager().show(this, "해당 ID는 현재 사용 중 입니다.", "확인", Constants.ALERT_OK_ONLY);
//				AlertDialog.Builder alert = new AlertDialog.Builder(this);
//				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();	
//					}
//				});
//				alert.setMessage("This ID already in use.");
//				alert.show();
				return;
			}	
			else if(password.getText().toString().compareTo("") == 0){
				new AlertManager().show(this, "패스워드를 입력하세요.", "확인", Constants.ALERT_OK_ONLY);
//				AlertDialog.Builder alert = new AlertDialog.Builder(this);
//				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();	
//					}
//				});
//				alert.setMessage("Input Password.");
//				alert.show();
				return;
			}else if(nickname.getText().toString().compareTo("") == 0){
				new AlertManager().show(this, "닉네임을 입력하세요.", "확인", Constants.ALERT_OK_ONLY);
//				AlertDialog.Builder alert = new AlertDialog.Builder(this);
//				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();	
//					}
//				});
//				alert.setMessage("Input Nickname");
//				alert.show();
				return;
			}else if(Nickname_check.compareTo("") != 0)
			{
				new AlertManager().show(this, "해당 닉네임은 이미 사용 중 입니다.", "확인", Constants.ALERT_OK_ONLY);
//				AlertDialog.Builder alert = new AlertDialog.Builder(this);
//				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();	
//					}
//				});
//				alert.setMessage("This Nickname already in use.");
//				alert.show();
				return;
			}
			else{
			
				String str = soapParser.sendQuery("SELECT MAX(memIdx) FROM tMember"); //+1 idx insert
				LogUtil.v(str);
				int maxMemIdx = Integer.parseInt(str);
				LogUtil.v("start sendQuery ");
				str = soapParser.sendQuery(
						"INSERT INTO tMember(memIdx,memID,memPW,memNick,memExp,memIsFacebook" +
								",memRegTime,memLastAccessTime,memIsAdmin)" +
								" values ('" +
								(maxMemIdx + 1) + //memIdx
								"','" + id.getText() +//memID
								"','" + password.getText() + //memPW
								"','" + nickname.getText() +  //memNick
								"','0"+ //memExp
								"','false" + //memIsFacebook
								"',GETDATE()" + //memRegTime
								",GETDATE()" + //memLastAccessTime
								",'false'" + //memIsAdmin 
						")");
				LogUtil.v("server return : "+str);
				break;
			}
		}
		Toast toast = Toast.makeText(this, "성공적으로 가입되었습니다. 환영합니다 ^ㅡ^",Toast.LENGTH_SHORT); 
		toast.show(); 
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		/*********remove activity list******/
		activityManager.removeActivity(this);
	}
}
