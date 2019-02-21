package com.northumbria.en0618;

import java.util.ArrayList;
import java.util.List;

class CollisionLists
{
    private final List<Bullet> m_playerBulletList = new ArrayList<>();
    private final List<Bullet> m_alienBulletList = new ArrayList<>();
    private final List<Alien> m_alienList = new ArrayList<>();
    private final List<Asteroid> m_asteroidList = new ArrayList<>();

    private final Player m_player;
    private final SpaceInvadersActivity m_activity;

    CollisionLists(Player player, SpaceInvadersActivity activity)
    {
        m_player = player;
        m_activity = activity;
    }

    void checkCollisions()
    {
        int alienBulletCount = m_alienBulletList.size();
        int asteroidCount = m_asteroidList.size();
        if(alienBulletCount > 0)
        {
            // Loops through each Alien Bullet to check for
            // Collision with Player or Asteroids

            for(int i = 0; i < alienBulletCount; i++)
            {
                Bullet bullet = m_alienBulletList.get(i);
                if(m_player.collidesWith(bullet))
                {
                    // If Player and Bullet collide, lower payer health and
                    // destroy bullet
                    bullet.destroy();
                    m_alienBulletList.remove(i);
                    alienBulletCount--;
                    i--;

                    if (m_player.consumeLife())
                    {
                        // Destroy player if lives = 0
                        m_player.destroy();
                        break;
                    }
                }
                if(asteroidCount > 0 && i >= 0)
                {
                    for (int j = 0; j < asteroidCount; j++)
                    {
                        Asteroid asteroid = m_asteroidList.get(j);
                        if (bullet.collidesWith(asteroid))
                        {
                            bullet.destroy();
                            m_alienBulletList.remove(i);
                            alienBulletCount--;
                            i--;

                            asteroid.destroy();
                            m_asteroidList.remove(j);
                            asteroidCount--;
                            j--;
                        }
                    }
                }
            }
        }

        int alienCount = m_alienList.size();
        int playerBulletCount = m_playerBulletList.size();
        asteroidCount = m_asteroidList.size();
        if(playerBulletCount > 0)
        {
            for(int i = 0; i < alienCount; i++)
            {
                // Loops through each Player Bullet to check for
                // Collision with Aliens or Asteroids
                Alien alien = m_alienList.get(i);

                if(m_player.collidesWith(alien))
                {
                    // Player vs Alien? Kill player.
                    m_player.destroy();
                }

                if(asteroidCount > 0)
                {
                    for (int j = 0; j < asteroidCount; j++)
                    {
                        Asteroid asteroid = m_asteroidList.get(j);
                        if (alien.collidesWith(asteroid))
                        {
                            asteroid.destroy();
                            m_asteroidList.remove(j);
                            asteroidCount--;
                            j--;
                        }
                    }
                }

                for(int k = 0; k < playerBulletCount; k++)
                {
                    // Alien vs Bullet? Destroy both, award Points.
                    Bullet playerBullet = m_playerBulletList.get(k);
                    if(alien.collidesWith(playerBullet))
                    {
                        alien.awardScore(m_player, m_activity.getCurrentLevel());

                        // Destroy Bullet
                        playerBullet.destroy();
                        m_playerBulletList.remove(k);
                        playerBulletCount--;
                        k--;

                        // Destroy Alien
                        alien.destroy();
                        m_alienList.remove(i);
                        alienCount--;
                        i--;
                    }
                }
            }

            asteroidCount = m_asteroidList.size();
            for(int j = 0; j < asteroidCount; j++)
            {
                Asteroid asteroid = m_asteroidList.get(j);
                for(int i = 0; i < playerBulletCount; i++)
                {
                    Bullet bullet = m_playerBulletList.get(i);
                    if(bullet.collidesWith(asteroid))
                    {
                        bullet.destroy();
                        m_playerBulletList.remove(i);
                        playerBulletCount--;
                        i--;

                        asteroid.destroy();
                        m_asteroidList.remove(j);
                        asteroidCount--;
                        j--;
                    }
                }
            }
        }
    }

    void addBullet(Bullet newBullet, boolean firedByPlayer)
    {
        // Add Bullet to List based on who fired it.
        if(firedByPlayer)
        {
            m_playerBulletList.add(newBullet);
        }
        else
        {
            m_alienBulletList.add(newBullet);
        }
    }

    void addAsteroid(Asteroid newAsteroid)
    {
        m_asteroidList.add(newAsteroid);
    }

    void addAlien(Alien newAlien)
    {
        // Add Alien, Called by Manager
        m_alienList.add(newAlien);
    }

    public void destroyAll(boolean destroyPlayer)
    {
        for (Alien alien : m_alienList)
        {
            alien.destroy();
        }
        for (Bullet bullet : m_alienBulletList)
        {
            bullet.destroy();
        }
        for (Bullet bullet : m_playerBulletList)
        {
            bullet.destroy();
        }
        for (Asteroid asteroid : m_asteroidList)
        {
            asteroid.destroy();
        }

        if (destroyPlayer)
        {
            m_player.destroy();
        }
    }
}
