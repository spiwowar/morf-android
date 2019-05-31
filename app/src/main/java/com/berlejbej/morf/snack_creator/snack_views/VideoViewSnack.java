package com.berlejbej.morf.snack_creator.snack_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.VideoView;

/**
 * Created by Szymon on 2016-03-15.
 */
public class VideoViewSnack extends VideoView {

    public VideoViewSnack(Context context) {
        super(context);
    }

    public VideoViewSnack(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }
}
