package com.northumbria.en0618.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.annotation.RawRes;
import android.util.Log;
import android.util.SparseArray;

import com.northumbria.en0618.SettingsActivity;

import java.util.HashSet;

public class SoundPool implements android.media.SoundPool.OnLoadCompleteListener
{
    private class Sound
    {
        final int soundID;
        final boolean pauseable;
        int references;

        Sound(int soundID, boolean pauseable)
        {
            this.soundID = soundID;
            this.pauseable = pauseable;
            references = 1;
        }
    }

    private static final String TAG = "SoundPool";

    private static final int SOURCE_QUALITY = 0;
    private static final int PRIORITY = 9;
    private static final int LOOPS = 0;
    private static final float RATE = 1.0f;

    private android.media.SoundPool m_soundPool;
    private float m_volume = 1.0f;

    private SparseArray<Sound> m_sounds = new SparseArray<>();
    private SparseArray<Sound> m_soundsToPlayOnLoad = new SparseArray<>();

    private HashSet<Integer> m_activeStreams = new HashSet<>();
    private HashSet<Integer> m_pausableStreams = new HashSet<>();

    SoundPool(int streamCount)
    {
        m_soundPool = new android.media.SoundPool(streamCount, AudioManager.STREAM_MUSIC, SOURCE_QUALITY);
        m_soundPool.setOnLoadCompleteListener(this);
    }

    @Override
    public void onLoadComplete(android.media.SoundPool soundPool, int soundID, int status)
    {
        if (status == 0)
        {
            Sound sound = m_soundsToPlayOnLoad.get(soundID);
            if (sound != null)
            {
                m_soundsToPlayOnLoad.remove(soundID);
                playSound(sound, null, -1);
            }
        }
    }

    private Sound loadSound(Context context, @RawRes int soundResourceID, boolean pausable)
    {
        Sound sound = m_sounds.get(soundResourceID);
        if (sound == null)
        {
            int soundID = m_soundPool.load(context, soundResourceID, PRIORITY);
            sound = new Sound(soundID, pausable);
            m_sounds.append(soundResourceID, sound);
        }
        else
        {
            sound.references--;
        }
        return sound;
    }

    private void unloadSound(@RawRes int soundResourceID)
    {
        Sound sound = m_sounds.get(soundResourceID);
        if (sound != null)
        {
            sound.references--;
            if (sound.references == 0)
            {
                m_soundPool.unload(sound.soundID);
                m_sounds.delete(soundResourceID);
            }
        }
    }

    public void loadSounds(Context context, @RawRes int[] soundResourceIDs, boolean pausable)
    {
        for (int soundResourceID : soundResourceIDs)
        {
            loadSound(context, soundResourceID, pausable);
        }
    }

    public void unloadSounds(@RawRes int[] soundResourceIDs)
    {
        for (int soundResourceID : soundResourceIDs)
        {
            unloadSound(soundResourceID);
        }
    }

    private int playSound(Sound sound, Context context, @RawRes int soundResourceID)
    {
        int streamID  = m_soundPool.play(sound.soundID, m_volume, m_volume, PRIORITY, LOOPS, RATE);
        if (streamID == 0)
        {
            String resourceName = "ERROR: unknown_sound_resource";
            if (context != null && soundResourceID != -1)
            {
                resourceName = context.getResources().getResourceName(soundResourceID);
            }
            Log.e(TAG, String.format("Failed to play \"%s\"", resourceName));
        }
        else
        {
            m_activeStreams.add(streamID);
            if (sound.pauseable)
            {
                m_pausableStreams.add(streamID);
            }
            else
            {
                m_pausableStreams.remove(streamID);
            }
        }
        return streamID;
    }

    public int playSound(Context context, @RawRes int soundResourceID)
    {
        int streamID = -1;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(SettingsActivity.PREFERENCE_KEY_SFX, true))
        {
            Sound sound = m_sounds.get(soundResourceID);
            if (sound == null)
            {
                Log.e(TAG, String.format("Sound asset \"%s\" not pre-loaded.", context.getResources().getResourceName(soundResourceID)));
                sound = loadSound(context, soundResourceID, true);
                m_soundsToPlayOnLoad.put(sound.soundID, sound);
            }
            else
            {
                streamID = playSound(sound, context, soundResourceID);
            }
        }
        return streamID;
    }

    public void stopSound(int streamID)
    {
        m_soundPool.stop(streamID);
    }

    public void pauseAll()
    {
        for (int streamID : m_pausableStreams)
        {
            m_soundPool.pause(streamID);
        }
    }

    public void resumeAll()
    {
        for (int streamID : m_pausableStreams)
        {
            m_soundPool.resume(streamID);
        }
    }

    public void setVolume(float volume)
    {
        m_volume = volume;
        for (int streamID : m_activeStreams)
        {
            m_soundPool.setVolume(streamID, volume, volume);
        }
    }

    public void release()
    {
        m_soundPool.release();
        m_soundPool = null;
        m_sounds.clear();
        m_soundsToPlayOnLoad.clear();
    }
}
