package com.example.post.Vquery;

import static com.example.post.Vquery.UserManagerActivity.i;
import static com.example.post.Vquery.UserManagerActivity.j;
import static com.example.post.Vquery.UserManagerActivity.weldingListinfoList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.post.Util.CommonUtils;
import com.example.post.Util.WeldingDataAdapter;
import com.example.post.R;
import com.example.post.Util.WeldingDatainfo;

import java.util.List;

/**
 * 用户管理界面业务逻辑
 */
public class UserManagerActivity2 extends AppCompatActivity implements View.OnClickListener {

    private WeldingDataAdapter weldingDataAdapter;   // 用户信息数据适配器
    private ListView lv_user;   // 用户列表组件
    private Handler mainHandler;   // 主线程
    private List<WeldingDatainfo> weldingDatainfoList2;   // 用户数据集合
    private TextView tv_wireDiameter2;
    private String wireDiameter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welding_list);
        initView();
        loadUserDb();
    }

    private void initView(){
        // 返回图片按钮 ，添加图片按钮
        ImageView btn_back = findViewById(R.id.btn_back);
        ImageView btn_next = findViewById(R.id.btn_next);
        lv_user = findViewById(R.id.lv_data);
        mainHandler = new Handler(getMainLooper());
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_wireDiameter2 = findViewById(R.id.tv_wireDiameter);
    }

    private void loadUserDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                weldingDatainfoList2 = null;

                for (j=i; weldingDatainfoList2 == null && j< weldingListinfoList.size(); j++) {
                    wireDiameter2 = weldingListinfoList.get(j).getWireDiameter();
                    weldingDatainfoList2 = weldingListinfoList.get(j).getWeldingList();
                }

                showLvData();
            }
        }).start();
    }

    // 显示列表数据的方法
    private void showLvData() {
        if (weldingDataAdapter == null) {   // 首次加载时的操作
            weldingDataAdapter = new WeldingDataAdapter(this, weldingDatainfoList2);
            lv_user.setAdapter(weldingDataAdapter);
        } else {   // 更新数据时的操作*/
            weldingDataAdapter.setweldingList(weldingDatainfoList2);
            weldingDataAdapter.notifyDataSetChanged();
        }
        tv_wireDiameter2.setText(wireDiameter2);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back)
            finish();
        else if (v.getId() == R.id.btn_next) {
            if(j == weldingListinfoList.size())   //判断是否是最后一页
                CommonUtils.showDlgMsg(UserManagerActivity2.this, "最后一页");
            else next();
        }
    }

    private void next() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(UserManagerActivity2.this, UserManagerActivity3.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
}