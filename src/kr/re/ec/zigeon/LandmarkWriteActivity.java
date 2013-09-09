package kr.re.ec.zigeon;

import java.io.File;

import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class LandmarkWriteActivity extends Activity implements OnClickListener{
	
	EditText edtTitle;
	EditText edtContents;
	ImageView imgInput;
	
	SoapParser soapParser;
	
	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
    private final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private String fileManagerString;
	
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(kr.re.ec.zigeon.R.layout.activity_landmark_write);
		LogUtil.v("onCreate invoked!");
		
		/******** Init UI ********/
		edtTitle = (EditText) findViewById(R.id.landmark_write_edt_title);
		edtContents = (EditText) findViewById(R.id.landmark_write_edt_contents);
		imgInput = (ImageView) findViewById(R.id.landmark_write_img_input);
		imgInput.setOnClickListener(this);

		
		/******** Init Handler *******/
		soapParser = SoapParser.getInstance();
		
		final Button btn = (Button) findViewById(R.id.landmark_write_btn_map);

		btn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				startActivity(new Intent(LandmarkWriteActivity.this,MapActivity.class));
				overridePendingTransition(0, 0); //no switching animation
			}
		});
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landmark_write, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){ //action bar or menu clicked
		switch(item.getItemId()) {
		case R.id.landmark_write_action_write:
		{
			LogUtil.v("action_write_landmark clicked");
			//TODO: here is error part. need to fix 
			Intent intent = new Intent(this, LandmarkActivity.class); 
			startActivity(intent);
			overridePendingTransition(0, 0); //no switching animation
			break;
		}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.landmark_write_img_input:
		{
			LogUtil.v("img_input clicked.");
			
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE);
			
			break;
		}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                fileManagerString = selectedImageUri.getPath();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                //DEBUG PURPOSE - you can delete this if you want
                if(selectedImagePath!=null) {
                    LogUtil.v("selectedImagePath: " + selectedImagePath);
                } else {
                	LogUtil.v("selectedImagePath is null");
                }
                if(fileManagerString!=null) {
                	LogUtil.v("fileManagerString: " + fileManagerString);
                } else {
                	LogUtil.v("filemanagerstring is null");
                }

                //NOW WE HAVE OUR WANTED STRING
                if(selectedImagePath!=null) {
                	LogUtil.v("selectedImagePath is the right one for you!");
                } else {
                	LogUtil.v("filemanagerstring is the right one for you!");
                }
                
                //path to imageview
                File imgFile = new File(selectedImagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgInput.setImageBitmap(bitmap);
            }
            
        }
    }
	
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null); 	//deprecated func used!
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
}
