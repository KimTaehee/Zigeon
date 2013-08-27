/**
 * Class Name: PostingWriteActivity
 * Description: Write Posting. Can attach one picture. 
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130828
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PostingWriteActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posting_write);
		LogUtil.v("onCreate invoked!");
		
		/******** Init UI ********/
		

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.posting_write, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.posting_write_action_write:
		{
			LogUtil.v("action_write clicked");
			//TODO: yo gi error part. need to fix 
			Intent intent = new Intent(this, PostingActivity.class); 
			startActivity(intent);
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}
}