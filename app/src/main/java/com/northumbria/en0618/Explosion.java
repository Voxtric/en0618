package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.SpriteGameObject;
import com.northumbria.en0618.engine.opengl.AnimatedSprite;
import com.northumbria.en0618.engine.opengl.Texture;

public class Explosion extends SpriteGameObject
{
    // CONSTANTS
    private static final int ANIMATION_X_FRAMES = 8;
    private static final int ANIMATION_Y_FRAMES = 2;
    private static final float ANIMATION_FRAME_DURATION = 0.05f;

    // Uninitialised Values
    private AnimatedSprite m_sprite;

    Explosion(Context context, float x, float y, float size)
    {
        super(new AnimatedSprite(context, R.drawable.explosion_frames,
                        ANIMATION_X_FRAMES, ANIMATION_Y_FRAMES, ANIMATION_FRAME_DURATION, false),
                x, y, size, size);
        // Renders animated Sprite
        m_sprite = (AnimatedSprite)getRenderable();
    }

    @Override
    public void update(float deltaTime)
    {
        // Runs through animation then is destroyed.
        m_sprite.stepAnimation(deltaTime);
        if (m_sprite.isFinished())
        {
            destroy();
        }
    }
}
