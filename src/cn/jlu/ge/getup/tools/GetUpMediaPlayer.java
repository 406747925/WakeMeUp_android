package cn.jlu.ge.getup.tools;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class GetUpMediaPlayer {
	
	MediaPlayer mediaPlayer;
	AssetManager assetManager;
	
	public GetUpMediaPlayer(Context context, String resName) {
		assetManager = context.getAssets();
		try {
			AssetFileDescriptor descriptor = assetManager.openFd(resName);
			this.mediaPlayer = new MediaPlayer();
			try {
				this.mediaPlayer.setDataSource(descriptor.getFileDescriptor(), 
						descriptor.getStartOffset(), 
						descriptor.getLength());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				Log.v("illegal Argument Error", e.toString());
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				Log.v("Security Error", e.toString());
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.v("illegal State Error", e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.v("IOException Error", e.toString());
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.v("IOException e1", e1.toString());
		}
		

	}
	
	public boolean startPlay() {
		
		this.mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer player) {
				// TODO Auto-generated method stub
				try {
					player.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					Log.v("2.illegal Argument Error", e.toString());
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					Log.v("2.Security Error", e.toString());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					Log.v("2.illegal State Error", e.toString());
				} finally {
					Log.v("2.Something Wrong.", "Dont Know What Happened.");
				}
			}
			
		});

		try {
			this.mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Log.v("3.illegal Argument Error", e.toString());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			Log.v("3.Security Error", e.toString());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.v("3.illegal State Error", e.toString());
		} finally {
			Log.v("3.Something Wrong.", "Dont Know What Happened.");
		}
		
		return false;
	}
	
	public boolean stopPlay() {
		this.mediaPlayer.stop();
		this.mediaPlayer.release();
		return true;
	}
}
