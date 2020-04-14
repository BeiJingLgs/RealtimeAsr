package com.hanvon.speech.realtime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.ui.IatActivity;

public class JumpAdapter extends BaseAdapter {
	
	protected Context context = null;
	protected int nCurIndex = 0;
	protected int count = 0;
	protected int curPage = 0;
	protected int BLOCK_SIZE = 10;
	
	public JumpAdapter(Context context, int curIndex, int count) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.nCurIndex = curIndex;
		this.count = count;
		this.curPage = curIndex/BLOCK_SIZE;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int itemCount = BLOCK_SIZE;
		if (count - curPage * BLOCK_SIZE < BLOCK_SIZE){
			itemCount = count - curPage * BLOCK_SIZE;
		}
		return itemCount;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return curPage * BLOCK_SIZE + pos;
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return curPage * BLOCK_SIZE + pos;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (contentView == null){
			contentView = LayoutInflater.from(context).inflate(R.layout.page_jump_list_item, null);
		}

		int index = curPage*BLOCK_SIZE + position;
		if (index >= count){
			return null;
		}
		
		TextView text = (TextView)contentView.findViewById(R.id.textPage);
		ImageView check = (ImageView)contentView.findViewById(R.id.checkPage);
		
		if (index == nCurIndex){
			check.setVisibility(View.VISIBLE);
		}else{
			check.setVisibility(View.INVISIBLE);
		}
		text.setText(String.format(context.getString(R.string.page_jump_info), index +1));
		
		return contentView;
	}
	
	/**
	 * 更新当前列表
	 * @param curPage
	 */
	public void updatePage(int curPage){
		this.curPage = curPage;
		notifyDataSetChanged();
	}

}
