package com.hanvon.speech.realtime.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class hvFileCommonUtils {
	
	public static final int CREATE_FILE_FAILED = 0;
	public static final int CREATE_FILE_OK = 1;
	public static final int CREATE_FILE_NONEED = 2;
	
	
	public static boolean createDirIfNeed(Context context, String path){
		File file = new File(path);
		return createDirIfNeed(context, file);
	}
	// 注意：1.  notifySystemToScan不能随意调用，
			// 比如已创建好的目录再次调用notifySystemToScan,然后再里面创建文件，
			// 再调用notifySystemToScan,MTP里不会立即显示创建的文件
	// 2. 用户有可能正连着MTP，然后操作电纸书创建目录，此时如果不调用notifySystemToScan, MTP里不会有；
	      //如果调用了，鼠标右键刷新一下就出来了
	public static boolean createDirIfNeed(Context context, File file){
		boolean bCreateOK = false;
		if (!file.exists()){
			if (!file.mkdir()){
				return false;
			}
			bCreateOK = true;
		}
		if (bCreateOK)
			notifySystemToScan(context, file);
		return bCreateOK;
	}
	
	
    public static void WriteToFile(Context context, String path, String content){
        File file = new File(path);
        int ret = createFileIfNeed(context,file);
        if (ret == CREATE_FILE_FAILED){
        	return;
        }
        try {
        OutputStream outstream = new FileOutputStream(file);
        OutputStreamWriter out = new OutputStreamWriter(outstream);
        out.write(content);
        out.close();
        if (ret == CREATE_FILE_OK){
        	notifyFileUpdate(context, file);
        }
        } catch (IOException e) {
        	e.printStackTrace();
        	return;
        }
    }
    
	public static void saveBitmapToPngFile(Context context, Bitmap bitmap, String coverPath, String name){
			createDirIfNeed(context, coverPath);
			File file = new File(coverPath + name + ".png");
	        int ret = createFileIfNeed(context,file);
	        if (ret == CREATE_FILE_FAILED){
	        	return;
	        }
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 5, out)) {
					out.flush();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
			        if (ret == CREATE_FILE_OK){
			        	notifyFileUpdate(context, file);
			        }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	//删除文件或目录
	public static boolean recursiveDelete(Context context, String strPath) {
		File file = new File(strPath);
		return recursiveDelete(context,file);
	}

	public static boolean recursiveDelete(Context context, File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				if (!file.delete()) {
					return false;
				}else{
					notifySystemToScan(context, file);
				}
			}
			for (int x = 0; x < files.length; x++) {
				File childFile = files[x];
				recursiveDelete(context, childFile);
			}
		}
		if (file.exists()) {
			if(!file.delete()){
				return false;
			}else{
				notifySystemToScan(context, file);
			}
		}
		return true;
	}
	

	private static int createFileIfNeed(Context context, File file){
		boolean bCreated = false;
        if (!file.exists()){
        	try {
        		bCreated = file.createNewFile();
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 				return CREATE_FILE_FAILED; // 创建失败
 			}
        }
        if (!bCreated){
        	return CREATE_FILE_NONEED; // 2 是没有创建
        }
        return CREATE_FILE_OK; // 创建成功
	}
	
    public static String ReadFromFile(String path, boolean utf8){
        String strContent = "";
    		File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
 	   	if (!file.exists()){
 	   		return "NULL";
 	   	}
 	       if (file.isDirectory()){
 	       	return "";
 	       }
 	       else{
 	       	InputStream instream = null;
 		        try {
 			        instream = new FileInputStream(file);
 			        if (instream != null) {
 				        InputStreamReader inputreader = null;
 				        if (utf8){
 				        	inputreader = new InputStreamReader(instream, "utf-8");
 				        }else{
 				        	inputreader = new InputStreamReader(instream);
 				        }
 				        BufferedReader buffreader = new BufferedReader(inputreader);
 				        String line;
 				        while (( line = buffreader.readLine()) != null) {
 				        	strContent += line;
 				        }
 			        }
 		        }catch (FileNotFoundException e) {
 		        	e.printStackTrace();
 		        }catch (IOException e) {
 		        	e.printStackTrace();
 		        }
 		        if (instream != null){
 			        try {
 						instream.close();
 					} catch (IOException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 		        }
 		        return strContent;
 	       }
    }
    
	public static String ReadFromFileLine(String path) {
		String strContent = "";
		File file = new File(path);
		// 如果path是传递过来的参数，可以做一个非目录的判断
		if (!file.exists() || file.isDirectory()) {
			return "";
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					while ((line = buffreader.readLine()) != null) {
						strContent += line;
						strContent += "\n";
					}
					instream.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "";
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
			return strContent;
		}
	}


	
	
    public static void notifyDirUpdate(Context context, File file) {
        ContentResolver resolver =context.getContentResolver();
        Uri uri = Uri.parse("content://" + MediaStore.AUTHORITY + "/external/file");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.Files.FileColumns.PARENT, file.getParent());
        values.put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Files.FileColumns.TITLE, file.getName());
        values.put("format", "12289");
        values.put("storage_id", "65537");
        try {
            resolver.insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public static void notifyFileUpdate(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
    
    public static void notifySystemToScan(Context context, File file) {
        if(file.isDirectory()){
            notifyDirUpdate(context, file);
        }else{
            notifyFileUpdate(context, file);
        }
    }
    
  //判断SD卡是否存在
  	public static boolean hasUdisk(Context context) {
  		String status = getExtUdiskStorageState(context);
  		if (status.equals(Environment.MEDIA_MOUNTED)
  				|| status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
  			return true;
  		} else {
  			return false;
  		}
  	}
  	
  	//判断SD卡是否存在
  	public static boolean hasSdcard(Context context) {
  		String status = getExtSdStorageState(context);
  		if (status.equals(Environment.MEDIA_MOUNTED) 
  				|| status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
  			if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
  				Log.v("hvFileUtils", "haha8 hasSdcard MEDIA_MOUNTED_READ_ONLY");
  			}
  			return true;
  		} else {
  			return false;
  		}
  	}
  	
  	public static boolean isSdChecking(Context context){
  		String status = getExtSdStorageState(context);
  		if (status.equals(Environment.MEDIA_CHECKING)) {
  			return true;
  		} else {
  			return false;
  		}
  	}
  	
  	public static String getExtUdiskStorageState(Context context){
  		String path = getUDiskPath(context);
  		if (TextUtils.isEmpty(path)){
  			return Environment.MEDIA_UNKNOWN;
  		}
  		return Environment.getStorageState(new File(path));
  	}
  	
  	public static String getExtSdStorageState(Context context){
  		String path = getSdcardPath(context);
  		if (TextUtils.isEmpty(path)){
  			return Environment.MEDIA_UNKNOWN;
  		}
  		return Environment.getStorageState(new File(getSdcardPath(context)));
  	}
  	
  	public static String getSdcardPath(Context context){
  		String path = hvReflectUtils.getStoragePath(context, "SD 卡");
  		return path;
  	}	
  	
  	public static String getUDiskPath(Context context){
  		String path = hvReflectUtils.getStoragePath(context, "U 盘");
  		return path;
  	}
  	

    public static long getSDAvailableBytes(Context context){
		String status = getExtSdStorageState(context);
		if (status.equals(Environment.MEDIA_MOUNTED)){
			StatFs stat = new StatFs(getSdcardPath(context));
			return stat.getAvailableBytes();
		}
		return 0;
    }
	
}
