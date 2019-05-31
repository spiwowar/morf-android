package com.berlejbej.morf;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Szymon on 2016-07-08.
 */
public class Utils {

    private static float screenWidth;
    private static float screenHeight;

    public static float getScreenWidth(Context context){
        if (screenWidth != 0.0f){
            return screenWidth;
        }
        else {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            return screenWidth;
        }
    }

    public static float getScreenHeight(Context context){
        if (screenHeight != 0.0f){
            return screenHeight;
        }
        else {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
            return screenHeight;
        }
    }
}
