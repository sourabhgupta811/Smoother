package com.samnetworks.smoother;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Sourabh Gupta on 16/4/20.
 */
public class DimensionUtils {
    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
