package com.berlejbej.morf.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Szymon on 2016-02-19.
 */
public class Emojis { //TODO not used right now, and probably never

    private ArrayList<Integer> emojisList = new ArrayList(Arrays.asList(
            0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606, 0x1F609, 0x1F60A,
            0x1F60B, 0x1F60C, 0x1F60D, 0x1F60F, 0x1F612, 0x1F613, 0x1F614, 0x1F616, 0x1F618));

    private ArrayList<String> emojis = new ArrayList<>();

    public Emojis() {
        Iterator it = emojisList.iterator();
        while (it.hasNext()) {
            emojis.add(new String(Character.toChars((int) it.next())));
        }
    }

    public ArrayList<String> getEmojisList(){
        return emojis;
    }

    public Integer getEmojisCount(){
        return (Integer)emojis.size();
    }
}
