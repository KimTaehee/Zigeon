package kr.re.ec.zigeon.login;

/**
 * LoginActivity
 * 130821 kim ji hong
 * */
import kr.re.ec.zigeon.BubbleActivity;
import kr.re.ec.zigeon.R;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private EditText id, password;
	private Button login, join;
	private Intent intent;
	private String strID;
	private String strPassword;
	private SoapParser soapParser;
	private CheckBox autoCheckBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(kr.re.ec.zigeon.R.layout.activity_login);
		id = (EditText) findViewById(R.id.Login_Id);
		password = (EditText) findViewById(R.id.Login_Password);
		login = (Button) findViewById(R.id.Login_Button);
		join = (Button) findViewById(R.id.Join_Button);
		soapParser = SoapParser.getInstance();
		autoCheckBox = (CheckBox) findViewById(R.id.Autologin_Box);
		
		// 자동로그인을 위한 Shared Preference를 불러옵니다.
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
		
		// 저장된 값들을 불러옵니다.
		String auto_ID = pref.getString("ID","");
		String auto_password = pref.getString("Password", "");
		Boolean auto_check = pref.getBoolean("AutoCheck",false);
		
		autoCheckBox.setChecked(auto_check);
		if(autoCheckBox.isChecked())
		{
			id.setText(auto_ID);
			password.setText(auto_password);
			login.performClick();
		}
		
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// TODO 데이터 베이스 에서 받아서 패스워드 체크정도만? 해봅시다
				/*
				 * String response = null; try { response =
				 * CustomHttpClient.executeHttpPost( "<target page url>",
				 * postParameters); String res = response.toString(); res =
				 * res.replaceAll("\\s+", ""); if (res.equals("1"))
				 * error.setText("Correct Username or Password"); else
				 * error.setText("Sorry!! Incorrect Username or Password"); }
				 * catch (Exception e) { un.setText(e.toString()); }
				 */
				
				LogUtil.v("onClick");
				strID = id.getText().toString();// TODO: user ID;
				LogUtil.v(id.getText().toString());
				String idQuery = "SELECT memPW FROM tMember WHERE memID ='"
						+ strID + "'";
				// uiHandler.sendMessage(Constants.MSG_TYPE_MEMBER, "",
				LogUtil.v("쿼리문 생성");
				
				strPassword = soapParser.sendQuery(idQuery);
				LogUtil.v(strPassword);
				LogUtil.v(password.getText().toString());
				if (strPassword.compareTo("")!=0)// 아이디가 있다면
				{
					if (password.getText().toString().compareTo(strPassword)==0) {
						intent = new Intent(LoginActivity.this,
								BubbleActivity.class);
						startActivity(intent);

						finish(); /* 로그인이 성공하면 뒤돌아가기를 눌럿을때 로그인화면으로 돌아가지 않도록 종료합니다. */
					} else {
						// 비밀번호 틀림
						AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
						alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();	
							}
						});
						alert.setMessage("비밀번호를 잘못입력하셨습니다.");
						alert.show();
						return;
					}
				} else {
					// 아이디없음
					AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();	
						}
					});
					alert.setMessage("아이디가 없습니다.");
					alert.show();
					return;
				}
			}
		});

		join.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent = new Intent(LoginActivity.this, JoinActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	public void onStop(){ //activity가 종료될때?
		super.onStop();
		//상태를 저장합니다.
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
		SharedPreferences.Editor editor = pref.edit(); //Editor 불러오기
		
		//저장할 값들
		id = (EditText) findViewById(R.id.Login_Id);
		password = (EditText) findViewById(R.id.Login_Password);
		autoCheckBox = (CheckBox) findViewById(R.id.Autologin_Box);
		
		//값을 입력합니다
		editor.putString("ID", id.getText().toString());
		editor.putString("Password", password.getText().toString());
		editor.putBoolean("AutoCheck", autoCheckBox.isChecked());
		
		editor.commit();//저장합니다.
	}
}
