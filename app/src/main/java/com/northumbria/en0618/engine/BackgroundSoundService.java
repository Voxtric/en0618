package com.northumbria.en0618.engine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

public class BackgroundSoundService extends Service implements AudioManager.OnAudioFocusChangeListener
{
    private static final String TAG = "BackgroundSoundService";

    private static final float FULL_VOLUME = 100.0f;
    private static final float DUCKED_VOLUME = 30.0f;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return m_binder;
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {
        case AudioManager.AUDIOFOCUS_GAIN:
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
                    unpauseMusic();
                }
            }
            break;
        case AudioManager.AUDIOFOCUS_LOSS:
            m_musicPosition = m_mediaPlayer.getCurrentPosition();
            m_mediaPlayer.stop();
            m_mediaPlayer.release();
            m_mediaPlayer = null;
            break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            pauseMusic();
            break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            m_mediaPlayer.setVolume(DUCKED_VOLUME, DUCKED_VOLUME);
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
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
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
        m_mediaPlayer = MediaPlayer.create(this, musicID);
        m_mediaPlayer.setLooping(true);
        requestAudioFocus();
        m_mediaPlayer.start();
        if (m_musicPosition != -1)
        {
            m_mediaPlayer.seekTo(m_musicPosition);
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
                stopMusic();
                initialiseMediaPlayer(musicID);
            }
        }
        else
        {
            initialiseMediaPlayer(musicID);
        }
        m_musicID = musicID;
    }

    public void stopMusic()
    {
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.stop();
            m_mediaPlayer.release();
            m_mediaPlayer = null;
            m_musicID = -1;
            m_musicPosition = -1;
        }
        releaseAudioFocus();
    }

    public void pauseMusic()
    {
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.pause();
        }
    }

    public void unpauseMusic()
    {
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.setVolume(FULL_VOLUME, FULL_VOLUME);
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
}
