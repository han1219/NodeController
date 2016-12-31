package cn.edu.bupt.qkdswitchercontroll.socketServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import com.topeet.serialtest.Com;
import com.topeet.serialtest.serial;

import cn.edu.bupt.qkdswitchercontroll.activity.MainActivity;
import cn.edu.bupt.qkdswitchercontroll.adapter.TableCell;
import cn.edu.bupt.qkdswitchercontroll.adapter.TableRow;
import cn.edu.bupt.qkdswitchercontroll.constant.ServerConstant;
import cn.edu.bupt.qkdswitchercontroll.constant.SignalConstant;
import cn.edu.bupt.qkdswitchercontroll.constant.ViewConstant;
import cn.edu.bupt.qkdswitchercontroll.device.WSSSetup;
import cn.edu.bupt.qkdswitchercontroll.httpServer.CommunicateObject;
import cn.edu.bupt.qkdswitchercontroll.httpServer.HttpServer;
import cn.edu.bupt.qkdswitchercontroll.view.LineUsing;
import cn.edu.bupt.qkdswitchercontroll.wssconstant.WSSCmd;

//与WSS交互类
public class WSSControlThread implements Runnable {

	private static final String TAG = "SocketThread";
	public  serial SWSS2 = new serial();
	public  serial SWSS1 = new serial();

	// 设置一个保存链路信息的public ArrayList<> msg;
	public void init() {

		int fd2= SWSS2.Open(4, 115200, Com.WSS2);
		Log.i(TAG, "Open WSS2 at fd"+fd2);
		int fd1=SWSS1.Open(3, 115200, Com.WSS1);//
		Log.i(TAG, "Open WSS1 at fd"+fd1);
		
	}

	public boolean checkDevice() {
		try {
			if (!checkWSS()) {
				Thread.sleep(1000);
				if (!checkWSS()) {
					Log.i(TAG, "init error,please restart");
					return false;
				}
			}
			Log.i(TAG, "WSS check OK!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	public static Handler handler;
	public int fromPort;
	public int userNums;

	public byte[] BandRoute = new byte[6];
	List<CommunicateObject> signalList = new ArrayList<CommunicateObject>();
	Map<String, CommunicateObject> signalMap = new HashMap<String, CommunicateObject>();
	public static  List<Integer> WSS1channel =new ArrayList<Integer>();
	public static 	List<Integer> WSS2channel =new ArrayList<Integer>();
	public static 	List<Integer> Tmpchannel =new ArrayList<Integer>(3);
	@Override
	public void run() {
		// TODO Auto-generated method stub
		init();
		// send check result to server
		int num=0;
		while (!checkDevice()){
			if(num==0) {
				Message msg = new Message();
				msg.obj = "WSS check error,please reset WSS!";
				MainActivity.handlerTV.sendMessage(msg);
				num++;
			}
		}
		Message msg=new Message();
		msg.obj="WSS check OK!";
		MainActivity.handlerTV.sendMessage(msg);
		Looper.prepare();

		while (true) {
			// 根据解析的json数据控制相应的通道。是否需要记录已用通道，还是直接可以查询WSS的该通道是否可用
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {// Server 收到消息回调此处
					super.handleMessage(msg);
					CommunicateObject comObject = (CommunicateObject) msg.obj;
					Log.i(TAG, "接收到Server传来的信息");
					signalMap.put(comObject.getSourceIP() + comObject.FromFlag,
							comObject);
					int conflictFlag=ConflishCheck(comObject);
					if (conflictFlag==0){
						try {
							HttpServer.queue.put(conflictFlag);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return;
					}
					int response = LinkEstablish(comObject);
					Log.i(TAG, "配置后返回信息" + response);
					if (response == 1) {
						msg=new Message();
						msg.obj="链路配置成功";

						MainActivity.handlerTV.sendMessage(msg);
						TableCell[] data = new TableCell[12];// 每行5个单元s
						data[0]=new TableCell(comObject.getFromFlag(),
								TableCell.STRING);
						data[1]=new TableCell(comObject.getClassicDesIP(),
								TableCell.STRING);
						data[2]=new TableCell(comObject.getQuantumDesIP(),
								TableCell.STRING);

						data[3]=new TableCell(comObject.getClassicDesIP()+"经典",
								TableCell.STRING);
						data[4]=new TableCell(comObject.getQuantumDesIP()+"量子",
								TableCell.STRING);

						data[5]=new TableCell(comObject.getQuantumDesIP()+"同步",
								TableCell.STRING);
						data[6]=new TableCell(comObject.getClassicOptical(),
								TableCell.STRING);
						data[7]=new TableCell(comObject.getQuantumOptical(),
								TableCell.STRING);
						data[8]=new TableCell(comObject.getSynOptical(),
								TableCell.STRING);
						data[9]=new TableCell(Tmpchannel.get(2),
								TableCell.STRING);
						data[10]=new TableCell(Tmpchannel.get(0),
								TableCell.STRING);
						data[11]=new TableCell(Tmpchannel.get(1),
								TableCell.STRING);
						Tmpchannel.clear();

						MainActivity.table.add(new TableRow(data));
//						if(comObject.getIsConnect()!=SignalConstant.LinkConnect){
//							MainActivity.table.remove(new TableRow(data));
//							updateIndex--;
//						} else {
//							MainActivity.table.add(new TableRow(data));
//							updateIndex++;
//						}
						Message msg11 = new Message();
//						msg11.arg1 = updateIndex;
//						msg11.obj=comObject;
						MainActivity.handler.sendMessage(msg11);
//						updateIndex++;

						Log.i(TAG, "response" + BandRoute[0] + BandRoute[1]
								+ BandRoute[3] + BandRoute[4]);
						if (BandRoute[0] == 1) {// 第一WSS
							LineUsing.usingLine[ViewConstant.WSS1]=1;
							if (BandRoute[1] == 1 && BandRoute[4] == 1) { // 量子光经典光都从1出去
								LineUsing.usingLine[ViewConstant.WSS1_LINE1] = 11;
							} else if (BandRoute[1] == 2 && BandRoute[4] == 1) { // 量子光从2出去,经典从1出
								LineUsing.usingLine[ViewConstant.WSS1_LINE2] = 11;
								LineUsing.usingLine[ViewConstant.WSS1_LINE1] = 10;

							} else if (BandRoute[1] == 1 && BandRoute[4] == 2) { // 量子光从2出去,经典从1出
								LineUsing.usingLine[ViewConstant.WSS1_LINE1] = 11;
								LineUsing.usingLine[ViewConstant.WSS1_LINE2] = 10;
							} else if (BandRoute[1] == 2 && BandRoute[4] == 2) {
								LineUsing.usingLine[ViewConstant.WSS1_LINE2] = 11;
							}
						} else if (BandRoute[0] == 2) {// 第2WSS
							LineUsing.usingLine[ViewConstant.WSS2]=1;
							if (BandRoute[1] == 1 && BandRoute[4] == 1) { // 量子光经典光都从1出去
								LineUsing.usingLine[ViewConstant.WSS2_LINE1] = 11;
							} else if (BandRoute[1] == 2 && BandRoute[4] == 1) { // 量子光从2出去,经典从1出
								LineUsing.usingLine[ViewConstant.WSS2_LINE2] = 11;
								LineUsing.usingLine[ViewConstant.WSS2_LINE1] = 10;

							} else if (BandRoute[1] == 1 && BandRoute[4] == 2) { // 量子光从2出去,经典从1出
								LineUsing.usingLine[ViewConstant.WSS2_LINE1] = 11;
								LineUsing.usingLine[ViewConstant.WSS2_LINE2] = 10;
							} else if (BandRoute[1] == 2 && BandRoute[4] == 2) {
								LineUsing.usingLine[ViewConstant.WSS2_LINE2] = 11;
							}
						} else if(BandRoute[0]==-1) {
							if (BandRoute[1] == 1 && BandRoute[4] == 1) {
								LineUsing.usingLine[ViewConstant.WSS1_LINE1] = 0;
							} else if (BandRoute[1] == 2 && BandRoute[4] == 1) { // 量子光从2出去,经典从1出
								LineUsing.usingLine[ViewConstant.WSS1_LINE1] = 0;
								LineUsing.usingLine[ViewConstant.WSS1_LINE2] = 0;
							} else if (BandRoute[0] == 1 && BandRoute[4] == 2) {
								LineUsing.usingLine[ViewConstant.WSS1_LINE1] = 0;
								LineUsing.usingLine[ViewConstant.WSS1_LINE2] = 0;
							} else if (BandRoute[1] == 2 && BandRoute[4] == 2) {
								LineUsing.usingLine[ViewConstant.WSS1_LINE2] = 0;
							}
						}
						else if (BandRoute[0] == -2) {// 第2WSS
								if (BandRoute[1] == 1 && BandRoute[4] == 1) { // 量子光经典光都从1出去
									LineUsing.usingLine[ViewConstant.WSS2_LINE1] = 0;
								} else if (BandRoute[1] == 2 && BandRoute[4] == 1) { // 量子光从2出去,经典从1出
									LineUsing.usingLine[ViewConstant.WSS2_LINE2] = 0;
									LineUsing.usingLine[ViewConstant.WSS2_LINE1] = 0;

								} else if (BandRoute[1] == 1 && BandRoute[4] == 2) { // 量子光从2出去,经典从1出
									LineUsing.usingLine[ViewConstant.WSS2_LINE1] = 0;
									LineUsing.usingLine[ViewConstant.WSS2_LINE2] = 0;
								} else if (BandRoute[1] == 2 && BandRoute[4] == 2) {
									LineUsing.usingLine[ViewConstant.WSS2_LINE2] = 0;
								}


						}
					}
					Log.i(TAG, "WSS返回信息" + response);
					try {
						HttpServer.queue.put(response);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(TAG, comObject.getClassicOptical());
				}
			};
			Looper.loop();
		}
	}

	public  int ConflishCheck(CommunicateObject com){
		int ch1=getChannel(Double.valueOf(com.ClassicOptical));
		int ch2=getChannel(Double.valueOf(com.getQuantumOptical()));
		int ch3=getChannel(Double.valueOf(com.SynOptical));
		if(com.FromFlag==1) {
			if ((ch1 != -1 && WSS1channel.contains(ch1)) || (ch2 != -1 && WSS1channel.contains(ch2)) || (ch2 != -1 && WSS1channel.contains(ch3))) {  //冲突
				Message msg = new Message();
				msg.obj = "信道冲突";
				MainActivity.handlerTV.sendMessage(msg);
				return 0;
			}
		}else {
			if ((ch1 != -1 && WSS2channel.contains(ch1)) || (ch2 != -1 && WSS2channel.contains(ch2)) || (ch2 != -1 && WSS2channel.contains(ch3))) {  //冲突
				Message msg = new Message();
				msg.obj = "信道冲突";
				MainActivity.handlerTV.sendMessage(msg);
				return 0;
			}
		}
		return  1;
	}
	/**
	 * 1.判断是否为断开信令
	 */
	public int LinkEstablish(CommunicateObject com) {
		boolean isOK = false;
		if (com.getIsConnect() != SignalConstant.LinkConnect) {// 断开信令
			String flag = com.getSourceIP() + com.getFromFlag();
			if (signalMap.containsKey(flag)) { // 从已用消息集合中移除
				signalMap.remove(flag);
			}
			if (com.FromFlag == 1) {
				isOK = setupWSS(com, SWSS1, Com.WSS1, 1);
				if (isOK) {
					BandRoute[0] = -1;
				}
			} else if (com.FromFlag == 2) {
				isOK = setupWSS(com, SWSS2, Com.WSS2, 1);
				if (isOK) {
					BandRoute[0] = -2;
				}
			}
			Log.i(TAG, "接收到断开信令，返回结果"+isOK);
			return 1; // 返回给server，表示已处理完毕
		} else {
			ArrayList<String> OutputIP=new ArrayList<String>();
			OutputIP.add(ServerConstant.OutPortIP1);
			OutputIP.add(ServerConstant.OutPortIP2);
			if(!OutputIP.contains(com.getClassicDesIP())||!OutputIP.contains(com.getQuantumDesIP())){
				Log.i(TAG,"目的IP不在交换节点内");
				Message msg=new Message();
				msg.obj="目的IP中有不在本交换机范围的";
				MainActivity.handlerTV.sendMessage(msg);
				return  0;
			}
			if (com.FromFlag == 1) {// 输入1端口来的信息，即需要WSS1进行链路分配
				isOK = setupWSS(com, SWSS1, Com.WSS1, 0);
				if (isOK) {
					BandRoute[0] = 1;
					BandRoute[3] = 1;
				}
			} else if (com.FromFlag == 2) {
				isOK = setupWSS(com, SWSS2, Com.WSS2, 0);
				if (isOK) {
					BandRoute[0] = 2;
					BandRoute[3] = 2;
				}
			}
			Log.i(TAG, "接收到建立链路信令，返回结果"+isOK);
		}
		if (isOK) { // 链路分配正确
			return 1;
		} else {
			return 0;
		}
	}

	/*
	 * WSS配置
	 */
	public boolean setupWSS(CommunicateObject msg, serial serial, int flag,
			int reset) {
		String[] cmd = CommandBuild(msg);
		if (reset == 0)
			Log.i(TAG, "写入的配置指令：" + cmd[reset]);
		else {
			Log.i(TAG, "写入的配置指令：" + cmd[reset]);
		}
		String response = WSSSetup.getInstance().sendAndGetResponse(serial,
				flag, cmd[reset]);
		if (response == null) {
			try {
				Thread.sleep(2000);
				response = WSSSetup.getInstance().sendAndGetResponse(serial,
						flag, cmd[reset]);
				if (response == null)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}//
		}
		if (response.contains("OK")) {
			String pattern = "[0-9]*";

			// 创建 Pattern 对象
			Pattern r = Pattern.compile(pattern);
			if(msg.getFromFlag()==1){
				String [] str1=cmd[reset].split(" ");
				String [] str= str1[1].split(";");
				for(String s:str)
					WSS1channel.add(Integer.valueOf( s.split(",")[0]));
			}else {
				String [] str1=cmd[reset].split(" ");
				String [] str= str1[1].split(";");
				for(String s:str)
					WSS2channel.add(Integer.valueOf(s.split(",")[0]));
			}

			response = WSSSetup.getInstance().sendAndGetResponse(serial, flag,
					WSSCmd.COMMEND_RSW);
			if (response.contains("OK"))// 返回OK，表示正常
				return true;
		}

		return false;
	}

	/*
	 * 根据通道情况组合WSS配置指令
	 */
	public String[] CommandBuild(CommunicateObject comObj) {
		Map<String, List<Integer>> map = channelPort(comObj);

		StringBuilder sBuilder = new StringBuilder();
		StringBuilder sbRes = new StringBuilder();
		sbRes.append(WSSCmd.COMMEND_URA);
		sBuilder.append(WSSCmd.COMMEND_URA);
		List<Integer> list1 = map.get("OutPort1");
		for (int tmp : list1) {
			if(tmp==-1){
				continue;
			}
//			sBuilder.append(String.valueOf(tmp-1) + ",1,0.0;");// 设置1端口的配置指令
			sBuilder.append(String.valueOf(tmp) + ",1,0.0;");// 设置1端口的配置指令
//			sBuilder.append(String.valueOf(tmp+1) + ",1,0.0;");// 设置1端口的配置指令
			sbRes.append(String.valueOf(tmp) + ",99,99.9;");// 设置1端口的配置指令
			BandRoute[1] = 1;// 通过WSS1 去往端口1，传输的是三个光
			BandRoute[2] = 2;
			BandRoute[4] = 1;
			BandRoute[5] = 2;
			Tmpchannel.add(tmp);
//			if(comObj.FromFlag==1) {
//				WSS1channel.add(tmp);
//			}else{
//				WSS2channel.add(tmp);
//			}
		}

		List<Integer> list2 = map.get("OutPort2");
		for (int tmp : list2) {
			if(tmp==-1){
				continue;
			}
			sBuilder.append(String.valueOf(tmp-1) + ",2,0.0;");// 设置1端口的配置指令
			sBuilder.append(String.valueOf(tmp) + ",2,0.0;");// 设置2端口的配置指令
			sBuilder.append(String.valueOf(tmp+1) + ",2,0.0;");// 设置1端口的配置指令
			sbRes.append(String.valueOf(tmp) + ",99,99.9;");// 设置1端口的复位指令
			BandRoute[1] = 2;
			BandRoute[2] = 2;
			BandRoute[4] = 2;
			BandRoute[5] = 2;
			Tmpchannel.add(tmp);
//			if(comObj.FromFlag==1) {
//				WSS1channel.add(tmp);
//			}else{
//				WSS2channel.add(tmp);
//			}
		}
		List<Integer> list3 = map.get("OutPort12");
		for (int i = 0; i < list3.size(); i++) {
			if(list3.get(i)==-1){
				continue;
			}
			if (i != 2) {// 0，1 代表是量子和同步的通道，这两个去的端口是一样的
//				sBuilder.append(String.valueOf(list3.get(i)-1) + ",2,0.0;");// 设置1端口的配置指令
				sBuilder.append(String.valueOf(list3.get(i)) + ",2,0.0;");// 量子和同步去端口2
				sBuilder.append(String.valueOf(list3.get(i)+1) + ",2,0.0;");// 设置1端口的配置指令
				sbRes.append(String.valueOf(list3.get(i)) + ",99,99.9;");// 设置1端口的复位指令
				BandRoute[1] = 2;
				BandRoute[2] = 2;
				Tmpchannel.add(list3.get(i));
//				if(comObj.FromFlag==1) {
//					WSS1channel.add(list3.get(i));
//				}else{
//					WSS2channel.add(list3.get(i));
//				}

			} else {
				sBuilder.append(String.valueOf(list3.get(i)) + ",1,0.0;");
				for(int j=1;j<2;j++) {
					sBuilder.append(String.valueOf(list3.get(i)+j) + ",1,0.0;");
					sBuilder.append(String.valueOf(list3.get(i)-j) + ",1,0.0;");
				}
				sbRes.append(String.valueOf(list3.get(i)) + ",99,99.9;");// 设置1端口的复位指令
				BandRoute[4] = 1;
				BandRoute[5] = 1;
				Tmpchannel.add(list3.get(i));
//				if(comObj.FromFlag==1) {
//					WSS1channel.add(list3.get(i));
//				}else{
//					WSS2channel.add(list3.get(i));
//				}
			}
		}

		String cmd = sBuilder.substring(0, sBuilder.length() - 1);
		cmd += "\n";
		String cmdRes = sbRes.substring(0, sbRes.length() - 1);
		cmdRes += "\n";
		String[] res = new String[2];
		res[0] = cmd;
		res[1] = cmdRes;
		return res;
	}

	/*
	 * 根据信令中IP设定相应的通道。
	 */
	public Map<String, List<Integer>> channelPort(CommunicateObject comObject) {
		// List<UserMsg> userMsgs=
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		List<Integer> list_ip1 = new ArrayList<Integer>();
		List<Integer> list_ip2 = new ArrayList<Integer>();
		List<Integer> list_ip_diff = new ArrayList<Integer>();

		if (comObject.getClassicDesIP() != null
				&& comObject.getQuantumDesIP().equals(
						comObject.getClassicDesIP())) { // 量子光与经典光Ip相同
			Log.i(TAG, "量子光与经典光路径相同");
			if (ServerConstant.OutPortIP1.equals(comObject.getClassicDesIP())) { // 从端口1出去
				list_ip1.add(getChannel(Double.valueOf(comObject
						.getQuantumOptical())));
				list_ip1.add(getChannel(Double.valueOf(comObject
						.getSynOptical())));
				list_ip1.add(getChannel(Double.valueOf(comObject
						.getClassicOptical())));
			} else if (ServerConstant.OutPortIP2.equals(comObject
					.getClassicDesIP())) { // 端口2出去
				list_ip2.add(getChannel(Double.valueOf(comObject
						.getQuantumOptical())));
				list_ip2.add(getChannel(Double.valueOf(comObject
						.getSynOptical())));
				list_ip2.add(getChannel(Double.valueOf(comObject
						.getClassicOptical())));
			}
		} else {
			list_ip_diff.add(getChannel(Double.valueOf(comObject
					.getQuantumOptical())));
			list_ip_diff.add(getChannel(Double.valueOf(comObject
					.getSynOptical())));
			list_ip_diff.add(getChannel(Double.valueOf(comObject
					.getClassicOptical())));
		}
		map.put("OutPort1", list_ip1);
		map.put("OutPort2", list_ip2);
		map.put("OutPort12", list_ip_diff);
		return map;
	}

	// public void getusers()//json 里一个用户
	/**
	 * 功能描述:根据给定波长或波长范围判定是哪个通道的数据 返回值：通道的值 进行就近取整
	 **/
	public int getChannel(double wave) {
		if(Math.abs(wave-0)<0.1){
			return  -1;
		}
		Double temp = 1 + (WSSCmd.SPEED_CONSTANT / wave - 191.35) * 20;
		return (int) (temp + 0.5);
	}

	public boolean checkWSS() {
		System.out.println("WSS1 hashcode:"+SWSS1.hashCode());
		if (!WSSSetup.getInstance().checkWSS(SWSS1, Com.WSS1)) {
			System.out.println("WSS1 " + "init error");
			return false;
		}
		if (!WSSSetup.getInstance().checkWSS(SWSS2, Com.WSS2)) {
			System.out.println("WSS2 "  + "init error");
			return false;
		}
		System.out.println("WSS2 hashcide:"+SWSS2.hashCode());
		return true;
	}

}
