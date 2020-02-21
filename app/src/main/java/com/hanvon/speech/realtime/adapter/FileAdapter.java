package com.hanvon.speech.realtime.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.util.hvFileCommonUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by guhongbo on 2019/11/21.
 */

public class FileAdapter extends BaseAdapter {

    List<FileBean> cateList;
    private Context context;
    private boolean mShowCheck;
    private HashMap<String, Boolean> mSelectStates ;


    public FileAdapter(List<FileBean> litms, Context context) {
        super();
        cateList = litms;
        this.context = context;
        mSelectStates = new HashMap<>();
    }

    public void setmShowCheck(boolean mShowCheck, int po) {
        this.mShowCheck = mShowCheck;
        mSelectStates.put(String.valueOf(cateList.get(po).getCreatemillis()), true);
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
            convertView = LayoutInflater.from(this.context).inflate(R.layout.fileitem, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.fileName);
            viewHolder.content = (TextView) convertView.findViewById(R.id.fileContent);
            viewHolder.time = (TextView) convertView.findViewById(R.id.fileModify);
            viewHolder.checkbox = convertView.findViewById(R.id.checkbox);
            viewHolder.sdImg = convertView.findViewById(R.id.ic_sd_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(cateList.get(position).getTitle());
        viewHolder.content.setText(cateList.get(position).getContent());

        viewHolder.time.setText(cateList.get(position).getCreatetime() +
        " " + context.getResources().getString(R.string.file_create) +
        " " + cateList.get(position).getModifytime() +
        " " + context.getResources().getString(R.string.file_modify));
        if (mShowCheck) {
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkbox.setVisibility(View.GONE);
        }

        //Log.e("TAG", "hvFileCommonUtils.hasSdcard(context): " + hvFileCommonUtils.hasSdcard(context));
        //Log.e("TAG", "TextUtils.equals(cateList.get(position).mSd, sd): " + TextUtils.equals(cateList.get(position).mSd, "sd"));
        if (hvFileCommonUtils.hasSdcard(context) && TextUtils.equals(cateList.get(position).mSd, "sd")) {
            viewHolder.sdImg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.sdImg.setVisibility(View.GONE);
        }

        boolean res = false;
        if (mSelectStates.containsKey(cateList.get(position).createmillis)) {
            res = true;
        } else
            res = false;

        viewHolder.checkbox.setChecked(res);
        return convertView;
    }

    static class ViewHolder {
        public TextView title;
        public TextView content;
        public TextView time;
        public ImageView sdImg;
        public CheckBox checkbox;
    }
}
