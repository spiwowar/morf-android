package com.berlejbej.morf.snack_creator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.berlejbej.morf.R;
import com.berlejbej.morf.utils.Values;
import com.berlejbej.morf.utils.ViewRegionCheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Szymon on 2016-07-21.
 */
public class Icons extends RelativeLayout {

    private static final String TAG = "Icons";

    private long startClickTime = 0;
    private int INVALID_POINTER_ID = 0;
    private int activePointerId;
    private float lastTouchX = 0.0f;
    private float dx = 0.0f;
    private double orbitWidth;
    private float iconSize;
    private boolean disableIconsMovement = false;
    private boolean iconsOutsideSphereHidden = false;
    private boolean binOpened = false;
    private boolean binShowed = false;
    private boolean isSubIcons = false;

    private ArrayList<Icon> iconsList = new ArrayList<>();
    private ArrayList<Icon> modifiedIconsList = new ArrayList<>();
    private ArrayList<Icon> hiddenIcon = new ArrayList<>();
    private ArrayList<Icon> hiddenAllIcons = new ArrayList<>();
    private ArrayList<Icon> hiddenIconsOutsideSphere = new ArrayList<>();
    private HashMap<String, Icons> subIcons = new HashMap<>();
    private Icon closedBinIcon = null;
    private Icon openedBinIcon = null;
    private Context context;
    private Icon parentIcon = null;

    // DISPLACEMENT_MULTIPLICATION says how fast icons move on touch event - for 2 they
    // move with the same speed as finger, because of orbitWidth (we divide by it in moveAll method)
    private int DISPLACEMENT_MULTIPLICATION = 4;
    private int ICON_MINIMIZE_Y_DISTANCE = 100;
    private float ICON_MINIMIZE_SCALE = 0.6f;
    private final static float ICONS_SPACING = 30;
    private final static int VISIBLE_ICONS_COUNT = (int) (180 / ICONS_SPACING) + 1;

    public Icons(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Icons(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public Icons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        setLayoutParams(layoutParams);
    }

    private void createBins(){
        Drawable binClosed = ResourcesCompat.getDrawable(getResources(), R.drawable.bin_closed, null);
        Drawable binOpened = ResourcesCompat.getDrawable(getResources(), R.drawable.bin_opened, null);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        closedBinIcon = createBinIcon(binClosed, Icon.ICON_CLOSED_BIN);
        closedBinIcon.setVisibility(GONE);
        closedBinIcon.setLayoutParams(layoutParams);

        openedBinIcon = createBinIcon(binOpened, Icon.ICON_OPENED_BIN);
        openedBinIcon.setVisibility(GONE);
        openedBinIcon.setLayoutParams(layoutParams);

        addView(closedBinIcon);
        addView(openedBinIcon);
    }

    public void showBin(){
        hideAllIcons();
        closedBinIcon.setVisibility(VISIBLE);
        binShowed = true;
    }

    public void hideBin(){
        closedBinIcon.setVisibility(GONE);
        openedBinIcon.setVisibility(GONE);
        showHiddenAllIcons();
        binShowed = false;
    }

    public void openBin(){
        closedBinIcon.setVisibility(GONE);
        openedBinIcon.setVisibility(VISIBLE);
        binOpened = true;
    }

    public void closeBin(){
        openedBinIcon.setVisibility(GONE);
        closedBinIcon.setVisibility(VISIBLE);
        binOpened = false;
    }

    public boolean isBinOpened(){
        return binOpened;
    }

    public boolean isBinShowed(){
        return binShowed;
    }

    private boolean active = false;
    private void setActive(boolean active){
        this.active = active;
    }

    public Icons createSubIcons(Icon parentIcon){
        Log.d(TAG, "Creating subIcons for: " + parentIcon.getName());
        Icons icons = new Icons(context);
        icons.isSubIcons(true);
        icons.setVisibility(GONE);
        icons.setParentIcon(parentIcon);
        subIcons.put(parentIcon.getName(), icons);
        return icons;
    }

    private void setParentIcon(Icon parentIcon){
        if (isSubIcons){
            this.parentIcon = parentIcon;
        }
    }

    public Icon getParentIcon(){
        return parentIcon;
    }

    private void isSubIcons(boolean isSubIcons){
        this.isSubIcons = isSubIcons;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Let's take care of all touch events
        // We move event to child if in onTouchEvent() there is a touch < MAX_ICON_TOUCH_TIME
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Hierarchy 4");
        Log.d(TAG, "Clicked on icons layout!");
        if (!disableIconsMovement) {
            final int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    final int pointerIndex = MotionEventCompat.getActionIndex(event);
                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float y = MotionEventCompat.getY(event, pointerIndex);

                    startClickTime = Calendar.getInstance().getTimeInMillis();

                    // Remember where we started (for dragging)
                    lastTouchX = x;
                    // Save the ID of this pointer (for dragging)
                    activePointerId = MotionEventCompat.getPointerId(event, 0);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    final float x = event.getRawX();
                    final float y = event.getRawY();
                    activePointerId = INVALID_POINTER_ID;
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    startClickTime = 0;

                    Log.d(TAG, "Click duration: " + clickDuration);
                    if (clickDuration < Values.MAX_ICON_TOUCH_TIME) {
                        Icon touchedIcon;
                        Log.d(TAG, "isSubIcons: " + isSubIcons);
                        Log.d(TAG, "getParentIcon(): " + getParentIcon());
                        if (isSubIcons && getParentIcon() != null && ViewRegionCheck.inRegion(x, y, getParentIcon())){
                            touchedIcon = getParentIcon();
                            Log.d(TAG, "Touched icon is the parent: " + touchedIcon.getName());
                        }
                        else {
                            Log.d(TAG, "Touched icon is not the parent");
                            touchedIcon = getTouchedIcon(x, y);
                        }
                        if (touchedIcon != null) {
                            if (subIcons.containsKey(touchedIcon.getName())){
                                Log.d(TAG, "Touched icon " + touchedIcon.getName() + " has subicons");
                                if (touchedIcon.isMiniature()){
                                    Log.d(TAG, "Touched icon is a miniature, maximizing it");
                                    maximalizeIcon(touchedIcon);
                                    subIcons.get(touchedIcon.getName()).setVisibility(GONE);
                                    showHiddenIcons();
                                }
                                else {
                                    Log.d(TAG, "Showing subIcons and setting icon as miniature");
                                    subIcons.get(touchedIcon.getName()).setVisibility(VISIBLE);
                                    centerAndMinimizeIcon(touchedIcon);
                                    hideIcons();
                                }
                            }
                            else {
                                centerIcon(touchedIcon);
                            }
                            touchedIcon.onTouchEvent(event);
                        }
                        else {
                            return true;
                        }
                    }
                    adjust();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Log.d(TAG, "ACTION_MOVE");
                    // Find the index of the active pointer and fetch its position
                    final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerId);

                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float y = MotionEventCompat.getY(event, pointerIndex);

                    // Calculate the distance moved
                    dx = x - lastTouchX;

                    boolean miniature = false;

                    for (Icon icon: modifiedIconsList){
                        if (icon.getOnFront() && icon.isMiniature()){
                            miniature = true;
                        }
                    }

                    if (miniature){
                        break;
                    }

                    moveAll(dx);

                    // Remember this touch position for the next move event
                    lastTouchX = x;

                    break;
                }
                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerIndex = MotionEventCompat.getActionIndex(event);
                    final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                    if (pointerId == activePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        lastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                        activePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN: {
                    break;
                }
                case MotionEvent.ACTION_CANCEL: {
                    activePointerId = INVALID_POINTER_ID;
                    break;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private Icon getTouchedIcon(float x, float y) {

        for (Icon icon : modifiedIconsList) {
            if (ViewRegionCheck.inRegion(x, y, icon)){
                Log.d(TAG, "Touched icon: " + icon.getName());
                return icon;
            }
        }
        return null;
    }

    private void centerIcon(Icon icon){
        Log.d(TAG, "Centering icon " + icon.getName());
        float distanceToMove = (float) -icon.getDistanceFromCenter() / DISPLACEMENT_MULTIPLICATION * 2;
        while(distanceToMove != 0) {
            moveAll(distanceToMove);
            distanceToMove = (float) -icon.getDistanceFromCenter() / DISPLACEMENT_MULTIPLICATION * 2;
        }
    }

    private void centerAndMinimizeIcon(Icon icon){
        Log.d(TAG, "Minimazing icon " + icon.getName());
        float distanceToMove = (float) -icon.getDistanceFromCenter() / DISPLACEMENT_MULTIPLICATION * 2;
        while(distanceToMove != 0) {
            moveAll(distanceToMove);
            distanceToMove = (float) -icon.getDistanceFromCenter() / DISPLACEMENT_MULTIPLICATION * 2;
        }
        icon.setScaleX(ICON_MINIMIZE_SCALE);
        icon.setScaleY(ICON_MINIMIZE_SCALE);
        icon.setY(icon.getY() - getIconsFullSize());
        icon.setMiniature(true);
    }

    private void maximalizeIcon(Icon icon){
        if (icon.isMiniature()){
            icon.setScaleX(1/ICON_MINIMIZE_SCALE);
            icon.setScaleY(1/ICON_MINIMIZE_SCALE);
            icon.setY(icon.getY() + getIconsFullSize());
            icon.setMiniature(false);
        }
    }

    private Icon getCenterIcon(){
        Icon centerIcon = null;
        for (Icon icon: modifiedIconsList){
            if (icon.getOnFront()){
                if (centerIcon == null || Math.abs(centerIcon.getDistanceFromCenter()) > Math.abs(icon.getDistanceFromCenter())){
                    centerIcon = icon;
                }
            }
        }
        return centerIcon;
    }

    public void hideIcons(){
        Log.d(TAG, "Hiding icons");
        Icon centerIcon = getCenterIcon();
        for (Icon icon: modifiedIconsList){
            if (icon.getOnFront() && icon != centerIcon && icon.getVisibility() == VISIBLE){
                icon.setVisibility(GONE);
                hiddenIcon.add(icon);
            }
        }
    }




    public void hideIconsOutsideSphere(){
        Log.d(TAG, "Hiding icons outside sphere");
        Icon centerIcon = getCenterIcon();
        for (Icon icon: modifiedIconsList){
            if (icon.getOnFront() && icon != centerIcon && icon.getVisibility() == VISIBLE){
                icon.setVisibility(GONE);
                hiddenIconsOutsideSphere.add(icon);
            }
        }
        disableIconsMovement = true;
        iconsOutsideSphereHidden= true;

        Set<String> iconsSet = subIcons.keySet();
        Iterator<String> iconsSetIt = iconsSet.iterator();
        while (iconsSetIt.hasNext()){
            subIcons.get(iconsSetIt.next()).hideIconsOutsideSphere();
        }
    }

    public boolean iconsOutsideSphereHidden(){
        return this.iconsOutsideSphereHidden;
    }

    public void hideAllIcons(){
        Log.d(TAG, "Hiding all icons");
        for (Icon icon: modifiedIconsList){
            if (icon.getOnFront()){
                if (icon.getVisibility() == VISIBLE) {
                    icon.setVisibility(GONE);
                    hiddenAllIcons.add(icon);
                }
            }
        }

        Set<String> iconsSet = subIcons.keySet();
        Iterator<String> iconsSetIt = iconsSet.iterator();
        while (iconsSetIt.hasNext()){
            subIcons.get(iconsSetIt.next()).hideAllIcons();
        }
    }

    public void showHiddenIcons(){
        Log.d(TAG, "Showing hidden icons");
        for (Icon icon: hiddenIcon){
            icon.setVisibility(VISIBLE);
        }
        hiddenIcon.clear();
    }

    public void showHiddenIconsOutsideSphere(){
        Log.d(TAG, "Showing hidden icons outside sphere");
        for (Icon icon: hiddenIconsOutsideSphere){
            icon.setVisibility(VISIBLE);
        }
        iconsOutsideSphereHidden = false;
        disableIconsMovement = false;
        hiddenIconsOutsideSphere.clear();

        Set<String> iconsSet = subIcons.keySet();
        Iterator<String> iconsSetIt = iconsSet.iterator();
        while (iconsSetIt.hasNext()){
            subIcons.get(iconsSetIt.next()).showHiddenIconsOutsideSphere();
        }
    }

    public void showHiddenAllIcons(){
        Log.d(TAG, "Showing hidden all icons");
        for (Icon icon: hiddenAllIcons){
            icon.setVisibility(VISIBLE);
        }
        hiddenAllIcons.clear();

        Set<String> iconsSet = subIcons.keySet();
        Iterator<String> iconsSetIt = iconsSet.iterator();
        while (iconsSetIt.hasNext()){
            subIcons.get(iconsSetIt.next()).showHiddenAllIcons();
        }
    }

    public Icon addIcon(Drawable iconDrawable, String name){
        Icon iconImageView = new Icon(context);
        iconImageView.setName(name);
        iconImageView.setImageDrawable(iconDrawable);
        iconImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        iconsList.add(iconImageView);
        return iconImageView;
    }

    public Icon createBinIcon(Drawable iconDrawable, String name){
        Icon iconImageView = new Icon(context);
        iconImageView.setName(name);
        iconImageView.setImageDrawable(iconDrawable);
        return iconImageView;
    }

    public void update(){
        removeAllViews();
        modifiedIconsList.clear();
        if (!isSubIcons) {
            createBins();
        }
        int iconsMultiplication = 0;
        while (true){
            iconsMultiplication++;
            if (iconsList.size() == 0 || iconsList.size() * iconsMultiplication >= VISIBLE_ICONS_COUNT){
                break;
            }
        }
        for (int i = 0; i < iconsMultiplication; i++) {
            for (Icon item : iconsList) {
                Icon icon = copyIcon(item);
                modifiedIconsList.add(icon);
                addView(icon);
                icon.setAdjustViewBounds(true);
                icon.setVisibility(GONE);
            }
        }
    }

    public void showIcons() {

        Log.d(TAG, "Showing icons");

        int eastCount = 0;
        int westCount = 0;
        for (Icon icon: modifiedIconsList){
            icon.setVisibility(VISIBLE);
            icon.setScaleX(0);
        }
        for (float degrees = 0; degrees <= 90; degrees += ICONS_SPACING){
            Icon icon = modifiedIconsList.get(eastCount);
            icon.setOnFront(true);

            setIconPosition(icon, degrees);
            eastCount++;
        }
        for (float degrees = 360 - ICONS_SPACING; degrees >= 270; degrees -= ICONS_SPACING){
            Icon icon = modifiedIconsList.get(modifiedIconsList.size() - 1 - westCount);
            icon.setOnFront(true);

            setIconPosition(icon, degrees);
            westCount++;
        }
    }

    public void moveAll(float dx){

        Log.d(TAG, "Moving all icons");

        double alpha = Math.toDegrees(dx * DISPLACEMENT_MULTIPLICATION / orbitWidth);

        // We check this because if we would not, icons would disappear in faster move.
        // The biggest degrees to move is 30, so the iconsSpacing, so the icons have a chance to appear
        // when another disappears
        if (alpha > 30){
            alpha = 30;
        }
        else if (alpha < -30){
            alpha = -30;
        }

        for (Icon icon: modifiedIconsList){
            if (icon.getOnFront()) {

                double degrees = icon.getDegrees() + alpha;
                if (degrees > 90 && degrees < 270) {
                    icon.setOnFront(false);
                    // make it invisible
                    setIconPosition(icon, 90);
                }
                if (degrees <= 90 || degrees > 270) {
                    setIconPosition(icon, degrees);
                }
            }
        }
        Icon rightIcon = getMostRightIcon();
        Icon leftIcon = getMostLeftIcon();
        double rightIconDegrees = rightIcon.getDegrees();
        double leftIconDegrees = leftIcon.getDegrees();
        if (90 - rightIconDegrees > 30) {
            Icon rightIconToShow = getRightIconToShow();
            rightIconToShow.setOnFront(true);
            double degreesToSet = rightIconDegrees + 30;
            setIconPosition(rightIconToShow, degreesToSet);
        }
        if (leftIconDegrees - 270 > 30) {
            Icon leftIconToShow = getLeftIconToShow();
            leftIconToShow.setOnFront(true);
            double degreesToSet = leftIconDegrees - 30;
            setIconPosition(leftIconToShow, degreesToSet);
        }
    }

    private Icon getLeftIconToShow() {
        boolean startsWithOnFront = false;
        boolean afterOnFront = false;

        if (modifiedIconsList.get(0).getOnFront()) {
            startsWithOnFront = true;
        }
        for (Icon icon : modifiedIconsList) {
            if (startsWithOnFront) {
                if (afterOnFront && icon.getOnFront()){
                    return modifiedIconsList.get(modifiedIconsList.indexOf(icon)-1);
                }
                else if (!icon.getOnFront()){
                    afterOnFront = true;
                }
            } else {
                if (icon.getOnFront()){
                    return modifiedIconsList.get(modifiedIconsList.indexOf(icon)-1);
                }
            }
        }
        return modifiedIconsList.get(modifiedIconsList.size() - 1);
    }


    private Icon getRightIconToShow(){
        boolean startsWithOnFront = false;
        boolean afterNotOnFront = false;

        if (modifiedIconsList.get(0).getOnFront()) {
            startsWithOnFront = true;
        }
        for (Icon icon : modifiedIconsList) {
            if (startsWithOnFront) {
                if (!icon.getOnFront()){
                    return modifiedIconsList.get(modifiedIconsList.indexOf(icon));
                }
            } else {
                if (afterNotOnFront && !icon.getOnFront()){
                    return modifiedIconsList.get(modifiedIconsList.indexOf(icon));
                }
                else if (icon.getOnFront()){
                    afterNotOnFront = true;
                }
            }
        }
        return modifiedIconsList.get(0);
    }

    private Icon getMostLeftIcon(){
        boolean startsWithOnFront = false;
        boolean afterOnFront = false;

        if (modifiedIconsList.get(0).getOnFront()) {
            startsWithOnFront = true;
        }
        for (Icon icon : modifiedIconsList) {
            if (startsWithOnFront) {
                if (afterOnFront && icon.getOnFront()){
                    return icon;
                }
                else if (!icon.getOnFront()){
                    afterOnFront = true;
                }
            } else {
                if (icon.getOnFront()){
                    return icon;
                }
            }
        }
        return modifiedIconsList.get(0);
    }

    private Icon getMostRightIcon(){
        boolean startsWithOnFront = false;
        boolean afterNotOnFront = false;

        if (modifiedIconsList.get(0).getOnFront()) {
            startsWithOnFront = true;
        }
        for (Icon icon : modifiedIconsList) {
            if (startsWithOnFront) {
                if (!icon.getOnFront()){
                    return modifiedIconsList.get(modifiedIconsList.indexOf(icon) - 1);
                }
            } else {
                if (afterNotOnFront && !icon.getOnFront()){
                    return modifiedIconsList.get(modifiedIconsList.indexOf(icon) - 1);
                }
                else if (icon.getOnFront()){
                    afterNotOnFront = true;
                }
            }
        }
        return modifiedIconsList.get(modifiedIconsList.size() - 1);
    }

    public Icon getMostCenteredIcon(){

        Icon mostCenteredIcon = null;
        double mostCenteredIconDegrees = 0;
        double currentIconDegrees = 0;

        for (Icon icon : modifiedIconsList) {
            if (icon.getOnFront()){
                if (mostCenteredIcon == null){
                    mostCenteredIcon = icon;
                    mostCenteredIconDegrees = mostCenteredIcon.getDegrees();
                    if (mostCenteredIconDegrees > 270) {
                        mostCenteredIconDegrees = 360 - mostCenteredIconDegrees;
                    }
                }
                else {
                    currentIconDegrees = icon.getDegrees();
                    if (currentIconDegrees > 270) {
                        currentIconDegrees = 360 - currentIconDegrees;
                    }
                    if (currentIconDegrees < mostCenteredIconDegrees){
                        mostCenteredIconDegrees = currentIconDegrees;
                        mostCenteredIcon = icon;
                    }
                }
            }
        }

        return mostCenteredIcon;
    }

    private void setIconPosition(Icon icon, double degrees){

        Log.d(TAG, "Setting icon " + icon.getName() + " position");

        double orbitRadius = getOrbitWidth() / 2;
        double centerOfLayout = (((RelativeLayout) getParent()).getWidth()/2 - getIconsFullSize()/2);
        degrees = getDegrees(degrees);
        icon.setDegrees(degrees);
        double distance = orbitRadius * Math.sin(Math.toRadians(degrees));
        icon.setDistanceFromCenter(distance);
        icon.setX((float) (centerOfLayout + distance));
        if (!icon.isMiniature()) {
            double scale = getIconsFullSize() * Math.abs(Math.cos(Math.toRadians(degrees))) / getIconsFullSize();
            icon.setScaleX((float) scale);
            icon.setScaleY((float) scale);
        }
    }

    public void setOrbitWidth(double orbitWidth){
        this.orbitWidth = orbitWidth;
    }

    public double getOrbitWidth(){
        return this.orbitWidth;
    }

    public void setIconsFullSize(float iconSize){
        this.iconSize = iconSize;
    }

    public float getIconsFullSize(){
        return this.iconSize;
    }

    public void adjust(){
        Log.d(TAG, "Adjusting icons!");

        Icon mostCenteredIcon = getMostCenteredIcon();

        // TODO: ADD SOME ANIMATION FOR THAT
        // we divide by DISPLACEMENT_MULTIPLICATION add multiple by 2 so the distanceFromCenter was
        // in scale 1:1 - look at moveAll method, otherwise it would be in scale specified by
        // DISPLACEMENT_MULTIPLICATION value (where 2 is for scale 1:!)
        moveAll((float) -mostCenteredIcon.getDistanceFromCenter() / DISPLACEMENT_MULTIPLICATION * 2);
    }

    public double getDegrees(double degrees){
        if (degrees >= 360){
            return getDegrees(degrees - 360);
        }
        else if (degrees < 0){
            return getDegrees(degrees + 360);
        }
        else {
            return degrees;
        }
    }

    private Icon copyIcon(Icon icon){
        Icon copiedIcon = new Icon(context);
        copiedIcon.setImageDrawable(icon.getDrawable());
        copiedIcon.setName(icon.getName());
        copiedIcon.setOnFront(icon.getOnFront());
        copiedIcon.setScaleType(icon.getScaleType());
        if (icon.getActionPerformListener() != null) {
            copiedIcon.setActionPerformListener(icon.getActionPerformListener());
        }
        return copiedIcon;
    }
}
