package com.example.post.MulVquery;

import static com.example.post.MulVquery.MulWeldingDataActivity.i;
import static com.example.post.MulVquery.MulWeldingDataActivity.j;
import static com.example.post.MulVquery.MulWeldingDataActivity.weldingListinfo;
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
import java.util.List;

/**
 * 用户管理界面业务逻辑
 */
public class MulWeldingDataActivity2 extends AppCompatActivity implements View.OnClickListener {

    private WeldingDataAdapter weldingDataAdapter;   
    private ListView lv_data;   // 列表组件
    private Handler mainHandler;   // 主线程
    private List<WeldingDatainfo> weldingDatainfo2;   
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

        ImageButton btn_back = findViewById(R.id.btn_back);
        ImageButton btn_next = findViewById(R.id.btn_next);
        lv_data = findViewById(R.id.lv_data);
        mainHandler = new Handler(getMainLooper());
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_wireDiameter2 = findViewById(R.id.tv_wireDiameter);
    }

    private void loadUserDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                weldingDatainfo2 = null;

                for (j=i; weldingDatainfo2 == null && j< weldingListinfo.size(); j++) {
                    wireDiameter2 = weldingListinfo.get(j).getWireDiameter();
                    weldingDatainfo2 = weldingListinfo.get(j).getWeldingList();
                }

                showLvData();
            }
        }).start();
    }

    // 显示列表数据的方法
    private void showLvData() {
        if (weldingDataAdapter == null) {   // 首次加载时的操作
            weldingDataAdapter = new WeldingDataAdapter(this, weldingDatainfo2);
            lv_data.setAdapter(weldingDataAdapter);
        } else {   // 更新数据时的操作*/
            weldingDataAdapter.setweldingList(weldingDatainfo2);
            weldingDataAdapter.notifyDataSetChanged();
        }
        tv_wireDiameter2.setText(wireDiameter2);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back)
            finish();
        else if (v.getId() == R.id.btn_next) {
            if(j == weldingListinfo.size())   //判断是否是最后一页
                CommonUtils.showDlgMsg(MulWeldingDataActivity2.this, "最后一页");
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
                        Intent intent = new Intent(MulWeldingDataActivity2.this, MulWeldingDataActivity3.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
}