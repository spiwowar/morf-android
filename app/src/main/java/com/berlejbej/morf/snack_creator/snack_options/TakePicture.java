package com.berlejbej.morf.snack_creator.snack_options;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import com.berlejbej.morf.snack_creator.SnackCreatorController;
import com.berlejbej.morf.utils.Code;
import com.berlejbej.morf.utils.PermissionChecker;
import com.berlejbej.morf.utils.Values;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Szymon on 2016-02-20.
 */
public class TakePicture {

    private static final String TAG = "TakePicture";

    public static void takePicture(Activity activity) {

        if (!checkPermissions(activity)){
            return;
        }

        Log.d(TAG, "Start taking picture");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
                String capturedPhotoPath = "file:" + photoFile.getAbsolutePath();
                galleryAddPic(activity, capturedPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Could not create file for taken picture");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d(TAG, "Starting intent - taking picture");
                if (hasImageCaptureBug()) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                activity.startActivityForResult(takePictureIntent, Code.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private static File createImageFile(Context context) throws IOException {
        Log.d(TAG, "Creating image file");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                context.getFilesDir()      // directory
        );
        return image;
    }

    public static void galleryAddPic(Context context, String capturedPhotoPath) {
        Log.d(TAG, "Adding picture to gallery");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(capturedPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static boolean hasImageCaptureBug() {

        // list of known devices that have the bug
        ArrayList<String> devices = new ArrayList<String>();
        devices.add("android-devphone1/dream_devphone/dream");
        devices.add("generic/sdk/generic");
        devices.add("vodafone/vfpioneer/sapphire");
        devices.add("tmobile/kila/dream");
        devices.add("verizon/voles/sholes");
        devices.add("google_ion/google_ion/sapphire");

        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
                + android.os.Build.DEVICE);

    }

    private static boolean checkPermissions(Activity activity){
        PermissionChecker permissionChecker = new PermissionChecker(activity);
        if (!permissionChecker.checkPermissionForCamera()){
            permissionChecker.requestPermissionForCamera();
            return false;
        }
        else if (!permissionChecker.checkPermissionForExternalStorage()){
            permissionChecker.requestPermissionForExternalStorage();
            return false;
        }
        return true;
    }
}
