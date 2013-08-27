package kr.re.ec.zigeon.login;

import kr.re.ec.zigeon.R;
import kr.re.ec.zigeon.handler.SoapParser;
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

public class JoinActivity extends Activity implements OnClickListener{
	private EditText id,password,nickname;
	private SoapParser soapParser;
	private String ID_check, Nickname_check; 
	private Button cancle_btn, join_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		cancle_btn = (Button)findViewById(R.id.Cancle_btu);
		cancle_btn.setOnClickListener(this);
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
			break;
		case R.id.Join_btn:
			soapParser = SoapParser.getInstance();
			String idQuery = "SELECT memPW FROM tMember WHERE memID ='" + id.getText().toString() + "'";
			ID_check = soapParser.sendQuery(idQuery);
			LogUtil.v("ID check = " + ID_check);
			LogUtil.v("ID check.compareTo(null) = " + ID_check.compareTo(""));
			String nicknameQuery = "SELECT memPW FROM tMember WHERE memNick ='" + nickname.getText().toString() + "'";
			Nickname_check = soapParser.sendQuery(nicknameQuery); 
			if(id.getText().toString().compareTo("") == 0) { //내용없으면 에러띄우고 강제 return
					
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
					}
				});
				alert.setMessage("id를 입력하세요");
				alert.show();
				return;
			} else if(ID_check.compareTo("") != 0)//이미 아이디가 있을때
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
					}
				});
				alert.setMessage("이미 사용중인 아이디입니다.");
				alert.show();
				return;
			}	
			else if(password.getText().toString().compareTo("") == 0){
			
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
					}
				});
				alert.setMessage("password를 입력하세요");
				alert.show();
				return;
			}else if(nickname.getText().toString().compareTo("") == 0){
				
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
					}
				});
				alert.setMessage("Nickname을 입력하세요");
				alert.show();
				return;
			}else if(Nickname_check.compareTo("") != 0)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
					}
				});
				alert.setMessage("이미 사용중인 닉네임입니다.");
				alert.show();
				return;
			}
			else{
			
				String str = soapParser.sendQuery("SELECT MAX(memIdx) FROM tMember"); //정상작동 확인. +1한 idx로 insert한다.
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
								",'true'" + //memIsAdmin 
						")");
				LogUtil.v("server return : "+str);
				break;
			}
		}
		Toast toast = Toast.makeText(this, "회원가입이 되셨습니다.",Toast.LENGTH_SHORT); 
		toast.show(); 
		finish();
	}
	
}
