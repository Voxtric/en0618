package com.northumbria.en0618.engine.opengl;

import android.opengl.GLES20;

// Shader for generally rendering sprites.
public class SpriteShader extends Shader
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
            "  gl_FragColor = texture2D(uTexture, vUV) * uColor;" +
            "}";

    private static SpriteShader s_instance = null;

    // Gets the only instance allowed of the font shader, creating it if it doesn't already exist.
    public static SpriteShader getInstance()
    {
        if (s_instance == null)
        {
            s_instance = new SpriteShader();
        }
        return s_instance;
    }

    public static void releaseInstance()
    {
        s_instance = null;
    }

    // Handles pointing to OpenGL shader attributes and uniforms.
    private int m_mvpMatrixHandle;
    private int m_positionHandle;
    private int m_uvHandle;
    private int m_textureHandle;
    private int m_colorHandle;

    // Creates the shader using the specified vertex and fragment shader code.
    private SpriteShader()
    {
        super(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
        assignAttributeHandles();
        assignUniformHandles();
    }

    // Creates the shader using the specified vertex and fragment shader code from
    // an inheriting shader.
    @SuppressWarnings("SameParameterValue")
    SpriteShader(String vertexShaderCode, String fragmentShaderCode)
    {
        super(vertexShaderCode, fragmentShaderCode);
        assignAttributeHandles();
        assignUniformHandles();
    }

    // Assigns handles for all the attributes of the shader.
    private void assignAttributeHandles()
    {
        m_positionHandle = GLES20.glGetAttribLocation(m_handle, "aPosition");
        m_uvHandle = GLES20.glGetAttribLocation(m_handle, "aUV");
    }

    // Assigns handles for all the uniforms of the shader.
    void assignUniformHandles()
    {
        m_mvpMatrixHandle = GLES20.glGetUniformLocation(m_handle, "uMVPMatrix");
        m_textureHandle = GLES20.glGetUniformLocation(m_handle, "uTexture");
        m_colorHandle = GLES20.glGetUniformLocation(m_handle, "uColor");
    }

    // Signal OpenGL to use the attributes of the shader.
    @Override
    public void use()
    {
        GLES20.glEnableVertexAttribArray(m_positionHandle);
        GLES20.glEnableVertexAttribArray(m_uvHandle);
    }

    // Signal OpenGL to stop using the attributes of the shader.
    @Override
    public void unUse()
    {
        GLES20.glDisableVertexAttribArray(m_positionHandle);
        GLES20.glDisableVertexAttribArray(m_uvHandle);
    }

    public int getMVPMatrixUniform()
    {
        return m_mvpMatrixHandle;
    }

    public int getPositionAttribute()
    {
        return m_positionHandle;
    }

    public int getUVAttribute()
    {
        return m_uvHandle;
    }

    public int getTextureUniform()
    {
        return m_textureHandle;
    }

    public int getColorUniform()
    {
        return m_colorHandle;
    }
}
