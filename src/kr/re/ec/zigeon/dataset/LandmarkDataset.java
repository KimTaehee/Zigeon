/*
 * Landmark Dataset
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
	
	public void setDataset(String[] strArr) {
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
			writtenTime = new Date(); //TODO: 임시로 현재 시간 반영.
			if (strArr[10]!=null) { //NumberFormatException 피하기 위함
				undoIdx = Integer.parseInt(strArr[10]);
			} else {
				//LogUtil.v("convert null to int DB_NULL");
				undoIdx = Constants.INT_NULL;
			}
			picturePath = strArr[11];
		} else {
			LogUtil.e("wrong data input");
		}
	}
	
	/*********** 
	 * NGeoPoint나 double latitude위도, double longitude경도를 입력받아
	 * 현재 LandmarkDataset과의 거리를 double(meter)로 반환한다.
	 * 130818 function check done.
	 *  ***********/
	public double getDistance(NGeoPoint gp) {
		if (this.latitude==0.0 || this.longitude==0.0 || gp==null) { //this나 gp가 세팅되지 않은 상태라면
			LogUtil.e("this.latlog or gp wasn't set. return dbl_null");
			return Constants.DOUBLE_NULL;
		} else {
			//NGeoPoint constructor인자는 lon, lat 순서임에 유의하라!
			return NGeoPoint.getDistance(gp, new NGeoPoint(this.longitude, this.latitude)); 
		}
	}
	
	/***** 위도latitude, 경도longitude ****/
	public double getDistance(double _latitude, double _longitude) {
		if (this.latitude==0.0 || this.longitude==0.0) { //this가 세팅되지 않은 상태라면
			LogUtil.e("this.latlog didn't set. return dbl_null");
			return Constants.DOUBLE_NULL;
		} else {
			//NGeoPoint constructor인자는 lon, lat 순서임에 유의하라!
			return NGeoPoint.getDistance(new NGeoPoint(_longitude, _latitude)
				, new NGeoPoint(this.longitude, this.latitude)); 
		}
	}
}
