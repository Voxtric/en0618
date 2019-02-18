package com.northumbria.en0618;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.GameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.TextGameObject;
import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Font;

public class SpaceInvadersActivity extends GameActivity
{
    Player m_player;
    TextGameObject m_text;
    CollisionLists m_collidableObjects;
    AlienManager m_alienManager;
    int bulletCountdown = 0;
    boolean leftGun = true;

    @Override
    public void onGameReady()
    {
        Game game = getGame();
        game.setPauseDialogLayoutID(R.layout.fragment_game_pause);

//   \/ Example stuff for Michael. Feel free to replace with actual game setup code.                                    \/
        // Settings Button
        GameObject settingsButton = new SettingsButton(game);
        game.addGameObject(settingsButton, true);

        // Text
        Font font = Font.getFont(this, "Roboto-Regular.ttf", 100, 2);
        m_text = new TextGameObject(font, "Collision:", 10.0f, Input.getScreenHeight() - 60.0f);
        game.addGameObject(m_text);


        // Creation of Player Character
        m_player = new Player(game);
        game.addGameObject(m_player);

        m_collidableObjects = new CollisionLists(m_player);

        // Member Variable to store all Collidable Objects for Automated Collision Detection

        // Manager class for all ALien Objects
        m_alienManager = new AlienManager(m_collidableObjects, game);
        m_alienManager.createAliens(game);


//   /\ Example stuff for Michael. Feel free to replace with actual game setup code.                                    /\

        super.onGameReady();
    }

    @Override
    public void onGameUpdate(float deltaTime)
    {
        Game m_game = getGame();
//   \/ Example stuff for Michael. Feel free to replace with actual game loop code.                                     \/
        if(!m_player.gameOver())
        {
            m_collidableObjects.checkCollisions();
            m_alienManager.update(deltaTime, m_collidableObjects.alienCount());
            m_text.setText("Score: " + m_player.score);

            if(bulletCountdown >= 75)
            {
                float bulletX;
                if(leftGun)
                {
                    bulletX = m_player.getX() - 100.0f;
                }
                else
                {
                    bulletX = m_player.getX() + 100.0f;
                }
                leftGun = !leftGun;
                Bullet bullet = new Bullet(m_game.getActivity(), bulletX, m_player.getY(),
                        direction.UP, R.drawable.player_shot);
                m_game.addGameObject(bullet);
                m_collidableObjects.addBullet(bullet);
                bulletCountdown = 0;
            }
            bulletCountdown++;
            if(m_collidableObjects.alienCount() <= 0)
            {
                // m_collidableObjects.newLevel();
                if(m_player.newLevel())
                {
                    m_alienManager.createAliens(m_game);
                }
            }
        }
        else
        {
            // GAME OVER
        }
//   /\ Example stuff for Michael. Feel free to replace with actual game loop code.                                     /\
    }
}
