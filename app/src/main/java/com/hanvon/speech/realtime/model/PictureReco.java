package com.hanvon.speech.realtime.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.bean.FTBlock;
import com.hanvon.speech.realtime.bean.FTChar;
import com.hanvon.speech.realtime.bean.FTLine;
import com.hanvon.speech.realtime.bean.RecoResult;
import com.hanvon.speech.realtime.ui.HtmlActivity;
import com.hanvon.speech.realtime.util.Base64Util;
import com.hanvon.speech.realtime.view.CommonDialog;


public class PictureReco {

	public static final int WIFI_SUCCESS = 0; // 成功

	public static final int PICTURE_FILE_NOT_FOUNT = 10001;
	public static final int PICTURE_FILE_PARSE_ERR = 10002;
	public static final int WIFI_SOCKET_TIMEOUT = 10003; // 网络响应返回的错误码，如404没找到等
	public static final int WIFI_CONNECT_TIMEOUT = 10004;
	public static final int WIFI_CONNECT_ERR = 10005;
	public static final int WIFI_CONTENT_READ_ERR = 10006;
	public static final int JSON_PARSE_ERR = 10007;
	public static final int GET_TOKEN_ERR = 10009;
	public static final int TOKEN_TIME_OUT = 10010;
	// 其他非0的错误为服务器传回的错误
	public static final int NO_RESULT = 101;


	static int timeoutConnection = 30000;
	static int timeoutSocket = 30000;
	static String strKey = "a91-hwzy";
	static String clientSecret = "275569523f474260b";
	static String strUrl = "http://cloud.hw-ai.com/api/";
	String strToken = null;
	Context context;

	private SharedPreferences preferSetting = null;
	private SharedPreferences.Editor editSetting = null;

	public PictureReco(Context context){
		this.context = context;

		preferSetting = context.getSharedPreferences("hvnote_setting", Context.MODE_PRIVATE);
		editSetting = preferSetting.edit();
		strToken = preferSetting.getString("reco_token", null);
	}


	public static void ReprotError(Context context, RecoResult err) {

		int errcode = err.code;
		int nNetResposeCode = 0;
		if(errcode > 20000){ // 叠加了翻落反馈的错误值
			// 此时err必然是WIFI_CONNECT_ERR
			nNetResposeCode = errcode - 10000 - WIFI_CONNECT_ERR;
			errcode = WIFI_CONNECT_ERR;
		}

		if(nNetResposeCode == 302 || nNetResposeCode == 301
				|| nNetResposeCode == 303 || nNetResposeCode == 307) // 302是跳转错误，转到登录页面
		{
			Intent intent = new Intent();
			intent.setClass(context, HtmlActivity.class);
			context.startActivity(intent);
			return;
		}

		String strMsg = "";
		switch (errcode) {
			case PICTURE_FILE_NOT_FOUNT:
				strMsg = context.getString(R.string.error_unknown);
				break;
			case PICTURE_FILE_PARSE_ERR:
				strMsg = context.getString(R.string.error_no_sd);
				break;
			case GET_TOKEN_ERR:
				strMsg = context.getString(R.string.error_no_space);
				break;
			case TOKEN_TIME_OUT:
				strMsg = context.getString(R.string.token_timeout);
				break;

			// 以下是连接网络过程中返回的自定义错误码
			case WIFI_CONNECT_ERR: // 网络响应返回的错误码，如404没找到等
				strMsg = context.getString(R.string.net_error);
				strMsg = strMsg + "(" + errcode + ":" + nNetResposeCode + ")";
				break;
			case WIFI_CONNECT_TIMEOUT: // 连接超时
			case WIFI_SOCKET_TIMEOUT:
				strMsg = context.getString(R.string.net_timeout);
				strMsg = strMsg + "(" + errcode + ")";
				break;
			case NO_RESULT:
				strMsg = context.getString(R.string.noresult);
				break;

			default:
				strMsg = context.getString(R.string.net_error);
				strMsg = strMsg + "(" + errcode + ":" + err.strErr + ")";
				break;

		}

		// 报错
		final CommonDialog msgbox =  new CommonDialog(context,0);
		msgbox.setInfo(strMsg);
		msgbox.setNegativeButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 此处最后一定要调用dismiss函数
				msgbox.dismiss();
			}
		});
		msgbox.show();
	}



	// 主要功能
	public RecoResult GetPicatureReco(String imgPath){

		RecoResult result = new RecoResult();
		// 如果本地token为空就获取token
		if(strToken == null){
			strToken = GetToken();
		}

		if(strToken == null){
			result.code = GET_TOKEN_ERR;
			return result;
		}
		SaveToken();

		File file = new File(imgPath);
		if(!file.exists()){
			result.code = PICTURE_FILE_NOT_FOUNT;
			return result;
		}

		HttpResult httpResult  = HttpPostData(strUrl + "ocr/docReco", file);


		if(httpResult.getNetStatusCode() == TOKEN_TIME_OUT){
			// token过期，重新获取
			strToken = GetToken();
			if(strToken == null){
				result.code = GET_TOKEN_ERR;
				return result;
			}
			else{
				SaveToken();
				httpResult  = HttpPostData(strUrl + "ocr/docReco", file);
			}
		}


		if(httpResult.getNetStatusCode() != WIFI_SUCCESS){
			result.code = httpResult.getNetStatusCode();
		}else{
			// 解析json
			String json = httpResult.GetResultStream();
			convertContents(json, result);
		}
		return result;
	}


	private  void SaveToken(){
		if (preferSetting == null){
			preferSetting = context.getSharedPreferences("hvnote_setting", Context.MODE_PRIVATE);
			editSetting = preferSetting.edit();
		}
		editSetting.putString("reco_token", strToken);
		editSetting.commit();
	}


	@SuppressWarnings("finally")
	private String GetToken()
	{
		String url = strUrl + "oauth/token";
		String strRes = null;
		String strBody = null;
		try {
			String key =  URLEncoder.encode(strKey, "utf-8");
			strBody = "grant_type=client_credentials&client_id=" + key + "&client_secret=" + URLEncoder.encode(clientSecret, "utf-8"); //new String(buffer);;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		setScreenOffValidation(false);
		setSleepValidation(context, false);

		InputStream inputStream = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();// Set the timeout
			// in
			httpParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
			HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);// Set the default socket timeout
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(url);

			//添加http头信息
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

			httppost.setEntity(new StringEntity(strBody, HTTP.UTF_8));
			HttpResponse response;
			response = httpclient.execute(httppost);

			//检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					inputStream = entity.getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(
							inputStream));
					String line = null;
					strRes = "";
					try {
						while ((line = br.readLine()) != null) {
							strRes += line;
						}
						strRes = convertToken(strRes);
					} catch (IOException e1) {
						strRes = null;
					}
				}
			}
			else{
				strRes = null;
			}
		}catch (ConnectTimeoutException e) {
			strRes = null;
		}catch(SocketTimeoutException e){
			strRes = null;
		}
		catch (Exception e) {
			e.printStackTrace();
			strRes = null;
		}finally {
			setScreenOffValidation(true);
			setSleepValidation(context, true);
			return strRes;
		}
	}


	@SuppressWarnings("finally")
	private HttpResult HttpPostData(String url, File file) {

		HttpResult httpResult = new HttpResult();
		// 获取文件流数据
		String strBody = null;
		try {
			FileInputStream fin;
			fin = new FileInputStream(file);
			int length = fin.available();
			byte [] buffer = new byte[length];
			fin.read(buffer);
			fin.close();

			String img = Base64Util.encode(buffer);
			img = URLEncoder.encode(img, "utf-8");
			strBody = "key=" + strKey + "&base64img=" + img; //new String(buffer);;

		} catch(Exception e){
			e.printStackTrace();
			httpResult.setNetStatusCode(PICTURE_FILE_PARSE_ERR);
			return httpResult;
		}

		setScreenOffValidation(false);
		setSleepValidation(context, false);

		String strRes = "";
		InputStream inputStream = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();// Set the timeout
			// in
			httpParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
			HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);// Set the default socket timeout
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(url);

			//添加http头信息
			httppost.addHeader("length", Integer.toString(strBody.length()));
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httppost.addHeader("Authorization", "bearer" + strToken);

			httppost.setEntity(new StringEntity(strBody, HTTP.UTF_8));
			HttpResponse response;
			response = httpclient.execute(httppost);

			//检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				httpResult.setNetStatusCode(WIFI_SUCCESS);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					inputStream = entity.getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(
							inputStream));
					String line = null;
					try {
						while ((line = br.readLine()) != null) {
							strRes += line;
						}
						httpResult.setResultStream(strRes);
					} catch (IOException e1) {
						httpResult.setNetStatusCode(WIFI_CONTENT_READ_ERR);
					}
				}
			}
			else if(code == 401){
				httpResult.setNetStatusCode(TOKEN_TIME_OUT);
			}
			else{
				httpResult.setNetStatusCode(WIFI_CONNECT_ERR + 10000 + code);
			}
		}catch (ConnectTimeoutException e) {
			httpResult.setNetStatusCode(WIFI_CONNECT_TIMEOUT);
		}catch(SocketTimeoutException e){
			httpResult.setNetStatusCode(WIFI_SOCKET_TIMEOUT);
		}
		catch (Exception e) {
			e.printStackTrace();
			httpResult.setNetStatusCode(WIFI_CONNECT_ERR);
		}finally {
			setScreenOffValidation(true);
			setSleepValidation(context, true);
			return httpResult;
		}

	}

	private  String convertToken(String json) {

		JSONObject jsonInfo = null;
		try {
			jsonInfo = new JSONObject(json);
		} catch (Exception e) {
			return null;
		}
		String token = jsonInfo.optString("access_token");
		return token;
	}


	private void convertContents(String json, RecoResult result) {

		JSONObject jsonInfo = null;
		try {
			jsonInfo = new JSONObject(json);
		} catch (Exception e) {
			result.code = JSON_PARSE_ERR;
			return;
		}

		result.code = jsonInfo.optInt("code");

		if (result.code != 0) {

			String strErr = jsonInfo.optString("result");
			result.strErr = strErr;
			return;
		}

		JSONObject jsonResult = jsonInfo.optJSONObject("result");
		if (jsonResult != null) {
			result.result = new FTBlock();
			result.result.rotate = jsonResult.optInt("rotate");
			JSONArray lineJsonArray = jsonResult.optJSONArray("lines");

			result.result.lines = new  ArrayList<FTLine>();
			for (int i = 0; i < lineJsonArray.length(); i++) {
				FTLine line = new FTLine();
				line.type = lineJsonArray.optJSONObject(i).optInt("type");
				line.score = lineJsonArray.optJSONObject(i).optInt("score");

				line.chars = new ArrayList<FTChar>();
				JSONArray charArray = lineJsonArray.optJSONObject(i).optJSONArray("chars");

				for (int j = 0; j < charArray.length(); j++) {
					FTChar char1 = new FTChar();
					char1.code = charArray.optJSONObject(j).optString("code");
					char1.type = lineJsonArray.optJSONObject(i).optInt("type");
					char1.score = lineJsonArray.optJSONObject(i).optInt("score");
					JSONArray coordsArray = lineJsonArray.optJSONObject(i).optJSONArray("coords");
					char1.coords = new Rect();
					if(coordsArray.length() == 8) {
						char1.coords.left = coordsArray.optInt(0);
						char1.coords.top  = coordsArray.optInt(1);
						char1.coords.right =  coordsArray.optInt(2);
						char1.coords.bottom = coordsArray.optInt(5);
					}
					line.chars.add(char1);
				}
				result.result.lines.add(line);
			}
		}
	}


	/**
	 * 设置是否允许锁屏
	 */
	public static void setScreenOffValidation(boolean isValidate) {

		if (isValidate) {
			reLock();
		} else {
			unLock();
		}
	}


	private static void unLock() {


	}

	private static void reLock() {


	}

	private static PowerManager.WakeLock mWakeLock = null;
	/**
	 * 设置是否允许休眠
	 */
	public static void setSleepValidation(Context mContext, boolean isValidate) {
		if (isValidate) {
			resleep(mContext);
		} else {
			unsleep(mContext);
		}
	}

	// forbidden to sleep, add by cs 20180125
	private static void unsleep(Context mContext){
		// forbidden to sleep,add by cs 20180125
		Intent intentUnsleep = new Intent();
		intentUnsleep.setAction("hanvon.intent.action.sysunsleep");
		mContext.sendBroadcast(intentUnsleep);
	}

	// allow to sleep, add by cs 20180125
	private static void resleep(Context mContext) {
		// allow to sleep,add by cs 20180125
		Intent intentUnsleep = new Intent();
		intentUnsleep.setAction("hanvon.intent.action.sysresleep");
		mContext.sendBroadcast(intentUnsleep);
	}
}



class HttpResult {

	private String strJson;
	private Integer netStatusCode; // 网络连接状态值


	public HttpResult() {
		// TODO Auto-generated constructor stub
		netStatusCode = PictureReco.WIFI_CONNECT_ERR;
		strJson = "";
	}

	public Integer getNetStatusCode() {
		return netStatusCode;
	}

	public void setNetStatusCode(Integer code) {
		netStatusCode = code;
	}

	public void setResultStream(String is) {
		strJson = is;
	}

	public String GetResultStream(){
		return strJson;
	}
}
