package com.berlejbej.morf.snack_creator.snack_views.scaling_and_moving_views;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.berlejbej.morf.R;
import com.berlejbej.morf.snack_creator.snack_views.VideoControllerView;
import com.berlejbej.morf.snack_creator.snack_views.VideoViewSnack;

/**
 * Created by Szymon on 2016-04-07.
 */
public class VideoFullscreen extends FragmentActivity implements VideoControllerView.MediaPlayerControl {

    private static final String TAG = "VideoFullscreen";
    private VideoControllerView controller;
    VideoViewSnack videoViewSnack;

    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        Log.d(TAG, "Created activity");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_fullscreen);

        String uriString = getIntent().getStringExtra("Video");
        final Uri uri = Uri.parse(uriString);
        Log.d(TAG, "Uri: " + uri);
        videoViewSnack = new VideoViewSnack(findViewById(R.id.video_view_fullscreen_layout).getContext());
        videoViewSnack.setVideoURI(uri);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        videoViewSnack.setLayoutParams(layoutParams);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.video_view_fullscreen_frame);
        frameLayout.addView(videoViewSnack);

        videoViewSnack.post(new Runnable() {
            @Override
            public void run() {
                final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoViewSnack.getContext(), uri);
                Bitmap bmp = retriever.getFrameAtTime();
                float videoInitialWidth = bmp.getWidth();
                float videoInitialHeight = bmp.getHeight();
                if (videoInitialWidth>videoInitialHeight){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        controller = new VideoControllerView(findViewById(R.id.video_view_fullscreen_layout).getContext());
        videoViewSnack.seekTo(10);
        controller.setMediaPlayer(VideoFullscreen.this);
        controller.setAnchorView((FrameLayout)videoViewSnack.getParent());
        controller.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Clicked VideoFullscreen");
        controller.show();
        return false;
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
        return videoViewSnack.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return videoViewSnack.getDuration();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return videoViewSnack.isPlaying();
    }

    @Override
    public void pause() {
        videoViewSnack.pause();
    }

    @Override
    public void seekTo(int pos) {
        videoViewSnack.seekTo(pos);
    }

    @Override
    public void start() {
        videoViewSnack.start();
    }

    @Override
    public void toggleFullScreen() {
        finish();
    }

}
