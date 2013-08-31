/**
 * Class name : SoapParser
 * Description : Data SoapParse from Server and sendQuery to server
 * Writer : kim ji hong 
 * Version :
 * Write date : 130815
 * Modify date : 130816
 */

package kr.re.ec.zigeon.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
import kr.re.ec.zigeon.dataset.MemberDataset;
import kr.re.ec.zigeon.util.Constants;
import kr.re.ec.zigeon.util.LogUtil;

import org.ksoap2.SoapEnvelope; //MIT lisence
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.StrictMode;
import android.util.Log;

public class SoapParser {

	private static final String SOAP_ACTION = "ZigeonXMLResponse/Response_SearchData";
	private static final String METHOD_NAME = "Response_SearchData";
	private static final String NAMESPACE = "ZigeonXMLResponse";
	private static final String URL = "http://117.17.198.41:8088/WebService.asmx";

	private static final int TABLE = 1;
	private static final int DATA = 2;
	private static final int NONE = 3;


	private static SoapParser instance;

	private SoapParser() {
		LogUtil.v("constructor called");
	}

	/**
	 * singleton pattern applied.
	 * if there is no instance, new instance will be created.
	 * @return instance
	 */

	public static SoapParser getInstance(){ //singleton
		if(instance==null){
			LogUtil.v("create new instance");
			instance = new SoapParser();
		}
		return instance;
	}

	public Object getSoapData(String query, int datatype){ 	//look Constants about datatype.
		LogUtil.v("getSoapData called. query: \"" + query + "\" / type: " + datatype);

		Object resultObj = null;
		String resultStrArr[][] = null;


		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);			

		request.addProperty("searchData", query); //TODO: what is searchData?
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		androidHttpTransport.debug = true;
		try {
			/**above Honeycomb, network operation on Main Thread occurs Error.
			 * StrictMode can Work it.
			 */
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			//LogUtil.v(result.toString());
			resultStrArr = xmlParser(result.toString(),datatype); // xml parsing

			
			resultObj = convertDatasetToObj(resultStrArr, datatype);
			

			resultObj = convertDatasetToObj(resultStrArr, datatype);


		} catch (Exception e) {
			LogUtil.e("Error occured. see printStackTrace().");
			e.printStackTrace();
		} // try-catch

		
		return resultObj;
	}
	
	private String[][] xmlParser(String data, int datatype) {	//look Constants about datatype.

		//LogUtil.v("xmlParser called.");
		String parsingData = null;
		String[][] parsingDataArr = null;

		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
			parser.setInput(input, "UTF-8");

			int parserEvent = parser.getEventType();
			String tag = null;
			int inText = NONE; 

			int tableCnt; //get num of total table. allocate 2d String array's rows.
			int i=0,j=0; //cursor of rows and cols.
			String[] tableCntArr = data.split("<Table>"); //split and count
			tableCnt = tableCntArr.length - 1; // because of <NewDataSet>.
			//LogUtil.v("tableCnt: " + tableCnt);
			parsingDataArr = new String[tableCnt][]; //allocate String Array's rows
			for(i=0;i<parsingDataArr.length;i++) { //allocate String Array's cols
				parsingDataArr[i] = new String[Constants.DATASET_FIELD[datatype].length];
			}
			//LogUtil.v("prsDArr length["+parsingDataArr.length+"]["+parsingDataArr[0].length+"]");
			i = -1; // first table is 0. look around i++!

			j = 0;
			while (parserEvent != XmlPullParser.END_DOCUMENT) {
				switch (parserEvent) {
				case XmlPullParser.START_TAG:
				{
					tag = parser.getName();
					if (tag.compareTo("NewDataSet") == 0) {
						inText = NONE;
						//parsingData = "";
					} else if (tag.compareTo("Table") == 0) {
						//LogUtil.v("new Table");
						inText = TABLE;
						i++;  
						j=0;
					} else {
						inText = DATA;
					}
					break;
				}
				case XmlPullParser.TEXT:
				{
					switch (inText) {
					case DATA:
					{
//						LogUtil.v("parser.gettext : " + parser.getText() + " i: " + i + " j: "+j);
//						LogUtil.v("Constants.DATASET_FIELD[datatype][j]" + Constants.DATASET_FIELD[datatype][j]);

						if(tag.compareTo(Constants.DATASET_FIELD[datatype][j]) == 0) { //no error on col name matching 
							parsingDataArr[i][j] = parser.getText();							
						} else { //error on col name matching
							parsingDataArr[i][j] = "null"; //force to insert null
						}
						j++;
						break;
					}
					case NONE:
					}
					tag = parser.getName();
					break;
					
				}
				case XmlPullParser.END_TAG:
				{
					inText = NONE;
					break;
				}
				}
				parserEvent = parser.next();
			}
		} catch (Exception e) {
			LogUtil.e("Error in network call" + e.toString());
		}
		parsingData = new String("");
		for(int i=0;i<parsingDataArr.length;i++) {
			for(int j=0;j<parsingDataArr[i].length;j++) {
				parsingData = parsingData + parsingDataArr[i][j] + " ";
			}
			parsingData += "\n";
		}

		


		return parsingDataArr;
	}

	 /**
	  *  to insert, update, delete. return is String that splited comma. written by KimTaehee
	  */
	public String sendQuery(String query) { 
		LogUtil.v("sendQuery called. query: \"" + query + "\"");
		String resultStr = null;


		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		request.addProperty("searchData", query); //TODO: what is searchData?


		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		androidHttpTransport.debug = true;
		try {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			LogUtil.i(result.toString());

			LogUtil.v("xmlparser start");

			resultStr = xmlRawParser(result.toString()); // xml parsing

		} catch (Exception e) {
			LogUtil.e("Error occured. see printStackTrace().");
			e.printStackTrace();
		} // try-catch

		
		return resultStr;
	}
	
	/**
	 * Author: KimTaehee
	 * Description: convert strArr[][] to Obj 
	 * 
	 * @param strArr: array of Dataset[] 
	 * Dataset[] MUST have type of each Dataset 
	 * from class Constants 
	 * @param dataType: MSG_TYPE_LANDMARK | MSG_TYPE_POSTING | MSG_TYPE_COMMENT | MSG_TYPE_MEMBER | MSG_TYPE_TEST
	 * from class Constants
	 * @return Obj is each Dataset[]
	 */
	public Object convertDatasetToObj(String[][] strArr, int datatype)	
	{
		//LogUtil.v("convert Dataset to Obj");
		Object obj = null;
		switch (datatype) {
		case Constants.MSG_TYPE_LANDMARK:
			//LogUtil.v("switched MSG_TYPE_LANDMARK");
			LandmarkDataset[] landmarkArr = new LandmarkDataset[strArr.length];	//create Landmark Array 

			for(int i=0; i<strArr.length; i++) {
				landmarkArr[i] = new LandmarkDataset(strArr[i]);	
			}
			obj = landmarkArr;
			break;

		case Constants.MSG_TYPE_POSTING:
			PostingDataset[] postingArr = new PostingDataset[strArr.length];	//create Posting Array 

			for(int i=0; i<strArr.length; i++) {
				postingArr[i] = new PostingDataset(strArr[i]);	
			}
			obj = postingArr;
			break;

		case Constants.MSG_TYPE_COMMENT:
			CommentDataset[] commentArr = new CommentDataset[strArr.length];	//create Comment Array 

			for(int i=0; i<strArr.length; i++) {
				commentArr[i] = new CommentDataset(strArr[i]);	
			}
			obj = commentArr;
			break;

		case Constants.MSG_TYPE_MEMBER: 
			MemberDataset[] memberArr = new MemberDataset[strArr.length];	//create Landmark Array 
			for(int i=0; i<strArr.length; i++) {
				memberArr[i] = MemberDataset.getInstance(); 
				memberArr[i].setDataset(strArr[i]);   //always one instance created
			}
			obj = memberArr;
			break;
			

		case Constants.MSG_TYPE_TEST:
			//LogUtil.v("object test converting. strArr[0][0] = " + strArr[0][0]);
			if(strArr[0][0]!=null) {
				obj = strArr[0][0];
			} else {
				obj = null;
			}
			break;

			
		default:
			LogUtil.e("default switched.");
			break;
		}

		
		return obj;
	}

	/**
	 * Author: KimTaehee
	 * Description: convert strArr[] to Obj
	 * 
	 * @param strArr: Dataset
	 * Dataset MUST have type of each Dataset 
	 * from class Constants 
	 * @param dataType: MSG_TYPE_LANDMARK | MSG_TYPE_POSTING | MSG_TYPE_COMMENT | MSG_TYPE_MEMBER
	 * from class Constants
	 * @return Obj is each Dataset
	 */
	public Object convertDatasetToObj(String[] strArr, int datatype)	
	{
		LogUtil.v("convert Dataset to Obj");
		Object obj = null;
		switch (datatype) {
		case Constants.MSG_TYPE_LANDMARK:
			//LogUtil.v("switched MSG_TYPE_LANDMARK");
			LandmarkDataset ldm = new LandmarkDataset();	//create Landmark 

			ldm = new LandmarkDataset(strArr);	
			
			obj = ldm;
			break;

//		case Constants.MSG_TYPE_POSTING:
//			PostingDataset[] postingArr = new PostingDataset[strArr.length];	//create Posting Array 
//
//			for(int i=0; i<strArr.length; i++) {
//				postingArr[i] = new PostingDataset(strArr[i]);	
//			}
//			obj = postingArr;
//			break;
//
//		case Constants.MSG_TYPE_COMMENT:
//			CommentDataset[] commentArr = new CommentDataset[strArr.length];	//create Comment Array 
//
//			for(int i=0; i<strArr.length; i++) {
//				commentArr[i] = new CommentDataset(strArr[i]);	
//			}
//			obj = commentArr;
//			break;
//
//		case Constants.MSG_TYPE_MEMBER: 
//			MemberDataset[] memberArr = new MemberDataset[strArr.length];	//create Landmark Array 
//			for(int i=0; i<strArr.length; i++) {
//				memberArr[i] = MemberDataset.getInstance(); 
//				memberArr[i].setDataset(strArr[i]);   //always one instance created
//			}
//			obj = memberArr;
//			break;
//
			
		default:
			LogUtil.e("default switched.");
			break;
		}

		
		return obj;
	}
	
	
	/**
	 * Name: String xmlRawParser(String data)
	 * Author: KimTaehee
	 * Description: XML to String
	 * 
	 * @param 	XML
	 * @return 	if data exist: String "t1.col1,t1.col2,t1.col3,t2.col1,t2.col2 ..."
	 * 			if no data: String ""
	 */
	private String xmlRawParser(String data) {
		String parsingData = null;


		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
			parser.setInput(input, "UTF-8");

			int parserEvent = parser.getEventType();
			String tag;
			int inText = NONE;

			//if there is no data, response is "<NewDataSet />".

			if(data.compareTo("<NewDataSet />")==0) {
				return "";
			} else {
				while (parserEvent != XmlPullParser.END_DOCUMENT) {
					switch (parserEvent) {
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						if (tag.compareTo("NewDataSet") == 0) {
							inText = NONE;
							parsingData = "";
						} else if (tag.compareTo("Table") == 0) {
							inText = TABLE;
						} else {
							inText = DATA;
						}
						break;
					case XmlPullParser.TEXT:
						tag = parser.getName();
						switch (inText) {
						case DATA:
							parsingData += parser.getText() + ",";

							// to divide data, insert comma

							break;

						case NONE:
						}
						tag = parser.getName();
						break;

					case XmlPullParser.END_TAG:
						inText = NONE;
						break;

					}
					parserEvent = parser.next();
				}
			}
		} catch (Exception e) {
			LogUtil.e("Error in network call");
			e.printStackTrace();
		}
		parsingData = parsingData.substring(0, parsingData.length()-1);

		return parsingData;

	}

	/**
	 * insert Dataset to DB
	 * @author KimTaehee
	 * @param dataType: MSG_TYPE_LANDMARK | MSG_TYPE_POSTING | MSG_TYPE_COMMENT | MSG_TYPE_MEMBER from Constants.
	 * @param datasetObj: It MUST conversion one each Dataset. ex: (Object)landmarkDataset
	 * @return result String of SoapParser.sendQuery() 
	 */
	public String insertDatasetUsingQuery(int dataType, Object datasetObj) {
		String query = null;
		
		//create query for each dataset
		switch(dataType) {
		case Constants.MSG_TYPE_LANDMARK:
			LandmarkDataset ldm = (LandmarkDataset)datasetObj;
			
			break;
			
		case Constants.MSG_TYPE_POSTING:
			PostingDataset pst = (PostingDataset)datasetObj;
			
			//get maxPstIdx
			String str = sendQuery("SELECT MAX(pstIdx) FROM tPosting"); 
    		int maxPstIdx = Integer.parseInt(str);
			
			query = "INSERT INTO tPosting (pstIdx,pstTitle,pstParentIdx,pstContents,pstLike,pstDislike" +
					",pstWriterIdx,pstReadedCount,pstWrittenTime,pstPicturePath)" +
					" values ('" +
					(maxPstIdx + 1) + //pstIdx
					"','" + pst.title + //pstTitle
					"','" + pst.parentIdx + //pstParentIdx
					"','"+ pst.contents + //pstContents
					"','0','0','" + //pstLike, pstDislike
					pst.writerIdx + //pstWriterIdx
					"','0'," + //pstReadedCount
					"GETDATE(),"; //pstWrittenTime
			if(pst.picturePath==null) {
				query += "NULL)";
			} else {
				query += "'" + pst.picturePath + "')";
			}
			
			break;
		case Constants.MSG_TYPE_COMMENT:
		
			break;
		
		case Constants.MSG_TYPE_MEMBER:
		
			break;
		}
		
		//send insert query
		LogUtil.v(query);
		String result = sendQuery(query);
		
		return result;
	}
}
