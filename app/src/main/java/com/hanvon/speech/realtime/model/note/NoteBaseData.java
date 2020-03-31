package com.hanvon.speech.realtime.model.note;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteBaseData {

	/** 便笺文件的版本号*/
	public static final String VERSION_NAME = "hvlatn01";
	public static final int VERSION_LENGTH = VERSION_NAME.length();

	/**单页笔记文件的版本号*/
	public static final String DATA_VERSION = "hvlatp01";
	
	/** 便笺文件的后缀*/
	public static final String NOTE_SUFFIX = ".tra";

	public static final String CHARSET = "GBK";

	/** 当前的记事文件，存储相关数据*/
	public static TraFile gTraFile;
	public static boolean gIsTraNoteModified = false;

	/** 文件路径*/
	public static final String KEY_FILE_PATH = "file_path";

	/** 页面的操作类型*/
	public static final String KEY_PAGE_TYPE = "page_type";	
	/** 新建一页*/
	public static final int PAGE_TYPE_NEW = 1;	
	/** 打开一页*/
	public static final int PAGE_TYPE_MODIFY = 2;
	
	
	/** 分类的操作类型*/
	public static final String KEY_CLASS_TYPE = "class_type";	
	/** 新建一页*/
	public static final int CLASS_TYPE_NEW = 1;	
	/** 打开一页*/
	public static final int CLASS_TYPE_MODIFY = 2;
	
	/** 新建便笺文件的缺省名称*/
	public static final String KEY_NEW_FILE_NAME = "newfile_name";
	
	/** 页面的序号，从1开始表示便笺页，0表示封面*/
	public static final String KEY_PAGE_INDEX = "page_index";

	/**
	 * Function byteToInt
	 		作用：把一个byte[4]的数组转换成一个int
	 * @param value
	 * 		value[4]为一个byte的数组
	 * @param isLittleEndian
	 * 		是否是 Little Endian, Little Endian: 0x12345678,内存中字节顺序为78 56 34 12
	 * 		Big Endian: 0x12345678,内存中的字节顺序为12 34 56 78
	 * @return int
	 * 		表示转换后的int值
	 */
	public static int byteToInt(byte[] value, boolean isLittleEndian) {
		// TODO Auto-generated method stub
		if (value.length < 4)
		{
			return -1;
		}
		int re = 0;
		if (isLittleEndian) {
			re = (((int) value[3]) << 24) & 0xFF000000;
			re |= (((int) value[2]) << 16) & 0x00FF0000;
			re |= (((int) value[1]) << 8) & 0x0000FF00;
			re |= ((int) value[0]) & 0x000000FF;
		} else {
			re = (((int) value[0]) << 24) & 0xFF000000;
			re |= (((int) value[1]) << 16) & 0x00FF0000;
			re |= (((int) value[2]) << 8) & 0x0000FF00;
			re |= ((int) value[3]) & 0x000000FF;
		}
		return re;
	}
	

	/**
	 * 把int转换成byte数组
	 * @param value 要转换的int值
	 * @param isLittleEndian 
	 * @return byte[] 返回byte数组
	 */
	public static byte[] intToByte(int value, boolean isLittleEndian)
	{
		byte[] data = new byte[4];
		if (isLittleEndian) {
			data[3] = (byte) ((value & 0xFF000000) >> 24);
			data[2] = (byte) ((value & 0x00FF0000) >> 16);
			data[1] = (byte) ((value & 0x0000FF00) >> 8);
			data[0] = (byte) (value & 0x000000FF);
		} else {
			data[0] = (byte) ((value & 0xFF000000) >> 24);
			data[1] = (byte) ((value & 0x00FF0000) >> 16);
			data[2] = (byte) ((value & 0x0000FF00) >> 8);
			data[3] = (byte) (value & 0x000000FF);
		}
		return data;
	}

	/**
	 * Function byteToInt
	 		作用：把一个byte的数组从某个位置开始的4个byte转换成一个int
	 * @param value
	 * 		value为一个byte的数组
	 * @param offset
	 * 		offset为byte[]数组中的偏移量
	 * @param isLittleEndian
	 * 		是否是 Little Endian, Little Endian: 0x12345678,内存中字节顺序为78 56 34 12
	 * 		Big Endian: 0x12345678,内存中的字节顺序为12 34 56 78
	 * @return int
	 * 		表示转换后的int值
	 */
	public static int byteToInt(byte[] value, int offset, boolean isLittleEndian){
		if (value.length - offset < 4)
		{
			return -1;
		}
		int re = 0;
		if (isLittleEndian) {
			re = (((int) value[3 + offset]) << 24) & 0xFF000000;
			re |= (((int) value[2 + offset]) << 16) & 0x00FF0000;
			re |= (((int) value[1 + offset]) << 8) & 0x0000FF00;
			re |= ((int) value[0 + offset]) & 0x000000FF;
		} else {
			re = (((int) value[0 + offset]) << 24) & 0xFF000000;
			re |= (((int) value[1 + offset]) << 16) & 0x00FF0000;
			re |= (((int) value[2 + offset]) << 8) & 0x0000FF00;
			re |= ((int) value[3 + offset]) & 0x000000FF;
		}
		return re;
	}
	

	/**
	 * 把int转换成指定byte数组中的从某个位置开始的4个byte的值
	 * @param result 指定存放转换结果的byte数组
	 * @param offset byte数组的偏移量
	 * @param value 要转换的int值
	 * @param isLittleEndian 是否是Little Endian
	 * @return int
	 */
	public static boolean intToByte(byte[] result, int offset, int value, boolean isLittleEndian)
	{
		if (result.length - offset < 4) {
			return false;
		}
		if (isLittleEndian) {
			result[3 + offset] = (byte) ((value & 0xFF000000) >> 24);
			result[2 + offset] = (byte) ((value & 0x00FF0000) >> 16);
			result[1 + offset] = (byte) ((value & 0x0000FF00) >> 8);
			result[0 + offset] = (byte) (value & 0x000000FF);
		} else {
			result[0 + offset] = (byte) ((value & 0xFF000000) >> 24);
			result[1 + offset] = (byte) ((value & 0x00FF0000) >> 16);
			result[2 + offset] = (byte) ((value & 0x0000FF00) >> 8);
			result[3 + offset] = (byte) (value & 0x000000FF);
		}
		return true;
	}
	
	/**
	 * 判断名称是否合法
	 * @param name 文件名称
	 * @return 返回是否合法
	 */
	public static boolean isFileNameValidate(String name){
		if (name == null || name.length() == 0){
			return false;
		}
		// 不能使用加号、减号或者"."作为普通文件的第一个字符
		if (name.startsWith(".")||name.startsWith("+")||name.startsWith("-")){
			return false;
		}
		
		// 不能包含以下特殊字符
		for (int i = 0; i < name.length(); i++) {// / \ | * ? <>:" 空格等
			if (name.charAt(i) == '/' || name.charAt(i) == '\\'
					|| name.charAt(i) == '|' || name.charAt(i) == '*'
					|| name.charAt(i) == '$' || name.charAt(i) == '?'
					|| name.charAt(i) == '<' || name.charAt(i) == '>'
					|| name.charAt(i) == ':' || name.charAt(i) == '"'
					/*|| name.charAt(i) == ' ' */|| name.charAt(i) == '@'
					|| name.charAt(i) == '#' || name.charAt(i) == '%'
					|| name.charAt(i) == '^' || name.charAt(i) == '&'
					/*|| name.charAt(i) == '(' || name.charAt(i) == ')'*/
					/*|| name.charAt(i) == '[' || name.charAt(i) == ']'*/) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断当前文件是否存在
	 * @param pathname 文件全路径
	 * @return 是否存在
	 */
	public static boolean isFileExist(String pathname) {
		// TODO Auto-generated method stub
		File file = new File(pathname);
		return file.exists();
	}
	
	/**
	 * 得到新建便笺文件的默认名称
	 * @param path 文件所在的文件夹,必须以"/"结尾
	 * @param nameDefault 文件的缺省名称
	 * @return 返回获取的名称,不带后缀名
	 */
	public static String getTraNoteDefaultName(String path, String nameDefault){
		// 得到当前时间
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");   
		String name = formatter.format(date);
		name += nameDefault;
		
		String fullName = name;// + NOTE_SUFFIX;
		for (int i = 1;; i++) {
			if (isFileExist(path + fullName + NOTE_SUFFIX)) {
				fullName = name + "(" + i + ")";
			} else {
				break;
			}
		}
		return fullName;
	}
	
	/**
	 * 得到新建文件夹的默认名称
	 * @param path 文件夹所在的目录
	 * @return 返回获取的名称
	 */
	public static String getDirDefaultName(String path, String nameDefault){
		String fullName = nameDefault;
		for (int i = 1;; i++) {
			if (isFileExist(path + fullName)) {
				fullName = nameDefault + "(" + i + ")";
			} else {
				break;
			}
		}
		return fullName;
	}


	/**
	 * Function byteToInt
	 		作用：把一个byte[8]的数组转换成一个long
	 * @param value
	 * 		value[8]为一个byte的数组
	 * @param isLittleEndian
	 * 		是否是 Little Endian, Little Endian: 0x1234567890ABCDEF,内存中字节顺序为 78 56 34 12 EF CD AB 90
	 * 		Big Endian: 0x1234567890ABCDEF,内存中的字节顺序为12 34 56 78 90 AB CD EF
	 * @return int
	 * 		表示转换后的int值
	 */
	public static long byteToLong(byte[] value, boolean isLittleEndian) {
		// TODO Auto-generated method stub
		if (value.length < 8)
		{
			return -1;
		}
		long re = 0;
		if (isLittleEndian) {
			re = (((long) value[3]) << 56) & 0xFF00000000000000L;
			re |= (((long) value[2]) << 48) & 0x00FF000000000000L;
			re |= (((long) value[1]) << 40) & 0x0000FF0000000000L;
			re |= (((long) value[0]) << 32) & 0x000000FF00000000L;
			re |= (((long) value[7]) << 24) & 0x00000000FF000000L;
			re |= (((int) value[6]) << 16) & 0x0000000000FF0000L;
			re |= (((int) value[5]) << 8) & 0x000000000000FF00L;
			re |= ((int) value[4]) & 0x00000000000000FFL;
		} else {
			re = (((long) value[0]) << 56) & 0xFF00000000000000L;
			re |= (((long) value[1]) << 48) & 0x00FF000000000000L;
			re |= (((long) value[2]) << 40) & 0x0000FF0000000000L;
			re |= (((long) value[3]) << 32) & 0x000000FF00000000L;
			re |= (((long) value[4]) << 24) & 0x00000000FF000000L;
			re |= (((int) value[5]) << 16) & 0x0000000000FF0000L;
			re |= (((int) value[6]) << 8) & 0x000000000000FF00L;
			re |= ((int) value[7]) & 0x00000000000000FFL;
		}
		return re;
	}
	

	/**
	 * 把long转换成指定byte数组中的从某个位置开始的8个byte的值
	 * @param result 指定存放转换结果的byte数组
	 * @param offset byte数组的偏移量
	 * @param value 要转换的long值
	 * @param isLittleEndian 是否是Little Endian
	 * @return 是否成功
	 */
	public static boolean longToByte(byte[] result, int offset, long value, boolean isLittleEndian)
	{
		if (result.length - offset < 8) {
			return false;
		}
		if (isLittleEndian) {
			result[3 + offset] = (byte) ((value & 0xFF00000000000000L) >> 56);
			result[2 + offset] = (byte) ((value & 0x00FF000000000000L) >> 48);
			result[1 + offset] = (byte) ((value & 0x0000FF0000000000L) >> 40);
			result[0 + offset] = (byte) ((value & 0x000000FF00000000L) >> 32);
			result[7 + offset] = (byte) ((value & 0x00000000FF000000L) >> 24);
			result[6 + offset] = (byte) ((value & 0x0000000000FF0000L) >> 16);
			result[5 + offset] = (byte) ((value & 0x000000000000FF00L) >> 8);
			result[4 + offset] = (byte) (value & 0x00000000000000FFL);
		} else {
			result[0 + offset] = (byte) ((value & 0xFF00000000000000L) >> 56);
			result[1 + offset] = (byte) ((value & 0x00FF000000000000L) >> 48);
			result[2 + offset] = (byte) ((value & 0x0000FF0000000000L) >> 40);
			result[3 + offset] = (byte) ((value & 0x000000FF00000000L) >> 32);
			result[4 + offset] = (byte) ((value & 0x00000000FF000000L) >> 24);
			result[5 + offset] = (byte) ((value & 0x0000000000FF0000L) >> 16);
			result[6 + offset] = (byte) ((value & 0x000000000000FF00L) >> 8);
			result[7 + offset] = (byte) (value & 0x00000000000000FFL);
		}
		return true;
	}
}
