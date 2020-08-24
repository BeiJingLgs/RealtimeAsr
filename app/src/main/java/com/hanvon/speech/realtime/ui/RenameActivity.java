package com.hanvon.speech.realtime.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.io.File;
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

        mRenameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 24) {
                    ToastUtils.showLong(getApplication(), "文件名最多支持25个字符");
                }
            }
        });
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
                String regex = "^[a-zA-Z0-9_:： \\-\u4e00-\u9fa5]+$";
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
        String newTitle = mFileBean.getTitle();
        String newPathFile = path + newTitle.replace(" ", "_").replace(":", "_") + ".txt";
        hvFileCommonUtils.renameFile(srcTmpFilePath, newPathFile);
    }


}
