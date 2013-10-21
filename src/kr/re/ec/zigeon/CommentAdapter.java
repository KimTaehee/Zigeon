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
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.AlertManager;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	private Context context = null;
	private CommentDataset[] mCommentArr = null;
	
	private UIHandler uiHandler;
	private SoapParser soapParser;
	
	public CommentAdapter(Context _context, CommentDataset[] CommentArr) {
		this.context = _context;
		mCommentArr = CommentArr;
		
		uiHandler = UIHandler.getInstance(this.context);
		soapParser = SoapParser.getInstance();
		
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
			holder.ivDelete = (ImageView) convertView.findViewById(R.id.listview_item_comment_iv_delete);			
			
			convertView.setTag(holder);
			
		} else {
			holder = (CommentViewHolder) convertView.getTag(); 	
		}
		
		holder.tvWriterName.setText(mCommentArr[position].writerName);
		holder.tvCommentContents.setText(mCommentArr[position].contents);
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_PRINT);
		holder.tvWrittenDate.setText(sdf.format(mCommentArr[position].writtenTime));
			
		//show and operate delete comment
		MemberDataset loginMem = MemberDataset.getLoginInstance();
		if(mCommentArr[position].writerIdx == loginMem.idx || loginMem.isAdmin == true) {
			final int mPosition = position;
			holder.ivDelete.setVisibility(View.VISIBLE);
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) { //when click iv_delete
					DialogInterface.OnClickListener dialogListner = new DialogInterface.OnClickListener() { //click yes
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String query = "UPDATE tComment SET comVisible = 'False' WHERE comIdx='" 
									+ mCommentArr[mPosition].idx + "'"; 
							LogUtil.v("data request. " + query);
							
							String result = soapParser.sendQuery(query);
							uiHandler.sendMessage(Constants.MSG_TYPE_REFRESH, "",null);
							if(result != null){
								LogUtil.v("result : " + result);
							} else {
								LogUtil.v("result is null");
							}
						}
					};
					
					LogUtil.v("ivDelete clicked. position: " + mPosition);
					new AlertManager().show(context, "댓글을 지웁니다. 계속할까요?", "확인"
							, Constants.ALERT_YES_NO, dialogListner);
					
				} 
			});
		} else {
			holder.ivDelete.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
}

class CommentViewHolder {
	TextView tvWriterName;
	TextView tvCommentContents;
	TextView tvWrittenDate;
	ImageView ivDelete;
}