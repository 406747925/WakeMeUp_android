package cn.jlu.ge.dreamclock.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class BitmapCache {
	private Map< String, SoftReference<Bitmap> > imageCache;
	final static String TAG = "BitmapCache";
	private Context context;
	private String path;
	
	public BitmapCache (Context c) {
		imageCache = new HashMap< String, SoftReference<Bitmap> >();
		context = c;
		path = context.getApplicationContext().getCacheDir().getPath();
	}
	
	public boolean putBitmapSRToCache (  final String key , SoftReference<Bitmap> bitmapSR ) {
		String keyStr = key.replaceAll("/", "-");
		if ( imageCache == null ) {
			Log.v(TAG, "imageCache is null.");
			return false;
		}

		imageCache.put(keyStr, bitmapSR);
		
		return true;
	}
	
	public Bitmap getBitmapFromCache (final String key) {
		
		Bitmap bitmap = null;
		String keyStr = key.replace("/", "-");
		
		if ( imageCache == null ) {
			Log.v(TAG, "imageCache is null");
			return null;
		}
		logHashmap();
		SoftReference< Bitmap > bitmapSR = imageCache.get(keyStr);
		
		if ( bitmapSR == null ) {
			
			Log.v(TAG, "bitmapSR is null");
			try {
				
				bitmap = readFileToBitmap(keyStr);
				if ( bitmap != null ) {
					Log.v(TAG, "文件名：" + keyStr + " , 文件大小： " + bitmap.getByteCount() );
					Log.v(TAG, "读取本地文件为bitmap");
					bitmapSR = new SoftReference< Bitmap >(bitmap);
					putBitmapSRToCache( key , bitmapSR );
					return bitmap;
				}
				else {
					Log.v(TAG, "error when read file.");
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			
			bitmap = bitmapSR.get();
			
			if ( bitmap == null ) {
				Log.v(TAG, "bitmap is null");
				return null;
			}
			else
				return bitmap;			
		}
		
		return null;
		
	}
	
	public static Bitmap BytesToBitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}
	
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
	
	public Bitmap readFileToBitmap( String fileName ) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(path + "/" + fileName.replaceAll("/", "-") );
		Bitmap bitmap  = BitmapFactory.decodeStream(fis);
		return bitmap;
	}
	
	public int storeBitmapToFile (final String key, final String fileName) {
		
		String fileNameStr = fileName.replaceAll("/", "-");
		String keyStr = fileName.replaceAll("/", "-");
		
		SoftReference<Bitmap> bitmapSR = imageCache.get(keyStr);
		
		Bitmap bitmap = null;
		if ( bitmapSR != null ) {
			bitmap = bitmapSR.get();
		} else {
			Log.v(TAG, "文件：" + keyStr + " ," + fileNameStr + ", 软引用为空，保存文件失败");
			return -1;
		}

		path = context.getApplicationContext().getCacheDir().getPath();
		
		File f = new File(path, keyStr);
		if (f.exists()) {
			f.delete();
		}
		try {
			
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress( Bitmap.CompressFormat.PNG, 90, out );
			out.flush();
			out.close();
			Log.i(TAG, keyStr + " -> 已经保存");
			
		} catch (FileNotFoundException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} catch (IOException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
		
		return 0;
	}
	
	
	public int getImageFromNet ( final String url, final String key, final int width, final int height, final ImageView IV ) {
		
		String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
		
		AsyncHttpClient imageClient = new AsyncHttpClient();
		
		imageClient.get(Const.HOST + url, new BinaryHttpResponseHandler ( allowedContentTypes ) {

			@SuppressWarnings("deprecation")
			@Override
			public void onFailure(Throwable responseCode, byte[] data) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "获取图片失败", Toast.LENGTH_SHORT).show();
				super.onFailure(responseCode, data);
			}

			@Override
			public void onSuccess(int responseCode, byte[] data) {
				Log.v(TAG, "hehe.");
				Bitmap avatar = BytesToBitmap( data );
				Bitmap zoomAvatar = zoomBitmap( avatar, width, height);
				SoftReference<Bitmap> zoomBitmapSR = new SoftReference<Bitmap>(zoomAvatar);
				Log.v(TAG, zoomBitmapSR.toString() );
				putBitmapSRToCache( key, zoomBitmapSR );
				if ( imageCache.containsKey( key.replaceAll("/", "-") ) ) {
					storeBitmapToFile(key, url);
				}
				BitmapDrawable avatarDrawable = new BitmapDrawable(context.getApplicationContext().getResources(), zoomAvatar);
				IV.setImageDrawable(avatarDrawable);
				avatar.recycle();
				avatarDrawable = null;
				super.onSuccess(responseCode, data);
			}
			
		});
		
		imageClient = null;
		return 0;
	}
	
	void logHashmap () {
		Iterator iterator = imageCache.keySet().iterator();
		Log.d(TAG, "logHashmap here.");
		while( iterator.hasNext() ) {
			SoftReference<Bitmap> bmSR = imageCache.get( iterator.next() );
			if ( bmSR == null )
				Log.v(TAG, "logHashmap bmSR is null.");
			else 
				Log.v(TAG, "bmSR has value.");
		}
	}
	
}
