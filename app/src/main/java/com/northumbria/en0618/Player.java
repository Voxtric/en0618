package com.northumbria.en0618;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.Input;

public class Player extends CollidableGameObject
{
    private static final float BORDER = 40.0f;

    public static final String PREFERENCE_KEY_INPUT_METHOD = "input_method";
    public static final int INPUT_METHOD_SCREEN_SIDE = 0;
    public static final int INPUT_METHOD_PLAYER_SIDE = 1;
    public static final int INPUT_METHOD_SCREEN_TILT = 3;

    private static int s_inputMethod;

    public static void updateInputMethod(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        s_inputMethod = preferences.getInt("input_method", INPUT_METHOD_SCREEN_SIDE);
    }

    public int m_lives = 3;
    public int score = 0;


    Player(Context context)
    {
        super(context, Sprite.getSprite(context, R.drawable.player, true),
                Input.getScreenWidth() / 2.0f, 75.0f, 200, 125);
        updateInputMethod(context);
    }

    @Override
    public void update(float deltaTime)
    {
        float screenWidth = Input.getScreenWidth();
        float x = getX();
        float halfWidth = getXSize() * 0.5f;

        if (s_inputMethod == INPUT_METHOD_SCREEN_TILT)
        {
            float deviceRoll = Input.getDeviceRoll();
            if (deviceRoll > 5.0f)
            {
                if (x < (screenWidth - halfWidth - BORDER))
                {
                    moveBy(800.0f * deltaTime, 0.0f);
                }
            }
            else if ((deviceRoll < -5.0f) && (x > (halfWidth + BORDER)))
            {
                moveBy(-800.0f * deltaTime, 0.0f);
            }
        }
        else if (s_inputMethod == INPUT_METHOD_PLAYER_SIDE)
        {
            if (Input.isTouched())
            {
                float touchX = Input.getCurrentTouchX();
                if (touchX > x)
                {
                    if (x < (screenWidth - halfWidth - BORDER))
                    {
                        float xMove = Math.min(800.0f * deltaTime, touchX - x);
                        moveBy(xMove, 0.0f);
                    }
                }
                else if ((touchX < x) && (x > (halfWidth + BORDER)))
                {
                    float xMove = Math.min(800.0f * -deltaTime, x - touchX);
                    moveBy(xMove, 0.0f);
                }
            }
        }
        else // if (s_inputMethod == INPUT_METHOD_SCREEN_SIDE)
        {
            if (Input.isTouched())
            {
                float touchX = Input.getCurrentTouchX();
                if (touchX > (screenWidth * 0.5f))
                {
                    if (x < (screenWidth - halfWidth - BORDER))
                    {
                        moveBy(800.0f * deltaTime, 0.0f);
                    }
                }
                else if (x > (halfWidth + BORDER))
                {
                    moveBy(-800.0f * deltaTime, 0.0f);
                }
            }
        }
    }

    public boolean gameOver()
    {
        return m_lives <= 0;
    }

    public boolean newLevel()
    {
        moveBy(0.0f, 100.0f);
        boolean newLevel = false;
        if(getY() >= Input.getScreenHeight())
        {
            newLevel = true;
            setPosition(Input.getScreenWidth() / 2.0f, 75);
        }
        return newLevel;
    }
}
