/**
 * Class Name: PostingAdapter
 * Description: for show Posting on listview.
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130917
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import java.text.SimpleDateFormat;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
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

public class PostingAdapter extends BaseAdapter implements ImageLoadingListener {

	private Context context = null;
	private PostingDataset[] mPostingArr = null;

//	private TextView tvName;
//	private TextView tvDistance;
//	private ImageView ivLandmark;
//	private ImageView ivBalloon;
//	private ImageView ivGrade;
	
	/******** AUIL init ********/
	private DisplayImageOptions imgOption = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_auil_stub)	
	.showImageForEmptyUri(R.drawable.ic_auil_empty)
	.showImageOnFail(R.drawable.ic_auil_error)
	.build();
	private ImageLoader imgLoader = ImageLoader.getInstance(); //singleton
	
	
	public PostingAdapter(Context _context, PostingDataset[] PostingArr) {
		this.context = _context;
		mPostingArr = PostingArr;
		LogUtil.v("constuctor called!");
	}
	
	@Override
	public int getCount() {
		return (null != mPostingArr) ? mPostingArr.length : 0; //TODO: is this be used?
	}

	@Override
	public Object getItem(int position) {
		return mPostingArr[position];	//TODO: is this be used?
	}

	@Override
	public long getItemId(int position) {
		return position;	//TODO: is this be used?
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		//View gridView;
		PostingViewHolder holder = null; //use viewholder pattern
		
		if(convertView == null) { 
			//gridView = new View(context);
			
			convertView = inflater.inflate(R.layout.listview_item_posting, null);
			
			/*********** set contents into gridview ***********/
			holder = new PostingViewHolder();
			
			holder.tvWriterName= (TextView) convertView.findViewById(R.id.listview_item_posting_tv_writer_name);
			holder.tvPostingTitle = (TextView) convertView.findViewById(R.id.listview_item_posting_tv_title);
			holder.tvWrittenDate = (TextView) convertView.findViewById(R.id.listview_item_posting_tv_date);
			holder.ivPosting = (ImageView) convertView.findViewById(R.id.listview_item_posting_iv_picture);
			
			convertView.setTag(holder);
			
		} else {
			holder = (PostingViewHolder) convertView.getTag(); 
			//gridView = (View) convertView;
			
		}
		
		holder.tvWriterName.setText(mPostingArr[position].writerName);
		holder.tvPostingTitle.setText(mPostingArr[position].title);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		holder.tvWrittenDate.setText(sdf.format(mPostingArr[position].writtenTime));
		
		//picture that represents postings
		LogUtil.v("image uri: " + mPostingArr[position].getImageUrl());
		if(mPostingArr[position].getImageUrl() != null) {
			LogUtil.v("position: " + position +", image load start!");
			
			imgLoader.displayImage(mPostingArr[position].getImageUrl(), holder.ivPosting, this);
			//System.gc(); 	//it may cause UI frame skip but do memory free
		} else {
			imgLoader.displayImage(null, holder.ivPosting, this);
		
		}
		
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

class PostingViewHolder {
	TextView tvWriterName;
	TextView tvPostingTitle;
	TextView tvWrittenDate;
	ImageView ivPosting;

}