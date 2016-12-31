package cn.edu.bupt.qkdswitchercontroll.httpServer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;

import cn.edu.bupt.qkdswitchercontroll.socketServer.WSSControlThread;
import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

	private static final String TAG = "HttpServer";
	public static Handler handler;
	public  static	ArrayBlockingQueue<Integer> queue=new ArrayBlockingQueue<Integer>(1);
	public HttpServer(int port) {
		super(port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response serve(IHTTPSession session) {
		// TODO Auto-generated method stub
		Gson gsonparse=new Gson();

//	byte[] buffer = new byte[0];
//		try {
//			int len =session.getInputStream().available();
//			buffer=new byte[len];
//			Log.i(TAG,"收到长度:"+len);
//			session.getInputStream().read(buffer);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String json=new String(buffer);
		String json = session.getUri().substring(1);
		Log.i(TAG, json);

		CommunicateObject comObject=gsonparse.fromJson(json, CommunicateObject.class);
		Message msg=new Message();
		if(comObject==null){
			Log.i(TAG,"server 解析出来为空");
			return new Response("0");
		}
		msg.obj=comObject;
		WSSControlThread.handler.sendMessage(msg);
//		Looper.prepare();
//		final String[] response = new String[1];
//		response[0]="init";
//

//		handler=new Handler(){   // 处理返回信息。
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				if(msg.arg1==1){
//					response[0]="OK";
//				}
//				else {
//					response[0]="error";
//				}
//				Log.i(TAG, "链路建立后配置后返回信息1111"+response[0]);
//				
//			}
//		};
//		Looper.loop();
		
		int WSSResponse = 0;
		try {
			WSSResponse = queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "链路建立后配置后返回信息"+WSSResponse);
		return  new Response(String.valueOf(WSSResponse));
	}
}
