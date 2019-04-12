package com.northumbria.en0618.engine;

import android.app.AlertDialog;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.util.Log;
import android.view.View;

import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.GameSurfaceView;
import com.northumbria.en0618.engine.opengl.Shader;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.opengl.Texture;

public abstract class GameActivity extends BackgroundMusicServiceLinkedActivity
{
    private @RawRes int m_pauseDialogForwardButtonSoundID = 0;
    private @RawRes int m_pauseDialogBackwardButtonSoundID = 0;

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
        setContentView(gameSurfaceView);
        m_glSurfaceView = gameSurfaceView;
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
    protected void onPause()
    {
        m_game.pause(false);
        super.onPause();
        m_pauseOnResume = true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getBackgroundSoundService().unloadSounds(new int[] { m_pauseDialogForwardButtonSoundID, m_pauseDialogBackwardButtonSoundID });
    }

    @Override
    protected void onDestroy()
    {
        m_game.destroyAll();
        super.onDestroy();
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
        getGame().resume();
        if (view != null)
        {
            BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
            backgroundSoundService.resumeMusic();
            backgroundSoundService.resumeAllSounds();
            playPauseDialogForwardButtonSound();
        }
    }

    @SuppressWarnings("unused")
    public void onQuitGame(View view)
    {
        getBackgroundSoundService().stopMusic(true);
        AlertDialog pauseMenu = m_game.getPauseDialog();
        if (pauseMenu != null)
        {
            pauseMenu.cancel();
        }
        playPauseDialogBackwardButtonSound();
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
        BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
        backgroundSoundService.pauseMusic();
        backgroundSoundService.pauseAllSounds();
    }

    public void onGameResume()
    {
    }

    protected void setPauseDialogButtonSoundIDs(@RawRes int forwardSoundID, @RawRes int backwardSoundID)
    {
        m_pauseDialogForwardButtonSoundID = forwardSoundID;
        m_pauseDialogBackwardButtonSoundID = backwardSoundID;
        BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
        if (backgroundSoundService != null)
        {
            backgroundSoundService.loadSounds(new int[] { forwardSoundID, backwardSoundID }, false);
        }
    }

    public void playPauseDialogForwardButtonSound()
    {
        if (m_pauseDialogForwardButtonSoundID != 0)
        {
            getBackgroundSoundService().playSound(m_pauseDialogForwardButtonSoundID);
        }
    }

    public void playPauseDialogBackwardButtonSound()
    {
        if (m_pauseDialogBackwardButtonSoundID != 0)
        {
            getBackgroundSoundService().playSound(m_pauseDialogBackwardButtonSoundID);
        }
    }
}
