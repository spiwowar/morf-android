package com.berlejbej.morf.snack_creator.snack_options;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.berlejbej.morf.R;
import com.berlejbej.morf.snack_creator.snack_views.DrawingViewSnack;
import com.berlejbej.morf.utils.Values;


/**
 * Created by Szymon on 2016-02-20.
 */
public class Drawing {

    private static final String TAG = "Drawing";
    private static DrawingViewSnack dv;
    private static Paint mPaint;
    private static String selectedColor = Values.GREEN;

    public static void startDrawing(Activity a, DrawingViewSnack drawingViewSnack){
        Log.d(TAG, "Enabling drawing");

        /*ImageButton drawingArrowButton = (ImageButton) activity.findViewById(R.id.drawable_arrow);
        ImageButton drawingChooseColorButton = (ImageButton) activity.findViewById(R.id.drawable_choose_color);
        ImageButton drawingEraserButton = (ImageButton) activity.findViewById(R.id.drawable_eraser);
        ImageButton drawingClearAll = (ImageButton) activity.findViewById(R.id.drawable_clear_all);

        startDrawingArrowButtonListener(drawingArrowButton, activity);
        startDrawingChooseColorButtonListener(drawingChooseColorButton, activity, fragmentTwo);
        startDrawingEraserButtonListener(drawingEraserButton);
        startDrawingClearALlButtonListener(drawingClearAll);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        LinearLayout drawingOptionsLayout = (LinearLayout) activity.findViewById(R.id.drawing_options_layout);
        fragmentTwo.setOptionsVisible(drawingOptionsLayout);*/

        /*if (dv == null) {
            Log.d(TAG, "Creating DrawingView");
            //dv = (DrawingViewSnack) activity.findViewById(R.id.drawing_view);
            dv.setLayoutParams(params);
            dv.startDrawing();
        }
        else {*/
        drawingViewSnack.startDrawing();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor(selectedColor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);
        drawingViewSnack.changePaint(mPaint);
        //}
        //SnackElementsFactory.getInstance().setOtherInvisibleToTouch(dv);
    }

    /*public static void startDrawingArrowButtonListener(ImageButton drawingArrow, final Activity activity) {
        Log.d(TAG, "Starting drawing arrow button listener");
        drawingArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainViewPager.setPagingEnabled(true);
                Log.d(TAG, "Drawing arrow button has been clicked.");
                parent.setMainOptionsVisible();
                if (dv != null) {
                    dv.stopDrawing();
                }
                SnackElementsFactory.getInstance().setMainSnackElementsVisibleToTouch();
            }
        });
    }

    public static void startDrawingChooseColorButtonListener(ImageButton drawingChooseColor, final Activity activity, final FragmentTwo fragmentTwo) {
        Log.d(TAG, "Starting drawing choose color button listener");
        drawingChooseColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Drawing choose color button has been clicked.");
                LinearLayout drawingColorOptionsLayout = (LinearLayout) activity.findViewById(R.id.drawing_color_options_layout);
                parent.setOptionsVisible(drawingColorOptionsLayout);

                ImageButton black = (ImageButton) activity.findViewById(R.id.drawable_choose_black);
                ImageButton green = (ImageButton) activity.findViewById(R.id.drawable_choose_green);
                ImageButton blue = (ImageButton) activity.findViewById(R.id.drawable_choose_blue);
                ImageButton red = (ImageButton) activity.findViewById(R.id.drawable_choose_red);
                ImageButton yellow = (ImageButton) activity.findViewById(R.id.drawable_choose_yellow);
                ImageButton pink = (ImageButton) activity.findViewById(R.id.drawable_choose_pink);
                ImageButton white = (ImageButton) activity.findViewById(R.id.drawable_choose_white);

                startColorImageButtonListener(black, R.drawable.drawable_pick_black, DrawingViewSnack.BLACK, activity, fragmentTwo);
                startColorImageButtonListener(green, R.drawable.drawable_pick_green, DrawingViewSnack.GREEN, activity, fragmentTwo);
                startColorImageButtonListener(blue, R.drawable.drawable_pick_blue, DrawingViewSnack.BLUE, activity, fragmentTwo);
                startColorImageButtonListener(red, R.drawable.drawable_pick_red, DrawingViewSnack.RED, activity, fragmentTwo);
                startColorImageButtonListener(yellow, R.drawable.drawable_pick_yellow, DrawingViewSnack.YELLOW, activity, fragmentTwo);
                startColorImageButtonListener(pink, R.drawable.drawable_pick_pink, DrawingViewSnack.PINK, activity, fragmentTwo);
                startColorImageButtonListener(white, R.drawable.drawable_pick_white, DrawingViewSnack.WHITE, activity, fragmentTwo);
            }
        });
    }

    public static void startColorImageButtonListener(ImageButton colorButton, final int drawable, final String color, final Activity activity, final FragmentTwo fragmentTwo){
        Log.d(TAG, "Starting color image button listener for color: " + color);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, color + " image button has been clicked.");
                LinearLayout drawingOptionsLayout = (LinearLayout) activity.findViewById(R.id.drawing_options_layout);
                parent.setOptionsVisible(drawingOptionsLayout);
                ImageButton chosenColor = (ImageButton) activity.findViewById(R.id.drawable_choose_color);
                chosenColor.setBackground(fragmentTwo.getResources().getDrawable(drawable));
                dv.setColor(color);
                selectedColor = color;
            }
        });
    }

    public static void startDrawingEraserButtonListener(final ImageButton drawingEraser) {
        Log.d(TAG, "Starting drawing eraser button listener");
        drawingEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Drawing eraser button has been clicked.");
                dv.startRubber();
            }
        });
    }

    public static void startDrawingClearALlButtonListener(final ImageButton drawingClearAll) {
        Log.d(TAG, "Starting drawing clear all button listener");
        drawingClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Drawing clear all button has been clicked.");
                dv.clearAll();
            }
        });
    }*/
}