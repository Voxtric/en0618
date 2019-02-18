package com.northumbria.en0618;


import android.support.annotation.DrawableRes;
import android.util.Log;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AlienManager {
    private List<List<Alien>> m_alienColumns = new ArrayList<>();
    private CollisionLists m_colList;
    private int m_bulletCounter = 0;
    private Game m_game;

    // Defines the number of Aliens to spawn
    final private int m_numOfRows = 4;
    final private int m_numOfColumns = 4;
    private int m_alienCount = m_numOfColumns * m_numOfRows;
    private float m_alienMoveSpeed = -70.0f;
    private float m_alienSize = 80.0f;
    private int m_bossCounter = 0;

    AlienManager(CollisionLists collisionList, Game game )
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

    public void checkSides()
    {
        boolean changeDir = false;
        for(Iterator<List<Alien>> l = m_alienColumns.iterator(); l.hasNext();)
        {
            List<Alien> tempList = l.next();
            for(Iterator<Alien> k = tempList.iterator(); k.hasNext();)
            {
                Alien tempAlien = k.next();
                if(tempAlien.getX() <= 100.0f ||
                        tempAlien.getX() >= Input.getScreenWidth() - 100.0f)
                {
                    changeDir = true;
                }
            }
        }


        if(changeDir)
        {
            for(Iterator<List<Alien>> l = m_alienColumns.iterator(); l.hasNext();)
            {
                List<Alien> tempList = l.next();
                for(Iterator<Alien> k = tempList.iterator(); k.hasNext();)
                {
                    Alien tempAlien = k.next();
                    tempAlien.switchDirection();
                }
            }
        }

//        Alien tempAlien = currentColumn.get(1);
//        if(tempAlien.getX() <= 50.0f)
//        {
//            tempAlien.moveRight();
//        }
    }

    public void update(float frameTime, int alienCount)
    {
        checkSides();
        if(m_bossCounter > 400)
        {
            Alien bossAlien = new Alien(m_game.getActivity(),
                    R.drawable.alien_large,
                    0.0f, Input.getScreenHeight() - m_alienSize - 20.0f,
                    m_alienSize, -m_alienMoveSpeed);
            m_game.addGameObject(bossAlien);
            m_colList.addAlien(bossAlien);
            m_bossCounter = 0;
        }
        m_bossCounter++;
        if(m_bulletCounter == 150)
        {
            Random rand = new Random();
            int alienChoice = rand.nextInt(m_alienColumns.size());
            Bullet alienBullet = new Bullet(m_game.getActivity(),
                    m_alienColumns.get(alienChoice).get(0).getX(),
                    m_alienColumns.get(alienChoice).get(0).getY() - 25.0f,
                    direction.DOWN,
                    R.drawable.alien_shot);
            m_bulletCounter = 0;
            m_game.addGameObject(alienBullet);
            m_colList.addBullet(alienBullet);
        }
        m_bulletCounter++;

        if(m_alienCount != alienCount)
        {
            while(m_alienCount > alienCount)
            {
                m_alienCount--;
                Log.e("Count:", String.valueOf(m_alienCount));
                for(Iterator<List<Alien>> l = m_alienColumns.iterator(); l.hasNext();)
                {
                    List<Alien> tempList = l.next();
                    for (Iterator<Alien> k = tempList.iterator(); k.hasNext(); )
                    {
                        Alien tempAlien = k.next();
                        tempAlien.incSpeed();
                    }
                }
            }
        }
    }
}
