package com.northumbria.en0618;

import android.content.Context;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.Bullet;

public class Player extends CollidableGameObject {

    int bulletCountdown = 0;
    Game m_game;

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
//        if(bulletCountdown >= 20)
//        {
//            Bullet bullet = new Bullet(m_game.getActivity(), getX(), getY(), direction.UP);
//            m_game.addGameObject(bullet);
//            bulletCountdown = 0;
//        }
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
        bulletCountdown = bulletCountdown + 1;
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
}
