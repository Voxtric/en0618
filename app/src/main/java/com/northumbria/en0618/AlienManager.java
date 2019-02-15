package com.northumbria.en0618;


import android.util.Log;

import com.northumbria.en0618.engine.Game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlienManager {
    private ArrayList<Alien> currentColumn = new ArrayList<>();
    private List<ArrayList<Alien>> alienColumns = new ArrayList<>();
    private CollisionLists m_colList;

    // Defines the number of Aliens to spawn
    static final private int numOfRows = 3;
    static final private int numOfColumns = 3;

    AlienManager(CollisionLists collisionList, Game game)
    {
        m_colList = collisionList;
    }

    public void createAliens(Game game)
    {
        for(int i = 0; i < numOfColumns; i++)
        {
            for(int j = 0; j < numOfRows; j++)
            {
                Alien tempAlien = new Alien(game.getActivity(), alienType.smallAlien, 300.0f + (i * 200.0f), 500.0f + (j * 200.0f), 50.0f, 50.0f);
                currentColumn.add(tempAlien);
                game.addGameObject(tempAlien);
                m_colList.addAlien(tempAlien);
            }
            Log.e(String.valueOf("Temp List Size: " + currentColumn.size()),String.valueOf("Temp List Size: " + currentColumn.size()));
            Log.e(String.valueOf("List of List Size: " + alienColumns.size()),String.valueOf("List of List Size: " + alienColumns.size()));
            alienColumns.add(currentColumn);
            for(Iterator<ArrayList<Alien>> l = alienColumns.iterator(); l.hasNext();)
            {
                List<Alien> k = l.next();
                Log.e("List Size: " + String.valueOf(k.size()),"List Size: " + String.valueOf(k.size()));
            }
            currentColumn.clear();
        }
        checkLeft();
    }

    public void checkBotY()
    {

    }

    public void checkLeft()
    {
        for(Iterator<ArrayList<Alien>> l = alienColumns.iterator(); l.hasNext();)
        {
            List<Alien> k = l.next();
            Log.e("List Size2: " + String.valueOf(k.size()),"List Size2: " + String.valueOf(k.size()));
        }
        currentColumn.clear();
//        Alien tempAlien = currentColumn.get(1);
//        if(tempAlien.getX() <= 50.0f)
//        {
//            tempAlien.moveRight();
//        }
    }

    public void checkRight()
    {

    }

    public void update(float frameTime)
    {
        checkLeft();
    }
}
