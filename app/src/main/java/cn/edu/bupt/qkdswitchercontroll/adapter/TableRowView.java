package cn.edu.bupt.qkdswitchercontroll.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TableRowView extends LinearLayout {
	private static final String TAG = "TableRowView";

	public TableRowView(Context context, TableRow tableRow) {
		super(context);

		int textCount=0;
		this.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < tableRow.getSize(); i++) {// 逐个格单元添加到行
			TableCell tableCell = tableRow.getCellValue(i);
			LayoutParams layoutParams = new LayoutParams(
					tableCell.width, tableCell.height);// 按照格单元指定的大小设置空间
			layoutParams.setMargins(0, 0, 2, 2);// 预留空隙制造边框
			LayoutParams layoutVertical=new LayoutParams(tableCell.width, tableCell.height/3);
			LinearLayout layout=new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			layoutVertical.setMargins(0, 2, 2, 2);
			if(i>1){
				TextView tView=new TextView(context);
				tView.setLines(6);
				tView.setGravity(Gravity.CENTER);
				tView.setBackgroundColor(Color.WHITE);
				tView.setText(String.valueOf(tableCell.value));
//				layoutVertical.addView(tView);
				layout.addView(tView, layoutVertical);
				textCount++;
				Log.i(TAG, "添加三个的");
				if(textCount==3){
					addView(layout, layoutParams);
					
					Log.i(TAG, "进入垂直三个的");
					textCount=0;
				}
			}
//			if (tableCell.type == TableCell.STRING) {// 如果格单元是文本内容
			if(i<=1){
				TextView textCell = new TextView(context);
				textCell.setLines(6);
				textCell.setGravity(Gravity.CENTER);
				textCell.setBackgroundColor(Color.WHITE);// 背景黑色
				textCell.setText(String.valueOf(tableCell.value));
				addView(textCell, layoutParams);
			} 
//			else if (tableCell.type == TableCell.IMAGE) {// 如果格单元是图像内容
//				ImageView imgCell = new ImageView(context);
//				imgCell.setBackgroundColor(Color.BLACK);// 背景黑色
//				imgCell.setImageResource((Integer) tableCell.value);
//				addView(imgCell, layoutParams);
//			}
		}
//		this.setBackgroundColor(Color.WHITE);// 背景白色，利用空隙来实现边框
	}
}



