package com.berlejbej.morf.sphere;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.berlejbej.morf.R;
import com.berlejbej.morf.Utils;
import com.berlejbej.morf.snack_creator.SnackCreatorActivity;

import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.AnimationQueue;
import org.rajawali3d.animation.ScaleAnimation3D;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AlphaMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;

/**
 * Created by Szymon on 2016-07-08.
 */
public class SphereRenderer extends RajawaliRenderer {

    private static final String TAG = "SphereRenderer";

    private static final float CAMERA_Z = 3.5f;
    private static final int SPHERE_SEGMENTATION = 50;
    public static double SPHERE_RADIUS;
    public static double SCREEN_WIDTH;
    public static double SCREEN_HEIGHT;
    public static double ONE_PIXEL_IN_UNIT;
    public static double ONE_UNIT_IN_PIXELS;
    private static final int FADE_OUT_ANIMATION_DURATION = 700;

    public Context context;

    private DirectionalLight directionalLight;
    private Sphere planetSphere;
    private Plane backgroundPlane;
    private Plane fingerPlane;
    float rotatedValueX = 0.0f;
    float rotatedValueY = 0.0f;

    public SphereRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene(){

        setScreenDimensionsInOpenGlSize();

        addLight();
        createFingerPlane();
        createBackgroundPlane();
        createPlanetSphere();
    }

    private void setScreenDimensionsInOpenGlSize(){
        final float width = Utils.getScreenWidth(context);
        final float height = Utils.getScreenHeight(context);

        final double cameraFieldDistance = (Math.tan(0.5 * Math.toRadians(getCurrentCamera().getFieldOfView())));
        final double cameraDistanceZ = CAMERA_Z;
        SCREEN_HEIGHT = cameraFieldDistance * cameraDistanceZ * 2;
        final double aspectRatio = (double) width / (double) height;

        if (getContext().getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            SCREEN_WIDTH = SCREEN_HEIGHT / aspectRatio;
        else
            SCREEN_WIDTH = SCREEN_HEIGHT * aspectRatio;

        ONE_UNIT_IN_PIXELS = width / SCREEN_WIDTH;
        ONE_PIXEL_IN_UNIT = SCREEN_WIDTH / width;
    }

    private void addLight(){
        directionalLight = new DirectionalLight(0.7f, -0.5f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(1.5f);
        getCurrentScene().addLight(directionalLight);
    }

    private void createFingerPlane(){
        Material fingerMaterial = new Material();
        fingerMaterial.enableLighting(false);
        fingerMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        fingerMaterial.setColor(Color.WHITE);

        AlphaMapTexture fingerTexture = new AlphaMapTexture("Finger", R.drawable.finger);

        try {
            fingerMaterial.addTexture(fingerTexture);
        }
        catch (ATexture.TextureException error){
            Log.d(TAG, "TEXTURE ERROR");
        }

        float fingerPlaneWidth = (float)SCREEN_WIDTH / 2.5f;

        fingerPlane = new Plane(fingerPlaneWidth, fingerPlaneWidth/3.5f, 1, 1, 1);
        fingerPlane.setMaterial(fingerMaterial);
        fingerPlane.setDoubleSided(true);
        fingerPlane.moveUp(-1.2);
        fingerPlane.setRotY(180);
        getCurrentScene().addChild(fingerPlane);
    }

    private void createBackgroundPlane(){
        Material backgroundMaterial = new Material();
        backgroundMaterial.enableLighting(false);
        backgroundMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        backgroundMaterial.setColor(0);
        backgroundMaterial.setColor(Color.BLACK);

        Texture backgroundTexture = new Texture("Background", R.drawable.main_background);
        try{
            backgroundMaterial.addTexture(backgroundTexture);

        } catch (ATexture.TextureException error){
            Log.d(TAG, "TEXTURE ERROR");
        }

        backgroundPlane = new Plane((float)SCREEN_WIDTH, (float)SCREEN_HEIGHT, 1, 1, 1);
        backgroundPlane.setDoubleSided(true);
        backgroundPlane.setMaterial(backgroundMaterial);
        getCurrentScene().addChild(backgroundPlane);
        backgroundPlane.setRotY(180);
    }

    private void createPlanetSphere(){
        Material planetMaterial = new Material();
        planetMaterial.enableLighting(true);
        planetMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        planetMaterial.setColor(0);
        planetMaterial.setColor(Color.TRANSPARENT);

        Texture planetTexture = new Texture("Planet", R.drawable.sphere);

        try {
            planetMaterial.addTexture(planetTexture);
        }
        catch (ATexture.TextureException error){
            Log.d(TAG, "TEXTURE ERROR");
        }

        SPHERE_RADIUS = SCREEN_WIDTH / 2.7;

        planetSphere = new Sphere((float)SPHERE_RADIUS, SPHERE_SEGMENTATION, SPHERE_SEGMENTATION);
        planetSphere.setMaterial(planetMaterial);
        getCurrentScene().addChild(planetSphere);
        getCurrentCamera().setZ(CAMERA_Z);
    }

    public void setRotationAngle(float rotationAngleX, float rotationAngleY){
        planetSphere.setRotation(0, -rotationAngleY, -rotationAngleX);
        rotatedValueX = rotationAngleX;
        rotatedValueY = rotationAngleY;
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
    }

    public void clickedOnField(int clickedField){
        switch (clickedField){
            case SphereUtils.CREATE_NEW: {
                Log.d(TAG, "CREATE_NEW");

                Intent intent = new Intent(context, SnackCreatorActivity.class);

                sphereOutAnimation(intent);
                break;
            }
            case SphereUtils.SETTINGS: {
                Log.d(TAG, "SETTINGS");
                break;
            }
            case SphereUtils.FRIENDS: {
                Log.d(TAG, "FRIENDS");
                break;
            }
            case SphereUtils.ADD_FRIENDS: {
                Log.d(TAG, "ADD_FRIENDS");
                break;
            }
            case SphereUtils.PUBLIC: {
                Log.d(TAG, "PUBLIC");
                break;
            }
            case SphereUtils.MESSAGES: {
                Log.d(TAG, "MESSAGES");
                break;
            }
        }
    }

    private void sphereOutAnimation(Intent intent){
        // Translation animation
        Vector3 translateAnimationVector = new Vector3(0, -0.5*Utils.getScreenHeight(context)*ONE_PIXEL_IN_UNIT, 0);
        Animation3D translateAnimation = new TranslateAnimation3D(translateAnimationVector);
        translateAnimation.setInterpolator(new AccelerateInterpolator(2f));
        translateAnimation.setDurationMilliseconds(FADE_OUT_ANIMATION_DURATION);
        translateAnimation.setTransformable3D(planetSphere);

        // Scale animation
        Vector3 scaleAnimationVector = new Vector3(0,0,0);
        Animation3D scaleAnimation = new ScaleAnimation3D(scaleAnimationVector);
        scaleAnimation.setInterpolator(new AccelerateInterpolator(2f));
        scaleAnimation.setDurationMilliseconds(FADE_OUT_ANIMATION_DURATION);
        scaleAnimation.setTransformable3D(planetSphere);

        getCurrentScene().registerAnimation(translateAnimation);
        getCurrentScene().registerAnimation(scaleAnimation);

        AnimationQueue animationQueue = new AnimationQueue();
        animationQueue.addAnimation(translateAnimation);
        animationQueue.addAnimation(scaleAnimation);
        animationQueue.play();

        while (true) {
            if (translateAnimation.isEnded()) {
                context.startActivity(intent);
                break;
            }
        }
    }


    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {}

    @Override
    public void onTouchEvent(MotionEvent event) {}
}