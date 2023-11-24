package com.example.post.Vquery;

import static com.example.post.Util.queryUtil.getResponse;
import static com.example.post.Util.queryUtil.readJsonFile;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.post.R;
import com.example.post.Util.queryUtil;

public class Vquery extends AppCompatActivity implements View.OnClickListener {
    private EditText et_vquery;
    private Button btn_vquery;
    private ImageButton btn_vback;
    private Handler mainHandler;   // 主线程
    private String response;
    public static String str, vquery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vquery);
        initView();
    }

    private void initView() {
        et_vquery = findViewById(R.id.et_vquery);
        vquery = et_vquery.getText().toString().trim();
        btn_vquery = findViewById(R.id.btn_vquery);
        btn_vquery.setOnClickListener(this);
        btn_vback = findViewById(R.id.btn_vback);
        btn_vback.setOnClickListener(this);
        mainHandler = new Handler(getMainLooper()); // 获取主线程
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_vquery) {
            new Thread(() -> {
                //new Thread(() -> mainHandler.post(() -> {
                try {
                    queryUtil.queryRequest(vquery,
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
        } else if (v.getId() == R.id.btn_vback)
            finish();
        }

    private void doLogin() {
        new Thread(() -> mainHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Vquery.this);
                builder.setTitle("提示");
                builder.setMessage(response);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Vquery.this, UserManagerActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alert = builder.create(); // 根据建造器构建提醒对话框对象
                alert.show();
            }
        })).start();
    }
}