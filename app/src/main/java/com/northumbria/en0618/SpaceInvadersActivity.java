package com.northumbria.en0618;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.GameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.TextGameObject;
import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.Sprite;

public class SpaceInvadersActivity extends GameActivity
{
    private static final float SHOT_SPAWN_WAIT = 1.3f;
    private static final float SHOT_OFFSET_MULTIPLIER = 0.45f;

    private float m_timeToShotSpawn = SHOT_SPAWN_WAIT;
    private float m_playerShotOffset = 0.0f;

    Player m_player;
    TextGameObject m_text;
    CollisionLists m_collidableObjects;
    AlienManager m_alienManager;

    @Override
    public void onGameReady()
    {
        Game game = getGame();
        game.setPauseDialogLayoutID(R.layout.dialog_game_pause);

        // Settings Button
        GameObject settingsButton = new SettingsButton(game);
        game.addGameObject(settingsButton, true);

        // Add the tiling background.
        int count = (int)(Input.getScreenHeight() / BackgroundTile.SIZE) + 2;
        float height = count * BackgroundTile.SIZE;
        for (float x = BackgroundTile.SIZE * 0.5f; x < Input.getScreenWidth() + (BackgroundTile.SIZE * 0.5f); x += BackgroundTile.SIZE)
        {
            for (float y = BackgroundTile.SNAP_HEIGHT; y < height; y += BackgroundTile.SIZE)
            {
                BackgroundTile backgroundTile = new BackgroundTile(this, x, y, height);
                ((Sprite)backgroundTile.getRenderable()).setUseTransparency(false);
                game.addGameObject(backgroundTile);
            }
        }

        // Text
        Font font = Font.getFont(this, "Roboto-Regular.ttf", 100, 2);
        m_text = new TextGameObject(font, "Score:", 10.0f, Input.getScreenHeight() - 60.0f);
        m_text.setColor(1.0f, 1.0f, 1.0f, 0.8f);
        game.addGameObject(m_text);

        // Creation of Player Character
        m_player = new Player(this);
        game.addGameObject(m_player);
        m_playerShotOffset = m_player.getXSize() * SHOT_OFFSET_MULTIPLIER;

        m_collidableObjects = new CollisionLists(m_player);

        // Member Variable to store all Collidable Objects for Automated Collision Detection

        // Manager class for all ALien Objects
        m_alienManager = new AlienManager(m_collidableObjects, game);
        m_alienManager.createAliens(game);

        super.onGameReady();
    }

    @Override
    public void onGameUpdate(float deltaTime)
    {
        Game m_game = getGame();
        if(!m_player.isDead())
        {
            if(m_collidableObjects.alienAlive())
            {
                m_collidableObjects.checkCollisions();
                m_alienManager.update(deltaTime);
                m_text.setText("Score: " + m_player.score);

                m_timeToShotSpawn -= deltaTime;
                if(m_timeToShotSpawn <= 0.0f)
                {
                    float bulletX = m_player.getX() + m_playerShotOffset;
                    m_playerShotOffset = -m_playerShotOffset;
                    Bullet bullet = new Bullet(
                            m_game.getActivity(), R.drawable.player_shot, bulletX, m_player.getY());
                    m_game.addGameObject(bullet);
                    m_collidableObjects.addBullet(bullet, true);
                    m_timeToShotSpawn = SHOT_SPAWN_WAIT;
                }
                    // m_collidableObjects.newLevel();
            }
            else
            {
                if(m_player.newLevel())
                {
                    m_alienManager.createAliens(m_game);
                }
            }
        }
        else
        {
            // GAME OVER
            getGame().pause(false);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    AlertDialog dialog = new AlertDialog.Builder(SpaceInvadersActivity.this)
                            .setTitle("Game Over")
                            .setMessage("You suck\nScore:" + m_player.score)
                            .setNegativeButton("Main Menu", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    finish();
                                }
                            })
                            .setPositiveButton("Restart", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    getGame().unPause();
                                    // Restart the game here
                                }
                            })
                            .create();
                    dialog.show();
                }
            });
        }
    }
}
