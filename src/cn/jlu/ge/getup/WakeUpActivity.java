package cn.jlu.ge.getup;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import cn.jlu.ge.getup.tools.GetUpVibrator;
import cn.jlu.ge.getup.tools.ShakeDetector;
import cn.jlu.ge.getup.tools.ShakeDetector.OnShakeListener;


public class WakeUpActivity extends Activity {

	GetUpVibrator vibrator;
	ShakeDetector shakeDetector;
	
	TextView tv;
	TextView welcome;
	int shakeTimes;
	
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
        
//        welcome = (TextView)findViewById(R.id.welcome);
//        welcome.setText("叫醒的是梦想，摇醒的是觉悟");
        
        vibrator = new GetUpVibrator(getApplicationContext());
		vibrator.playVibrate(pattern, type);
		shakeDetector = new ShakeDetector(getApplicationContext());
		shakeDetector.start();
        shakeDetector.registerOnShakeListener(new OnShakeListener(){
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				shakeTimes--;
				if (shakeTimes == 0) {
					shakeDetector.stop();
					vibrator.cancelVibrate();
				}
				tv.setText("闹钟解锁剩余摇晃次数：" + shakeTimes);
			}
        });
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
