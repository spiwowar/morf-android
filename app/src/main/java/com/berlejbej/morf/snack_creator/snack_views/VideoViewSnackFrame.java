package com.berlejbej.morf.snack_creator.snack_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.berlejbej.morf.snack_creator.snack_views.scaling_and_moving_views.SnackVideo;

/**
 * Created by Szymon on 2016-03-15.
 */
public class VideoViewSnackFrame extends SnackVideo {

    public VideoViewSnackFrame(Context context) {
        super(context);
    }

    public VideoViewSnackFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }
}
