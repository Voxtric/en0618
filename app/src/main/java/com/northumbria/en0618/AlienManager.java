package com.northumbria.en0618;

import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;

import java.util.ArrayList;
import java.util.List;

class AlienManager
{
    private static final @DrawableRes int[] ALIEN_SPRITE_DRAWABLE_IDS = new int[]
    {
            R.drawable.alien_1,
            R.drawable.alien_2,
            R.drawable.alien_3,
            R.drawable.alien_4
    };

    private static final float BOSS_ALIEN_SPAWN_WAIT = 10.0f; // In seconds
    private static final float ALIEN_SHOT_SPAWN_WAIT = 2.5f;  // In seconds

    private static final float BOSS_ALIEN_SIZE_MULTIPLIER = 1.3f;
    private static final float BOSS_ALIEN_SPEED_MULTIPLIER = 5.0f;
    private static final float ALIEN_SPEEDUP_DIVISOR = 0.1f;       // Lower means larger speedup
    private static final float ALIEN_SPEEDUP_PER_LEVEL = 1.1f;

    private static final float SCREEN_DISTANCE_PER_SECOND = 0.03f;
    private static final float SCREEN_DISTANCE_GAP_BETWEEN_ALIENS = 0.05f;
    private static final float SCREEN_DISTANCE_SPACE_UNTIL_EDGE = 0.3f;
    private static final float SCREEN_DISTANCE_SIDE_BORDER = 0.02f;
    private static final float SCREEN_DISTANCE_START_HEIGHT = 0.8f;

    // Defines the number of Aliens to spawn
    private static final int COLUMNS = 5;
    private static final int ROWS = 4;
    private static final int MAX_COUNT = COLUMNS * ROWS;
    private static final int STEPS_TO_PLAYER = 10;

    private float m_timeToBossSpawn = BOSS_ALIEN_SPAWN_WAIT;
    private float m_timeToShotSpawn = ALIEN_SHOT_SPAWN_WAIT;

    private final List<List<Alien>> m_alienColumns = new ArrayList<>();
    private int m_activeAliens;
    private final CollisionLists m_colList;
    private final Game m_game;
    private boolean m_alienWin = false;

    private float m_alienMoveSpeed;
    private final float m_alienSize;
    private final float m_sideBorder;

    AlienManager(CollisionLists collisionList, Game game)
    {
        m_colList = collisionList;
        m_game = game;

        m_alienMoveSpeed = Input.getScreenWidth() * SCREEN_DISTANCE_PER_SECOND;
        float spaceForAliens = 1.0f - (SCREEN_DISTANCE_GAP_BETWEEN_ALIENS * (COLUMNS - 1)) - SCREEN_DISTANCE_SPACE_UNTIL_EDGE;
        m_alienSize = Input.getScreenWidth() * (spaceForAliens / (float)COLUMNS);
        m_sideBorder = Input.getScreenHeight() * SCREEN_DISTANCE_SIDE_BORDER;
    }

    public void incrementBaseAlienSpeed(int level)
    {
        m_alienMoveSpeed *= (ALIEN_SPEEDUP_PER_LEVEL * level);
    }

    void createAliens()
    {
        m_alienWin = false;

        float lowestHeight = (Input.getScreenHeight() * SCREEN_DISTANCE_START_HEIGHT) -
                (ROWS * m_alienSize) - ((ROWS - 1) * (Input.getScreenWidth() * SCREEN_DISTANCE_GAP_BETWEEN_ALIENS));
        float xPosition = m_sideBorder + (m_alienSize * 0.5f);
        for(int i = 0; i < COLUMNS; i++)
        {
            float yPosition = lowestHeight;
            List<Alien> currentColumn = new ArrayList<>();
            for(int j = 0; j < ROWS; j++)
            {
                Alien tempAlien = new Alien(m_game.getActivity(), ALIEN_SPRITE_DRAWABLE_IDS[j % ALIEN_SPRITE_DRAWABLE_IDS.length],
                        xPosition, yPosition, m_alienSize, m_alienMoveSpeed, (lowestHeight - Player.getStartHeight()) / (float)STEPS_TO_PLAYER);
                currentColumn.add(tempAlien);
                m_game.addGameObject(tempAlien);
                m_colList.addAlien(tempAlien);
                yPosition += m_alienSize + (Input.getScreenWidth() * SCREEN_DISTANCE_GAP_BETWEEN_ALIENS);
            }
            m_alienColumns.add(currentColumn);
            xPosition += m_alienSize + (Input.getScreenWidth() * SCREEN_DISTANCE_GAP_BETWEEN_ALIENS);
        }
        m_activeAliens = MAX_COUNT;
    }

    private void checkSides(float deltaTime)
    {
        boolean changeDir = false;
        for (List<Alien> tempList : m_alienColumns)
        {
            for (Alien tempAlien : tempList)
            {
                if(tempAlien.getY() < Player.getStartHeight())
                {
                    m_alienWin = true;
                    break;
                }
                if (tempAlien.getX() < (tempAlien.getXSize() * 0.5f) + m_sideBorder ||
                        tempAlien.getX() > Input.getScreenWidth() - (tempAlien.getXSize() * 0.5f) - m_sideBorder)
                {
                    changeDir = true;
                    break;
                }
            }
        }

        if(changeDir)
        {
            for (List<Alien> tempList : m_alienColumns)
            {
                for (Alien tempAlien : tempList)
                {
                    tempAlien.switchDirection(deltaTime);
                }
            }
        }
    }

    public void update(float deltaTime)
    {
        if(m_alienColumns.size() > 0)
        {
            checkSides(deltaTime);

            m_timeToBossSpawn -= deltaTime;
            if(m_timeToBossSpawn <= 0.0f)
            {
                float bossAlienSize = m_alienSize * BOSS_ALIEN_SIZE_MULTIPLIER;
                Alien bossAlien;
                if (m_game.getRandom().nextBoolean())
                {
                    bossAlien = new BossAlien(m_game.getActivity(), R.drawable.alien_large_right,
                            -bossAlienSize * 0.5f, bossAlienSize,
                            m_alienMoveSpeed * BOSS_ALIEN_SPEED_MULTIPLIER);
                }
                else
                {
                    bossAlien = new BossAlien(m_game.getActivity(), R.drawable.alien_large_left,
                            Input.getScreenWidth() + (bossAlienSize * 0.5f), bossAlienSize,
                            -m_alienMoveSpeed * BOSS_ALIEN_SPEED_MULTIPLIER);
                }

                m_game.addGameObject(bossAlien);
                m_colList.addAlien(bossAlien);
                m_timeToBossSpawn = BOSS_ALIEN_SPAWN_WAIT;
            }

            m_timeToShotSpawn -= deltaTime;
            if(m_timeToShotSpawn <= 0.0f)
            {
                int alienChoice = m_game.getRandom().nextInt(m_alienColumns.size());
                Bullet alienBullet = new Bullet(m_game.getActivity(),
                        R.drawable.alien_shot,
                        m_alienColumns.get(alienChoice).get(0).getX(),
                        m_alienColumns.get(alienChoice).get(0).getY());
                m_game.addGameObject(alienBullet);
                m_colList.addBullet(alienBullet, false);
                m_timeToShotSpawn = ALIEN_SHOT_SPAWN_WAIT;
            }

            int alienCount = m_activeAliens;
            checkSurvivingAliens();
            if (alienCount != m_activeAliens)
            {
                for (List<Alien> tempList : m_alienColumns)
                {
                    for (Alien tempAlien : tempList)
                    {
                        tempAlien.setSpeed(m_alienMoveSpeed *
                                (1.0f + ((MAX_COUNT - m_activeAliens) /
                                        ((float)MAX_COUNT * ALIEN_SPEEDUP_DIVISOR))));
                    }
                }
            }
        }
    }

    private void checkSurvivingAliens()
    {
        for(int i = 0; i < m_alienColumns.size(); i++)
        {
            List<Alien> alienColumn = m_alienColumns.get(i);
            for (int j = 0; j < alienColumn.size(); j++)
            {
                if (alienColumn.get(j).isDestroyed())
                {
                    alienColumn.remove(j);
                    j--;
                    m_activeAliens--;
                }
            }

            if (alienColumn.size() == 0)
            {
                m_alienColumns.remove(i);
                i--;
            }
        }
    }

    public boolean checkAlienWin()
    {
        return m_alienWin;
    }

    public boolean alienRemaining()
    {
        return !m_alienColumns.isEmpty();
    }
}
