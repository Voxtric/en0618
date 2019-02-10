package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.DrawableRes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

public class Sprite implements IRenderable
{
    private static final int COORDS_PER_VERTEX = 3;
    private static final float VERTEX_COORDS[] = {
            -0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f,  -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f
    };
    private static final int VERTEX_COORD_STRIDE = COORDS_PER_VERTEX * 4;

    private static final int UVS_PER_VERTEX = 2;
    private static final float VERTEX_UVS[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };
    private static final int VERTEX_UV_STRIDE = UVS_PER_VERTEX * 4;

    private static final short[] INDICES = { 0, 1, 2, 0, 2, 3 };
    private static final int VERTEX_COUNT = VERTEX_COORDS.length / COORDS_PER_VERTEX;

    private static HashMap<Texture, Sprite> s_sprites = new HashMap<>();

    public static Sprite getSprite(Texture texture)
    {
        Sprite sprite = s_sprites.get(texture);
        if (sprite == null)
        {
            sprite = new Sprite(SpriteShader.getInstance(), texture);
            s_sprites.put(texture, sprite);
        }
        return sprite;
    }

    public static Sprite getSprite(Context context, @DrawableRes int drawableID, boolean generateCollisionMask)
    {
        return getSprite(Texture.getTexture(context, drawableID, generateCollisionMask));
    }

    public static Sprite getSprite(Context context, @DrawableRes int drawableID)
    {
        return getSprite(Texture.getTexture(context, drawableID));
    }

    public static void clearCache()
    {
        s_sprites.clear();
    }

    private FloatBuffer m_vertexBuffer;
    private FloatBuffer m_uvBuffer;
    private ShortBuffer m_indexBuffer;

    private SpriteShader m_shader;
    private Texture m_texture;
    private boolean m_transparent = true;

    private Sprite(SpriteShader shader, Texture texture)
    {
        m_shader = shader;

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_COORDS.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        m_vertexBuffer = vertexByteBuffer.asFloatBuffer();
        m_vertexBuffer.put(VERTEX_COORDS);
        m_vertexBuffer.position(0);

        ByteBuffer uvByteBuffer = ByteBuffer.allocateDirect(VERTEX_UVS.length * 4);
        uvByteBuffer.order(ByteOrder.nativeOrder());
        m_uvBuffer = uvByteBuffer.asFloatBuffer();
        m_uvBuffer.put(VERTEX_UVS);
        m_uvBuffer.position(0);

        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(INDICES.length * 2);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        m_indexBuffer = indexByteBuffer.asShortBuffer();
        m_indexBuffer.put(INDICES);
        m_indexBuffer.position(0);

        m_texture = texture;
    }

    Sprite(SpriteShader shader, Texture texture, Texture.Region textureRegion)
    {
        m_shader = shader;

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_COORDS.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        m_vertexBuffer = vertexByteBuffer.asFloatBuffer();
        m_vertexBuffer.put(VERTEX_COORDS);
        m_vertexBuffer.position(0);

        ByteBuffer uvByteBuffer = ByteBuffer.allocateDirect(VERTEX_UVS.length * 4);
        uvByteBuffer.order(ByteOrder.nativeOrder());
        m_uvBuffer = uvByteBuffer.asFloatBuffer();
        m_uvBuffer.put(textureRegion.uvs());
        m_uvBuffer.position(0);

        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(INDICES.length * 2);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        m_indexBuffer = indexByteBuffer.asShortBuffer();
        m_indexBuffer.put(INDICES);
        m_indexBuffer.position(0);

        m_texture = texture;
    }

    public void setUseTransparency(boolean useTransparency)
    {
        m_transparent = useTransparency;
    }

    public void draw(float[] mvpMatrix, float[] color)
    {
        Shader.useShader(m_shader);

        int shaderMVPMatrixUniform = m_shader.getMVPMatrixUniform();
        GLES20.glUniformMatrix4fv(shaderMVPMatrixUniform, 1, false, mvpMatrix, 0);

        int shaderPositionAttribute = m_shader.getPositionAttribute();
        GLES20.glVertexAttribPointer(shaderPositionAttribute, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                                     false, VERTEX_COORD_STRIDE, m_vertexBuffer);

        int shaderUVAttribute = m_shader.getUVAttribute();
        GLES20.glVertexAttribPointer(shaderUVAttribute, UVS_PER_VERTEX, GLES20.GL_FLOAT,
                                     false, VERTEX_UV_STRIDE, m_uvBuffer);

        int shaderTextureUniform = m_shader.getTextureUniform();
        m_texture.use(shaderTextureUniform, m_transparent);

        int shaderColorUniform = m_shader.getColorUniform();
        GLES20.glUniform4fv(shaderColorUniform, 1, color, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.length, GLES20.GL_UNSIGNED_SHORT, m_indexBuffer);
    }

    @Override
    public Shader getShader()
    {
        return m_shader;
    }

    @Override
    public Texture getTexture()
    {
        return m_texture;
    }

    @Override
    public boolean isTransparent()
    {
        return m_transparent;
    }
}
