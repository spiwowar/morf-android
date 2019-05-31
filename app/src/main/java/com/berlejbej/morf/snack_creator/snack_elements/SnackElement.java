package com.berlejbej.morf.snack_creator.snack_elements;

import android.view.View;

/**
 * Created by Szymon on 2016-02-26.
 */
public interface SnackElement {

    View getElement();

    float getPositionX();

    float getPositionY();

    float getTouchableWidth();

    float getTouchableHeight();

    float getRotationDegrees();

    void setSnackElementType(int snackElementType);

    int getSnackElementType();

    void setInvisibleToTouch(boolean invisible);

    boolean getInvisibleToTouch();

    boolean isRemovable();

    void setOnTop();

    void setOnTopListener(OnTopListener onTopListener);

    interface OnTopListener{
        void setOnTop();
    }

    void onActionDown(SnackElement snackElement, float x, float y);

    void onMove(SnackElement snackElement, float x, float y);

    void onActionUp(SnackElement snackElement, float x, float y);

    void setOnSnackElementMove(OnSnackElementMove onSnackElementMove);

    interface OnSnackElementMove{
        void onActionDown(SnackElement snackElement, float x, float y);
        void onMove(SnackElement snackElement, float x, float y);
        void onActionUp(SnackElement snackElement, float x, float y);
    }
}
