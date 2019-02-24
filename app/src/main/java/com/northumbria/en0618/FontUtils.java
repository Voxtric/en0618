package com.northumbria.en0618;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

class FontUtils
{
    private static final HashMap<String, Typeface> s_fonts = new HashMap<>();

    public static void setFont(ViewGroup viewGroup, String fontPath)
    {
        Context context = viewGroup.getContext();
        Typeface font = s_fonts.get(fontPath);
        if (font == null)
        {
            font = Typeface.createFromAsset(context.getAssets(), fontPath);
            s_fonts.put(fontPath, font);
        }
        setAllFonts(viewGroup, font);
    }

    private static void setAllFonts(ViewGroup viewGroup, Typeface font)
    {
        for (int i = 0; i < viewGroup.getChildCount(); i++)
        {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup)
            {
                setAllFonts((ViewGroup)child, font);
            }
            else if (child instanceof Button)
            {
                ((Button)child).setTypeface(font);
            }
            else if (child instanceof TextView)
            {
                ((TextView)child).setTypeface(font);
            }
        }
    }
}
