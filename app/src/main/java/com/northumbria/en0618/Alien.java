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

    private static float m_x = -20.0f;
    private static float m_y = 0.0f;
    public boolean m_alive = true;

    public Alien(Context context, alienType alien, float x, float y, float xSize, float ySize) {
        super(context, Sprite.getSprite(context, R.drawable.alien_1, true), x, y, xSize, ySize);
        //Log.e("ALIEN SPAWNED", "ALIEN SPAWNED");
    }

    @Override
    public void update(float frameTime)
    {
        if(m_y > 0.0f) {
            moveBy(0.0f, m_y * frameTime);
            m_y = 0.0f;
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

    public void moveDown()
    {
        m_y = - 50.0f;
    }

    public void moveLeft()
    {
        m_x = -50.0f;
    }

    public void moveRight()
    {
        m_x = 50.0f;

    }
}
