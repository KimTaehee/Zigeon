/**
 * 130828 kim ji hong
 * memberDataset
 */
package kr.re.ec.zigeon.dataset;

import java.util.Date;

import kr.re.ec.zigeon.util.LogUtil;

public class MemberDataset extends Object {
	public static final int MEMBER_FIELD_COUNT = 9;
	
	public int idx;
	public String id;
	public String pw;
	public String nick;
	public int exp;
	public boolean isfacebook;
	public Date regtime;
	public Date lastaccesstime;
	public boolean isadmin;
	
	public MemberDataset() {
		
	}
	
	public MemberDataset(String[] strArr) {
		setDataset(strArr);
	}
	
	public void setDataset(String[] strArr) {
		if(strArr.length == MEMBER_FIELD_COUNT){
			idx = Integer.parseInt(strArr[0]);
			id = strArr[1]; 
			pw = strArr[2];
			nick = strArr[3];
			exp = Integer.parseInt(strArr[4]);
			isfacebook = Boolean.parseBoolean(strArr[5]);
			regtime = new Date(); //TODO: temporary.
			lastaccesstime = new Date(); //TODO: temporary.
			isadmin = Boolean.parseBoolean(strArr[8]);
		} else {
			LogUtil.e("wrong data input");
		}
	}
}


