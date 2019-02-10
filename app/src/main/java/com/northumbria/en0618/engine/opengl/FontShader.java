package com.northumbria.en0618.engine.opengl;

import android.opengl.GLES20;

public class FontShader extends SpriteShader
{
    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 aPosition;" +
            "attribute vec2 aUV;" +
            "varying vec2 vUV;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * aPosition;" +
            "  vUV = aUV;" +
            "}";
    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "uniform vec4 uColor;" +
            "varying vec2 vUV;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vUV).w * uColor;" +
            "}";

    private static FontShader s_instance = null;

    public static FontShader getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new FontShader();
        }
        return s_instance;
    }

    public static void releaseInstance()
    {
        s_instance = null;
    }

    private FontShader()
    {
        super(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }
}
