package com.cc.customviewdemo.chapter03;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import com.cc.customviewdemo.R;

/**
 * Author: Cheng
 * Date: 2021/5/20 21:20
 * Description: 仿 qq 运动步数
 */
public class QQStepView extends View {

    private final Paint mOutPaint = new Paint();
    private final Paint mInnerPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    // 总共的，当前的
    private int mStepMax = 100;
    private int mCurrentStep = 50;
    private int mOuterColor = Color.RED;
    private int mInnerColor = Color.BLUE;
    // px
    private int mBorderWidth = 20;
    private int mStepTextSize;
    private int mStepTextColor;


    public QQStepView(Context context) {
        this(context, null);
    }


    public QQStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public QQStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 1.分析效果：
        // 2.确定自定义属性，编写 attrs.xml
        // 3.在布局中使用
        // 4.在自定义 View 中获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QQStepView);
        mOuterColor = array.getColor(R.styleable.QQStepView_outerColor, mOuterColor);
        mInnerColor = array.getColor(R.styleable.QQStepView_innerColor, mInnerColor);
        mBorderWidth = (int) array.getDimension(R.styleable.QQStepView_borderWidth, mBorderWidth);
        mStepTextSize = array.getDimensionPixelSize(R.styleable.QQStepView_stepTextSize, mStepTextSize);
        mStepTextColor = array.getColor(R.styleable.QQStepView_stepTextColor, mStepTextColor);
        array.recycle();

        mOutPaint.setAntiAlias(true);
        mOutPaint.setStrokeWidth(mBorderWidth);
        mOutPaint.setColor(mOuterColor);
        mOutPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutPaint.setStyle(Paint.Style.STROKE);

        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStrokeWidth(mBorderWidth);
        mInnerPaint.setColor(mInnerColor);
        mInnerPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerPaint.setStyle(Paint.Style.STROKE);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mStepTextColor);
        mTextPaint.setTextSize(mStepTextSize);
        // 5.onMeasure()
        // 6.画外圆弧，内圆弧，文字
        // 7.其他
    }


    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 调用者在布局文件中，可能 wrap_content 宽度高度不一致
        // 宽度高度不一致 取最小值，确保是一个正方形
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width > height ? height : width, width > height ? height : width);
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 6.1 画外圆弧，分析：圆弧闭合了，边缘没显示完整 圆弧
        // 描边有宽度，mBorderWith

        int center = getWidth() / 2;
        int radius = getHeight() / 2 - mBorderWidth;

        RectF rectF = new RectF(center - radius, center - radius,
            center + radius, center + radius);
        // RectF rectF1 = new RectF(mBorderWidth / 2, mBorderWidth / 2,
        //     getWidth() - mBorderWidth / 2, getHeight() - mBorderWidth / 2);
        canvas.drawArc(rectF, 135, 270, false, mOutPaint);
        // 6.2 内圆弧，怎么画，肯定不能写死，用百分比，是使用者设置的

        if (mStepMax == 0) {
            return;
        }
        float sweepAngle = (float) mCurrentStep / mStepMax;
        canvas.drawArc(rectF, 135, sweepAngle * 270, false, mInnerPaint);

        // 6.3 画文字
        String stepText = mCurrentStep + "";
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(stepText, 0, stepText.length(), textBounds);
        int dx = getWidth() / 2 - textBounds.width() / 2;
        // 基线 baseLine fontMetricsInt top 负数，bottom 正数
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
        Log.d("cxg", fontMetricsInt.top + " " + fontMetricsInt.bottom);
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;
        canvas.drawText(stepText, dx, baseLine, mTextPaint);
    }


    public synchronized void setStepMax(int stepMax) {
        this.mStepMax = stepMax;
    }


    public synchronized void setCurrentStep(int currentStep) {
        this.mCurrentStep = currentStep;
        // 不断绘制
        invalidate();
    }
}
