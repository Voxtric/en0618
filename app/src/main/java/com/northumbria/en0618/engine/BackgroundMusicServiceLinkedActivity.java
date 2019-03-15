package com.northumbria.en0618.engine;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

public abstract class BackgroundMusicServiceLinkedActivity extends AppCompatActivity
{
    private boolean m_navigatingInApp = false;
    private BackgroundMusicService m_backgroundMusicService = null;
    ServiceConnection m_serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            BackgroundMusicService.LocalBinder binder = (BackgroundMusicService.LocalBinder)service;
            m_backgroundMusicService = binder.getBackgroundSoundServiceInstance();
            onBackgroundSoundServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            m_backgroundMusicService = null;
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent serviceIntent = new Intent(this, BackgroundMusicService.class);
        bindService(serviceIntent, m_serviceConnection, BIND_IMPORTANT);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (!m_navigatingInApp)
        {
            m_backgroundMusicService.pauseMusic();
        }
        unbindService(m_serviceConnection);
        onBackgroundSoundServiceUnbound();
        m_navigatingInApp = false;
    }

    protected void onBackgroundSoundServiceBound()
    {
        m_backgroundMusicService.clientBound();
    }

    protected void onBackgroundSoundServiceUnbound()
    {
        m_backgroundMusicService.clientUnBound();
    }

    protected BackgroundMusicService getBackgroundSoundService()
    {
        return m_backgroundMusicService;
    }

    protected void startBackgroundSoundService()
    {
        Intent serviceIntent = new Intent(this, BackgroundMusicService.class);
        startService(serviceIntent);
    }

    protected void stopBackgroundSoundService()
    {
        Intent serviceIntent = new Intent(this, BackgroundMusicService.class);
        stopService(serviceIntent);
    }

    protected void notifyActivityChanging()
    {
        m_navigatingInApp = true;
    }
}
