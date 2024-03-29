package com.northumbria.en0618.engine.opengl;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.BitSet;

// An array of bits that can be indexed into using world co-ordinated to determine whether a pixel
// in a bitmap is collidable.
public class CollisionMask
{
    private final int m_collisionMaskXSize;
    private final int m_collisionMaskYSize;
    private final BitSet m_collisionMask;

    // Create the bit array using a bitmap image.
    CollisionMask(Bitmap bitmap)
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
                // Only the transparency determines whether the pixel is collidable.
                if (Color.alpha(pixel) > 0xF0)
                {
                    m_collisionMask.set(index);
                }
                index++;
            }
        }
    }

    // Converts the world co-ordinated into co-ordinates in the bitmap, then generates an index to
    // find the correct pixel collision value.
    public boolean getMaskValue(int i, int j,
                                float objectOriginX, float objectOriginY,
                                float objectWidth, float objectHeight)
    {
        float xRatio = m_collisionMaskXSize / objectWidth;
        float yRatio = m_collisionMaskYSize / objectHeight;
        int x = (int)((i - (int)(objectOriginX - (objectWidth * 0.5f))) * xRatio);
        int y = (int)((j - (int)(objectOriginY - (objectHeight * 0.5f))) * yRatio);
        int index = x + (y * m_collisionMaskXSize);
        return (index >= 0) && m_collisionMask.get(index);
    }
}