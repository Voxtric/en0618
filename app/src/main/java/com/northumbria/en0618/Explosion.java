package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.SpriteGameObject;
import com.northumbria.en0618.engine.opengl.AnimatedSprite;
import com.northumbria.en0618.engine.opengl.Texture;

public class Explosion extends SpriteGameObject
{
    private static final int ANIMATION_X_FRAMES = 8;
    private static final int ANIMATION_Y_FRAMES = 2;
    private static final float ANIMATION_FRAME_DURATION = 0.05f;

    private AnimatedSprite m_sprite;

    Explosion(Context context, float x, float y, float xSize, float ySize)
    {
        super(new AnimatedSprite(
                Texture.getTexture(context, R.drawable.explosion_frames, false),
                        ANIMATION_X_FRAMES, ANIMATION_Y_FRAMES, ANIMATION_FRAME_DURATION, false),
                x, y, xSize, ySize);
        m_sprite = (AnimatedSprite)getRenderable();
    }

    @Override
    public void update(float deltaTime)
    {
        m_sprite.stepAnimation(deltaTime);
        if (m_sprite.isFinished())
        {
            destroy();
        }
    }
}
