package cn.edu.bupt.qkdswitchercontroll.device;

import android.R.integer;
import android.util.Log;
import android.widget.Toast;

import com.topeet.serialtest.Com;
import com.topeet.serialtest.serial;

import cn.edu.bupt.qkdswitchercontroll.activity.MainActivity;
import cn.edu.bupt.qkdswitchercontroll.wssconstant.WSSCmd;

public class WSSSetup {
	private static final String TAG = "WSSSETUP";
	private WSSSetup(){
		
	}
	public static class WSSSetupSingle{
		public static final WSSSetup SINGLE=new WSSSetup();
	}
	public static WSSSetup getInstance() {

		return WSSSetupSingle.SINGLE;
	}
	public  boolean checkWSS(serial com,int flag){

		String recv=sendAndGetResponse(com, flag, WSSCmd.COMMEND_SN0);
		if(recv==null){
			try {
				Thread.sleep(1000);
				recv=sendAndGetResponse(com, flag, WSSCmd.COMMEND_SN0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i(TAG, "检测WSS时返回的信息为："+recv);
		if(recv==null){
			Log.i(TAG, "recv data is null");
			return false;
		}
		if(recv.contains("SN126726")||recv.contains("SN127049"))
			return true;
		else {
			return false;
		}
		
	}

	/*
	 * ���ʹ���ָ���ȡ����ֵ
	 */
	public  String sendAndGetResponse(serial com,int flag,String cmd){
		if(WriteToSerial(com, flag, cmd)==-1){
			Log.i(TAG, "write to serial error");
			System.out.println("write to serial error");
			return null;
		}
		try {
			if (cmd.contains("RRA?")||cmd.contains("URA")) {//通道配置指令，延时一会
				Thread.sleep(1000);
			} else if(cmd.contains("RSW")){
				Thread.sleep(2000);
			}else{
				Thread.sleep(3000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = readFromSerial(cmd, com,flag);

		Log.i(TAG, "WSS返回信息为:"+res);
		return res;
	}
	
	private String readFromSerial(String cmd, serial com, int flag) {
		byte[] reader=new byte[3024];
		int len=0;
		if(flag==Com.WSS1) {
			len= uartProxy.readData(USB2SerialProxy.PORT.A, reader);
		}
		if(flag==Com.WSS2)
			len=uartProxy.readData(USB2SerialProxy.PORT.B,reader);
//		int [] reader=com.Read(flag);
		if(len==0){
			try {
				Thread.currentThread().sleep(100);
//				reader=com.Read(flag);
				if(flag==Com.WSS1)
					len=uartProxy.readData(USB2SerialProxy.PORT.A,reader);
				if(flag==Com.WSS2)
					len=uartProxy.readData(USB2SerialProxy.PORT.B,reader);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			return null;
		}
		Log.i(TAG,"接收到数据长度："+len);
		return new String(reader, 0, len);
	}

	private final USB2SerialProxy uartProxy = MainActivity.getContext();
	public int WriteToSerial(serial com,int flag,String tx) {
		Log.i(TAG,uartProxy.hashCode()+"WSS Uart");
		int[] text = new int[tx.length()];
		System.out.println("tx len" + tx.length());
		for (int i = 0; i < tx.length(); i++) {
			text[i] = tx.charAt(i);
		}
		Log.i(TAG,uartProxy.hashCode()+"  "+"tx:"+tx);
//		return com.Write(text, tx.length(),flag);
		if (flag == Com.WSS1)
			return uartProxy.sendMessage(USB2SerialProxy.PORT.A, tx.getBytes());
		else
			return uartProxy.sendMessage(USB2SerialProxy.PORT.B, tx.getBytes());
	}
}
