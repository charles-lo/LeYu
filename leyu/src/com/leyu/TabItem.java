package com.leyu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabItem extends Button{

	public TabItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TabItem(Context context) {
		super(context);
		init();
	}
	
	public TabItem(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		init();
	}
	public TabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	void init() {
		Resources res = getContext().getResources();
		setBackgroundResource(R.drawable.btn_tab);
		setTextColor(R.color.footer);
		setIncludeFontPadding(false);
		setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelOffset(R.dimen.font_01));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);  
		//此处相当于布局文件中的Android:layout_gravity属性  
		lp.gravity = Gravity.CENTER;  
		setLayoutParams(lp);
		this.setGravity(Gravity.CENTER);
//		setLayoutParams(new LinearLayout.LayoutParams(75*3, LinearLayout.LayoutParams.WRAP_CONTENT));
	}
}