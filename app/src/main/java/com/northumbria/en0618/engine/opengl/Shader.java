package com.northumbria.en0618.engine.opengl;

import android.opengl.GLES20;

public abstract class Shader
{
    private static Shader s_activeShader = null;

    static int loadSubShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static void useShader(Shader shader)
    {
        if (s_activeShader != shader)
        {
            if (s_activeShader != null)
            {
                s_activeShader.unUse();
            }
            GLES20.glUseProgram(shader.getHandle());
            shader.use();
            s_activeShader = shader;
        }
    }

    public static void clearCache()
    {
        s_activeShader = null;

        SpriteShader.releaseInstance();
        FontShader.releaseInstance();
    }

    int m_handle;

    protected Shader(String vertexShaderCode, String fragmentShaderCode)
    {
        m_handle = GLES20.glCreateProgram();
        int vertexShader = loadSubShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadSubShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        GLES20.glAttachShader(m_handle, vertexShader);
        GLES20.glAttachShader(m_handle, fragmentShader);
        GLES20.glLinkProgram(m_handle);
    }

    public int getHandle()
    {
        return m_handle;
    }

    public abstract void use();
    public abstract void unUse();
}
