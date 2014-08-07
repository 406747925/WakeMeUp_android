package cn.jlu.ge.knightView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jlu.ge.getup.R;

public class KnightNumberPicker extends LinearLayout {

	private TextView centerObjectText;
	private TextView headObjectText;
	private TextView tailObjectText;
	private TextView centerObjectDescText;
	
	int maxValue;
	int minVlaue;
	
	int headNum;
	int centerNum;
	int tailNum;
	
	int headTextSize;
	int centerTextSize;
	int tailTextSize;

	float centerX;
	float centerY;
	float minMoveDelta;
	
	float layoutX;
	float layoutY;
	float layoutHeight;
	float layoutWidth;
	
	float downTime;
	float downX;
	float downY;
	
	float upTime;
	float upX;
	float upY;
	
	float deltaTime;
	float deltaX;
	float deltaY;
	
	public KnightNumberPicker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	public KnightNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.number_picker, this);

		layoutX = this.getX();
		layoutY = this.getY();
		layoutHeight = this.getHeight();
		layoutWidth = this.getWidth();
		
		centerObjectText = (TextView) findViewById (R.id.centerObject);
		headObjectText = (TextView) findViewById (R.id.headObject);
		tailObjectText = (TextView) findViewById (R.id.tailObject);
		centerObjectDescText = (TextView) findViewById (R.id.centerObjectDesc);
		
		this.setOnTouchListener(new OnTouchListener () {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				int action = event.getAction();
				
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					Log.v("MotionEvent", "ACTION_DOWN");
					break;
				case MotionEvent.ACTION_MOVE:
					printSamples(event);
					final int historySize = event.getHistorySize();
					final int pointerCount = event.getPointerCount();
					if ( historySize > 1 ) {
						downTime = event.getHistoricalEventTime(0);
						downX = event.getHistoricalX(0, 0);
						downY = event.getHistoricalY(0, 0);
						upTime = event.getHistoricalEventTime(historySize - 1);
						upX = event.getHistoricalX(pointerCount - 1, historySize - 1);
						upY = event.getHistoricalY(pointerCount - 1, historySize - 1);
						deltaTime = upTime - downTime;
						deltaX = downX - upX;
						deltaY = downY - upY;
						Log.v("MotionEvent", "deltaY: " + deltaY);
						if ( deltaY >= minMoveDelta ) {
							Log.v("delta length", "deltaX: " + deltaX + ", deltaY: " + deltaY);
							setNumText(headNum + 1, centerNum + 1, tailNum + 1);
						} else if ( -1*deltaY >= minMoveDelta ) {
							Log.v("delta length", "deltaX: " + deltaX + ", deltaY: " + deltaY);
							setNumText(headNum - 1, centerNum - 1, tailNum - 1);
						}
					}

					Log.v("MotionEvent", "ACTION_MOVE");
					break;
		        case MotionEvent.ACTION_CANCEL:
		        	Log.v("MotionEvent", "ACTION_CANCEL");
		            break;  
		        case MotionEvent.ACTION_UP:
		        	Log.v("MotionEvent", "ACTION_UP");
		            break;
				}
				return true;
			}
			
		});
	}
	
	public KnightNumberPicker(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	public void setText (String head, String center, String tail) {
		centerObjectText.setText("" + center);
		headObjectText.setText("" + head);
		tailObjectText.setText("" + tail);
	}
	
	public void setMaxAndMinValue (int max, int min) {
		maxValue = max;
		minVlaue = min;
	}
	
	public void setNumText (int headObjectNum, int centerObjectNum, int tailObjectNum) {
		
		headNum = headObjectNum;
		centerNum = centerObjectNum;
		tailNum = tailObjectNum;
		
		makeNumPositive();
		
		headObjectText.setText("" + headNum);
		centerObjectText.setText("" + centerNum);
		tailObjectText.setText("" + tailNum);
		
	}
	
	private void makeNumPositive () {
		if ( ( headNum == -1 ) ) {
			headNum = maxValue - 1;
		} else if ( centerNum == -1 ) {
			centerNum = maxValue - 1;
		} else if ( tailNum == -1 ) {
			tailNum = maxValue - 1;
		}
		if ( headNum == maxValue ) {
			headNum = 0;
		} else if ( centerNum == maxValue ) {
			centerNum = 0;
		} else if ( tailNum == maxValue ) {
			tailNum = 0;
		}
		if ( headNum == maxValue + 1 ) {
			headNum = 1;
		} else if ( centerNum == maxValue + 1 ) {
			centerNum = 1;
		} else if ( tailNum == maxValue + 1 ) {
			tailNum = 1;
		}
	}
	
	public void setPickerObjectDesc (String desc, int size) {
		centerObjectDescText.setTextSize(size);
		centerObjectDescText.setText(desc);
	}
	
	public void setTextSize (int headSize, int centerSize, int tailSize) {
		
		headTextSize = headSize;
		centerTextSize = centerSize;
		tailTextSize = tailSize;
		
		centerObjectText.setTextSize(centerSize);
		headObjectText.setTextSize(headSize);
		tailObjectText.setTextSize(tailSize);
		
		centerX = centerObjectText.getX();
		centerY = centerObjectText.getY();

	}

	public void setMinMoveDelta (float delta) {
		minMoveDelta = delta;
		Toast.makeText(getContext(), "minMoveDelta: " + minMoveDelta, Toast.LENGTH_SHORT).show();
	}
	
	public int getCenterNum () {
		return centerNum;
	}
	
	void printSamples(MotionEvent ev) {
	     final int historySize = ev.getHistorySize();
	     final int pointerCount = ev.getPointerCount();
	     Log.v("MotionEvent", "historySize:" + historySize);
	     Log.v("MotionEvent", "pointerCount:" + pointerCount);
	     for (int h = 0; h < historySize; h++) {
	         Log.v("MotionEvent", "At historical time " + ev.getHistoricalEventTime(h));
	         for (int p = 0; p < pointerCount; p++) {
	             Log.v("MotionEvent", "Historical pointer " + ev.getPointerId(p) + ": (" + ev.getHistoricalX(p, h) + "," + ev.getHistoricalY(p, h) + ")" );
	         }
	     }
	     Log.v("MotionEvent", "At time " + ev.getEventTime());
	     for (int p = 0; p < pointerCount; p++) {
	    	 Log.v("MotionEvent", "Pointer " + ev.getPointerId(p) + ": (" + ev.getX(p) + "," + ev.getY(p) + ")" );
	     }
	 }
	
}
