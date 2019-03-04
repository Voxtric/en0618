package com.northumbria.en0618;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

class Asteroid extends CollidableGameObject
{
    // Asteroid. Does no move, destroyed if hit by anything.
    Asteroid(Context context, @DrawableRes int spriteType, float x, float y, float size)
    {
        super(context, Sprite.getSprite(context, spriteType, true), x, y, size, size);
    }
}
