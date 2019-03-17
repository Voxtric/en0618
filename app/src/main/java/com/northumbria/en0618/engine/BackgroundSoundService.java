package com.northumbria.en0618.engine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import com.northumbria.en0618.SettingsActivity;

public class BackgroundSoundService extends Service implements AudioManager.OnAudioFocusChangeListener
{
    // TODO: Move all sound pools into background service.
    private static final String TAG = "BackgroundSoundService";

    private static final float FULL_VOLUME = 1.0f;
    private static final float DUCKED_VOLUME = 0.3f;
    private static final int AUDIO_STREAM_COUNT = 10;

    public class LocalBinder extends Binder
    {
        public BackgroundSoundService getBackgroundSoundServiceInstance()
        {
            return BackgroundSoundService.this;
        }
    }

    private LocalBinder m_binder = new LocalBinder();
    private int m_clientsBound = 0;

    private MediaPlayer m_mediaPlayer = null;
    @RawRes private int m_musicID = -1;
    private int m_musicPosition = -1;

    private SoundPool m_soundPool;

    private boolean m_hasAudioFocus = false;
    private float m_volume = FULL_VOLUME;

    @Override
    public void onCreate()
    {
        super.onCreate();
        m_soundPool = new SoundPool(this, AUDIO_STREAM_COUNT, null);
    }

    @Override
    public void onDestroy()
    {
        releaseAudioFocus();
        stopMusic(true);
        m_soundPool.release();
        m_soundPool = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        requestAudioFocus();
        return m_binder;
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {
        case AudioManager.AUDIOFOCUS_GAIN:
            m_hasAudioFocus = true;
            m_volume = FULL_VOLUME;
            m_soundPool.setVolume(m_volume);
            if (m_clientsBound > 0)
            {
                if (m_mediaPlayer == null)
                {
                    if (m_musicID != -1)
                    {
                        initialiseMediaPlayer(m_musicID);
                    }
                }
                else
                {
                    resumeMusic();
                    m_mediaPlayer.setVolume(m_volume, m_volume);
                }
            }
            break;
        case AudioManager.AUDIOFOCUS_LOSS:
            m_hasAudioFocus = false;
            if (m_mediaPlayer != null)
            {
                m_musicPosition = m_mediaPlayer.getCurrentPosition();
                m_mediaPlayer.stop();
                m_mediaPlayer.release();
                m_mediaPlayer = null;
            }
            break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            m_hasAudioFocus = false;
            pauseMusic();
            break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            m_volume = DUCKED_VOLUME;
            m_soundPool.setVolume(m_volume);
            if (m_mediaPlayer != null)
            {
                m_mediaPlayer.setVolume(m_volume, m_volume);
            }
            break;
        }
    }

    public void clientBound()
    {
        m_clientsBound++;
    }

    public void clientUnBound()
    {
        m_clientsBound--;
    }

    private void requestAudioFocus()
    {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
        {
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            m_hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            if (!m_hasAudioFocus)
            {
                Log.e(TAG, "Audio focus not granted.");
            }
        }
    }

    private void releaseAudioFocus()
    {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
        {
            audioManager.abandonAudioFocus(this);
        }
    }

    private void initialiseMediaPlayer(@RawRes int musicID)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(SettingsActivity.PREFERENCE_KEY_MUSIC, true))
        {
            m_mediaPlayer = MediaPlayer.create(this, musicID);
            m_mediaPlayer.setLooping(true);
            m_mediaPlayer.start();
            m_mediaPlayer.setVolume(m_volume, m_volume);
            if (m_musicPosition != -1)
            {
                m_mediaPlayer.seekTo(m_musicPosition);
            }
        }
    }

    public void startMusic(@RawRes int musicID)
    {
        if (m_mediaPlayer != null)
        {
            if (m_musicID == musicID)
            {
                m_mediaPlayer.seekTo(0);
            }
            else
            {
                stopMusic(true);
                initialiseMediaPlayer(musicID);
            }
        }
        else
        {
            initialiseMediaPlayer(musicID);
        }
        m_musicID = musicID;
    }

    public void startMusic()
    {
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.seekTo(0);
        }
        else
        {
            initialiseMediaPlayer(m_musicID);
        }
    }

    public void stopMusic(boolean unsetMusicID)
    {
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.stop();
            m_mediaPlayer.release();
            m_mediaPlayer = null;
            m_musicPosition = -1;
            if (unsetMusicID)
            {
                m_musicID = -1;
            }
        }
    }

    public void pauseMusic()
    {
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.pause();
        }
    }

    public void resumeMusic()
    {
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.start();
        }
        else if (m_musicID != -1)
        {
            startMusic(m_musicID);
        }
    }

    public boolean musicStarted()
    {
        return m_mediaPlayer != null;
    }

    public int playSound(@RawRes int soundResourceID)
    {
        int streamID = -1;
        if (m_hasAudioFocus)
        {
            streamID = m_soundPool.playSound(this, soundResourceID);
        }
        return streamID;
    }

    public void stopSound(int streamID)
    {
        m_soundPool.stopSound(streamID);
    }

    public void resumeAllSounds()
    {
        m_soundPool.resumeAll();
    }

    public void pauseAllSounds()
    {
        m_soundPool.pauseAll();
    }

    public void loadSounds(@RawRes int[] soundResourceIDs)
    {
        m_soundPool.loadSounds(this, soundResourceIDs);
    }

    public void unloadSounds(@RawRes int[] soundResourceIDs)
    {
        m_soundPool.unloadSounds(soundResourceIDs);
    }
}
