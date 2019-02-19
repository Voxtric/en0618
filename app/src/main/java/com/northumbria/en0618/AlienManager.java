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

    private float m_timeToBossSpawn = BOSS_SPAWN_WAIT;
    private float m_timeToShotSpawn = SHOT_SPAWN_WAIT;

    private List<List<Alien>> m_alienColumns = new ArrayList<>();
    private CollisionLists m_colList;
    private Game m_game;

    // Defines the number of Aliens to spawn
    final private int m_numOfRows = 4;
    final private int m_numOfColumns = 4;
    private int m_alienCount = m_numOfColumns * m_numOfRows;
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
        for(int i = 0; i < m_numOfColumns; i++)
        {
            List<Alien> currentColumn = new ArrayList<>();
            for(int j = 0; j < m_numOfRows; j++)
            {
                @DrawableRes int spriteType;
                if(j == m_numOfRows - 1)
                {
                    spriteType = R.drawable.alien_4;
                }
                else if(j == m_numOfRows - 2)
                {
                    spriteType = R.drawable.alien_3;
                }
                else if(j == m_numOfRows - 3)
                {
                    spriteType = R.drawable.alien_2;
                }
                else
                {
                    spriteType = R.drawable.alien_1;
                }
                float testx = Input.getScreenHeight() - (((m_numOfRows * m_alienSize) * 2) + m_numOfRows * 2);
                Alien tempAlien = new Alien(game.getActivity(), spriteType,
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

        if(m_alienCount != alienCount)
        {
            while(m_alienCount > alienCount)
            {
                m_alienCount--;
                for (List<Alien> tempList : m_alienColumns)
                {
                    for (Alien tempAlien : tempList)
                    {
                        tempAlien.increaseSpeed();
                    }
                }
            }
        }
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
