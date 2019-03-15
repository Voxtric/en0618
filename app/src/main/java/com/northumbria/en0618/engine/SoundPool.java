package com.northumbria.en0618.engine;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.RawRes;
import android.util.Log;
import android.util.SparseIntArray;

import java.util.HashSet;

public class SoundPool implements android.media.SoundPool.OnLoadCompleteListener
{
    private static final String TAG = "SoundPool";

    private Context m_context;
    private android.media.SoundPool m_soundPool;
    private SparseIntArray m_soundIDs = new SparseIntArray();
    private HashSet<Integer> m_soundsToPlayOnLoad = new HashSet<>();

    public SoundPool(Context context, int streamCount, @RawRes int[] soundResourceIDs)
    {
        m_context = context;
        m_soundPool = new android.media.SoundPool(streamCount, AudioManager.STREAM_MUSIC, 0);
        m_soundPool.setOnLoadCompleteListener(this);
        if (soundResourceIDs != null)
        {
            for (int soundResourceID : soundResourceIDs)
            {
                int soundID = m_soundPool.load(context, soundResourceID, 1);
                m_soundIDs.append(soundResourceID, soundID);
            }
        }
    }

    @Override
    public void onLoadComplete(android.media.SoundPool soundPool, int soundID, int status)
    {
        if (status == 0 && m_soundsToPlayOnLoad.contains(soundID))
        {
            m_soundsToPlayOnLoad.remove(soundID);
            m_soundPool.play(soundID, 1.0f, 1.0f, 9, 0, 1.0f);
        }
    }

    public void playSound(@RawRes int soundResourceID)
    {
        int soundID = m_soundIDs.get(soundResourceID, -1);
        if (soundID == -1)
        {
            Log.e(TAG, String.format("Sound asset \"%s\" not pre-loaded.", m_context.getResources().getResourceName(soundResourceID)));
            soundID = m_soundPool.load(m_context, soundResourceID, 1);
            m_soundsToPlayOnLoad.add(soundID);
            m_soundIDs.append(soundResourceID, soundID);
        }
        else
        {
            int streamID  = m_soundPool.play(soundID, 1.0f, 1.0f, 9, 0, 1.0f);
            if (streamID == 0)
            {
                Log.e(TAG, String.format("Failed to play \"%s\"", m_context.getResources().getResourceName(soundResourceID)));
            }
        }
    }

    public void pauseAll()
    {
        m_soundPool.autoPause();
    }

    public void unpauseAll()
    {
        m_soundPool.autoResume();
    }

    public void release()
    {
        m_soundPool.release();
        m_context = null;
        m_soundPool = null;
        m_soundIDs.clear();
        m_soundsToPlayOnLoad.clear();
    }
}
