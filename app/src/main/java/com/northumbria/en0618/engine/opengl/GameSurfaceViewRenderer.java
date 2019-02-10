package com.northumbria.en0618.engine.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.Input;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameSurfaceViewRenderer implements GLSurfaceView.Renderer
{
    private GameActivity m_activity;
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

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        Matrix.orthoM(m_projectionMatrix, 0, 0, -width, 0, height, 3, 7);
        Matrix.setLookAtM(m_viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(m_vpMatrix, 0, m_projectionMatrix, 0, m_viewMatrix, 0);

        Input.initialise(m_activity, width, height, 10);

        if (!m_game.isLaunched())
        {
            m_game.launch();
            m_activity.onGameReady();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        m_game.update(m_vpMatrix);
    }
}
