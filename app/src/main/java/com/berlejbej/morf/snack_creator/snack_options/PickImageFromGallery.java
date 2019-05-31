package com.berlejbej.morf.snack_creator.snack_options;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;

import com.berlejbej.morf.utils.Code;

/**
 * Created by Szymon on 2016-02-20.
 */
public class PickImageFromGallery {

    private static final String TAG = "PickImageFromGallery";

    public static void pickPicture(Activity activity){
        Log.d(TAG, "Start picking picture");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        Log.d(TAG, "Starting activity - picking picture from file");
        activity.startActivityForResult(intent, Code.PICK_IMAGE_FROM_FILE);
    }
}
