package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.Input;

public class BossAlien extends Alien
{
    BossAlien(Context context, int spriteType, float x, float size, float moveSpeed)
    {
        super(context, spriteType, x, Input.getScreenHeight() * 0.85f, size, moveSpeed, 0.0f);
    }

    @Override
    public void update(float deltaTime)
    {
        moveBy(m_moveSpeed * deltaTime, 0.0f);
        if(getX() < -getXSize() || getX() > Input.getScreenWidth() + getXSize())
        {
            destroy();
        }
    }
}
