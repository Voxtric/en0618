package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.DrawableRes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// A renderable texture.
public class Sprite implements IRenderable
{
    // Position co-ordinate data for rendering a sprite.
    static final int COORDS_PER_VERTEX = 3;
    private static final float VERTEX_COORDS[] = {
            -0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f,  -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f
    };
    static final int VERTEX_COORDS_STRIDE = COORDS_PER_VERTEX * 4;

    // Texture co-ordinate data for rendering a sprite.
    static final int UVS_PER_VERTEX = 2;
    private static final float VERTEX_UVS[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };
    static final int VERTEX_UV_STRIDE = UVS_PER_VERTEX * 4;

    // Draw order for drawing the vertices of the sprite.
    static final short[] INDICES = { 0, 1, 2, 0, 2, 3 };

    // Gets a sprite using a texture, creating if it it hasn't already been created.
    private static Sprite getSprite(Texture texture)
    {
        return new Sprite(SpriteShader.getInstance(), texture);
    }

    // Gets a sprite using the ID of what should be loaded in, creating the texture for it and the
    // sprite itself if it it hasn't already been created.
    public static Sprite getSprite(Context context, @DrawableRes int drawableID)
    {
        return getSprite(Texture.getTexture(context, drawableID));
    }

    // Buffers for the different attributes of a vertex
    final FloatBuffer m_vertexBuffer;
    final FloatBuffer m_uvBuffer;
    final ShortBuffer m_indexBuffer;


    final SpriteShader m_shader;
    private final Texture m_texture;
    boolean m_transparent = true;

    // Creates a sprite using the specified shader and texture.
    private Sprite(SpriteShader shader, Texture texture)
    {
        m_shader = shader;

        // Upload the vertex position co-ordinate data for rendering.
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_COORDS.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        m_vertexBuffer = vertexByteBuffer.asFloatBuffer();
        m_vertexBuffer.put(VERTEX_COORDS);
        m_vertexBuffer.position(0);

        // Upload the vertex texture co-ordinate data for rendering.
        ByteBuffer uvByteBuffer = ByteBuffer.allocateDirect(VERTEX_UVS.length * 4);
        uvByteBuffer.order(ByteOrder.nativeOrder());
        m_uvBuffer = uvByteBuffer.asFloatBuffer();
        m_uvBuffer.put(VERTEX_UVS);
        m_uvBuffer.position(0);

        // Upload the vertex draw order data for rendering.
        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(INDICES.length * 2);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        m_indexBuffer = indexByteBuffer.asShortBuffer();
        m_indexBuffer.put(INDICES);
        m_indexBuffer.position(0);

        m_texture = texture;
    }

    // Creates a sprite using the specified shader and texture, overriding the UVs to point to
    // different locations within the texture.
    Sprite(SpriteShader shader, Texture texture, Texture.Region textureRegion)
    {
        m_shader = shader;

        // Upload the vertex position co-ordinate data for rendering.
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_COORDS.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        m_vertexBuffer = vertexByteBuffer.asFloatBuffer();
        m_vertexBuffer.put(VERTEX_COORDS);
        m_vertexBuffer.position(0);

        // Upload the vertex texture co-ordinate data for rendering.
        ByteBuffer uvByteBuffer = ByteBuffer.allocateDirect(VERTEX_UVS.length * 4);
        uvByteBuffer.order(ByteOrder.nativeOrder());
        m_uvBuffer = uvByteBuffer.asFloatBuffer();
        m_uvBuffer.put(textureRegion.uvs());
        m_uvBuffer.position(0);

        // Upload the vertex draw order data for rendering.
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

    // Draws a sprite at the specified position with a specified color.
    public void draw(float[] mvpMatrix, float[] color)
    {
        Shader.useShader(m_shader);

        // Upload the world position matrix.
        int shaderMVPMatrixUniform = m_shader.getMVPMatrixUniform();
        GLES20.glUniformMatrix4fv(shaderMVPMatrixUniform, 1, false, mvpMatrix, 0);

        // Upload the vertex position co-ordinates.
        int shaderPositionAttribute = m_shader.getPositionAttribute();
        GLES20.glVertexAttribPointer(shaderPositionAttribute, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                                     false, VERTEX_COORDS_STRIDE, m_vertexBuffer);

        // Upload the vertex texture co-ordinates.
        int shaderUVAttribute = m_shader.getUVAttribute();
        GLES20.glVertexAttribPointer(shaderUVAttribute, UVS_PER_VERTEX, GLES20.GL_FLOAT,
                                     false, VERTEX_UV_STRIDE, m_uvBuffer);

        // Use the current texture.
        int shaderTextureUniform = m_shader.getTextureUniform();
        m_texture.use(shaderTextureUniform, m_transparent);

        // Upload the sprite color.
        int shaderColorUniform = m_shader.getColorUniform();
        GLES20.glUniform4fv(shaderColorUniform, 1, color, 0);

        // Draw the sprite.
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
