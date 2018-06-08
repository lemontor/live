package com.example.user.live.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import java.text.DecimalFormat;

/**
 * Created by user on 2018/6/5.
 */
public class CustomProgressBar extends View {
    private Paint paint;
    private static final int mWidthDefault = 100;
    private static final int mHeightDefault = 100;

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(6);
        paint.setColor(Color.parseColor("#34A446"));
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);
    }

    private int percent;

    public  void  setPercent(int percent){
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数,保留两位小数
        String num = df.format((float) percent / 100);//返回的是String类型
        this.percent = (int) (Float.valueOf(num) * 360);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mWidthDefault;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mHeightDefault;
        }
        setMeasuredDimension(widthSize, heightSize);
    }
    private RectF rectF = new RectF();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int widthMeasure = getMeasuredWidth();
        int heightMeasure = getMeasuredHeight();
        rectF.left = 4;
        rectF.top = 4;
        rectF.right = widthMeasure - 6;
        rectF.bottom = heightMeasure - 6;
        canvas.drawArc(rectF, -90, percent, false, paint);
    }
}
