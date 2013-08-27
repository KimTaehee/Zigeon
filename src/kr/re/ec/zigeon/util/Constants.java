package kr.re.ec.zigeon.util;

/**
 * Class Constants.
 * include constants of project Zigeon
 * 130816 Kim Taehee
 * slhyvaa@nate.com
 */

public final class Constants {
	/************** key value(msg.what) for message send ************/
	/************** DO NOT MODIFY THE NUMBER ! SoapParser and Dataset Definition should be fooled ! ***********/
	public static final int MSG_TYPE_LANDMARK = 0;	//query result of landmarks. about LandmarkDataset. 
	public static final int MSG_TYPE_POSTING = 1;	//query result of postings. about PostingDataset.
	public static final int MSG_TYPE_COMMENT = 2;	//query result of comments. about CommentDataset.
	public static final int MSG_TYPE_MEMBER = 3;	//query result of member.
	public static final int MSG_TYPE_TEST = 4;		//for test query. String Object "test"
	public static final int MSG_TYPE_LOCATION = 5;	// NGeoPoint. Location Object(android.location.Location) is unused.
	
	/********************* DB_NULL Definition *********************/
	/************** DO NOT MODIFY THE NUMBER ! DOUBLE_NULL may cast to INT_NULL ************/
	public static final int INT_NULL = -1; //because of int cannot have null value. Dataset.setDataSet use.
	public static final double DOUBLE_NULL = -1.0; //double cannot have null value. LandmarkDataset.getDistance use.
	
	/***************** GCM **************/
	public static final String GCM_PROJECT_ID = "39642280488"; 
	
	/************** Dataset Definition ���̺�?���� ��********************/ 
	public static final String DATASET_FIELD[][] = { //table arrtibute name. SoapParser use.
		{ //0
			"ldmIdx",
			"ldmName",
			"ldmLatitude",
			"ldmLongitude",
			"ldmContents",
			"ldmLike",
			"ldmDislike",
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
			"pstPicturePath"
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
			"comPicturePath"
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

