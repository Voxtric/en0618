package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.SpriteGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

public class BackgroundTile extends SpriteGameObject
{
    public static final float SIZE = 512.0f;
    public static final float SNAP_HEIGHT = -SIZE * 0.5f;

    private static final float SCROLL_SPEED = 200.0f;

    private final float m_resetHeight;

    BackgroundTile(Context context, float x, float y, float resetHeight)
    {
        super(Sprite.getSprite(context, R.drawable.game_background, false),
                x, y, SIZE, SIZE);
        m_resetHeight = resetHeight;
    }

    @Override
    public void update(float deltaTime)
    {
        if (getY() < SNAP_HEIGHT)
        {
            moveBy(0.0f, m_resetHeight);
        }
        moveBy(0.0f, -SCROLL_SPEED * deltaTime);
    }
}
