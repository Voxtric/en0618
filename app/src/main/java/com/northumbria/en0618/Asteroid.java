package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

public class Asteroid extends CollidableGameObject {
    public Asteroid(Context context, Sprite sprite, float x, float y, float xSize, float ySize) {
        super(context, sprite, x, y, xSize, ySize);
    }
}
