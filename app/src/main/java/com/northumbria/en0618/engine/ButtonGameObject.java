package com.northumbria.en0618.engine;

import com.northumbria.en0618.engine.opengl.Sprite;

public abstract class ButtonGameObject extends SpriteGameObject
{
    protected ButtonGameObject(Sprite sprite, float x, float y, float xSize, float ySize)
    {
        super(sprite, x, y, xSize, ySize);
    }

    @Override
    public void update(float deltaTime)
    {
        if (Input.getTouchPressed() &&
            Input.getCurrentTouchX() >= getX() - getXSize() &&
            Input.getCurrentTouchX() <= getX() + getXSize() &&
            Input.getCurrentTouchY() >= getY() - getYSize() &&
            Input.getCurrentTouchY() <= getY() + getYSize())
        {
            Input.consumeTouch();
            onTouch();
        }
    }

    protected abstract void onTouch();
}
