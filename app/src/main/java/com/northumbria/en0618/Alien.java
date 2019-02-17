package com.northumbria.en0618;

import android.content.Context;
import android.util.Log;

import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

enum alienType{
    smallAlien,
    medAlien,
    bigAlien,
    bossAlien
}

public class Alien extends CollidableGameObject {

    private float m_x = -75.0f;
    private float m_y = 0.0f;
    private boolean m_goingDown = false;
    private int m_moveCounter = 0;

    public Alien(Context context, alienType alien, float x, float y, float size, float moveSpeed) {
        super(context, Sprite.getSprite(context, R.drawable.alien_1, true), x, y, size, size);
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
    }

    @Override
    public void update(float frameTime)
    {
        if(m_goingDown)
        {
            if(m_moveCounter >= 50)
            {
                moveBy(m_x * frameTime, 0.0f);
                m_goingDown = false;
                m_moveCounter = 0;
            }
            else
            {
                m_moveCounter++;
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
            m_goingDown = true;
        }
    }

    public void incSpeed()
    {
        float spdMult = m_x / 4;
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

}
