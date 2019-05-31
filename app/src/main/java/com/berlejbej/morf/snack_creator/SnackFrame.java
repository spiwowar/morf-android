package com.berlejbej.morf.snack_creator;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.berlejbej.morf.snack_creator.snack_elements.ImageElement;
import com.berlejbej.morf.snack_creator.snack_elements.SnackElement;
import com.berlejbej.morf.snack_creator.snack_elements.VideoElement;
import com.berlejbej.morf.snack_creator.snack_views.DrawingViewSnack;
import com.berlejbej.morf.snack_creator.snack_views.ImageViewSnack;
import com.berlejbej.morf.snack_creator.snack_views.VideoViewSnackFrame;
import com.berlejbej.morf.utils.BitmapDecoder;
import com.berlejbej.morf.utils.Code;
import com.berlejbej.morf.utils.ViewRegionCheck;

import java.util.ArrayList;
import java.util.Iterator;

import com.berlejbej.morf.snack_creator.snack_elements.SnackElement.OnSnackElementMove;

/**
 * Created by Szymon on 2016-07-23.
 */
public class SnackFrame extends RelativeLayout {

    private static final String TAG = "SnackFrame";

    private OnSnackElementMove onSnackElementMove = null;
    private View childtoTouch;
    private ArrayList<SnackElement> snackElements = new ArrayList<>();
    private Iterator snackElementsIterator;
    private SnackElement snackElement;
    private DrawingViewSnack drawingView;
    private Context context;

    public SnackFrame(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SnackFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SnackFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        drawingView = new DrawingViewSnack(context);
        addDrawingSnackElement(drawingView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN ||
                action == MotionEvent.ACTION_MOVE) {
            return false; // Do not intercept touch event, let the child handle it
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
        // scroll this container).
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN ||
                action == MotionEvent.ACTION_MOVE && childtoTouch != null) {
            if (childtoTouch != null) {
                childtoTouch.onTouchEvent(ev);
            }
            return true; // Do not intercept touch event, let the child handle it
        }

        final float x = MotionEventCompat.getX(ev, 0);
        final float y = MotionEventCompat.getY(ev, 0);

        snackElementsIterator = snackElements.iterator();
        Log.d(TAG, "Looking for view to set onTouch");
        Log.d(TAG, "Elements to check: " + snackElements);
        while (snackElementsIterator.hasNext()) {
            snackElement = (SnackElement) snackElementsIterator.next();
            Log.d(TAG, "Checking " + snackElement);
            if (snackElement.getInvisibleToTouch()) {
                Log.d(TAG, "The element is invisible to touch - continue");
                continue;
            }
            else {
                Log.d(TAG, "PositionX: " + snackElement.getPositionX());
                Log.d(TAG, "PositionY: " + snackElement.getPositionY());
                Log.d(TAG, "TouchableWidth: " + snackElement.getTouchableWidth());
                Log.d(TAG, "TouchableHeight: " + snackElement.getTouchableHeight());
                if (ViewRegionCheck.inSnackElementRegion(x, y, getWidth(), getHeight(), snackElement)) {
                    Log.d(TAG, "Sending touch to another view: " + snackElement.getElement());
                    childtoTouch = snackElement.getElement();
                    snackElement.setOnTop();
                    snackElement.getElement().onTouchEvent(ev);
                    return true;
                }
            }
        }
        return false;
    }

    public void addSnackElement(SnackElement snackElement) {
        Log.d(TAG, "Setting onSnackElementMoveListener for " + snackElement.getSnackElementType());
        snackElement.setOnSnackElementMove(onSnackElementMove);
        addView((View) snackElement);
    }

    public void removeSnackElement(SnackElement snackElement){
        if (snackElements.contains(snackElement)) {
            snackElements.remove(snackElement);
            removeView((View) snackElement);
        }
    }

    public void addImageSnackElement(String imagePath){
        ImageCreator imageCreator = new ImageCreator(context, imagePath, this);
        imageCreator.execute();
    }

    public void addDrawingSnackElement(DrawingViewSnack drawingViewSnack){
        addNewElement(drawingViewSnack, Code.SNACK_DRAWING);
    }

    public void addVideoSnackElement(Uri videoUri){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        VideoViewSnackFrame videoViewSnackFrame = (VideoViewSnackFrame) createVideoElement(context);
        videoViewSnackFrame.setLayoutParams(params);
        videoViewSnackFrame.setOnTop();
        videoViewSnackFrame.addVideoView(videoUri);
    }

    public ImageElement createImageElement(Context context){
        Log.d(TAG, "Creating image element");
        final ImageElement imageElement = new ImageViewSnack(context);
        addNewElement(imageElement, Code.SNACK_IMAGE);
        return imageElement;
    }

    public VideoElement createVideoElement(Context context){
        Log.d(TAG, "Creating video element");
        final VideoElement videoElement = new VideoViewSnackFrame(context);
        addNewElement(videoElement, Code.SNACK_VIDEO);
        return videoElement;
    }

    private void addNewElement(final SnackElement snackElement, int snackType){
        snackElement.setSnackElementType(snackType);
        snackElement.setOnTopListener(new SnackElement.OnTopListener() {
            @Override
            public void setOnTop() {
                Log.d(TAG, "SetOnTopListener started for " + snackElement);
                setElementOnTop(snackElement);
            }
        });
        setElementOnTop(snackElement);
        addSnackElement(snackElement);
    }

    public void setOnSnackMoveListener(OnSnackElementMove onSnackElementMove){
        this.onSnackElementMove = onSnackElementMove;
    }

    public DrawingViewSnack getDrawingView(){
        if (drawingView != null) {
            return drawingView;
        }
        else {
            return null;
        }
    }

    private void setElementOnTop(SnackElement view){
        int elementPositionGroup = view.getSnackElementType();
        Log.d(TAG, "Elements number: " + snackElements.size());
        Iterator it = snackElements.iterator();
        SnackElement elementView;
        if (it.hasNext()) {
            while (it.hasNext()) {
                elementView = (SnackElement) it.next();
                Log.d(TAG, "Checking whether " + elementView + " < " + view);
                if (elementPositionGroup > elementView.getSnackElementType()) {
                    Log.d(TAG, "Previous element has higher precedence - continue");
                    if (!it.hasNext()) {
                        Log.d(TAG, "There's no more elements, setting at the end element:" + view);
                        snackElements.add(view);
                        break;
                    }
                    Log.d(TAG, "The list of elements: " + snackElements);
                    continue;
                } else  {
                    Log.d(TAG, "Previous element has lower precedence");
                    if (snackElements.contains(view)) {
                        int indexAfter = snackElements.indexOf(elementView);
                        int indexBefore = snackElements.indexOf(view);
                        Log.d(TAG, "Setting element from index: " + indexBefore + " to index: " + indexAfter);
                        snackElements.remove(indexBefore);
                        snackElements.add(indexAfter, view);
                        ((View) view).bringToFront();
                        for (int i = indexAfter - 1; i >= 0; i--) {
                            ((View) snackElements.get(i)).bringToFront();
                        }
                        Log.d(TAG, "The list of elements: " + snackElements);
                        break;
                    } else {
                        Log.d(TAG, "Adding new element");
                        int indexToSet = snackElements.indexOf(elementView);
                        Log.d(TAG, "Setting element to index: " + indexToSet);
                        snackElements.add(indexToSet, view);
                        Log.d(TAG, "The list of elements: " + snackElements);
                        break;
                    }
                }
            }
        }
        else {
            Log.d(TAG, "Adding the first element:" + view);
            snackElements.add(view);
        }
    }

    private class ImageCreator extends AsyncTask<String, Integer, String> {

        private Bitmap bitmap;
        private ImageViewSnack imageViewSnack;
        private ProgressBar progressBar;
        private SnackFrame snackFrame;
        private String imagePath;
        private Context context;

        public ImageCreator(Context context, String imagePath, SnackFrame snackFrame) {
            this.imagePath = imagePath;
            this.context = context;
            this.snackFrame = snackFrame;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            bitmap = BitmapDecoder.decodeSampledBitmapFromStringResource(imagePath, 200, 200);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.width = 200;
            params.height = 200;
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
            progressBar.setMax(100);
            progressBar.setIndeterminate(false);
            progressBar.setLayoutParams(params);
            snackFrame.addView(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            imageViewSnack = (ImageViewSnack) createImageElement(this.context);
            imageViewSnack.setImageBitmap(bitmap);
            imageViewSnack.setLayoutParams(params);
            imageViewSnack.setAdjustViewBounds(true);
            imageViewSnack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            snackFrame.removeView(progressBar);
            imageViewSnack.setOnTop();
            super.onPostExecute(result);
            try {
                finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
