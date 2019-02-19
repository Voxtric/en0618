package com.northumbria.en0618;

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
        int alienBulletCount = m_alienBulletList.size();
        if(alienBulletCount > 0)
        {
            for(int i = 0; i < alienBulletCount; i++)
            {
                Bullet bullet = m_alienBulletList.get(i);
                if(m_player.collidesWith(bullet))
                {
                    bullet.destroy();
                    m_alienBulletList.remove(i);
                    alienBulletCount--;
                    i--;


                    m_player.m_lives--;
                    if (m_player.m_lives == 0)
                    {
                        m_player.destroy();
                        break;
                    }
                }
            }
        }

        int alienCount = m_alienList.size();
        int playerBulletCount = m_playerBulletList.size();
        if(playerBulletCount > 0)
        {
            for(int i = 0; i < alienCount; i++)
            {
                Alien alien = m_alienList.get(i);

                if(m_player.collidesWith(alien))
                {
                    m_player.m_lives = 0;
                    m_player.destroy();
                }

                for(int k = 0; k < playerBulletCount; k++)
                {
                    Bullet playerBullet = m_playerBulletList.get(k);
                    if(alien.collidesWith(playerBullet))
                    {
                        if(alien instanceof BossAlien)
                        {
                            m_player.score += 150;
                        }
                        else
                        {
                            m_player.score += 50;
                        }

                        playerBullet.destroy();
                        m_playerBulletList.remove(k);
                        playerBulletCount--;
                        k--;

                        alien.destroy();
                        m_alienList.remove(i);
                        alienCount--;
                        i--;
                    }
                }
            }
        }
    }

    void addBullet(Bullet newBullet, boolean firedByPlayer)
    {
        if(firedByPlayer)
        {
            m_playerBulletList.add(newBullet);
        }
        else
        {
            m_alienBulletList.add(newBullet);
        }
    }

    void addAlien(Alien newAlien)
    {
        m_alienList.add(newAlien);
    }

    void removeAlien(Alien oldAlien)
    {
        m_alienList.remove(oldAlien);
    }

    public boolean alienAlive()
    {
        return m_alienList.size() > 0;
    }
}
