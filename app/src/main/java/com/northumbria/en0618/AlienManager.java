package com.northumbria.en0618;


import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlienManager
{
    private static final float BORDER = 40.0f;
    private static final float BOSS_SPAWN_WAIT = 15.0f; // In seconds
    private static final float SHOT_SPAWN_WAIT = 2.5f;  // In seconds

    // Defines the number of Aliens to spawn
    public static final int COLUMNS = 5;
    public static final int ROWS = 4;
    private static final int MAX_COUNT = COLUMNS * ROWS;
    private static final int STEPS_TO_PLAYER = 7;

    private float m_timeToBossSpawn = BOSS_SPAWN_WAIT;
    private float m_timeToShotSpawn = SHOT_SPAWN_WAIT;

    private List<List<Alien>> m_alienColumns = new ArrayList<>();
    private CollisionLists m_colList;
    private Game m_game;

    private float m_alienMoveSpeed = -70.0f;
    private float m_alienSize = 100.0f;

    private Random m_random = new Random(System.currentTimeMillis());

    AlienManager(CollisionLists collisionList, Game game)
    {
        m_colList = collisionList;
        m_game = game;
    }

    public void createAliens(Game game)
    {
        @DrawableRes int[] alienSprites = new int[]
        {
               R.drawable.alien_1,
               R.drawable.alien_2,
               R.drawable.alien_3,
               R.drawable.alien_4
        };
        for(int i = 0; i < COLUMNS; i++)
        {
            List<Alien> currentColumn = new ArrayList<>();
            for(int j = 0; j < ROWS; j++)
            {
                float testx = Input.getScreenHeight() - (((ROWS * m_alienSize) * 2) + ROWS * 2);
                Alien tempAlien = new Alien(game.getActivity(), alienSprites[j],
                        300.0f + (i * 200.0f), testx + (j * (m_alienSize * 2)),
                        m_alienSize, m_alienMoveSpeed);
                currentColumn.add(tempAlien);
                game.addGameObject(tempAlien);
                m_colList.addAlien(tempAlien);
            }
            m_alienColumns.add(currentColumn);
        }
    }

    private void checkSides(float deltaTime)
    {
        boolean changeDir = false;
        for (List<Alien> tempList : m_alienColumns)
        {
            for (Alien tempAlien : tempList)
            {
                if (tempAlien.getX() < (tempAlien.getXSize() / 2.0f) + BORDER ||
                        tempAlien.getX() > Input.getScreenWidth() - (tempAlien.getXSize() / 2.0f) - BORDER)
                {
                    changeDir = true;
                    break;
                }
            }
        }

        if(changeDir)
        {
            for (List<Alien> tempList : m_alienColumns)
            {
                for (Alien tempAlien : tempList)
                {
                    tempAlien.switchDirection(deltaTime);
                }
            }
        }
    }

    public void update(float deltaTime, int alienCount)
    {
        checkSides(deltaTime);

        m_timeToBossSpawn -= deltaTime;
        if(m_timeToBossSpawn <= 0.0f)
        {
            Alien bossAlien;
            if (m_random.nextBoolean())
            {
                bossAlien = new BossAlien(m_game.getActivity(), R.drawable.alien_large_right,
                        -m_alienSize * 0.5f, m_alienSize * 1.2f, -m_alienMoveSpeed * 3.0f);
            }
            else
            {
                bossAlien = new BossAlien(m_game.getActivity(), R.drawable.alien_large_left,
                        Input.getScreenWidth() + (m_alienSize * 0.5f), m_alienSize * 1.2f, m_alienMoveSpeed * 3.0f);
            }

            m_game.addGameObject(bossAlien);
            m_colList.addAlien(bossAlien);
            m_timeToBossSpawn = BOSS_SPAWN_WAIT;
        }

        m_timeToShotSpawn -= deltaTime;
        if(m_timeToShotSpawn <= 0.0f)
        {
            Random rand = new Random();
            int alienChoice = rand.nextInt(m_alienColumns.size());
            Bullet alienBullet = new Bullet(m_game.getActivity(),
                    R.drawable.alien_shot,
                    m_alienColumns.get(alienChoice).get(0).getX(),
                    m_alienColumns.get(alienChoice).get(0).getY() - 25.0f);
            m_game.addGameObject(alienBullet);
            m_colList.addBullet(alienBullet, false);
            m_timeToShotSpawn = SHOT_SPAWN_WAIT;
        }

        // TODO: Fix this.
//        if(MAX_COUNT != alienCount)
//        {
//            while(m_alienCount > alienCount)
//            {
//                m_alienCount--;
//                for (List<Alien> tempList : m_alienColumns)
//                {
//                    for (Alien tempAlien : tempList)
//                    {
//                        tempAlien.increaseSpeed();
//                    }
//                }
//            }
//        }
        checkLives();
    }

    private void checkLives()
    {
        for(int i = 0; i < m_alienColumns.size(); i++)
        {
            List<Alien> alienColumn = m_alienColumns.get(i);
            for (int j = 0; j < alienColumn.size(); j++)
            {
                if (alienColumn.get(j).isDestroyed())
                {
                    m_colList.removeAlien(alienColumn.get(j));
                    alienColumn.remove(j);
                    j--;
                }
            }

            if (alienColumn.size() == 0)
            {
                m_alienColumns.remove(i);
                i--;
            }
        }
    }
}
