package com.example.post.Vquery;

import static com.example.post.Vquery.UserManagerActivity.k;
import static com.example.post.Vquery.UserManagerActivity.l;
import static com.example.post.Vquery.UserManagerActivity.weldingListinfoList;

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
public class UserManagerActivity4 extends AppCompatActivity implements View.OnClickListener {

    private WeldingDataAdapter weldingDataAdapter;   // 用户信息数据适配器
    private ListView lv_user;   // 用户列表组件
    private Handler mainHandler;   // 主线程
    private List<WeldingDatainfo> weldingDatainfoList4;   // 用户数据集合
    private TextView tv_wireDiameter4;
    private String wireDiameter4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welding_list);
        initView();
        loadUserDb();
    }

    private void initView() {
        // 返回图片按钮 ，添加图片按钮
        ImageView btn_back = findViewById(R.id.btn_back);
        ImageView btn_next = findViewById(R.id.btn_next);
        lv_user = findViewById(R.id.lv_data);
        mainHandler = new Handler(getMainLooper());
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_wireDiameter4 = findViewById(R.id.tv_wireDiameter);
    }

    private void loadUserDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                weldingDatainfoList4 = null;
                for (l = k; weldingDatainfoList4 == null && k < weldingListinfoList.size(); k++) {
                    wireDiameter4 = weldingListinfoList.get(l).getWireDiameter();
                    weldingDatainfoList4 = weldingListinfoList.get(l).getWeldingList();
                }
                showLvData();
            }
        }).start();
    }

    // 显示列表数据的方法
    private void showLvData() {
        if (weldingDataAdapter == null) {   // 首次加载时的操作
            weldingDataAdapter = new WeldingDataAdapter(this, weldingDatainfoList4);
            lv_user.setAdapter(weldingDataAdapter);
        } else {   // 更新数据时的操作*/
            weldingDataAdapter.setweldingList(weldingDatainfoList4);
            weldingDataAdapter.notifyDataSetChanged();
        }
        tv_wireDiameter4.setText(wireDiameter4);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back)
            finish();
        else if (v.getId() == R.id.btn_next)
            CommonUtils.showDlgMsg(UserManagerActivity4.this, "最后一页");
    }
}