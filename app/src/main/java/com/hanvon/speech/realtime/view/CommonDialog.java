package com.hanvon.speech.realtime.view;



import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asr.ai.speech.realtime.R;

public class CommonDialog {
	
	/**��ͨ�����ݵĶԻ���*/
	private static final int DIALOG_NORMAL = 0;
	/**messagebox*/
	private static final int DIALOG_MESSAGEBOX = 1;

	private static final int TYPE_NULL = 0;
	private static final int TYPE_OK = 1;
	private static final int TYPE_CANCEL = 2;
	private static final int TYPE_RETRY = 4;

	//private static final int DIALOG_WIDTH = 700;
	private PopupWindow popup = null;
	private View view = null;
	private Context context = null;
	private	int btnType = TYPE_NULL;
	private int dlgType = DIALOG_NORMAL;
	
	private Button btnOK;
	private Button btnCancel;
	private Button btnRetry;
	
	private static int BTN_MAX_LEN = 6;
	
	/**
	 * ����Ի���
	 * @param context
	 * @param stubViewId Ϊ0��ʱ���ʾ��messagebox������ʹ��ViewStub�е�android:id��ֵ
	 */
	public CommonDialog(Context context, int stubViewId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_base, null);
		if(stubViewId == 0){
			// ��ʾtextInfo
			view.findViewById(R.id.textInfo).setVisibility(View.VISIBLE);
			//view.findViewById(R.id.dialogSeparator).setVisibility(View.GONE);
			this.dlgType = DIALOG_MESSAGEBOX;
		}else{
			ViewStub stub = (ViewStub) view.findViewById(stubViewId);
			stub.inflate();
		}
		view.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
					popup.dismiss();
				    return true;
				}
				return false;
			}
		});
	}
	
	public CommonDialog(Context context, int stubViewId, int type) {
		// TODO Auto-generated constructor stub
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_down_apk_progress, null);
		if(stubViewId == 0){
			// ��ʾtextInfo
			//view.findViewById(R.id.textInfo).setVisibility(View.VISIBLE);
			//view.findViewById(R.id.dialogSeparator).setVisibility(View.GONE);
			this.dlgType = DIALOG_MESSAGEBOX;
		}else{
			ViewStub stub = (ViewStub) view.findViewById(stubViewId);
			stub.inflate();
		}
		view.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
					popup.dismiss();
				    return true;
				}
				return false;
			}
		});
	}
	
	
	
	/**
	 * 构造对话框
	 * @param context
	 * @param layoutId 为布局id
	 */
	public CommonDialog(Context context, int layoutId , boolean isLayout) {
		// TODO Auto-generated constructor stub
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(layoutId, null);
	}
	
	/**
	 * 构造对话框
	 * @param context
	 * @param stubViewId 为0的时候表示是messagebox，否则使用ViewStub中的android:id的值
	 */
	public CommonDialog(Context context, View stubView) {
		// TODO Auto-generated constructor stub
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_base, null);
		if(stubView == null){
			// 显示textInfo
			view.findViewById(R.id.textInfo).setVisibility(View.VISIBLE);
		}else{
			ViewStub stub = (ViewStub) stubView;
			stub.inflate();
		}
	}
	private static int DIALOG_WIDTH = 700;
	private int dialog_width = DIALOG_WIDTH;
	public void setDialogWidth(int width){
		dialog_width = width;
	}

	/**
	 * ��ʾ�Ի�������ʾ֮ǰ������һЩ��ʼ���Ķ���
	 */
	public void show(){
		adjustButtons();
		// ˢ��һ�½���
		((Activity)(context)).getWindow().getDecorView().invalidate();
		popup = new PopupWindow(view, (int)context.getResources().getDimension(R.dimen.mydialog_width), LayoutParams.WRAP_CONTENT, true);

		// ��Ļ������ʾ
		if (view == null)
			return;
		popup.showAtLocation(view, Gravity.TOP, 0, 200);
	}
	
	public void show(PopupWindow.OnDismissListener onDismissListener){
		adjustButtons();
		// 刷新一下界面
		((Activity)(context)).getWindow().getDecorView().invalidate();
		popup = new PopupWindow(view, (int)context.getResources().getDimension(R.dimen.mydialog_width), LayoutParams.WRAP_CONTENT, true);
		// 屏幕中心显示------
		popup.showAtLocation(view, Gravity.CENTER, 0, 0);
		popup.setOnDismissListener(onDismissListener);
		view.setFocusableInTouchMode(true);
		view.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event){

				if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)) {
					if(popup != null && popup.isShowing())
						popup.dismiss();
					return true;
				}

				return false;
			}});
	}
    public void setNeutralWidthButton(String text, OnClickListener listener){
        btnType |= TYPE_RETRY;
        btnRetry = (Button) view.findViewById(R.id.btnNeutral);

        ViewGroup.LayoutParams params = btnRetry.getLayoutParams();
        params.width = (int) context.getResources().getDimension(R.dimen.buttonW);
        view.setLayoutParams(params);

        btnRetry.setText(text);
        btnRetry.setOnClickListener(listener);
    }
	/**
	 * ��������ťλ��
	 */
	private void adjustButtons() {
		// TODO Auto-generated method stub
		// ֻ��һ����ť�����
		if (btnType == TYPE_OK || btnType == TYPE_CANCEL || btnType == TYPE_RETRY){
			Button btn = null;
			if (btnType == TYPE_OK){
				btn = btnOK;
			}else if(btnType == TYPE_CANCEL){
				btn = btnCancel;
			}else if(btnType == TYPE_RETRY){
				btn = btnRetry;
			}
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btn.getLayoutParams();
			params.alignWithParent = true;
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.rightMargin = (int)context.getResources().getDimension(R.dimen.mydialog_button_margin);
			btn.setLayoutParams(params);
			btn.setVisibility(View.VISIBLE);
		}else if (btnType == (TYPE_OK|TYPE_CANCEL) || btnType == (TYPE_OK|TYPE_RETRY) || btnType == (TYPE_RETRY|TYPE_CANCEL)){
			// ��������ť�����
			Button btn1 = null;
			Button btn2 = null;
			if (btnType == (TYPE_OK|TYPE_CANCEL)){
				btn1 = btnOK;
				btn2 = btnCancel;
			}else if(btnType == (TYPE_OK|TYPE_RETRY)){
				btn1 = btnOK;
				btn2 = btnRetry;
			}else if(btnType == (TYPE_RETRY|TYPE_CANCEL)){
				btn1 = btnRetry;
				btn2 = btnCancel;
			}
			//int btnWidth = (int)context.getResources().getDimension(R.dimen.dialog_okcancelbuttonW);
			int btnSpace = (int)context.getResources().getDimension(R.dimen.mydialog_button_margin);;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btn2.getLayoutParams();
			params.alignWithParent = true;
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.rightMargin = btnSpace;
			btn2.setLayoutParams(params);
			btn2.setVisibility(View.VISIBLE);
			
			params = (RelativeLayout.LayoutParams) btn1.getLayoutParams();
			params.alignWithParent = false;
			params.addRule(RelativeLayout.LEFT_OF, btn2.getId());
			params.rightMargin = btnSpace;
			btn1.setLayoutParams(params);
			btn1.setVisibility(View.VISIBLE);
		} else if (btnType == (TYPE_OK|TYPE_CANCEL|TYPE_RETRY)){
			// ������ť�����ڵ����
			//int btnWidth = (int)context.getResources().getDimension(R.dimen.dialog_okcancelbuttonW);
			int btnSpace = (int)context.getResources().getDimension(R.dimen.mydialog_button_margin);
			
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnCancel.getLayoutParams();
			params.alignWithParent = true;
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.rightMargin = btnSpace;
			btnCancel.setLayoutParams(params);
			btnCancel.setVisibility(View.VISIBLE);
			
			params = (RelativeLayout.LayoutParams) btnRetry.getLayoutParams();
			params.addRule(RelativeLayout.LEFT_OF, btnCancel.getId());
			params.rightMargin = btnSpace;
			btnRetry.setLayoutParams(params);
			btnRetry.setVisibility(View.VISIBLE);
		
			params = (RelativeLayout.LayoutParams) btnOK.getLayoutParams();
			params.addRule(RelativeLayout.LEFT_OF, btnRetry.getId());
			params.rightMargin = btnSpace;
			btnOK.setLayoutParams(params);
			btnOK.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ���õ�ǰ�ı���
	 * @param stringID
	 */
	public void setTitle(int stringID){
		TextView text = (TextView) view.findViewById(R.id.textTitle);
		text.setText(stringID);
	}
	
	/**
	 * ���õ�ǰ�ı���
	 * @param title
	 */
	public void setTitle(String title){
		TextView text = (TextView) view.findViewById(R.id.textTitle);
		text.setText(title);
	}
	

	public void hideTitle(){
		TextView text = (TextView) view.findViewById(R.id.textTitle);
		text.setVisibility(View.GONE);
	}


	public void showTitle(){
		TextView text = (TextView) view.findViewById(R.id.textTitle);
		text.setVisibility(View.VISIBLE);
	}
	/**
	 * ����Messagebox������
	 * @param stringID
	 */
	public void setInfo(int stringID){

		TextView text = (TextView) view.findViewById(R.id.textInfo);
		text.setText(stringID);
	}
	
	/**
	 * ����Messagebox������
	 * @param info
	 */
	public void setInfo(String info){
		TextView text = (TextView) view.findViewById(R.id.textInfo);
		text.setText(info);
	}
	
	/**
	 * ����ȷ����ť
	 * @param stringID ��ʾ���ַ���
	 * @param listener �����ť����Ӧ
	 */
	public void setPositiveButton(int stringID, OnClickListener listener){		
		String text = context.getResources().getString(stringID);
		setPositiveButton(text,listener);
	}

	/**
	 * ����ȡ����ť
	 * @param stringID ��ʾ���ַ���
	 * @param listener �����ť����Ӧ
	 */
	public void setNegativeButton(int stringID, OnClickListener listener){
		btnType |= TYPE_CANCEL;
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCancel.setText(stringID);
		btnCancel.setOnClickListener(listener);
	}
	
	/**
	 * �����м�İ�ť
	 * @param stringID ��ʾ���ַ���
	 * @param listener �����ť����Ӧ
	 */
	public void setNeutralButton(int stringID, OnClickListener listener){
		String text = context.getResources().getString(stringID);
		setNeutralButton(text,listener);
	}
	
	
	/**
	 * ����ȷ����ť
	 * @param text ��ʾ���ַ���
	 * @param listener �����ť����Ӧ
	 */
	public void setPositiveButton(String text, OnClickListener listener){
		btnType |= TYPE_OK;
		btnOK = (Button) view.findViewById(R.id.btnOK);
		
		int changeLen = 4;
		boolean beng = false;
		if(Locale.getDefault().getDisplayLanguage().compareToIgnoreCase("English") == 0)
		{
			beng = true;
			BTN_MAX_LEN = 20;
			changeLen = 8;
		}		
		if (text.length() > BTN_MAX_LEN) {
			text = text.substring(0, BTN_MAX_LEN - 1) + "...";
		}
		// ������Ļ���Ҫ�����ؼ��ĳߴ�	
		if(text.length() > changeLen)
		{
			android.view.ViewGroup.LayoutParams  lp = btnOK.getLayoutParams();
			if(beng){
				lp.width += 25;
			}			
			else{
				lp.width += (text.length() - changeLen) * 25;	
			}	
			btnOK.setLayoutParams(lp);
		}
		btnOK.setText(text);
		btnOK.setOnClickListener(listener);
	}

	/**
	 * ����ȡ����ť
	 * @param text ��ʾ���ַ���
	 * @param listener �����ť����Ӧ
	 */
	public void setNegativeButton(String text, OnClickListener listener){
		btnType |= TYPE_CANCEL;
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		
		int changeLen = 4;
		boolean beng = false;
		if(Locale.getDefault().getDisplayLanguage().compareToIgnoreCase("English") == 0)
		{
			beng = true;
			BTN_MAX_LEN = 20;
			changeLen = 8;
		}			
		if (text.length() > BTN_MAX_LEN) {
			text = text.substring(0, BTN_MAX_LEN - 1) + "...";
		}
		// ������Ļ���Ҫ�����ؼ��ĳߴ�	
		if(text.length() > changeLen)
		{
			android.view.ViewGroup.LayoutParams  lp = btnCancel.getLayoutParams();
			if(beng){
				lp.width += 25;
			}			
			else{
				lp.width += (text.length() - changeLen) * 25;	
			}		
			btnCancel.setLayoutParams(lp);
		}
		
		btnCancel.setText(text);
		btnCancel.setOnClickListener(listener);
	}
	
	/**
	 * �����м�İ�ť
	 * @param text ��ʾ���ַ���
	 * @param listener �����ť����Ӧ
	 */
	public void setNeutralButton(String text, OnClickListener listener){
		btnType |= TYPE_RETRY;
		btnRetry = (Button) view.findViewById(R.id.btnNeutral);
		
		int changeLen = 4;
		boolean beng = false;
		if(Locale.getDefault().getDisplayLanguage().compareToIgnoreCase("English") == 0)
		{
			beng = true;
			BTN_MAX_LEN = 20;
			changeLen = 8;
		}			
		if (text.length() > BTN_MAX_LEN) {
			text = text.substring(0, BTN_MAX_LEN - 1) + "...";
		}
		// ������Ļ���Ҫ�����ؼ��ĳߴ�	
		if(text.length() > changeLen)
		{
			android.view.ViewGroup.LayoutParams  lp = btnRetry.getLayoutParams();
			if(beng){
				lp.width += 25;
			}			
			else{
				lp.width += (text.length() - changeLen) * 25;	
			}						
			btnRetry.setLayoutParams(lp);
		}
		btnRetry.setText(text);
		btnRetry.setOnClickListener(listener);
	}


	public void setShareButton(String text, OnClickListener listener){
		//btnType |= TYPE_RETRY;
		btnOK = (Button) view.findViewById(R.id.btnOK);
		btnOK.setVisibility(View.VISIBLE);
		int changeLen = 4;
		boolean beng = false;
		if(Locale.getDefault().getDisplayLanguage().compareToIgnoreCase("English") == 0)
		{
			beng = true;
			BTN_MAX_LEN = 20;
			changeLen = 8;
		}
		if (text.length() > BTN_MAX_LEN) {
			text = text.substring(0, BTN_MAX_LEN - 1) + "...";
		}
		// ������Ļ���Ҫ�����ؼ��ĳߴ�
		if(text.length() > changeLen)
		{
			android.view.ViewGroup.LayoutParams  lp = btnRetry.getLayoutParams();
			if(beng){
				lp.width += 25;
			}
			else{
				lp.width += (text.length() - changeLen) * 25;
			}
			btnOK.setLayoutParams(lp);
		}
		btnOK.setText(text);
		btnOK.setOnClickListener(listener);
	}

	public final View getView() {
		// TODO Auto-generated method stub
		return view;
	}

	public void dismiss() {
		// TODO Auto-generated method stub
		popup.dismiss();
	}
	
	public boolean isShowing(){
		return (popup != null && popup.isShowing());
	}

	public void setOnKeyListener(OnKeyListener onKeyListener) {
		// TODO Auto-generated method stub
		view.setOnKeyListener(onKeyListener);
	}
}
