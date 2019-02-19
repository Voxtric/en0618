package com.northumbria.en0618.engine;

import android.app.AlertDialog;
import android.opengl.GLSurfaceView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.northumbria.en0618.R;

import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.GameSurfaceView;
import com.northumbria.en0618.engine.opengl.Shader;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.opengl.Texture;

enum GameSound {PLAYER_HIT, ENEMY_HIT, BARRIER_HIT, PLAYER_FIRE, ENEMY_FIRE}

public abstract class GameActivity extends AppCompatActivity
{
    private Game m_game;
    private GLSurfaceView m_glSurfaceView;
    private boolean m_pauseOnResume = false;

    MediaPlayer m_mediaPlayer;
    SoundPool soundPool;

    int enemyHitID;
    int enemyFiringID;
    int playerHitID;
    int playerFiringID;
    int barrierHitID;

    public static final int MAX_STREAMS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            final int finalFlags = flags;

            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
            {
                @Override
                public void onSystemUiVisibilityChange(int visibility)
                {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                    {
                        decorView.setSystemUiVisibility(finalFlags);
                    }
                }
            });
        }

        m_game = new Game(this);

        GameSurfaceView gameSurfaceView = new GameSurfaceView(this);
        gameSurfaceView.setPreserveEGLContextOnPause(true);
        //gameSurfaceView.setTargetFrameRate(30);
        setContentView(gameSurfaceView);
        m_glSurfaceView = gameSurfaceView;

        m_mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamemusic);
        m_mediaPlayer.pause();
        soundPool = new SoundPool(MAX_STREAMS, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);

        enemyHitID = soundPool.load(this, R.raw.enemyhit, 1);
        enemyFiringID = soundPool.load(this, R.raw.enemyfire, 1);
        playerHitID = soundPool.load(this, R.raw.playerhit, 1);
        playerFiringID = soundPool.load(this, R.raw.playerfire, 1);
        barrierHitID = soundPool.load(this, R.raw.barrierhit, 1);
    }

    @Override
    protected void onDestroy()
    {
        m_game.destroyAll();
        Font.clearCache();
        Sprite.clearCache();
        Texture.clearCache();
        Shader.clearCache();
        m_mediaPlayer.stop();
        m_mediaPlayer.release();
        soundPool.release();
        super.onDestroy();

    }

    @Override
    protected void onPause()
    {
        m_game.pause(false);
        super.onPause();

        m_mediaPlayer.pause();
        soundPool.autoPause();

        m_pauseOnResume = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        m_glSurfaceView.onResume();

        m_mediaPlayer.start();
        soundPool.autoResume();

        if (m_pauseOnResume)
        {
            m_pauseOnResume = false;
            m_game.pause(true);
        }
    }

    @Override
    public void onBackPressed()
    {
        m_game.pause(true);
    }

    public Game getGame()
    {
        return m_game;
    }

    public GLSurfaceView getSurfaceView()
    {
        return m_glSurfaceView;
    }

    public void onResumeGame(View view)
    {
        getGame().unPause();
    }

    public void onQuitGame(View view)
    {
        AlertDialog pauseMenu = m_game.getPauseDialog();
        if (pauseMenu != null)
        {
            pauseMenu.cancel();
        }
        finish();
    }

    public void onGameReady()
    {
    }

    public void onGameUpdate(float deltaTime)
    {
    }

    public void playSound(GameSound sound)
    {
        switch (sound)
        {
            case PLAYER_HIT:
                SoundManager.getInstance().gPlaySoundPlayerHit();
                break;
            case ENEMY_HIT:
                SoundManager.getInstance().gPlaySoundEnemyHit();
                break;
            case BARRIER_HIT:
                SoundManager.getInstance().gPlaySoundBarrierHit();
                break;
            case PLAYER_FIRE:
                SoundManager.getInstance().gPlaySoundPlayerFiring();
                break;
            case ENEMY_FIRE:
                SoundManager.getInstance().gPlaySoundEnemyFiring();
                break;
        }
    }
}
