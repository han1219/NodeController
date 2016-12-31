package cn.edu.bupt.qkdswitchercontroll.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.topeet.serialtest.Com;

import java.io.IOException;
import java.util.ArrayList;

import cn.edu.bupt.qkdswitchercontroll.R;
import cn.edu.bupt.qkdswitchercontroll.adapter.TableAdapter;
import cn.edu.bupt.qkdswitchercontroll.adapter.TableCell;
import cn.edu.bupt.qkdswitchercontroll.adapter.TableRow;
import cn.edu.bupt.qkdswitchercontroll.constant.ServerConstant;
import cn.edu.bupt.qkdswitchercontroll.constant.ViewConstant;
import cn.edu.bupt.qkdswitchercontroll.device.USB2SerialProxy;
import cn.edu.bupt.qkdswitchercontroll.device.WSSSetup;
import cn.edu.bupt.qkdswitchercontroll.httpServer.HttpServer;
import cn.edu.bupt.qkdswitchercontroll.socketServer.WSSControlThread;
import cn.edu.bupt.qkdswitchercontroll.view.CustomDialog;
import cn.edu.bupt.qkdswitchercontroll.view.LineUsing;
import cn.edu.bupt.qkdswitchercontroll.wssconstant.WSSCmd;

public class MainActivity extends Activity {

    private static final String  TAG ="MainActivity" ;
    SurfaceView sView;
    SurfaceHolder sfHolder;

    EditText uartRcvView;
    Button uart_send_btn;

    public  static  USB2SerialProxy uartSeriesProxy;
    public  static Handler handler;
    WSSControlThread controlThread = new WSSControlThread();
    Button WSS_RES;
    HttpServer server = new HttpServer(ServerConstant.SERVER_PORT);
    private Paint paint;
    final int HEIGHT = 320;
    final int WIDTH = 320;
    final int X_OFFSET = 5;
    public static ListView lv;
    int centerY = HEIGHT / 2;
    public static TableAdapter tableAdapter;
    public static ArrayList<TableRow> table = new ArrayList<TableRow>();
    Thread thread=new Thread(controlThread);

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


        // 启动WSS配置线程，并等待server接收到消息
        if(!thread.isAlive())
            thread.start();
        Log.i("Main", "socket start");

        try { // 开启Server服务器
            if(!server.isAlive())
                server.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

   public static Handler handlerTV;
    EditText tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sView = (SurfaceView) findViewById(R.id.surface);
        sView.setZOrderOnTop(true);//设置画布  背景透明


        sfHolder = sView.getHolder();
        sfHolder.setFormat(PixelFormat.TRANSLUCENT);
        Mydialog =new CustomDialog(this);

        lv = (ListView) findViewById(R.id.info_list_view);
        tv=(EditText) findViewById(R.id.tv_info);
        tv.setInputType(InputType.TYPE_NULL);//不弹出键盘
        tv.setSingleLine(false);
//水平滚动设置为False
        tv.setHorizontallyScrolling(false);

        thread.setName("ControlThread");
        WSS_RES = (Button) findViewById(R.id.btnRes);
        tableAdapter = new TableAdapter(this);

        TableCell[] titles = new TableCell[5];// 每行5个单元
        titles[0] = new TableCell("入口",
                TableCell.STRING);
        titles[1] = new TableCell("出口",
                TableCell.STRING);
        titles[2] = new TableCell("目的IP",
                TableCell.STRING);
        titles[3] = new TableCell("波长",
                TableCell.STRING);
        titles[4] = new TableCell("通道",
                TableCell.STRING);

        table.add(new TableRow(titles));
        tableAdapter.setData(table);
        lv.setAdapter(tableAdapter);
        handlerTV=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                tv.append("\n"+msg.obj);
            }
        };
        //
        // DisplayMetrics metric = new DisplayMetrics();
        // getWindowManager().getDefaultDisplay().getMetrics(metric);
        // int width = metric.widthPixels; // ��Ļ��ȣ����أ�
        // int height = metric.heightPixels; // ��Ļ�߶ȣ����أ�

        // sfHolder.addCallback(new MySurfaceView(sfHolder, width,
        // height,this));
        // ViewConstant.setScreenHeight(height);
        // ViewConstant.setScreenWidth(width);
        // setContentView(new MySurfaceView(this));


        uartSeriesProxy=new USB2SerialProxy(getApplicationContext());
        WSS_RES.setOnClickListener(new BtnOnClick());
        lv = (ListView) findViewById(R.id.info_list_view);
handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        tableAdapter.notifyDataSetChanged();
    }
};
    }

    public  static  USB2SerialProxy getContext(){
        return  uartSeriesProxy;
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (server != null && server.isAlive()) {
            server.stop();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        System.exit(0);
    }

    public interface  updateTV{
        public void update(String[] str);
    }
    CustomDialog Mydialog;
    Handler dialogHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1==1) {
//                Mydialog.showMsg("Please wait while WSS reseting");
                Mydialog.show();
            }else {
                Mydialog.dismiss();
            }
        }
    };
    class BtnOnClick implements View.OnClickListener {

        private static final String TAG = "BTNONCLICK";

        updateTV uptv=new updateTV() {
            @Override
            public void update(String[] res1) {
                Message msg=new Message();
                if (res1[0] != null && res1[1] != null)
                    if (res1[0].contains("OK") && res1[1].contains("OK")) {
                        Log.i(TAG, "复位成功");

                        msg.obj="复位成功";
                        handlerTV.sendMessage(msg);
                        LineUsing.usingLine[ViewConstant.WSS2_LINE2] = 0;
                        LineUsing.usingLine[ViewConstant.WSS2_LINE1] = 0;
                        LineUsing.usingLine[ViewConstant.WSS1_LINE2] = 0;
                        LineUsing.usingLine[ViewConstant.WSS1_LINE1] = 0;
                        WSSControlThread.WSS2channel.clear();
                        WSSControlThread.WSS1channel.clear();
                        WSSControlThread.Tmpchannel.clear();
                        for(int i=1;i<table.size();i++)
                            table.remove(i);
                    }else {
                        msg=new Message();
                        msg.obj="复位失败";
                        handlerTV.sendMessage(msg);
                    }
                msg=new Message();
                    msg.arg1=0;//结束Dialog
                    dialogHandler.sendMessage(msg);
            }
        };

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.btnRes) {
                Log.i(TAG, "WSS 复位");
                final String[] res1 = {"",""};
                class  Res implements  Runnable{
                    @Override
                    public void run() {
                        Message msg=new Message();
                        msg.arg1=1;
                       dialogHandler.sendMessage(msg);
                       res1[0] = WSSSetup.getInstance().sendAndGetResponse(
                                controlThread.SWSS1, Com.WSS1, WSSCmd.COMMEND_RES);
                        res1[1] = WSSSetup.getInstance().sendAndGetResponse(
                                controlThread.SWSS2, Com.WSS2, WSSCmd.COMMEND_RES);

                        uptv.update(res1);
                    }
                }
                new Thread(new Res()).start();

            }
        }

    }
}
