package com.hanvon.speech.realtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.util.MethodUtils;

public class LoginActivity extends BaseActivity {

    private TextView btn_reg;

    @Override
    int provideContentViewId() {
        return R.layout.login_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mMenus.setVisibility(View.GONE);
        btn_reg = findViewById(R.id.btn_reg);
        btn_reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reg:
                Intent intent = new Intent(this, RegisterActivity.class);
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
