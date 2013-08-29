/**
 * 130828 kim ji hong
 * memberDataset
 */
package kr.re.ec.zigeon.dataset;

import java.util.Date;

import android.content.Context;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.LogUtil;

public class MemberDataset extends Object {
	public static final int MEMBER_FIELD_COUNT = 9;
	
	public int idx;
	public String id;
	public String pw;
	public String nick;
	public int exp;
	public boolean isFacebook;
	public Date regTime;
	public Date lastAccessTime;
	public boolean isAdmin;
	
	private static MemberDataset instance; //for singleton
	
	private MemberDataset() {
		
	}	
	
	public static MemberDataset getInstance(){
		if(instance==null) {
			instance = new MemberDataset(); 
		}
		return instance;
	}
	
	public void setDataset(String[] strArr) {
		if(strArr.length == MEMBER_FIELD_COUNT){
			idx = Integer.parseInt(strArr[0]);
			id = strArr[1]; 
			pw = strArr[2];
			nick = strArr[3];
			exp = Integer.parseInt(strArr[4]);
			isFacebook = Boolean.parseBoolean(strArr[5]);
			regTime = new Date(); //TODO: temporary.
			lastAccessTime = new Date(); //TODO: temporary.
			isAdmin = Boolean.parseBoolean(strArr[8]);
		} else {
			LogUtil.e("wrong data input");
		}
	}
	public void setDataset(MemberDataset dataset) {
		//LogUtil.i("dataset.id: " + dataset.id);
		instance = dataset;
		//LogUtil.i("instance.id: " + instance.id);		 
	}
}


