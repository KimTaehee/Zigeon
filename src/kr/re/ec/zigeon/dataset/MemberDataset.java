/**
 * 130828 kim ji hong
 * memberDataset
 * usage: loginInstance is data about logged in user, or also can create instance such as 
 * MemberDataset bestReplier = new MemberDataset();
 */
package kr.re.ec.zigeon.dataset;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import kr.re.ec.zigeon.handler.UIHandler;
import kr.re.ec.zigeon.util.Constants;
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
	
	private static MemberDataset loginInstance; //for singleton
	
	public MemberDataset() {
		
	}	
	
	public static MemberDataset getLoginInstance(){
		if(loginInstance==null) {
			loginInstance = new MemberDataset(); 
		}
		return loginInstance;
	}
	
	public void setDataset(String[] strArr) {
		try {
			if(strArr.length == MEMBER_FIELD_COUNT){
				idx = Integer.parseInt(strArr[0]);
				id = strArr[1]; 
				pw = strArr[2];
				nick = strArr[3];
				exp = Integer.parseInt(strArr[4]);
				isFacebook = Boolean.parseBoolean(strArr[5]);
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DB);
				if(strArr[6]!=null) { 
					regTime = sdf.parse(strArr[6]);
				} else {
					regTime = null;
				}
				if(strArr[7]!=null) {
					lastAccessTime = sdf.parse(strArr[7]);
				} else {
					lastAccessTime = null;
				}
				isAdmin = Boolean.parseBoolean(strArr[8]);
			} else {
				LogUtil.e("wrong data input");
			}
		} catch (Exception e) {
			LogUtil.e("parse error: " + e.toString());
		}
		
	}
	public void setLoginDataset(MemberDataset dataset) {
		//LogUtil.i("dataset.id: " + dataset.id);
		loginInstance = dataset;
		//LogUtil.i("instance.id: " + instance.id);		 
	}
}


