package com.example.uiapplication.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.example.uiapplication.R;
import com.example.uiapplication.localQueryActivity;
import java.util.List;

/**
 * 下拉框适配器类
 */
// LvDataAdapter2.java

public class LvDataAdapter2 extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> dataList;
    private TextView textView;


    public LvDataAdapter2(Context context, List<String> dataList, TextView textView) {
        this.context = context;
        this.dataList = dataList;
        this.textView = textView;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.content = convertView.findViewById(R.id.text_row);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String contentText = dataList.get(position);
        viewHolder.content.setText(contentText);

        // 设置分割线颜色为黑色
        convertView.setBackground(new ColorDrawable(Color.BLACK));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(contentText);
                textView.setTextColor(Color.BLACK); // 将文字颜色设置为黑色
                if (context instanceof localQueryActivity) {
                    localQueryActivity activity = (localQueryActivity) context;
                    activity.dismissPopupWindow(); // 关闭对应的PopupWindow
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView content;
    }

    public void updateData(List<String> newData) {
        this.dataList.clear();
        this.dataList.addAll(newData);
        notifyDataSetChanged();
    }

}