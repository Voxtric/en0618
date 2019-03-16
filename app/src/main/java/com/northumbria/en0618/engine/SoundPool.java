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

    private static final int SOURCE_QUALITY = 0;
    private static final float FULL_VOLUME = 1.0f;
    private static final int PRIORITY = 9;
    private static final int LOOPS = 0;
    private static final float RATE = 1.0f;

    private android.media.SoundPool m_soundPool;

    private SparseIntArray m_soundIDs = new SparseIntArray();
    private HashSet<Integer> m_soundsToPlayOnLoad = new HashSet<>();

    public SoundPool(Context context, int streamCount, @RawRes int[] soundResourceIDs)
    {
        m_soundPool = new android.media.SoundPool(streamCount, AudioManager.STREAM_MUSIC, SOURCE_QUALITY);
        m_soundPool.setOnLoadCompleteListener(this);
        if (soundResourceIDs != null)
        {
            loadSounds(context, soundResourceIDs);
        }
    }

    @Override
    public void onLoadComplete(android.media.SoundPool soundPool, int soundID, int status)
    {
        if (status == 0 && m_soundsToPlayOnLoad.contains(soundID))
        {
            m_soundsToPlayOnLoad.remove(soundID);
            playSound(soundID, null, -1);
        }
    }

    public void loadSounds(Context context, @RawRes int[] soundResourceIDs)
    {
        for (int soundResourceID : soundResourceIDs)
        {
            loadSound(context, soundResourceID);
        }
    }

    @RawRes
    private int loadSound(Context context, @RawRes int soundResourceID)
    {
        int soundID = m_soundPool.load(context, soundResourceID, PRIORITY);
        m_soundIDs.append(soundResourceID, soundID);
        return soundID;
    }

    private int playSound(int soundID, Context context, @RawRes int soundResourceID)
    {
        // TODO: Sounds should not be played unless the preferences allow it.
        int streamID  = m_soundPool.play(soundID, FULL_VOLUME, FULL_VOLUME, PRIORITY, LOOPS, RATE);
        if (streamID == 0)
        {
            String resourceName = "ERROR: unknown_sound_resource";
            if (context != null && soundResourceID != -1)
            {
                resourceName = context.getResources().getResourceName(soundResourceID);
            }
            Log.e(TAG, String.format("Failed to play \"%s\"", resourceName));
        }
        return streamID;
    }

    public int playSound(Context context, @RawRes int soundResourceID)
    {
        int streamID = -1;
        int soundID = m_soundIDs.get(soundResourceID, -1);
        if (soundID == -1)
        {
            Log.e(TAG, String.format("Sound asset \"%s\" not pre-loaded.", context.getResources().getResourceName(soundResourceID)));
            soundID = loadSound(context, soundResourceID);
            m_soundsToPlayOnLoad.add(soundID);
        }
        else
        {
            streamID = playSound(soundID, context, soundResourceID);
        }
        return streamID;
    }

    public void stopSound(int streamID)
    {
        m_soundPool.stop(streamID);
    }

    public void pauseAll()
    {
        // TODO: We need unpausable sounds for pause/gameover signifiers.
        m_soundPool.autoPause();
    }

    public void resumeAll()
    {
        m_soundPool.autoResume();
    }

    public void release()
    {
        m_soundPool.release();
        m_soundPool = null;
        m_soundIDs.clear();
        m_soundsToPlayOnLoad.clear();
    }
}
