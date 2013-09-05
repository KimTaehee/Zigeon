package kr.re.ec.zigeon.dataset;
import java.util.Date;

import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;

/*
 * Posting Dataset
 * 130816 Kim Taehee
 * slhyvaa@nate.com
 */
public class PostingDataset extends Object {
	public static final int POSTING_FIELD_COUNT = 10;
	
	public int idx;
	public String title;
	public int parentIdx;
	public String contents;
	public int like;
	public int dislike;
	public int writerIdx;
	public int readedCount;
	public Date writtenTime;
	public String picturePath;
	
	public PostingDataset() {
		
	}
	
	public PostingDataset(String[] strArr) {
		setDataset(strArr);
	}
	
	/**
	 * 
	 * @param strArr: MUST have format of each Dataset. from class Constants.
	 * @return errCode. from class Constants
	 */
	public int setDataset(String[] strArr) {
		try {
			if(strArr.length == POSTING_FIELD_COUNT){
//				LogUtil.v("test: postingdataset setDataset 진입. strArr[]:"
//						+ strArr[0] + " "+ strArr[1] + " "+ strArr[2] + " "+ strArr[3] + " "+ strArr[4] + " "
//						+ strArr[5] + " "+ strArr[6] + " "+ strArr[7] + " "+ strArr[8] + " "+ strArr[9] + " ");
				idx = Integer.parseInt(strArr[0]);
				title = strArr[1];
				parentIdx = Integer.parseInt(strArr[2]);
				contents = strArr[3];
				like = Integer.parseInt(strArr[4]);
				dislike = Integer.parseInt(strArr[5]);
				writerIdx = Integer.parseInt(strArr[6]);
				readedCount = Integer.parseInt(strArr[7]);
				writtenTime = new Date(); //TODO: temporary.
				picturePath = strArr[9];
				return 0; // no error
			} else {
				LogUtil.e("wrong data input");
				return Constants.ERR_DATASET_MISMATCHED; 
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERR_DATASET_MISMATCHED;
		}

	}
	public String getImageUrl() {
		String str = "";
		if(picturePath!=null) {
			str += Constants.URL_SERVER_IMAGE_DIR + String.valueOf(Constants.MSG_TYPE_POSTING) + "/" 
				+ String.valueOf(idx) + "/" + picturePath;
		} else {
			str = null;
		}
		return str;
	}
	
}
