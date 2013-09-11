/**
 * Author newcho
 * first created date: 130830
 */

package kr.re.ec.zigeon;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import kr.re.ec.zigeon.util.LogUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PhotoViewActivity extends Activity implements ImageLoadingListener {

//	private final int imgWidth = 320;
//	private final int imgHeight = 372;

	private ImageView iv;

	/******** AUIL init ********/
	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_auil_stub)	
	.showImageForEmptyUri(R.drawable.ic_auil_empty)
	.showImageOnFail(R.drawable.ic_auil_error)
	.build();
	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_view);


		/** receive intent */
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String imgPath = extras.getString("imgPath");

		if(imgPath==null) { 
			LogUtil.w("null imgPath. call finish()");
			finish();
			return;
		}

		/** show image from path  */
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		iv = (ImageView)findViewById(R.id.photo_view_image);

		LogUtil.v("image load start! uri: " + imgPath);
		imgLoader.loadImage(imgPath, PhotoViewActivity.this); //load landmark image


		//Bitmap bm = BitmapFactory.decodeFile(imgPath, bfo);

//		/** calc and resize image to fit screen **/
//		Point displaySize = new Point();
//		getWindowManager().getDefaultDisplay().getSize(displaySize);
//		int scaledY = displaySize.x * bm.getHeight() / bm.getWidth(); //calc height
//		
//		LogUtil.v("display w, h, scaledY: " + displaySize.x + ", " + displaySize.y + ", " + scaledY);
//		
//		bm = Bitmap.createScaledBitmap(bm, displaySize.x, scaledY, true);
//		
//		iv.setImageBitmap(bm);	
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub
		iv.setImageResource(R.drawable.ic_auil_stub);
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub
		iv.setImageResource(R.drawable.ic_auil_error);
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		// TODO Auto-generated method stub
		LogUtil.v("Image onLoadingComplete!");
		iv.setImageBitmap(arg2);
	}

	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub
		iv.setImageResource(R.drawable.ic_auil_error);
	}
}