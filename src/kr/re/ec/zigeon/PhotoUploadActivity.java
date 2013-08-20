/**
 * Class PhotoUploadeActivity
 * KimTaehee slhyvaa@nate.com
 * 130821 first created
 * 
 */

package kr.re.ec.zigeon;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.re.ec.zigeon.util.LogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhotoUploadActivity extends Activity {

	private static final String UPLOAD_FILE_PATH = "/sdcard/f1.png"; 
	private static final String UPLOAD_PAGE_URL = "http://117.17.198.41:8088/Upload.aspx";
	
	private Button mUploadBtn;
	private FileInputStream mFileInputStream = null;
	private URL connectUrl = null;
	private EditText mEdityEntry; 
	
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_photo_upload);
		
		mEdityEntry = (EditText)findViewById(R.id.photo_upload_edit);
		mUploadBtn = (Button)findViewById(R.id.photo_upload_btn);
		mUploadBtn.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					mEdityEntry.setText("Uploading...");
					
					DoFileUpload(UPLOAD_FILE_PATH);					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
		
	}
	
	private void DoFileUpload(String filePath) throws IOException {
		LogUtil.v("file path = " + filePath);		
		HttpFileUpload(UPLOAD_PAGE_URL, "", filePath);	
	}
	
	//TODO: T: This function need too much time on main thread 
	private void HttpFileUpload(String urlString, String params, String fileName) {
		try {
			
			mFileInputStream = new FileInputStream(fileName);			
			connectUrl = new URL(urlString);
			LogUtil.v("mFileInputStream  is " + mFileInputStream);
			
			// open connection 
			HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			
			// write data
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName+"\"" + lineEnd);
			dos.writeBytes(lineEnd);
			
			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			
			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			
			LogUtil.v("image byte is " + bytesRead);
			
			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}	
			
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			// close streams
			LogUtil.v("File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...			
			
			// get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer b =new StringBuffer();
			while( ( ch = is.read() ) != -1 ){
				b.append( (char)ch );
			}
			String s=b.toString(); 
			LogUtil.v("result = " + s);
			mEdityEntry.setText(s);
			dos.close();			
			
		} catch (Exception e) {
			LogUtil.e("exception " + e.getMessage()+", "+e.toString());
		}		
	}
}

