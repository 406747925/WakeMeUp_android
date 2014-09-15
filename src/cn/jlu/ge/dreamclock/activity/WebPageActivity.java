package cn.jlu.ge.dreamclock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import cn.jlu.ge.dreamclock.R;
import cn.jlu.ge.dreamclock.tools.MenuFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class WebPageActivity extends BaseActivity {
	private float startx=0;
	private float starty=0;
	private float endx=0;
	private float endy=0;
	private boolean flag=false;

	public WebPageActivity() {
		super(R.string.app_name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		init();
		
	}
	
	void init () {
	//	getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setContentView(R.layout.activity_webpage);
		
		viewInit();
		
		// 因为在父类 BaseActivity 的 onCreate 方法执行时， 所需要的 Fragment 控件必须是 子控件，
        // 方法中的 FragmentTransaction 会使用 id 资源( R.id.menu_frame2 ) 引用 Fragment 控件，
        // 如果未先将对应的 Fragment 控件设置为子控件进行初始化， FragmentManager 将会找不到这个子控件，
		// 而在绘制界面时才抛出运行时异常
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame2, new MenuFragment()).commit();
		
	}
	
	void viewInit () {

		bottomBarInit();
		
		Intent urlIntent = getIntent();
		Bundle bundle = urlIntent.getExtras();
		String url = bundle.getString("url");
		
		// Just test
		Intent intent=getIntent();
		
		url =intent.getStringExtra("url");
		
		final WebView positiveWebView = (WebView) findViewById(R.id.webPage);
		
		positiveWebView.setWebViewClient(new WebViewClient () {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});

		positiveWebView.loadUrl(url);
//		positiveWebView.setOnTouchListener(new OnTouchListener() {	
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				// TODO Auto-generated method stub
//				if(arg1.getAction()==MotionEvent.ACTION_DOWN)
//				{
//					startx=arg1.getX();
//					starty=arg1.getY();
//					flag=false;
//				}
//				else if(arg1.getAction()==MotionEvent.ACTION_MOVE)
//				{
//					endx=arg1.getX();
//					endy=arg1.getY();
//				}
//				else if(arg1.getAction()==MotionEvent.ACTION_UP)
//				{
//					flag=true;
//				}
//
//				//	barHide();
//
//				return false;
//			}
//		});
		barHide();
//positiveWebView.setOnCustomScroolChangeListener(new WebPage.ScrollInterface() {
//	
//	@Override
//	public void onSChanged(int l, int t, int oldl, int oldt) {
//		// TODO Auto-generated method stub
//		float webViewContentHeight=positiveWebView.getContentHeight() * positiveWebView.getScale();
//
//float webViewCurrentHeight=(positiveWebView.getHeight() + positiveWebView.getScrollY());
//
//if ((webViewContentHeight-webViewCurrentHeight) == 0) {
//	barShow();
//	}
//if(positiveWebView.getScrollY() == 0){  
//    barShow();     
//    }  
//if(oldt-t<0&&endy-starty<0)
//	barHide();
//}
//}); 
//	           
//		
//	

	}
	///////////////
	void barHide(){
		  View view=findViewById(R.id.topBarLayout);
		  view.setVisibility(View.GONE);
	}
	void barShow(){
		  View view=findViewById(R.id.topBarLayout);
		  view.setVisibility(View.VISIBLE);
	}
	void bottomBarInit () {
		Button firstBottomBarBtn = (Button) findViewById(R.id.firstBottomBarBtn);
		Button secBottomBarBtn = (Button) findViewById(R.id.secBottomBarBtn);
		Button thirdBottomBarBtn = (Button) findViewById(R.id.thirdBottomBarBtn);
		firstBottomBarBtn.setText(R.string.bottom_bar_first);
		secBottomBarBtn.setText("text");
		thirdBottomBarBtn.setText("third");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	
	

}