package com.example.flightrecorder.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class TextBox extends View
{
    static private String TAG = "TextBox";

    private Boolean m_isIndicating = false;

    private Paint m_textPaint = null;

    private Map<String, DataItem> m_dataItems = new HashMap<String, DataItem>();
    private int m_textPadding = 5;

    public TextBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        m_textPaint = new Paint();
        m_textPaint.setARGB(255, 255, 255, 255);
        m_textPaint.setTextSize(24.0f);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        Paint linePaint = new Paint();
        linePaint.setARGB(255, 255, 255, 255);
        linePaint.setStrokeWidth(5.0f);

        drawBox(canvas, linePaint);

        if(m_isIndicating == false)
        {
            drawNoIndicationWarning(canvas);
        }
        else
        {
            drawDataItems(canvas, m_textPaint);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Rect windowRect = new Rect();
        getWindowVisibleDisplayFrame(windowRect);

        int[] location = new int[2];
        getLocationInWindow(location);

        setMeasuredDimension(windowRect.width() - location[0] - 40, Math.max(calculateDataItemsHeight(m_textPaint), 100));
    }

    public void setIndicating(Boolean indicating)
    {
        if(indicating != m_isIndicating)
        {
            m_isIndicating = indicating;
            invalidate();
        }
    }

    public void addDataItem(DataItem dataItem)
    {
        try
        {
            m_dataItems.put(dataItem.getName(), dataItem);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }

        invalidate();
    }

    public void updateDataItem(DataItem dataItem)
    {
        m_dataItems.put(dataItem.getName(), dataItem);

        invalidate();
    }

    public void removeDataItem(DataItem dataItem)
    {
        m_dataItems.remove(dataItem.getName());

        invalidate();
    }

    private void drawBox(Canvas canvas, Paint lineStyle)
    {
        canvas.drawLine(0.0f, 0.0f, 0.0f, (float)getHeight(), lineStyle);
        canvas.drawLine((float)getWidth(), 0.0f, (float)getWidth(), (float)getHeight(), lineStyle);

        canvas.drawLine(0.0f, 0.0f, (float)getWidth(), 0.0f, lineStyle);
        canvas.drawLine(0.0f, (float)getHeight(), (float)getWidth(), (float)getHeight(), lineStyle);
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

    private void drawDataItems(Canvas canvas, Paint textStyle)
    {
        float textHeight = textStyle.descent() - textStyle.ascent();
        float currentTextYPos = textHeight;

        for (Map.Entry<String, DataItem> entry : m_dataItems.entrySet())
        {
            canvas.drawText(((DataItem)entry.getValue()).toString(), 10.0f, currentTextYPos, textStyle);
            currentTextYPos += textHeight + m_textPadding;
        }
    }

    private int calculateDataItemsHeight(Paint textStyle)
    {
        int textHeight = (int)(textStyle.descent() - textStyle.ascent());
        return m_dataItems.size() * (textHeight + m_textPadding) + m_textPadding;
    }
}
