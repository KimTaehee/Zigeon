package kr.re.ec.zigeon;

<<<<<<< HEAD
import kr.re.ec.zigeon.dataset.PreferenceDataset;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

	private Intent intent;
	private Button testBtn;
<<<<<<< HEAD
	private Button testBtn2;
	private SharedPreferences pref;
	private PreferenceDataset preference;
=======
	private SharedPreferences pref;
>>>>>>> preference data load test OK
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
<<<<<<< HEAD
		testBtn = (Button) findViewById(R.id.test_btn1);
		testBtn2 = (Button) findViewById(R.id.test_btn2);
		
		preference = PreferenceDataset.getInstance(this);
		preference.setPreference(this);
		
		if(preference.sharedPref.getBoolean(PreferenceDataset.CHECKBOX_SOUND,false))
		{
			testBtn.setText("Checked");
		} else {
			testBtn.setText("not Checked");	
		}
		
		
		/*
		PreferenceManager.setDefaultValues(TestActivity.this, R.xml.preference,
				false);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getBoolean("preference_CheckBox_notificationSound", false)) {
			testBtn.setText("Checked");
		} else {
			testBtn.setText("not Checked");
		}*/
		testBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(TestActivity.this, PreferenceActivity.class);
				startActivity(intent);

			}
		});
		
		testBtn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(TestActivity.this, TestActivity2.class);
				startActivity(intent);
				
			}
		});

=======
		/*
		getFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content,
						new kr.re.ec.zigeon.PreferenceFrag()).commit();
*/
		
		
		PreferenceManager.setDefaultValues(TestActivity.this, R.xml.preference,
				false);
		  LogUtil.v("1");
		  testBtn = (Button)findViewById(R.id.test_btn1);
		  LogUtil.v("2");
		  pref = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
		  LogUtil.v("test=" + pref.getBoolean("preference_CheckBox_notificationSound", false));
		  if(pref.getBoolean("preference_CheckBox_notificationSound", false))
		  {
			  LogUtil.v("3");
			  testBtn.setText("Checked");
		  }else
		  {
			  testBtn.setText("not Checked");
		  }
		  LogUtil.v("5");
		  testBtn.setOnClickListener(new OnClickListener() {
		  
		  @Override public void onClick(View v) {
			  intent = new Intent(TestActivity.this,
						PreferenceActivity.class);
				startActivity(intent);
		 
		  } });
		 
>>>>>>> preference data load test OK
	}
	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * addPreferencesFromResource(R.xml.activity_preference); }
	 */
=======
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TestActivity extends PreferenceActivity{
/*
	private Button testBtn;
	private Intent mIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		testBtn = (Button)findViewById(R.id.test_btn1);
		testBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIntent = new Intent(TestActivity.this, PreferenceActivity.class);
				startActivity(mIntent);
			}
		});
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_preference);
	}
>>>>>>> test... why not? SSANG
}
