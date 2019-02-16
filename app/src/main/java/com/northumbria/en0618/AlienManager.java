package com.northumbria.en0618;


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
    static final private int numOfRows = 3;
    static final private int numOfColumns = 3;

    AlienManager(CollisionLists collisionList, Game game)
    {
        m_colList = collisionList;
        m_game = game;
    }

    public void createAliens(Game game)
    {
        for(int i = 0; i < numOfColumns; i++)
        {
            List<Alien> currentColumn = new ArrayList<>();
            for(int j = 0; j < numOfRows; j++)
            {
                Alien tempAlien = new Alien(game.getActivity(), alienType.smallAlien,
                        300.0f + (i * 200.0f), 1000.0f + (j * 200.0f));
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
            if(tempList.get(0).getX() <= 100.0f ||
                    tempList.get(tempList.size() - 1).getX() >= Input.getScreenWidth() - 100.0f)
            {
                changeDir = true;
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

    public void update(float frameTime)
    {
        checkSides();
        if(m_bulletCounter == 150)
        {
            Random rand = new Random();
            int alienChoice = rand.nextInt(m_alienColumns.size());
            Bullet alienBullet = new Bullet(m_game.getActivity(),
                    m_alienColumns.get(alienChoice).get(0).getX(),
                    m_alienColumns.get(alienChoice).get(0).getY() - 25.0f,
                    direction.DOWN,
                    R.drawable.alien_shot,
                    m_colList);
            m_bulletCounter = 0;
            m_game.addGameObject(alienBullet);
            m_colList.addBullet(alienBullet);
        }
        m_bulletCounter++;
    }
}
