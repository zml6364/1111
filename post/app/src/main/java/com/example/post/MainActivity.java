package com.example.post;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.post.queryUtil.getResponse;
import static com.example.post.queryUtil.readJsonFile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_test;
    private Button btn_post;
    private Handler mainHandler;   // 主线程
    private String response;
    public static String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initView();
        //doLogin();
    }

    private void initView() {
        et_test = findViewById(R.id.et_test);
        btn_post = findViewById(R.id.btn_post);
        btn_post.setOnClickListener(this);
        mainHandler = new Handler(getMainLooper()); // 获取主线程
    }

    @Override
    public void onClick(View v) {

            new Thread(() -> {
                //new Thread(() -> mainHandler.post(() -> {
                try {
                    queryUtil.queryRequest("焊接材料是纯铝，焊接厚度是1，焊接方法是MIG，焊接参数",
                            "http://202.38.247.12:8001/api/chat");

                    /*
                    真机无法存储文件，访问失败
                     */
                    str = readJsonFile("/storage/emulated/0/Download/response.json");

                    response = getResponse(str);

                    doLogin();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

        }


    private void doLogin() {

        new Thread(() -> mainHandler.post(new Runnable() {
            @Override
            public void run() {
        //调用参数界面
        //Intent intent = new Intent(MainActivity.this, UserManagerActivity.class);
        //startActivity(intent);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage(response);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, UserManagerActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alert = builder.create(); // 根据建造器构建提醒对话框对象
                alert.show();

            }
        })).start();

    }
}