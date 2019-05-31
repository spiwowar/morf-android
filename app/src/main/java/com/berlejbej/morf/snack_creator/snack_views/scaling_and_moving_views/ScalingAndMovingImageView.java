package com.berlejbej.morf.snack_creator.snack_views.scaling_and_moving_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.berlejbej.morf.snack_creator.snack_elements.ImageElement;
import com.berlejbej.morf.snack_creator.snack_elements.SnackElement;

/**
 * Created by Szymon on 2016-02-23.
 */
public abstract class ScalingAndMovingImageView extends ImageView implements ImageElement {

    private static final String TAG = "ScalingAndMovingIV";

    private static int INVALID_POINTER_ID = 0;
    private boolean inMagneticField = false;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mLastTouchX = 0.0f;
    private float mLastTouchY = 0.0f;
    private float dx = 0.0f;
    private float dy = 0.0f;
    private float translateX = 0.0f;
    private float translateY = 0f;
    private float imageWidth = 0.0f;
    private float imageHeight = 0.0f;
    private float scaledImageWidth = 0.0f;
    private float scaledImageHeight = 0.0f;
    private float pivotX = 0.0f;
    private float pivotY = 0.0f;

    private int mActivePointerId;
    private boolean firstScale = false;
    private boolean invisibleToTouch;

    private float rotateDegrees = 0.0f;
    private float rotateToReturn = 0.0f;
    private float rotateSnapshot = 0.0f;
    private float rotateSave = 0.0f;
    private boolean rotateResume = false;
    private boolean rotating = false;

    //For overriden methods
    private int snackGroup;
    private OnTopListener onTopListener = null;
    private OnSnackElementMove onSnackElementMove = null;

    public ScalingAndMovingImageView(Context context) {
        super(context);
        Log.d(TAG, "Creating ScalingAndMovingImageView");
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        firstScale = true;
    }

    public ScalingAndMovingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Creating ScalingAndMovingImageView");
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        firstScale = true;
    }

    public ScalingAndMovingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "Creating ScalingAndMovingImageView");
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        firstScale = true;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        Log.d(TAG, "Setting image bitmap for ImageViewSnack");
        imageWidth = bm.getWidth();
        imageHeight = bm.getHeight();
        scaledImageWidth = imageWidth;
        scaledImageHeight = imageHeight;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw()");
        canvas.save();
        if (firstScale) {
            pivotX = getWidth() / 2 + translateX;
            pivotY = getHeight() / 2 + translateY;
        }
        canvas.scale(mScaleFactor, mScaleFactor, pivotX, pivotY);
        canvas.translate(translateX, translateY);
        canvas.rotate(rotateDegrees, getWidth() / 2, getHeight() / 2);

        // onDraw() code goes here
        super.onDraw(canvas);
        canvas.restore();
    }

    private boolean isInMagneticZone = false;
    private boolean leftOrRightMagnetActivated = false;
    private boolean topOrBottomMagnetActivated = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "Starting on touch event");
        // Let the ScaleGestureDetector inspect all events.
        firstScale = false;
        mScaleDetector.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                rotating = true;
                rotateResume = true;
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                onActionDown(this, x, y);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                onMove(this, x, y);
                int value = 15;

                // Calculate the distance moved
                dx = x - mLastTouchX;
                dy = y - mLastTouchY;
                if (!mScaleDetector.isInProgress()) {
                    //float result[] = SnackElementsFactory.checkMagneticField(this, value, (SnackFrameView) getParent(), dx, dy);

                    //if (result==null) {
                    //isInMagneticZone = false;
                    Log.d(TAG, "There is no magnetic field");
                    translateX = translateX + dx;
                    translateY = translateY + dy;
                    pivotX = getWidth() / 2 + translateX;
                    pivotY = getHeight() / 2 + translateY;
                    /*}
                    else if (dx >= value || dy >= value){
                        if (dx >= value){
                            Log.d(TAG, "There is no magnetic field");
                            translateX = translateX + dx;
                            pivotX = getWidth() / 2 + translateX;
                        }
                        if (dy >= value){
                            Log.d(TAG, "There is no magnetic field");
                            translateY = translateY + dy;
                            pivotY = getHeight() / 2 + translateY;
                        }
                    }
                    else {
                        Log.d(TAG, "Magnetic field has been found");
                        if (result[0] == SnackElementsFactory.LEFT_MAGNET) {
                                rotateDegrees = result[3];
                                translateX = result[1];
                                translateY = result[2];

                        } else if (result[0] == SnackElementsFactory.RIGHT_MAGNET) {
                                rotateDegrees = result[3];
                                translateX = result[1];
                                translateY = result[2];

                        } else if (result[0] == SnackElementsFactory.TOP_MAGNET) {
                                rotateDegrees = result[3];
                                translateX = result[1];
                                translateY = result[2];

                        } else if (result[0] == SnackElementsFactory.BOTTOM_MAGNET) {
                                rotateDegrees = result[3];
                                translateX = result[1];
                                translateY = result[2];
                        }
                    }
                }*/
                }




                invalidate();
                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                if (rotating){
                    rotateDegrees = rotation(ev);
                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                final float x = ev.getX();
                final float y = ev.getY();

                onActionUp(this, x, y);
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                rotating = false;
                rotateSnapshot = rotateToReturn;
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        if (rotateResume == true) {
            rotateResume = false;
            rotateSave = (float) Math.toDegrees(radians) - 90;
        }
        rotateToReturn = (float)(rotateSnapshot + Math.toDegrees(radians) - rotateSave)-90;
        return rotateToReturn;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG, "onScale()");
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            scaledImageWidth = imageWidth * mScaleFactor;
            scaledImageHeight = imageHeight * mScaleFactor;

            invalidate();
            return true;
        }
    }

    @Override
    public View getElement() {
        return this;
    }

    @Override
    public float getPositionX() {
        return translateX;
    }

    @Override
    public float getPositionY() {
        return translateY;
    }

    @Override
    public float getTouchableWidth(){
        return scaledImageWidth;
    }

    @Override
    public float getTouchableHeight(){
        return scaledImageHeight;
    }

    @Override
    public float getRotationDegrees(){
        return rotateDegrees;
    }

    @Override
    public void setSnackElementType(int snackGroup){
        this.snackGroup = snackGroup;
    }

    @Override
    public int getSnackElementType(){
        return snackGroup;
    }

    @Override
    public void setOnTop(){
        if (this.onTopListener != null) {
            this.onTopListener.setOnTop();
        }
    }

    @Override
    public void setOnTopListener(OnTopListener onTopListener){
        this.onTopListener = onTopListener;
    }

    @Override
    public void onActionDown(SnackElement snackElement, float x, float y) {
        if (this.onSnackElementMove != null){
            this.onSnackElementMove.onActionDown(snackElement, x, y);
        }
    }

    @Override
    public void onMove(SnackElement snackElement, float x, float y) {
        if (this.onSnackElementMove != null){
            this.onSnackElementMove.onMove(snackElement, x, y);
        }
    }

    @Override
    public void onActionUp(SnackElement snackElement, float x, float y) {
        if (this.onSnackElementMove != null){
            this.onSnackElementMove.onActionUp(snackElement, x, y);
        }
    }

    @Override
    public void setOnSnackElementMove(OnSnackElementMove onSnackElementMove) {
        this.onSnackElementMove = onSnackElementMove;

    }

    @Override
    public boolean getInvisibleToTouch() {
        return invisibleToTouch;
    }

    @Override
    public void setInvisibleToTouch(boolean invisible) {
        this.invisibleToTouch = invisible;
    }

    @Override
    public boolean isRemovable() {
        return true;
    }
}
