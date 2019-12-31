package com.hanvon.speech.realtime.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.util.MethodUtils;

public class MeActivity extends BaseActivity {

    private TextView login_or_register;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    int provideContentViewId() {
        return R.layout.me_activity;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mMenus.setVisibility(View.GONE);
        login_or_register = findViewById(R.id.login_or_register);
        login_or_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.login_or_register:
                 Intent intent=new Intent(this,LoginActivity.class);
                 startActivity(intent);
                 break;
             case R.id.btnHome:
                 new MethodUtils(this).getHome();
                 break;
             case R.id.btnReturn:
                 finish();
                 break;
         }
    }
}
