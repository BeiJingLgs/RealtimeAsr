package com.hanvon.speech.realtime.ui;

import android.os.Bundle;
import android.view.View;
import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.util.MethodUtils;

public class RegisterActivity extends BaseActivity {
    @Override
    int provideContentViewId() {
        return R.layout.register_;
    }

    @Override
    void initView(Bundle savedInstanceState, View view) {
        mMenus.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnHome:
                new MethodUtils(this).getHome();
                break;
            case R.id.btnReturn:
                finish();
                break;

        }
    }
}
