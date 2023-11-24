package com.example.post.Vquery;

import static com.example.post.Vquery.Vquery.str;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.post.CommonUtils;
import com.example.post.LvDataAdapter;
import com.example.post.R;
import com.example.post.Userinfo;
import com.example.post.Weldinginfo;
import com.example.post.queryUtil;

import java.util.List;

/**
 * 用户管理界面业务逻辑
 */
public class UserManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private LvDataAdapter lvDataAdapter;   // 用户信息数据适配器
    private ListView lv_user;   // 用户列表组件
    private Handler mainHandler;   // 主线程
    private List<Userinfo> userinfoList;   // 用户数据集合
    private TextView tv_wireDiameter;
    private String wireDiameter;
    public static List<Weldinginfo> weldinginfoList;
    public static int i, j, k, l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);
        initView();
        loadUserDb();
    }

    private void initView(){
        ImageView btn_back = findViewById(R.id.btn_back);
        ImageView btn_next = findViewById(R.id.btn_next);
        lv_user = findViewById(R.id.lv_user);
        mainHandler = new Handler(getMainLooper());
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_wireDiameter = findViewById(R.id.tv_wireDiameter);
    }

    private void loadUserDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                userinfoList = null;
                weldinginfoList = queryUtil.parseJson(str);
                for (i=0; userinfoList == null && i<weldinginfoList.size(); i++) {
                    wireDiameter = weldinginfoList.get(i).getWireDiameter();
                    userinfoList = weldinginfoList.get(i).getWeldingList();
                }
                showLvData();
            }
        }).start();
    }

    // 显示列表数据的方法
    private void showLvData() {
        if (lvDataAdapter == null) {   // 首次加载时的操作
            lvDataAdapter = new LvDataAdapter(this, userinfoList);
            lv_user.setAdapter(lvDataAdapter);
        } else {   // 更新数据时的操作
            lvDataAdapter.setweldingList(userinfoList);
            lvDataAdapter.notifyDataSetChanged();
        }
        tv_wireDiameter.setText(wireDiameter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back)
            finish();
        else if (v.getId() == R.id.btn_next) {
            if(i == weldinginfoList.size())   //判断是否是最后一页
            {
                CommonUtils.showDlgMsg(UserManagerActivity.this, "最后一页");
            } else { next();}
        }
    }

    private void next() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(UserManagerActivity.this, UserManagerActivity2.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
}