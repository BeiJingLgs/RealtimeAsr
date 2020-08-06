package com.hanvon.speech.realtime.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.asr.ai.speech.realtime.R;
import com.hanvon.speech.realtime.bean.UpdataInfo;
import com.hanvon.speech.realtime.view.CommonDialog;

import static android.webkit.WebViewZygote.getPackageName;

public class UpdateUtil {
    Context context;
    private boolean isChecking;
    private static final String UPDATEURL = "http://edu.hwebook.cn/xys/mobile.mvc?api=getapps&type=12";
    private static final String TAG = "UpdateUtil";
    private static String APPNAME60 = "hvRecord.apk";
    private static String APPNAME960 = "hvRecord.apk";
    private boolean isShowToast;
    private String apkPath;
    private Uri uri;
    public UpdateUtil(Context context, boolean isShow) {
        this.context = context;
        isShowToast = isShow;
        // TODO Auto-generated constructor stub
    }

    // 下载xml检查版本号
    public class CheckApkTask extends AsyncTask<Void, Void, Void> {

        int curVersion;
        int resultCode = -1;
        String errorMessage;
        UpdataInfo record;

        @Override
        protected void onPreExecute() {
            isChecking = true;
            super.onPreExecute();
            curVersion = CommonUtils.getVersionCode(context);
            LogUtils.printErrorLog(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String getappUrl = null;
                URL url = new URL(UPDATEURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5 * 1000);
                connection.setReadTimeout(5 * 1000);
                connection.setRequestProperty("Connection", "close");

                // 网络连接错误
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    return null;
                    // return LoginUtils.LOGIN_NETWORK_ERROR;
                }

                org.w3c.dom.Document doc = null;

                InputStream inputStream = connection.getInputStream();

                if (inputStream == null) {
                    return null;
                }

                try {
                    // 解析xml文件
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    doc = docBuilder.parse(inputStream);

                    // 断开连接
                    inputStream.close();

                } catch (IOException e) {
                    return null;
                } catch (Exception e) {
                    return null;
                }
                /**
                 * <Return Code="0" Message="成功"> <Apps> <App Order="01"> <Version>147</Version>
                 * <Description>当当阅读</Description>
                 * <Uri>HTTP://edu.hwebook.cn/soft/hvDDReader930UP.apk</Uri> </App> </Apps>
                 * </Return>
                 */

                // 获取根节点<Return>。
                Element root = doc.getDocumentElement();
                resultCode = Integer.parseInt(root.getAttribute("Code"));
                String message = root.getAttribute("Message");
                LogUtils.printErrorLog(TAG, "message: " + message);
                if (resultCode != 0) {
                    errorMessage = message;
                    return null;
                }
                NodeList appsList = root.getElementsByTagName("Apps");
                LogUtils.printErrorLog(TAG, "appsList.getLength(): " + appsList.getLength());
                if (appsList.getLength() != 1) {
                    return null;
                }
                int version_type = context.getResources().getInteger(R.integer.VERSION_TYPE);
                Element appsRoot = (Element) appsList.item(0);
                NodeList appList = appsRoot.getElementsByTagName("App");
                int itemCount = appList.getLength();
                LogUtils.printErrorLog(TAG, "itemCount: " + itemCount);
                record = new UpdataInfo();
                for (int i = 0; i < itemCount; i++) {
                    Element recordNode = (Element) appList.item(i);

                    if (recordNode.getAttribute("Order").equals(version_type + "")) {
                        NodeList childList = recordNode.getChildNodes();
                        LogUtils.printErrorLog(TAG, "childList.getLength()=" + childList.getLength());
                        for (int j = 0; j < childList.getLength(); j++) {
                            LogUtils.printErrorLog(TAG,
                                    "childList.item(j).getNodeName() j =" + j + childList.item(j).getNodeName());
                            if ((childList.item(j)).getNodeName().equals("Version")) {
                                record.setVersion(childList.item(j).getTextContent());
                            } else if ((childList.item(j)).getNodeName().equals("Description")) {
                                record.setDescription(childList.item(j).getTextContent());
                            } else if ((childList.item(j)).getNodeName().equals("Uri")) {
                                record.setUrl(childList.item(j).getTextContent());
                            }
                        }
                    }
                }

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            if (resultCode != 0) {
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    //网络未连接时
                    Toast.makeText(context, R.string.checkNeterror, Toast.LENGTH_SHORT).show();
                }
                isChecking = false;
                return;
            }

            LogUtils.printErrorLog(TAG, " curVersion =" + curVersion);
            LogUtils.printErrorLog(TAG, " record.getVersion() =" + record.getVersion());
            LogUtils.printErrorLog(TAG, " record.getUrl() =" + record.getUrl());

            if (curVersion != 0 && record != null) {
                if (record.getVersion() != null && Integer.parseInt(record.getVersion()) > curVersion) {
                    final CommonDialog myDialog = new CommonDialog(context, 0);
                    final String updataUrl = record.getUrl();
                    LogUtils.printErrorLog(TAG, " updataUrl =" + updataUrl);
                    LogUtils.printErrorLog(TAG, " record.getVersion() =" + record.getVersion());
                    myDialog.setTitle(R.string.dialog_hint);
                    myDialog.setInfo(
                            context.getResources().getString(R.string.check_version_updata1) +
                                    CommonUtils.getVersionCode2Name(record.getVersion()) +
                                    context.getResources().getString(R.string.check_version_updata2)
                    );
                    myDialog.setPositiveButton(R.string.dialog_positive_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownNewApkTask downNewApkTask = new DownNewApkTask(updataUrl);
                            downNewApkTask.execute();
                            myDialog.dismiss();
                        }
                    });
                    myDialog.setNegativeButton(R.string.dialog_negative_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isChecking = false;
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                } else {
                    isChecking = false;

                    if (isShowToast) {
                        LogUtils.printErrorLog(TAG, " isShowToast =" + isShowToast);
                        Toast.makeText(context, context.getResources().getString(R.string.version_latest), 0).show();
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }
    }

    // 下载apk
    public class DownNewApkTask extends AsyncTask<Void, Integer, Integer> {

        String apkUrl;
        CommonDialog progressDialog;
        ProgressBar progressBar;
        TextView progressTv;
        File installFile;

        public DownNewApkTask(String url) {
            apkUrl = url;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            isChecking = false;
            super.onPreExecute();
            apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ APPNAME960;
            LogUtils.printErrorLog(TAG, " apkPath =" + apkPath);
            uri = FileProvider.getUriForFile(context, "com.asr.ai.speech.realtime.fileprovider", new File(apkPath));
            progressDialog = new CommonDialog(context, R.layout.dialog_down_apk_progress, true);
            progressDialog.setTitle(R.string.downing_apk);
            View rootView = progressDialog.getView();
            progressBar = (ProgressBar) rootView.findViewById(R.id.down_progressbar);
            progressTv = (TextView) rootView.findViewById(R.id.down_progress_tv);


            rootView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownNewApkTask.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
            progressDialog.show(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DownNewApkTask.this.cancel(true);
                }
            });
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {

                String appName = "";
                if (context.getResources().getInteger(R.integer.device_inch) == 6) {
                    appName = APPNAME60;
                } else {
                    appName = APPNAME960;
                }

                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Charset", "UTF-8");
                // 获取到文件的大小
                if (Build.VERSION.SDK_INT == 8) {
                    installFile = new File("mnt/flash", appName);
                } else {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                        installFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                                appName);
                    }
                }
                if (installFile == null) {
                    return -1;
                }
                int total = 0;
                if (installFile.exists()) {
                    installFile.delete();
                }
                int code = conn.getResponseCode();
                System.out.println("code = " + code);


                progressBar.setMax(conn.getContentLength());
                InputStream is = conn.getInputStream();

                LogUtils.printErrorLog(TAG, "getContentLength=" + conn.getContentLength());

                FileOutputStream fos = new FileOutputStream(installFile, true);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;

                while ((len = bis.read(buffer)) != -1) {
                    if (isCancelled()) {
                        return -2;
                    }
                    fos.write(buffer, 0, len);
                    total += len;
                    // 获取当前下载量
                    //Thread.sleep(1000);
                    publishProgress(total);
                    LogUtils.printErrorLog(TAG, "total: " + total);
                }
                fos.close();
                bis.close();
                is.close();
                //sleep(1000);
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int total = values[0];
            progressBar.setProgress(total);
            int maxProgress = progressBar.getMax();
            progressTv.setText(total * 100 / maxProgress + "%");

        }

        @Override
        protected void onPostExecute(Integer reslut) {
            super.onPostExecute(reslut);
            progressDialog.dismiss();
            if (reslut == 0) {
                installApk(uri, apkPath);
                return;
            }
            if (reslut == -1) {
                Toast.makeText(context, R.string.downapk_fail, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
     * 获取当前程序的版本号
     */




    /**
     * 安装apk
     *
     * @param fileSavePath
     * @param apkPath
     */
    private void installApk(Uri fileSavePath, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
            intent.setDataAndType(fileSavePath, "application/vnd.android.package-archive");
            LogUtils.printErrorLog(TAG, "==Build.VERSION.SDK_INT >= Build.VERSION_CODES.N");
        } else {*/
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            data = Uri.fromFile(new File(apkPath));
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            LogUtils.printErrorLog(TAG, "==Build.VERSION.SDK_INT < Build.VERSION_CODES.N");
        //}
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
