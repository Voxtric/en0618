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

    public void checkCollisions()
    {
        int bulletCount = m_alienBulletList.size();
        if(bulletCount > 0)
        {
            for(int i = 0; i < bulletCount; i++)
            {

            }
        }

        bulletCount = m_playerBulletList.size();
        if(bulletCount > 0)
        {
            for(int i = 0; i < m_alienList.size(); i++)
            {
                for(int k = 0; k < m_playerBulletList.size(); k++)
                {
                    if(m_alienList.get(i).collidesWith(m_playerBulletList.get(k)))
                    {
                        m_alienList.get(i).collidedWith(objectType.bullet);
                        m_playerBulletList.get(k).collidedWith(objectType.alien);
                    }
                }
            }
        }
    }

    public void addBullet(Bullet newBullet)
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

    public void removeBullet(Bullet oldBullet, direction dir)
    {
        if(dir == direction.UP)
        {
            m_playerBulletList.remove(oldBullet);
        }
        else
        {
            m_alienBulletList.remove(oldBullet);
        }
    }

    public void addAlien(Alien newAlien)
    {
        m_alienList.add(newAlien);
    }
}
