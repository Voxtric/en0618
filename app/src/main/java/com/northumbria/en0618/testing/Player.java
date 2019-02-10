package com.northumbria.en0618.testing;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.SpriteGameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.R;
import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.opengl.Texture;

public class Player extends CollidableGameObject
{
    public Player(Game game)
    {
        super(game.getActivity(),
              Sprite.getSprite(game.getActivity(), R.drawable.player, true),
              Input.getScreenWidth() / 2.0f, 75.0f, 200, 125);
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
}
