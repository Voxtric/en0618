package com.northumbria.en0618.engine;

import android.opengl.Matrix;

import com.northumbria.en0618.engine.opengl.IRenderable;
import com.northumbria.en0618.engine.opengl.Sprite;

public class SpriteGameObject extends GameObject
{
    private final Sprite m_sprite;
    private final float[] m_color = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

    public SpriteGameObject(Sprite sprite, float x, float y, float xSize, float ySize)
    {
        super(x, y, xSize, ySize);
        m_sprite = sprite;
    }

    @Override
    public void draw(float[] vpMatrix)
    {
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, getMatrix(), 0);
        m_sprite.draw(mvpMatrix, m_color);
    }

    @Override
    public IRenderable getRenderable()
    {
        return m_sprite;
    }

    public void setColor(float r, float g, float b, float a)
    {
        m_color[0] = r;
        m_color[1] = g;
        m_color[2] = b;
        m_color[3] = a;
    }
}
