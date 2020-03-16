package com.hanvon.speech.realtime.util;

import android.content.Context;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.R;
import com.hanvon.speech.realtime.ui.LoginActivity;
import com.hanvon.speech.realtime.view.CustomDialog;

public class DialogUtil {
    CustomDialog customDialog;
    private static DialogUtil singleton = null;

    private DialogUtil() {
    }



    public static DialogUtil getInstance() {
        if (singleton == null) {
            singleton = new DialogUtil();
        }
        return singleton;
    }


    public void showProgressDialog(Context context) {
        customDialog = new CustomDialog(context);
        customDialog.setMessage(context.getResources().getString(R.string.loading));
        customDialog.setCancel(context.getResources().getString(R.string.cancel), new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {
                Toast.makeText(context, context.getResources().getString(R.string.success_Cancel),Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.show();
    }
    public void disProgressDialog() {
        if (customDialog.isShowing())
            customDialog.dismiss();
    }

    public void showWaitingDialog(Context context) {
        customDialog = new CustomDialog(context);
        customDialog.setMessage(context.getResources().getString(R.string.contecting));
        customDialog.setCancel(context.getResources().getString(R.string.cancel), new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {
                Toast.makeText(context, context.getResources().getString(R.string.success_Cancel),Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.show();
    }
    public void disWaitingDialog() {
        if (customDialog.isShowing())
            customDialog.dismiss();
    }
}
