package com.example.flightrecorder.customViews;

import java.text.DecimalFormat;

public class DataItem
{
    private String m_name = "";
    private float m_value = 0.0f;

    public DataItem(String name, float value)
    {
        m_name = name;
        m_value = value;
    }

    public void setName(String name)
    {
        m_name = name;
    }
    public String getName()
    {
        return m_name;
    }

    public void setValue(float value)
    {
        m_value = value;
    }
    public float getValue()
    {
        return m_value;
    }

    public String toString()
    {
        DecimalFormat format = new DecimalFormat("#.00");
        String value = format.format(m_value);
        return m_name + ": " + value;
    }
}