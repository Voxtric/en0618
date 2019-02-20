package com.northumbria.en0618;

import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.SpriteGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

import java.util.ArrayList;
import java.util.List;

public class LifeManager {

    private float m_yPlacement;
    private float m_xPlacement;
    private static final float SPRITE_SIZE = 50.0f;
    private static final float X_DISPLACEMENT = SPRITE_SIZE + 20.0f;
    private Game m_game;
    private List<SpriteGameObject> m_lives = new ArrayList<>();
    private static final @DrawableRes int LIFE_SPRITE = R.drawable.player;

    LifeManager(Game game, float xPlacement, int numberOfLives)
    {
        m_yPlacement = Input.getScreenHeight() - 80.0f - SPRITE_SIZE;
        m_xPlacement = xPlacement;
        m_game = game;
        drawLives(numberOfLives);
    }

    private void drawLives(int numOfLives)
    {
        for(int i = 0; i < numOfLives; i++)
        {
            SpriteGameObject newLife = new SpriteGameObject(Sprite.getSprite(m_game.getActivity(), LIFE_SPRITE, false),
                    m_xPlacement + (i * X_DISPLACEMENT), m_yPlacement, SPRITE_SIZE, SPRITE_SIZE);
            m_lives.add(newLife);
            m_game.addGameObject(newLife);
        }
    }

    public void loseLife()
    {
        int lifeCount = m_lives.size();
        if(lifeCount > 0)
        {
            SpriteGameObject life = m_lives.get(lifeCount - 1);
            life.destroy();
            m_lives.remove(lifeCount - 1);
        }
    }
}
