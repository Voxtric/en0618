package com.northumbria.en0618;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CollisionLists {

    private List<Bullet> m_playerBulletList = new ArrayList<>();
    private List<Bullet> m_alienBulletList = new ArrayList<>();
    private List<Alien> m_alienList = new ArrayList<>();
    private List<Asteroid> m_asteroidList = new ArrayList<>();
    private Player m_player;

    CollisionLists(Player player)
    {
        m_player = player;
    }

    void checkCollisions()
    {
        int aBulletCount = m_alienBulletList.size();
        if(aBulletCount > 0)
        {
            for(int i = 0; i < m_alienBulletList.size(); i++)
            {
                if(m_player.collidesWith(m_alienBulletList.get(i)))
                {
                    m_alienBulletList.get(i).collidedWith(objectType.player);
                    m_player.collidedWith(objectType.bullet);
                }
            }
        }

        int pBulletCount = m_playerBulletList.size();
        if(pBulletCount > 0)
        {
            for(int i = 0; i < m_alienList.size(); i++)
            {
                for(int k = 0; k < m_playerBulletList.size(); k++)
                {
                    if(m_alienList.get(i).collidesWith(m_playerBulletList.get(k)))
                    {
                        m_alienList.get(i).collidedWith(objectType.bullet);
                        m_playerBulletList.get(k).collidedWith(objectType.alien);
                        m_player.score += 50;
                    }
                }

                if(m_player.collidesWith(m_alienList.get(i)))
                {
                    m_player.collidedWith(objectType.alien);
                }
            }
        }
        cleanLists();
    }

    void addBullet(Bullet newBullet)
    {
        if(newBullet.shotByAlien())
        {
            m_alienBulletList.add(newBullet);
        }
        else
        {
            m_playerBulletList.add(newBullet);
        }
    }

    void addAlien(Alien newAlien)
    {
        m_alienList.add(newAlien);
    }

    private void cleanLists()
    {
        for(int i = m_alienList.size() - 1; i >= 0; i--)
        {
            if(!m_alienList.get(i).m_isAlive)
            {
                m_alienList.get(i).destroy();
                m_alienList.remove(i);
            }
        }


        for(int i = m_alienBulletList.size() - 1; i >= 0; i--)
        {
            if(!m_alienBulletList.get(i).m_isAlive)
            {
                m_alienBulletList.get(i).destroy();
                m_alienBulletList.remove(i);
            }
        }


        for(int i = m_playerBulletList.size() - 1; i >= 0; i--)
        {
            if(!m_playerBulletList.get(i).m_isAlive)
            {
                m_playerBulletList.get(i).destroy();
                m_playerBulletList.remove(i);
            }
        }

//        for(int i = m_asteroidList.size() - 1; i >= 0; i--)
//        {
//            if(m_asteroidList.get(i).m_isAlive == false)
//            {
//                m_asteroidList.get(i).destroy();
//                m_asteroidList.remove(i);
//            }
//        }
    }

    public int alienCount()
    {
        return m_alienList.size();
    }
}
