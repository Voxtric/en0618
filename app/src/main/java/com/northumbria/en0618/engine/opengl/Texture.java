package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.DrawableRes;
import android.util.SparseArray;


public class Texture
{
    public static class Region
    {
        public final float u;
        public final float v;
        public final float width;
        public final float height;

        public Region(float texWidth, float texHeight, float x, float y, float width, float height)
        {
            u = x / texWidth;
            v = y / texHeight;
            this.width = width / texWidth;
            this.height = height / texHeight;
        }

        public float[] uvs()
        {
            return new float[] { u, v, u, v + height, u + width, v + height, u + width, v};
        }
    }

    private static SparseArray<Texture> s_textures = new SparseArray<>();
    private static SparseArray<CollisionMask> s_collisionMasks = new SparseArray<>();

    private static int s_activeTexture = -1;
    private static boolean s_transparencyActive = false;

    public static Texture getTexture(Context context, @DrawableRes int drawableID)
    {
        return getTexture(context, drawableID, false);
    }

    public static Texture getTexture(Context context, @DrawableRes int drawableID, boolean generateCollisionMask)
    {
        Texture texture = s_textures.get(drawableID, null);
        if (texture == null)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableID, options);

            texture = createFromBitmap(bitmap, drawableID);
            cacheTexture(texture, drawableID);

            if (generateCollisionMask && (s_collisionMasks.get(drawableID) == null))
            {
                s_collisionMasks.put(drawableID, new CollisionMask(bitmap));
            }

            bitmap.recycle();
        }
        return texture;
    }

    public static Texture createFromBitmap(Bitmap bitmap, @DrawableRes int drawableID)
    {
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        return new Texture(textureHandle[0], drawableID);
    }

    public static void cacheTexture(Texture texture, int id)
    {
        s_textures.put(id, texture);
    }

    public static CollisionMask getCollisionMask(Context context, @DrawableRes int drawableID)
    {
        CollisionMask collisionMask = s_collisionMasks.get(drawableID, null);
        if (collisionMask == null)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableID, options);
            collisionMask = new CollisionMask(bitmap);
            s_collisionMasks.put(drawableID, collisionMask);
            bitmap.recycle();
        }
        return collisionMask;
    }

    public static void clearCache()
    {
        int textureCount = s_textures.size();
        for (int i = 0; i < textureCount; i++)
        {
            int key = s_textures.keyAt(i);
            GLES20.glDeleteTextures(1, new int[] {s_textures.get(key).getHandle()}, 0);
        }
        s_textures.clear();
        s_activeTexture = -1;
        s_transparencyActive = false;
    }

    private int m_handle;
    private @DrawableRes int m_drawableID;

    private Texture(int handle, @DrawableRes int drawableID)
    {
        m_handle = handle;
        m_drawableID = drawableID;
    }

    private Texture(int handle)
    {
        m_handle = handle;
        m_drawableID = 0;
    }

    public void use(int shaderTextureUniformHandle, boolean allowTransparency)
    {
        if (m_handle != s_activeTexture)
        {
            s_activeTexture = m_handle;
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_handle);
            GLES20.glUniform1i(shaderTextureUniformHandle, 0);
        }

        if (allowTransparency)
        {
            if (!s_transparencyActive)
            {
                s_transparencyActive = true;
                GLES20.glEnable(GLES20.GL_BLEND);
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            }
        }
        else if (s_transparencyActive)
        {
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
