package cn.jlu.ge.getup;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import cn.jlu.ge.getup.tools.ForegroundService;
import cn.jlu.ge.getup.tools.GetUpMediaPlayer;
import cn.jlu.ge.getup.tools.GetUpVibrator;
import cn.jlu.ge.getup.tools.ShakeDetector;
import cn.jlu.ge.getup.tools.ShakeDetector.OnShakeListener;


public class WakeUpActivity extends Activity {

	GetUpVibrator vibrator;
	ShakeDetector shakeDetector;
	GetUpMediaPlayer mediaPlayer;
	
	TextView tv;
	TextView welcome;
	int shakeTimes;
	public static String mediaNameStr = "the_train_in_the_spring.mp3";;
	public static String welcomeStr = "愿，叫醒你的是梦想";
	
	KeyguardManager mKeyguardManager = null;
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wakeup);
		

		setScreenBrightAndShowWindow();
		
		shakeTimes = 3;
        long[] pattern = {1000, 500, 1000, 500};
        int type = 0;
        
        tv = (TextView)findViewById(R.id.tv);
        tv.setText("剩余摇停次数：" + shakeTimes);
        
//        Bundle bundle = getIntent().getExtras();
//        welcomeStr = bundle.getString("welcomeStr");
//        int rowId = bundle.getInt("rowId");

//        Log.v("WakeUpActivity welcomeStr", "" + welcomeStr);
//        Log.v("WakeUpActivity rowId", "" + rowId);

        welcome = (TextView)findViewById(R.id.welcome);
        welcome.setText(welcomeStr);
        
        WakeUpActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new GetUpMediaPlayer(getApplicationContext(), mediaNameStr);
        mediaPlayer.startPlay();
        
        vibrator = new GetUpVibrator(getApplicationContext());
        
//		vibrator.playVibrate(pattern, type);
		shakeDetector = new ShakeDetector(getApplicationContext());
		shakeDetector.start();
        shakeDetector.registerOnShakeListener(new OnShakeListener(){
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				shakeTimes--;
				if (shakeTimes == 0) {
					shakeDetector.stop();

//					vibrator.cancelVibrate();
					mediaPlayer.stopPlay();
				}
				tv.setText("剩余摇停次数：" + shakeTimes);
			}
        });
        
        Intent foregroundServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
        foregroundServiceIntent.putExtra("doSth", ForegroundService.SHOW_NEXT_ALARM);
        startService(foregroundServiceIntent);
        
	}
	
	public void setScreenBrightAndShowWindow() {
		final Window win = (Window) this.getWindow();
		final WindowManager.LayoutParams params = win.getAttributes();
		params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		wakeLock.acquire();
		wakeLock.release();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
