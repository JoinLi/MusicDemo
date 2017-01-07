package com.li.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/12/28.
 */
public class ToastUtil {


    private static Toast toast;

    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }


}
