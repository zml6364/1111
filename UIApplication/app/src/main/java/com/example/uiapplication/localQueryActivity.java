package com.example.uiapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uiapplication.util.CommonUtils;
import com.example.uiapplication.util.LvDataAdapter2;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class localQueryActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btn_vback;
    private ImageButton bt_material, bt_thinkness, bt_method;
    private TextView tv_material, tv_thinkness, tv_method; 
    private View rl_material, rl_thinkness, rl_method;
    private String s_material, s_thinkness, s_index, s_method;
    private Handler mainHandler ;     // 主线程
    private final String DATABASE_NAME = "SQLite.db";
    private SQLiteDatabase db;
    private final PopupWindow[] popupWindows = new PopupWindow[3]; // 存储三个 PopupWindow

    public localQueryActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_local_query);

        // 在onCreate方法中获取根布局
        // 点击空白处关闭键盘
        View rootLayout = findViewById(android.R.id.content);
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 关闭键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });

        initView();

        // 检查数据库文件是否存在，如果不存在则从 assets 目录中复制
        boolean isDbExist = checkDatabase();
        if(!isDbExist) {
            copyDatabase();
        }

        LvDataAdapter2 adapter = new LvDataAdapter2(localQueryActivity.this, getMaterials(), tv_material);
        ListView lv_materials = new ListView(localQueryActivity.this);
        lv_materials.setAdapter(adapter);
        bt_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_thinkness = tv_thinkness.getText().toString().trim();
                s_method = tv_method.getText().toString().trim();
                adapter.updateData(getMaterials()); // 更新适配器的数据源
                showPopupWindow(0, lv_materials, rl_material);
            }
        });

        LvDataAdapter2 adapter2 = new LvDataAdapter2(localQueryActivity.this, getthinknesss(), tv_thinkness);
        ListView lv_thinknesss = new ListView(localQueryActivity.this);
        lv_thinknesss.setAdapter(adapter2);
        bt_thinkness.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                s_material = tv_material.getText().toString().trim();
                s_method = tv_method.getText().toString().trim();
                adapter2.updateData(getthinknesss()); // 更新适配器的数据源
                showPopupWindow(1, lv_thinknesss, rl_thinkness);
            }
        });

        LvDataAdapter2 adapter3 = new LvDataAdapter2(localQueryActivity.this, getMethods(), tv_method);
        ListView lv_methods = new ListView(localQueryActivity.this);
        lv_methods.setAdapter(adapter3);
        bt_method.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                s_material = tv_material.getText().toString().trim();
                s_thinkness = tv_thinkness.getText().toString().trim();
                adapter3.updateData(getMethods()); // 更新适配器的数据源
                showPopupWindow(2, lv_methods, rl_method);
            }
        });
    }

    private void initView(){
        rl_material = findViewById(R.id.rl_material);
        bt_material = (ImageButton)findViewById(R.id.bt_material);
        tv_material = (TextView) findViewById(R.id.tv_material);

        rl_thinkness = findViewById(R.id.rl_thinkness);
        bt_thinkness = (ImageButton)findViewById(R.id.bt_thinkness);
        tv_thinkness = (TextView)findViewById(R.id.tv_thinkness);

        rl_method = findViewById(R.id.rl_method);
        bt_method = (ImageButton)findViewById(R.id.bt_method);
        tv_method = (TextView)findViewById(R.id.tv_method);

        // 查询按钮
        Button btn_query = findViewById(R.id.btn_query);
        btn_query.setOnClickListener(this);
        mainHandler = new Handler(getMainLooper());   // 获取主线程

        btn_vback = findViewById(R.id.btn_vback2);
        btn_vback.setOnClickListener(this);
    }

    private boolean checkDatabase() {
        File file = new File(getDatabasePath(DATABASE_NAME).getPath());
        return file.exists();
    }

    private void copyDatabase() {
        try {
            InputStream inputStream = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath(DATABASE_NAME).getPath();
            OutputStream outputStream = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                outputStream = Files.newOutputStream(Paths.get(outFileName));
            }
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException ignored) {
        }
    }

    private List<String> getMaterials() {
        String selection;
        String[] selectionArgs;
        Cursor cursor;
        List<String> materials = new ArrayList<>();
        try {
            if ((s_thinkness == "" && s_method == "") || s_material != ""){//防止卡死
                materials.add("Al-Si-5");
                materials.add("Al-Mg-5");
                materials.add("Al-99.5");
                materials.add("Steel");
                materials.add("Cr-Ni-199");
                materials.add("Cu-Si-3");
            } else if (s_method != "") {
                db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                selection = "WeldingMethod = ?"; // 查询条件
                selectionArgs = new String[]{s_method};
                cursor = db.rawQuery("SELECT DISTINCT WeldingMaterial FROM paramsdatatable WHERE " + selection, selectionArgs);
                if (cursor != null && cursor.getCount() > 0) {
                    int columnIndex = cursor.getColumnIndex("WeldingMaterial");
                    while (cursor.moveToNext()) {
                        String material = cursor.getString(columnIndex);
                        materials.add(material);
                    }
                    cursor.close();
                }
            } else {
                db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                selection = "Wirethinkness = ?"; // 查询条件
                selectionArgs = new String[]{s_thinkness};
                cursor = db.rawQuery("SELECT DISTINCT WeldingMaterial FROM paramsdatatable WHERE " + selection, selectionArgs);
                if (cursor != null && cursor.getCount() > 0) {
                    int columnIndex = cursor.getColumnIndex("WeldingMaterial");
                    while (cursor.moveToNext()) {
                        String material = cursor.getString(columnIndex);
                        materials.add(material);
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return materials;
    }

    private List<String> getthinknesss() {
        String selection;
        String[] selectionArgs;
        Cursor cursor;
        List<String> thinknesss = new ArrayList<>();
        try {
            if (s_material == "" && s_method == ""){
                thinknesss.add("0.8");
                thinknesss.add("1");
                thinknesss.add("1.2");
                thinknesss.add("1.6");
            } else if (s_material != "") {
                db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                selection = "WeldingMaterial = ?"; // 查询条件
                selectionArgs = new String[]{s_material};
                cursor = db.rawQuery("SELECT DISTINCT Wirethinkness FROM paramsdatatable WHERE " + selection, selectionArgs);
                if (cursor != null && cursor.getCount() > 0) {
                    int columnIndex = cursor.getColumnIndex("Wirethinkness");
                    while (cursor.moveToNext()) {
                        String thinkness = cursor.getString(columnIndex);
                        thinknesss.add(thinkness);
                    }
                    cursor.close();
                }
            } else {
                    db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                    selection = "WeldingMethod = ?"; // 查询条件
                    selectionArgs = new String[]{s_method};
                    cursor = db.rawQuery("SELECT DISTINCT Wirethinkness FROM paramsdatatable WHERE " + selection + " ORDER BY Wirethinkness ASC", selectionArgs);
                    if (cursor != null && cursor.getCount() > 0) {
                        int columnIndex = cursor.getColumnIndex("Wirethinkness");
                        while (cursor.moveToNext()) {
                            String thinkness = cursor.getString(columnIndex);
                            thinknesss.add(thinkness);
                        }
                        cursor.close();
                    }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return thinknesss;
    }

    private List<String> getMethods() {
        String selection;
        String[] selectionArgs;
        Cursor cursor;
        List<String> methods = new ArrayList<>();
        try {
            if (s_material == "" && s_thinkness == ""){
                methods.add("MIG");
                methods.add("GMAW");
            } else if (s_material != "") {
                db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                selection = "WeldingMaterial = ?"; // 查询条件
                selectionArgs = new String[]{s_material};
                cursor = db.rawQuery("SELECT DISTINCT WeldingMethod FROM paramsdatatable WHERE " + selection, selectionArgs);
                if (cursor != null && cursor.getCount() > 0) {
                    int columnIndex = cursor.getColumnIndex("WeldingMethod");
                    while (cursor.moveToNext()) {
                        String method = cursor.getString(columnIndex);
                        methods.add(method);
                    }
                    cursor.close();
                }
            } else {
                db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                selection = "Wirethinkness = ?"; // 查询条件
                selectionArgs = new String[]{s_thinkness};
                cursor = db.rawQuery("SELECT DISTINCT WeldingMethod FROM paramsdatatable WHERE " + selection, selectionArgs);
                if (cursor != null && cursor.getCount() > 0) {
                    int columnIndex = cursor.getColumnIndex("WeldingMethod");
                    while (cursor.moveToNext()) {
                        String method = cursor.getString(columnIndex);
                        methods.add(method);
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return methods;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_query) {
            doQuery();
        } else if (v.getId() == R.id.btn_vback2) {
            finish();//关闭页面
        }
    }

    private void doQuery(){
        s_material = tv_material.getText().toString().trim();
        s_thinkness = tv_thinkness.getText().toString().trim();
        s_method = tv_method.getText().toString().trim();
        if(TextUtils.isEmpty(s_material)){
            CommonUtils.showShortMsg(this, "请选择焊接材料");
            tv_material.requestFocus();
        }else if(TextUtils.isEmpty(s_thinkness)){
            CommonUtils.showShortMsg(this, "请选择焊接直径");
            tv_thinkness.requestFocus();
        }else if(TextUtils.isEmpty(s_method)){
            CommonUtils.showShortMsg(this, "请选择焊接方法");
            tv_method.requestFocus();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            db = SQLiteDatabase.openDatabase(getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                            String[] projection = {"Wirethinkness", "ParamIndex"};//查询的数据列
                            String selection = "WeldingMaterial = ? and ParamName = 'Guideline value for material' and ParamValue = ? and WeldingMethod = ?"; // 查询条件
                            String[] selectionArgs = {s_material, s_thinkness, s_method};

                            ArrayList<String> mList = new ArrayList<>();
                            Cursor cursor = db.query("paramsdatatable", projection, selection, selectionArgs, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                int wirethinknessIndex = cursor.getColumnIndex("Wirethinkness");
                                int paramIndexIndex = cursor.getColumnIndex("ParamIndex");

                                // 检查列名是否存在
                                if (wirethinknessIndex >= 0 && paramIndexIndex >= 0) {
                                    do {
                                        // 获取每行数据的 Wirethinkness 和 ParamIndex 列值
                                        String wirethinkness = cursor.getString(wirethinknessIndex);
                                        String paramIndex = cursor.getString(paramIndexIndex);

                                        // 将数据添加到结果列表中
                                        mList.add(wirethinkness);
                                        mList.add(paramIndex);
                                    } while (cursor.moveToNext());
                                }
                            }


                            /*wlist = new ArrayList<>();
                            for (int i = 0; i < resultList.size(); i += 2) {
                                List<oneGroupInfo> list = new ArrayList<>(); // 建立list存放单组数据
                                Weldinginfo witem = new Weldinginfo();
                                String s_thinkness = resultList.get(i);
                                String s_index = resultList.get(i + 1);
                                String[] projection2 = {"ParamName", "ParamValue"};//查询的数据列
                                String selection2 = "WeldingMaterial = ? and Wirethinkness = ?  and ParamIndex = ? and WeldingMethod = ?"; // 查询条件
                                String[] selectionArgs2 = {s_material, s_thinkness, s_index, s_method};
                                Cursor cursor2 = db.query("paramsdatatable", projection2, selection2, selectionArgs2, null, null, null);
                                if (cursor2 != null && cursor2.moveToFirst()) {
                                    int paramNameIndex = cursor2.getColumnIndex("ParamName");
                                    int paramValueIndex = cursor2.getColumnIndex("ParamValue");
                                    // 检查列名是否存在
                                    if (paramNameIndex >= 0 && paramValueIndex >= 0) {
                                        do {
                                            // 获取每行数据的 ParamName 和 ParamValue 列值
                                            String paramName = cursor2.getString(paramNameIndex);
                                            String paramValue = cursor2.getString(paramValueIndex);
                                            // 处理查询结果
                                            oneGroupInfo item = new oneGroupInfo(); // 创建单个参数对象
                                            item.setParamValue(paramValue); // 设置参数的数值
                                            item.setParamName(paramName); // 设置参数名称
                                            list.add(item); // 将设置好的参数加入列表当中
                                        } while (cursor2.moveToNext());
                                    }
                                }
                                if(!list.isEmpty()) {
                                    witem.setWeldingList(list);//存储一组参数
                                    wlist.add(witem);//存储一组焊丝直径下的参数
                                    int a = 0;
                                }
                            }*/

                            cursor.close();
                            db.close();

                            if(!mList.isEmpty()){
                                Intent intent = new Intent(localQueryActivity.this, query2Activity.class);
                                Bundle bundle = new Bundle(); // 创建一个新包裹
                                bundle.putString("material", s_material);//将参数传递给下一个页面
                                bundle.putStringArrayList("mList", mList);
                                bundle.putString("method", s_method);
                                intent.putExtras(bundle); // 把快递包裹塞给意图
                                startActivity(intent); // 跳转到意图指定的活动页面
                            } else {
                                CommonUtils.showLonMsg(localQueryActivity.this, "查询失败");
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void showPopupWindow(int index, ListView listView, View anchorView) {
        // 点击下拉框时隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(anchorView.getWindowToken(), 0);

        if (popupWindows[index] == null) {
            //popupWindows[index] = new PopupWindow(listView, anchorView.getWidth(), listView.getCount() * anchorView.getHeight());
            popupWindows[index] = new PopupWindow(listView, anchorView.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindows[index].setOutsideTouchable(true); // 设置点击框外关闭
            popupWindows[index].setFocusable(true);
            popupWindows[index].setBackgroundDrawable(new ColorDrawable(Color.WHITE)); // 将背景色设置为白色
            popupWindows[index].setAnimationStyle(android.R.style.Animation_Dialog);

            // 获取父View并计算按钮在父View中的位置
            View parentView = anchorView.getRootView();
            int[] location = new int[2];
            anchorView.getLocationOnScreen(location);
            // 显示PopupWindow
            popupWindows[index].showAtLocation(parentView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());

        } else {
            if (popupWindows[index].isShowing()) {
                popupWindows[index].dismiss();
            } else {
                // 在文本框下显示PopupWindow
                View parentView = (View) anchorView.getParent();
                int[] location = new int[2];
                anchorView.getLocationOnScreen(location);
                popupWindows[index].showAtLocation(parentView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());

            }
        }
    }

    public void dismissPopupWindow() {
        for (int i = 0; i < popupWindows.length; i++) {
            if (popupWindows[i] != null && popupWindows[i].isShowing()) {
                popupWindows[i].dismiss();
            }
        }
    }
}