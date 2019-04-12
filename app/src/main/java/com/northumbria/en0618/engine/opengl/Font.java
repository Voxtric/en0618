package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Matrix;

// A collection of sprites that can be used to render individual characters to display text.
public class Font implements IRenderable
{
    // Only basic ascii is supported.
    private final static int CHAR_START = 32;
    private final static int CHAR_END = 126;
    private final static int CHAR_COUNT = ((CHAR_END - CHAR_START) + 1) + 1 ;

    private final static int CHAR_NONE = 32;
    private final static int CHAR_UNKNOWN = (CHAR_COUNT - 1);

    // Font size bounds.
    private final static int FONT_SIZE_MIN = 6;
    private final static int FONT_SIZE_MAX = 180;

    public static Font getFont(Context context, String fontName, int fontSize, int fontPadding)
    {
        // Create and cache a new font if it doesn't already exist within the cache.
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        return new Font(typeface, fontSize, fontPadding);
    }

    private final int m_cellWidth;
    private final int m_cellHeight;
    private final float[] m_charWidths = new float[CHAR_COUNT];   // Width of each individual character.
    private final Sprite[] m_characterSprites = new Sprite[CHAR_COUNT];   // Sprites for each character.
    private Texture m_texture;
    private final int m_padding;

    private Font(Typeface typeface, int size, int padding)
    {
        m_padding = padding;

        // Use inbuilt Android functions to determine font characteristics.
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        paint.setColor(0xffffffff);
        paint.setTypeface(typeface);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = (float)Math.ceil(Math.abs(fontMetrics.bottom) + Math.abs(fontMetrics.top));
        float fontDescent = (float)Math.ceil(Math.abs(fontMetrics.descent));

        // Store the correct width for each individual character.
        char[] s = new char[2];
        float charWidthMax = 0;
        float charHeight;
        float[] w = new float[2];
        int counter = 0;
        for (char characterIndex = CHAR_START; characterIndex <= CHAR_END; characterIndex++)
        {
            s[0] = characterIndex;
            paint.getTextWidths(s, 0, 1, w);
            m_charWidths[counter] = w[0];
            if (m_charWidths[counter] > charWidthMax)
            {
                charWidthMax = m_charWidths[counter];
            }
            counter++;
        }
        // Get final character width.
        s[0] = CHAR_NONE;
        paint.getTextWidths(s, 0, 1, w);
        m_charWidths[counter] = w[0];
        if (m_charWidths[counter] > charWidthMax)
        {
            charWidthMax = m_charWidths[counter];
        }

        charHeight = fontHeight;

        m_cellWidth = (int)charWidthMax + (2 * padding);    // Padding is applied to each side.
        m_cellHeight = (int)charHeight + (2 * padding); // Padding is applied to each side.

        // Textures can only be so big, so throw an exception if the font size
        // would exceed the texture size.
        int maxSize = m_cellWidth > m_cellHeight ? m_cellWidth : m_cellHeight;
        if (maxSize < FONT_SIZE_MIN)
        {
            throw new RuntimeException("Font is too small.");
        }
        else if (maxSize > FONT_SIZE_MAX)
        {
            throw new RuntimeException("Font is too large.");
        }

        // Determine the size of the texture necessary to hold all the characters.
        int textureSize;
        if (maxSize <= 24)
        {
            textureSize = 256;
        }
        else if (maxSize <= 40)
        {
            textureSize = 512;
        }
        else if (maxSize <= 80)
        {
            textureSize = 1024;
        }
        else
        {
            textureSize = 2048;
        }

        // Create a bitmap that will serve as a texture atlas for all the characters.
        Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);

        // Iterate through each character and draw it to the bitmap in a grid.
        float x = padding;
        float y = (m_cellHeight - 1) - fontDescent - padding;
        for (char characterIndex = CHAR_START; characterIndex <= CHAR_END; characterIndex++)
        {
            s[0] = characterIndex;
            canvas.drawText(s, 0, 1, x, y, paint);
            x += m_cellWidth;
            if ((x + m_cellWidth - padding) > textureSize)
            {
                x = padding;
                y += m_cellHeight;
            }
        }
        s[0] = CHAR_NONE;
        canvas.drawText(s, 0, 1, x, y, paint);

        // Use the bitmap to create an OpenGL texture.
        m_texture = Texture.createFromBitmap(bitmap, 0);
        bitmap.recycle();

        // Iterate over every character to create a sprite with the correct texture co-ordinates
        // for the character texture atlas.
        x = 0;
        y = 0;
        for (int characterIndex = 0; characterIndex < CHAR_COUNT; characterIndex++)
        {
            Texture.Region textureRegion = new Texture.Region(
                    textureSize, textureSize, x, y, m_cellWidth - 1, m_cellHeight - 1);
            m_characterSprites[characterIndex] =
                    new Sprite(FontShader.getInstance(), m_texture, textureRegion);
            x += m_cellWidth;
            if (x + m_cellWidth > textureSize)
            {
                x = 0;
                y += m_cellHeight;
            }
        }
    }

    @Override
    public Shader getShader()
    {
        return FontShader.getInstance();
    }

    @Override
    public Texture getTexture()
    {
        return m_texture;
    }

    @Override
    public boolean isTransparent()
    {
        return true;
    }

    // Draws a string at the specified position with the specified color.
    public void draw(String string, float[] vpMatrix, float x, float y, float[] color)
    {
        // Set the starting position matrix.
        // (Left side of first character halfway through it vertically).
        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x + (m_cellWidth / 2.0f), y, 0.0f);
        Matrix.scaleM(modelMatrix, 0, m_cellWidth, m_cellHeight, 0.0f);

        float[] mvpMatrix = new float[16];
        for (int charIndex = 0; charIndex < string.length(); charIndex++)
        {
            // Multiply the model matrix with the view projection matrix to create the MVP matrix.
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);

            // Draw the character sprite at the correct location.
            int index = string.charAt(charIndex) - CHAR_START;
            if (index < 0 || index > m_characterSprites.length)
            {
                index = CHAR_UNKNOWN;
            }
            m_characterSprites[index].draw(mvpMatrix, color);

            // Translate the model matrix along using the width of the char just drawn.
            Matrix.translateM(modelMatrix, 0, ((m_charWidths[index] + m_padding) / m_cellWidth), 0, 0);
        }
    }

    public float getHeight()
    {
        return m_cellHeight;
    }

    public float getCharWidth(char character)
    {
        return m_charWidths[character - CHAR_START];
    }
}
