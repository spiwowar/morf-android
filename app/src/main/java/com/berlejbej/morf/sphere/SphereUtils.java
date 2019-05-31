package com.berlejbej.morf.sphere;

import android.content.Context;
import android.view.MotionEvent;

import com.berlejbej.morf.Utils;

/**
 * Created by Szymon on 2016-07-08.
 */
public class SphereUtils {

    private static final String TAG = "SphereUtils";

    public static final int EMPTY_FIELD = 0;
    public static final int CREATE_NEW = 1;
    public static final int SETTINGS = 2;
    public static final int FRIENDS = 3;
    public static final int ADD_FRIENDS = 4;
    public static final int PUBLIC = 5;
    public static final int MESSAGES = 6;

    public static boolean isClickedOnSphere(Context context, MotionEvent event){

        // Checking whether clicked point is inside sphere
        final double clickedX = event.getX()/ SphereRenderer.ONE_UNIT_IN_PIXELS;
        final double clickedY = event.getY()/ SphereRenderer.ONE_UNIT_IN_PIXELS;
        final double centerX = (Utils.getScreenWidth(context)/ SphereRenderer.ONE_UNIT_IN_PIXELS)/2;
        final double centerY = (Utils.getScreenHeight(context)/ SphereRenderer.ONE_UNIT_IN_PIXELS)/2;
        final double radius = SphereRenderer.SPHERE_RADIUS;

        if ((Math.pow(clickedX - centerX, 2) + Math.pow(clickedY - centerY, 2)) < Math.pow(radius, 2)){
            return true;
        }
        else {
            return false;
        }
    }

    public static int getClickedField(Context context, MotionEvent event, double rotationX, double rotationY) {

        boolean upsideDown = false;
        final double clickedX = event.getRawX()/SphereRenderer.ONE_UNIT_IN_PIXELS;
        final double clickedY = event.getRawY()/SphereRenderer.ONE_UNIT_IN_PIXELS;
        final double centerX = (Utils.getScreenWidth(context)/SphereRenderer.ONE_UNIT_IN_PIXELS)/2;
        final double centerY = (Utils.getScreenHeight(context)/SphereRenderer.ONE_UNIT_IN_PIXELS)/2;

        double rotX = getRotation(rotationX);
        double rotY = getRotation(rotationY);

        // Checking whether sphere is upside down
        if ((rotY < 90 && rotY > -90) || (rotY > 270 && rotY < 360) || (rotY < -270 && rotY > -360)) {
            rotationX = getDegreesUpTo90(rotX);
        }
        else{
            upsideDown = true;
            rotationX = -getDegreesUpTo90(rotX);
        }

        rotationY = -getDegreesUpTo90(rotY);

        // 1/2*pi*r -> circle arc for 90 degrees
        // Sphere's dimensions mapped as a rectangle
        double flatSphereHeight = Math.PI* SphereRenderer.SPHERE_RADIUS;
        double flatSphereWidth = flatSphereHeight*2;

        // There are 8 field in a row (width of sphere), fieldSize - field width and height
        double fieldSize = flatSphereWidth / 8;

        // Angles from center of sphere to clicked points
        double clickedXAlpha = Math.toDegrees(Math.asin((centerX - clickedX) / SphereRenderer.SPHERE_RADIUS));
        double clickedYAlpha = Math.toDegrees(Math.asin((centerY - clickedY) / SphereRenderer.SPHERE_RADIUS));

        // alpha/360*2*pi*r -> circle arc
        // Clicked points mapped to 'flat sphere'
        double clickedXOnFlatSphere = clickedXAlpha/360*2*Math.PI* SphereRenderer.SPHERE_RADIUS;
        double clickedYOnFlatSphere = -clickedYAlpha/360*2*Math.PI* SphereRenderer.SPHERE_RADIUS;

        // FINAL POINTS Clicked points mapped to 'flat sphere' including rotations of the sphere
        double x = clickedXOnFlatSphere + flatSphereWidth/360*rotationX;
        double y = clickedYOnFlatSphere + flatSphereHeight/180*rotationY;

        // For CREATE_NEW and SETTINGS we don't need to check if sphere is upside down,
        // because these fields are on sphere's centre
        // Clicked on CREATE NEW field
        if (x < (fieldSize / 2) && x > (-fieldSize / 2) && y < (fieldSize / 2) && y > (-fieldSize / 2)){
            return CREATE_NEW;
        }
        // Clicked on SETTINGS field
        else if ((x < ((fieldSize / 2) + fieldSize * 2) && x > ((-fieldSize / 2) + fieldSize * 2) ||
                x < ((fieldSize / 2) - fieldSize * 2) && x > ((-fieldSize / 2) - fieldSize * 2)) &&
                y < (fieldSize / 2) && y > (-fieldSize / 2)) {
                return SETTINGS;
        }
        else {
            if (!upsideDown) {
                if (x < (fieldSize / 2 * 3) && x > (fieldSize / 2) &&  y < (fieldSize / 2 * 3) && y > (fieldSize / 2)) {
                    return PUBLIC;
                }
                if (x > (-fieldSize / 2 * 3) && x < (-fieldSize / 2) && y > (-fieldSize / 2 * 3) && y < (-fieldSize / 2)) {
                    return ADD_FRIENDS;
                }
                if (x > (-fieldSize / 2 * 3) && x < (-fieldSize / 2) && y < (fieldSize / 2 * 3) && y > (fieldSize / 2)) {
                    return MESSAGES;
                }
                if (x < (fieldSize / 2 * 3) && x > (fieldSize / 2) && y > (-fieldSize / 2 * 3) && y < (-fieldSize / 2)) {
                    return FRIENDS;
                }
            }
            else if (upsideDown) {
                if (x > (-fieldSize / 2 * 3) && x < (-fieldSize / 2) && y > (-fieldSize / 2 * 3) && y < (-fieldSize / 2)) {
                    return PUBLIC;
                }
                if (x < (fieldSize / 2 * 3) && x > (fieldSize / 2) && y < (fieldSize / 2 * 3) && y > (fieldSize / 2)) {
                    return ADD_FRIENDS;
                }
                if (x < (fieldSize / 2 * 3) && x > (fieldSize / 2) && y > (-fieldSize / 2 * 3) && y < (-fieldSize / 2)) {
                    return MESSAGES;
                }
                if (x > (-fieldSize / 2 * 3) && x < (-fieldSize / 2) && y < (fieldSize / 2 * 3) && y > (fieldSize / 2)) {
                    return FRIENDS;
                }
            }
            return EMPTY_FIELD;
        }
    }

    private static double getDegreesUpTo90(double rotationDegrees) {
        // 90 degrees is the only degrees visible on the screen (half of sphere, so 2x90 = 180)
        // so when degrees is over 90 degrees or below -90 degrees we subtract (add) 180 degrees to it
        if (rotationDegrees > 90) {
            return getDegreesUpTo90(rotationDegrees - 180);
        } else if (rotationDegrees < -90) {
            return getDegreesUpTo90(rotationDegrees + 180);
        } else {
            return rotationDegrees;
        }
    }

    private static double getRotation(double rotationDegrees) {
        // Getting rotation up to 360 degrees
        if (rotationDegrees > 360) {
            return getRotation(rotationDegrees - 360);
        } else if (rotationDegrees < -360) {
            return getRotation(rotationDegrees + 360);
        } else {
            return rotationDegrees;
        }
    }
}
