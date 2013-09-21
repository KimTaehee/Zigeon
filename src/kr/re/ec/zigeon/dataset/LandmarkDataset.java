/*
 * LandmarkDataset
 * 130816 Kim Taehee
 * slhyvaa@nate.com
 */

package kr.re.ec.zigeon.dataset;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nhn.android.maps.maplib.NGeoPoint;

import kr.re.ec.zigeon.util.LogUtil;
import kr.re.ec.zigeon.util.Constants;


public class LandmarkDataset extends Object {
	public static final int LANDMARK_FIELD_COUNT = 13;
	
	public int idx;
	public String name;
	public double latitude;
	public double longitude;
	public String contents;
	public double rating;
	public int ratingVotes;
	public boolean visible;
	public int writerIdx;
	public int readedCount;
	public Date writtenTime;
	public int undoIdx;
	public String picturePath;
	
	public double distanceFromCurrentLocation = Constants.DOUBLE_NULL; 
	
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
				rating = Double.parseDouble(strArr[5]);
				ratingVotes = Integer.parseInt(strArr[6]);
				visible = Boolean.parseBoolean(strArr[7]);
				writerIdx = Integer.parseInt(strArr[8]);
				readedCount = Integer.parseInt(strArr[9]);
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DB);
				writtenTime = sdf.parse(strArr[10]);
				if (strArr[11]!=null) {
					if (strArr[11].compareTo("null")!=0) { //for avoid NumberFormatException
						//LogUtil.i("undoIdx: " + strArr[11]);
						undoIdx = Integer.parseInt(strArr[11]);
					} else {
						//LogUtil.v("convert null to int DB_NULL");
						undoIdx = Constants.INT_NULL;
					}
				}

				picturePath = strArr[12];
				//LogUtil.i("PicturePath is: " + picturePath);
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
			distanceFromCurrentLocation = Constants.DOUBLE_NULL;
			
		} else {
			//WARN: NGeoPoint constructor is (lon, lat)! not (lat, lon)
			distanceFromCurrentLocation = NGeoPoint.getDistance(gp, new NGeoPoint(this.longitude, this.latitude)); 
		}
		return distanceFromCurrentLocation;
	}
	
	/**
	 * @param double latitude(WiDo), double longitude(GyeongDo)
	 * @return meter between current LandmarkDataset and latlog
	 */
	public double getDistance(double _latitude, double _longitude) {
		if (this.latitude==0.0 || this.longitude==0.0) { //if this has no latlog
			LogUtil.e("this.latlog didn't set. return dbl_null");
			distanceFromCurrentLocation = Constants.DOUBLE_NULL;
		} else {
			//WARN: NGeoPoint constructor is (lon, lat)! not (lat, lon)
			distanceFromCurrentLocation = NGeoPoint.getDistance(new NGeoPoint(_longitude, _latitude)
				, new NGeoPoint(this.longitude, this.latitude)); 
		}
		return distanceFromCurrentLocation;
	}
	
	public String getImageUrl() {
		String str = "";
		if(picturePath!=null) {
			str += Constants.URL_SERVER_IMAGE_DIR + String.valueOf(Constants.MSG_TYPE_LANDMARK) + "/" 
				+ String.valueOf(idx) + "/" + picturePath;
		} else {
			str = null;
		}
		//LogUtil.i("image url is: " + str);
		return str;
	}
	
}
