package kr.re.ec.zigeon;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class CommentAdapter extends BaseAdapter implements ImageLoadingListener {

	private Context context = null;
	//private LandmarkDataset[] mLandmarkArr = null;
	private CommentDataset[] mCommentArr = null;

	private TextView tvNickName;
	private TextView tvTime;
//	private ImageView ivLandmark;
//	private ImageView ivBalloon;
//	private ImageView ivGrade;
	
	/******** AUIL init ********/
//	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
//	.showStubImage(R.drawable.ic_auil_stub)	
//	.showImageForEmptyUri(R.drawable.ic_auil_empty)
//	.showImageOnFail(R.drawable.ic_auil_error)
//	.build();
//	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton
	
	
	public CommentAdapter(Context _context, CommentDataset[] commentArr) {
		this.context = _context;
		mCommentArr = commentArr;
		LogUtil.v("constuctor called!");
	}
	
	@Override
	public int getCount() {
		return (null != mCommentArr) ? mCommentArr.length : 0; //TODO: is this be used?
	}

	@Override
	public Object getItem(int position) {
		return mCommentArr[position];	//TODO: is this be used?
	}

	@Override
	public long getItemId(int position) {
		return position;	//TODO: is this be used?
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		//CommentView gridView;
		CommentViewHolder holder = null; //use viewholder pattern
		
		if(convertView == null) { 
			//gridView = new View(context);
			
			convertView = inflater.inflate(R.layout.gridview_item_comment, null);
			
			/*********** set contents into gridview ***********/
			holder = new CommentViewHolder();
			
			holder.tvContent= (TextView) convertView.findViewById(R.id.gridview_item_comment_tv_content);
			holder.tvNickName = (TextView) convertView.findViewById(R.id.gridview_item_comment_tv_nick_name);
			holder.tvTime = (TextView) convertView.findViewById(R.id.gridview_item_comment_tv_time);
			
			convertView.setTag(holder);
			
		} else {
			holder = (CommentViewHolder) convertView.getTag(); 
			//gridView = (View) convertView;
			
		}
		
		LogUtil.v("Landmark name: " +mCommentArr[position].contents + ", pos: " + position);
		holder.tvContent.setText(mCommentArr[position].contents);
		
		LogUtil.v("Landmark name: " +mCommentArr[position].contents + ", pos: " + position);
		holder.tvNickName.setText(mCommentArr[position].writerIdx);// TODO: soapparser
//		
//		LogUtil.v("Landmark name: " +mCommentArr[position].contents + ", pos: " + position);
//		holder.tvTime.setText(toString(mCommentArr[position].writtenTime));
//		
//		//picture that represents landmarks
//		LogUtil.v("image uri: " + mLandmarkArr[position].getImageUrl());
//		if(mLandmarkArr[position].getImageUrl() != null) {
//			LogUtil.v("position: " + position +", image load start!");
//			
//			imgLoader.displayImage(mLandmarkArr[position].getImageUrl(), holder.ivLandmark, this);
//			//System.gc(); 	//it may cause UI frame skip but do memory free
//		} else {
//			imgLoader.displayImage(null, holder.ivLandmark, this);
//		
//		}
//		
//		//grade: best, new, ... 
//		holder.ivGrade.setVisibility(ImageView.INVISIBLE);
//		//ivGrade = //TODO: gonna work
		
		return convertView;
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

class CommentViewHolder {
	TextView tvNickName;
	TextView tvTime;
	TextView tvContent;
}
