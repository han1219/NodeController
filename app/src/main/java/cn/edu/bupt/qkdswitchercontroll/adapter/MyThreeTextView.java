package cn.edu.bupt.qkdswitchercontroll.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.bupt.qkdswitchercontroll.R;

public class MyThreeTextView extends FrameLayout {

	public Context context;

	public int width,height;
	public MyThreeTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.combiningtv, this);
		tv1 = (TextView) view.findViewById(R.id.com_tv_1);
		tv2 = (TextView) view.findViewById(R.id.com_tv_2);
		tv3 = (TextView) view.findViewById(R.id.com_tv_3);
	}

	
@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width=this.getWidth();
		height=this.getHeight();
	}

	TextView tv1, tv2, tv3;

	public MyThreeTextView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.combiningtv, this);
		tv1 = (TextView) view.findViewById(R.id.com_tv_1);
		tv2 = (TextView) view.findViewById(R.id.com_tv_2);
		tv3 = (TextView) view.findViewById(R.id.com_tv_3);
	}

	public MyThreeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.combiningtv, this);
		tv1 = (TextView) view.findViewById(R.id.com_tv_1);
		tv2 = (TextView) view.findViewById(R.id.com_tv_2);
		tv3 = (TextView) view.findViewById(R.id.com_tv_3);

		tv1.setTextSize(20);
		tv2.setTextSize(20);
		tv3.setTextSize(20);
	}

	public void setText(String str){
		tv1.setTextSize(20);

		tv1.setWidth(width);
		tv1.setGravity(Gravity.CENTER);
		tv1.setBackgroundColor(Color.WHITE);
		tv1.setLines(6);
		tv1.setText(str);
		
		tv2.setVisibility(View.GONE);
		tv3.setVisibility(View.GONE);
	}
	public void setText(String str,String str1){
		tv1.setTextSize(20);
		tv2.setTextSize(20);
		tv3.setTextSize(20);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,2.0f);
		params.setMargins(1,1,1,1);
		tv1.setLayoutParams(params);
		params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,1.0f);
		params.setMargins(1,1,1,1);
		tv3.setLayoutParams(params);

		tv1.setWidth(width);
		tv1.setGravity(Gravity.CENTER);
		tv1.setBackgroundColor(Color.WHITE);
		tv1.setLines(6);
		tv1.setText(str);


		tv2.setVisibility(View.GONE);
		tv3.setWidth(width);
		tv3.setGravity(Gravity.CENTER);
		tv3.setBackgroundColor(Color.WHITE);
		tv3.setLines(6);
		tv3.setText(str1);
	}
	public void setText(String str1, String str2, String str3) {
		tv1.setTextSize(20);
		tv2.setTextSize(20);
		tv3.setTextSize(20);

		tv1.setWidth(width);
		tv1.setGravity(Gravity.CENTER);
		tv1.setBackgroundColor(Color.WHITE);
		tv1.setLines(6);
		tv1.setText(str1);
		
		
		tv2.setWidth(width);
		tv2.setGravity(Gravity.CENTER);
		tv2.setBackgroundColor(Color.WHITE);
		tv2.setLines(6);
		tv2.setText(str2);
		
		tv3.setWidth(width);
		tv3.setGravity(Gravity.CENTER);
		tv3.setBackgroundColor(Color.WHITE);
		tv3.setLines(6);
		tv3.setText(str3);
	}

}
