package com.northumbria.en0618.engine;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.support.annotation.LayoutRes;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game
{
    private final GameActivity m_activity;
    private AlertDialog m_pauseDialog = null;
    private @LayoutRes int m_pauseDialogLayoutID = 0;
    private boolean m_suppressPauseDialog = false;

    private final List<GameObject> m_gameObjects = new ArrayList<>();
    private long m_currentFrameBegin = 0L;
    private boolean m_launched = false;
    private boolean m_paused = false;
    private final Random m_random = new Random(System.currentTimeMillis());

    Game(GameActivity context)
    {
        m_activity = context;
    }

    public Random getRandom()
    {
        return m_random;
    }

    public void setPauseDialogLayoutID(@LayoutRes int pauseDialogLayoutID)
    {
        m_pauseDialogLayoutID = pauseDialogLayoutID;
    }

    public void launch()
    {
        m_launched = true;
        m_currentFrameBegin = System.currentTimeMillis();
    }

    public boolean isLaunched()
    {
        return m_launched;
    }

    public void setSuppressPauseDialog(boolean suppressPauseDialog)
    {
        m_suppressPauseDialog = suppressPauseDialog;
    }

    public void pause(final boolean displayDialog)
    {
        m_paused = true;
        m_activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                m_activity.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

                if (displayDialog && m_pauseDialog == null && !m_suppressPauseDialog)
                {
                    if (m_pauseDialogLayoutID != 0)
                    {
                        @SuppressLint("InflateParams")
                        View view = m_activity.getLayoutInflater().inflate(m_pauseDialogLayoutID, null);
                        m_pauseDialog = new AlertDialog.Builder(m_activity)
                                .setView(view)
                                .create();
                    }
                    else
                    {
                        m_pauseDialog = new AlertDialog.Builder(m_activity)
                                .setTitle("Game Paused")
                                .setMessage("The game is paused.")
                                .setPositiveButton("Resume", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        m_activity.onResumeGame(null);
                                    }
                                })
                                .setNegativeButton("Quit", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        m_activity.onQuitGame(null);
                                    }
                                })
                                .create();
                    }

                    m_pauseDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            m_activity.onResumeGame(null);
                        }
                    });
                    m_pauseDialog.show();
                }

                m_activity.onGamePause(m_pauseDialog);
            }
        });
    }

    public void resume()
    {
        m_paused = false;
        m_currentFrameBegin = System.currentTimeMillis();
        if (m_pauseDialog != null)
        {
            m_pauseDialog.dismiss();
            m_pauseDialog = null;
        }
        m_activity.onGameResume();
    }

    public AlertDialog getPauseDialog()
    {
        return m_pauseDialog;
    }

    public GameActivity getActivity()
    {
        return m_activity;
    }

    public void addGameObject(GameObject gameObject)
    {
        m_gameObjects.add(gameObject);
    }

    public void destroyAll()
    {
        m_gameObjects.clear();
    }

    public void update(float[] vpMatrix)
    {
        long lastFrameBegin = m_currentFrameBegin;
        m_currentFrameBegin = System.currentTimeMillis();
        float deltaTime = (m_currentFrameBegin - lastFrameBegin) / 1000.0f;

        if (!m_paused)
        {
            int gameObjectCount = m_gameObjects.size();
            for (int i = 0; i < gameObjectCount; i++)
            {
                if (m_gameObjects.get(i).isDestroyed())
                {
                    m_gameObjects.remove(i);
                    --i;
                    --gameObjectCount;
                }
                else
                {
                    m_gameObjects.get(i).update(deltaTime);
                }
            }
            m_activity.onGameUpdate(deltaTime);
        }

        for (GameObject gameObject : m_gameObjects)
        {
            gameObject.draw(vpMatrix);
        }

        Input.update();
    }
}
