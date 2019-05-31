package com.berlejbej.morf.sphere;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.rajawali3d.surface.IRajawaliSurfaceRenderer;
import org.rajawali3d.surface.RajawaliSurfaceView;

import java.util.Calendar;

/**
 * Created by Szymon on 2016-07-08.
 */
public class SphereView extends RajawaliSurfaceView{

    private final static double DISTANCE_TO_ANGLE_FRICTION = Math.PI;
    private final int MIN_CLICK_DURATION = 50;
    private final int MAX_CLICK_DURATION = 200;

    private SphereRenderer renderer;
    private long startClickTime = 0;
    private float rotationAngleX = 0.0f;
    private float rotationAngleY = 0.0f;
    private static int INVALID_POINTER_ID = 0;
    private int activePointerId;
    private float lastTouchX = 0.0f;
    private float lastTouchY = 0.0f;
    private float dx = 0.0f;
    private float dy = 0.0f;

    public SphereView(Context context) {
        super(context);
    }

    public SphereView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSurfaceRenderer(IRajawaliSurfaceRenderer renderer) throws IllegalStateException {
        super.setSurfaceRenderer(renderer);
        this.renderer = (SphereRenderer) renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        final int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                startClickTime = Calendar.getInstance().getTimeInMillis();

                // Remember where we started (for dragging)
                lastTouchX = x;
                lastTouchY = y;
                // Save the ID of this pointer (for dragging)
                activePointerId = MotionEventCompat.getPointerId(event, 0);
                break;
            }
            case MotionEvent.ACTION_UP: {
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION) {
                    if (SphereUtils.isClickedOnSphere(getContext(), event)) {
                        int clickedField = SphereUtils.getClickedField(getContext(), event, rotationAngleY, rotationAngleX);
                        if (clickedField != SphereUtils.EMPTY_FIELD){
                            renderer.clickedOnField(clickedField);
                        }
                    }
                }
                activePointerId = INVALID_POINTER_ID;
                startClickTime = 0;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerId);
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                // Calculate the distance moved
                if (clickDuration > MIN_CLICK_DURATION) {
                    dx = x - lastTouchX;
                    dy = y - lastTouchY;

                    rotationAngleX += dy / DISTANCE_TO_ANGLE_FRICTION;
                    rotationAngleY += dx / DISTANCE_TO_ANGLE_FRICTION;
                    this.renderer.setRotationAngle(rotationAngleX, rotationAngleY);

                    // Remember this touch position for the next move event
                    lastTouchX = x;
                    lastTouchY = y;
                }

                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                if (pointerId == activePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                    lastTouchY = MotionEventCompat.getY(event, newPointerIndex);
                    activePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                activePointerId = INVALID_POINTER_ID;
                break;
            }
        }
        return true;
    }
}
