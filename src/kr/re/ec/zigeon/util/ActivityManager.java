package kr.re.ec.zigeon.util;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityManager {
	private static ActivityManager activityManager = null;
	private ArrayList<Activity> activityList = null;
	
	private ActivityManager() {
		activityList = new ArrayList<Activity>();
	}
	
	public static ActivityManager getInstance(){
		
		if( ActivityManager.activityManager == null) {
			activityManager = new ActivityManager();
		}
		return activityManager;
	}
	
	/**
	 * Activity list getter
	 * */
	public ArrayList<Activity> getActivityList() {
		return activityList;
	}
	
	/**
	 * Activity list add
	 * */
	public void addActivity(Activity activity){
		activityList.add(activity);
	}

	/**
	 * Activity list remove
	 */
	public boolean removeActivity(Activity activity) {
		return activityList.remove(activity);
	}
	
	public void finishAllActivity() {
		for(Activity activity : activityList){
			activity.finish();
		}
	}
}




