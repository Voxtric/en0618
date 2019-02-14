package com.northumbria.en0618.engine;

import android.app.AlertDialog;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.GameSurfaceView;
import com.northumbria.en0618.engine.opengl.Shader;
import com.northumbria.en0618.engine.opengl.Sprite;
import com.northumbria.en0618.engine.opengl.Texture;

public class GameActivity extends AppCompatActivity
{
    private Game m_game;
    private GLSurfaceView m_glSurfaceView;

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
        m_glSurfaceView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        m_glSurfaceView.onResume();
    }

    @Override
    public void onBackPressed()
    {
        m_game.pause();
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
}
