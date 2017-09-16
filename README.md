# Sloading
### 一款漂亮的自定义loading 

---
圆的数量，大小，颜色 间隙 都可改变

[简书 地址](http://www.jianshu.com/p/b03750a83fb4)
---
### 1.效果
![image](http://upload-images.jianshu.io/upload_images/6456519-00c279b76ec9453d.gif?imageMogr2/auto-orient/strip)

### 2.实现
view动画的实现步骤一般如下


#### -  attrs中自定义属性

##### 1.定义属性
```
    <declare-styleable name="SLoading">
        <attr name="snum" format="integer"/>
        <attr name="sgap" format="dimension"/>
        <attr name="scolorarray" format="reference"/>
        <attr name="sradius" format="dimension"/>
        <attr name="scolortype" format="integer"/>
    </declare-styleable>

```
##### 2.获取属性

```
  private void obtainData(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SLoading);
        radius = a.getDimension(R.styleable.SLoading_sradius, -1);
        gap = a.getDimension(R.styleable.SLoading_sgap, -1);
        num = a.getInt(R.styleable.SLoading_snum, -1);
        type = a.getInt(R.styleable.SLoading_scolortype, 0);
        int resourceId = a.getResourceId(R.styleable.SLoading_scolorarray, 0);
        try {
            if (resourceId != 0)
                color = getResources().getIntArray(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        a.recycle();
    }
```


#### -  View onMeasure测量
包含测量以及一些属性初始化

```
 int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int contentLength = (int) (2 * num * radius + (num - 1) * gap);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.min(contentLength, widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) Math.min(2 * radius, heightSize);
        }
        //xml中若没定义间隙，按高度的一半
        if (gap == -1)
            gap = height / 2;

        //xml中若没定义半径，按高度一半
        if (radius == -1)
            radius = height / 2;
        //添加小圆实体到集合
        if (list.size() == 0) {
            for (int i = 0; i < num; i++) {
                list.add(new Progress(radius, type == 0 ? 0 : ((num - i) % num)));
            }

        }
        setMeasuredDimension(width, height);
```

#### -  Ondraw()
绘制圆形

```
  for (int i = 0; i < num; i++) {
            Log.i("=----------", "onDraw: " + ((0.1 + 0.6 * list.get(i).getPercentage()) * 255));
            paint.setAlpha((int) ((0.1 + 0.7 * list.get(i).getPercentage()) * 255));
            paint.setColor(color[list.get(i).getColorIndex() % color.length]);
            canvas.drawCircle(calculateCenterX(i), height / 2, list.get(i).getCurrent(), paint);
        }
```

#### -  AnimatorSet 实现动画
通过objectAnimator控制每个园的动画颜色变化

```
private ObjectAnimator getAnimator(final Progress progress, final int i) {
        ObjectAnimator animator = ObjectAnimator.ofObject(progress, "percentage", new FloatEvaluator(), 0.2, 1, 0.2);
        animator.setDuration(1200);
        animator.setRepeatCount(-1);
        animator.setStartDelay((long) (i * 700 / num));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
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
```

### 总结
自定义view的步骤如上，基本上大部分动画都可这样实现出来。
