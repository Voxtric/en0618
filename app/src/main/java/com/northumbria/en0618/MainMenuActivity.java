package com.northumbria.en0618;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity
{
    NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        createNotificationChannel();

        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(this, "pie")
                .setSmallIcon(R.drawable.settings)
                .setContentTitle("Annoying notification")
                .setContentText("Hey PLAY OUR GAME NOW!!!!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("pie", name, importance);

            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startGame(View view)
    {
        Intent intent = new Intent(this, SpaceInvadersActivity.class);
        startActivity(intent);

        //launch the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
    }

    public void toggleSoundEffects(View view) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean currentValue = sp.getBoolean("play_sfx", true);

        sp.edit().putBoolean("play_sfx", !currentValue).apply();

//        Button b = (Button) view;
//        b.setText();
    }

    public void toggleMusic(View view) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean currentValue = sp.getBoolean("play_music", true);

        sp.edit().putBoolean("play_music", !currentValue).apply();

//        Button b = (Button) view;
//        b.setText();
    }


}
