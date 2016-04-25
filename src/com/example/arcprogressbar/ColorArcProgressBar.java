
package com.example.arcprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ColorArcProgressBar extends View {

    private static int ANIM_SPEED = 1000;
    private final float DP_2 = dipToPx(2);
    private final float DP_1_4 = dipToPx(1.4f);

    private float diameter = 0; // 直径
    private float centerX; // 圆心X坐标
    private float centerY; // 圆心Y坐标

    private Paint arcPaint;
    private Paint outerArcPaint;
    private Paint dialPaint;
    private Paint progressPaint;
    private Paint titlePaint;
    private Paint speedPaint;
    private Paint unitPaint;

    private Matrix rotateMatrix;
    private SweepGradient outArcSweepGradient;
    private SweepGradient progressSweepGradient;

    private float arcWidth = dipToPx(10);
    private float outArcWidth = dipToPx(2);
    private float progressWidth = dipToPx(10);
    private float dialLongWidth = dipToPx(8);
    private float dialShortWidth = dipToPx(4);

    private float outArcMargin = dipToPx(2);
    private float dialMargin = dipToPx(2);

    private int arcColor = Color.BLACK;
    private int[] outArcColors = new int[] {
            Color.BLACK, Color.DKGRAY, Color.LTGRAY
    };
    private int[] progressColors = new int[] {
            Color.GREEN, Color.YELLOW, Color.RED, Color.RED
    };
    private int titleColor = Color.DKGRAY;
    private int speedColor = Color.DKGRAY;
    private int unitColor = Color.DKGRAY;
    private int dialLongColor = Color.DKGRAY;
    private int dialShortColor = Color.DKGRAY;

    private RectF rectArc;
    private RectF rectOutArc;

    private boolean outArcVisible;
    private boolean dialVisible;
    private boolean titleVisible;
    private boolean speedVisible;
    private boolean unitVisible;

    private String titleText;
    private String unitText;

    private float titleSize = dipToPx(15);
    private float speedSize = dipToPx(60);
    private float unitSize = dipToPx(13);

    private float valueCurrent = 0;
    private float valueMax = 100;

    private ValueAnimator progressAnimator;
    private PaintFlagsDrawFilter mDrawFilter;

    private float angleStart = 135;
    private float angleSweep = 270;
    private float angelCurrent = 0;
    private float angleLast;
    private float k; // angleSweep / valueMax 的比值

    public static interface OnProgressUpdateListener {

        void onProgressUpdate(float value);
    }

    private OnProgressUpdateListener onProgressUpdateListener;

    public ColorArcProgressBar(Context context) {
        super(context, null);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        parseAttrs(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     * @param context
     * @param attrs
     */
    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);

        diameter = a.getDimension(R.styleable.ColorArcProgressBar_diameter, diameter);

        angleStart = a.getFloat(R.styleable.ColorArcProgressBar_engle_start, angleStart);
        angleSweep = a.getFloat(R.styleable.ColorArcProgressBar_engle_sweep, angleSweep);

        arcWidth = a.getDimension(R.styleable.ColorArcProgressBar_arc_width, arcWidth);
        arcColor = a.getColor(R.styleable.ColorArcProgressBar_arc_color, arcColor);

        outArcWidth = a.getDimension(R.styleable.ColorArcProgressBar_out_arc_width, outArcWidth);
        int outArcColor1 = a.getColor(R.styleable.ColorArcProgressBar_out_arc_color1, Color.BLACK);
        int outArcColor2 = a.getColor(R.styleable.ColorArcProgressBar_out_arc_color2, outArcColor1);
        int outArcColor3 = a.getColor(R.styleable.ColorArcProgressBar_out_arc_color3, outArcColor1);
        outArcColors = new int[] {
                outArcColor1, outArcColor2, outArcColor3, outArcColor3
        };
        outArcMargin = a.getDimension(R.styleable.ColorArcProgressBar_out_arc_margin, outArcMargin);
        outArcVisible = a.getBoolean(R.styleable.ColorArcProgressBar_out_visible, outArcVisible);

        dialVisible = a.getBoolean(R.styleable.ColorArcProgressBar_dial_visible, dialVisible);
        dialMargin = a.getDimension(R.styleable.ColorArcProgressBar_dial_margin, dialMargin);
        dialLongColor = a.getColor(R.styleable.ColorArcProgressBar_dial_long_color, dialLongColor);
        dialShortColor = a.getColor(R.styleable.ColorArcProgressBar_dial_short_color, dialShortColor);

        progressWidth = a.getDimension(R.styleable.ColorArcProgressBar_progress_width, progressWidth);
        int color1 = a.getColor(R.styleable.ColorArcProgressBar_progress_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.ColorArcProgressBar_progress_color2, color1);
        int color3 = a.getColor(R.styleable.ColorArcProgressBar_progress_color3, color1);
        progressColors = new int[] {
                color1, color2, color3, color3
        };

        valueMax = a.getFloat(R.styleable.ColorArcProgressBar_value_max, valueMax);
        setMaxValue(valueMax);
        valueCurrent = a.getFloat(R.styleable.ColorArcProgressBar_value_current, valueCurrent);
        setCurrentValue(valueCurrent);

        titleText = a.getString(R.styleable.ColorArcProgressBar_title_text);
        titleSize = a.getDimension(R.styleable.ColorArcProgressBar_title_color, titleSize);
        titleColor = a.getColor(R.styleable.ColorArcProgressBar_title_color, titleColor);
        titleVisible = a.getBoolean(R.styleable.ColorArcProgressBar_title_visible, titleVisible);

        speedSize = a.getDimension(R.styleable.ColorArcProgressBar_speed_color, speedSize);
        speedColor = a.getColor(R.styleable.ColorArcProgressBar_speed_color, speedColor);
        speedVisible = a.getBoolean(R.styleable.ColorArcProgressBar_speed_visible, speedVisible);

        unitText = a.getString(R.styleable.ColorArcProgressBar_unit_text);
        unitSize = a.getDimension(R.styleable.ColorArcProgressBar_unit_color, unitSize);
        unitColor = a.getColor(R.styleable.ColorArcProgressBar_unit_color, unitColor);
        unitVisible = a.getBoolean(R.styleable.ColorArcProgressBar_unit_visible, unitVisible);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        float diameterTmp = diameter != 0 ? diameter : 3 * getScreenWidth() / 5;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            float widthTmp = diameterTmp + arcWidth;
            if (outArcVisible) {
                widthTmp += outArcMargin * 2 + outArcWidth;
            }
            if (dialVisible) {
                widthTmp += dialMargin * 2 + dialLongWidth;
            }

            if (widthMode == MeasureSpec.UNSPECIFIED) {
                width = (int) Math.max(widthTmp, getSuggestedMinimumWidth());
            } else {
                width = (int) Math.min(widthTmp, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float heightTmp = diameterTmp + arcWidth;
            if (outArcVisible) {
                heightTmp += outArcMargin * 2 + outArcWidth;
            }
            if (dialVisible) {
                heightTmp += dialMargin * 2 + dialLongWidth;
            }

            if (widthMode == MeasureSpec.UNSPECIFIED) {
                height = (int) Math.max(heightTmp, getSuggestedMinimumHeight());
            } else {
                height = (int) Math.min(heightTmp, heightSize);
            }
        }

        diameterTmp = Math.max(width, height);
        if (outArcVisible) {
            diameterTmp -= outArcMargin * 2 + outArcWidth;
        }
        if (dialVisible) {
            diameterTmp -= dialMargin * 2 + dialLongWidth;
        }
        diameter = diameterTmp;

        setMeasuredDimension(width, height);
        updateView();
    }

    private void initView() {
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        rotateMatrix = new Matrix();

        // 圆心
        centerX = centerY = Math.max(getWidth(), getHeight()) / 2;

        // 圆弧的矩形区域
        rectArc = new RectF();

        // 外圆弧矩形区域
        if (outArcVisible) {
            rectOutArc = new RectF();
        }

        // 刻度盘
        if (dialVisible) {
            dialPaint = new Paint();
            dialPaint.setColor(dialLongColor);
        }

        // 圆弧
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(arcWidth);
        arcPaint.setColor(arcColor);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        // 外圆弧
        if (outArcVisible) {
            outerArcPaint = new Paint();
            outerArcPaint.setAntiAlias(true);
            outerArcPaint.setStyle(Paint.Style.STROKE);
            outerArcPaint.setStrokeCap(Paint.Cap.ROUND);
            outerArcPaint.setStrokeWidth(outArcWidth);
            outerArcPaint.setColor(outArcColors[0]);
        }

        // 进度条
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(progressColors[0]);

        // 标题文字
        if (titleVisible) {
            titlePaint = new Paint();
            titlePaint.setTextSize(titleSize);
            titlePaint.setColor(titleColor);
            titlePaint.setTextAlign(Paint.Align.CENTER);
        }

        // 速度文字
        if (speedVisible) {
            speedPaint = new Paint();
            speedPaint.setTextSize(speedSize);
            speedPaint.setColor(speedColor);
            speedPaint.setTextAlign(Paint.Align.CENTER);
        }

        // 单位文字
        if (unitVisible) {
            unitPaint = new Paint();
            unitPaint.setTextSize(unitSize);
            unitPaint.setColor(unitColor);
            unitPaint.setTextAlign(Paint.Align.CENTER);
        }
    }

    private void updateView() {
        // 圆心
        centerX = centerY = Math.max(getWidth(), getHeight()) / 2;

        // 圆弧的矩形区域
        float arcWidthHalf = arcWidth / 2;
        rectArc.set(arcWidthHalf, arcWidthHalf, getWidth() - arcWidthHalf, getHeight() - arcWidthHalf);
        if (outArcVisible) {
            float inflate = outArcWidth + outArcMargin;
            rectArc.set(rectArc.left + inflate, rectArc.top + inflate, rectArc.right - inflate, rectArc.bottom - inflate);
        }
        if (dialVisible) {
            float inflate = dialLongWidth + dialMargin;
            rectArc.set(rectArc.left + inflate, rectArc.top + inflate, rectArc.right - inflate, rectArc.bottom - inflate);
        }
        // 绘制圆弧时需是正方形，否则会被压缩；底部收缩是为了得到圆角效果
        if (rectArc.right != rectArc.bottom) {
            float tmp = Math.max(rectArc.right, rectArc.bottom);
            rectArc.set(rectArc.left + arcWidthHalf, rectArc.top, tmp - arcWidthHalf, tmp - arcWidth);
        }

        // 外圆弧矩形区域
        if (outArcVisible) {
            float outArcWidthHalf = outArcWidth / 2;
            rectOutArc.set(outArcWidthHalf, outArcWidthHalf, getWidth() - outArcWidthHalf, getHeight() - outArcWidthHalf);
            if (dialVisible) {
                float inflate = dialLongWidth + dialMargin;
                rectOutArc.set(rectOutArc.left + inflate, rectOutArc.top + inflate, rectOutArc.right - inflate, rectOutArc.bottom - inflate);
            }
            // 绘制圆弧时需是正方形，否则会被压缩；底部收缩是为了得到圆角效果
            // 为了与圆弧吻合，此处以圆弧直径收缩
            if (rectOutArc.right != rectOutArc.bottom) {
                float tmp = Math.max(rectOutArc.right, rectOutArc.bottom);
                rectOutArc.set(rectOutArc.left + arcWidthHalf, rectOutArc.top, tmp - arcWidthHalf, tmp - arcWidth);
            }

            outArcSweepGradient = new SweepGradient(centerX, centerY, outArcColors, null);
        }

        // 进度条
        progressSweepGradient = new SweepGradient(centerX, centerY, progressColors, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        rotateMatrix.setRotate(130, centerX, centerY);

        // 圆弧
        canvas.drawArc(rectArc, angleStart, angleSweep, false, arcPaint);

        // 外圆弧
        if (outArcVisible) {
            outArcSweepGradient.setLocalMatrix(rotateMatrix);
            outerArcPaint.setShader(outArcSweepGradient);
            canvas.drawArc(rectOutArc, angleStart, angleSweep, false, outerArcPaint);
        }

        // 刻度线
        if (dialVisible) {
            for (int i = 0; i < 40; i++) {
                if (i > 15 && i < 25) {
                    canvas.rotate(9, centerX, centerY);
                    continue;
                }
                if (i % 5 == 0) {
                    dialPaint.setStrokeWidth(DP_2);
                    dialPaint.setColor(dialLongColor);
                    canvas.drawLine(centerX, 0, centerX, dialLongWidth, dialPaint);
                } else {
                    dialPaint.setStrokeWidth(DP_1_4);
                    dialPaint.setColor(dialShortColor);
                    canvas.drawLine(centerX, dialLongWidth - dialShortWidth, centerX, dialLongWidth, dialPaint);
                }
                canvas.rotate(9, centerX, centerY);
            }
        }

        // 当前进度
        progressSweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(progressSweepGradient);
        canvas.drawArc(rectArc, angleStart, angelCurrent, false, progressPaint);

        if (titleVisible && !TextUtils.isEmpty(titleText)) {
            canvas.drawText(titleText, centerX, centerY - 2 * speedSize / 3, titlePaint);
        }
        if (speedVisible) {
            canvas.drawText(String.format("%.0f", valueCurrent), centerX, centerY + speedSize / 3, speedPaint);
        }
        if (unitVisible && !TextUtils.isEmpty(unitText)) {
            canvas.drawText(unitText, centerX, centerY + 2 * speedSize / 3, unitPaint);
        }

        invalidate();
    }

    /**
     * 设置进度最大值
     * @param maxValues
     */
    public void setMaxValue(float maxValue) {
        if (maxValue <= 0) {
            throw new IllegalArgumentException("max valuse must be greater than 0");
        }
        this.valueMax = maxValue;
        k = angleSweep / maxValue;

        invalidate();
    }

    /**
     * 获取进度最大值
     * @return
     */
    public float getMaxValue() {
        return this.valueMax;
    }

    /**
     * 设置当前进度
     * @param currentValues
     */
    public void setCurrentValue(float currentValue) {
        if (currentValue > valueMax) {
            currentValue = valueMax;
        }
        if (currentValue < 0) {
            currentValue = 0;
        }
        this.valueCurrent = currentValue;
        angleLast = angelCurrent;
        setAnimation(angleLast, currentValue * k, ANIM_SPEED);
    }

    /**
     * 获取当前进度值
     * @return
     */
    public float getCurrentValue() {
        return this.valueCurrent;
    }

    /**
     * 设置进度动画监听接口
     * 
     * @param progressUpdateListener
     */
    public void setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener) {
        this.onProgressUpdateListener = onProgressUpdateListener;
    }

    /**
     * 为进度设置动画
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(angelCurrent);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angelCurrent = (Float) animation.getAnimatedValue();
                valueCurrent = angelCurrent / k;
                if (onProgressUpdateListener != null) {
                    onProgressUpdateListener.onProgressUpdate(valueCurrent);
                }
            }
        });
        progressAnimator.start();
    }

    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
