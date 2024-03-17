package com.example.uiapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiapplication.util.CommonUtils;
import com.example.uiapplication.util.LvDataAdapter;
import com.example.uiapplication.util.Weldinginfo;
import com.example.uiapplication.util.oneGroupInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class query2Activity extends AppCompatActivity implements View.OnClickListener  {

    private LvDataAdapter lvDataAdapter;   // 用户信息数据适配器
    private ListView lv_user;   // 用户列表组件
    private TextView tv_wireDiameter;
    private String  wireDiameter;
    private Handler mainHandler;   // 主线程
    private List<oneGroupInfo> pramlist;   // 数据集合
    private int page_count=0, page_index=1;//初始为第一页
    private GestureDetector gestureDetector;
    private List<Weldinginfo> wlist;
    private final String DATABASE_NAME = "SQLite.db";
    private SQLiteDatabase db;
    private final ArrayList<String> wireDiameterList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query2);
        initView();
        // 检查数据库文件是否存在，如果不存在则从 assets 目录中复制
        boolean isDbExist = checkDatabase();
        if(!isDbExist) {
            copyDatabase();
        }
        loadUserDb();
    }

    private void initView(){
        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        tv_wireDiameter = findViewById(R.id.tv_wireDiameter);
        mainHandler = new Handler(getMainLooper());
        /*设置其滑动监听*/
        lv_user = findViewById(R.id.lv_data);
        gestureDetector=new GestureDetector(this,new MyGestureListener());
        lv_user.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
        //在listview区域外也能滑动
        LinearLayout layout = findViewById(R.id.activity_query2);
        layout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
    }

    private void loadUserDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                Bundle bundle = getIntent().getExtras();
                assert bundle != null;

                ArrayList<String> mList = new ArrayList<>();
                String s_material = bundle.getString("material");
                mList = bundle.getStringArrayList("mList");
                String s_method = bundle.getString("method");

                //需要显示的参数
                ArrayList<String> keys = new ArrayList<>();
                keys.add("ArcingCurrent");
                keys.add("PulseBaseCurrent");
                keys.add("PulsingCurrent");
                keys.add("PulseFreqency");
                keys.add("WireFeedSpeed");
                keys.add("Voltage command value");
                keys.add("Guideline value for material");
                keys.add("Amperage guideline value");
                keys.add("Voltage guideline value");


                Cursor cursor = null;
                wlist = new ArrayList<>();

                for (int i = 0; i < mList.size(); i += 2) {
                    List<oneGroupInfo> list = new ArrayList<>(); // 建立list存放单组数据
                    Weldinginfo witem = new Weldinginfo();
                    String s_diameter = mList.get(i);
                    wireDiameterList.add(s_diameter);   //建立数组存放焊丝直径
                    String s_index = mList.get(i + 1);
                    String[] projection = {"ParamName", "ParamValue"};//查询的数据列
                    String selection = "WeldingMaterial = ? and WireDiameter = ?  and ParamIndex = ? and WeldingMethod = ?"; // 查询条件
                    String[] selectionArgs = {s_material, s_diameter, s_index, s_method};
                    cursor = db.query("paramsdatatable", projection, selection, selectionArgs, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int paramNameIndex = cursor.getColumnIndex("ParamName");
                        int paramValueIndex = cursor.getColumnIndex("ParamValue");
                        // 检查列名是否存在
                        if (paramNameIndex >= 0 && paramValueIndex >= 0) {
                            do {
                                // 获取每行数据的 ParamName 和 ParamValue 列值
                                String paramName = cursor.getString(paramNameIndex);
                                String paramValue = cursor.getString(paramValueIndex);
                                if (keys.contains(paramName)) {
                                    oneGroupInfo item = new oneGroupInfo(); // 创建单个参数对象
                                    item.setParamValue(paramValue); // 设置参数的数值
                                    item.setParamName(paramName); // 设置参数名称
                                    list.add(item); // 将设置好的参数加入列表当中
                                }
                            } while (cursor.moveToNext());
                        }
                    }
                    if (!list.isEmpty()) {
                        witem.setWeldingList(list);//存储一组参数
                        wlist.add(witem);//存储一组焊丝直径下的参数
                    }
                    page_count++;
                }

                cursor.close();
                db.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pramlist = null;
                        pramlist = wlist.get(page_index-1).getWeldingList();//这个直径下的参数列表
                        wireDiameter = "WireDiameter:" + wireDiameterList.get(page_index-1);
                        showLvData(wireDiameter, pramlist);//显示一组参数
                    }
                });
            }
        }).start();
    }

    //显示一组焊丝直径对应的参数表
    private void showLvData( String wireDiameter, List<oneGroupInfo> pramlist) {
        if (lvDataAdapter == null) {
            // 首次加载时的操作
            lvDataAdapter = new LvDataAdapter(this, pramlist);
            lv_user.setAdapter(lvDataAdapter);
        } else {
            // 更新数据时的操作
            if(pramlist==null) CommonUtils.showDlgMsg(query2Activity.this, "数据库当中没有相关信息");
            else {
                lvDataAdapter.setweldingList(pramlist);
                lvDataAdapter.notifyDataSetChanged();
            }
        }
        tv_wireDiameter.setText(wireDiameter);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.btn_back) finish();
    }

    private boolean checkDatabase() {
        File file = new File(getDatabasePath(DATABASE_NAME).getPath());
        return file.exists();
    }

    private void copyDatabase() {
        try {
            InputStream inputStream = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath(DATABASE_NAME).getPath();
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
    }


    /*用户实现的的监听器*/
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private float startX,startY,endX,endY;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // 在这里处理滑动手势，根据需要执行相应的操作
            // e1：表示手势动作的起始事件，包含了手势动作开始时的信息，比如手指按下的位置、时间等
            // e2：表示手势动作的结束事件，包含了手势动作结束时的信息，比如手指抬起的位置、时间等。
            //velocityX：表示手指在X轴方向上的速度，单位是像素/秒。它表示手指在水平方向上的移动速度
            //velocityY：表示手指在Y轴方向上的速度，单位同样是像素/秒。它表示手指在垂直方向上的移动速度。
            /*水平滑动*/
            if(Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX > 0) {
                    // 手指向右滑动
                    page_index--;
                    if (page_index<1)
                    {
                        page_index++;
                        CommonUtils.showDlgMsg(query2Activity.this, "第一页");
                    }
                    else {
                        pramlist = wlist.get(page_index-1).getWeldingList();//这个直径下的参数列表

                        wireDiameter = "WireDiameter:" + wireDiameterList.get(page_index-1);

                        showLvData(wireDiameter, pramlist);//显示一组参数
                    }
                } else {
                    // 手指向左滑动,切换到下一页
                    page_index++;//页面索引递增
                    if(page_index > page_count)   //判断是否是最后一页
                    {
                        page_index--;
                        CommonUtils.showDlgMsg(query2Activity.this, "最后一页");
                    }
                    else {
                        pramlist = wlist.get(page_index-1).getWeldingList();//这个直径下的参数列表
                        wireDiameter = "WireDiameter:" + wireDiameterList.get(page_index-1);
                        showLvData(wireDiameter, pramlist);//显示一组参数
                    }
                }
            }
            return true;
        }
    }
}