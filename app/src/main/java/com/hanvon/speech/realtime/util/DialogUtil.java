package com.hanvon.speech.realtime.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.hanvon.speech.realtime.view.CommonDialog;
import com.hanvon.speech.realtime.view.CustomDialog;
import com.hanvon.speech.realtime.view.NetWorkDialog;

public class DialogUtil {
    CustomDialog customDialog;
    private static DialogUtil singleton = null;
    NetWorkDialog mNetDialog;
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
        if (customDialog == null)
            return;
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
        if (customDialog == null)
            return;
        if (customDialog.isShowing())
            customDialog.dismiss();
    }

    public void showNetWorkDialog(Context context) {
        if (mNetDialog != null) {
            if (mNetDialog.isShowing())
                return;
        }
        mNetDialog = new NetWorkDialog(context);
        mNetDialog.setMessage(context.getResources().getString(R.string.unwifi));
        mNetDialog.setCancel(context.getResources().getString(R.string.cancel), new NetWorkDialog.IOnCancelListener() {
            @Override
            public void onCancel(NetWorkDialog dialog) {
                HvApplication.IS_NETDIALOG = false;
                disNetWorkDialog();
            }
        });

        mNetDialog.setConfirm(context.getResources().getString(R.string.ok), new NetWorkDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(NetWorkDialog dialog) {
                disNetWorkDialog();
                WifiOpenHelper wifi = new WifiOpenHelper(HvApplication.getContext());
                wifi.openWifi();
                HvApplication.getContext().startActivity(new Intent(
                        android.provider.Settings.ACTION_WIFI_SETTINGS));
            }
        });
        mNetDialog.show();
    }
    public void disNetWorkDialog() {
        if (mNetDialog == null)
            return;
        if (mNetDialog.isShowing())
            mNetDialog.dismiss();
    }


    private AlertDialog processDialog;

    public boolean showRecogDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.myProgressDialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.tip_btn_dialog, null);
        ((TextView) layout.findViewById(R.id.textInfo)).setText(context.getResources().getString(R.string.processing));
        builder.setView(layout);

        ((TextView) layout.findViewById(R.id.btnCancel)).setVisibility(View.VISIBLE);
        ((TextView) layout.findViewById(R.id.btnCancel)).setText(context.getResources().getString(R.string.cancel));
        ((TextView) layout.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                processDialog.dismiss();
            }
        });

        processDialog = builder.create();
        processDialog.setInverseBackgroundForced(true);
        processDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    processDialog.cancel();
                }
                return false;
            }
        });
        processDialog.setCancelable(false);
        if (!processDialog.isShowing()) {
            processDialog.show();
        }
        return false;
    }

    public void disRecogDialog() {
        if (processDialog == null)
            return;
        if (processDialog.isShowing()) {
            processDialog.dismiss();
        }
    }
}
