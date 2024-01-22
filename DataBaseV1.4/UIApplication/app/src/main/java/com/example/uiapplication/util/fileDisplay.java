package com.example.uiapplication.util;

import static com.example.uiapplication.util.queryUtil.readJsonFile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.example.uiapplication.R;
import com.example.uiapplication.query0Activity;
import com.example.uiapplication.query1Activity;

import java.util.List;

public class fileDisplay extends BaseAdapter {

    private Context context;    // 上下文信息
    private List<savedFile> filelist;
    private String response_file_path, response_str;

    public  fileDisplay(Context context,List<savedFile> filelist){
        this.context=context;
        this.filelist=filelist;
    }

    public void setFilelist(List<savedFile> filelist) {
        this.filelist = filelist;
    }

    private class ViewHolder{
        Button button;
    }

    @Override
    public int getCount() {
        return filelist.size();
    }

    @Override
    public Object getItem(int position) {
        return filelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        fileDisplay.ViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.file_item, null);
            viewHolder = new ViewHolder();
            viewHolder.button = convertView.findViewById(R.id.button_id);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (fileDisplay.ViewHolder) convertView.getTag();
        }

        // 这里进行数据填充
        savedFile item=filelist.get(position);
        viewHolder.button.setText(item.getFilename());
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理按钮点击事件的逻辑
                Intent intent = new Intent(context, query0Activity.class);
                Bundle bundle = new Bundle(); // 创建一个新包裹
                response_file_path = item.getFilepath();
                response_str = readJsonFile(response_file_path);
                bundle.putString("response_json", response_str);//将解析的string传递给下一个页面
                intent.putExtras(bundle); // 把快递包裹塞给意图
                context.startActivity(intent); // 跳转到意图指定的活动页面
            }
        });
        return convertView;
    }
}