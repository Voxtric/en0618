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
            }
        }

        int alienCount = m_alienList.size();
        int playerBulletCount = m_playerBulletList.size();
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

                for(int k = 0; k < playerBulletCount; k++)
                {
                    // Alien vs Bullet? Destroy both, award Points.
                    Bullet playerBullet = m_playerBulletList.get(k);
                    if(alien.collidesWith(playerBullet))
                    {
                        if(alien instanceof BossAlien)
                        {
                            // TODO: REMOVE LITERALS, REPLACE WITH VARIABLE NUMBER
                            m_player.addScore(150);
                        }
                        else
                        {
                            m_player.addScore(50);
                        }

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

    void addAlien(Alien newAlien)
    {
        // Add Alien, Called by Manager
        m_alienList.add(newAlien);
    }

    public boolean alienRemaining()
    {
        // Checks that there is at least one living alien
        return m_alienList.size() > 0;
    }

    public void cleanLists()
    {
        // Clears lists
        // Only runs when Alien list is empty.
        m_alienBulletList.clear();
        m_playerBulletList.clear();
    }

    public void newPlayer(Player player)
    {
        // Sets m_player to new value
        m_player = player;
    }
}
