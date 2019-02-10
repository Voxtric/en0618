package com.northumbria.en0618.engine;

import android.opengl.Matrix;

import com.northumbria.en0618.engine.opengl.IRenderable;

public abstract class GameObject
{
    private boolean m_destroyed = false;

    private float[] m_matrix = new float[16];
    private float m_x = 0.0f;
    private float m_y = 0.0f;
    private float m_xScale;
    private float m_yScale;

    public GameObject(float x, float y, float xSize, float ySize)
    {
        Matrix.setIdentityM(m_matrix, 0);

        m_xScale = xSize;
        m_yScale = ySize;
        Matrix.scaleM(m_matrix, 0, xSize, ySize, 0.0f);

        m_x = x;
        m_y = y;
        Matrix.translateM(m_matrix, 0, x / m_xScale, y / m_yScale, 0.0f);
    }

    public void moveBy(float x, float y)
    {
        m_x += x;
        m_y += y;
        Matrix.translateM(m_matrix, 0, x / m_xScale, y / m_yScale, 0.0f);
    }

    public void setPosition(float x, float y)
    {
        Matrix.translateM(m_matrix, 0, (x - m_x) / m_xScale, (y - m_y) / m_yScale, 0.0f);
        m_x = x;
        m_y = y;
    }

    public float[] getMatrix()
    {
        return m_matrix;
    }

    public void destroy()
    {
        m_destroyed = true;
    }

    public boolean isDestroyed()
    {
        return m_destroyed;
    }

    public float getX()
    {
        return m_x;
    }

    public float getY()
    {
        return m_y;
    }

    public float getXSize()
    {
        return m_xScale;
    }

    public float getYSize()
    {
        return m_yScale;
    }

    public void update(float deltaTime)
    {
    }

    public abstract void draw(float[] vpMatrix);
    public abstract IRenderable getRenderable();
}
