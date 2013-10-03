/**
 * Class Name: LandmarkAdapter
 * Description: for show Landmarks on gridview.
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130909
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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
	private LayoutInflater mInflater;
//	private TextView tvName;
//	private TextView tvDistance;
//	private ImageView ivLandmark;
//	private ImageView ivBalloon;
//	private ImageView ivGrade;
	
	private int imgBalloon[] = {
			R.drawable.ic_best_balloon0,
			R.drawable.ic_best_balloon1,
			R.drawable.ic_best_balloon2,
			R.drawable.ic_best_balloon3,
			R.drawable.ic_best_balloon4,
			R.drawable.ic_best_balloon5
	};
	
	/******** AUIL init ********/
	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_auil_stub)	
	.showImageForEmptyUri(R.drawable.ic_auil_empty)
	.showImageOnFail(R.drawable.ic_auil_error)
	.build();
	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton
	
	
	public LandmarkAdapter(Context _context, ArrayList<LandmarkDataset> landmarkArl) {
		this.context = _context;
		mLandmarkArr = (LandmarkDataset[]) landmarkArl.toArray(new LandmarkDataset[0]);
		//mLandmarkArr = landmarkArr;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LogUtil.v("constuctor called!");
	}
	
	public void updateLandmarkList(ArrayList<LandmarkDataset> landmarkArl) {
		mLandmarkArr = (LandmarkDataset[]) landmarkArl.toArray(new LandmarkDataset[0]);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return (null != mLandmarkArr) ? mLandmarkArr.length : 0; //TODO: is this be used?
	}

	@Override
	public LandmarkDataset getItem(int position) {
		return mLandmarkArr[position];	//TODO: is this be used?
	}

	@Override
	public long getItemId(int position) {
		return position;	//TODO: is this be used?
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		//View gridView;
		ViewHolder holder = null; //use viewholder pattern
		
		if(convertView == null) { 
			//gridView = new View(context);
			
			convertView = mInflater.inflate(R.layout.gridview_item_best_list, null);
			
			/*********** set contents into gridview ***********/
			holder = new ViewHolder();
			
			holder.tvName= (TextView) convertView.findViewById(R.id.gridview_item_best_list_tv_name);
			holder.tvDistance = (TextView) convertView.findViewById(R.id.gridview_item_best_list_tv_distance);
			holder.ivLandmark = (ImageView) convertView.findViewById(R.id.gridview_item_best_list_iv_landmark);
			holder.ivGrade = (ImageView) convertView.findViewById(R.id.gridview_item_best_list_iv_grade);
			holder.ivBalloon = (ImageView) convertView.findViewById(R.id.gridview_item_best_list_iv_balloon);
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag(); 
			//gridView = (View) convertView;
			
		}
		
		LogUtil.v("Landmark name: " + mLandmarkArr[position].name + ", pos: " + position);
		holder.tvName.setText(mLandmarkArr[position].name);
		
		int distanceFromMe = (int)mLandmarkArr[position].distanceFromCurrentLocation;
		holder.tvDistance.setText((distanceFromMe==Constants.INT_NULL)?"wait...":distanceFromMe + " m");
		
		//picture that represents landmarks
		//LogUtil.v("image uri: " + mLandmarkArr[position].getImageUrl());
		if(mLandmarkArr[position].getImageUrl() != null) {
			//LogUtil.v("position: " + position +", image load start!");
			
			imgLoader.displayImage(mLandmarkArr[position].getImageUrl(), holder.ivLandmark, this);
			//System.gc(); 	//it may cause UI frame skip but do memory free
		} else {
			imgLoader.displayImage(null, holder.ivLandmark, this);
		
		}
		
		int balloonIdx = (int) Math.round(mLandmarkArr[position].rating);
		//LogUtil.i("num of balloon: " + balloonIdx);
		
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), imgBalloon[balloonIdx]);
		
		if(balloonIdx==0) {
			balloonIdx = 1;
		}
		
		bmp = Bitmap.createScaledBitmap(bmp, 28*balloonIdx, 37, false);
		holder.ivBalloon.setImageBitmap(bmp);
		
		//grade: best, new, ... 
		holder.ivGrade.setVisibility(ImageView.INVISIBLE);
		//ivGrade = //TODO: gonna work
		
		return convertView;
	}

	@Override
	public void onLoadingStarted(String arg0, View v) {
		//LogUtil.v("invoked!");
	}

	@Override
	public void onLoadingFailed(String arg0, View v, FailReason arg2) {
		LogUtil.e("invoked! failreason: " + arg2.toString());
	}

	@Override
	public void onLoadingComplete(String arg0, View v, Bitmap bmp) {
		//LogUtil.v("Image onLoadingComplete!");
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

class ViewHolder {
	TextView tvName;
	TextView tvDistance;
	ImageView ivLandmark;
	ImageView ivBalloon;
	ImageView ivGrade;
}