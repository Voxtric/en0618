package com.northumbria.en0618;

import com.northumbria.en0618.engine.Button;
import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.R;
import com.northumbria.en0618.engine.opengl.Sprite;

public class SettingsButton extends Button
{
    private Game m_game;

    public SettingsButton(Game game)
    {
        super(Sprite.getSprite(game.getActivity(), R.drawable.settings),
              Input.getScreenWidth() - 100.0f, Input.getScreenHeight() - 100.0f,
              200.0f, 200.0f);
        m_game = game;
    }

    @Override
    protected void onTouch()
    {
        m_game.pause();
    }
}
