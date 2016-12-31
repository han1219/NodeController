package cn.edu.bupt.qkdswitchercontroll.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cn.edu.bupt.qkdswitchercontroll.R;


public class TableAdapter extends BaseAdapter {
		private static final String TAG = "TableAdapter";
		ArrayList<TableRow> table;
		Context context;

		LayoutInflater mInflater;

		public TableAdapter(Context context) {
		
			this.context = context;
			mInflater = LayoutInflater.from(context);
		}

		public void setData(ArrayList<TableRow> table){
			this.table = table;
		}
		@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return table.get(position).getSize();
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 14;
	}
		@Override
		public int getCount() {
			return table.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			
			return table.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public void setTV(TextView tView, String str) {
			tView.setGravity(Gravity.CENTER);
			tView.setBackgroundColor(Color.WHITE);
			tView.setLines(6);
			tView.setText(str);
		}

		public void setTV(MyThreeTextView tView, String str1, String str2,
						  String str3) {

			tView.setText(str1, str2, str3);
		}
		public void setTV(MyThreeTextView tView, String str1) {

			tView.setText(str1);
		}
	public void setTV(MyThreeTextView tView, String str1,String str2) {

		tView.setText(str1,str2);
	}
		static class ViewHolder{
			public  TextView tv1;
			MyThreeTextView tv2;
			MyThreeTextView myView1;
			MyThreeTextView myView2;
			MyThreeTextView myView3;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vHolder = null;
			 if (convertView == null) {
				 convertView = mInflater.inflate(R.layout.listitem, null);
				 vHolder=new ViewHolder();
				vHolder.tv1=(TextView) convertView.findViewById(R.id.list_tv_1);
				vHolder.tv2= (MyThreeTextView) convertView.findViewById(R.id.list_tv_2);
				vHolder.myView1=(MyThreeTextView) convertView
						.findViewById(R.id.list_com_tv1);
				vHolder.myView2=(MyThreeTextView) convertView
						.findViewById(R.id.list_com_tv2);
				vHolder.myView3=	(MyThreeTextView) convertView
						.findViewById(R.id.list_com_tv3);
				convertView.setTag(vHolder);
		        }else{
		        	vHolder=(ViewHolder) convertView.getTag();
		        }

			 TableRow tableRow = table.get(position);
			int len=getItemViewType(position);
			Log.i(TAG, "getItemViewType"+len);
			if(len==5){
				setTV(vHolder.tv1, String.valueOf(tableRow.getCellValue(0).value));
				setTV(vHolder.tv2, String.valueOf(tableRow.getCellValue(1).value));
				setTV(vHolder.myView1, String.valueOf(tableRow.getCellValue(2).value));
				setTV(vHolder.myView2, String.valueOf(tableRow.getCellValue(3).value));
				setTV(vHolder.myView3, String.valueOf(tableRow.getCellValue(4).value));
//			}
//			else if(len==11){
//				setTV(vHolder.tv1, String.valueOf(tableRow.getCellValue(0).value));
//				setTV(vHolder.tv2, String.valueOf(tableRow.getCellValue(1).value));
//				setTV(vHolder.myView1, String.valueOf(tableRow.getCellValue(2).value),
//						String.valueOf(tableRow.getCellValue(3).value),
//						String.valueOf(tableRow.getCellValue(4).value));
//				setTV(vHolder.myView2, String.valueOf(tableRow.getCellValue(5).value),
//						String.valueOf(tableRow.getCellValue(6).value),
//						String.valueOf(tableRow.getCellValue(7).value));
//				setTV(vHolder.myView3, String.valueOf(tableRow.getCellValue(8).value),
//						String.valueOf(tableRow.getCellValue(9).value),
//						String.valueOf(tableRow.getCellValue(10).value));
			} else  if(len==12){
					setTV(vHolder.tv1, String.valueOf(tableRow.getCellValue(0).value));
					setTV(vHolder.tv2, String.valueOf(tableRow.getCellValue(1).value),String.valueOf(tableRow.getCellValue(2).value));
					setTV(vHolder.myView1, String.valueOf(tableRow.getCellValue(3).value),
							String.valueOf(tableRow.getCellValue(4).value),
							String.valueOf(tableRow.getCellValue(5).value));
					setTV(vHolder.myView2, String.valueOf(tableRow.getCellValue(6).value),
							String.valueOf(tableRow.getCellValue(7).value),
							String.valueOf(tableRow.getCellValue(8).value));
					setTV(vHolder.myView3, String.valueOf(tableRow.getCellValue(9).value),
							String.valueOf(tableRow.getCellValue(10).value),
							String.valueOf(tableRow.getCellValue(11).value));
			}

//			if (tableRow.getSize() == 11) {
//				setTV(vHolder.tv1, String.valueOf(tableRow.getCellValue(0).value));
//				setTV(vHolder.tv2, String.valueOf(tableRow.getCellValue(1).value));
//				setTV(vHolder.myView1, String.valueOf(tableRow.getCellValue(2).value),
//						String.valueOf(tableRow.getCellValue(3).value),
//						String.valueOf(tableRow.getCellValue(4).value));
//				setTV(vHolder.myView2, String.valueOf(tableRow.getCellValue(5).value),
//						String.valueOf(tableRow.getCellValue(6).value),
//						String.valueOf(tableRow.getCellValue(7).value));
//				setTV(vHolder.myView3, String.valueOf(tableRow.getCellValue(8).value),
//						String.valueOf(tableRow.getCellValue(9).value),
//						String.valueOf(tableRow.getCellValue(10).value));
//				
//				Log.i(TAG, "tableRow 11更新");
//			}else{
//				setTV(vHolder.tv1, String.valueOf(tableRow.getCellValue(0).value));
//				setTV(vHolder.tv2, String.valueOf(tableRow.getCellValue(1).value));
//				setTV(vHolder.myView1, String.valueOf(tableRow.getCellValue(2).value));
//				setTV(vHolder.myView2, String.valueOf(tableRow.getCellValue(3).value));
//				setTV(vHolder.myView3, String.valueOf(tableRow.getCellValue(4).value));
//				Log.i(TAG, "tableRow 5更新");
//			}
			
			return convertView;
		}
		
		public  void updateItem(ListView lv,int position){
			int firstVisiblePosition=lv.getFirstVisiblePosition();
			int  lastVisiblePosition =lv.getLastVisiblePosition();
//			getData(table);
			Log.i(TAG, firstVisiblePosition+"  "+lastVisiblePosition+"  "+position);
//			View view=lv.getChildAt(position);
//			getView(position, view, lv);
			
			for(;firstVisiblePosition<=lastVisiblePosition;firstVisiblePosition++){
//				if(position==lv.getI)
				View view=lv.getChildAt(position-firstVisiblePosition);
				getView(position, view, lv);
			}
//			if(position>=firstVisiblePosition&&position<=lastVisiblePosition){
//				View view=lv.getChildAt(position-firstVisiblePosition);
//				getView(position, view, lv);
//			}
		}
}
