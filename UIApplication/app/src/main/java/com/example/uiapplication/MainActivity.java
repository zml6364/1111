package com.example.uiapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.uiapplication.util.CommonUtils;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_IMAGE_CAPTURE = 114;
    private ImageView textView_topTitle;
    private FrameLayout content;
    private NavigationView nav_view;
    private ImageButton btn_nva;
    private DrawerLayout drawer_layout;
    private RadioGroup rg_main;
    private ImageView help_info;
    private Handler mainHandler;   // 主线程
    private LinearLayout main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*隐藏系统状态栏*/
        Hide_SystemUI();
        /*设置状态栏颜色*/
        int[] colors = {Color.parseColor("#ffffff"), Color.parseColor("#C5C5C5")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        getWindow().getDecorView().setBackground(gradientDrawable);

        setContentView(R.layout.activity_main);
        initView();
    }


    //侧边栏的选项点击监听
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if(menuItem.getItemId()==R.id.camera)
        {
            drawer_layout.closeDrawer(GravityCompat.START);//关闭侧滑栏

            /*开启相机*/
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        else if (menuItem.getItemId()==R.id.wifi) {

            drawer_layout.closeDrawer(GravityCompat.START);//关闭侧滑栏
        }
        else if (menuItem.getItemId()==R.id.data) {
            drawer_layout.closeDrawer(GravityCompat.START);//关闭侧滑栏
            /*转跳到指定页面*/
            Intent intent = new Intent(this, dataSaveActivity.class);
            startActivity(intent); // 跳转到意图指定的活动页面

        }
        else if (menuItem.getItemId()==R.id.status) {
            drawer_layout.closeDrawer(GravityCompat.START);//关闭侧滑栏
            startActivity(new Intent(this, statusActivity.class));
        }
        else if (menuItem.getItemId()==R.id.param) {
            drawer_layout.closeDrawer(GravityCompat.START);//关闭侧滑栏
        }

        return false;
    }

    //按钮监听
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_nva)//
        {
            drawer_layout.openDrawer(GravityCompat.START);//设置左边菜单栏显示出来
        }
        else if (R.id.help_info==v.getId()) {

            new Thread(String.valueOf(mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        /*弹出对话框*/
                        //构建对话框构建器
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("帮助信息 ：");
                        builder.setMessage("请点击 本地查询/智能查询开始查询焊接参数");//json文件的response部分
                        builder.setNegativeButton("取消", null);
                        /*设置对话框按键的监听和文本*/
                        builder.setPositiveButton("确定", null);
                        // 根据建造器构建提醒对话框对象
                        AlertDialog alert = builder.create();
                        alert.show();
                    }catch (Exception e){
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showShortMsg(MainActivity.this,e.toString());
                            }
                        });
                    }
                }
            }))).start();

        }
    }

    //底部状态栏点击监听
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.rb_me)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content,new SettingFragment()).commit();
        } else if (checkedId==R.id.rb_home) {

            getSupportFragmentManager().beginTransaction().replace(R.id.content,new HomeFragment()).commit();
        } else if (checkedId==R.id.rb_message) {
            /*待添加*/
            getSupportFragmentManager().beginTransaction().replace(R.id.content,new SettingFragment()).commit();
        } else if (checkedId==R.id.rb_community) {
            /*待添加*/
            getSupportFragmentManager().beginTransaction().replace(R.id.content,new SettingFragment()).commit();
        }
    }


    private void initView() {

        main_layout = findViewById(R.id.main_layout);
        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hide_SystemUI();
            }
        });

        /*主线程句柄*/
        mainHandler = new Handler(getMainLooper());

        textView_topTitle = (ImageView) findViewById(R.id.textView_topTitle);//标题
        content = (FrameLayout) findViewById(R.id.content);//Fragment碎片布局
        //左侧隐藏的NavigationView布局
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);//nva菜单的Item点击事件钮监听
        //左上角导航按钮
        btn_nva = (ImageButton) findViewById(R.id.btn_nva);
        btn_nva.setOnClickListener(this);//监听是否按下导航按钮
        //activity_main文件内最外层布局
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_layout.setOnClickListener(this);

        help_info = findViewById(R.id.help_info);
        help_info.setOnClickListener(this);

        rg_main = findViewById(R.id.rg_main);
        rg_main.setOnCheckedChangeListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content,new HomeFragment()).commit();//加载主界面布局

    }
    private void Hide_SystemUI(){
        View view = getWindow().getDecorView();
        WindowInsetsController insetsController = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController = view.getWindowInsetsController();
        }
        if (insetsController != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        }

    }
}