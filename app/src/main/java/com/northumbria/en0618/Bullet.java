package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.opengl.Sprite;

public class Bullet extends CollidableGameObject
{
    private static final float SCREEN_DISTANCE_PER_SECOND = 0.4f;
    private static final float SCREEN_DISTANCE_HEIGHT = 0.025f;
    private static final float WIDTH_TO_HEIGHT_RATIO = 0.33f;

    private float m_moveSpeed;

    Bullet(Context context, @DrawableRes int spriteType, float x, float y)
    {
        super(context,
                Sprite.getSprite(context, spriteType, true),
                x, y,
                (Input.getScreenHeight() * SCREEN_DISTANCE_HEIGHT) * WIDTH_TO_HEIGHT_RATIO,
                Input.getScreenHeight() * SCREEN_DISTANCE_HEIGHT);

        m_moveSpeed = Input.getScreenHeight() * SCREEN_DISTANCE_PER_SECOND;
        if (spriteType == R.drawable.alien_shot)
        {
            m_moveSpeed = -m_moveSpeed;
        }
    }

    @Override
    public void update(float deltaTime)
    {
        moveBy(0.0f, m_moveSpeed * deltaTime);
        if ((getY() < -getYSize()) || (getY() > Input.getScreenHeight() + getYSize()))
        {
            // Self-Destroys if off the screen
            destroy();
        }
    }
}
