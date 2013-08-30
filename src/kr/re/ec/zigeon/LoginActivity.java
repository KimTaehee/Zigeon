﻿package kr.re.ec.zigeon;

/**
 * LoginActivity
 * 130821 kim ji hong
 * */
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
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
		login = (Button) findViewById(R.id.Login_Button);
		join = (Button) findViewById(R.id.Join_Button);
		soapParser = SoapParser.getInstance();
		autoCheckBox = (CheckBox) findViewById(R.id.Autologin_Box);
		mMemberDataset = MemberDataset.getInstance();
		
		//  To Auto Login, get Shared Preference
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
		
		// load from saved values
		String auto_ID = pref.getString("ID","");
		String auto_password = pref.getString("Password", "");
		Boolean auto_check = pref.getBoolean("AutoCheck",false);
		
		autoCheckBox.setChecked(auto_check);
		if(autoCheckBox.isChecked())
		{
			id.setText(auto_ID);
			password.setText(auto_password);
			//login.performClick(); //TODO: Click event not perform
		}
		
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
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
								BubbleActivity.class);
						startActivity(intent);

						finish(); /* If login successed, pressing back button means finish app. (not loginActivity) */
					} else {
						// Wrong Password
						AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
						alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();	
							}
						});
						alert.setMessage("Wrong Password.");
						alert.show();
						return;
					}
				} else {
					// No matched ID
					AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
					alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();	
						}
					});
					alert.setMessage("There is no matched ID");
					alert.show();
					return;
				}
				
				
			}
		});

		join.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	public void onStop(){ //when activity onStop(invisible)
		super.onStop();
		//save status
		SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE); 
		SharedPreferences.Editor editor = pref.edit(); //load Editor
		
		//values to save
		id = (EditText) findViewById(R.id.Login_Id);
		password = (EditText) findViewById(R.id.Login_Password);
		autoCheckBox = (CheckBox) findViewById(R.id.Autologin_Box);
		
		//input values
		editor.putString("ID", id.getText().toString());
		editor.putString("Password", password.getText().toString());
		editor.putBoolean("AutoCheck", autoCheckBox.isChecked());
		
		editor.commit();	//save
	}
}
