package com.example.flightrecorder.math;

import android.util.Log;

public class Matrix33f
{
    private static String TAG = "Matrix33f";

    private float[] m_values = new float[3*3];

    public Matrix33f(float[] row0, float[] row1, float[] row2)
    {
        if(row0.length != 3)
        {
            Log.e(TAG, "constructor: row0 does not have 3 values");
        }
        else if(row1.length != 3)
        {
            Log.e(TAG, "constructor: row1 does not have 3 values");
        }
        else if(row2.length != 3)
        {
            Log.e(TAG, "constructor: row2 does not have 3 values");
        }

        m_values[0] = row0[0];
        m_values[1] = row0[1];
        m_values[2] = row0[2];

        m_values[3] = row1[0];
        m_values[4] = row1[1];
        m_values[5] = row1[2];

        m_values[6] = row2[0];
        m_values[7] = row2[1];
        m_values[8] = row2[2];
    }

    public float[] getRow0()
    {
        float[] result = {m_values[0], m_values[1], m_values[2]};
        return result;
    }

    public float[] getRow1()
    {
        float[] result = {m_values[3], m_values[4], m_values[5]};
        return result;
    }

    public float[] getRow2()
    {
        float[] result = {m_values[6], m_values[7], m_values[8]};
        return result;
    }

    public void setRow0(float[] row0)
    {
        if(row0.length != 3)
        {
            Log.e(TAG, "setRow0: row0 does not have 3 values");
        }

        m_values[0] = row0[0];
        m_values[1] = row0[1];
        m_values[2] = row0[2];
    }

    public void setRow1(float[] row1)
    {
        if(row1.length != 3)
        {
            Log.e(TAG, "setRow1: row1 does not have 3 values");
        }

        m_values[3] = row1[0];
        m_values[4] = row1[1];
        m_values[5] = row1[2];
    }

    public void setRow2(float[] row2)
    {
        if(row2.length != 3)
        {
            Log.e(TAG, "setRow2: row2 does not have 3 values");
        }

        m_values[3] = row2[0];
        m_values[4] = row2[1];
        m_values[5] = row2[2];
    }

    public void multiplyByMatrix33f(Matrix33f matrix)
    {
        float[] row0 = new float[3];
        row0[0] = m_values[0]*matrix.getRow0()[0] + m_values[1]*matrix.getRow1()[0] + m_values[2]*matrix.getRow2()[0];
        row0[1] = m_values[0]*matrix.getRow0()[1] + m_values[1]*matrix.getRow1()[1] + m_values[2]*matrix.getRow2()[1];
        row0[2] = m_values[0]*matrix.getRow0()[2] + m_values[1]*matrix.getRow1()[2] + m_values[2]*matrix.getRow2()[2];

        float[] row1 = new float[3];
        row1[0] = m_values[3]*matrix.getRow0()[0] + m_values[4]*matrix.getRow1()[0] + m_values[5]*matrix.getRow2()[0];
        row1[1] = m_values[3]*matrix.getRow0()[1] + m_values[4]*matrix.getRow1()[1] + m_values[5]*matrix.getRow2()[1];
        row1[2] = m_values[3]*matrix.getRow0()[2] + m_values[4]*matrix.getRow1()[2] + m_values[5]*matrix.getRow2()[2];

        float[] row2 = new float[3];
        row2[0] = m_values[6]*matrix.getRow0()[0] + m_values[7]*matrix.getRow1()[0] + m_values[8]*matrix.getRow2()[0];
        row2[1] = m_values[6]*matrix.getRow0()[1] + m_values[7]*matrix.getRow1()[1] + m_values[8]*matrix.getRow2()[1];
        row2[2] = m_values[6]*matrix.getRow0()[2] + m_values[7]*matrix.getRow1()[2] + m_values[8]*matrix.getRow2()[2];

        setRow0(row0);
        setRow1(row1);
        setRow2(row2);
    }

    public Vector3f multiplyVector3f(Vector3f vector)
    {
        float value0 = m_values[0]*vector.getX() + m_values[1]*vector.getY() + m_values[2]*vector.getZ();
        float value1 = m_values[3]*vector.getX() + m_values[4]*vector.getY() + m_values[5]*vector.getZ();
        float value2 = m_values[6]*vector.getX() + m_values[7]*vector.getY() + m_values[8]*vector.getZ();

        return new Vector3f(value0, value1, value2);
    }
}
