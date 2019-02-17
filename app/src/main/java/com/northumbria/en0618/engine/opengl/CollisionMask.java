package com.northumbria.en0618.engine.opengl;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.BitSet;

public class CollisionMask
{
    private int m_collisionMaskXSize;
    private int m_collisionMaskYSize;
    private BitSet m_collisionMask;

    public CollisionMask(Bitmap bitmap)
    {
        m_collisionMaskXSize = bitmap.getWidth();
        m_collisionMaskYSize = bitmap.getHeight();
        m_collisionMask = new BitSet(m_collisionMaskXSize * m_collisionMaskYSize);
        int index = 0;
        for (int yPixel = 0; yPixel < m_collisionMaskYSize; yPixel++)
        {
            for (int xPixel = 0; xPixel < m_collisionMaskXSize; xPixel++)
            {
                int pixel = bitmap.getPixel(xPixel, m_collisionMaskYSize - yPixel - 1);
                if (Color.alpha(pixel) > 0xF0)
                {
                    m_collisionMask.set(index);
                }
                index++;
            }
        }
    }

    public boolean getMaskValue(int i, int j, float objectOriginX, float objectOriginY, float objectWidth, float objectHeight)
    {
        float xRatio = m_collisionMaskXSize / objectWidth;
        float yRatio = m_collisionMaskYSize / objectHeight;
        int x = (int)((i - (int)(objectOriginX - (objectWidth / 2.0f))) * xRatio);
        int y = (int)((j - (int)(objectOriginY - (objectHeight / 2.0f))) * yRatio);
        int index = x + (y * m_collisionMaskXSize);
        return (index >= 0) && m_collisionMask.get(index);
    }
}