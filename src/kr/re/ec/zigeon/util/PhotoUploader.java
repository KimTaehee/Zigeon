/**
 * Class Name: PhotoUploader
 * Description: Upload photo to server using AsyncTask
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130821
 * Modified Date: 130905
 */

package kr.re.ec.zigeon.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import kr.re.ec.zigeon.R;
import kr.re.ec.zigeon.R.id;
import kr.re.ec.zigeon.R.layout;
import kr.re.ec.zigeon.dataset.ImageUploadDataset;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhotoUploader extends AsyncTask<ImageUploadDataset, Integer, Void> {

	//private static final String UPLOAD_FILE_PATH = "/sdcard/f1.png"; 
		
//	private Button mUploadBtn;
//	private FileInputStream mFileInputStream = null;
//	private URL connectUrl = null;
//	private EditText mEdityEntry; 
	
//	private String lineEnd = "\r\n";
//	private String twoHyphens = "--";
//	private String boundary = "*****";	
	
	private void DoFileUpload(ImageUploadDataset imageUpload) throws IOException {
		LogUtil.v("file source path = " + imageUpload.sourcePath);
		HttpFileUpload(Constants.URL_SERVER_IMAGE_UPLOAD_PAGE, imageUpload.type, imageUpload.idx, imageUpload.sourcePath);	
	}
	
	//TODO: T: This function need too much time on main thread 
	private void HttpFileUpload(String urlString, int type, int idx, String fileName) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(Constants.URL_SERVER_IMAGE_UPLOAD_PAGE);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			/********** add POST string values *********/
			reqEntity.addPart("type", new StringBody(String.valueOf(type)));
			reqEntity.addPart("idx", new StringBody(String.valueOf(idx)));
			reqEntity.addPart("filename",new StringBody(fileName));
			try{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Bitmap bitmap = BitmapFactory.decodeFile(fileName);
				bitmap.compress(CompressFormat.JPEG, 75, bos);
				byte[] data = bos.toByteArray();
				ByteArrayBody bab = new ByteArrayBody(data, fileName);
				reqEntity.addPart("picture", bab);
			}
			catch(Exception e){
				//Log.v("Exception in Image", ""+e);
				reqEntity.addPart("picture", new StringBody(""));
			}
			postRequest.setEntity(reqEntity);       
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();
			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse + "\n");
			}
			LogUtil.v("result: "+ s);

			
//			mFileInputStream = new FileInputStream(fileName);			
//			connectUrl = new URL(urlString);
//			LogUtil.v("mFileInputStream  is " + mFileInputStream);
//			
//			// open connection 
//			HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();			
//			conn.setDoInput(true);
//			conn.setDoOutput(true);
//			conn.setUseCaches(false);
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("Connection", "Keep-Alive");
//			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//			conn.setRequestProperty("type", String.valueOf(type)); // refer to Constants
//			conn.setRequestProperty("idx", String.valueOf(idx)); 
//			conn.setRequestProperty("filename", fileName); 
//			
//			// write data
//			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
//			dos.writeBytes(twoHyphens + boundary + lineEnd);
//			
//			//input type and idx here
//			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName+"\"" + lineEnd); 
//			dos.writeBytes(lineEnd);
//			
//			int bytesAvailable = mFileInputStream.available();
//			int maxBufferSize = 1024;
//			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//			
//			byte[] buffer = new byte[bufferSize];
//			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//			
//			LogUtil.v("image byte is " + bytesRead);
//			
//			// read image
//			while (bytesRead > 0) {
//				dos.write(buffer, 0, bufferSize);
//				bytesAvailable = mFileInputStream.available();
//				bufferSize = Math.min(bytesAvailable, maxBufferSize);
//				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//			}	
//			
//			dos.writeBytes(lineEnd);
//			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//			
//			// close streams
//			LogUtil.v("File is written");
//			mFileInputStream.close();
//			dos.flush(); // finish upload...			
//			
//			// get response
//			int ch;
//			InputStream is = conn.getInputStream();
//			StringBuffer b =new StringBuffer();
//			while( ( ch = is.read() ) != -1 ){
//				b.append( (char)ch );
//			}
//			String s=b.toString(); 
//			LogUtil.v("result = " + s);
//			mEdityEntry.setText(s);
//			dos.close();			
			
		} catch (Exception e) {
			LogUtil.e("exception " + e.getMessage()+", "+e.toString());
		}		
	}

	@Override
	protected void onPreExecute() {
		LogUtil.v("onPreExecute invoked!");
	};
	
	@Override
	protected void onPostExecute(Void result) {
		LogUtil.v("onPostExecute invoked! result:" + result);
	};
	
	/**
	 * arg format: type(lowercase)/Index/filename
	 * ex: ldm/3/hanwoo.jpg
	 */
	@Override
	protected Void doInBackground(ImageUploadDataset... arg0) { 
		try {
			DoFileUpload(arg0[0]); //TODO: one time one file. test phrase 
		} catch (IOException e) {
			LogUtil.e("error occured!!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}

