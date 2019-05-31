package com.berlejbej.morf.snack_creator.snack_views.scaling_and_moving_views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.berlejbej.morf.snack_creator.snack_elements.SnackElement;
import com.berlejbej.morf.snack_creator.snack_elements.VideoElement;
import com.berlejbej.morf.snack_creator.snack_views.VideoControllerView;
import com.berlejbej.morf.snack_creator.snack_views.VideoViewSnack;


/**
 * Created by Szymon on 2016-02-23.
 */
public abstract class SnackVideo extends FrameLayout implements VideoElement, VideoControllerView.MediaPlayerControl {

    private static final String TAG = "SnackVideo";

    private static int INVALID_POINTER_ID = 0;

    private static final float MIN_SCALE_FACTOR = 0.5f;
    private static final float MAX_SCALE_FACTOR = 5.0f;

    private float scaleFactor = 1.0f;
    private float lastTouchX = 0.0f;
    private float lastTouchY = 0.0f;
    private float dx = 0.0f;
    private float dy = 0.0f;
    private float translateX = 0.0f;
    private float translateY = 0.0f;
    private float actualWidth = 0.0f;
    private float actualHeight = 0.0f;
    private float initialWidth = 0.0f;
    private float initialHeight = 0.0f;
    private int activePointerId = 0;

    private ScaleGestureDetector mScaleDetector;
    private VideoViewSnack videoView;
    private VideoControllerView videoControllerView;
    private OnSnackElementMove onSnackElementMove = null;
    private Uri uri;

    // For overriden methods
    private int snackElementType;
    private boolean invisibleToTouch = false;
    private SnackElement.OnTopListener onTopListener;

    public SnackVideo(Context context) {
        super(context);
        init(context);
    }

    public SnackVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnackVideo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        Log.d(TAG, "Creating SnackVideo");
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void addVideoView(final Uri uri){
        Log.d(TAG, "Adding video view");
        this.uri = uri;

        LayoutParams frameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(frameParams);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        videoView = new VideoViewSnack(getContext());
        videoView.setVideoURI(uri);
        videoView.setLayoutParams(params);
        videoView.post(new Runnable() {
            @Override
            public void run() {
                final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoView.getContext(), uri);
                actualWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                actualHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                initialWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                initialHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                Bitmap bmp = retriever.getFrameAtTime();
                float videoInitialWidth = bmp.getWidth();
                float videoInitialHeight = bmp.getHeight();
                Log.d(TAG, "videoInitialWidth: " + videoInitialWidth);
                Log.d(TAG, "videoInitialHeight: " + videoInitialHeight);
                Log.d(TAG, "actualWidth: " + actualWidth);
                retriever.release();
                if (videoInitialHeight >= videoInitialWidth) {
                    Log.d(TAG, "videoInitialHeight >= videoInitialWidth");
                    actualHeight = ((RelativeLayout) getParent()).getWidth();
                    actualWidth = (videoInitialWidth * (((RelativeLayout) getParent()).getWidth()) / videoInitialHeight);
                    initialWidth = actualWidth;
                    initialHeight = actualHeight;
                    getLayoutParams().height = (int) actualHeight;
                    getLayoutParams().width = (int) actualWidth;
                    invalidate();
                } else if (videoInitialWidth > videoInitialHeight){
                    Log.d(TAG, "videoInitialWidth > videoInitialHeight");
                    actualHeight = (int) (actualHeight * (((RelativeLayout) getParent()).getWidth()) / videoInitialWidth);
                    actualWidth = ((RelativeLayout) getParent()).getWidth();
                    initialWidth = actualWidth;
                    initialHeight = actualHeight;
                    getLayoutParams().height = (int) actualHeight;
                    getLayoutParams().width = (int) actualWidth;
                    invalidate();
                }
                requestLayout();
            }
        });

        videoControllerView = new VideoControllerView(getContext());
        videoView.seekTo(1);
        relativeLayout.addView(videoView);
        addView(relativeLayout);
        videoControllerView.setMediaPlayer(SnackVideo.this);
        videoControllerView.setAnchorView((FrameLayout) videoView.getParent().getParent());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "Starting on touch event");
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_UP: {
                activePointerId = INVALID_POINTER_ID;
                videoControllerView.show();

                onActionUp(this, ev.getX(), ev.getY());
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                videoControllerView.onTouchEvent(ev);
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                onActionDown(this, x, y);

                // Remember where we started (for dragging)
                lastTouchX = x;
                lastTouchY = y;
                // Save the ID of this pointer (for dragging)
                activePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Log.d(TAG, "Moving video view");
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                onMove(this, x, y);

                // Calculate the distance moved
                dx = x - lastTouchX;
                dy = y - lastTouchY;
                if (!mScaleDetector.isInProgress()) {
                    moveVideo(x, y);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == activePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    lastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG, "onScale()");
            scaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            scaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(scaleFactor, MAX_SCALE_FACTOR));
            scaleVideo();
            return true;
        }
    }

    private void moveVideo(float x, float y) {
        ViewGroup parent = (ViewGroup)getParent();
        if (!mScaleDetector.isInProgress()) {
            Log.d(TAG, "Moving Video");
            if (dx < 0) {
                if (getX() + dx > 0) {
                    setX(getX() + dx);
                    translateX = translateX + dx;
                    lastTouchX = x;
                }
                else {
                    setX(0);
                    translateX = -(parent.getWidth()/2 - actualWidth/2);
                    lastTouchX = x;
                }
            }
            else if (dx > 0) {
                if (getX() + getWidth() + dx < parent.getWidth()) {
                    setX(getX() + dx);
                    translateX = translateX + dx;
                    lastTouchX = x;
                }
                else {
                    setX(parent.getWidth() - getWidth());
                    translateX = parent.getWidth()/2 - actualWidth/2;
                    lastTouchX = x;
                }
            }
            if (dy < 0) {
                if (getY() + dy > 0) {
                    setY(getY() + dy);
                    translateY = translateY + dy;
                    lastTouchY = y;
                }
                else {
                    setY(0);
                    translateY = -(parent.getHeight()/2 - actualHeight/2);
                    lastTouchY = y;
                }
            }
            else if (dy > 0) {
                if (getY() + getHeight() + dy < parent.getHeight()) {
                    setY(getY() + dy);
                    translateY = translateY + dy;
                    lastTouchY = y;
                }
                else {
                    setY(parent.getHeight() - getHeight());
                    translateY = parent.getHeight()/2 - actualHeight/2;
                    lastTouchY = y;
                }
            }
            requestLayout();
        }
    }

    private void scaleVideo(){
        Log.d(TAG, "Scaling video");
        ViewGroup parent = (ViewGroup)getParent();
        if (mScaleDetector.isInProgress()) {
            if (scaleFactor > 1 && (parent.getWidth() > getWidth() &&
                    parent.getHeight() > getHeight())) {
                getLayoutParams().width = (int) (scaleFactor * initialWidth);
                getLayoutParams().height = (int) (scaleFactor * initialHeight);
                actualWidth = initialWidth*scaleFactor;
                actualHeight = initialHeight*scaleFactor;
                if (getX() + getWidth() > parent.getWidth()) {
                    setX(parent.getWidth() - getWidth());
                }
                if (getY() + getHeight() > parent.getHeight()) {
                    setY(parent.getHeight() - getHeight());
                }
                if (getX() < 0) {
                    setX(0);
                }
                if (getY() < 0) {
                    setY(0);
                }
                requestLayout();
            }
            else if (scaleFactor > 1 &&
                    (parent.getWidth() <= getWidth() ||
                            parent.getHeight() <= getHeight())){
                scaleFactor = 1;
            }
            else if (scaleFactor<1){
                getLayoutParams().width = (int) (scaleFactor * initialWidth);
                getLayoutParams().height = (int) (scaleFactor * initialHeight);
                actualWidth = getWidth();
                actualHeight = getHeight();
                if (getX() < 0) {
                    setX(0);
                }
                else if (getX() + getWidth() > parent.getWidth()) {
                    setX(parent.getWidth() - getWidth());
                }
                if (getY() < 0) {
                    setY(0);
                }
                else if (getY() + getHeight() > parent.getHeight()) {
                    setY(parent.getHeight() - getHeight());
                }
                requestLayout();
            }
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
        return actualWidth;
    }

    @Override
    public float getTouchableHeight(){
        return actualHeight;
    }

    @Override
    public float getRotationDegrees(){
        return 0.0f ;
    }

    @Override
    public void setSnackElementType(int snackElementType){
        this.snackElementType = snackElementType;
    }

    @Override
    public int getSnackElementType(){
        return snackElementType;
    }


    @Override
    public void setOnTop(){
        onTopListener.setOnTop();
    }

    @Override
    public void setOnTopListener(OnTopListener onTopListener){
        this.onTopListener = onTopListener;
    }

    @Override
    public boolean getInvisibleToTouch() {
        return false;
    }

    @Override
    public void setInvisibleToTouch(boolean invisible) {
        this.invisibleToTouch = invisible;
    }

    @Override
    public void toggleFullScreen() {
        Intent intent = new Intent(getContext(), VideoFullscreen.class);
        intent.putExtra("Video", this.uri.toString());
        getContext().startActivity(intent);
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return videoView.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return videoView.getDuration();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return videoView.isPlaying();
    }

    @Override
    public void pause() {
        videoView.pause();
    }

    @Override
    public void seekTo(int pos) {
        videoView.seekTo(pos);
    }

    @Override
    public void start() {
        videoView.start();
    }

    @Override
    public boolean isRemovable() {
        return true;
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
}
