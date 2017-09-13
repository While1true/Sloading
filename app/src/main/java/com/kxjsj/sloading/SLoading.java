package com.kxjsj.sloading;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by S0005 on 2017/5/12.
 */

public class SLoading extends View {


    private int[] color = {0xFFF4511E, 0xFFFDD835, 0xFF43A047,
            0xFF1E88E5, 0xFF8E24AA, 0xFF546E7A};

    private float gap = -1;

    private float radius = -1;

    private int num = 3;

    private Paint paint;

    private int width = 60;
    private int height = 100;
    List<Progress> list = new ArrayList<>();
    private AnimatorSet set;

    public SLoading(Context context) {
        this(context, null);
    }

    public SLoading(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainData(attrs);
        initialized();
    }

    private void obtainData(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SLoading);
        radius = a.getDimension(R.styleable.SLoading_sradius, -1);
        gap = a.getDimension(R.styleable.SLoading_sgap, -1);
        num = a.getInt(R.styleable.SLoading_snum, -1);
        int resourceId = a.getResourceId(R.styleable.SLoading_scolors, 0);
        try {
            if (resourceId != 0)
                color = getResources().getIntArray(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        a.recycle();
    }

    private void initialized() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.min(width, widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = Math.min(height, heightSize);
        }
        if (gap == -1)
            gap = height / 2;

        if (radius == -1)
            radius = height / 2;

        for (int i = 0; i < num; i++) {
            list.add(new Progress(radius, i));
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < num; i++) {
            paint.setAlpha((int) ((0.3 + 0.7 * list.get(i).getPercentage()) * 255));
            paint.setColor(color[list.get(i).getColorIndex() % color.length]);
            canvas.drawCircle(calculateCenterX(i), height / 2, list.get(i).getCurrent(), paint);
        }
    }

    private float calculateCenterX(int i) {
        return calculateStart() + i * 2 * radius + radius + i * gap;
    }


    private int calculateStart() {
        int contentLength = (int) (2 * num * radius + (num - 1) * gap);

        return width / 2 - contentLength / 2;
    }

    public SLoading setColor(int[] color) {
        this.color = color;
        return this;
    }

    public SLoading setGap(int gap) {
        this.gap = gap;
        return this;
    }

    public SLoading setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public SLoading setNum(int num) {
        this.num = num;
        return this;
    }

    public SLoading startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                goAnimator();
            }
        });
        return this;
    }

    private ObjectAnimator getAnimator(final Progress progress, final int i) {
        ObjectAnimator animator = ObjectAnimator.ofObject(progress, "percentage", new FloatEvaluator(), 0, 1);
        animator.setDuration(700);
        animator.setRepeatCount(-1);
        animator.setStartDelay((long) (i * 700 / num));
        animator.setInterpolator(new OvershootInterpolator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        if (i == 0) {
            ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    postInvalidate();
                }
            };
            animator.addUpdateListener(listener);
        }
        animator.addListener(new Anl(progress));

        return animator;
    }

    private void goAnimator() {
        if (set == null) {
            set = new AnimatorSet();
            Collection<Animator> animators = new ArrayList<>(num);
            for (int i = 0; i < num; i++) {
                ObjectAnimator animator = getAnimator(list.get(i), i);
                animators.add(animator);
            }
            set.playTogether(animators);
            set.start();
        } else {

            if (!set.isStarted())
                set.start();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimator();
        super.onDetachedFromWindow();
    }

    public void stopAnimator() {
        if (set != null) {
            set.cancel();
            set.getChildAnimations().clear();
            setVisibility(GONE);
        }
    }

    private static class Progress {
        float percentage = 0;
        float current;
        float radius;
        int colorIndex;

        public Progress(float radius, int colorIndex) {
            this.current = percentage * radius;
            this.radius = radius;
            this.colorIndex = colorIndex;
        }

        public void setColorIndex(int colorIndex) {
            this.colorIndex = colorIndex;
        }

        public int getColorIndex() {
            return colorIndex;
        }

        public float getCurrent() {
            return current;
        }

        public float getPercentage() {
            return percentage;
        }

        public void setPercentage(float percentage) {
            this.percentage = percentage;
            this.current = percentage * radius;
        }
    }

    static  class Anl implements Animator.AnimatorListener {
        Progress progress;

        public Anl(Progress progress) {
            this.progress = progress;
        }

        @Override
        public void onAnimationStart(Animator animation, boolean isReverse) {

        }

        @Override
        public void onAnimationEnd(Animator animation, boolean isReverse) {

        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            int colorIndex = progress.getColorIndex();
            progress.setColorIndex(++colorIndex);
            Log.i("dddd", "onAnimationRepeat: "+colorIndex);
        }
    }
}
