package com.northumbria.en0618;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.GameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.TextGameObject;
import com.northumbria.en0618.engine.opengl.CollidableGameObject;
import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.testing.Player;

public class SpaceInvadersActivity extends GameActivity
{
    Player m_player;
    CollidableGameObject m_alien;
    TextGameObject m_text;

    @Override
    public void onGameReady()
    {
        Game game = getGame();
        game.setPauseDialogLayoutID(R.layout.fragment_game_pause);

//   \/ Example stuff for Michael. Feel free to replace with actual game setup code.                                    \/

        m_player = new Player(game);
        game.addGameObject(m_player);

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
    public void onGameUpdate()
    {
//   \/ Example stuff for Michael. Feel free to replace with actual game loop code.                                     \/

        if (m_player.collidesWith(m_alien))
        {
            m_text.setText("Collision: 1");
        }
        else
        {
            m_text.setText("Collision: 0");
        }

//   /\ Example stuff for Michael. Feel free to replace with actual game loop code.                                     /\
    }
}
