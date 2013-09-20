/**
 * Class Name: CommentAdapter
 * Description: for show Comments on listview.
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130920
 * Modified Date: 
 */

package kr.re.ec.zigeon;

import java.text.SimpleDateFormat;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import kr.re.ec.zigeon.dataset.CommentDataset;
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

public class CommentAdapter extends BaseAdapter {

	private Context context = null;
	private CommentDataset[] mCommentArr = null;

	public CommentAdapter(Context _context, CommentDataset[] CommentArr) {
		this.context = _context;
		mCommentArr = CommentArr;
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
		
		//View gridView;
		CommentViewHolder holder = null; //use viewholder pattern
		
		if(convertView == null) { 
			//gridView = new View(context);
			
			convertView = inflater.inflate(R.layout.listview_item_comment, null);
			
			/*********** set contents into gridview ***********/
			holder = new CommentViewHolder();
			
			holder.tvWriterName= (TextView) convertView.findViewById(R.id.listview_item_comment_tv_writer_name);
			holder.tvCommentContents = (TextView) convertView.findViewById(R.id.listview_item_comment_tv_contents);
			holder.tvWrittenDate = (TextView) convertView.findViewById(R.id.listview_item_comment_tv_date);
			
			convertView.setTag(holder);
			
		} else {
			holder = (CommentViewHolder) convertView.getTag(); 
			//gridView = (View) convertView;
			
		}
		
		holder.tvWriterName.setText(mCommentArr[position].writerName);
		holder.tvCommentContents.setText(mCommentArr[position].contents);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		holder.tvWrittenDate.setText(sdf.format(mCommentArr[position].writtenTime));
			
		return convertView;
	}
}

class CommentViewHolder {
	TextView tvWriterName;
	TextView tvCommentContents;
	TextView tvWrittenDate;

}