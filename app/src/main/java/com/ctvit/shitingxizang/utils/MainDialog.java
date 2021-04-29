package com.ctvit.shitingxizang.utils;


import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ctvit.shitingxizang.R;


/**
 * 主要对话框
 *
 * @author PC
 */
public class MainDialog {
    private Context context;
    private TextView tv_title, tv_content, tv_no, tv_yes,yes;
    private Dialog dlg;
    private View v;
    public MainDialog(Context context) {
        this.context = context;
    }

    private void findView(Window view) {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_no = (TextView) view.findViewById(R.id.tv_no);
        tv_yes = (TextView) view.findViewById(R.id.tv_yes);
        yes = view.findViewById(R.id.yes);
        v = view.findViewById(R.id.v);
    }

    /**
     * 主要调用函数
     */
    public void showMsgDialog(String title, String content, final SureListener listener) {
        dlg = new Dialog(context, R.style.myNewsDialogStyle);
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        params.width = display.getWidth();
        // params.height = display.getHeight() * 2;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(params);
        window.setContentView(R.layout.view_dialog);

        // 一部分控件在这里初始化
        findView(window);

        if (!TextUtils.isEmpty(title))
            tv_title.setText(title);

        if (!TextUtils.isEmpty(content))
            tv_content.setText(content);

        tv_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSure();
                dlg.dismiss();
            }
        });

    }

    /**
     * 主要调用函数 可以设置按钮的字
     */
    public void showMsgDialog(String title, String content, String leftBtnText, String rightBtnText, final SureListener listener) {
        dlg = new Dialog(context, R.style.myNewsDialogStyle);
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        params.width = display.getWidth();
        // params.height = display.getHeight() * 2;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(params);
        window.setContentView(R.layout.view_dialog);

        // 一部分控件在这里初始化
        findView(window);

        if (!TextUtils.isEmpty(title)){
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }


        if (!TextUtils.isEmpty(content))
            tv_content.setText(content);

        tv_yes.setText(rightBtnText);
        tv_no.setText(leftBtnText);
        tv_yes.setTextSize(12);
        tv_no.setTextSize(12);

        tv_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dlg.dismiss();
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSure();
                dlg.dismiss();
            }
        });

    }

    /**
     * 主要调用函数 可以设置按钮的字
     */
    public void showMsgDialog4(String title, String content, String leftBtnText, String rightBtnText, final SureListener listener,final SureCancelListener listeners) {
        dlg = new Dialog(context, R.style.myNewsDialogStyle);
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        params.width = display.getWidth();
        // params.height = display.getHeight() * 2;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(params);
        window.setContentView(R.layout.view_dialog);

        // 一部分控件在这里初始化
        findView(window);

        if (!TextUtils.isEmpty(title)){
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }


        if (!TextUtils.isEmpty(content))
            tv_content.setText(content);

        tv_yes.setText(rightBtnText);
        tv_no.setText(leftBtnText);
        tv_yes.setTextSize(12);
        tv_no.setTextSize(12);

        tv_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listeners.onCancel();
                dlg.dismiss();
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSure();
                dlg.dismiss();
            }
        });

    }
    /**
     * 主要调用函数 可以设置按钮的字
     */
    public void showMsgDialog2(String title, String content, String leftBtnText, String rightBtnText, final SureListener listener) {
        dlg = new Dialog(context, R.style.myNewsDialogStyle);
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        params.width = display.getWidth();
        // params.height = display.getHeight() * 2;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(params);
        window.setContentView(R.layout.view_dialog);

        // 一部分控件在这里初始化
        findView(window);

        if (!TextUtils.isEmpty(title))
            tv_title.setText(title);

        if (!TextUtils.isEmpty(content))
            tv_content.setText(content);

        tv_yes.setText(rightBtnText);
        tv_no.setText(leftBtnText);
        tv_yes.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        tv_no.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);

        tv_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
                listener.onSure();
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

    }
    /**
     * 一个按钮
     */
    public void showMsgDialog3(String title, String content, String btnText,  final SureListener listener) {
        dlg = new Dialog(context, R.style.myNewsDialogStyle);
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        params.width = display.getWidth();
        // params.height = display.getHeight() * 2;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(params);
        window.setContentView(R.layout.view_dialog);

        // 一部分控件在这里初始化
        findView(window);
        v.setVisibility(View.GONE);
        tv_yes.setVisibility(View.GONE);
        tv_no.setVisibility(View.GONE);
        yes.setVisibility(View.VISIBLE);
        yes.setText(btnText);
        if (!TextUtils.isEmpty(title)){
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);

        }

        if (!TextUtils.isEmpty(content))
            tv_content.setText(content);






        yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
                listener.onSure();
            }
        });


    }

    public interface SureListener {
        void onSure();

    }
    public interface SureCancelListener {

        void onCancel();
    }
}
