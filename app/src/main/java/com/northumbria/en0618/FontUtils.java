package com.northumbria.en0618;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

class FontUtils
{
    public static void setAllFonts(ViewGroup viewGroup, Typeface font)
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
