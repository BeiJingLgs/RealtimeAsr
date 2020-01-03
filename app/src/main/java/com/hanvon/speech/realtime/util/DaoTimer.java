package com.hanvon.speech.realtime.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.baidu.ai.speech.realtime.R;


/**xufei
 * Created by Administrator on 2018/9/30.
 */

public class DaoTimer extends CountDownTimer {
    public final static int MAX_S = 60 * 1000;
    public  final static int COUNT_S = 1000;
    private Context context;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        private TextView textView;
        public DaoTimer(long millisInFuture, long countDownInterval, TextView textView, Context context) {
            super(millisInFuture, countDownInterval);
            this.textView=textView;
            this.context=context;
        }
        @Override
        public void onTick(long millisUntilFinished) {
            textView.setText(millisUntilFinished / COUNT_S + "s");
            textView.setClickable(false);
            textView.setFocusable(false);
        }
        @Override
        public void onFinish() {
            textView.setText(context.getResources().getString(R.string.get_again));
            textView.setClickable(true);
            textView.setFocusable(true);
        }


}
