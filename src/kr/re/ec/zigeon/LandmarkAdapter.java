/**
 * Class Name: LandmarkAdapter
 * Description: for show Landmarks on gridview.
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130909
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class LandmarkAdapter extends BaseAdapter implements ImageLoadingListener {

	private Context context = null;
	private LandmarkDataset[] mLandmarkArr = null;

	private TextView tvName;
	private TextView tvDistance;
	private ImageView ivLandmark;
	private ImageView ivBalloon;
	private ImageView ivGrade;
	
	/******** AUIL init ********/
	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_auil_stub)	
	.showImageForEmptyUri(R.drawable.ic_auil_empty)
	.showImageOnFail(R.drawable.ic_auil_error)
	.build();
	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton
	
	
	public LandmarkAdapter(Context _context, LandmarkDataset[] landmarkArr) {
		this.context = _context;
		mLandmarkArr = landmarkArr;
		LogUtil.v("constuctor called!");
	}
	
	@Override
	public int getCount() {
		return (null != mLandmarkArr) ? mLandmarkArr.length : 0; //TODO: is this be used?
	}

	@Override
	public Object getItem(int position) {
		return mLandmarkArr[position];	//TODO: is this be used?
	}

	@Override
	public long getItemId(int position) {
		return position;	//TODO: is this be used?
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View gridView;
		
		if(convertView == null) { 
			gridView = new View(context);
			
			gridView = inflater.inflate(R.layout.gridview_item_best_list, null);
			
			/*********** set contents into gridview ***********/
			tvName = (TextView) gridView.findViewById(R.id.gridview_item_best_list_tv_name);
			tvName.setText(mLandmarkArr[position].name);
			
			tvDistance = (TextView) gridView.findViewById(R.id.gridview_item_best_list_tv_distance);
			
			int distanceFromMe = (int)mLandmarkArr[position].distanceFromCurrentLocation;
			tvDistance.setText((distanceFromMe==Constants.INT_NULL)?"wait...":distanceFromMe + " m");
			
			//picture that represents landmarks
			ivLandmark = (ImageView) gridView.findViewById(R.id.gridview_item_best_list_iv_landmark);
			
			LogUtil.v("image uri: " + mLandmarkArr[position].getImageUrl());
			if(mLandmarkArr[position].getImageUrl() != null) {
				LogUtil.v("image load start!");
				
				imgLoader.displayImage(mLandmarkArr[position].getImageUrl(), ivLandmark, this);
				//System.gc(); 	//it may cause UI frame skip but do memory free
			}
			//grade: best, new, ... 
			ivGrade = (ImageView) gridView.findViewById(R.id.gridview_item_best_list_iv_grade);
			ivGrade.setVisibility(ImageView.INVISIBLE);
			//ivGrade = //TODO: gonna work
			
			ivBalloon = (ImageView) gridView.findViewById(R.id.gridview_item_best_list_iv_balloon);
			//TODO: gonna work
			
			
		} else {
			gridView = (View) convertView;
			
		}
		return gridView;
	}

	@Override
	public void onLoadingStarted(String arg0, View v) {
		LogUtil.v("invoked!");
	}

	@Override
	public void onLoadingFailed(String arg0, View v, FailReason arg2) {
		LogUtil.e("invoked! failreason: " + arg2.toString());
	}

	@Override
	public void onLoadingComplete(String arg0, View v, Bitmap bmp) {
		LogUtil.v("Image onLoadingComplete!");
	}

	@Override
	public void onLoadingCancelled(String arg0, View v) {
		LogUtil.i("invoked!");
		if(v != null) {

		} else {
			LogUtil.e("View is null!!!");	//may occured by GC freed memory.
		}
		
	}
}
