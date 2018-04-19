package cz.matyapav.valentinesday;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @author Pavel Matyáš (matyapav@fel.cvut.cz).
 * @since 1.0.0..
 */

public class Utils {

    public static int convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(dp);
    }

    public static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }
}
