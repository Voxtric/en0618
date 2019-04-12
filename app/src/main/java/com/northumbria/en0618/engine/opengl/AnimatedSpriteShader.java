package com.northumbria.en0618.engine.opengl;

import android.opengl.GLES20;

public class AnimatedSpriteShader extends SpriteShader
{
    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
            "uniform vec2 uUVOffset;" +
            "attribute vec4 aPosition;" +
            "attribute vec2 aUV;" +
            "varying vec2 vUV;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * aPosition;" +
            "  vUV = aUV + uUVOffset;" +
            "}";
    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "uniform vec4 uColor;" +
            "varying vec2 vUV;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vUV) * uColor;" +
            "}";

    // Gets the only instance allowed of the font shader, creating it if it doesn't already exist.
    public static AnimatedSpriteShader getInstance()
    {
        return new AnimatedSpriteShader();
    }

    private int m_uvOffsetHandle;

    // Creates the shader using the specified vertex and fragment shader code.
    private AnimatedSpriteShader()
    {
        super(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }

    @Override
    void assignUniformHandles()
    {
        super.assignUniformHandles();
        m_uvOffsetHandle = GLES20.glGetUniformLocation(m_handle, "uUVOffset");
    }

    public int getUVOffsetUniform()
    {
        return m_uvOffsetHandle;
    }
}
