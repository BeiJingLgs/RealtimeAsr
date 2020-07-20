package com.hanvon.speech.realtime.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;


import com.asr.ai.speech.realtime.R;
import com.asr.ai.speech.realtime.full.download.Result;
import com.asr.ai.speech.realtime.full.util.TimeUtil;

import java.util.List;

/**
 * Created by guhongbo on 2019/11/25.
 */

public class SequenceAdapter extends BaseAdapter {

    List<Result> cateList;
    private Context context;

    public SequenceAdapter(List<Result> litms, Context context) {
        super();
        cateList = litms;
        this.context = context;

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
        SequenceAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new SequenceAdapter.ViewHolder();
            convertView = LayoutInflater.from(this.context).inflate(R.layout.edit_item, parent, false);
            viewHolder.timeTv = (TextView)convertView.findViewById(R.id.sentence_time);
            viewHolder.sentenceEd = (EditText) convertView.findViewById(R.id.sentence_edit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SequenceAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.timeTv.setText(TimeUtil.convertMillions2Time(cateList.get(position).getStartTime()) + " - "
                + TimeUtil.convertMillions2Time(cateList.get(position).getEndTime()));
        viewHolder.sentenceEd.setText(cateList.get(position).getResult());
        viewHolder.sentenceEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cateList.get(position).setResult(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return convertView;
    }

    public List<Result>getCateList() {
        return cateList;
    }

    static class ViewHolder {
        public TextView timeTv;
        public EditText sentenceEd;

    }
}
