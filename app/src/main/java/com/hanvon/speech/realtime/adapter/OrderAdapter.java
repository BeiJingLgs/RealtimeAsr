package com.hanvon.speech.realtime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.full.util.TimeUtil;
import com.hanvon.speech.realtime.bean.Result.Order;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by guhongbo on 2019/11/21.
 */

public class OrderAdapter extends BaseAdapter {

    List<Order> cateList;
    private Context context;
    private boolean mShowCheck;
    private HashMap<String, Boolean> mSelectStates ;


    public OrderAdapter(List<Order> litms, Context context) {
        super();
        cateList = litms;
        this.context = context;
        mSelectStates = new HashMap<>();
    }

    public void setmShowCheck(boolean mShowCheck, int po) {
        this.mShowCheck = mShowCheck;
        //mSelectStates.put(String.valueOf(cateList.get(po).getCreatemillis()), true);
    }

    public void setmCheckGone() {
        this.mShowCheck = false;
        mSelectStates.clear();
        notifyDataSetChanged();
    }

    public HashMap<String, Boolean> getmSelectStates() {
        return mSelectStates;
    }
    public boolean ismShowCheck() {
        return mShowCheck;
    }

    public void checkClear() {
        mSelectStates.clear();
    }

    public void addCheck(String name) {
        Set<String> mNameSet = new HashSet();;
        mNameSet = mSelectStates.keySet();
        if (mNameSet.contains(name)) {
            mSelectStates.remove(name);
        } else {
            mSelectStates.put(name, true);
        }
    }


    @Override
    public int getCount() {
        return cateList.size();
    }

    @Override
    public Object getItem(int position) {
        return cateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.context).inflate(R.layout.orderitem, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.fileName);
            viewHolder.content = (TextView) convertView.findViewById(R.id.fileContent);
            viewHolder.time = (TextView) convertView.findViewById(R.id.fileModify);
            viewHolder.checkbox = convertView.findViewById(R.id.checkbox);
            viewHolder.states = convertView.findViewById(R.id.states);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(cateList.get(position).getPackName());
        viewHolder.content.setText(cateList.get(position).getPayChannelName() + ": " + TimeUtil.centToyuan(cateList.get(position).getAmount()));
        viewHolder.content.setVisibility(View.VISIBLE);
        viewHolder.states.setVisibility(View.VISIBLE);

        viewHolder.time.setText(cateList.get(position).getUpdateTime());

        if (cateList.get(position).getState() == 1 || cateList.get(position).getState() == 2) {
            viewHolder.states.setText("已完成");
        } else {
            viewHolder.states.setText("未完成");
        }
        if (mShowCheck) {
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkbox.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        public TextView title;
        public TextView content;
        public TextView time;
        public TextView states;
        public CheckBox checkbox;
    }
}
