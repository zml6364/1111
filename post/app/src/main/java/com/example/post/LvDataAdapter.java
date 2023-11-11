package com.example.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 自定义用户数据适配器类
 */
public class LvDataAdapter extends BaseAdapter {
    private Context context;    // 上下文信息
    private List<Userinfo> weldingList;    // 用户信息数据集合

    public LvDataAdapter(Context context, List<Userinfo> weldingList) {
        this.context = context;
        this.weldingList = weldingList;
    }

    public void setweldingList(List<Userinfo> weldingList) {
        this.weldingList = weldingList;
    }

    @Override
    public int getCount() {
        return weldingList.size();
    }

    @Override
    public Object getItem(int position) {
        return weldingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.user_list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.tv_pname = convertView.findViewById(R.id.tv_pname);
            viewHolder.tv_pvalue = convertView.findViewById(R.id.tv_pvalue);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 这里进行数据填充
        Userinfo item = weldingList.get(position);
        viewHolder.tv_pname.setText(item.getParamName());
        viewHolder.tv_pvalue.setText(item.getParamValue());

        return convertView;
    }

    // 自定义内部类
    private class ViewHolder{
        private TextView tv_pname, tv_pvalue;
    }
}
