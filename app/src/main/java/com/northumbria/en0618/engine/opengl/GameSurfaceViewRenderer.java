package com.northumbria.en0618.engine.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.Input;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// The object that performs that actual rendering to the OpenGL surface.
class GameSurfaceViewRenderer implements GLSurfaceView.Renderer
{
    // Objects used to manage the lifecycle of the game and the app.
    private final GameActivity m_activity;
    private Game m_game;

    private final float[] m_projectionMatrix = new float[16];
    private final float[] m_viewMatrix = new float[16];
    private final float[] m_vpMatrix = new float[16];

    GameSurfaceViewRenderer(GameActivity activity)
    {
        m_activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.7f, 0.0f, 0.7f, 1.0f);
        m_game = m_activity.getGame();
    }

    // When the surface has changed either due to resizing or rotation, recreate the camera matrices
    // and reinitialise the Input manager.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        // Calculate camera matrices.
        Matrix.orthoM(m_projectionMatrix, 0,
                0, -width, 0, height, 3, 7);
        Matrix.setLookAtM(m_viewMatrix, 0,
                0, 0, -3,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(m_vpMatrix, 0, m_projectionMatrix,
                0, m_viewMatrix, 0);

        // Initialise the input manager.
        Input.initialise(m_activity, width, height, 10);

        // If the game hasn't already been started, now is a good time to start it.
        if (!m_game.isLaunched())
        {
            m_game.launch();
            m_activity.onGameReady();
        }
    }

    // Game updates are directly tied to frame time so that the game doesn't
    // run when it can't be seen.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        m_game.update(m_vpMatrix);
    }
}
