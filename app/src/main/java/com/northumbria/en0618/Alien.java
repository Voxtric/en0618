package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

import static java.lang.Math.abs;

public class Alien extends CollidableGameObject
{
    private static final long BASE_SCORE = 10;

    float m_moveSpeed;
    private final float m_moveDownDistance;

    private float m_lastY;
    private boolean m_goingDown = false;

    Alien(Context context, @DrawableRes int spriteType, float x, float y, float size, float moveSpeed, float moveDownDistance)
    {
        super(context, Sprite.getSprite(context, spriteType), x, y, size, size);
        m_moveSpeed = moveSpeed;
        m_moveDownDistance = moveDownDistance;
    }

    @Override
    public void update(float frameTime)
    {
        if (m_goingDown)
        {
            // If Alien is Moving down
            if (getY() <= m_lastY - m_moveDownDistance)
            {
                // If Alien has moved at least it's own size in a downward distance
                // Move sideways and stop going down
                moveBy(m_moveSpeed * frameTime, 0.0f);
                m_goingDown = false;
            }
            else
            {
                // Otherwise, Move down until above is true
                moveBy(0.0f, -abs(m_moveSpeed) * frameTime);
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
        if (!m_goingDown)
        {
            m_moveSpeed = -m_moveSpeed; // Switches Move speed
            m_lastY = getY(); // Saving Current Y
            m_goingDown = true;
            moveBy(m_moveSpeed * deltaTime, 0.0f);
        }
    }

    public void setSpeed(float moveSpeed)
    {
        float modifier = abs(m_moveSpeed) / m_moveSpeed;
        m_moveSpeed = moveSpeed * modifier;
    }

    public void awardScore(Player player, int currentLevel)
    {
        player.addScore(BASE_SCORE * currentLevel);
    }
}
