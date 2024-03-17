package com.example.uiapplication;

import android.os.Bundle;
import android.os.Environment;
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
import com.example.uiapplication.util.queryUtil;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class query1Activity extends AppCompatActivity implements View.OnClickListener {


    private LvDataAdapter lvDataAdapter;   // 用户信息数据适配器
    private ListView lv_user;   // 用户列表组件
    private Handler mainHandler;   // 主线程
    private List<oneGroupInfo> pramlist;   // 用户数据集合
    private TextView tv_wireDiameter;
    private String wireDiameter;
    public static List<Weldinginfo> weldinginfoList;//不同焊丝直径对应的参数组
    public static int wireDiameter_count,page_index=1;//初始为第一页
    private GestureDetector gestureDetector;
    private ImageView btn_save;
    private ImageView btn_back;
    private String response_file_path;
    private String filename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query1);

        initView();
        loadUserDb();
    }

    private void initView(){
        btn_back = findViewById(R.id.btn_back);
        btn_save = findViewById(R.id.btn_save);
        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        mainHandler = new Handler(getMainLooper());
        tv_wireDiameter = findViewById(R.id.tv_wireDiameter);

        /*设置其滑动监听*/
        lv_user = findViewById(R.id.lv_user);
        gestureDetector=new GestureDetector(this,new MyGestureListener());
        lv_user.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });

        //在listview区域外也能滑动
        LinearLayout layout = findViewById(R.id.activity_query1);
        layout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });

    }


    private void loadUserDb(){
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                pramlist = null;
                Bundle bundle = getIntent().getExtras();
                String str = bundle.getString("response_json");//获取上一个页面传递来的str
                response_file_path = bundle.getString("response_filepath");
                filename = bundle.getString("response_filename");;
                weldinginfoList = queryUtil.parseJson(str);//将str转换为参数列表
                for (wireDiameter_count=0; pramlist == null && wireDiameter_count<weldinginfoList.size(); wireDiameter_count++) {/*统计页面数量*/}

                /*显示第一组参数*/
                page_index=1;
                wireDiameter = weldinginfoList.get(page_index-1).getWireDiameter();//焊丝直径
                pramlist = weldinginfoList.get(page_index-1).getWeldingList();//这个直径下的参数列表
                showLvData(wireDiameter,pramlist);//显示一组参数
            }
        }).start();

    }

    //显示一组焊丝直径对应的参数表
    private void showLvData( String wireDiameter,List<oneGroupInfo> pramlist) {
        if (lvDataAdapter == null) {
            // 首次加载时的操作
            lvDataAdapter = new LvDataAdapter(this, pramlist);
            lv_user.setAdapter(lvDataAdapter);
        } else {
            // 更新数据时的操作
            if(pramlist==null) CommonUtils.showDlgMsg(query1Activity.this, "数据库当中没有相关信息");
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
        else if (v.getId() == R.id.btn_save) {
            /*SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String filename=formatter.format(date).toString()+"save.json";*/

            String file_path=fileSave(filename,response_file_path);
            CommonUtils.showDlgMsg(query1Activity.this,"保存成功！");

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
                        CommonUtils.showDlgMsg(query1Activity.this, "第一页");
                    }
                    else {
                        wireDiameter = weldinginfoList.get(page_index-1).getWireDiameter();//焊丝直径
                        pramlist = weldinginfoList.get(page_index-1).getWeldingList();//这个直径下的参数列表
                        showLvData(wireDiameter,pramlist);//显示一组参数
                    }

                } else {
                    // 手指向左滑动,切换到下一页
                    page_index++;//页面索引递增
                    if(page_index > wireDiameter_count)   //判断是否是最后一页
                    {
                        page_index--;
                        CommonUtils.showDlgMsg(query1Activity.this, "最后一页");
                    }
                    else {
                        wireDiameter = weldinginfoList.get(page_index-1).getWireDiameter();//焊丝直径
                        pramlist = weldinginfoList.get(page_index-1).getWeldingList();//这个直径下的参数列表
                        showLvData(wireDiameter,pramlist);//显示一组参数
                    }
                    // 执行相应的操作，比如切换到上一页
                }
            }

            return true;
        }
    }



    /*
     * 参数：保存文件的名称，源文件路径(query_file_path  response_file_path)
     * 返回：文件的路径
     * */
    private String fileSave(String filename,String srcFile){

        String filepath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+filename;
        File target=new File(filepath);//目标文件
        File src=new File(srcFile);//源文件
        try {
            Files.copy(src.toPath(),target.toPath());
        }catch (Exception e)
        {
           e.printStackTrace();
        }
        return  filepath;
    }

}