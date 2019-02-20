package com.northumbria.en0618;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.TextGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.Input;

import java.util.Locale;

public class Player extends CollidableGameObject
{
    private static final float SCREEN_DISTANCE_PER_SECOND = 0.6f;
    private static final float SCREEN_DISTANCE_SIDE_BORDER = 0.02f;
    private static final float SCREEN_DISTANCE_BOTTOM_BORDER = 0.05f;
    private static final float SCREEN_DISTANCE_WIDTH = 0.2f;
    private static final float HEIGHT_TO_WIDTH_RATIO = 0.6f;
    private static final float LEVEL_COMPLETE_SPEED_MODIFIER = 3.0f;

    private static final int START_LIVES_COUNT = 3;

    public static final String PREFERENCE_KEY_INPUT_METHOD = "input_method";
    public static final int INPUT_METHOD_SCREEN_SIDE = 0;
    public static final int INPUT_METHOD_PLAYER_SIDE = 1;
    public static final int INPUT_METHOD_SCREEN_TILT = 3;
    private LifeManager m_lifeManager;

    private static int s_inputMethod;

    public static void updateInputMethod(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        s_inputMethod = preferences.getInt(PREFERENCE_KEY_INPUT_METHOD, INPUT_METHOD_SCREEN_SIDE);
    }

    private int m_lives = START_LIVES_COUNT;
    private int m_score = 0;

    private float m_moveSpeed;
    private float m_sideBorder;

    private TextGameObject m_scoreTracker;

    Player(Game game, TextGameObject scoreTracker)
    {
        super(game.getActivity(),
                Sprite.getSprite(game.getActivity(), R.drawable.player, true),
                Input.getScreenWidth() * 0.5f,
                Input.getScreenHeight() * SCREEN_DISTANCE_BOTTOM_BORDER,
                Input.getScreenWidth() * SCREEN_DISTANCE_WIDTH,
                (Input.getScreenWidth() * SCREEN_DISTANCE_WIDTH) * HEIGHT_TO_WIDTH_RATIO);
        updateInputMethod(game.getActivity());

        m_moveSpeed = Input.getScreenWidth() * SCREEN_DISTANCE_PER_SECOND;
        m_sideBorder = Input.getScreenHeight() * SCREEN_DISTANCE_SIDE_BORDER;
        m_scoreTracker = scoreTracker;
        m_lifeManager = new LifeManager(game, 500.0f, 3);
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
            if (deviceRoll > 0.0f)
            {
                if (x < (screenWidth - halfWidth - m_sideBorder))
                {
                    moveBy(m_moveSpeed* deltaTime, 0.0f);
                }
            }
            else if ((deviceRoll < 0.0f) && (x > (halfWidth + m_sideBorder)))
            {
                moveBy(-m_moveSpeed * deltaTime, 0.0f);
            }
        }
        else if (s_inputMethod == INPUT_METHOD_PLAYER_SIDE)
        {
            if (Input.isTouched())
            {
                float touchX = Input.getCurrentTouchX();
                if (touchX > x)
                {
                    if (x < (screenWidth - halfWidth - m_sideBorder))
                    {
                        float xMove = Math.min(m_moveSpeed * deltaTime, touchX - x);
                        if (xMove > 1.0f)
                        {
                            moveBy(xMove, 0.0f);
                        }
                    }
                }
                else if ((touchX < x) && (x > (halfWidth + m_sideBorder)))
                {
                    float xMove = Math.min(m_moveSpeed * deltaTime, x - touchX);
                    moveBy(-xMove, 0.0f);
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
                    if (x < (screenWidth - halfWidth - m_sideBorder))
                    {
                        moveBy(m_moveSpeed * deltaTime, 0.0f);
                    }
                }
                else if (x > (halfWidth + m_sideBorder))
                {
                    moveBy(-m_moveSpeed * deltaTime, 0.0f);
                }
            }
        }
    }

    public boolean isDead()
    {
        // Returns the players living state based off of lives
        return m_lives <= 0 || isDestroyed();
    }

    public boolean newLevel(float deltaTime)
    {
        // Player Moves upwards until they are off the screen. Once they are, returns true.
        moveBy(0.0f, (m_moveSpeed * LEVEL_COMPLETE_SPEED_MODIFIER) * deltaTime);
        boolean newLevel = false;
        if(getY() >= Input.getScreenHeight())
        {
            newLevel = true;
            setPosition(Input.getScreenWidth() * 0.5f, Input.getScreenHeight() * SCREEN_DISTANCE_BOTTOM_BORDER);
        }
        return newLevel;
    }

    public void addScore(int score)
    {
        m_score += score;
        m_scoreTracker.setText(String.format(Locale.getDefault(), "Score: %d", m_score));
    }

    public int getScore()
    {
        return m_score;
    }

    public boolean consumeLife()
    {
        m_lives--;
        m_lifeManager.loseLife();
        return m_lives == 0;
    }
}
