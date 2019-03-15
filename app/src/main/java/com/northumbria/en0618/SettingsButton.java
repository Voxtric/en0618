package com.northumbria.en0618;

import com.northumbria.en0618.engine.ButtonGameObject;
import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.opengl.Sprite;

public class SettingsButton extends ButtonGameObject
{
    private static final float SCREEN_DISTANCE_SIZE = 0.1f;

    private final Game m_game;

    SettingsButton(Game game)
    {
        super(Sprite.getSprite(game.getActivity(), R.drawable.settings),
              Input.getScreenWidth() - ((Input.getScreenHeight() * SCREEN_DISTANCE_SIZE) * 0.5f),
                Input.getScreenHeight() - ((Input.getScreenHeight() * SCREEN_DISTANCE_SIZE) * 0.5f),
                Input.getScreenHeight() * SCREEN_DISTANCE_SIZE,
                Input.getScreenHeight() * SCREEN_DISTANCE_SIZE);
        m_game = game;
    }

    @Override
    protected void onTouch()
    {
        m_game.pause(true);
    }
}
