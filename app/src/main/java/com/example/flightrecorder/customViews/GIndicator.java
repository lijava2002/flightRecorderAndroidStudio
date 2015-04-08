package com.example.flightrecorder.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class GIndicator extends View
{
    private static String TAG = "GIndicator";

    private Boolean m_isIndicating = false;

    private float m_currentG = 1.0f;
    private float m_maxG = 1.0f;
    private float m_minG = 1.0f;

    private int m_maxScaleG = 6;
    private int m_minScaleG = -3;

    private int m_scaleWidth = 150;
    private int m_leftSpacing = 50;

    public GIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //canvas.drawARGB(70, 255, 0, 0);

        Paint linePaint = new Paint();
        linePaint.setARGB(255, 255, 255, 255);
        linePaint.setStrokeWidth(5.0f);

        Paint textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextSize(24.0f);

        drawBox(canvas, linePaint);

        drawScale(canvas, linePaint, textPaint);

        if(m_isIndicating == false)
        {
            drawNoIndicationWarning(canvas);
        }
        else
        {
            drawCurrentGIndicator(canvas, linePaint, m_currentG);
            drawMaxGIndicator(canvas, linePaint, m_maxG);
            drawMinGIndicator(canvas, linePaint, m_minG);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //TODO: implement measurement

        Rect windowRect = new Rect();
        getWindowVisibleDisplayFrame(windowRect);

        int[] location = new int[2];
        getLocationInWindow(location);

        setMeasuredDimension(m_scaleWidth + m_leftSpacing, windowRect.height() - location[1]);
    }

    public void setIndicating(Boolean indicating)
    {
        if(indicating != m_isIndicating)
        {
            m_isIndicating = indicating;
            invalidate();
        }
    }

    public void setCurrentG(float currentG)
    {
        if(currentG != m_currentG)
        {
            m_currentG = currentG;
            invalidate();
        }
    }

    public void setMaxG(float maxG)
    {
        if(maxG != m_maxG)
        {
            m_maxG = maxG;
            invalidate();
        }
    }

    public void setMinG(float minG)
    {
        if(minG != m_minG)
        {
            m_minG = minG;
            invalidate();
        }
    }

    private void drawBox(Canvas canvas, Paint lineStyle)
    {
        canvas.drawLine(m_leftSpacing, 0.0f, m_leftSpacing, (float)getHeight(), lineStyle);
        canvas.drawLine(m_leftSpacing + m_scaleWidth, 0.0f, m_leftSpacing + m_scaleWidth, (float)getHeight(), lineStyle);

        canvas.drawLine(m_leftSpacing, 0.0f, m_leftSpacing + m_scaleWidth, 0.0f, lineStyle);
        canvas.drawLine(m_leftSpacing, (float)getHeight(), m_leftSpacing + m_scaleWidth, (float)getHeight(), lineStyle);
    }

    private void drawScale(Canvas canvas, Paint lineStyle, Paint textStyle)
    {
        int lineCount = m_maxScaleG - m_minScaleG + 2;

        float scaleSpacing = (float)(getHeight() / (lineCount+1));
        float scaleWidth = (float)(getWidth() / 15);

        for(int i = 1; i < lineCount + 1; i++)
        {
            canvas.drawLine(m_leftSpacing, i*scaleSpacing, m_leftSpacing + scaleWidth, i*scaleSpacing, lineStyle);

            canvas.drawText(Integer.toString((m_maxScaleG +1) - i), m_leftSpacing + scaleWidth + 10.0f, i*scaleSpacing + 12.0f, textStyle);
        }
    }

    private void drawCurrentGIndicator(Canvas canvas, Paint lineStyle, float currentG)
    {
        drawIndicatorLine(canvas, lineStyle, currentG, m_leftSpacing + 50, m_scaleWidth);
    }

    private void drawMaxGIndicator(Canvas canvas, Paint lineStyle, float maxG)
    {
        drawIndicatorLine(canvas, lineStyle, maxG, 0, m_leftSpacing);
    }

    private void drawMinGIndicator(Canvas canvas, Paint lineStyle, float minG)
    {
        drawIndicatorLine(canvas, lineStyle, minG, 0, m_leftSpacing);
    }

    private void drawIndicatorLine(Canvas canvas, Paint lineStyle, float g, int lineXPos, int lineLength)
    {
        float lineCount = (float)(m_maxScaleG - m_minScaleG + 2 + 1);

        float position = lineCount - (g - (float) m_minScaleG) - 2.0f;
        position /= lineCount;

        float scaleSpacing = (float)(getHeight());

        position = scaleSpacing * position;

        canvas.drawLine(lineXPos, position, lineXPos + lineLength, position, lineStyle);
    }

    private void drawNoIndicationWarning(Canvas canvas)
    {
        Paint rectPaint = new Paint();
        rectPaint.setARGB(70, 255, 0, 0);
        canvas.drawRect(0.0f, 0.0f, (float)getWidth(), (float)getHeight(), rectPaint);

        Paint linePaint = new Paint();
        linePaint.setARGB(255, 255, 0, 0);
        linePaint.setStrokeWidth(5.0f);
        canvas.drawLine(0.0f, 0.0f, (float)getWidth(), (float)getHeight(), linePaint);
        canvas.drawLine((float)getWidth(), 0.0f, 0.0f, (float)getHeight(), linePaint);
    }
}
