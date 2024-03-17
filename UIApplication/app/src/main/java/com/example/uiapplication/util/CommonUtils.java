package com.example.uiapplication.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * 自定义通用工具类
 */
public class CommonUtils {

    /**
     * 显示短消息
     *
     * @param context 上下文
     * @param msg     要显示的消息
     */
    public static void showShortMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长消息
     *
     * @param context 上下文
     * @param msg     要显示的消息
     */
    public static void showLonMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示消息对话框
     *
     * @param context 上下文
     * @param msg     要显示的消息
     */
    public static void showDlgMsg(Context context, String msg) {

        new AlertDialog.Builder(context)
                .setTitle("提示信息")
                .setMessage(msg)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create().show();
    }



}