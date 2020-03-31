package com.hanvon.speech.realtime.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
   private static Toast mToast;
   public static void show(Context context, CharSequence text) {

      if(mToast == null) {
         mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
      } else {
         mToast.setText(text);
         mToast.setDuration(Toast.LENGTH_SHORT);
      }
      mToast.show();
   }
 
   public static void showLong(Context context, CharSequence text) {

      if(mToast == null) {
         mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
      } else {
         mToast.setText(text);
         mToast.setDuration(Toast.LENGTH_LONG);
      }
      mToast.show();
   }

   public static void cancelToast() {
      if (mToast != null) {
         mToast.cancel();
      }
   }
}