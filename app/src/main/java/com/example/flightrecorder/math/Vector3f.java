package com.example.flightrecorder.math;

public class Vector3f
{
    private float[] m_values = new float[3];

    public Vector3f(float x, float y, float z)
    {
        m_values[0] = x;
        m_values[1] = y;
        m_values[2] = z;
    }

    public float getX()
    {
        return m_values[0];
    }

    public float getY()
    {
        return m_values[1];
    }

    public float getZ()
    {
        return m_values[2];
    }

    public void setX(float x)
    {
        m_values[0] = x;
    }

    public void setY(float y)
    {
        m_values[1] = y;
    }

    public void setZ(float z)
    {
        m_values[2] = z;
    }


}
