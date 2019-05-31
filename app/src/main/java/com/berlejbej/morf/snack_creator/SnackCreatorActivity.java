package com.berlejbej.morf.snack_creator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.berlejbej.morf.snack_creator.SnackCreatorController.OnCodeReceivedListener;
import com.berlejbej.morf.snack_creator.snack_options.PickImageFromGallery;
import com.berlejbej.morf.snack_creator.snack_options.TakePicture;
import com.berlejbej.morf.snack_creator.snack_views.DrawingViewSnack;
import com.berlejbej.morf.utils.Code;

import java.io.File;
import java.io.FileNotFoundException;

public class SnackCreatorActivity extends AppCompatActivity {

    private static final String TAG = "SnackCreatorActivity";

    private boolean drawingOn = false;
    private SnackCreator snackCreator;
    private OnCodeReceivedListener onCodeReceivedListener = onCodeReceivedListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        snackCreator = new SnackCreator(this);
        setContentView(snackCreator);
        snackCreator.setOnCodeReceivedListener(onCodeReceivedListener);
    }

    private OnCodeReceivedListener onCodeReceivedListener() {
        return new OnCodeReceivedListener() {
            @Override
            public void onCodeReceived(Code code) {
                switch (code.getCode()) {
                    case Code.CONTROLLER_CLICKED:
                        Log.d(TAG, "Controller's sphere has been clicked!");
                        controllerClickedActionPerform();
                        break;
                    case Code.CAMERA:
                        Log.d(TAG, "Camera has been clicked!");
                        takePicture();
                        break;
                    case Code.GALLERY:
                        Log.d(TAG, "Gallery has been clicked!");
                        pickImage();
                        break;
                    case Code.DRAWING:
                        Log.d(TAG, "Drawing has been clicked!");
                        if (drawingOn){
                            stopDrawing();
                        }
                        else {
                            startDrawing();
                        }
                        break;
                    case Code.RUBBER:
                        Log.d(TAG, "Eraser has been clicked!");
                        startRubber();
                        break;
                    case Code.ERASER:
                        Log.d(TAG, "Eraser has been clicked!");
                        eraseDrawing();
                        break;
                }
            }
        };
    }

    private void controllerClickedActionPerform(){
        //DrawingViewSnack drawingViewSnack = snackCreator.getDrawingView();
        //if (drawingViewSnack != null && drawingViewSnack.isDrawing()){
        //    drawingViewSnack.stopDrawing();
        //}
    }

    private void takePicture() {
        TakePicture.takePicture(this);
    }

    private void pickImage(){
        PickImageFromGallery.pickPicture(this);
    }

    private void startDrawing(){
        DrawingViewSnack drawingViewSnack = snackCreator.getDrawingView();
        if (drawingViewSnack != null){
            drawingViewSnack.startDrawing();
            drawingOn = true;
        }
    }

    private void stopDrawing(){
        DrawingViewSnack drawingViewSnack = snackCreator.getDrawingView();
        if (drawingViewSnack != null){
            drawingViewSnack.stopDrawing();
            drawingOn = false;
        }
    }

    private void startRubber(){
        DrawingViewSnack drawingViewSnack = snackCreator.getDrawingView();
        if (drawingViewSnack != null && drawingOn){
            drawingViewSnack.startRubber();
        }
    }

    private void eraseDrawing(){
        DrawingViewSnack drawingViewSnack = snackCreator.getDrawingView();
        if (drawingViewSnack != null && drawingOn){
            drawingViewSnack.clearAll();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Starting onActivityResult");
        if (requestCode == Code.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (TakePicture.hasImageCaptureBug()) {
                File file = new File("/sdcard/tmp");
                try {
                    uri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), null, null));
                    if (!file.delete()) {
                        Log.d(TAG, "Failed to delete " + file);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                uri = data.getData();
            }
            Log.d(TAG, "Got picture taken intent ");
            String selectedImagePath = getPath(uri);
            snackCreator.addImageSnackElement(selectedImagePath);
        }
        else if (requestCode == Code.PICK_IMAGE_FROM_FILE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "got data uri " + data.getData());
            Uri uri = data.getData();
            if (uri.toString().contains("image")) {
                String selectedImagePath = getPath(data.getData());
                Log.d(TAG, "Setting picked image");
                snackCreator.addImageSnackElement(selectedImagePath);
            }
            else if (uri.toString().contains("video")){
                Log.d(TAG, "Setting video image");
                Uri videoUri = data.getData();
                snackCreator.addVideoSnackElement(videoUri);
            }
        }
        /* Not used right now, videos are taken also with pictures,
        else if (requestCode == Code.PICK_VIDEO_FROM_FILE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "got data uri " + data.getData());
            Log.d(TAG, "Setting video image");
            Uri videoUri = data.getData();
            snackCreator.addVideoSnackElement(videoUri);
        }*/
    }

    public String getPath(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
/*
        else if (requestCode == Values.PICK_MUSIC && resultCode == Activity.RESULT_OK){
            final MusicViewSnack soundImage = (MusicViewSnack) SnackCreatorActivity.this.findViewById(R.id.sound_icon);
            soundImage.setVisibility(View.VISIBLE);
            final Uri music = data.getData();
            soundImage.stopPlaying();
            soundImage.setMusicUri(music);
            soundImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Music button has been clicked");
                    soundImage.triggerMusicPlaying();
                }
            });
        }
        else if (requestCode == Values.PICK_PDF_DOCUMENT && resultCode == Activity.RESULT_OK) {
            Uri pdfFileUri = data.getData();
            setDocumentInsideFrame(pdfFileUri);
        }
    }


    private void setDocumentInsideFrame(Uri pdfFileUri){
        try {
            int w = 297, h = 420;
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(w, h, conf);
            PdfRenderer renderer = null;

            renderer = new PdfRenderer(SnackCreatorActivity.this.getContentResolver().openFileDescriptor(pdfFileUri, "r"));
            PdfRenderer.Page p = renderer.openPage(0);
            p.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            p.close();
            renderer.close();

            RelativeLayout snackFrame = (RelativeLayout) SnackCreatorActivity.this.findViewById(R.id.snack_creation_layout);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            params.width = w;
            params.height = h;
            DocumentViewSnack documentViewSnack;
            documentViewSnack = (DocumentViewSnack) SnackElementsFactory.createDocumentElement(SnackCreatorActivity.this);
            documentViewSnack.setDocument(pdfFileUri, SnackCreatorActivity.this);
            documentViewSnack.setLayoutParams(params);
            snackFrame.addView(documentViewSnack);
            documentViewSnack.setOnTop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/