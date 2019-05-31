package com.berlejbej.morf.snack_creator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.berlejbej.morf.R;
import com.berlejbej.morf.Utils;
import com.berlejbej.morf.snack_creator.Icon.ActionPerformListener;
import com.berlejbej.morf.utils.Code;
import com.berlejbej.morf.utils.Values;
import com.berlejbej.morf.utils.ViewRegionCheck;

import java.util.Calendar;

/**
 * Created by Szymon on 2016-05-05.
 */
public class SnackCreatorController extends RelativeLayout {

    private static final String TAG = "SnackCreatorController";

    // 158.6 - width of sphere in image
    // 250 - width of image
    private final float SPHERE_IN_IMAGE_RATIO = (float) (250 / 158.6);
    private final float ICONS_SIZE_FRICTION = 2.5f;
    private final float ORBIT_TO_SPHERE_WIDTH_COEFFICIENT = 2.5f;

    private ActionPerformListener cameraClickListener = cameraClickListener();
    private ActionPerformListener drawingClickListener = drawingClickListener();
    private ActionPerformListener textClickListener = textClickListener();
    private ActionPerformListener galleryClickListener = galleryClickListener();
    private ActionPerformListener musicClickListener = musicClickListener();
    private ActionPerformListener rubberClickListener = rubberClickListener();
    private ActionPerformListener eraserClickListener = eraserClickListener();
    private OnTouchListener sphereTouchListener = sphereTouchListener();

    private Icons icons;
    private Context context;
    private Code code = new Code();
    private ImageView sphereView = null;
    private OnCodeReceivedListener onCodeReceivedListener = null;

    private float sphereYPositionDown = 0;
    private float sphereYPositionUp = 0;
    boolean firstIconsShowing = true;
    boolean firstAnimationStart = true;

    public SnackCreatorController(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SnackCreatorController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SnackCreatorController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){

        addToParent();

        // Adding sphere image
        RelativeLayout.LayoutParams sphereLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sphereLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        final ImageView sphereImage = new ImageView(context);
        sphereView = sphereImage;
        Drawable sphereDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.snack_controller_sphere, null);
        sphereImage.setImageDrawable(sphereDrawable);
        sphereImage.setLayoutParams(sphereLayoutParams);
        sphereImage.setOnTouchListener(sphereTouchListener);
        sphereImage.setAdjustViewBounds(true);
        addView(sphereImage);

        // Adding icons images
        Drawable camera = ResourcesCompat.getDrawable(getResources(), R.drawable.aparat, null);
        Drawable drawing = ResourcesCompat.getDrawable(getResources(), R.drawable.pencil, null);
        Drawable gallery = ResourcesCompat.getDrawable(getResources(), R.drawable.pictures, null);
        Drawable text = ResourcesCompat.getDrawable(getResources(), R.drawable.text, null);
        Drawable music = ResourcesCompat.getDrawable(getResources(), R.drawable.music, null);
        Drawable rubber = ResourcesCompat.getDrawable(getResources(), R.drawable.rubber, null);
        Drawable eraser = ResourcesCompat.getDrawable(getResources(), R.drawable.eraser, null);

        // Adding icons
        icons = new Icons(context);
        Icon cameraIcon = icons.addIcon(camera, Icon.ICON_CAMERA);
        Icon drawingIcon = icons.addIcon(drawing, Icon.ICON_DRAWING);
        Icon galleryIcon = icons.addIcon(gallery, Icon.ICON_GALLERY);
        Icon textIcon = icons.addIcon(text, Icon.ICON_TEXT);
        Icon musicIcon = icons.addIcon(music, Icon.ICON_MUSIC);

        cameraIcon.setActionPerformListener(cameraClickListener);
        drawingIcon.setActionPerformListener(drawingClickListener);
        galleryIcon.setActionPerformListener(galleryClickListener);
        textIcon.setActionPerformListener(textClickListener);
        musicIcon.setActionPerformListener(musicClickListener);

        // Adding subIcons
        final Icons drawableSubIcons = icons.createSubIcons(drawingIcon);
        Icon rubberIcon = drawableSubIcons.addIcon(rubber, Icon.ICON_RUBBER);
        Icon eraserIcon = drawableSubIcons.addIcon(eraser, Icon.ICON_ERASER);

        rubberIcon.setActionPerformListener(rubberClickListener);
        eraserIcon.setActionPerformListener(eraserClickListener);

        icons.update();
        drawableSubIcons.update();

        addView(icons);
        addView(drawableSubIcons);

        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (firstIconsShowing) {
                    firstIconsShowing = false;
                    final float screenHeight = Utils.getScreenHeight(getContext());

                    // Sphere width on screen
                    int sphereWidthOnScreen = (int) (sphereImage.getHeight() / SPHERE_IN_IMAGE_RATIO);
                    int controllerHeight = SnackCreatorController.this.getHeight();
                    float iconFullSize = controllerHeight / ICONS_SIZE_FRICTION;

                    sphereYPositionDown = getY();
                    sphereYPositionUp = (screenHeight - getY() - getHeight());

                    icons.setIconsFullSize(iconFullSize);
                    drawableSubIcons.setIconsFullSize(iconFullSize);
                    icons.getLayoutParams().height = (int) iconFullSize;
                    icons.setOrbitWidth(sphereWidthOnScreen * ORBIT_TO_SPHERE_WIDTH_COEFFICIENT);
                    drawableSubIcons.getLayoutParams().height = (int) iconFullSize;
                    drawableSubIcons.setOrbitWidth(sphereWidthOnScreen * ORBIT_TO_SPHERE_WIDTH_COEFFICIENT);
                    icons.showIcons();
                    drawableSubIcons.showIcons();
                }
            }
        });
    }

    private void addToParent(){
        final float screenHeight = Utils.getScreenHeight(getContext());

        setClipChildren(false);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.height = (int)(screenHeight / 5);
        setLayoutParams(layoutParams);
        setVisibility(INVISIBLE);
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (firstAnimationStart) {
                    startSphereAnimation(screenHeight);
                    firstAnimationStart = false;
                }
            }
        });
    }

    private void startSphereAnimation(float screenHeight){
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, screenHeight - getY(), 0);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new AnticipateOvershootInterpolator(2));
        animationSet.setFillAfter(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        translateAnimation.setDuration(1000);
        scaleAnimation.setDuration(1000);
        animationSet.setDuration(1000);
        setAnimation(animationSet);
        animate();
        setVisibility(VISIBLE);
    }

    public View getSphereView(){
        return sphereView;
    }

    public void hideIcons(){
        icons.hideIconsOutsideSphere();
    }

    public void showHiddenIcons(){
        icons.showHiddenIconsOutsideSphere();
    }

    public void showBin(){
        icons.showBin();
    }

    public void hideBin(){
        icons.hideBin();
    }

    public void openBin(){
        icons.openBin();
    }

    public void closeBin(){
        icons.closeBin();
    }

    public boolean isBinOpened(){
        return icons.isBinOpened();
    }

    public boolean isBinShowed(){
        return icons.isBinShowed();
    }

    public boolean iconsHidden(){
        return icons.iconsOutsideSphereHidden();
    }

    public void setOnCodeReceivedListener(OnCodeReceivedListener onCodeReceivedListener){
        Log.d(TAG, "Setting OnCodeReceivedListener");
        this.onCodeReceivedListener = onCodeReceivedListener;
    }

    interface OnCodeReceivedListener{
        void onCodeReceived(Code code);
    }

    private void sendCode(int codeName){
        if (onCodeReceivedListener != null){
            code.setCode(codeName);
            onCodeReceivedListener.onCodeReceived(code);
        }
    }

    private ActionPerformListener cameraClickListener() {
        return new ActionPerformListener() {
            @Override
            public void performAction() {
                sendCode(Code.CAMERA);
            }
        };
    }

    private ActionPerformListener galleryClickListener() {
        return new ActionPerformListener() {
            @Override
            public void performAction() {
                sendCode(Code.GALLERY);
            }
        };
    }

    private ActionPerformListener drawingClickListener() {
        return new ActionPerformListener() {
            @Override
            public void performAction() {
                sendCode(Code.DRAWING);
            }
        };
    }

    private ActionPerformListener rubberClickListener() {
        return new ActionPerformListener() {
            @Override
            public void performAction() {
                sendCode(Code.RUBBER);
            }
        };
    }

    private ActionPerformListener eraserClickListener() {
        return new ActionPerformListener() {
            @Override
            public void performAction() {
                sendCode(Code.ERASER);
            }
        };
    }

    private ActionPerformListener textClickListener() {
        return new ActionPerformListener() {
            @Override
            public void performAction() {
            }
        };
    }

    private ActionPerformListener musicClickListener() {
        return new ActionPerformListener() {
            @Override
            public void performAction() {
            }
        };
    }

    private long startSphereClickTime = 0;
    private float dxSphere = 0.0f;
    private float dySphere = 0.0f;
    private float lastTouchXSphere = 0.0f;
    private float lastTouchYSphere = 0.0f;
    private int activePointerIdSphere;
    private int INVALID_POINTER_ID_SPHERE = 0;
    private boolean movingSphere = false;

    private OnTouchListener sphereTouchListener(){
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "Hierarchy 2");
                sendCode(Code.CONTROLLER_CLICKED);
                if (iconsHidden()) {
                    final int action = MotionEventCompat.getActionMasked(event);
                    switch (action) {
                        case MotionEvent.ACTION_DOWN: {
                            startSphereClickTime = Calendar.getInstance().getTimeInMillis();

                            final int pointerIndex = MotionEventCompat.getActionIndex(event);
                            final float x = MotionEventCompat.getX(event, pointerIndex);
                            final float y = MotionEventCompat.getY(event, pointerIndex);

                            startSphereClickTime = Calendar.getInstance().getTimeInMillis();

                            // Remember where we started (for dragging)
                            lastTouchXSphere = x;
                            lastTouchYSphere = y;
                            // Save the ID of this pointer (for dragging)
                            activePointerIdSphere = MotionEventCompat.getPointerId(event, 0);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startSphereClickTime;
                            startSphereClickTime = 0;

                            Log.d(TAG, "Click duration: " + clickDuration);
                            if (clickDuration < Values.MIN_SPHERE_TOUCH_TIME_TO_HIDE_ICONS) {
                                showHiddenIcons();
                            }
                            if (movingSphere) {
                                Log.d(TAG, "Moving Sphere");
                                movingSphere = false;
                                translateSphere();
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startSphereClickTime;
                            // Find the index of the active pointer and fetch its position
                            final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerIdSphere);

                            final float x = MotionEventCompat.getX(event, pointerIndex);
                            final float y = MotionEventCompat.getY(event, pointerIndex);

                            // Calculate the distance moved
                            dxSphere = x - lastTouchXSphere;
                            dySphere = y - lastTouchYSphere;

                            if (clickDuration > Values.MAX_SPHERE_TOUCH_TIME_BEFORE_MOVE) {
                                SnackCreatorController.this.setX(SnackCreatorController.this.getX() + dxSphere);
                                SnackCreatorController.this.setY(SnackCreatorController.this.getY() + dySphere);
                                movingSphere = true;
                            }
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_UP: {
                            final int pointerIndex = MotionEventCompat.getActionIndex(event);
                            final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                            if (pointerId == activePointerIdSphere) {
                                // This was our active pointer going up. Choose a new
                                // active pointer and adjust accordingly.
                                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                                lastTouchXSphere = MotionEventCompat.getX(event, newPointerIndex);
                                lastTouchYSphere = MotionEventCompat.getY(event, newPointerIndex);
                                activePointerIdSphere = MotionEventCompat.getPointerId(event, newPointerIndex);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_DOWN: {
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            activePointerIdSphere = INVALID_POINTER_ID_SPHERE;
                            break;
                        }
                    }
                }
                icons.onTouchEvent(event);
                return true;
            }
        };
    }



    private void translateSphere(){
        final float screenHeight = Utils.getScreenHeight(getContext());
        float yPosition;

        // Position to set after animation end
        final float xPos = 0;
        final float yPos;

        if ((getY() + getHeight()/2) < screenHeight/2){
            yPosition = - (getY() - sphereYPositionUp);
            yPos = sphereYPositionUp;
        }
        else {
            yPosition = sphereYPositionDown - getY();
            yPos = sphereYPositionDown;
        }

        TranslateAnimation translateAnimation = new TranslateAnimation(0, -getX(), 0, yPosition);
        translateAnimation.setInterpolator(new OvershootInterpolator());
        translateAnimation.setDuration(Values.SPHERE_TRANSLATION_TIME);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                invalidate();
                setX(xPos);
                setY(yPos);
                showHiddenIcons();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        setAnimation(translateAnimation);
        startAnimation(translateAnimation);
    }
}
