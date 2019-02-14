package com.northumbria.en0618;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.GameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.TextGameObject;
import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.Sprite;

public class SpaceInvadersActivity extends GameActivity
{
    Player m_player;
    CollidableGameObject m_alien;
    TextGameObject m_text;
    CollisionLists m_collidableObjects;

    @Override
    public void onGameReady()
    {
        Game game = getGame();
        game.setPauseDialogLayoutID(R.layout.fragment_game_pause);

//   \/ Example stuff for Michael. Feel free to replace with actual game setup code.                                    \/

        m_player = new Player(game);
        game.addGameObject(m_player);

        m_collidableObjects = new CollisionLists(m_player);

        Bullet m_bullet = new Bullet(game.getActivity(), 540.0f,540.0f, direction.DOWN);
        game.addGameObject(m_bullet);
        m_collidableObjects.addBullet(m_bullet);

        m_alien = new CollidableGameObject(this, Sprite.getSprite(this, R.drawable.player, true), 100.0f, 100.0f, 20.0f, 20.0f);
        game.addGameObject(m_alien);

        GameObject settingsButton = new SettingsButton(game);
        game.addGameObject(settingsButton, true);

        Font font = Font.getFont(this, "Roboto-Regular.ttf", 100, 2);
        m_text = new TextGameObject(font, "Collision:", 10.0f, Input.getScreenHeight() - 60.0f);
        game.addGameObject(m_text);

//   /\ Example stuff for Michael. Feel free to replace with actual game setup code.                                    /\

        super.onGameReady();
    }

    @Override
    public void onGameUpdate(float deltaTime)
    {
//   \/ Example stuff for Michael. Feel free to replace with actual game loop code.                                     \/
        m_collidableObjects.checkCollisions();
        if (m_player.collidesWith(m_alien))
        {
            m_text.setText("Collision: 1");
        }
        else
        {
            m_text.setText("Collision: 0");
        }
        if(m_collidableObjects.collided)
        {
            m_text.setText("Derp");
        }

//   /\ Example stuff for Michael. Feel free to replace with actual game loop code.                                     /\
    }
}
