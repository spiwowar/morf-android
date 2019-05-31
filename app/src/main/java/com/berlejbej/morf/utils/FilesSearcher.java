package com.berlejbej.morf.utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Szymon on 2016-04-17.
 */
public class FilesSearcher {

    public static ArrayList getFiles(File dir, String suffix) {
        ArrayList<String> pdfFiles = new ArrayList<>();
        walkDir(dir, suffix, pdfFiles);
        return pdfFiles;
    }

    public static ArrayList walkDir(File dir, String suffix, ArrayList pdfFiles) {
        String pdfPattern = suffix;

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkDir(listFile[i], suffix, pdfFiles);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)){
                        pdfFiles.add(listFile[i].getAbsolutePath());
                    }
                }
            }
        }
        return pdfFiles;
    }
}
