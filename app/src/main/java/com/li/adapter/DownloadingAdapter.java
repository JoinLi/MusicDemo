package com.li.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.li.R;
import com.li.bean.SongBean;

import java.util.List;

/**
 * 下载列表适配器
 *
 * @author Administrator
 */
public class DownloadingAdapter extends BaseAdapter {
    List<SongBean> list;
    Context context;

    public DownloadingAdapter(List<SongBean> list, Context context) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        ViewHolder holder = null;
        if (arg1 == null) {
            holder = new ViewHolder();
            arg1 = LayoutInflater.from(context).inflate(R.layout.item_downloading_list, null);
            holder.item = (TextView) arg1.findViewById(R.id.item_down);
            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }
        holder.item.setText(list.get(arg0).getSongTypeName());

        return arg1;
    }

    class ViewHolder {
        TextView item;
    }
}
