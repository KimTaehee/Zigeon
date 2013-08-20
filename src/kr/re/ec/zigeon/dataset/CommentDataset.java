package kr.re.ec.zigeon.dataset;
import java.util.Date;
import kr.re.ec.zigeon.util.LogUtil;

/*
 * Comment Dataset
 * 130816 Kim Taehee
 * slhyvaa@nate.com
 */
public class CommentDataset extends Object {
	public static final int COMMENT_FIELD_COUNT = 9;
	
	public int idx;
	public char parentType;
	public int parentIdx;
	public String contents;
	public int like;
	public int dislike;
	public int writerIdx;
	public Date writtenTime;
	public String picturePath;
	
	public CommentDataset() {
		
	}
	
	public CommentDataset(String[] strArr) {
		setDataset(strArr);
	}
	
	public void setDataset(String[] strArr) {
		if(strArr.length == COMMENT_FIELD_COUNT){
			idx = Integer.parseInt(strArr[0]);
			parentType = strArr[1].charAt(0); //TODO: test해봐야 함
			parentIdx = Integer.parseInt(strArr[2]);
			contents = strArr[3];
			like = Integer.parseInt(strArr[4]);
			dislike = Integer.parseInt(strArr[5]);
			writerIdx = Integer.parseInt(strArr[6]);
			writtenTime = new Date(); //TODO: temporary.
			picturePath = strArr[8];
		} else {
			LogUtil.e("wrong data input");
		}
	}
}
