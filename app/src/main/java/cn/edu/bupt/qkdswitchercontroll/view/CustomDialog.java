package cn.edu.bupt.qkdswitchercontroll.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;

import cn.edu.bupt.qkdswitchercontroll.R;

/**
 * Created by YH on 2016/12/26.
 */

public class CustomDialog extends ProgressDialog {

    public CustomDialog(Context context){
        super(context);
    }
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    private  void init(Context context){
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

    }
    public  void showMsg(String str){
        LayoutInflater inflater= LayoutInflater.from(getContext());
        TextView tv= (TextView) inflater.inflate(R.layout.loading_dialog,null);
        tv.setText(str);
    }
    @Override
    public void show() {
        super.show();
    }
}
