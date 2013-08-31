/**
 * Class Name: PhotoDownloader
 * Description: download photo from server using AsyncTask
 * Author: KimTaehee slhyvaa@nate.com
 * Version: 0.0.1
 * Created Date: 130831
 * Modified Date: 
 */

package kr.re.ec.zigeon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import kr.re.ec.zigeon.util.PhotoDownloader.DownloadedDrawable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class PhotoDownloader
{
	public static final int IMGAE_CACHE_LIMIT_SIZE = 50;
	public static HashMap<String, Bitmap> mImageCache = new HashMap<String, Bitmap>();
	
	public static void download(String url, ImageView imageView)
	{
		Bitmap cachedImage = mImageCache.get(url);
		if(cachedImage != null)
		{
			LogUtil.i("there is a cached image!");
			imageView.setImageBitmap(cachedImage);
		}
		else if(cancelPotentialDownload(url, imageView))
		{
			if(mImageCache.size() > IMGAE_CACHE_LIMIT_SIZE)
			{
				mImageCache.clear();
			}
			
			ImageDownloaderTask task = new ImageDownloaderTask(url, imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	private static boolean cancelPotentialDownload(String url, ImageView imageView)
	{
		LogUtil.i("cancelPotenialDownload called!");
		ImageDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if(bitmapDownloaderTask != null)
		{
			String bitmapUrl = bitmapDownloaderTask.url;
			if((bitmapUrl == null) || (!bitmapUrl.equals(url)))
			{
				bitmapDownloaderTask.cancel(true);
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	private static ImageDownloaderTask getBitmapDownloaderTask(ImageView imageView)
	{
		LogUtil.i("ImageDownloaderTask called!");
		if(imageView != null)
		{
			Drawable drawable = imageView.getDrawable();
			if(drawable instanceof DownloadedDrawable)
			{
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	static class DownloadedDrawable extends ColorDrawable
	{
		private final WeakReference<ImageDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(ImageDownloaderTask bitmapDownloaderTask)
		{
			super(Color.TRANSPARENT);
			LogUtil.i("Contructor called!");
			bitmapDownloaderTaskReference = new WeakReference<ImageDownloaderTask>(bitmapDownloaderTask);
		}

		public ImageDownloaderTask getBitmapDownloaderTask()
		{
			return bitmapDownloaderTaskReference.get();
		}
	}
}

class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap>
{
	public String url;
	public String targetUrl;
	private WeakReference<ImageView> imageViewReference;

	public ImageDownloaderTask(String url, ImageView imageView)
	{
		this.targetUrl = url;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params)
	{
		return downloadBitmap(params[0]);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		if(isCancelled())
		{
			bitmap = null;
		}

		if(imageViewReference != null)
		{
			ImageView imageView = imageViewReference.get();
			ImageDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
			
			if(this == bitmapDownloaderTask)
			{
				PhotoDownloader.mImageCache.put(targetUrl, bitmap);
				imageView.setImageBitmap(bitmap);
			}
		}
	}
	
	private ImageDownloaderTask getBitmapDownloaderTask(ImageView imageView)
	{
		LogUtil.i("getBitmpaDownloaderTask called");
		//if(imageView != null)
		{
			LogUtil.i("imageview isn't null");
			Drawable drawable = imageView.getDrawable();
			if(drawable instanceof DownloadedDrawable)
			{
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	static Bitmap downloadBitmap(String url)
	{
		LogUtil.i("downloadBitmap called");
		final HttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		try
		{
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK)
			{
				Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if(entity != null)
			{
				InputStream inputStream = null;
				//BitmapFactory.Options options = new BitmapFactory.Options();
				//options.inSampleSize = 2;
				
				try
				{
					inputStream = entity.getContent();
					
					//save to file test. 130822 KTH
					LogUtil.i("**********start to save image file**********");
					File root = Environment.getExternalStorageDirectory();
					File file = new File(root, "fucked.jpg");
					FileOutputStream f = new FileOutputStream(file);
					LogUtil.i("what a bitch filepath: " + file.getPath());
					byte[] buffer = new byte[1024];
					int len1 =0 ;
					LogUtil.i("is inputStream null? => " + (inputStream ==null));
					LogUtil.i("is fileOutputStream null? => " + (f ==null));
					while ((len1 = inputStream.read(buffer)) > 0) {
						f.write(buffer, 0, len1);
					}
					f.close();
					
					final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
					LogUtil.i("is bitmap null? => " + (bitmap == null));
					//LogUtil.i("bitmap.getBytecount" + bitmap.getByteCount()); 
					//final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options);
					return bitmap;
				}
				finally
				{
					if(inputStream != null)
					{
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		}
		catch(Exception e)
		{
			LogUtil.e("SHIT ERROR - code " + e.toString());
			e.printStackTrace();
			getRequest.abort();
		}
		return null;
	}

	static class FlushedInputStream extends FilterInputStream
	{
		public FlushedInputStream(InputStream inputStream)
		{
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException
		{
			long totalBytesSkipped = 0L;
			while(totalBytesSkipped < n)
			{
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if(bytesSkipped == 0L)
				{
					int bytes = read();
					if(bytes < 0)
					{
						break; // we reached EOF
					}
					else
					{
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
