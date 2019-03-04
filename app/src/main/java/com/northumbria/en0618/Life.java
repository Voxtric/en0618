package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.SpriteGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

class Life extends SpriteGameObject
{
    // CONSTANTS
    private static final float PROGRESSION_MULTIPLIER = 2.0f;

    // Initialised Values
    private boolean m_lifeLost = false;
    private double m_loseProgression = 0.0;

    Life(Context context, float x, float y, float size)
    {
        super(Sprite.getSprite(context, R.drawable.player, false),
                x, y, size, size * Player.HEIGHT_TO_WIDTH_RATIO);
    }

    @Override
    public void update(float deltaTime)
    {
        // Makes Life Icon move when player loses a Life. Disappears and Destroys after moving it's sprite size.
        if (m_lifeLost)
        {
            m_loseProgression += deltaTime * PROGRESSION_MULTIPLIER;
            moveBy(0.0f, (float)(Math.cos(m_loseProgression) * m_loseProgression) * PROGRESSION_MULTIPLIER);

            if (getY() <= -getYSize())
            {
                destroy();
            }
        }
    }

    public void lose()
    {
        m_lifeLost = true;
    }
}
