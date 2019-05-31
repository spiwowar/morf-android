package com.berlejbej.morf.utils;

import android.view.View;

import com.berlejbej.morf.snack_creator.snack_elements.SnackElement;


/**
 * Created by Szymon on 2016-04-14.
 */
public class ViewRegionCheck {

    private static final String TAG = "ViewRegionCheck";
    public static int[] mCoordBuffer = new int[2];

    public static boolean inRegion(float x, float y, View v) {
        v.getLocationOnScreen(mCoordBuffer);
        return mCoordBuffer[0] + v.getWidth() > x &&    // right edge
                mCoordBuffer[1] + v.getHeight() > y &&   // bottom edge
                mCoordBuffer[0] < x &&                   // left edge
                mCoordBuffer[1] < y;                     // top edge
    }

    public static boolean inSnackElementRegion(float x, float y, float width, float height, SnackElement snackElement){
        float xPlusCoord = snackElement.getPositionX() + snackElement.getTouchableWidth() / 2;
        float yPlusCoord = snackElement.getPositionY() + snackElement.getTouchableHeight() / 2;
        float xMinusCoord = snackElement.getPositionX() - snackElement.getTouchableWidth() / 2;
        float yMinusCoord = snackElement.getPositionY() - snackElement.getTouchableHeight() / 2;
        float touchXCoord = x - width / 2;
        float touchYCoord = y - height / 2;
        float degree = snackElement.getRotationDegrees();

        // Rotate touched point with degree of rotated element.
        // The center point of rotation is in center of snack element.
        double xPrim = (touchXCoord - snackElement.getPositionX())*Math.cos(Math.toRadians(-degree))
                - (touchYCoord - snackElement.getPositionY())*Math.sin(Math.toRadians(-degree))
                + snackElement.getPositionX();
        double yPrim = (touchXCoord - snackElement.getPositionX())*Math.sin(Math.toRadians(-degree))
                + (touchYCoord - snackElement.getPositionY())*Math.cos(Math.toRadians(-degree))
                + snackElement.getPositionY();

        return (xPrim <= xPlusCoord && xPrim >= xMinusCoord) &&
                (yPrim <= yPlusCoord && yPrim >= yMinusCoord);
    }
/*
    public static boolean inInsideMagneticSnackRegion(float x, float y, SnackElement snackElement){
        float xPlusCoord = snackElement.getPositionX() + snackElement.getTouchableWidth() / 2;
        float yPlusCoord = snackElement.getPositionY() + snackElement.getTouchableHeight() / 2;
        float xMinusCoord = snackElement.getPositionX() - snackElement.getTouchableWidth() / 2;
        float yMinusCoord = snackElement.getPositionY() - snackElement.getTouchableHeight() / 2;
        float touchXCoord = x;
        float touchYCoord = y;
        float degree = snackElement.getRotationDegrees();

        // Rotate touched point with degree of rotated element.
        // The center point of rotation is in center of snack element.
        double xPrim = (touchXCoord - snackElement.getPositionX())*Math.cos(Math.toRadians(-degree))
                - (touchYCoord - snackElement.getPositionY())*Math.sin(Math.toRadians(-degree))
                + snackElement.getPositionX();
        double yPrim = (touchXCoord - snackElement.getPositionX())*Math.sin(Math.toRadians(-degree))
                + (touchYCoord - snackElement.getPositionY())*Math.cos(Math.toRadians(-degree))
                + snackElement.getPositionY();

        return (xPrim <= xPlusCoord && xPrim >= xMinusCoord) &&
                (yPrim <= yPlusCoord && yPrim >= yMinusCoord);
    }*/
}
