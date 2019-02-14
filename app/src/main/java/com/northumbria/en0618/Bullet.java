package com.northumbria.en0618;

import android.content.Context;
import android.util.Log;

import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

enum direction {
    UP, DOWN
}

public class Bullet extends CollidableGameObject {

    private direction m_moveDir;

    public Bullet(Context context, float x, float y, direction moveDir)
    {
        super(context, Sprite.getSprite(context, R.drawable.player, true),
                x, y, 100.0f, 100.0f);
        m_moveDir = moveDir;
    }

    @Override
    public void update(float deltaTime)
    {
        if(m_moveDir == direction.UP)
        {
            moveBy(0.0f,100.0f * deltaTime);
        }
        if (m_moveDir == direction.DOWN)
        {
            moveBy(0.0f, -100.0f * deltaTime);
        }
    }

    public boolean shotByAlien()
    {
        boolean result;
        if(m_moveDir == direction.UP)
        {
            result = false;
        }
        else
        {
            result = true;
        }
        return result;
    }
}
