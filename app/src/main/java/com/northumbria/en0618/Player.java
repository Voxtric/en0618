package com.northumbria.en0618;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.Input;

public class Player extends CollidableGameObject {

    private Game m_game;
    public int m_lives = 3;
    public int score = 0;


    public Player(Game game) {
        super(game.getActivity(),
                Sprite.getSprite(game.getActivity(), R.drawable.player, true),
                Input.getScreenWidth() / 2.0f, 75.0f, 200, 125);
        m_game = game;
    }

    @Override
    public void update(float deltaTime)
    {
        float screenWidth = Input.getScreenWidth();
        if (Input.isTouched())
        {
            float touchX = Input.getCurrentTouchX();
            float x = getX();
            if (touchX > screenWidth / 2.0f)
            {
                if (x < screenWidth)
                {
                    moveBy(800.0f * deltaTime, 0.0f);
                }
            }
            else if (x > 0.0f)
            {
                moveBy(-800.0f * deltaTime, 0.0f);
            }
        }
//        else if (Input.rotationSensorAvailable())
//        {
//            float deviceRoll = Input.getDeviceRoll();
//            if (deviceRoll > 5.0f)
//            {
//                moveBy(800.0f * deltaTime, 0.0f);
//            }
//            else if (deviceRoll < -5.0f)
//            {
//                moveBy(-800.0f * deltaTime, 0.0f);
//            }
//        }
    }

    @Override
    public void collidedWith(objectType other)
    {
        if(other == objectType.bullet)
        {
            m_lives--;
        }
        else
        {
            m_lives = 0;
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
