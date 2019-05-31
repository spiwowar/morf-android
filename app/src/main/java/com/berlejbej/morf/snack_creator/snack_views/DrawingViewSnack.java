package com.berlejbej.morf.snack_creator.snack_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.berlejbej.morf.snack_creator.snack_elements.DrawingElement;
import com.berlejbej.morf.snack_creator.snack_elements.SnackElement;
import com.berlejbej.morf.utils.Values;

/**
 * Created by Szymon on 2016-02-20.
 */
public class DrawingViewSnack extends View implements DrawingElement {

    private static final String TAG = "DrawingViewSnack";

    private static final float TOUCH_TOLERANCE = 4;
    private static final float DRAWING_SIZE = 20.0f;
    private static final float CIRCLE_SIZE = 4.0f;
    private static final float RUBBER_SIZE = 50.0f;

    private OnSnackElementMove onSnackElementMove = null;
    Context context;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private boolean drawing;
    private Canvas canvas;
    private Paint mPaint;
    private float mX, mY;
    private OnTopListener onTopListener;
    private int snackElementType;

    private boolean invisibleToTouch = true;

    public DrawingViewSnack(Context context) {
        this(context, null);
        init(context);
    }

    public DrawingViewSnack(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawingViewSnack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        Log.d(TAG, "Creating DrawingView instance");
        this.context = context;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        createPaint();
        createCirclePaint();
    }

    private void createPaint(){
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(Color.parseColor(Values.GREEN));
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(DRAWING_SIZE);
    }

    private void createCirclePaint(){
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(CIRCLE_SIZE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "Size of drawing view has been changed");
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw()");
        this.canvas = canvas;
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(this, x, y);
                if (this.drawing) {
                    touch_start(x, y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(this, x, y);
                if (this.drawing) {
                    touch_move(x, y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(this, x, y);
                if (this.drawing) {
                    touch_up();
                    invalidate();
                }
                break;
        }
        return true;
    }

    public void stopDrawing() {
        Log.d(TAG, "Disabling drawing");
        setClickable(false);
        setEnabled(false);
        setInvisibleToTouch(true);
        this.drawing = false;
    }

    public void startDrawing() {
        Log.d(TAG, "Enabling drawing");
        setClickable(true);
        setEnabled(true);
        String color = Values.GREEN;
        setColor(color);
        setInvisibleToTouch(false);
        this.drawing = true;
    }

    public boolean isDrawing(){
        return this.drawing;
    }

    public void setColor(String color) {
        Log.d(TAG, "Setting drawing color to: " + color);
        switch (color) {
            case Values.GREEN:
                changeColor(Values.GREEN);
                break;
            case Values.BLUE:
                changeColor(Values.BLUE);
                break;
            case Values.YELLOW:
                changeColor(Values.YELLOW);
                break;
            case Values.RED:
                changeColor(Values.RED);
                break;
            case Values.PINK:
                changeColor(Values.PINK);
                break;
            case Values.BLACK:
                changeColor(Values.BLACK);
                break;
            case Values.WHITE:
                changeColor(Values.WHITE);
                break;
            default:
                changeColor(Values.GREEN);
                break;
        }
    }

    private void changeColor(String color){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor(color));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(DRAWING_SIZE);
        canvas.drawPath(mPath, mPaint);
    }

    public void startRubber(){
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setStrokeWidth(RUBBER_SIZE);
        invalidate();
    }

    public void stopRubber(){
        //TODO: implement this
    }

    public void changePaint(Paint mPaint){
        this.mPaint = mPaint;
    }

    public void clearAll(){
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    @Override
    public View getElement() {
        return this;
    }

    @Override
    public boolean getInvisibleToTouch() {
        return invisibleToTouch;
    }

    @Override
    public float getPositionX() {
        return 0;
    }

    @Override
    public float getPositionY() {
        return 0;
    }

    @Override
    public float getTouchableHeight() {
        return getHeight();
    }

    @Override
    public float getTouchableWidth() {
        return getWidth();
    }

    @Override
    public float getRotationDegrees(){
        return 0.0f;
    }

    @Override
    public void setInvisibleToTouch(boolean invisible) {
        this.invisibleToTouch = invisible;
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
    public boolean isRemovable() {
        return false;
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
