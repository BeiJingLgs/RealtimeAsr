package com.hanvon.speech.realtime.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.ToastUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class BaseActivity extends Activity  implements View.OnClickListener {
    protected Button mHomeBtn;
    protected Button mBackBtn;
    protected ImageButton mMenus, mCreateFile, mMineBtn, mUpdateBtn;
    public String TAG;
    public static String DEVICEID = "1000000000000007";
    public String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    //返回code
    public static final int OPEN_SET_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(provideContentViewId());
        initPermissions();
        initbar();
        initView(savedInstanceState, this.getWindow().getDecorView());
    }

    private void initPermissions() {
        if (lacksPermission()) {//判断是否拥有权限
            //请求权限，第二参数权限String数据，第三个参数是请求码便于在onRequestPermissionsResult 方法中根据code进行判断
            ActivityCompat.requestPermissions(this, permissions, OPEN_SET_REQUEST_CODE);
        } else {
            //拥有权限执行操作
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public boolean lacksPermission() {
        for (String permission : permissions) {
            //判断是否缺少权限，true=缺少权限
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] strings, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, strings, grantResults);
        LogUtils.printErrorLog("onRequestPermissionsResult", "strings: " + strings.length);
        LogUtils.printErrorLog("onRequestPermissionsResult", "grantResults: " + grantResults.length);
        switch (requestCode) {
            case OPEN_SET_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            ToastUtils.show(getApplicationContext(), "未拥有相应权限: " + strings[1]);
                            return;
                        }
                    }
                    //拥有权限执行操作
                } else {
                    ToastUtils.show(getApplicationContext(), "未拥有相应权限: ");
                }
        }
    }

    private void initbar() {
        mHomeBtn=  findViewById(R.id.btn_Home);
        mBackBtn = (Button) findViewById(R.id.btn_Return);
        mMenus = findViewById(R.id.btn_option_menus);
        mCreateFile = findViewById(R.id.btn_option_create);
        mMineBtn = findViewById(R.id.btn_option_mine);
        //mUpdateBtn = findViewById(R.id.btn_option_update);
        mBackBtn.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mMenus.setOnClickListener(this);
        mCreateFile.setOnClickListener(this);
        mMineBtn.setOnClickListener(this);
        //mUpdateBtn.setOnClickListener(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    abstract int provideContentViewId();//用于引入布局文件

    abstract void initView(Bundle savedInstanceState, View view);

}
