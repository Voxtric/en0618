package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.SpriteGameObject;

public class CollidableGameObject extends SpriteGameObject
{
    private CollisionMask m_collisionMask;
    private RectF m_boundingRect;

    public CollidableGameObject(Context context, Sprite sprite, float x, float y, float xSize, float ySize)
    {
        super(sprite, x, y, xSize, ySize);

        @DrawableRes int drawableID = sprite.getTexture().getDrawableID();
        m_collisionMask = Texture.getCollisionMask(context, drawableID);

        float halfXSize = xSize / 2.0f;
        float halfYSize = ySize / 2.0f;
        m_boundingRect = new RectF(x - halfXSize, y - halfYSize, x + halfXSize, y + halfYSize);
    }

    @Override
    public void moveBy(float x, float y)
    {
        super.moveBy(x, y);
        m_boundingRect.offset(x, y);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        float halfXSize = getXSize() / 2.0f;
        float halfYSize = getYSize() / 2.0f;
        m_boundingRect.set(x - halfXSize, y - halfYSize, x + halfXSize, y + halfYSize);
    }

    public boolean collidesWith(CollidableGameObject other)
    {
        if (m_boundingRect.intersects(other.m_boundingRect.left, other.m_boundingRect.top, other.m_boundingRect.right, other.m_boundingRect.bottom))
        {
            int left = (int)Math.max(m_boundingRect.left, other.m_boundingRect.left);
            int top = (int)Math.max(m_boundingRect.top, other.m_boundingRect.top);
            int right = (int)Math.min(m_boundingRect.right, other.m_boundingRect.right);
            int bottom = (int)Math.min(m_boundingRect.bottom, other.m_boundingRect.bottom);

            for (int i = left; i < right; i++)
            {
                for (int j = top; j < bottom; j++)
                {
                    if (getMaskValue(i, j) && other.getMaskValue(i, j))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean getMaskValue(int i, int j)
    {
        return m_collisionMask.getMaskValue(i, j, getX(), getY(), getXSize(), getYSize());
    }

    public void hasCollided()
    {

    }
}
