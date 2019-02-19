package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

public class Alien extends CollidableGameObject
{
    protected float m_moveSpeed;

    private float m_lastY;
    private boolean m_goingDown = false;

    Alien(Context context, @DrawableRes int spriteType, float x, float y, float size, float moveSpeed)
    {
        super(context, Sprite.getSprite(context, spriteType, true), x, y, size, size);
        m_moveSpeed = moveSpeed;
    }

    @Override
    public void update(float frameTime)
    {

        if(m_goingDown)
        {
            if(getY() <= m_lastY - getYSize())
            {
                moveBy(m_moveSpeed * frameTime, 0.0f);
                m_goingDown = false;
            }
            else
            {
                moveBy(0.0f, -Math.abs(m_moveSpeed) * frameTime);
            }
        }
        else
        {
            moveBy(m_moveSpeed * frameTime, 0.0f);
        }
    }

    public void switchDirection(float deltaTime)
    {
        if(!m_goingDown)
        {
            m_moveSpeed = -m_moveSpeed;
            m_lastY = getY();
            m_goingDown = true;
            moveBy(m_moveSpeed * deltaTime, 0.0f);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void increaseSpeed()
    {
        m_moveSpeed *= 1.1f;
    }
}
