package com.northumbria.en0618.engine.opengl;

public interface IRenderable
{
    Shader getShader();
    Texture getTexture();
    boolean isTransparent();
}
