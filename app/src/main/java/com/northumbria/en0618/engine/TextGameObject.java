package com.northumbria.en0618.engine;

import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.IRenderable;

public class TextGameObject extends GameObject
{
    private Font m_font;
    private String m_text;
    private float m_width;
    private float[] m_color = new float[] { 0.0f, 1.0f, 0.0f, 1.0f };

    public TextGameObject(Font font, String text, float x, float y)
    {
        super(x, y, 0.0f, 0.0f);
        m_font = font;
        setText(text);
    }

    public void setText(String text)
    {
        m_text = text;

        m_width = 0.0f;
        for (int charIndex = 0; charIndex < text.length(); charIndex++)
        {
            m_width += m_font.getCharWidth(text.charAt(charIndex));
        }
    }

    public void setColor(float r, float g, float b, float a)
    {
        m_color[0] = r;
        m_color[1] = g;
        m_color[2] = b;
        m_color[3] = a;
    }

    @Override
    public float getXSize()
    {
        return m_width;
    }

    @Override
    public float getYSize()
    {
        return m_font.getHeight();
    }

    @Override
    public void draw(float[] vpMatrix)
    {
        m_font.draw(vpMatrix, m_color, m_text, getX(), getY());
    }

    @Override
    public IRenderable getRenderable()
    {
        return m_font;
    }
}
