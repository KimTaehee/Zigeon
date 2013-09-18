/**
 * 130828 kim ji hong
 * memberDataset
 * usage: loginInstance is data about logged in user, or also can create instance such as 
 * MemberDataset bestReplier = new MemberDataset();
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
	public void setLoginDataset(MemberDataset dataset) {
		//LogUtil.i("dataset.id: " + dataset.id);
		loginInstance = dataset;
		//LogUtil.i("instance.id: " + instance.id);		 
	}
}


