package com.northumbria.en0618.engine.opengl;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;

import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.Input;

public class GameSurfaceView extends GLSurfaceView
{
    private final GameSurfaceViewRenderer m_renderer;
    private Handler m_handler = null;
    private long m_targetFrameTime = 0L;

    public GameSurfaceView(GameActivity context)
    {
        super(context);

        setEGLContextClientVersion(2);
        m_renderer = new GameSurfaceViewRenderer(context);
        setRenderer(m_renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Input.onTouchEvent(event);
        return true;
    }

    public void setTargetFrameRate(int targetFrameRate)
    {
        if (targetFrameRate == 0)
        {
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }
        else
        {
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            m_targetFrameTime = (long)((1 / (float)targetFrameRate) * 1000.0f);
            renderFrame();
        }
    }

    private void renderFrame()
    {
        requestRender();
        m_handler = new Handler();
        m_handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                renderFrame();
            }
        }, m_targetFrameTime);
    }
}
