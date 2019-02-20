package com.northumbria.en0618;

import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.ArrayList;
import java.util.List;

public class AsteroidManager {

    private static final float ASTEROID_SIZE = 75.0f;
    private static final int ASTEROIDS_PER_BARRIER = 3;
    private static final int NUMBER_OF_BARRIERS = 2;

    private float FIRST_BARRIER_X;
    private float SECOND_BARRIER_X;

    private static final @DrawableRes

    int[] ASTEROID_SPRITE_DRAWABLE_IDS = new int[]
            {
                    R.drawable.asteroid_1,
                    R.drawable.asteroid_2,
                    R.drawable.asteroid_3,
                    R.drawable.asteroid_4,
                    R.drawable.asteroid_5,
                    R.drawable.asteroid_6
            };

    private Game m_game;
    private CollisionLists m_colList;

    private float m_size = 50.0f;

    AsteroidManager(CollisionLists collisionList, Game game)
    {
        m_colList = collisionList;
        m_game = game;
        FIRST_BARRIER_X = Input.getScreenWidth() * 0.33f;
        SECOND_BARRIER_X = Input.getScreenWidth() * 0.66f;
    }

    void createAsteroids()
    {
        //TODO - CONSTANTS / RETHINKING
        float baseXModifier = 85.0f; // NEEDS RETHINKING, CURRENTLY REMOVED FROM BASE X, THEN ADDED * I
        float baseYPosition = 320.0f; // BASE Y POSITION, MAKE CONSTANT
        float asteroidYModifier = -40.0f; // Disjoints the base Y position

        for(int i = 0; i < ASTEROIDS_PER_BARRIER; i++)
        {
            Asteroid tempFirstAsteroid = new Asteroid(m_game.getActivity(),
                    ASTEROID_SPRITE_DRAWABLE_IDS[i], FIRST_BARRIER_X - baseXModifier + (baseXModifier * i),
                    baseYPosition + asteroidYModifier, ASTEROID_SIZE);
            m_game.addGameObject(tempFirstAsteroid);
            m_colList.addAsteroid(tempFirstAsteroid);


            Asteroid tempSecondAsteroid = new Asteroid(m_game.getActivity(),
                    ASTEROID_SPRITE_DRAWABLE_IDS[5-i], SECOND_BARRIER_X - baseXModifier + (baseXModifier * i),
                    baseYPosition + asteroidYModifier, ASTEROID_SIZE);
            m_game.addGameObject(tempSecondAsteroid);
            m_colList.addAsteroid(tempSecondAsteroid);

            asteroidYModifier = -asteroidYModifier; // Flips so that asteroids spawn down, Up, down, Up
        }
    }
}
