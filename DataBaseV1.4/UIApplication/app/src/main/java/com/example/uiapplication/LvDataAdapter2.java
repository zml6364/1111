package com.example.uiapplication;

import android.content.Context;
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
    private EditText editText;

    public LvDataAdapter2(Context context, List<String> dataList, EditText editText) {
        this.context = context;
        this.dataList = dataList;
        this.editText = editText;
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(contentText);
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
}