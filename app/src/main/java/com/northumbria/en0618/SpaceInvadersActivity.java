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
    CollidableGameObject m_alien;
    TextGameObject m_text;
    CollisionLists m_collidableObjects;
    AlienManager m_alienManager;

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

        m_collidableObjects = new CollisionLists(m_player);

        // Creation of Player Character
        m_player = new Player(game, m_collidableObjects);
        game.addGameObject(m_player);

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
//   \/ Example stuff for Michael. Feel free to replace with actual game loop code.                                     \/
        m_collidableObjects.checkCollisions();
        m_alienManager.update(deltaTime);
            m_text.setText("Derp");

//   /\ Example stuff for Michael. Feel free to replace with actual game loop code.                                     /\
    }
}
