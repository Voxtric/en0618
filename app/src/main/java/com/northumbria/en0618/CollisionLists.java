package com.northumbria.en0618;

import com.northumbria.en0618.engine.CollidableGameObject;
import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.ArrayList;
import java.util.List;

class CollisionLists
{
    // CONSTANTS
    private static final float SCREEN_DISTANCE_EXPLOSION_SIZE = 0.06f;

    private final List<Bullet> m_playerBulletList = new ArrayList<>();
    private final List<Bullet> m_alienBulletList = new ArrayList<>();
    private final List<Alien> m_alienList = new ArrayList<>();
    private final List<Asteroid> m_asteroidList = new ArrayList<>();

    private final SpaceInvadersActivity m_activity;
    private final Game m_game;
    private final Player m_player;

    private final float m_explosionSize;

    CollisionLists(SpaceInvadersActivity activity, Player player)
    {
        // Sets References
        m_activity = activity;
        m_game = activity.getGame();
        m_player = player;
        m_explosionSize = Input.getScreenWidth() * SCREEN_DISTANCE_EXPLOSION_SIZE;
    }

    void checkCollisions()
    {
        // Checks through every list for collisions
        int alienBulletCount = m_alienBulletList.size();
        int asteroidCount = m_asteroidList.size();
        if (alienBulletCount > 0)
        {
            // Loops through each Alien Bullet to check for
            // Collision with Player or Asteroids

            for (int i = 0; i < alienBulletCount; i++)
            {
                Bullet bullet = m_alienBulletList.get(i);
                CollidableGameObject.CollisionInfo collisionInfo = m_player.collidesWith(bullet);
                if (collisionInfo != null)
                {
                    // If Player and Bullet collide, lower payer health and
                    // destroy bullet
                    bullet.destroy();
                    m_alienBulletList.remove(i);
                    alienBulletCount--;
                    i--;

                    Explosion explosion = new Explosion(m_activity, collisionInfo.x, collisionInfo.y, m_explosionSize);
                    m_game.addGameObject(explosion);

                    if (m_player.consumeLife())
                    {
                        // Destroy player if lives = 0
                        m_player.destroy();
                        break;
                    }
                }
                if (asteroidCount > 0 && i >= 0)
                {
                    // Loops through asteroids for collisions
                    for (int j = 0; j < asteroidCount; j++)
                    {
                        Asteroid asteroid = m_asteroidList.get(j);
                        collisionInfo = bullet.collidesWith(asteroid);
                        if (collisionInfo != null)
                        {
                            bullet.destroy();
                            m_alienBulletList.remove(i);
                            alienBulletCount--;
                            i--;

                            asteroid.destroy();
                            m_asteroidList.remove(j);
                            asteroidCount--;
                            j--;

                            Explosion explosion = new Explosion(m_activity, collisionInfo.x, collisionInfo.y, m_explosionSize);
                            m_game.addGameObject(explosion);
                        }
                    }
                }
            }
        }

        int alienCount = m_alienList.size();
        int playerBulletCount = m_playerBulletList.size();
        asteroidCount = m_asteroidList.size();
        if (playerBulletCount > 0)
        {
            for (int i = 0; i < alienCount; i++)
            {
                // Loops through each Player Bullet to check for
                // Collision with Aliens or Asteroids
                Alien alien = m_alienList.get(i);
                CollidableGameObject.CollisionInfo collisionInfo = m_player.collidesWith(alien);
                if (collisionInfo != null)
                {
                    // Player vs Alien? Kill player.
                    m_player.destroy();

                    Explosion explosion = new Explosion(m_activity, collisionInfo.x, collisionInfo.y, m_explosionSize);
                    m_game.addGameObject(explosion);
                }

                if (asteroidCount > 0)
                {
                    // Loops through asteroids for collisions
                    for (int j = 0; j < asteroidCount; j++)
                    {
                        Asteroid asteroid = m_asteroidList.get(j);
                        collisionInfo = alien.collidesWith(asteroid);
                        if (collisionInfo != null)
                        {
                            asteroid.destroy();
                            m_asteroidList.remove(j);
                            asteroidCount--;
                            j--;

                            Explosion explosion = new Explosion(m_activity, collisionInfo.x, collisionInfo.y, m_explosionSize);
                            m_game.addGameObject(explosion);
                        }
                    }
                }

                for (int k = 0; k < playerBulletCount; k++)
                {
                    // Alien vs Bullet? Destroy both, award Points.
                    Bullet playerBullet = m_playerBulletList.get(k);
                    collisionInfo = alien.collidesWith(playerBullet);
                    if (collisionInfo != null)
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

                        Explosion explosion = new Explosion(m_activity, alien.getX(), alien.getY(), alien.getXSize());
                        m_game.addGameObject(explosion);
                    }
                }
            }

            asteroidCount = m_asteroidList.size();
            for (int j = 0; j < asteroidCount; j++)
            {
                Asteroid asteroid = m_asteroidList.get(j);
                for (int i = 0; i < playerBulletCount; i++)
                {
                    // Loops through asteroids for collisions
                    Bullet bullet = m_playerBulletList.get(i);
                    CollidableGameObject.CollisionInfo collisionInfo = bullet.collidesWith(asteroid);
                    if (collisionInfo != null)
                    {
                        bullet.destroy();
                        m_playerBulletList.remove(i);
                        playerBulletCount--;
                        i--;

                        asteroid.destroy();
                        m_asteroidList.remove(j);
                        asteroidCount--;
                        j--;

                        Explosion explosion = new Explosion(m_activity, collisionInfo.x, collisionInfo.y, m_explosionSize);
                        m_game.addGameObject(explosion);
                    }
                }
            }
        }
    }

    void addBullet(Bullet newBullet, boolean firedByPlayer)
    {
        // Add Bullet to List based on who fired it.
        if (firedByPlayer)
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
        // Destroys the references to all objects
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
