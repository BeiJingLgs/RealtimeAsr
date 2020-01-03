package com.hanvon.speech.realtime.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import com.baidu.ai.speech.realtime.R;

public abstract class BaseActivity extends Activity  implements View.OnClickListener {
    protected Button mHomeBtn;
    protected Button mBackBtn;
    protected ImageButton mMenus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(provideContentViewId());

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
