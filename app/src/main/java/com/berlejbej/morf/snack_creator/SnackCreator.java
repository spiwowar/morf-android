package com.berlejbej.morf.snack_creator;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.berlejbej.morf.snack_creator.snack_elements.SnackElement;
import com.berlejbej.morf.snack_creator.snack_views.DrawingViewSnack;
import com.berlejbej.morf.utils.ViewRegionCheck;

import com.berlejbej.morf.snack_creator.snack_elements.SnackElement.OnSnackElementMove;

/**
 * Created by Szymon on 2016-05-05.
 */
public class SnackCreator extends RelativeLayout {

    private static final String TAG = "SnackCreator";

    private SnackFrame snackFrame;
    private SnackCreatorController snackCreatorController;
    private Context context;
    private OnSnackElementMove onSnackMoveListener = onSnackMoveListener();

    public SnackCreator(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SnackCreator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SnackCreator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {

        RelativeLayout.LayoutParams mainLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(mainLayoutParams);

        snackCreatorController = new SnackCreatorController(context);

        RelativeLayout.LayoutParams snackFrameLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        snackFrame = new SnackFrame(context);
        snackFrame.setLayoutParams(snackFrameLayoutParams);
        snackFrame.setOnSnackMoveListener(onSnackMoveListener);

        setChildrenDrawingCacheEnabled(true);

        addView(snackFrame);
        addView(snackCreatorController);
    }

    public void hideIcons(){
        snackCreatorController.hideIcons();
    }

    public void setOnCodeReceivedListener(SnackCreatorController.OnCodeReceivedListener onCodeReceivedListener) {
        snackCreatorController.setOnCodeReceivedListener(onCodeReceivedListener);
    }

    public void addImageSnackElement(String imagePath) {
        snackFrame.addImageSnackElement(imagePath);
    }

    public void addVideoSnackElement(Uri videoUri) {
        snackFrame.addVideoSnackElement(videoUri);
    }

    private void removeSnackElement(SnackElement snackElement){
        snackFrame.removeSnackElement(snackElement);
    }

    public DrawingViewSnack getDrawingView(){
        if (snackFrame != null) {
            return snackFrame.getDrawingView();
        }
        else {
            return null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // If we touched the snack creator controller, then we do nothing (let children take care of event)
        // otherwise, we hideIcons
        Log.d(TAG, "Hierarchy 1");
        int action = ev.getAction();
        if (!ViewRegionCheck.inRegion(ev.getX(), ev.getY(), snackCreatorController)) {
            if (action == MotionEvent.ACTION_DOWN && !snackCreatorController.iconsHidden()) {
                snackCreatorController.hideIcons();
            }
        }
        return false;
    }

    private OnSnackElementMove onSnackMoveListener(){
        return new OnSnackElementMove() {
            @Override
            public void onActionDown(SnackElement snackElement, float x, float y) {
                Log.d(TAG, "OnSnackElementMove listener - ACTION_DOWN");
                if (snackElement.isRemovable()){
                    snackCreatorController.showBin();
                }
                snackCreatorController.hideIcons();
            }

            @Override
            public void onActionUp(SnackElement snackElement, float x, float y) {
                Log.d(TAG, "OnSnackElementMove listener - ACTION_UP");
                if (snackElement.isRemovable() && snackCreatorController.isBinOpened()){
                    removeSnackElement(snackElement);
                    snackCreatorController.hideBin();
                    snackCreatorController.showHiddenIcons();
                }
                else if (snackElement.isRemovable() && snackCreatorController.isBinShowed()){
                    snackCreatorController.hideBin();
                    snackCreatorController.hideIcons();
                }
            }

            @Override
            public void onMove(SnackElement snackElement, float x, float y) {
                Log.d(TAG, "OnSnackElementMove listener - ACTION_MOVE");
                View sphereView = snackCreatorController.getSphereView();
                if (snackElement.isRemovable() && sphereView != null && ViewRegionCheck.inRegion(x, y, sphereView)){
                    snackCreatorController.openBin();
                }
                else if (snackElement.isRemovable() && snackCreatorController.isBinOpened()){
                    snackCreatorController.closeBin();
                }
            }
        };
    }
}
