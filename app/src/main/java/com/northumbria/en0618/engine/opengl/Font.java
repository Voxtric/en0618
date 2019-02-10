package com.northumbria.en0618.engine.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Font implements IRenderable
{
    private final static int CHAR_START = 32;
    private final static int CHAR_END = 126;
    private final static int CHAR_COUNT = ((CHAR_END - CHAR_START) + 1) + 1 ;

    private final static int CHAR_NONE = 32;
    private final static int CHAR_UNKNOWN = (CHAR_COUNT - 1);

    private final static int FONT_SIZE_MIN = 6;
    private final static int FONT_SIZE_MAX = 180;

    private static HashMap<String, Font> s_fonts = new HashMap<>();

    public static Font getFont(Context context, String fontName, int fontSize, int fontPadding)
    {
        String fullFontName = String.format(Locale.getDefault(), "%s%d%d", fontName.hashCode(), fontSize, fontPadding);
        Font font = s_fonts.get(fullFontName);
        if (font == null)
        {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            font = new Font(typeface, fontSize, fontPadding);
            s_fonts.put(fullFontName, font);
        }
        return font;
    }

    public static void clearCache()
    {
        for (Object mapEntry : s_fonts.entrySet())
        {
            Font font = (Font)((Map.Entry)mapEntry).getValue();
            Texture texture = font.getTexture();
            GLES20.glDeleteTextures(1, new int[] {texture.getHandle()}, 0);
        }
        s_fonts.clear();
    }

    private int m_cellWidth;
    private int m_cellHeight;
    private float[] m_charWidths = new float[CHAR_COUNT];
    private Sprite[] m_characterSprites = new Sprite[CHAR_COUNT];
    private Texture m_texture;

    private Font(Typeface typeface, int size, int padding)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        paint.setColor(0xffffffff);
        paint.setTypeface(typeface);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = (float)Math.ceil(Math.abs(fontMetrics.bottom) + Math.abs(fontMetrics.top));
        float fontDescent = (float)Math.ceil(Math.abs(fontMetrics.descent));

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
        s[0] = CHAR_NONE;
        paint.getTextWidths(s, 0, 1, w);
        m_charWidths[counter] = w[0];
        if (m_charWidths[counter] > charWidthMax)
        {
            charWidthMax = m_charWidths[counter];
        }
        //counter++;

        charHeight = fontHeight;

        m_cellWidth = (int)charWidthMax + (2 * padding);
        m_cellHeight = (int)charHeight + (2 * padding);
        int maxSize = m_cellWidth > m_cellHeight ? m_cellWidth : m_cellHeight;
        if (maxSize < FONT_SIZE_MIN)
        {
            throw new RuntimeException("Font is too small.");
        }
        else if (maxSize > FONT_SIZE_MAX)
        {
            throw new RuntimeException("Font is too large.");
        }
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

        Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);

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

        m_texture = Texture.createFromBitmap(bitmap, 0);
        bitmap.recycle();

        x = 0;
        y = 0;
        for (int characterIndex = 0; characterIndex < CHAR_COUNT; characterIndex++)
        {
            Texture.Region textureRegion = new Texture.Region(textureSize, textureSize, x, y, m_cellWidth - 1, m_cellHeight - 1);
            m_characterSprites[characterIndex] = new Sprite(FontShader.getInstance(), m_texture, textureRegion);
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

    public Sprite getSprite(char character)
    {
        return m_characterSprites[character - CHAR_START];
    }

    public void draw(float[] vpMatrix, float[] color, String string, float x, float y)
    {
        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x + (m_cellWidth / 2.0f), y, 0.0f);
        Matrix.scaleM(modelMatrix, 0, m_cellWidth, m_cellHeight, 0.0f);

        float[] mvpMatrix = new float[16];
        for (int charIndex = 0; charIndex < string.length(); charIndex++)
        {
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);

            int index = string.charAt(charIndex) - CHAR_START;
            m_characterSprites[index].draw(mvpMatrix, color);

            Matrix.translateM(modelMatrix, 0, m_charWidths[index] / m_cellWidth, 0, 0);
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
