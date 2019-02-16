package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.Log;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

enum direction {
    UP, DOWN
}

public class Bullet extends CollidableGameObject {

    private direction m_moveDir;
    private CollisionLists m_colList;

    public Bullet(Context context, float x, float y, direction moveDir, @DrawableRes int spriteType,
                  CollisionLists colList)
    {
        super(context, Sprite.getSprite(context, spriteType, true),
                x, y, 50.0f, 50.0f);
        m_moveDir = moveDir;
        m_colList = colList;
    }

    @Override
    public void update(float deltaTime)
    {
        float yMove = 200.0f;
        if (m_moveDir == direction.DOWN)
        {
            yMove = -yMove;
        }
        moveBy(0.0f, yMove * deltaTime);
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


    @Override
    public void collidedWith(objectType other)
    {
        if(m_moveDir == direction.UP)
        {
            if(other == objectType.alien)
            {
                m_colList.removeBullet(this, m_moveDir);
                destroy();
            }
        }
    }

}
