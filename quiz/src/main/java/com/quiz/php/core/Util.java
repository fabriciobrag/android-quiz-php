package com.quiz.php.core;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by fabricio on 3/31/14.
 */
public class Util {

    public static int dpToPx (Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
