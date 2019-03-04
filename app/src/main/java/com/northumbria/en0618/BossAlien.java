package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.Input;

public class BossAlien extends Alien
{
    // Boss Alien that spawns randomly on a horizontal line at the top of the screen
    private static final long BASE_SCORE = 50;

    BossAlien(Context context, int spriteType, float x, float size, float moveSpeed)
    {
        super(context, spriteType, x, Input.getScreenHeight() * 0.85f, size, moveSpeed, 0.0f);
    }

    @Override
    public void update(float deltaTime)
    {
        // Moves each frame. Dies once off screen
        moveBy(m_moveSpeed * deltaTime, 0.0f);
        if(getX() < -getXSize() || getX() > Input.getScreenWidth() + getXSize())
        {
            destroy();
        }
    }

    @Override
    public void awardScore(Player player, int currentLevel)
    {
        // Overides basic function to provide score when killed
        player.addScore(BASE_SCORE * currentLevel);
    }
}
