package com.northumbria.en0618.engine;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

public abstract class BackgroundSoundAccessingActivity extends AppCompatActivity
{
    private boolean m_navigatingInApp = false;
    private BackgroundSoundService m_backgroundSoundService = null;
    ServiceConnection m_serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            BackgroundSoundService.LocalBinder binder = (BackgroundSoundService.LocalBinder)service;
            m_backgroundSoundService = binder.getBackgroundSoundServiceInstance();
            onBackgroundSoundServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            m_backgroundSoundService = null;
            onBackgroundSoundServiceUnbound();
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent serviceIntent = new Intent(this, BackgroundSoundService.class);
        bindService(serviceIntent, m_serviceConnection, BIND_IMPORTANT);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (!m_navigatingInApp)
        {
            m_backgroundSoundService.pauseMusic();
        }
        unbindService(m_serviceConnection);
        m_navigatingInApp = false;
    }

    protected void onBackgroundSoundServiceBound()
    {
    }

    protected void onBackgroundSoundServiceUnbound()
    {
    }

    protected BackgroundSoundService getBackgroundSoundService()
    {
        return m_backgroundSoundService;
    }

    protected void startBackgroundSoundService()
    {
        Intent serviceIntent = new Intent(this, BackgroundSoundService.class);
        startService(serviceIntent);
    }

    protected void stopBackgroundSoundService()
    {
        Intent serviceIntent = new Intent(this, BackgroundSoundService.class);
        stopService(serviceIntent);
    }

    protected void notifyActivityChanging()
    {
        m_navigatingInApp = true;
    }
}