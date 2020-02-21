package com.hanvon.speech.realtime.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import com.baidu.ai.speech.realtime.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class BaseActivity extends Activity  implements View.OnClickListener {
    protected Button mHomeBtn;
    protected Button mBackBtn;
    protected ImageButton mMenus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(provideContentViewId());
        // 检查权限
        if (ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请授权
            ActivityCompat
                    .requestPermissions(
                            BaseActivity.this,
                            new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.INTERNET,
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.WRITE_MEDIA_STORAGE,
                                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                            1);
        }

        initbar();
        initView(savedInstanceState, this.getWindow().getDecorView());
    }



    private void initbar() {
        mHomeBtn=  findViewById(R.id.btn_Home);
        mBackBtn = (Button) findViewById(R.id.btn_Return);
        mMenus = findViewById(R.id.btn_option_menus);
        mBackBtn.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mMenus.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    abstract int provideContentViewId();//用于引入布局文件

    abstract void initView(Bundle savedInstanceState, View view);

}
