/**
 * Ŭ���� �̸� : SoapParsing
 * Ŭ���� ���� : SoapParsing �Ľ��Ľ�
 * �ۼ��� (Ȥ�� ��) : kim ji hong 
 * ���� ���� :
 * �ۼ� ���� : 8�� 15��
 * ���� �̷� : 8�� 19�� ���� 6:46
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
	
	public Object getSoapData(String query, int datatype){ //datatype�� Contants�� ����.
		LogUtil.v("getSoapData called. query: \"" + query + "\" / type: " + datatype);
		
		Object resultObj = null;
		String resultStrArr[][] = null;
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		request.addProperty("searchData", query); //TODO: searchData Ű������ �ǹ�?
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		androidHttpTransport.debug = true;
		try {
			/**StrictMode�� ����� �̻� ������ ���ξ��������� ��Ʈ��ũ �۾��� �ϸ�
			 * ����������. ������ ���ο��� �۾��ؾ��Ұ�� ���� �ڵ�.....
			 * ������ �ٿ����Ŷ� ��� ó���� ���ؼ� �׳� �پ���Ⱦ��.....
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
	
	private String[][] xmlParser(String data, int datatype) {// ������ �Ľ�. datatype�� Constants�� ����
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
			
			int tableCnt; //�� ���̺� ������ ���Ͽ� 2���� String �迭�� ���� �Ҵ��Ѵ�.
			int i=0,j=0; //��,�� Ŀ��
			String[] tableCntArr = data.split("<Table>"); //�߶� �� ������ �˾ƺ���.
			tableCnt = tableCntArr.length - 1; //-1�� <NewDataSet> ����
			//LogUtil.v("tableCnt: " + tableCnt);
			
			parsingDataArr = new String[tableCnt][]; //String �迭 �� �Ҵ� 
			for(i=0;i<parsingDataArr.length;i++) { //String �迭 �� �Ҵ�
				parsingDataArr[i] = new String[Constants.DATASET_FIELD[datatype].length];
			}
			LogUtil.v("prsDArr length["+parsingDataArr.length+"]["+parsingDataArr[0].length+"]");
						
			i = -1; //ù ���̺��� 0�� �Ǿ�� �ϴϱ�. i++ �κ��� ����!
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
						if(tag.compareTo(Constants.DATASET_FIELD[datatype][j]) == 0) { //������� �귯����
							parsingDataArr[i][j] = parser.getText();							
						} else { //������ �Ž����� null����
							parsingDataArr[i][j] = "null"; //�޾ƶ� �� ���߷þ�
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
	 * insert, update, delete ���ۿ뵵. return��  ����� �޸��� ���е� String. ������ �ۼ�.
	 */
	public String sendQuery(String query) { 
		LogUtil.v("sendQuery called. query: \"" + query + "\"");
		
		String resultStr = null;
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		request.addProperty("searchData", query); //TODO: searchData Ű������ �ǹ�?
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
	
	private Object convertDatasetToObj(String[][] strArr, int datatype)	//2���� strArr�� Obj[]�� convert.
	{
		//LogUtil.v("convert Dataset to Obj");
		Object obj = null;
		switch (datatype) {
		case Constants.MSG_TYPE_LANDMARK:
			//LogUtil.v("switched MSG_TYPE_LANDMARK");
			LandmarkDataset[] landmarkArr = new LandmarkDataset[strArr.length];	//Landmark Array ����
			for(int i=0; i<strArr.length; i++) {
				landmarkArr[i] = new LandmarkDataset(strArr[i]);	
			}
			obj = landmarkArr;
			break;
			
		case Constants.MSG_TYPE_POSTING:
			PostingDataset[] postingArr = new PostingDataset[strArr.length];	//Posting Array ����
			for(int i=0; i<strArr.length; i++) {
				postingArr[i] = new PostingDataset(strArr[i]);	
			}
			obj = postingArr;
			break;
			
		case Constants.MSG_TYPE_COMMENT:
			CommentDataset[] commentArr = new CommentDataset[strArr.length];	//Comment Array ����
			for(int i=0; i<strArr.length; i++) {
				commentArr[i] = new CommentDataset(strArr[i]);	
			}
			obj = commentArr;
			break;
			
			//TODO: MEMBER ó���ؾ���
//		case Constants.MSG_TYPE_MEMBER: 
//			MemberDataset[] landmark = new LandmarkDataset[strArr.length];	//Landmark Array ����
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
	
	private String xmlRawParser(String data) {// String���� return.
		String parsingData = null;
		
		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
			parser.setInput(input, "UTF-8");

			int parserEvent = parser.getEventType();
			String tag;
			int inText = NONE;

			//data�� ������ <NewDataSet />�� �´�.
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
							// �����͸� �����ϱ����� �޸��� �߰��߽��ϴ�
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
