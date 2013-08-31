/*
 * LandmarkDataset
 * 130816 Kim Taehee
 * slhyvaa@nate.com
 */

package kr.re.ec.zigeon.dataset;
import java.util.Date;

import com.nhn.android.maps.maplib.NGeoPoint;

import kr.re.ec.zigeon.util.LogUtil;
import kr.re.ec.zigeon.util.Constants;


public class LandmarkDataset extends Object {
	public static final int LANDMARK_FIELD_COUNT = 12;
	
	public int idx;
	public String name;
	public double latitude;
	public double longitude;
	public String contents;
	public int like;
	public int dislike;
	public int writerIdx;
	public int readedCount;
	public Date writtenTime;
	public int undoIdx;
	public String picturePath;
	
	public LandmarkDataset() {
		
	}
	
	public LandmarkDataset(String[] strArr) {
		setDataset(strArr);
	}
	
	/**
	 * 
	 * @param strArr: MUST have format of each Dataset. from class Constants.
	 * @return errCode. from class Constants.
	 */
	public int setDataset(String[] strArr) {
		try {
			if(strArr.length == LANDMARK_FIELD_COUNT){
				idx = Integer.parseInt(strArr[0]);
				name = strArr[1];
				latitude = Double.parseDouble(strArr[2]);
				longitude = Double.parseDouble(strArr[3]);
				contents = strArr[4];
				like = Integer.parseInt(strArr[5]);
				dislike = Integer.parseInt(strArr[6]);
				writerIdx = Integer.parseInt(strArr[7]);
				readedCount = Integer.parseInt(strArr[8]);
				writtenTime = new Date(); //TODO: temp. return now.
				if (strArr[10]!=null) { //for avoid NumberFormatException
					undoIdx = Integer.parseInt(strArr[10]);
				} else {
					//LogUtil.v("convert null to int DB_NULL");
					undoIdx = Constants.INT_NULL;
				}
				picturePath = strArr[11];
				return 0;
			} else {
				LogUtil.e("wrong data input");
				return Constants.ERR_DATASET_MISMATCHED;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERR_DATASET_MISMATCHED;
		}
		
	}
	
	/*********** 
	 * @param NGeoPoint  
	 * @return meter between current LandmarkDataset and NGeoPoint
	 * 
	 */
	public double getDistance(NGeoPoint gp) {
		if (this.latitude==0.0 || this.longitude==0.0 || gp==null) { //if this has no latlog
			LogUtil.e("this.latlog or gp wasn't set. return dbl_null");
			return Constants.DOUBLE_NULL;
		} else {
			//WARN: NGeoPoint constructor is (lon, lat)! not (lat, lon)
			return NGeoPoint.getDistance(gp, new NGeoPoint(this.longitude, this.latitude)); 
		}
	}
	
	/**
	 * @param double latitude(WiDo), double longitude(GyeongDo)
	 * @return meter between current LandmarkDataset and latlog
	 */
	public double getDistance(double _latitude, double _longitude) {
		if (this.latitude==0.0 || this.longitude==0.0) { //if this has no latlog
			LogUtil.e("this.latlog didn't set. return dbl_null");
			return Constants.DOUBLE_NULL;
		} else {
			//WARN: NGeoPoint constructor is (lon, lat)! not (lat, lon)
			return NGeoPoint.getDistance(new NGeoPoint(_longitude, _latitude)
				, new NGeoPoint(this.longitude, this.latitude)); 
		}
	}
}
