package com.ctvit.shitingxizang.utils;

import android.content.Context;


public class DialogUtils {




    /**
     * 大众对话框
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showMainDialog(Context context, String title, String content, MainDialog.SureListener listener) {
        MainDialog mainDialog = new MainDialog(context);
        mainDialog.showMsgDialog(title, content, listener);

    }


    /**
     * 大众对话框
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showMainDialog(Context context, String title, String content, String leftBtnText, String rightBtnText, MainDialog.SureListener listener) {
        MainDialog mainDialog = new MainDialog(context);
        mainDialog.showMsgDialog(title, content, leftBtnText, rightBtnText, listener);

    }

    /**
     * 大众对话框
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showMainDialog3(Context context, String title, String content, String leftBtnText, String rightBtnText, MainDialog.SureListener listener,MainDialog.SureCancelListener listeners) {
        MainDialog mainDialog = new MainDialog(context);
        mainDialog.showMsgDialog4(title, content, leftBtnText, rightBtnText, listener,listeners);

    }
    /**
     * 大众对话框
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showMainDialog2(Context context, String title, String content, String leftBtnText, String rightBtnText, MainDialog.SureListener listener) {
        MainDialog mainDialog = new MainDialog(context);
        mainDialog.showMsgDialog2(title, content, leftBtnText, rightBtnText, listener);

    }
    /**
     * 大众对话框
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showMainDialog3(Context context, String title, String content, String btnText, MainDialog.SureListener listener) {
        MainDialog mainDialog = new MainDialog(context);
        mainDialog.showMsgDialog3(title, content,btnText , listener);

    }
}
