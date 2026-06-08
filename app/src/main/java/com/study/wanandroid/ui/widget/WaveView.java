package com.study.wanandroid.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


/**
 * 自定义波浪动画
 */
public class WaveView extends ConstraintLayout {

    private Paint paint;
    private Path path;

    private float waveWidth = 550f;  // 波长
    private float waveHeight = 15f;  // 波浪起伏的高度

    // 两层波浪的偏移量
    private float waveOffset1 = 0f;
    private float waveOffset2 = 0f;

    private int waveColor = 0xFF2196F3; // 主色调

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();

        // 让动画跑起来
        ValueAnimator animator = ValueAnimator.ofFloat(0f, waveWidth);
        animator.setDuration(2500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            waveOffset1 = value;
            // 第二层波浪速度稍微慢一点，产生交错感
            waveOffset2 = (value * 0.7f) % waveWidth;
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 自动计算半透明颜色 (透明度设为 30% 左右)
        int translucentColor = Color.argb(
                76, // alpha: 0~255 (76 约等于 30%)
                Color.red(waveColor),
                Color.green(waveColor),
                Color.blue(waveColor)
        );

        // 1. 先画底层半透明波浪 (稍微高一点，错开一点)
        drawWave(canvas, waveOffset2, waveHeight + 5f, translucentColor);

        // 2. 再画顶层实色波浪
        drawWave(canvas, waveOffset1, waveHeight, waveColor);
    }

    /**
     * 抽取绘制单层波浪的方法，方便复用
     */
    private void drawWave(Canvas canvas, float offset, float height, int color) {
        path.reset();
        float width = getWidth();
        float viewHeight = getHeight();
        float waveBaseLine = viewHeight - height;
        float startX = -waveWidth - offset;

        // 底部波浪线
        path.moveTo(startX, waveBaseLine);
        for (float x = startX; x <= width + waveWidth; x += waveWidth) {
            path.rQuadTo(waveWidth / 4, height, waveWidth / 2, 0);
            path.rQuadTo(waveWidth / 4, -height, waveWidth / 2, 0);
        }

        // 连到右上角和左上角
        path.lineTo(width + waveWidth, 0);
        path.lineTo(startX, 0);
        path.close();

        paint.setColor(color);
        canvas.drawPath(path, paint);
    }

    public void setWaveColor(int color) {
        this.waveColor = color;
        invalidate();
    }
}