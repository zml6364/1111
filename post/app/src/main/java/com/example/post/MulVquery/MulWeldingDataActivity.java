package com.example.post.MulVquery;

import static com.example.post.MulVquery.MulVquery.Mresponse;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
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


public class MulWeldingDataActivity extends AppCompatActivity implements View.OnClickListener {

    private WeldingDataAdapter weldingDataAdapter;   // 焊接数据适配器
    public ListView lv_data;   // 列表组件
    private Handler mainHandler;   // 主线程
    private List<WeldingDatainfo> weldingDatainfo;   // 焊接数据
    private TextView tv_wireDiameter;
    private String wireDiameter;
    public static List<WeldingListinfo> weldingListinfo; // 焊接数据列表
    public static int i,j, k, l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welding_list);
        initView();
        loadUserDb();
    }

    private void initView(){
        ImageButton btn_back = findViewById(R.id.btn_back);
        ImageButton btn_next = findViewById(R.id.btn_next);
        lv_data = findViewById(R.id.lv_data);
        mainHandler = new Handler(getMainLooper());
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_wireDiameter = findViewById(R.id.tv_wireDiameter);
    }

    private void loadUserDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                weldingDatainfo = null;
                weldingListinfo = queryUtil.parseJson(Mresponse);
                for (i=0; weldingDatainfo == null && i< weldingListinfo.size(); i++) {
                    wireDiameter = weldingListinfo.get(i).getWireDiameter();
                    weldingDatainfo = weldingListinfo.get(i).getWeldingList();
                }
                showLvData();
            }
        }).start();
    }

    // 显示列表数据的方法
    private void showLvData() {
        if (weldingDataAdapter == null) {   // 首次加载时的操作
            weldingDataAdapter = new WeldingDataAdapter(this, weldingDatainfo);
            lv_data.setAdapter(weldingDataAdapter);
        } else {   // 更新数据时的操作
            weldingDataAdapter.setweldingList(weldingDatainfo);
            weldingDataAdapter.notifyDataSetChanged();
        }
        tv_wireDiameter.setText(wireDiameter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back)
            finish();
        else if (v.getId() == R.id.btn_next) {
            if(i == weldingListinfo.size())   //判断是否是最后一页
            {
                CommonUtils.showDlgMsg(MulWeldingDataActivity.this, "最后一页");
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
                        Intent intent = new Intent(MulWeldingDataActivity.this, MulWeldingDataActivity2.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
}