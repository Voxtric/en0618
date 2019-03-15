package com.northumbria.en0618.engine;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.opengl.GLSurfaceView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.northumbria.en0618.R;

import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.GameSurfaceView;
import com.northumbria.en0618.engine.opengl.Shader;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.opengl.Texture;

// TODO: Can't use enums because Android is dumb.
enum GameSound {PLAYER_HIT, ENEMY_HIT, BARRIER_HIT, PLAYER_FIRE, ENEMY_FIRE}

public abstract class GameActivity extends BackgroundSoundAccessingActivity
{
    private BackgroundSoundService m_backgroundSoundService = null;
    ServiceConnection m_serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            m_backgroundSoundService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            BackgroundSoundService.LocalBinder binder = (BackgroundSoundService.LocalBinder)service;
            m_backgroundSoundService = binder.getBackgroundSoundServiceInstance();
        }
    };

    private Game m_game;
    private GLSurfaceView m_glSurfaceView;
    private boolean m_pauseOnResume = false;

    private SoundPool m_soundPool;

    // TODO: Move this into sound manager and use a dictionary.
    private int enemyHitID;
    private int enemyFiringID;
    private int playerHitID;
    private int playerFiringID;
    private int barrierHitID;

    private static final int MAX_STREAMS = 10;

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

        m_soundPool = new SoundPool(MAX_STREAMS, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);

        enemyHitID = m_soundPool.load(this, R.raw.enemyhit, 1);
        enemyFiringID = m_soundPool.load(this, R.raw.enemyfire, 1);
        playerHitID = m_soundPool.load(this, R.raw.playerhit, 1);
        playerFiringID = m_soundPool.load(this, R.raw.playerfire, 1);
        barrierHitID = m_soundPool.load(this, R.raw.barrierhit, 1);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent serviceIntent = new Intent(this, BackgroundSoundService.class);
        bindService(serviceIntent, m_serviceConnection, BIND_IMPORTANT);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unbindService(m_serviceConnection);
    }

    @Override
    protected void onDestroy()
    {
        m_game.destroyAll();
        Font.clearCache();
        Sprite.clearCache();
        Texture.clearCache();
        Shader.clearCache();
        m_soundPool.release();
        super.onDestroy();

    }

    @Override
    protected void onPause()
    {
        m_game.pause(false);
        super.onPause();

        m_soundPool.autoPause();

        m_pauseOnResume = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        m_glSurfaceView.onResume();

        m_soundPool.autoResume();

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

    @SuppressWarnings("unused")
    public void onResumeGame(View view)
    {
        getGame().unPause();
    }

    @SuppressWarnings("unused")
    public void onQuitGame(View view)
    {
        AlertDialog pauseMenu = m_game.getPauseDialog();
        if (pauseMenu != null)
        {
            pauseMenu.cancel();
        }
        notifyActivityChanging();
        finish();
    }

    public void onGameReady()
    {
    }

    public void onGameUpdate(float deltaTime)
    {
    }

    public void onGamePause(AlertDialog pauseDialog)
    {
    }

    public void onGameUnpause()
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
