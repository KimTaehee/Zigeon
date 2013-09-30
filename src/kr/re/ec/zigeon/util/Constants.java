package kr.re.ec.zigeon.util;

/**
 * Class: Constants
 * Descripton: include constants of project
 * Author: KimTaehee slhyvaa@nate.com
 * Created: 130816
 * Modified: 130831
 */

public final class Constants {
	/************** key value(msg.what) for message send. int range 0~ ************/
	/************** DO NOT MODIFY THE NUMBER ! SoapParser and Dataset Definition should be FOOLED ! ***********/
	public static final int MSG_TYPE_LANDMARK = 0;	//query result of landmarks. about LandmarkDataset. 
	public static final int MSG_TYPE_POSTING = 1;	//query result of postings. about PostingDataset.
	public static final int MSG_TYPE_COMMENT = 2;	//query result of comments. about CommentDataset.
	public static final int MSG_TYPE_MEMBER = 3;	//query result of member.
	public static final int MSG_TYPE_TEST = 4;		//for test query. String Object "test"
	public static final int MSG_TYPE_LOCATION = 5;	// NGeoPoint. Location Object(android.location.Location) is unused.
	public static final int MSG_TYPE_REFRESH = 6;	
	
	/******************* Alert Type. int range 10000~ *******************/
	//dialog type
	public static final int ALERT_OK_ONLY = 10000;
	public static final int ALERT_YES_NO = 10001;
	//return value
	public static final int ALERT_RETURN_YES = 10010;
	public static final int ALERT_RETURN_NO = 10011;
	public static final int ALERT_RETURN_CANCEL = 10012;
	
	/********************* DB_NULL Definition *********************/
	/************** DO NOT MODIFY THE NUMBER ! DOUBLE_NULL may cast to INT_NULL ************/
	public static final int INT_NULL = -1; //because of int cannot have null value. Dataset.setDataSet use.
	public static final double DOUBLE_NULL = -1.0; //double cannot have null value. LandmarkDataset.getDistance use.

	/***************** Date format **************/
	public static final String DATE_FORMAT_DB = "yyyy-MM-dd\'T\'HH:mm:ss"; 
	public static final String DATE_FORMAT_PRINT = "yyyy-MM-dd HH:mm";
	
	/***************** GCM **************/
	public static final String GCM_PROJECT_ID = "39642280488"; //can identify unique GCM server
	
	/**************** Image Server URL *********************/
	//image dir: only used in eachDataset.getImageUrl()
	public static final String URL_SERVER_IMAGE_DIR = "http://117.17.198.41:8088/images/"; 
	public static final String URL_SERVER_IMAGE_UPLOAD_PAGE = "http://117.17.198.41:8088/Upload.aspx"; //upload page
	
	/**************** Image optimization for Uploading ***************/
	//public static final int IMG_UPLOAD_MAX_BYTE = 150000;	//150KB
	public static final int IMG_UPLOAD_MAX_SIDE_PIXEL = 1000;	//max image size is 1000 x 1000
	
	/**************** Naver Map *********************/
	//Aug 2013, Seo Juri registered.
	public static final String NMAP_API_KEY = "3aa5ca39d123f5448faff118a4fd9528";
	//default location (Mirae hall, Seoultech. when no location detected, use this temporary value.)
	public static final double NMAP_DEFAULT_LAT = 37.6292700;
	public static final double NMAP_DEFAULT_LON = 127.0815700; 
	
	/**************** Err Code. int range 1000~1069 ****************/
	public static final int ERR_UNEXPECTED = 1000;
	public static final int ERR_DEFAULT_SWITCHED = 1001;
	public static final int ERR_DATASET_MISMATCHED = 1010;
	public static final int ERR_NETWORK_NOT_FOUND = 1020;
	public static final int ERR_SERVER_TIMEOUT = 1030;
	public static final int ERR_DB_CONN_FAILED = 1040;
	public static final int ERR_DB_INSERT_FAILED = 1041;
	public static final int ERR_DOWNLOAD_NO_DEST_FILE = 1050;
	public static final int ERR_UPLOAD_NO_SRC_FILE = 1060;
	
	/**************** Err String. ****************/
	public static final String ERR_STR_UNEXPECTED = "Unexpected Error";
	public static final String ERR_STR_DEFAULT_SWTICHED = "Default Switched";
	public static final String ERR_STR_DATASET_MISMATCHED = "Dataset Mismatched";
	public static final String ERR_STR_NETWORK_NOT_FOUND = "No Network Found";
	public static final String ERR_STR_SERVER_TIMEOUT = "Server Timeout";
	public static final String ERR_STR_DB_CONN_FAILED = "Cannot connect to DB";
	public static final String ERR_STR_DB_INSERT_FAILED = "Cannot insert to DB";
	public static final String ERR_STR_DOWNLOAD_NO_DEST_FILE = "Cannot Download image. No destination file";
	public static final String ERR_STR_UPLOAD_NO_SRC_FILE = "Cannot Upload image. No source file";

	/***
	 * Author: KimTaehee
	 * Convert ERR Code to ERR String
	 */
	public static String toErrSting(int errCode) {
		String str;
		switch (errCode) {
		case ERR_UNEXPECTED: 				str = ERR_STR_UNEXPECTED;						break;
		case ERR_DEFAULT_SWITCHED:			str = ERR_STR_DEFAULT_SWTICHED;					break;
		case ERR_DATASET_MISMATCHED: 		str = ERR_STR_DATASET_MISMATCHED;				break;
		case ERR_NETWORK_NOT_FOUND: 		str = ERR_STR_NETWORK_NOT_FOUND;				break;
		case ERR_SERVER_TIMEOUT: 			str = ERR_STR_SERVER_TIMEOUT;					break;
		case ERR_DB_CONN_FAILED: 			str = ERR_STR_DB_CONN_FAILED;					break;
		case ERR_DB_INSERT_FAILED: 			str = ERR_STR_DB_INSERT_FAILED;					break;
		case ERR_DOWNLOAD_NO_DEST_FILE: 	str = ERR_STR_DOWNLOAD_NO_DEST_FILE;			break;
		case ERR_UPLOAD_NO_SRC_FILE: 		str = ERR_STR_UPLOAD_NO_SRC_FILE;				break;
		default: 							str = ERR_STR_DEFAULT_SWTICHED;					break;
		}
		return str;
	}
	
	/************** Dataset Definition ********************/ 
	public static final String DATASET_FIELD[][] = { //table arrtibute name. SoapParser use.
		{ //0
			"ldmIdx",
			"ldmName",
			"ldmLatitude",
			"ldmLongitude",
			"ldmContents",
			"ldmRating",
			"ldmRatingVotes",
			"ldmVisible",
			"ldmWriterIdx",
			"ldmReadedCount",
			"ldmWrittenTime",
			"ldmUndoIdx",
			"ldmPicturePath"
		},
		{ //1
			"pstIdx",
			"pstTitle",
			"pstParentIdx",
			"pstContents",
			"pstLike",
			"pstDislike",
			"pstWriterIdx",
			"pstReadedCount",
			"pstWrittenTime",
			"pstPicturePath",
			"pstVisible"
		},
		{ //2
			"comIdx",
			"comParentType",
			"comParentIdx",
			"comContents",
			"comLike",
			"comDislike",
			"comWriterIdx",
			"comWrittenTime",
			"comPicturePath",
			"comVisible"
		},
		{ //3
			"memIdx",
			"memID",
			"memPW",
			"memNick",
			"memExp",
			"memIsFacebook",
			"memRegTime",
			"memLastAccessTime",
			"memIsAdmin"
		},
		{ //4
			"test"
		}
	};	
	
	

}

