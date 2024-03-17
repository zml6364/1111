package com.example.uiapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiapplication.util.CommonUtils;
import com.example.uiapplication.util.fileDisplay;
import com.example.uiapplication.util.savedFile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class dataSaveActivity extends AppCompatActivity implements View.OnClickListener {

    private String filepath;
    private fileDisplay fd;
    private ListView lv_file;
    private List<savedFile> savedFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_save);

        findViewById(R.id.bnt_clear_all_file).setOnClickListener(this);
        findViewById(R.id.bnt_flush).setOnClickListener(this);

        lv_file = findViewById(R.id.lv_file);
        filepath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "";

    }

    @Override
    public void onClick(View v)
    {
        if (R.id.bnt_clear_all_file == v.getId())
        {
            File directory = new File(filepath);

            File[] files = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith(".db");
                }
            });

            try {
                int file_delete_count=0;
                savedFileList=new ArrayList<>();
                for (File file : files)
                {
                    if (file.delete()){
                        file_delete_count++;
                    }
                    else CommonUtils.showLonMsg(this,  file.getName()+"删除失败");
                }
                CommonUtils.showLonMsg(this, "删除文件数量为："+String.valueOf(file_delete_count));
            }catch (Exception e){

                Intent intent = new Intent(dataSaveActivity.this, ErrActivity.class);
                Bundle bundle = new Bundle(); // 创建一个新包裹
                bundle.putString("Error",e.toString());
                intent.putExtras(bundle); // 把快递包裹塞给意图
                startActivity(intent); // 跳转到意图指定的活动页面
            }


        } else if (R.id.bnt_flush==v.getId()) {
            try {
                File directory = new File(filepath);
                File[] files = directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        //return file.getName().endsWith("save.json");
                        return file.getName().endsWith(".db");
                    }
                });

                int file_delete_count=0;
                savedFileList=new ArrayList<>();
                for (File file : files)
                {
                    file_delete_count++;
                    /*创建listview相关组件*/
                    savedFile f = new savedFile(file.getName(),file.getPath());
                    savedFileList.add(f);
                }
                CommonUtils.showDlgMsg(dataSaveActivity.this,"刷新成功！新增文件数量为："+String.valueOf(file_delete_count));
                showFile(savedFileList);

            }catch (Exception e){
                Intent intent = new Intent(dataSaveActivity.this, ErrActivity.class);
                Bundle bundle = new Bundle(); // 创建一个新包裹
                bundle.putString("Error",e.toString());
                intent.putExtras(bundle); // 把快递包裹塞给意图
                startActivity(intent); // 跳转到意图指定的活动页面
            }
        }
    }

    private void showFile( List<savedFile> list) {
        if (fd == null) {
            // 首次加载时的操作
            fd = new fileDisplay(this, list);
            lv_file.setAdapter(fd);
        } else {
            // 更新数据时的操作
            if(list==null) CommonUtils.showDlgMsg(dataSaveActivity.this, "数据库当中没有相关信息");
            else {
                fd.setFilelist(list);
                fd.notifyDataSetChanged();
            }
        }
    }
}