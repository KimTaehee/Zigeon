/**
 * 클래스 이름 : SoapParsing
 * 클래스 설명 : SoapParsing 파싱파싱
 * 작성자 (혹은 팀) : kim ji hong 
 * 버전 정보 :
 * 작성 일자 : 8월 15일
 * 수정 이력 : 8월 19일 오전 6:46
 */

package kr.re.ec.zigeon.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import kr.re.ec.zigeon.dataset.CommentDataset;
import kr.re.ec.zigeon.dataset.LandmarkDataset;
import kr.re.ec.zigeon.dataset.PostingDataset;
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
	
	public static SoapParser getInstance(){ //singleton
		if(instance==null){
			LogUtil.v("create new instance");
			instance = new SoapParser();
		}
		return instance;
	}
	
	public Object getSoapData(String query, int datatype){ //datatype은 Contants를 보라.
		LogUtil.v("getSoapData called. query: \"" + query + "\" / type: " + datatype);
		
		Object resultObj = null;
		String resultStrArr[][] = null;
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		request.addProperty("searchData", query); //TODO: searchData 키워드의 의미?
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		androidHttpTransport.debug = true;
		try {
			/**StrictMode는 허니콤 이상 버젼에 메인쓰레스에서 네트워크 작업을 하면
			 * 에러가난다. 기필코 메인에서 작업해야할경우 쓰는 코드.....
			 * 가져다 붙여쓴거라 어떻게 처리를 못해서 그냥 붙어버렸어요.....
			 */
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
			
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			//LogUtil.v(result.toString());
			resultStrArr = xmlParser(result.toString(),datatype); // xml parsing
			
			resultObj = convertDatasetToObj(resultStrArr, datatype);
			
		} catch (Exception e) {
			LogUtil.e("Error occured. see printStackTrace().");
			e.printStackTrace();
		} // try-catch
		
		return resultObj;
	}
	
	private String[][] xmlParser(String data, int datatype) {// 데이터 파싱. datatype은 Constants에 따름
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
			
			int tableCnt; //총 테이블 개수를 구하여 2차원 String 배열의 행을 할당한다.
			int i=0,j=0; //행,열 커서
			String[] tableCntArr = data.split("<Table>"); //잘라서 몇 개인지 알아본다.
			tableCnt = tableCntArr.length - 1; //-1은 <NewDataSet> 때문
			//LogUtil.v("tableCnt: " + tableCnt);
			
			parsingDataArr = new String[tableCnt][]; //String 배열 행 할당 
			for(i=0;i<parsingDataArr.length;i++) { //String 배열 열 할당
				parsingDataArr[i] = new String[Constants.DATASET_FIELD[datatype].length];
			}
			LogUtil.v("prsDArr length["+parsingDataArr.length+"]["+parsingDataArr[0].length+"]");
						
			i = -1; //첫 테이블이 0이 되어야 하니까. i++ 부분을 보라!
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
						if(tag.compareTo(Constants.DATASET_FIELD[datatype][j]) == 0) { //순리대로 흘러가면
							parsingDataArr[i][j] = parser.getText();							
						} else { //순리를 거스르면 null값임
							parsingDataArr[i][j] = "null"; //받아라 널 씨발련아
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
	 * insert, update, delete 전송용도. return은  결과가 콤마로 구분된 String. 김태희 작성.
	 */
	public String sendQuery(String query) { 
		LogUtil.v("sendQuery called. query: \"" + query + "\"");
		
		String resultStr = null;
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		request.addProperty("searchData", query); //TODO: searchData 키워드의 의미?
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		androidHttpTransport.debug = true;
		try {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
			
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			LogUtil.i(result.toString());
			resultStr = xmlRawParser(result.toString()); // xml parsing
			
		} catch (Exception e) {
			LogUtil.e("Error occured. see printStackTrace().");
			e.printStackTrace();
		} // try-catch
		
		return resultStr;
	}
	
	private Object convertDatasetToObj(String[][] strArr, int datatype)	//2차원 strArr를 Obj[]로 convert.
	{
		//LogUtil.v("convert Dataset to Obj");
		Object obj = null;
		switch (datatype) {
		case Constants.MSG_TYPE_LANDMARK:
			//LogUtil.v("switched MSG_TYPE_LANDMARK");
			LandmarkDataset[] landmarkArr = new LandmarkDataset[strArr.length];	//Landmark Array 생성
			for(int i=0; i<strArr.length; i++) {
				landmarkArr[i] = new LandmarkDataset(strArr[i]);	
			}
			obj = landmarkArr;
			break;
			
		case Constants.MSG_TYPE_POSTING:
			PostingDataset[] postingArr = new PostingDataset[strArr.length];	//Posting Array 생성
			for(int i=0; i<strArr.length; i++) {
				postingArr[i] = new PostingDataset(strArr[i]);	
			}
			obj = postingArr;
			break;
			
		case Constants.MSG_TYPE_COMMENT:
			CommentDataset[] commentArr = new CommentDataset[strArr.length];	//Comment Array 생성
			for(int i=0; i<strArr.length; i++) {
				commentArr[i] = new CommentDataset(strArr[i]);	
			}
			obj = commentArr;
			break;
			
			//TODO: MEMBER 처리해야함
//		case Constants.MSG_TYPE_MEMBER: 
//			MemberDataset[] landmark = new LandmarkDataset[strArr.length];	//Landmark Array 생성
//			for(int i=0; i<strArr.length; i++) {
//				landmark[i] = new LandmarkDataset(strArr[i]);	
//			}
//			obj = landmark;
//			break;
//			
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
	
	private String xmlRawParser(String data) {// String으로 return.
		String parsingData = null;
		
		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
			parser.setInput(input, "UTF-8");

			int parserEvent = parser.getEventType();
			String tag;
			int inText = NONE;

			//data가 없으면 <NewDataSet />이 온다.
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
							// 데이터를 구분하기위해 콤마를 추가했습니다
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
}
