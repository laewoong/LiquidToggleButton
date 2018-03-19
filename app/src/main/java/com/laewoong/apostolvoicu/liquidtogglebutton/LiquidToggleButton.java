package com.laewoong.apostolvoicu.liquidtogglebutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;

/**
 * Created by laewoong on 2018. 3. 15..
 *
 * https://laewoong.github.io
 */

public class LiquidToggleButton extends android.support.v7.widget.AppCompatButton {

    private static final String TAG = "LiquidToggleButton";
    private static final int THUMB_ANIMATION_DURATION = 1300;

    private int canvasWidth;
    private int canvasHeight;

    private int mUncheckedColor;
    private int mCheckedColor;
    private int mCurParentBgColor;

    private float mThumbRotation;
    private float mCurThumbRadius;

    private float TRACK_RADIUS;
    private float THUMB_RADIUS;

    private boolean mIsChecked;
    private boolean mIsAnimate;

    private AnimatorSet mToggleAnimator;

    private Path mThumbPath;
    private Path mTrackPath;
    private Path mFinalPath;

    private Paint mTrackPaint;

    public LiquidToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackground(null);

        canvasWidth     = 0;
        canvasHeight    = 0;

        mUncheckedColor     = Color.parseColor("#d5505b");
        mCheckedColor       = Color.parseColor("#3dbf87");
        mCurParentBgColor   = mUncheckedColor;

        mThumbRotation  = 0;
        mCurThumbRadius = 0;

        TRACK_RADIUS = 0;
        THUMB_RADIUS = 0;

        mIsChecked = false;
        mIsAnimate = false;

        mThumbPath = new Path();
        mTrackPath = new Path();
        mFinalPath = new Path();

        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setColor(Color.parseColor("#DDDDE0"));
    }

    public void setChecked(boolean checked) {

        if(mIsChecked == checked) {
            return;
        }

        mIsChecked = checked;

        if (isAttachedToWindow() && isLaidOut()) {
            animateThumbToCheckedState(checked);
        } else {
            // TODO: Immediately move the thumb to the new position.
            animateThumbToCheckedState(checked);
        }
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {

        final float SRC_ROTATION    = newCheckedState ? 0f : 180f;
        final float DEST_ROTATION   = newCheckedState ? -180f : 0f;

        final int SRC_BG_COLOR      = newCheckedState ? mUncheckedColor : mCheckedColor;
        final int DEST_BG_COLOR     = newCheckedState ? mCheckedColor : mUncheckedColor;

        final float TOTAL_FRACTION = 80f;

        ObjectAnimator thumbRotateAnim;
        {
            Keyframe kf0 = Keyframe.ofFloat(0f,                 computeRatioValue(SRC_ROTATION, DEST_ROTATION, 0.0f));
            Keyframe kf1 = Keyframe.ofFloat(24f/TOTAL_FRACTION, computeRatioValue(SRC_ROTATION, DEST_ROTATION, 0.5f));
            Keyframe kf2 = Keyframe.ofFloat(30f/TOTAL_FRACTION, computeRatioValue(SRC_ROTATION, DEST_ROTATION, 1.0f));
            Keyframe kf3 = Keyframe.ofFloat(44f/TOTAL_FRACTION, computeRatioValue(SRC_ROTATION, DEST_ROTATION, 1.05f));
            Keyframe kf4 = Keyframe.ofFloat(70f/TOTAL_FRACTION, computeRatioValue(SRC_ROTATION, DEST_ROTATION, 1f));
            Keyframe kf5 = Keyframe.ofFloat(1f,                 computeRatioValue(SRC_ROTATION, DEST_ROTATION, 1f));
            PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("thumbRotate", kf0, kf1, kf2, kf3, kf4, kf5);
            thumbRotateAnim = ObjectAnimator.ofPropertyValuesHolder(this, pvhRotation);
            thumbRotateAnim.setDuration(THUMB_ANIMATION_DURATION);
        }

        ObjectAnimator thumbRadiusAnim;
        {
            Keyframe kf0 = Keyframe.ofFloat(0f,                         THUMB_RADIUS);
            Keyframe kf1 = Keyframe.ofFloat(15f / TOTAL_FRACTION, THUMB_RADIUS * 1.5f);
            Keyframe kf2 = Keyframe.ofFloat(24f / TOTAL_FRACTION, THUMB_RADIUS * 1.5f);
            Keyframe kf3 = Keyframe.ofFloat(43f / TOTAL_FRACTION, THUMB_RADIUS * 0.95f);
            Keyframe kf4 = Keyframe.ofFloat(1f,                         THUMB_RADIUS);
            PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("thumbRadius", kf0, kf1, kf2, kf3, kf4);
            thumbRadiusAnim = ObjectAnimator.ofPropertyValuesHolder(this, pvhRotation);
            thumbRadiusAnim.setDuration(THUMB_ANIMATION_DURATION);
        }

        ObjectAnimator parentBgColorAnim = ObjectAnimator.ofArgb(this, "parentBgColor", SRC_BG_COLOR, DEST_BG_COLOR);
        parentBgColorAnim.setInterpolator(new LinearInterpolator());
        parentBgColorAnim.setDuration(250);

        mToggleAnimator = new AnimatorSet(); // Clear previous animators. If you delete this line, Previous animators work togather.
        mToggleAnimator.playTogether(thumbRotateAnim, thumbRadiusAnim, parentBgColorAnim);
        mToggleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimate = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimate = true;
            }
        });

        mToggleAnimator.start();
    }

    public float computeRatioValue(float start, float end, float ratio) {

        return start + (end-start)*ratio;
    }

    public void setThumbRadius(float scale) {

        mCurThumbRadius = scale;
    }

    public float getThumbRadius() {

        return mCurThumbRadius;
    }

    public void setThumbRotate(float rotate) {

        this.mThumbRotation = rotate;
        invalidate();
    }

    public float getThumbRotate() {

        return  this.mThumbRotation;
    }

    public boolean setParentBgColor(int color) {

        this.mCurParentBgColor = color;

        ViewParent parent = getParent();

        if(parent instanceof View)
        {
            View view = (View)getParent();
            view.setBackgroundColor(color);

            return true;
        }
        else
        {
            return false;
        }
    }

    public int getParentBgColor() {

        return mCurParentBgColor;
    }

    private void cancelToggleAnimator() {

        if (mToggleAnimator != null) {
            mToggleAnimator.cancel();
        }
    }

    public boolean isChecked() {

        return mIsChecked;
    }

    public void toggle() {

        setChecked(!isChecked());
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setParentBgColor(mUncheckedColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasWidth = w;
        canvasHeight = h;

        TRACK_RADIUS = canvasHeight / 2f;

        THUMB_RADIUS = TRACK_RADIUS / 2f;

        mCurThumbRadius = THUMB_RADIUS;
    }

    private void makeTogglePath() {

        mFinalPath.reset();

        RectF leftRound = new RectF();
        leftRound.set(0, 0, canvasHeight, canvasHeight);
        mFinalPath.arcTo(leftRound, 90, 180);
        mFinalPath.lineTo(canvasWidth-TRACK_RADIUS, 0);

        float lineWidth = canvasWidth-(TRACK_RADIUS*2);
        leftRound.set(lineWidth, 0, canvasWidth, canvasHeight);
        mFinalPath.arcTo(leftRound, 270, 180);
        mFinalPath.lineTo(TRACK_RADIUS, canvasHeight);
        mFinalPath.close();

        Matrix tractMatrix = new Matrix();
        tractMatrix.postRotate(-mThumbRotation, canvasWidth/2f, canvasHeight/2f);
        mFinalPath.transform(tractMatrix);

        mThumbPath.reset();
        Matrix thumbMatrix = new Matrix();
        thumbMatrix.postRotate(mThumbRotation, canvasWidth/2,canvasHeight/2);

        float thumbCenter = 2 * THUMB_RADIUS;
        mThumbPath.addCircle(thumbCenter, thumbCenter, mCurThumbRadius, Path.Direction.CW);
        mThumbPath.transform(thumbMatrix);

        mFinalPath.op(mThumbPath, Path.Op.DIFFERENCE);

        PathMeasure pm = new PathMeasure(mFinalPath, true);
        final float SEGMENT_NUM = 20f;
        final float SEGMENT_LENGHT = pm.getLength()/SEGMENT_NUM; // If you delete or move this line, pm.nextContour() method doesn't work I intended.

        if(pm.nextContour()) {

            mTrackPaint.setPathEffect( null);
        }
        else {

            DiscretePathEffect discretePathEffect= new DiscretePathEffect(SEGMENT_LENGHT, 0);
            CornerPathEffect cornerPathEffect = new CornerPathEffect(50);
            ComposePathEffect pathEffect = new ComposePathEffect(cornerPathEffect, discretePathEffect);

            mTrackPaint.setPathEffect( pathEffect);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Rect newRect = canvas.getClipBounds();
        newRect.inset(-canvasWidth/2, -canvasHeight/2);  //make the rect larger
        canvas.clipRect (newRect, Region.Op.REPLACE);

        makeTogglePath();
        canvas.drawPath(mFinalPath, mTrackPaint);
    }

    @Override
    public boolean performClick() {

        if(mIsAnimate == true) {
            return true;
        }

        toggle();

        final boolean handled = super.performClick();

        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

}
