package kr.re.ec.zigeon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoViewActivity extends Activity {

	private final int imgWidth = 320;
	private final int imgHeight = 372;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_view);

		
		/** 전송메시지 */
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String imgPath = extras.getString("imgPath");
		
		/** 완성된 이미지 보여주기  */
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		ImageView iv = (ImageView)findViewById(R.id.photo_view_image);
		Bitmap bm = BitmapFactory.decodeFile(imgPath, bfo);
		Bitmap resized = Bitmap.createScaledBitmap(bm, imgWidth, imgHeight, true);
		iv.setImageBitmap(resized);
		
		
	}

}