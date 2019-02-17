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
    private boolean m_goingDown = false;
    private int m_moveCounter = 0;

    public Alien(Context context, alienType alien, float x, float y) {
        super(context, Sprite.getSprite(context, R.drawable.alien_1, true), x, y, 75.0f, 75.0f);
        //Log.e("ALIEN SPAWNED", "ALIEN SPAWNED");
        m_goingDown = false;
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
                moveBy(0.0f, -100.0f * frameTime);
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

    }

    public void switchDirection()
    {
        if(!m_goingDown)
        {
            m_x = -m_x;
            m_goingDown = true;
        }
    }

}
