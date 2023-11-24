package com.example.post.MulVquery;

import static com.example.post.MulVquery.MulWeldingDataActivity.k;
import static com.example.post.MulVquery.MulWeldingDataActivity.l;
import static com.example.post.MulVquery.MulWeldingDataActivity.weldingListinfo;
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
public class MulWeldingDataActivity4 extends AppCompatActivity implements View.OnClickListener {

    private WeldingDataAdapter weldingDataAdapter;
    private ListView lv_data;   // 列表组件
    private Handler mainHandler;   // 主线程
    private List<WeldingDatainfo> weldingDatainfo4;
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
        ImageButton btn_back = findViewById(R.id.btn_back);
        ImageButton btn_next = findViewById(R.id.btn_next);
        lv_data = findViewById(R.id.lv_data);
        mainHandler = new Handler(getMainLooper());
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_wireDiameter4 = findViewById(R.id.tv_wireDiameter);
    }

    private void loadUserDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                weldingDatainfo4 = null;
                for (l = k; weldingDatainfo4 == null && k < weldingListinfo.size(); k++) {
                    wireDiameter4 = weldingListinfo.get(l).getWireDiameter();
                    weldingDatainfo4 = weldingListinfo.get(l).getWeldingList();
                }
                showLvData();
            }
        }).start();
    }

    // 显示列表数据的方法
    private void showLvData() {
        if (weldingDataAdapter == null) {   // 首次加载时的操作
            weldingDataAdapter = new WeldingDataAdapter(this, weldingDatainfo4);
            lv_data.setAdapter(weldingDataAdapter);
        } else {   // 更新数据时的操作*/
            weldingDataAdapter.setweldingList(weldingDatainfo4);
            weldingDataAdapter.notifyDataSetChanged();
        }
        tv_wireDiameter4.setText(wireDiameter4);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back)
            finish();
        else if (v.getId() == R.id.btn_next)
            CommonUtils.showDlgMsg(MulWeldingDataActivity4.this, "最后一页");
    }
}