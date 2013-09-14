package kr.re.ec.zigeon;

/**
 * LoginActivity
 * 130821 kim ji hong
 * */
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract.Instances;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

public class LoginActivity extends Activity implements OnClickListener {
	private EditText id, password;
	private ImageButton imgbtnLogin, imgbtnRegister;
	private Switch swtAutoLogin;
	private Intent intent;
	private String strID;
	private String strPassword;
	private SoapParser soapParser;
	private MemberDataset mMemberDataset; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(kr.re.ec.zigeon.R.layout.activity_login);

		//call Intro Activity from here
//		LogUtil.v("call IntroActivity");
//		startActivity(new Intent(this,IntroActivity.class)); 
		
		id = (EditText) findViewById(R.id.Login_Id);
		password = (EditText) findViewById(R.id.Login_Password);
		imgbtnLogin = (ImageButton) findViewById(R.id.login_imgbtn_login);
		imgbtnRegister = (ImageButton) findViewById(R.id.login_imgbtn_register);
		soapParser = SoapParser.getInstance();
		swtAutoLogin = (Switch) findViewById(R.id.login_switch_autologin);
		mMemberDataset = MemberDataset.getInstance();
		
		//  To Auto Login, get Shared Preference
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
		SharedPreferences id_pref = getSharedPreferences("id_pref", Activity.MODE_PRIVATE);
		
		// load from saved values
		String auto_ID = pref.getString("ID","");
		String auto_password = pref.getString("Password", "");
		Boolean auto_check = pref.getBoolean("AutoCheck",false);
		
		swtAutoLogin.setChecked(auto_check);
		if(swtAutoLogin.isChecked())
		{
			id.setText(auto_ID);
			password.setText(auto_password);
			//login.performClick(); //TODO: Click event not perform
		}
		
		imgbtnLogin.setOnClickListener(this);
		imgbtnRegister.setOnClickListener(this);			
		
		
	}
	
	public void onStop(){ //when activity onStop(invisible)
		super.onStop();
		//save status
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
		SharedPreferences.Editor editor = pref.edit(); //load Editor
		
		//values to save
		id = (EditText) findViewById(R.id.Login_Id);
		password = (EditText) findViewById(R.id.Login_Password);
		swtAutoLogin = (Switch) findViewById(R.id.login_switch_autologin);
		
		//input values
		editor.putString("ID", id.getText().toString());
		editor.putString("Password", password.getText().toString());
		editor.putBoolean("AutoCheck", swtAutoLogin.isChecked());
		
		editor.commit();	//save
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.login_imgbtn_login:
		{
			LogUtil.v("onClick");
			strID = id.getText().toString();// TODO: user ID;
			LogUtil.v(id.getText().toString());
			
			String idQuery = "SELECT memPW FROM tMember WHERE memID ='"
					+ strID + "'";
			LogUtil.v("query created");
			
			strPassword = soapParser.sendQuery(idQuery);
			LogUtil.v(strPassword);
			LogUtil.v(password.getText().toString());
			if (strPassword.compareTo("")!=0)// if have ID
			{
				if (password.getText().toString().compareTo(strPassword)==0) {
					
					//save to dataset
					String query = "SELECT * FROM tMember WHERE memID='" + strID + "'"; 
					LogUtil.v("data request. " + query);

					mMemberDataset.setDataset(((MemberDataset[]) soapParser.getSoapData(query,
							Constants.MSG_TYPE_MEMBER))[0]);
					
					/******************** dataset test *******************/
					LogUtil.v("MemberDataset ID=" + mMemberDataset.id);
					

					intent = new Intent(LoginActivity.this,
							BestListActivity.class);
					startActivity(intent);

					finish(); /* If login successed, pressing back button means finish app. (not loginActivity) */
				} else {
					// Wrong Password
					new AlertManager(this,"Wrong Password? ^^","Confirm");
					return;
				}
			} else {
				// No matched ID
				new AlertManager(this,"There is no matched ID? ^^","Confirm");
				
				return;
			}
			break;
			
		}
		case R.id.login_imgbtn_register:
		{
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			break;
		}
		}
		
	}
}
