package com.example.post.MulVquery;

import static com.example.post.queryUtil.getResponse;
import static com.example.post.queryUtil.getid;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.post.R;
import com.example.post.Userinfo;
import com.example.post.Weldinginfo;
import org.json.JSONObject;
import java.util.List;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MulVquery extends AppCompatActivity implements View.OnClickListener {
    private EditText et_mulvquery;
    private Button btn_vquery;
    private ImageButton btn_vback;
    private Handler mainHandler;   // 主线程
    private String mresponse = "";
    public static List<Weldinginfo> weldinginfoList;
    public static String murl, Murl, Mvquery, Mresponse, id;
    public static List<Userinfo> userinfoList;
    public static int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vquery);
        initView();
    }

    private void initView() {
        et_mulvquery = findViewById(R.id.et_vquery);
        btn_vquery = findViewById(R.id.btn_vquery);
        btn_vquery.setOnClickListener(this);
        btn_vback = findViewById(R.id.btn_vback);
        btn_vback.setOnClickListener(this);
        mainHandler = new Handler(getMainLooper()); // 获取主线程
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_vquery && mresponse =="") {
                new Thread(() -> {
                    try {
                        murl = "http://202.38.247.12:8001/api/session/";
                        id = getid();
                        Mvquery = et_mulvquery.getText().toString().trim();
                        Murl = murl + id + "?query=" + Mvquery;
                        FormBody.Builder params = new FormBody.Builder();
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(Murl)
                                .post(params.build())
                                .build();
                        Response response = client.newCall(request).execute();
                        Mresponse = response.body().string();
                            JSONObject jsonObject = new JSONObject(Mresponse);
                            mresponse = jsonObject.optString("response");
                            dofirst();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
        } else if (v.getId() == R.id.btn_vquery && mresponse !="") {
            new Thread(() -> {
                try {
                    Mvquery = et_mulvquery.getText().toString().trim();
                    Murl = murl + id + "?query=" + Mvquery;
                    FormBody.Builder params = new FormBody.Builder();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Murl)
                            .post(params.build())
                            .build();
                    Response response = client.newCall(request).execute();
                    Mresponse = response.body().string();
                    mresponse = getResponse(Mresponse);
                    doLogin();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } else if(v.getId() == R.id.btn_vback)
            finish();
    }

    private void dofirst() {
        new Thread(() -> mainHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MulVquery.this);
                builder.setTitle("提示");
                builder.setMessage(mresponse);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", null);
                AlertDialog alert = builder.create(); // 根据建造器构建提醒对话框对象
                alert.show();
            }
        })).start();
    }

    private void doLogin() {
        new Thread(() -> mainHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MulVquery.this);
                builder.setTitle("提示");
                builder.setMessage(mresponse);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MulVquery.this, MulUserManagerActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alert = builder.create(); // 根据建造器构建提醒对话框对象
                alert.show();
            }
        })).start();
    }
}