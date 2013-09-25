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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import kr.re.ec.zigeon.dataset.PhotoUploadDataset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;

public class PhotoUploader extends AsyncTask<PhotoUploadDataset, Integer, Void> {	
	private void DoFileUpload(PhotoUploadDataset imageUpload) throws IOException {
		LogUtil.v("file source path = " + imageUpload.sourcePath);
		HttpFileUpload(Constants.URL_SERVER_IMAGE_UPLOAD_PAGE, imageUpload.type, imageUpload.idx, imageUpload.sourcePath);	
	}
	
	//T: DO NOT INPUT This function on MAIN THREAD. UI frameskip may occur. 
	private void HttpFileUpload(String urlString, int type, int idx, String fileName) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(Constants.URL_SERVER_IMAGE_UPLOAD_PAGE);
			postRequest.setHeader("Accept-Charset", "UTF-8");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			/********** add POST string values *********/
			reqEntity.addPart("type", new StringBody(String.valueOf(type)));
			reqEntity.addPart("idx", new StringBody(String.valueOf(idx)));
			reqEntity.addPart("filename",new StringBody(fileName, Charset.forName("UTF-8")));
			try{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				
				BitmapFactory.Options bfo = new BitmapFactory.Options();
				bfo.inJustDecodeBounds = true;	//DO NOT remove this phrase. it may occur OUT OF MEMORY
				Bitmap bitmap = BitmapFactory.decodeFile(fileName, bfo);
				
				//if image side size is longer than constants, reduce image size 
				if(bfo.outHeight * bfo.outWidth >=
						Constants.IMG_UPLOAD_MAX_SIDE_PIXEL * Constants.IMG_UPLOAD_MAX_SIDE_PIXEL)
				{
					bfo.inSampleSize = (int)Math.pow(2, (int)Math.round(Math.log(Constants.IMG_UPLOAD_MAX_SIDE_PIXEL
							/ (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
				}
				bfo.inJustDecodeBounds = false;
				bfo.inDither = true;
				bitmap = BitmapFactory.decodeFile(fileName, bfo);
				
				//bitmap.compress(CompressFormat.JPEG, 75, bos);	//2,659,566B -> 1,970,481B
				//bitmap.compress(CompressFormat.JPEG, 25, bos);	//2,659,566B -> 809,638B
				
				//current optimization example(Byte)
				//1,820,983(3264x2448,jpg) -> 46,722(816x612,jpg) => 2.6% (Cannot feel broken)
				//1,495,698(1040x6416,jpg) -> 13,883(130x802,jpg) => 0.9% (feel broken)
				//4,995,376(3264x2448,jpg) -> 168,861(816x612,jpg) => 3.4% (Cannot feel broken)
				bitmap.compress(CompressFormat.JPEG, 75, bos);	
				byte[] data = bos.toByteArray();
				ByteArrayBody bab = new ByteArrayBody(data, fileName);
				reqEntity.addPart("picture", bab);
			}
			catch(Exception e){
				LogUtil.v("Exception in Image" + e.toString());
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
			
			System.gc();
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
	 * arg format: type/Index/filename
	 * ex: ldm/3/hanwoo.jpg
	 */
	@Override
	protected Void doInBackground(PhotoUploadDataset... arg0) { 
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

