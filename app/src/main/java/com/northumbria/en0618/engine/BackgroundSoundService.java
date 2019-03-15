package com.northumbria.en0618.engine;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

public class BackgroundSoundService extends Service
{
    public class LocalBinder extends Binder
    {
        public BackgroundSoundService getBackgroundSoundServiceInstance()
        {
            return BackgroundSoundService.this;
        }
    }

    private LocalBinder m_binder = new LocalBinder();
    private MediaPlayer m_mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return m_binder;
    }

    public void startMusic(@RawRes int musicID)
    {
        if (m_mediaPlayer != null)
        {
            stopMusic();
        }
        m_mediaPlayer = MediaPlayer.create(this, musicID);
        m_mediaPlayer.setLooping(true);
        m_mediaPlayer.start();
    }

    public void stopMusic()
    {
        m_mediaPlayer.stop();
        m_mediaPlayer.release();
        m_mediaPlayer = null;
    }

    public void pauseMusic()
    {
        m_mediaPlayer.pause();
    }

    public void unpauseMusic()
    {
        m_mediaPlayer.start();
    }

    public boolean hasMusic()
    {
        return m_mediaPlayer != null;
    }
}
