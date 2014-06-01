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
	public static String welcomeStr = "叫醒你的不是小闹，是梦想！";
	
	// 声明键盘管理器
	KeyguardManager mKeyguardManager = null;
	// 声明电源管理器
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wakeup);
		
		// 点亮屏幕，解锁手机

		setScreenBrightAndShowWindow();
		
        // 初始化摇晃次数
		shakeTimes = 3;
        long[] pattern = {1000, 500, 1000, 500};
        int type = 0;
        
        tv = (TextView)findViewById(R.id.tv);
        tv.setText("闹钟解锁剩余摇晃次数：" + shakeTimes);
        
//        Bundle bundle = getIntent().getExtras();
//        welcomeStr = bundle.getString("welcomeStr");
//        int rowId = bundle.getInt("rowId");

//        Log.v("WakeUpActivity welcomeStr", "" + welcomeStr);
//        Log.v("WakeUpActivity rowId", "" + rowId);

        welcome = (TextView)findViewById(R.id.welcome);
        welcome.setText(welcomeStr);
        
        // 闹铃模块
        WakeUpActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new GetUpMediaPlayer(getApplicationContext(), mediaNameStr);
        mediaPlayer.startPlay();
        
        // 震动模块
        vibrator = new GetUpVibrator(getApplicationContext());
     // 测试音乐播放，暂停震动
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
					// 测试音乐播放，暂停震动
//					vibrator.cancelVibrate();
					mediaPlayer.stopPlay();
				}
				tv.setText("闹钟解锁剩余摇晃次数：" + shakeTimes);
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

		//获取电源的服务
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//获取系统服务
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		//点亮亮屏
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		wakeLock.acquire();// 点亮
		wakeLock.release();// 释放点亮

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
