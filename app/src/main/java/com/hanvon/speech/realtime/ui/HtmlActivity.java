
package com.hanvon.speech.realtime.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;

public class HtmlActivity extends Activity{
	
	protected TextView stateText;
	protected Button btnExit;
	protected String strTitle;
	protected LinearLayout webLayout;
	protected WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); // ���ر���
		setContentView(R.layout.html_view);
		
		Bundle bunde = this.getIntent().getExtras();
		String url = bunde.getString("Url");

		btnExit = (Button) findViewById(R.id.btn_exit);
		stateText = (TextView) findViewById(R.id.text_title);
		webLayout = (LinearLayout)findViewById(R.id.webLayout);
		stateText.setText(R.string.esdk_loading);
		webview = new WebView(this);
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		webLayout.addView(webview, param );
		
		webview.setScrollbarFadingEnabled( false ); //Ҫ������ʱ����ʾ
		webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY); 
		webview.setVisibility(View.VISIBLE);
		webview.clearFormData();
		
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//����js����ֱ�Ӵ򿪴��ڣ���window.open()��Ĭ��Ϊfalse
		webview.getSettings().setJavaScriptEnabled(true);//�Ƿ�����ִ��js��Ĭ��Ϊfalse������trueʱ�������ѿ������XSS©��
		webview.getSettings().setSupportZoom(true);//�Ƿ�������ţ�Ĭ��true
		webview.getSettings().setBuiltInZoomControls(true);//�Ƿ���ʾ���Ű�ť��Ĭ��false
//		webview.getSettings().setUseWideViewPort(true);//���ô����ԣ�������������š�����ͼģʽ
		webview.getSettings().setLoadWithOverviewMode(true);//��setUseWideViewPort(true)һ������ҳ����Ӧ����
		webview.getSettings().setAppCacheEnabled(true);//�Ƿ�ʹ�û���
		webview.getSettings().setDomStorageEnabled(true);//DOM Storage
		
		webview.loadUrl(url);
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
//				return super.shouldOverrideUrlLoading(view, url);
			}
			
			// ����վ��SSL֤���ʱ���������ִ��
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				//super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}
		});
		webview.setWebChromeClient(new WebChromeClient(){
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if (newProgress == 0){
					strTitle = null;
				}
				String text = null;
				if (strTitle != null && !strTitle.isEmpty()){
					text = strTitle;
				}else {
					text = getResources().getString(R.string.esdk_loading);
				}
				if (newProgress == 100){
					stateText.setText( text);
				}else{
					stateText.setText( text + "  " + newProgress +"%");
				}
			}
			
			@Override
			public void onReceivedTitle(WebView view, String title) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(view, title);
				strTitle = title;
				stateText.setText(strTitle);
			}
			
		});
		
		btnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		Intent i = new Intent();
//	    i.setAction("hanvon.intent.action.InputGrayModeIn");
//	    this.sendBroadcast(i);
	}
	
	@Override
	protected void onStop() {
		super.onStop();

//        Intent i = new Intent();
//        i.setAction("hanvon.intent.action.InputGrayModeOut");
//        this.sendBroadcast(i);
	};
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			setResult(RESULT_OK);
			finish();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		webview.stopLoading();
		webLayout.removeView(webview);
		webview.removeAllViews();
		webview.destroy();
		super.onDestroy();
	};
}

