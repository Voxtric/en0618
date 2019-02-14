package com.northumbria.en0618;

import java.util.ArrayList;
import java.util.List;

public class CollisionLists {

    private List<Bullet> m_playerBulletList = new ArrayList<>();
    private List<Bullet> m_alienBulletList = new ArrayList<>();
    private Player m_player;
    public boolean collided = false;

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
                if(m_player.collidesWith(m_alienBulletList.get(i)))
                {
                    collided = true;
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

}
