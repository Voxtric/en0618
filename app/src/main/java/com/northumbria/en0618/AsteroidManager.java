package com.northumbria.en0618;

import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Game;

import java.util.ArrayList;
import java.util.List;

public class AsteroidManager {

    private static final @DrawableRes
    int[] ALIEN_SPRITE_DRAWABLE_IDS = new int[]
            {
                    R.drawable.asteroid_1,
                    R.drawable.asteroid_2,
                    R.drawable.asteroid_3,
                    R.drawable.asteroid_4,
                    R.drawable.asteroid_5,
                    R.drawable.asteroid_6
            };

    private List m_asteroidList = new ArrayList<>();

    private Game m_game;
    private CollisionLists m_colList;

    private float m_size = 50.0f;

    AsteroidManager(CollisionLists collisionList, Game game)
    {
        m_colList = collisionList;
        m_game = game;
    }

    void createAsteroids()
    {

    }
}
