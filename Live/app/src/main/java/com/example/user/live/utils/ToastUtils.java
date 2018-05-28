package com.example.user.live.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.live.R;

public class ToastUtils {

    public static Toast toast;

    public ToastUtils() {
    }

    public static void showToast(Context context, String text) {
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.qupai_common_toast_default_layout, null, false);
        TextView message = (TextView) toastRoot.findViewById(R.id.toast_info);
        message.setText(text);
        if (toast != null) {
            toast.cancel();
            toast = null;
        }


        toast = new Toast(context);
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(Context context, int resID) {
        showToast(context, context.getString(resID));
    }


    /**
     * dp --> px
     */
    public static int dpToPx(Resources res, float f) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f, res.getDisplayMetrics());
    }
    public static int Px2Dp(Resources res, float px) {
        final float scale = res.getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

}
