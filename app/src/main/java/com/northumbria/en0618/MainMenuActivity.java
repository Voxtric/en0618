package com.northumbria.en0618;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainMenuActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE_LEADERBOARD_ACTIVITY = 301;
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN_ACTIVITY = 302;

    NotificationCompat.Builder mBuilder;

    MediaPlayer mediaPlayer;
    SoundPool soundPool;

    int menuSoundId;
    public static final int MAX_STREAMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        determineGooglePlayGamesButton();

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

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mainmenu);
        mediaPlayer.start();

        soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);

        menuSoundId = soundPool.load(this, R.raw.menuselect, 1);

        //SoundManager.getInstance().Init(this);
        //SoundManager.getInstance().gPlayMainMenu();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mediaPlayer.start();
        soundPool.autoResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        mediaPlayer.pause();
        soundPool.autoPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN_ACTIVITY)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                initialiseGooglePlayGamesButtonForLeaderboard(result.getSignInAccount());
            }
            else
            {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty())
                {
                    message = getString(R.string.google_sign_in_fail_message);
                }
                new AlertDialog.Builder(this)
                        .setTitle(R.string.google_sign_in_fail_title)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
        else if (requestCode == REQUEST_CODE_LEADERBOARD_ACTIVITY)
        {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account == null)
            {
                initialiseGooglePlayGamesButtonForSignIn();
            }
        }
    }

    private void determineGooglePlayGamesButton()
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null)
        {
            initialiseGooglePlayGamesButtonForSignIn();
        }
        else
        {
            initialiseGooglePlayGamesButtonForLeaderboard(account);
        }
    }

    private void initialiseGooglePlayGamesButtonForSignIn()
    {
        Button button = findViewById(R.id.google_play_games_button);
        button.setText(R.string.google_sign_in_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                GoogleSignInOptions googleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        .build();
                GoogleSignInClient client = GoogleSignIn.getClient(MainMenuActivity.this, googleSignIn);
                Intent intent = client.getSignInIntent();
                startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN_ACTIVITY);
            }
        });
    }

    private void initialiseGooglePlayGamesButtonForLeaderboard(final GoogleSignInAccount account)
    {
        Button button = findViewById(R.id.google_play_games_button);
        button.setText(R.string.leaderboard_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Games.getLeaderboardsClient(MainMenuActivity.this, account)
                        .getLeaderboardIntent(getString(R.string.global_leaderboard_id))
                        .addOnSuccessListener(new OnSuccessListener<Intent>()
                        {
                            @Override
                            public void onSuccess(Intent intent)
                            {
                                startActivityForResult(intent, REQUEST_CODE_LEADERBOARD_ACTIVITY);
                            }
                        });
            }
        });
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

    public void startGameActivity(View view)
    {
        soundPool.play(menuSoundId, 1, 1, 1, 0, 1);

        Intent intent = new Intent(this, SpaceInvadersActivity.class);
        startActivity(intent);

        //launch the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());

        soundPool.release();
    }

    public void toggleSoundEffects(View view) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean currentValue = sp.getBoolean("play_sfx", true);

        sp.edit().putBoolean("play_sfx", !currentValue).apply();

        if (currentValue)
        {
            soundPool.play(menuSoundId, 1, 1, 1, 0, 1);
        }

//        Button b = (Button) view;
//        b.setText();
    }

    public void toggleMusic(View view) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean currentValue = sp.getBoolean("play_music", true);


        //soundPool.play(menuSoundId, 1, 1, 1, 0, 1);
        if (currentValue)
        {
            mediaPlayer.pause();
        }
        else
        {
            mediaPlayer.start();
        }

        sp.edit().putBoolean("play_music", !currentValue).apply();

//        Button b = (Button) view;
//        b.setText();
    }


}
