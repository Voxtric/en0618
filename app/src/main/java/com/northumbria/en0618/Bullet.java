package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.opengl.Sprite;

enum direction {
    UP, DOWN
}

public class Bullet extends CollidableGameObject
{
    private direction m_moveDir;

    Bullet(Context context, float x, float y, direction moveDir, @DrawableRes int spriteType)
    {
        super(context, Sprite.getSprite(context, spriteType, true),
                x, y, 50.0f, 50.0f);
        m_moveDir = moveDir;
    }

    @Override
    public void update(float deltaTime)
    {
        float yMove = 500.0f;
        if (m_moveDir == direction.DOWN)
        {
            yMove = -yMove;
        }
        moveBy(0.0f, yMove * deltaTime);

        if ((getY() < -getYSize()) || (getY() > Input.getScreenHeight() + getYSize()))
        {
            destroy();
        }
    }

    public boolean shotByAlien()
    {
        return m_moveDir != direction.UP;
    }
}
