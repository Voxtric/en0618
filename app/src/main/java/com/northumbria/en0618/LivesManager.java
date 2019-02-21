package com.northumbria.en0618;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.Stack;

class LivesManager
{
    private static final float SCREEN_DISTANCE_SIZE = 0.075f;
    private static final float SCREEN_DISTANCE_SIDE_BORDER = 0.01f;
    private static final float SCREEN_DISTANCE_BOTTOM_BORDER = 0.005f;
    private static final float SIZE_MODIFIER = 1.05f;

    private final Game m_game;
    private final Stack<Life> m_lives = new Stack<>();

    private final float m_size;
    private final float m_startX;
    private final float m_startY;

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
            Life life = m_lives.pop();
            if (m_lives.isEmpty())
            {
                life.destroy();
            }
            else
            {
                life.lose();
            }
        }
        while (lives > m_lives.size())
        {
            Life life = new Life(m_game.getActivity(),
                    m_startX + (m_lives.size() * m_size * SIZE_MODIFIER), m_startY, m_size);
            m_game.addGameObject(life);
            m_lives.push(life);
        }
    }
}
