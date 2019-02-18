package com.northumbria.en0618.engine.opengl;

// Interface for any object that can be drawn to the screen.
public interface IRenderable
{
    Shader getShader();
    Texture getTexture();
    boolean isTransparent();
}
