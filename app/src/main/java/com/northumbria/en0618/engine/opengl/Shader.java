package com.northumbria.en0618.engine.opengl;

import android.opengl.GLES20;

// Shader cache and base for actual shader code.
public abstract class Shader
{
    private static Shader s_activeShader = null;

    // Creates a sub shader program to be used in a main shader.
    private static int loadSubShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // Sets OpenGL to use the specified shader if it isn't already doing so.
    public static void useShader(Shader shader)
    {
        // No need to change if we're already using the correct shader.
        if (s_activeShader != shader)
        {
            // Stop using the previous shader if there is one.
            if (s_activeShader != null)
            {
                s_activeShader.unUse();
            }
            GLES20.glUseProgram(shader.getHandle());
            shader.use();
            s_activeShader = shader;
        }
    }

    // Clears the shader cache.
    public static void clearCache()
    {
        s_activeShader = null;

        SpriteShader.releaseInstance();
        FontShader.releaseInstance();
    }

    int m_handle;   // OpenGL shader program identifier.

    // Create a shader program using code for the vertex and fragment shader.
    Shader(String vertexShaderCode, String fragmentShaderCode)
    {
        m_handle = GLES20.glCreateProgram();

        // Load the individual shaders.
        int vertexShader = loadSubShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadSubShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // link them together.
        GLES20.glAttachShader(m_handle, vertexShader);
        GLES20.glAttachShader(m_handle, fragmentShader);
        GLES20.glLinkProgram(m_handle);
    }

    private int getHandle()
    {
        return m_handle;
    }

    public abstract void use();
    public abstract void unUse();
}
