package com.berlejbej.morf.snack_creator;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.berlejbej.morf.utils.Values;

import java.util.Calendar;

/**
 * Created by Szymon on 2016-07-21.
 */
public class Icon extends ImageView{

    private static final String TAG = "Icon";

    public static final String ICON_CAMERA = "ICON_CAMERA";
    public static final String ICON_TEXT = "ICON_TEXT";
    public static final String ICON_GALLERY = "ICON_GALLERY";
    public static final String ICON_DRAWING = "ICON_DRAWING";
    public static final String ICON_MUSIC = "ICON_MUSIC";
    public static final String ICON_RUBBER = "ICON_RUBBER";
    public static final String ICON_ERASER = "ICON_ERASER";
    public static final String ICON_OPENED_BIN = "ICON_OPENED_BIN";
    public static final String ICON_CLOSED_BIN = "ICON_CLOSED_BIN";

    private OnClickListener onClickListener = null;
    private double degrees = 0.0f;
    private boolean onFront = false;
    private String name;
    private double distanceFromCenter;
    private boolean isMiniature = false;

    public Icon(Context context) {
        super(context);
    }

    public Icon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Icon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDegrees(double degrees){
        this.degrees = degrees;
    }

    public double getDegrees(){
        return degrees;
    }

    public boolean getOnFront(){
        return this.onFront;
    }

    public void setOnFront(boolean onFront){
        this.onFront = onFront;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setDistanceFromCenter(double distanceFromCenter){
        this.distanceFromCenter = distanceFromCenter;
    }

    public boolean isMiniature(){
        return isMiniature;
    }

    public void setMiniature(boolean isMiniature){
        this.isMiniature = isMiniature;
    }

    public double getDistanceFromCenter(){
        return distanceFromCenter;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Checking whether the touch is more than MAX_TOUCH_TIME on ACTION_UP.
        // If so, the event is triggered, else return false;
        Log.d(TAG, "Hierarchy 5");
        Log.d(TAG, "Icon " + getName() + " has been clicked.");
        if (actionPerformListener != null && getOnFront()){
            Log.d(TAG, "Performing action for Icon: " + getName());
            actionPerformListener.performAction();
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        this.onClickListener = l;
    }

    public OnClickListener getOnClickListener(){
        return this.onClickListener;
    }

    private ActionPerformListener actionPerformListener = null;

    public void setActionPerformListener(ActionPerformListener actionPerformListener){
        Log.d(TAG, "Setting action perform listener for Icon: " + getName());
        this.actionPerformListener = actionPerformListener;
    }

    public ActionPerformListener getActionPerformListener(){
        return this.actionPerformListener;
    }

    interface ActionPerformListener{
        void performAction();
    }
}
