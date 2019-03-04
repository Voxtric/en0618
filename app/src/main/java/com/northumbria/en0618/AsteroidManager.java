package com.northumbria.en0618;

import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.Random;

class AsteroidManager
{
    private static final @DrawableRes int[] ASTEROID_SPRITE_DRAWABLE_IDS = new int[]
    {
            R.drawable.asteroid_1,
            R.drawable.asteroid_2,
            R.drawable.asteroid_3,
            R.drawable.asteroid_4,
            R.drawable.asteroid_5,
            R.drawable.asteroid_6
    }; // Stores the six asteroid sprites in an Array to be looped through later

    // CONSTANT VARIABLES
    private static final int SWAP_COUNT = 6;

    private static final int ASTEROIDS_PER_BARRIER = 3;
    private static final int BARRIER_COUNT = 2;

    private static final float SCREEN_DISTANCE_SIZE = 0.04f;
    private static final float SCREEN_DISTANCE_BARRIER_WIDTH = 0.11f;
    private static final float SCREEN_DISTANCE_BOTTOM_BORDER = 0.17f;
    private static final float SCREEN_DISTANCE_Y_OFFSET = -0.01f;

    // Object References
    private final CollisionLists m_collisionList;
    private final Game m_game;

    // Uninitialised Values
    private final float m_size;
    private final float m_distanceBetweenBarrierCenters;
    private final float m_barrierWidth;
    private final float m_startY;

    AsteroidManager(CollisionLists collisionList, Game game)
    {
        // Initialises values
        m_collisionList = collisionList;
        m_game = game;

        m_size = Input.getScreenWidth() * SCREEN_DISTANCE_SIZE;
        m_distanceBetweenBarrierCenters = Input.getScreenWidth() / (float)(BARRIER_COUNT + 1);
        m_barrierWidth = Input.getScreenWidth() * SCREEN_DISTANCE_BARRIER_WIDTH;
        m_startY = Input.getScreenHeight() * SCREEN_DISTANCE_BOTTOM_BORDER;
    }

    void createAsteroids()
    {
        // Creates asteroids with random sprites.
        Random random = m_game.getRandom();
        for (int i = 0; i < SWAP_COUNT; i++)
        {
            int swapFrom = random.nextInt(ASTEROID_SPRITE_DRAWABLE_IDS.length);
            int swapTo = random.nextInt(ASTEROID_SPRITE_DRAWABLE_IDS.length);
            @DrawableRes int tempDrawableID = ASTEROID_SPRITE_DRAWABLE_IDS[swapTo];
            ASTEROID_SPRITE_DRAWABLE_IDS[swapTo] = ASTEROID_SPRITE_DRAWABLE_IDS[swapFrom];
            ASTEROID_SPRITE_DRAWABLE_IDS[swapFrom] = tempDrawableID;
        }

        // Creates Asteroids within specific constraints per 'Shield'.
        int drawableIDIndex = 0;
        float barrierXCenter = m_distanceBetweenBarrierCenters;
        for (int i = 0; i < BARRIER_COUNT; i++)
        {
            float asteroidYModifier = Input.getScreenHeight() * SCREEN_DISTANCE_Y_OFFSET; // Disjoints the base Y position
            float xPosition = barrierXCenter - (m_barrierWidth * 0.5f) + (m_size * 0.5f);
            for (int j = 0; j < ASTEROIDS_PER_BARRIER; j++)
            {
                Asteroid asteroid = new Asteroid(m_game.getActivity(),
                        ASTEROID_SPRITE_DRAWABLE_IDS[drawableIDIndex % ASTEROID_SPRITE_DRAWABLE_IDS.length],
                        xPosition, m_startY + asteroidYModifier, m_size);
                m_game.addGameObject(asteroid);
                m_collisionList.addAsteroid(asteroid);

                asteroidYModifier = -asteroidYModifier; // Flips so that asteroids spawn down, Up, down, Up
                xPosition += m_barrierWidth / (float)ASTEROIDS_PER_BARRIER;
                drawableIDIndex++;
            }

            barrierXCenter += m_distanceBetweenBarrierCenters;
        }
    }
}
