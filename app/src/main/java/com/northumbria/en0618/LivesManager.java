package com.northumbria.en0618;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.Stack;

class LivesManager
{
    // CONSTANTS
    private static final float SCREEN_DISTANCE_SIZE = 0.075f;
    private static final float SCREEN_DISTANCE_SIDE_BORDER = 0.01f;
    private static final float SCREEN_DISTANCE_BOTTOM_BORDER = 0.005f;
    private static final float SIZE_MODIFIER = 1.05f;

    // References
    private final Game m_game;

    // List of Lives
    private final Stack<Life> m_lives = new Stack<>();

    // Uninitialised Values
    private final float m_size;
    private final float m_startX;
    private final float m_startY;

    LivesManager(Game game)
    {
        // Sets References
        m_game = game;

        // Initialises Values
        m_size = Input.getScreenWidth() * SCREEN_DISTANCE_SIZE;
        m_startX = (Input.getScreenWidth() * SCREEN_DISTANCE_SIDE_BORDER) + (m_size * 0.5f);
        m_startY = (Input.getScreenHeight() * SCREEN_DISTANCE_BOTTOM_BORDER) + (m_size * 0.5f);
    }

    public void updateLivesDisplay(int lives, boolean forceDestroy)
    {
        // Runs every frame
        while (lives < m_lives.size())
        {
            // Removes a life until LifeCount is equal to PlayerLives
            Life life = m_lives.pop();
            if (forceDestroy || m_lives.isEmpty())
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
            // Creates new lives when the player has more lives than are displayed
            Life life = new Life(m_game.getActivity(),
                    m_startX + (m_lives.size() * m_size * SIZE_MODIFIER), m_startY, m_size);
            m_game.addGameObject(life);
            m_lives.push(life);
        }
    }
}
