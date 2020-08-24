package com.hanvon.speech.realtime.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.asr.ai.speech.realtime.ConstBroadStr;
import com.asr.ai.speech.realtime.R;
import com.hanvon.speech.realtime.bean.FileBean;
import com.hanvon.speech.realtime.database.DatabaseUtils;
import com.hanvon.speech.realtime.model.TranslateBean;
import com.hanvon.speech.realtime.util.FileUtils;
import com.hanvon.speech.realtime.util.LogUtils;
import com.hanvon.speech.realtime.util.ToastUtils;
import com.hanvon.speech.realtime.util.hvFileCommonUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameActivity extends Activity implements View.OnClickListener {
    private Button mCancelBtn, mOkBtn;
    private EditText mRenameEd;
    private FileBean mFileBean;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);
        initView();
        init();
    }

    private void initView() {
        mCancelBtn = findViewById(R.id.btnCancel);
        mOkBtn = findViewById(R.id.btnOKImage);
        mRenameEd = findViewById(R.id.renameEd);

        mOkBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    private void init() {
        mFileBean = TranslateBean.getInstance().getFileBean();
        mFilePath = mFileBean.title;
        mRenameEd.setText(mFileBean.title);
        mRenameEd.setSelection(mFileBean.title.length());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                onBackPressed();
                break;
            case R.id.btnOKImage:
                if (TextUtils.isEmpty(mRenameEd.getText().toString().trim())) {
                    ToastUtils.showLong(this, getString(R.string.namenull));
                }
                String regex = "^[a-zA-Z0-9_\\-\u4e00-\u9fa5]+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher match=pattern.matcher(mRenameEd.getText().toString().trim());
                if (match.matches()) {
                    mFileBean.setTitle(mRenameEd.getText().toString().trim());
                    DatabaseUtils.getInstance(getApplicationContext()).updateTileByMillis(mFileBean);
                    deleteFileTxt(mFilePath);
                    onBackPressed();
                } else {
                    ToastUtils.showLong(this, getString(R.string.nomatchName));
                }

                break;
        }
    }

    public static final String TAG = "RenameActivity";
    private void deleteFileTxt(String title) {
        String path = ConstBroadStr.ROOT_PATH + getResources().getString(R.string.recog_recordtxt);
        String srcTmpFilePath = path + title.replace(" ", "_").replace(":", "_") + ".txt";
        LogUtils.printErrorLog(TAG, "srcTmpFilePath: " + srcTmpFilePath);
        hvFileCommonUtils.recursiveDeleteAll(this, srcTmpFilePath);
    }
}
