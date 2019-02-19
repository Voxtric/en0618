package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

public class Alien extends CollidableGameObject
{
    float m_moveSpeed;

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
            // If Alien is Moving down
            if(getY() <= m_lastY - getYSize())
            {
                // If Alien has moved at least it's own size in a downward distance
                // Move sideways and stop going down
                moveBy(m_moveSpeed * frameTime, 0.0f);
                m_goingDown = false;
            }
            else
            {
                // Otherwise, Move down until above is true
                moveBy(0.0f, -Math.abs(m_moveSpeed) * frameTime);
            }
        }
        else
        {
            // Not Moving down, Horizontal Movement
            moveBy(m_moveSpeed * frameTime, 0.0f);
        }
    }

    void switchDirection(float deltaTime)
    {
        // Switch Direction if not going down
        if(!m_goingDown)
        {
            m_moveSpeed = -m_moveSpeed; // Switches Move speed
            m_lastY = getY(); // Saving Current Y
            m_goingDown = true;
            moveBy(m_moveSpeed * deltaTime, 0.0f);
        }
    }

    void increaseSpeed()
    {
        // Increments speed
        // Ratio TBD
        m_moveSpeed *= 1.3f;
    }
}
