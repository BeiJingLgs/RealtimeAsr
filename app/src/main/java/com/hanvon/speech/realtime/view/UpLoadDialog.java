package com.hanvon.speech.realtime.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.baidu.ai.speech.realtime.R;

public class UpLoadDialog extends Dialog implements View.OnClickListener {

    private Button bt_cancel;
    private String cancel;
    private ImageView mCancelImg, mUpLoadImg;
    private TextView mUpLoadTxt;

    //声明两个点击事件，等会一定要为取消和确定这两个按钮也点击事件
    private IOnCancelListener cancelListener;
    private IOnDismisListener confirmListener;


    public void setCancel(String cancel, IOnCancelListener cancelListener) {
        this.cancel = cancel;
        this.cancelListener = cancelListener;
    }

    public void setOnDismiss(IOnDismisListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setUpLoadStatus(Bitmap bitmap) {
        mUpLoadTxt.setVisibility(View.GONE);
        bt_cancel.setVisibility(View.GONE);
        mCancelImg.setVisibility(View.VISIBLE);
        findViewById(R.id.qr_des).setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams para1;
        para1 = mUpLoadImg.getLayoutParams();
        para1.height = (int)(para1.height * 1.7);
        para1.width = para1.height;
        mUpLoadImg.setLayoutParams(para1);
        mUpLoadImg.setImageBitmap(bitmap);
    }


    //CustomDialog类的构造方法
    public UpLoadDialog(@NonNull Context context) {
        super(context);
    }

    public UpLoadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    //在app上以对象的形式把xml里面的东西呈现出来的方法！
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        setContentView(R.layout.dialog_qr);

        //设置弹窗的宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x * 0.6);//是dialog的宽度为app界面的80%
        p.height = p.width;
        getWindow().setAttributes(p);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        bt_cancel = findViewById(R.id.btnCancel);
        mCancelImg = findViewById(R.id.cancelImg);
        mUpLoadTxt = findViewById(R.id.uploadTxt);
        mUpLoadImg = findViewById(R.id.uploadWait);
        if (!TextUtils.isEmpty(cancel)) {
            bt_cancel.setVisibility(View.VISIBLE);
            bt_cancel.setText(cancel);
        } else {
            bt_cancel.setVisibility(View.INVISIBLE);
        }

        bt_cancel.setOnClickListener(this);
        mCancelImg.setOnClickListener(this);
    }

    //重写onClick方法
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                if (cancelListener != null) {
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.cancelImg:
                if (cancelListener != null) {
                    confirmListener.onDismiss(this);
                }
                break;
        }
    }

    //写两个接口，当要创建一个CustomDialog对象的时候，必须要实现这两个接口
    //也就是说，当要弹出一个自定义dialog的时候，取消和确定这两个按钮的点击事件，一定要重写！
    public interface IOnCancelListener {
        void onCancel(UpLoadDialog dialog);
    }

    public interface IOnDismisListener {
        void onDismiss(UpLoadDialog dialog);
    }
}
