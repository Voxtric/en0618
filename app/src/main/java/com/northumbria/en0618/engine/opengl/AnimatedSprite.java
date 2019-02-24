package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.DrawableRes;

public class AnimatedSprite extends Sprite
{
    private int m_xFrames;
    private int m_totalFrames;
    private int m_frame = 0;

    private float m_secondsPerFrame;
    private float m_secondsInFrame = 0.0f;

    private boolean m_repeating;
    private boolean m_finished = false;

    private float m_frameWidth;
    private float m_frameHeight;
    private float m_xOffset = 0.0f;
    private float m_yOffset = 0.0f;

    public AnimatedSprite(Context context, @DrawableRes int drawableID, int xFrames, int yFrames, float secondsPerFrame, boolean repeating)
    {
        super(AnimatedSpriteShader.getInstance(),
                Texture.getTexture(context, drawableID, false),
                new Texture.Region(0.0f, 0.0f, 1.0f / (float)xFrames, 1.0f / (float)yFrames));

        m_xFrames = xFrames;
        m_totalFrames = xFrames * yFrames;

        m_secondsPerFrame = secondsPerFrame;
        m_repeating = repeating;

        m_frameWidth = 1.0f / (float)xFrames;
        m_frameHeight = 1.0f / (float)yFrames;
    }

    public void stepAnimation(float deltaTime)
    {
        m_secondsInFrame += deltaTime;
        while (m_secondsInFrame > m_secondsPerFrame)
        {
            m_secondsInFrame -= m_secondsPerFrame;
            m_frame++;
            if (m_frame >= m_totalFrames)
            {
                if (m_repeating)
                {
                    m_frame = 0;
                }
                else
                {
                    m_finished = true;
                }
            }

            m_xOffset = (m_frame % m_xFrames) * m_frameWidth;
            m_yOffset = (m_frame / m_xFrames) * m_frameHeight;
        }
    }

    public boolean isFinished()
    {
        return m_finished;
    }

    public void draw(float[] mvpMatrix, float[] color)
    {
        if (!m_finished)
        {
            Shader.useShader(m_shader);

            // Upload the world position matrix.
            int shaderMVPMatrixUniform = m_shader.getMVPMatrixUniform();
            GLES20.glUniformMatrix4fv(shaderMVPMatrixUniform, 1, false, mvpMatrix, 0);

            int shaderUVOffsetUniform = ((AnimatedSpriteShader) m_shader).getUVOffsetUniform();
            GLES20.glUniform2f(shaderUVOffsetUniform, m_xOffset, m_yOffset);

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
            getTexture().use(shaderTextureUniform, m_transparent);

            // Upload the sprite color.
            int shaderColorUniform = m_shader.getColorUniform();
            GLES20.glUniform4fv(shaderColorUniform, 1, color, 0);

            // Draw the sprite.
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.length, GLES20.GL_UNSIGNED_SHORT, m_indexBuffer);
        }
    }
}
