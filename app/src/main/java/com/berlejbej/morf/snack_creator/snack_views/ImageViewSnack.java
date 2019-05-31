package com.berlejbej.morf.snack_creator.snack_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.berlejbej.morf.snack_creator.snack_views.scaling_and_moving_views.ScalingAndMovingImageView;

/**
 * Created by Szymon on 2016-02-21.
 */
public class ImageViewSnack extends ScalingAndMovingImageView {

    public ImageViewSnack(Context context) {
        super(context);
    }

    public ImageViewSnack(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewSnack(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }
}