package com.northumbria.en0618.engine;

import android.app.AlertDialog;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.view.View;

import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.GameSurfaceView;
import com.northumbria.en0618.engine.opengl.Shader;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.opengl.Texture;

public abstract class GameActivity extends BackgroundMusicServiceLinkedActivity
{
    private static final int MAX_SOUND_STREAMS = 10;

    private SoundPool m_soundPool;
    private @RawRes int m_pauseDialogButtonSoundID = 0;

    private Game m_game;
    private GLSurfaceView m_glSurfaceView;
    private boolean m_pauseOnResume = false;

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
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        m_soundPool = new SoundPool(this, MAX_SOUND_STREAMS, null);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        m_soundPool.release();
        m_soundPool = null;
    }

    @Override
    protected void onDestroy()
    {
        m_game.destroyAll();
        Font.clearCache();
        Sprite.clearCache();
        Texture.clearCache();
        Shader.clearCache();
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        m_game.pause(false);
        super.onPause();
        m_pauseOnResume = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        m_glSurfaceView.onResume();

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
        getBackgroundSoundService().resumeMusic();
        m_soundPool.resumeAll();
        playPauseDialogButtonSound();
    }

    @SuppressWarnings("unused")
    public void onQuitGame(View view)
    {
        AlertDialog pauseMenu = m_game.getPauseDialog();
        if (pauseMenu != null)
        {
            pauseMenu.cancel();
        }
        playPauseDialogButtonSound();
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
        getBackgroundSoundService().pauseMusic();
        m_soundPool.pauseAll();
    }

    public SoundPool getSoundPool()
    {
        return m_soundPool;
    }

    protected void setPauseDialogButtonSoundID(@RawRes int pauseDialogSoundID)
    {
        m_pauseDialogButtonSoundID = pauseDialogSoundID;
    }

    public void playPauseDialogButtonSound()
    {
        if (m_pauseDialogButtonSoundID != 0)
        {
            m_soundPool.playSound(this, m_pauseDialogButtonSoundID);
        }
    }
}
