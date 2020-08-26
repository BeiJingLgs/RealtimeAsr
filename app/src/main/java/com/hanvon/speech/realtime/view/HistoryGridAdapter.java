package com.hanvon.speech.realtime.view;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asr.ai.speech.realtime.R;

public class HistoryGridAdapter extends BaseAdapter {

	List<String> cateList;
	private Context context;

	public HistoryGridAdapter(List<String> litms, Context context) {
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
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(this.context).inflate(R.layout.historyword_item, arg2, false);
			viewHolder.hotwordTv = (TextView) convertView.findViewById(R.id.hotword_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.hotwordTv.setText(cateList.get(arg0));

		return convertView;
	}

	static class ViewHolder {
		public TextView hotwordTv;

	}

}