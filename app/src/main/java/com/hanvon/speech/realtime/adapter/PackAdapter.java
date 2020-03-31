package com.hanvon.speech.realtime.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.full.util.TimeUtil;
import com.hanvon.speech.realtime.bean.Result.Order;
import com.hanvon.speech.realtime.bean.Result.PackBean;
import com.hanvon.speech.realtime.util.MethodUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by guhongbo on 2019/11/21.
 */

public class PackAdapter extends BaseAdapter {

    List<PackBean> cateList;
    private Context context;
    private boolean mShowCheck;
    private HashMap<String, Boolean> mSelectStates ;


    public PackAdapter(List<PackBean> litms, Context context) {
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
            convertView = LayoutInflater.from(this.context).inflate(R.layout.packitem, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.fileName);
            viewHolder.content = (TextView) convertView.findViewById(R.id.fileContent);
            viewHolder.time = (TextView) convertView.findViewById(R.id.fileModify);
            viewHolder.states = convertView.findViewById(R.id.states);
            viewHolder.progressBar = convertView.findViewById(R.id.progesss);
            viewHolder.progressTv = convertView.findViewById(R.id.progesss_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(cateList.get(position).getPackName());
        viewHolder.content.setText(context.getString(R.string.lastTime) + TimeUtil.secondToTime(cateList.get(position).getRemainDuration()));

        viewHolder.time.setText(context.getString(R.string.validTime) + cateList.get(position).getEndTime());

        viewHolder.progressTv.setText(context.getString(R.string.used) + MethodUtils.calculatorProgress(cateList.get(position).getRemainDuration(), cateList.get(position).getDuration()) + "%" );
        viewHolder.progressBar.setProgress(MethodUtils.calculatorProgress(cateList.get(position).getRemainDuration(), cateList.get(position).getDuration()));
        viewHolder.states.setVisibility(View.VISIBLE);
        if(TextUtils.equals(cateList.get(position).getSource(), "Device")) {
            viewHolder.states.setText(context.getString(R.string.gift_package));
        } else {
            viewHolder.states.setText(context.getString(R.string.shop_package));
        }



        return convertView;
    }

    static class ViewHolder {
        public TextView title;
        public TextView content;
        public TextView time;
        public TextView states;
        public TextView progressTv;
        public ProgressBar progressBar;
    }
}
