package com.northumbria.en0618.engine.opengl;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;

import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.Input;

@SuppressLint("ViewConstructor")    // Multi-view constructors are not needed due to limited use.
public class GameSurfaceView extends GLSurfaceView
{

    @SuppressWarnings("FieldCanBeLocal")    // Field cannot be local or it will be garbage collected.
    private Handler m_handler = null;       // Timing mechanism for non-standard frame-rate.
    private long m_targetFrameTime = 0L;    // Measured in milliseconds.

    // Create the renderer and assign it to be rendered to continuously.
    public GameSurfaceView(GameActivity context)
    {
        super(context);

        setEGLContextClientVersion(2);  // Specify OpenGL ES usage.

        setRenderer(new GameSurfaceViewRenderer(context));
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        setKeepScreenOn(true);
    }

    @SuppressLint("ClickableViewAccessibility") // Blind people cannot play this game, sorry.
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Input.onTouchEvent(event);
        return true;
    }

    // Sets the target frames per second (0 to be in sync with screen).
    public void setTargetFrameRate(int targetFrameRate)
    {
        // Render every time the screen updates.
        if (targetFrameRate == 0)
        {
            m_targetFrameTime = 0;
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }
        // Render only when enough time has elapsed.
        else
        {
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            m_targetFrameTime = (long)((1 / (float)targetFrameRate) * 1000.0f);
            renderFrame();
        }
    }

    // Requests the rendering of a frame and creates a new timer to render a new frame after
    // the calculated period.
    private void renderFrame()
    {
        m_handler = null;
        if (m_targetFrameTime > 0)
        {
            requestRender();    // Render a new frame.

            // Create a new timer and tell it to render at the desired time.
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
}
