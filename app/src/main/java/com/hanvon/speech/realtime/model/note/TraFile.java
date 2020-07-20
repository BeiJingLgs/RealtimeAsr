package com.hanvon.speech.realtime.model.note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;

public class TraFile {
	private static final String TAG = "TraFile";
	/**文件名称*/
	private String name = null;
	/** 便笺文件夹的根路径*/
	private String filepath = null;	 

	/**上次打开便笺时退出是在第几页*/
	private int lastIndex = 0;  
	private int width = 0;  // 宽
	private int height = 0; // 高
	
	/** 页面的相关信息*/
	public ArrayList<TraPage> pages = null;
	
	/** 便笺页是否被初始化*/
	private boolean isPageInit = false;

	public int getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isPageInit() {
		return isPageInit;
	}

	public void setPageInit(boolean isPageInit) {
		this.isPageInit = isPageInit;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
		if (!filepath.endsWith("/")){
			this.filepath += "/";
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getCount() {
		if (pages == null){
			return 0;
		}
		return pages.size();
	}


	/**
	 * 在最后添加一个page页
	 * @param page page页的信息
	 * @return 返回是否成功
	 */
	public boolean addPage(TraPage page){
		if (page == null){
			return false;
		}
		if (pages == null){
			pages = new ArrayList<TraPage>();
		}
		pages.add(page);
		return true;
	}
	
	/**
	 * 在某个位置插入一页
	 * @param index 要插入的位置
	 * @param page page页的信息
	 * @return 返回是否成功
	 */
	public boolean addPage(int index, TraPage page){
		if (page == null){
			return false;
		}
		if (pages == null){
			pages = new ArrayList<TraPage>();
		}
		pages.add(index, page);
		return true;
	}
	/**
	 * 根据指定索引返回page页的对象
	 * @param index 索引
	 * @return page页的对象
	 */
	public TraPage getPage(int index){
		if (pages== null){
			return null;
		}
		if (pages.size() <= index){
			return null;
		}
		return (TraPage)(pages.get(index));
	}
	
	/**
	 * 读取一个便笺文件的基本信息，不包括便笺页
	 * @param onlyReadInfo 是否只读取信息，不读取便签页
	 * @param path 便笺文件的全路径
	 * @param context
	 * @return 返回便笺文件的结构，其中pages = null
	 */
	public static TraFile readTraFile(boolean onlyReadInfo, String path, Context context){
		File file = new File(path);
		return readTraFile(onlyReadInfo, file, context);
	}
	
	/**
	 * 读取一个便笺文件的基本信息，不包括便笺页
	 * @param onlyReadInfo 是否只读取信息，不读取便签页
	 * @param file 便笺文件的文件对象
	 * @param context
	 * @return 返回便笺文件的结构，其中pages = null
	 */
	public static TraFile readTraFile(boolean onlyReadInfo, File file, Context context){
		// 文件不存在或不是便笺后缀结尾则直接返回
		if (file == null ||!file.exists() || !file.getName().toLowerCase().endsWith(NoteBaseData.NOTE_SUFFIX)){
			return null;
		}

		FileInputStream stream = null;
		TraFile tra = null;
		
		try{
			stream = new FileInputStream(file);
			// 读取文件版本信息
			byte[] info = new byte[NoteBaseData.VERSION_LENGTH];
			stream.read(info, 0, NoteBaseData.VERSION_LENGTH);

			tra = new TraFile();
			// 文件名，文件路径
			tra.setName(file.getName().substring(0, file.getName().lastIndexOf('.'))); // 去掉文件后缀
			tra.setFilepath(file.getPath().substring(0, file.getPath().lastIndexOf('/')+1)); // 去掉文件名称
			// 读取便笺文件上次打开的位置
			stream.read(info, 0, 4);
			tra.setLastIndex(NoteBaseData.byteToInt(info, true));
			
			// 便笺文件的宽
			stream.read(info, 0, 4);
			tra.setWidth(NoteBaseData.byteToInt(info, true));
			
			// 便笺文件的高
			stream.read(info, 0, 4);
			tra.setHeight(NoteBaseData.byteToInt(info, true));
			
			tra.setPageInit(false);
			if (!onlyReadInfo){
				// 读取记事的页数
				stream.read(info, 0, 4);
				int pageCount = (NoteBaseData.byteToInt(info, true));

				tra.setPageInit(true);
				tra.pages = new ArrayList<TraPage>();
				
				for (int i = 0 ; i < pageCount; i++){
					/*TraPage page = new TraPage();
					// 修改时间
					stream.read(info, 0, 8);
					long highInt = (NoteBaseData.byteToLong(info, true));
					page.setModifyTime(highInt);*/
					TraPage page = TraPage.getCurPage(stream);
					tra.pages.add(page);
				}
			}
			info = null;
			stream.close();
			stream = null;
			
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return tra;
	}

	/**
	 * 生成缺省的名字
	 * @param time 字符串格式的时间，如20150907
	 * @return 返回生成的名字readTraNote
	 */
	public static String generateShowName(String time) {
		String str = null; 
		SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
		long nTime = 0;
		try {
			Date date = fm.parse(time);
			fm = new SimpleDateFormat("yyyy-MM-dd");
			str = fm.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 获取当前便笺文件的全路径
	 * @return String
	 */
	public String getFilePathName() {
		// TODO Auto-generated method stub
		return filepath + name + NoteBaseData.NOTE_SUFFIX;
	}
	/**
	 * 仅修改当前文件的上次打开页的索引值
	 * @return 是否成功
	 */
	public boolean saveLastIndex() {
		// TODO Auto-generated method stub
		String path = getFilePathName();
		try {
			RandomAccessFile file = new RandomAccessFile(path, "rwd");
			file.seek(0);
			file.seek(NoteBaseData.VERSION_LENGTH);
			
			// 写上次打开的页码位置
			// 判断最近阅读位置的正确性
			if(getLastIndex() >= pages.size()){
				setLastIndex(pages.size() - 1);
			}
			byte[] info = new byte[4];
			NoteBaseData.intToByte(info, 0, getLastIndex(), true);
			file.write(info, 0, 4);
			
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 清空当前的数据
	 */
	public void clear(){
		name = null;   // 名称,不带后缀
		filepath = null;	  // 路径 ，不带文件名
	    setPageInit(false);
		if (pages != null) {
			for (int i = pages.size() - 1; i >= 0; i--) {
				pages.remove(i);
			}
			pages.clear();
			pages = null;
		}
	}
	
	/**
	 * 把当前的便笺保存为一个文件，文件如果不存在则创建，如果存在则重写
	 * @param pathname 要保存的文件路径
	 * @return 返回是否成功
	 */
	public boolean saveTraNote(String pathname){
		if(pathname == null){
			return false;
		}
		// 检查文件路径是否存在，不存在则创建
		int local = pathname.lastIndexOf("/");
		if (local == -1){
			return false;
		}
		String path = pathname.substring(0, local);
		File file = new File(path);
		if (!file.exists()){
			file.mkdirs();
		}

		// 检查文件后缀名是否合法
		if (!pathname.toLowerCase().endsWith(NoteBaseData.NOTE_SUFFIX)){
			pathname += NoteBaseData.NOTE_SUFFIX;
		}
		
		file = new File(pathname);
		// 如果文件存在则先删除
		if (file.exists()){
			file.delete();
		}
		
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
			// 便笺文件的版本号
			byte[] head = NoteBaseData.VERSION_NAME.getBytes(NoteBaseData.CHARSET);
			if (head.length != NoteBaseData.VERSION_LENGTH){
				return false;
			}
			stream.write(head, 0, head.length);
			
			// 便笺文件上次打开的位置
			if(pages != null && getLastIndex() >= pages.size()){
				setLastIndex(pages.size() - 1);
			}
			byte[] info = new byte[8];
			NoteBaseData.intToByte(info, 0, getLastIndex(), true);
			stream.write(info, 0, 4);
			
			// 便笺文件的宽
			NoteBaseData.intToByte(info, 0, getWidth(), true);
			stream.write(info, 0, 4);
			
			// 便笺文件的高
			NoteBaseData.intToByte(info, 0, getHeight(), true);
			stream.write(info, 0, 4);

			if (pages != null){			
				// 便笺的页数
				NoteBaseData.intToByte(info, 0, pages.size(), true);
				stream.write(info, 0, 4);
				
				for (int i = 0; i < pages.size(); i++){
					TraPage page = pages.get(i);
					page.saveToFile(stream);

/*					// 写入修改时间
					long time = page.getModifyTime();
					NoteBaseData.longToByte(info, 0, time, true);
					stream.write(info, 0, 8);*/
				}
			}
			stream.flush();
			stream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 生成一个新的便笺文件，包括便笺的路径，tra文件，缩略图路径
	 * @param rootPath 便笺文件夹所在的根目录
	 * @param name 便笺的名称
	 * @return 
	 */
	public boolean createNewTraFile(String rootPath, String name){
		if (rootPath == null || name == null){
			return false;
		}
		this.name = name;
		this.filepath = rootPath + "/";

		// 文件夹不存在则创建文件夹
		File file = new File(filepath);
		if (!file.exists()){
			file.mkdirs();
		}

		// 创建一个tra文件
		return saveTraNote(getFilePathName());
	}

	/**
	 * 删除指定页
	 * @param index 页的索引
	 * @return 返回是否成功
	 */
	public boolean deletePage(int index){
		if (pages == null && pages.size() <= index){
			return false;
		}
		pages.remove(index);
		return true;

	}

}
