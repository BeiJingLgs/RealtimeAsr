package com.hanvon.speech.realtime.model.note;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TraPage {
	private String thumbPath;	// 缩略图位置
	public ArrayList<Trace> traces = null ; // 笔迹数据
	//public ArrayList<String> records = null;	// 录音文件

	public String getThumbPath() {
		return thumbPath;
	}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}
	/**
	 * 从文件流中获取当前page页
	 * @param stream 文件流
	 * @return page信息，null表示失败
	 */
	public static TraPage getCurPage(FileInputStream stream) {
		// TODO Auto-generated method stub
		if (stream == null) {
			return null;
		}

		TraPage page = new TraPage();
		try {
			byte[] info = new byte[4];

			// 笔迹的条数
			stream.read(info, 0, 4);
			int count = NoteBaseData.byteToInt(info, true);
			if (count > 0){
				page.traces = new ArrayList<Trace>();
			}
			// 笔迹
			for(int j = 0; j < count; j++){
				Trace trace = Trace.getCurTrace(stream);
				page.traces.add(trace);
			}

			info = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return page;
	}

	/**
	 * 把笔迹保存到文件中
	 * @param stream 要保存的文件流
	 * @return 返回是否成功
	 */
	public boolean saveToFile(FileOutputStream stream) {
		// TODO Auto-generated method stub
		if (stream == null){
			return false;
		}
		byte[] info = new byte[4];
		try {
			int nTraecCount = 0;
			if(traces != null)
			{
				nTraecCount = traces.size();
			}
			// 笔迹的条数
			NoteBaseData.intToByte(info, 0, nTraecCount, true);
			stream.write(info, 0, 4);

			// 笔迹的内容
			for (int i = 0; i < nTraecCount; i++){
				traces.get(i).saveToFile(stream);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<Trace> getTraces() {
		return traces;
	}

	/**
	 * 设置当前的笔迹集合，traces里的数据不能被clear
	 * @param traces 笔迹的集合
	 */
	protected void setTraces(ArrayList<Trace> traces) {
		this.traces = traces;
	}

	/**
	 * 设置当前的笔迹的集合，trace里的数据和次结构中的没有关系
	 * @param inTraces 笔迹的集合
	 */
	public void copyTraces(ArrayList<Trace> inTraces) {
		if (inTraces == null){
			this.traces = null;
			return;
		}
		if (this.traces == null){
			this.traces = new ArrayList<Trace>();
		}else{
			this.traces.clear();
		}

		for(Trace trace:inTraces){
			Trace newTrace = trace.deepClone();
			this.traces.add(newTrace);
		}
	}

	/**
	 * 清空当前的数据
	 */
	public void clear() {
		// TODO Auto-generated method stub
		if (traces != null){
			for(int i = traces.size() - 1; i >= 0; i--){
				traces.get(i).clear();
				traces.remove(i);
			}
			traces.clear();
		}
	}



}
