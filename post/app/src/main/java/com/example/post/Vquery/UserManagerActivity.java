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

import com.example.post.Util.CommonUtils;
import com.example.post.Util.WeldingDataAdapter;
import com.example.post.R;
import com.example.post.Util.WeldingDatainfo;
import com.example.post.Util.WeldingListinfo;
import com.example.post.Util.queryUtil;

import java.util.List;

/**
 * 用户管理界面业务逻辑
 */
public class UserManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private WeldingDataAdapter weldingDataAdapter;   // 用户信息数据适配器
    private ListView lv_user;   // 用户列表组件
    private Handler mainHandler;   // 主线程
    private List<WeldingDatainfo> weldingDatainfoList;   // 用户数据集合
    private TextView tv_wireDiameter;
    private String wireDiameter;
    public static List<WeldingListinfo> weldingListinfoList;
    public static int i, j, k, l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welding_list);
        initView();
        loadUserDb();
    }

    private void initView(){
        ImageView btn_back = findViewById(R.id.btn_back);
        ImageView btn_next = findViewById(R.id.btn_next);
        lv_user = findViewById(R.id.lv_data);
        mainHandler = new Handler(getMainLooper());
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_wireDiameter = findViewById(R.id.tv_wireDiameter);
    }

    private void loadUserDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                weldingDatainfoList = null;
                weldingListinfoList = queryUtil.parseJson(str);
                for (i=0; weldingDatainfoList == null && i< weldingListinfoList.size(); i++) {
                    wireDiameter = weldingListinfoList.get(i).getWireDiameter();
                    weldingDatainfoList = weldingListinfoList.get(i).getWeldingList();
                }
                showLvData();
            }
        }).start();
    }

    // 显示列表数据的方法
    private void showLvData() {
        if (weldingDataAdapter == null) {   // 首次加载时的操作
            weldingDataAdapter = new WeldingDataAdapter(this, weldingDatainfoList);
            lv_user.setAdapter(weldingDataAdapter);
        } else {   // 更新数据时的操作
            weldingDataAdapter.setweldingList(weldingDatainfoList);
            weldingDataAdapter.notifyDataSetChanged();
        }
        tv_wireDiameter.setText(wireDiameter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back)
            finish();
        else if (v.getId() == R.id.btn_next) {
            if(i == weldingListinfoList.size())   //判断是否是最后一页
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