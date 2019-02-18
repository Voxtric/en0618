package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.Log;

import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

public class Alien extends CollidableGameObject {

    private float m_x = -75.0f;
    private float m_y = 0.0f;
    private boolean m_goingDown = false;
    private float m_size;
    private float m_lastY;
    private boolean m_isBoss = false;

    public Alien(Context context, @DrawableRes int spriteType, float x, float y, float size, float moveSpeed) {
        super(context, Sprite.getSprite(context, spriteType, true), x, y, size, size);
        //Log.e("ALIEN SPAWNED", "ALIEN SPAWNED");
        m_goingDown = false;
        m_x = moveSpeed;
        if(m_x > 0)
        {
            m_y = -m_x;
        }
        else
        {
            m_y = m_x;
        }
        m_size = size;
        if(spriteType == R.drawable.alien_large)
        {
            m_isBoss = true;
        }
    }

    @Override
    public void update(float frameTime)
    {
        if(m_isBoss)
        {
            if(getX() <= -50.0f || getX() >= Input.getScreenWidth() + 50.0f)
            {
                m_isAlive = false;
            }
        }
        if(m_goingDown)
        {
            if(getY() <= m_lastY - m_size)
            {
                moveBy(m_x * frameTime, 0.0f);
                m_goingDown = false;
            }
            else
            {
                moveBy(0.0f, m_y * frameTime);
            }
        }
        else
        {
            moveBy(m_x * frameTime, 0.0f);
        }
    }


    @Override
    public void collidedWith(objectType other)
    {
        if(other == objectType.bullet)
        {
            m_isAlive = false;
        }
    }

    public void switchDirection()
    {
        if(!m_goingDown)
        {
            m_x = -m_x;
            m_lastY = getY();
            m_goingDown = true;
        }
    }

    public void incSpeed()
    {
        float spdMult = m_x / 5;
        m_x = m_x + spdMult;
        if(m_x > 0)
        {
            m_y = -m_x;
        }
        else
        {
            m_y = m_x;
        }
    }

    public boolean isBoss()
    {
        return m_isBoss;
    }
}
