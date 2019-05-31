package com.berlejbej.morf.utils;

/**
 * Created by Szymon on 2016-07-23.
 */
public class Code {

    private int code = 0;

    public final static int CONTROLLER_CLICKED = 100;
    public final static int CAMERA = 101;
    public final static int GALLERY = 102;
    public final static int DRAWING = 103;
    public final static int RUBBER = 104;
    public final static int ERASER = 105;

    public final static int REQUEST_IMAGE_CAPTURE = 201;
    public final static int PICK_IMAGE_FROM_FILE = 202;
    public final static int PICK_VIDEO_FROM_FILE = 203;
    public static final int PICK_PDF_DOCUMENT = 204;
    public static final int PICK_MUSIC = 205;

    public static final int SNACK_MUSIC = 301;
    public static final int SNACK_VIDEO = 302;
    public static final int SNACK_DOCUMENT = 303;
    public static final int SNACK_EMOTICON = 304;
    public static final int SNACK_TEXT = 305;
    public static final int SNACK_DRAWING = 306;
    public static final int SNACK_IMAGE = 307;

    public void setCode(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}