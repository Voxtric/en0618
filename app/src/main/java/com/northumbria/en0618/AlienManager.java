package com.northumbria.en0618;

import android.support.annotation.DrawableRes;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
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
    }; // Stores the four alien sprites in an Array to be looped through later


    // CONSTANTS
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

    private final List<List<Alien>> m_alienColumns = new ArrayList<>(); // List of Alien Columns
                                                                        // Each Column is a list
    private int m_activeAliens; // Number of Aliens alive
    private final CollisionLists m_colList; // Reference to Collision List Class
    private final Game m_game; // Reference to Game
    private boolean m_alienWin = false; // Sets true if Aliens hit the bottom

    private float m_alienMoveSpeed; // Alien Move Speed, calculated below.
    private final float m_alienSize; // Alien Sprite Size, calculated below.
    private final float m_sideBorder; // Side Borders

    AlienManager(CollisionLists collisionList, Game game)
    {
        // Game and Collision List references set in constructor
        m_colList = collisionList;
        m_game = game;

        // Default values calculated
        m_alienMoveSpeed = Input.getScreenWidth() * SCREEN_DISTANCE_PER_SECOND;
        float spaceForAliens = 1.0f - (SCREEN_DISTANCE_GAP_BETWEEN_ALIENS * (COLUMNS - 1)) - SCREEN_DISTANCE_SPACE_UNTIL_EDGE;
        m_alienSize = Input.getScreenWidth() * (spaceForAliens / (float)COLUMNS);
        m_sideBorder = Input.getScreenHeight() * SCREEN_DISTANCE_SIDE_BORDER;
    }

    public void incrementBaseAlienSpeed(int level)
    {
        // Increments alien sped based on level.
        m_alienMoveSpeed *= (ALIEN_SPEEDUP_PER_LEVEL * level);
    }

    void createAliens()
    {
        // Creates the aliens.
        m_alienWin = false;

        // Sets Initial Values
        float lowestHeight = (Input.getScreenHeight() * SCREEN_DISTANCE_START_HEIGHT) -
                (ROWS * m_alienSize) - ((ROWS - 1) * (Input.getScreenWidth() * SCREEN_DISTANCE_GAP_BETWEEN_ALIENS));
        float xPosition = m_sideBorder + (m_alienSize * 0.5f);
        for (int i = 0; i < COLUMNS; i++)
        {
            // Loops through Column count and Row count to create the correct number of aliens
            float yPosition = lowestHeight;
            List<Alien> currentColumn = new ArrayList<>();
            for (int j = 0; j < ROWS; j++)
            {
                // Each alien is given the universal values, with a unique location that changes each iteration
                // Sprits are set based on Row number, returning to the first sprite if there are more rows then sprites available
                Alien tempAlien = new Alien(m_game.getActivity(), ALIEN_SPRITE_DRAWABLE_IDS[j % ALIEN_SPRITE_DRAWABLE_IDS.length],
                        xPosition, yPosition, m_alienSize, m_alienMoveSpeed, (lowestHeight - Player.getStartHeight()) / (float)STEPS_TO_PLAYER);
                // Aliens are added to the column, Game and Collision List before the Position is updated.
                currentColumn.add(tempAlien);
                m_game.addGameObject(tempAlien);
                m_colList.addAlien(tempAlien);
                yPosition += m_alienSize + (Input.getScreenWidth() * SCREEN_DISTANCE_GAP_BETWEEN_ALIENS);
            }
            // Column stored in Manager and Position updated for next column
            m_alienColumns.add(currentColumn);
            xPosition += m_alienSize + (Input.getScreenWidth() * SCREEN_DISTANCE_GAP_BETWEEN_ALIENS);
        }
        m_activeAliens = MAX_COUNT;
    }

    private void checkSides(float deltaTime)
    {
        // This function checks the aliens positions in order to determine if any alien has hit a border
        // If this returns true, aliens are told to go through the change direction process
        boolean changeDir = false;
        for (List<Alien> tempList : m_alienColumns)
        {
            for (Alien tempAlien : tempList)
            {
                // Either condition breaks from the loop, as so long as there is 1 successful
                // use case, it applies to all aliens.
                if (tempAlien.getY() < Player.getStartHeight())
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
            // Runs if an alien has hit a border.
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
        // Runs on every frame.
        // Checks that there are aliens still existing, if so, bosses are spawned and bullets
        // are fired from any alien at the bottom of it's column
        if (m_alienColumns.size() > 0)
        {
            checkSides(deltaTime);

            m_timeToBossSpawn -= deltaTime;
            if (m_timeToBossSpawn <= 0.0f)
            {
                float bossAlienSize = m_alienSize * BOSS_ALIEN_SIZE_MULTIPLIER;
                Alien bossAlien;
                // Spawns Boss
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
            if (m_timeToShotSpawn <= 0.0f)
            {
                // randomly selects which column to choose from
                int alienChoice = m_game.getRandom().nextInt(m_alienColumns.size());
                Bullet alienBullet = new Bullet(m_game.getActivity(),
                        R.drawable.alien_shot,
                        m_alienColumns.get(alienChoice).get(0).getX(),
                        m_alienColumns.get(alienChoice).get(0).getY());
                m_game.addGameObject(alienBullet);
                m_colList.addBullet(alienBullet, false);
                m_game.getActivity().getSoundPool().playSound(m_game.getActivity(), R.raw.alien_fire);
                m_timeToShotSpawn = ALIEN_SHOT_SPAWN_WAIT;
            }

            int alienCount = m_activeAliens;
            checkSurvivingAliens();
            if (alienCount != m_activeAliens)
            {
                // Increases speed for each alien until the number of aliens alive
                // is equal to how many were alive last frame.
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
        // Determines how many Aliens are currently alive
        // Before removing references of aliens that are dead.
        for (int i = 0; i < m_alienColumns.size(); i++)
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
        // Returns true if Aliens have hit the bottom
        return m_alienWin;
    }

    public boolean alienRemaining()
    {
        // Returns true if all aliens are dead.
        return !m_alienColumns.isEmpty();
    }
}
