package com.hanvon.speech.realtime.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ai.speech.realtime.R;
import com.baidu.ai.speech.realtime.android.HvApplication;
import com.hanvon.speech.realtime.adapter.JumpAdapter;
import com.hanvon.speech.realtime.model.note.NoteBaseData;
import com.hanvon.speech.realtime.view.CustomDialog;
import com.hanvon.speech.realtime.view.NetWorkDialog;

public class DialogUtil {
    private static String TAG = "DialogUtil";

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
                Toast.makeText(context, context.getResources().getString(R.string.success_Cancel), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, context.getResources().getString(R.string.success_Cancel), Toast.LENGTH_SHORT).show();
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
                HvApplication.IS_NEEDIALOG = false;
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


    protected PopupWindow popPageJump = null;
    protected View viewPageJump = null;
    protected ImageButton btnPrevTen = null;
    protected ImageButton btnNextTen = null;
    protected ListView listPageJump = null;
    protected JumpAdapter listApapter = null;
    protected int jumpCurPage = 0;
    protected int jumpPageCount = 0;
    public static final int PAGE_JMP_BLOCK_SIZE = 10;
    public NoteChanged noteChanged;

    /**
     * 页面跳转
     */
    protected void pageJump(int mNotePageIndex) {
        // 退出二值模式


    }


    private View.OnClickListener onPageJumpClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            switch (view.getId()) {
                case R.id.btn_prev_ten:
                    if (jumpCurPage > 0) {
                        modifyPageJumpBtnState(--jumpCurPage, NoteBaseData.gTraFile.getCount());
                    }
                    break;
                case R.id.btn_next_ten:
                    if (jumpCurPage < jumpPageCount - 1) {
                        modifyPageJumpBtnState(++jumpCurPage, NoteBaseData.gTraFile.getCount());
                    }
                    break;
            }
        }
    };

    /**
     * 修改btn的状态
     *
     * @param pageIndex 第几页
     * @param count     总共有多少页
     */
    protected void modifyPageJumpBtnState(int pageIndex, int count) {
        btnPrevTen.setClickable(true);
        btnPrevTen.setImageResource(R.drawable.prev_ten_page);
        btnNextTen.setClickable(true);
        btnNextTen.setImageResource(R.drawable.next_ten_page);
        if (pageIndex == 0) {
            btnPrevTen.setClickable(false);
            btnPrevTen.setImageResource(R.drawable.prev_ten_page_grey);
        }
        if (pageIndex == jumpPageCount - 1) {
            btnNextTen.setClickable(false);
            btnNextTen.setImageResource(R.drawable.next_ten_page_grey);
        }
        listApapter.updatePage(pageIndex);
    }

    public void showJumpDialog(Context context, int mNotePageIndex) {
        LayoutInflater inflater = LayoutInflater.from(context);
        viewPageJump = inflater.inflate(R.layout.popup_page_jump, null);

        int width = HvApplication.mContext.getResources().getDimensionPixelSize(R.dimen.dialog_button_mid);
        popPageJump = new PopupWindow(viewPageJump, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popPageJump.setBackgroundDrawable(new BitmapDrawable());

        final int count = NoteBaseData.gTraFile.getCount();

        btnPrevTen = (ImageButton) viewPageJump.findViewById(R.id.btn_prev_ten);
        btnPrevTen.setOnClickListener(onPageJumpClick);

        btnNextTen = (ImageButton) viewPageJump.findViewById(R.id.btn_next_ten);
        btnNextTen.setOnClickListener(onPageJumpClick);

        listPageJump = (ListView) viewPageJump.findViewById(R.id.listPage);

        listApapter = new JumpAdapter(HvApplication.mContext, mNotePageIndex, count);
        listPageJump.setAdapter(listApapter);

        jumpCurPage = mNotePageIndex / PAGE_JMP_BLOCK_SIZE;
        jumpPageCount = (count + PAGE_JMP_BLOCK_SIZE - 1) / PAGE_JMP_BLOCK_SIZE;
        modifyPageJumpBtnState(jumpCurPage, count);

        int gravity = Gravity.BOTTOM | Gravity.LEFT;
        popPageJump.showAtLocation(viewPageJump, gravity, 72,
                95);
        popPageJump.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                // mNoteView.setInputEnabled(true);
            }
        });

        listPageJump.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                popPageJump.dismiss();
                if (id < count) {
                    //jumpToPage((int) id);
                    noteChanged.notifyNoteChanged((int) id);
                }
            }
        });
    }


    int index = 0;

    public void showOCRSelectDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        viewPageJump = inflater.inflate(R.layout.dialog_ocr_select2, null);
        int width = HvApplication.mContext.getResources().getDimensionPixelSize(R.dimen.dialog_button_largeX);
        popPageJump = new PopupWindow(viewPageJump, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popPageJump.setBackgroundDrawable(new BitmapDrawable());
        int gravity = Gravity.CENTER;
        popPageJump.showAtLocation(viewPageJump, gravity, 0,
                0);

        RadioGroup rgOcrType = (RadioGroup) viewPageJump.findViewById(R.id.ocrType);
        rgOcrType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.btn_ocr_local:
                        index = 0;
                        LogUtils.printErrorLog(TAG, "btn_ocr_local");
                        break;
                    case R.id.btn_ocr_online:
                        index = 1;
                        LogUtils.printErrorLog(TAG, "btn_ocr_online");
                        break;
                }
            }
        });
        Button btnOk = viewPageJump.findViewById(R.id.btnOKImage);
        Button btnCancel = viewPageJump.findViewById(R.id.btnCancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteChanged.startOcr(index);
                popPageJump.dismiss();
                LogUtils.printErrorLog(TAG, "btnOk.setOnClickListener");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.printErrorLog(TAG, "btnCancel.setOnClickListener");
                popPageJump.dismiss();
            }
        });
    }

    public void regListener(NoteChanged ml) {
        this.noteChanged = ml;
    }

    public interface NoteChanged {
        void notifyNoteChanged(int i);

        void startOcr(int i);
    }
}
