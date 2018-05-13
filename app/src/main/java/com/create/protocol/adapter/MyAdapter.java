package com.create.protocol.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.create.protocol.ProtocolActivity;
import com.create.protocol.R;
import com.create.protocol.model.Info;

import java.util.List;

/**
 * Created by jsntnjzb on 2018/4/9.
 */

public class MyAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Info> list;
    private OnShowItemClickListener onShowItemClickListener;

    public MyAdapter(List<Info> list, Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void updateListView(List<Info> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item, null);
            holder.name = convertView.findViewById(R.id.listview_tv);
            holder.time = convertView.findViewById(R.id.listview_time);
            holder.cb = convertView.findViewById(R.id.listview_select_cb);
            holder.arrow = convertView.findViewById(R.id.bid_btn_arrow);
            holder.tvProtocol = convertView.findViewById(R.id.tv_protocol);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Info info = list.get(position);
        // 是否是多选状态
        if (info.isShow()) {
            holder.cb.setVisibility(View.VISIBLE);
            holder.arrow.setVisibility(View.GONE);
        } else {
            holder.cb.setVisibility(View.GONE);
            holder.arrow.setVisibility(View.VISIBLE);
        }

        holder.name.setText(Html.fromHtml(context.getString(R.string.people_format, info.getStatus())));
        holder.time.setText(Html.fromHtml(context.getString(R.string.date_format, info.getDate())));
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    info.setChecked(true);
                } else {
                    info.setChecked(false);
                }
                // 回调方法，将Item加入已选
                onShowItemClickListener.onShowItemClick(info);
            }
        });
        // 必须放在监听后面
        holder.cb.setChecked(info.isChecked());
        holder.tvProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.getProtocolBitmap() != null ) {
                    Intent intent = new Intent(context, ProtocolActivity.class);
                    intent.putExtra("id", info.getId());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, position + "未保存协议", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView time;
        CheckBox cb;
        ImageView arrow;
        TextView tvProtocol;
    }

    public interface OnShowItemClickListener {
        void onShowItemClick(Info bean);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}
