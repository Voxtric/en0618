package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

public class Alien extends CollidableGameObject {

    private float m_xSpeed = -75.0f;
    private float m_ySpeed = 0.0f;
    private boolean m_goingDown = false;
    private float m_size;
    private float m_lastY;
    private boolean m_isBoss = false;

    public Alien(Context context, @DrawableRes int spriteType, float x, float y, float size, float moveSpeed) {
        super(context, Sprite.getSprite(context, spriteType, true), x, y, size, size);
        //Log.e("ALIEN SPAWNED", "ALIEN SPAWNED");
        m_goingDown = false;
        m_xSpeed = moveSpeed;
        if(m_xSpeed > 0)
        {
            m_ySpeed = -m_xSpeed;
        }
        else
        {
            m_ySpeed = m_xSpeed;
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
                destroy();
            }
        }

        if(m_goingDown)
        {
            if(getY() <= m_lastY - m_size)
            {
                moveBy(m_xSpeed * frameTime, 0.0f);
                m_goingDown = false;
            }
            else
            {
                moveBy(0.0f, m_ySpeed * frameTime);
            }
        }
        else
        {
            moveBy(m_xSpeed * frameTime, 0.0f);
        }
    }

    @Override
    public void collidedWith(objectType other)
    {
        if(other == objectType.bullet)
        {
            destroy();
        }
    }

    public void switchDirection(float deltaTime)
    {
        if(!m_goingDown)
        {
            m_xSpeed = -m_xSpeed;
            m_lastY = getY();
            m_goingDown = true;
            moveBy(m_xSpeed * deltaTime, 0.0f);
        }
    }

    public void incSpeed()
    {
        float spdMult = m_xSpeed / 5;
        m_xSpeed = m_xSpeed + spdMult;
        if(m_xSpeed > 0)
        {
            m_ySpeed = -m_xSpeed;
        }
        else
        {
            m_ySpeed = m_xSpeed;
        }
    }

    public boolean isBoss()
    {
        return m_isBoss;
    }
}
