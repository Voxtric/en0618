package com.northumbria.en0618;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.SpriteGameObject;
import com.northumbria.en0618.engine.opengl.Sprite;

import java.util.Stack;

public class LivesManager
{
    private static final float SCREEN_DISTANCE_SIZE = 0.075f;
    private static final float SCREEN_DISTANCE_SIDE_BORDER = 0.01f;
    private static final float SCREEN_DISTANCE_BOTTOM_BORDER = 0.005f;
    private static final float SIZE_MODIFIER = 1.05f;

    private Game m_game;
    private Stack<SpriteGameObject> m_lives = new Stack<>();

    private float m_size;
    private float m_startX;
    private float m_startY;

    LivesManager(Game game)
    {
        m_game = game;

        m_size = Input.getScreenWidth() * SCREEN_DISTANCE_SIZE;
        m_startX = (Input.getScreenWidth() * SCREEN_DISTANCE_SIDE_BORDER) + (m_size * 0.5f);
        m_startY = (Input.getScreenHeight() * SCREEN_DISTANCE_BOTTOM_BORDER) + (m_size * 0.5f);
    }

    public void updateLivesDisplay(int lives)
    {
        while (lives < m_lives.size())
        {
            SpriteGameObject life = m_lives.pop();
            life.destroy();
        }
        while (lives > m_lives.size())
        {
            SpriteGameObject life = new SpriteGameObject(
                    Sprite.getSprite(m_game.getActivity(), R.drawable.player, false),
                    m_startX + (m_lives.size() * m_size * SIZE_MODIFIER),
                    m_startY,
                    m_size,
                    m_size * Player.HEIGHT_TO_WIDTH_RATIO);
            m_game.addGameObject(life);
            m_lives.push(life);
        }
    }
}
