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
import com.hanvon.speech.realtime.bean.speechBean.AsrEditSentence;
import com.hanvon.speech.realtime.util.CommonUtils;
import com.hanvon.speech.realtime.util.LogUtils;

import java.util.List;

/**
 * Created by guhongbo on 2019/11/25.
 */

public class SequenceAdapter extends BaseAdapter {

    private final static String TAG = "SequenceAdapter";
    List<AsrEditSentence> cateList;
    private Context context;
    OnCall onCall;
    public SequenceAdapter(List<AsrEditSentence> litms, Context context) {
        super();
        cateList = litms;
        this.context = context;
    }

    public void setOnCall(OnCall onCall) {
        this.onCall = onCall;
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

        viewHolder.timeTv.setText(TimeUtil.convertMillions2Time(cateList.get(position).getBg()) + " - "
                + TimeUtil.convertMillions2Time(cateList.get(position).getEd()));
        viewHolder.sentenceEd.setText(cateList.get(position).getContent());


        /*
         * 获取到焦点的监听
         */
        viewHolder.sentenceEd.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (CommonUtils.isNoFastClick()) {
                        onCall.setOnEditClick(position);
                    }
                }
            }
        });

        /*viewHolder.sentenceEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cateList.get(position).setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        return convertView;
    }

    public List<AsrEditSentence>getCateList() {
        return cateList;
    }



    static class ViewHolder {
        public TextView timeTv;
        public EditText sentenceEd;
    }

    public interface OnCall {
        public void setOnEditClick(int position);
    }

}
