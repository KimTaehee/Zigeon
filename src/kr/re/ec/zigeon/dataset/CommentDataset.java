package kr.re.ec.zigeon.dataset;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.re.ec.zigeon.handler.SoapParser;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;

/*
 * Comment Dataset
 * 130816 Kim Taehee
 * slhyvaa@nate.com
 */
public class CommentDataset extends Object {
	public static final int COMMENT_FIELD_COUNT = 10;
	
	public int idx;
	public char parentType;
	public int parentIdx;
	public String contents;
	public int like;
	public int dislike;
	public int writerIdx;
	public Date writtenTime;
	public String picturePath;
	public boolean visible;
	
	public String writerName;
	
	private SoapParser soapParser;
	
	public CommentDataset() {
		
	}
	
	public CommentDataset(String[] strArr) {
		setDataset(strArr);
	}
	
	public int setDataset(String[] strArr) {
		try {
			if(strArr.length == COMMENT_FIELD_COUNT){
				idx = Integer.parseInt(strArr[0]);
				parentType = strArr[1].charAt(0); //TODO: test해봐야 함
				parentIdx = Integer.parseInt(strArr[2]);
				contents = strArr[3];
				like = Integer.parseInt(strArr[4]);
				dislike = Integer.parseInt(strArr[5]);
				writerIdx = Integer.parseInt(strArr[6]);
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DB);
				writtenTime = sdf.parse(strArr[7]);
				picturePath = strArr[8];
				visible = Boolean.parseBoolean(strArr[9]);
				
				writerName = getWriterName();
				
				return 0;
			} else {
				LogUtil.e("wrong data input");
				return Constants.ERR_DATASET_MISMATCHED;
			}
		} catch (Exception e) {
			LogUtil.e(e.toString());
			return Constants.ERR_DATASET_MISMATCHED;
		}
		
	}
	
	public String getWriterName() {
		
		String query = "SELECT * FROM tMember WHERE memIdx='" + writerIdx + "'"; 
		LogUtil.v("data request. " + query);
		
		soapParser = SoapParser.getInstance();
		MemberDataset[] mem = (MemberDataset[]) soapParser.getSoapData(query, Constants.MSG_TYPE_MEMBER);
		writerName = mem[0].nick; 
		return writerName;
	}
}
