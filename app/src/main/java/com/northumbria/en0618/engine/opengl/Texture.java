package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.DrawableRes;

public class Texture
{
    // A specific square within a texture.
    public static class Region
    {
        final float u;
        final float v;
        final float width;
        final float height;

        Region(float texWidth, float texHeight, float x, float y, float width, float height)
        {
            u = x / texWidth;
            v = y / texHeight;
            this.width = width / texWidth;
            this.height = height / texHeight;
        }

        Region(float x, float y, float width, float height)
        {
            u = x;
            v = y;
            this.width = width;
            this.height = height;
        }

        public float[] uvs()
        {
            return new float[] { u, v, u, v + height, u + width, v + height, u + width, v};
        }
    }

    private static int s_activeTexture = -1;    // The handle of the currently active texture.
    // Indicates whether transparency blending is currently enabled.
    private static boolean s_transparencyActive = false;

    // Gets a texture, creating if it it hasn't already been created and generating a collision mask
    // if one is requested but hasn't already been created.
    public static Texture getTexture(Context context, @DrawableRes int drawableID)
    {
        // Create a bitmap using the drawable ID if the texture doesn't already exist.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableID, options);
        return createFromBitmap(bitmap, drawableID);
    }

    // Creates an OpenGL texture from a bitmap.
    public static Texture createFromBitmap(Bitmap bitmap, @DrawableRes int drawableID)
    {
        int[] textureHandle = new int[1];   // OpenGL likes things to boil down to a pointer.
        GLES20.glGenTextures(1, textureHandle, 0);  // Create the texture handle.
        // Bind the texture handle as the active texture.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // Do... something?
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Push the bitmap data into the OpenGL texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return new Texture(textureHandle[0], drawableID);
    }

    // Gets a texture, creating if it it hasn't already been created.
    public static CollisionMask getCollisionMask(Context context, @DrawableRes int drawableID)
    {
        // Load the bitmap.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableID, options);
        return new CollisionMask(bitmap);
    }

    private final int m_handle;   // The OpenGL handle pointing to the texture.
    // The ID of the drawable that the texture was created from.
    private @DrawableRes
    final int m_drawableID;

    private Texture(int handle, @DrawableRes int drawableID)
    {
        m_handle = handle;
        m_drawableID = drawableID;
    }

    // Sets the currently active texture to this one if it isn't already and enables or disables
    // blend mode depending on whether transparency is needed or not.
    public void use(int shaderTextureUniformHandle, boolean allowTransparency)
    {
        if (m_handle != s_activeTexture)
        {
            // Bind the current texture if it isn't already bound.
            s_activeTexture = m_handle;
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_handle);
            GLES20.glUniform1i(shaderTextureUniformHandle, 0);
        }

        if (allowTransparency)
        {
            if (!s_transparencyActive)
            {
                // Turn on blending if transparency is needed but isn't already on.
                s_transparencyActive = true;
                GLES20.glEnable(GLES20.GL_BLEND);
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            }
        }
        else if (s_transparencyActive)
        {
            // Turn off blending if transparency isn't needed but it is currently on.
            s_transparencyActive = false;
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

    public int getHandle()
    {
        return m_handle;
    }

    public @DrawableRes int getDrawableID()
    {
        return m_drawableID;
    }
}
