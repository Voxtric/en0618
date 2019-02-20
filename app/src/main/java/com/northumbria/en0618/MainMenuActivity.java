package com.northumbria.en0618;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    public static final String PREFERENCE_KEY_POWER_SAVER = "power_saver";

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

        Typeface font = Typeface.createFromAsset(getAssets(), "death_star.ttf");
        ViewGroup root = findViewById(R.id.view_root);
        setAllFonts(root, font);

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

    // Updates google_play_games_button UI element based on google sign in status.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN_ACTIVITY)
        {
            // Check if a sign in was successful, if not display a message explaining whu.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                initialiseGooglePlayGamesButtonForLeaderboard(result.getSignInAccount());
            }
            else
            {
                // Apparently google provides error codes, but I've never seen them,
                // so we have our own.
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty())
                {
                    message = getString(R.string.google_sign_in_fail_message);
                }
                // Show the error message.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.google_sign_in_fail_title)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
        else if (requestCode == REQUEST_CODE_LEADERBOARD_ACTIVITY)
        {
            // Check if a sign in can be found, if not then the player needs to sign in.
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account == null)
            {
                initialiseGooglePlayGamesButtonForSignIn();
            }
        }
    }

    // Initialises the google_play_games_button UI element based on google sign in status.
    private void determineGooglePlayGamesButton()
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null)
        {
            // No account found so allow sign in.
            initialiseGooglePlayGamesButtonForSignIn();
        }
        else
        {
            // Account found so allow leaderboards.
            initialiseGooglePlayGamesButtonForLeaderboard(account);
        }
    }

    // Initialises the google_play_games_button UI element to allow the user to sign in via google.
    private void initialiseGooglePlayGamesButtonForSignIn()
    {
        Button button = findViewById(R.id.google_play_games_button);
        button.setText(R.string.google_sign_in_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create an activity to sign into google for the explicit purpose of using
                // google play game services.
                GoogleSignInOptions googleSignIn =
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                                .build();
                GoogleSignInClient client =
                        GoogleSignIn.getClient(MainMenuActivity.this, googleSignIn);
                Intent intent = client.getSignInIntent();
                startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN_ACTIVITY);
            }
        });
    }

    // Initialises the google_play_games_button UI element to allow the user to view the global
    // leaderboard for the game.
    private void initialiseGooglePlayGamesButtonForLeaderboard(final GoogleSignInAccount account)
    {
        Button button = findViewById(R.id.google_play_games_button);
        button.setText(R.string.leaderboard_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create an activity to view the global leaderboard for the game.
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

    private void setAllFonts(ViewGroup viewGroup, Typeface font)
    {
        for (int i = 0; i < viewGroup.getChildCount(); i++)
        {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup)
            {
                setAllFonts((ViewGroup)child, font);
            }
            else if (child instanceof Button)
            {
                ((Button)child).setTypeface(font);
            }
            else if (child instanceof TextView)
            {
                ((TextView)child).setTypeface(font);
            }
        }
    }

    // Updates the app settings and the player with the new input method.
    @SuppressLint("ApplySharedPref")    // We want the data to be available immediately.
    public void changeInputMethod(View view)
    {
        // Find the current input method.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int inputMethod = preferences.getInt(Player.PREFERENCE_KEY_INPUT_METHOD, Player.INPUT_METHOD_SCREEN_SIDE);
        int newInputMethod = inputMethod;

        // Determine the new input method and update the UI.
        switch (inputMethod)
        {
            case Player.INPUT_METHOD_SCREEN_SIDE:
                newInputMethod = Player.INPUT_METHOD_PLAYER_SIDE;
                // TODO: Update button text here.
                break;
            case Player.INPUT_METHOD_PLAYER_SIDE:
                newInputMethod = Player.INPUT_METHOD_SCREEN_TILT;
                // TODO: Update button text here.
                break;
            case Player.INPUT_METHOD_SCREEN_TILT:
                newInputMethod = Player.INPUT_METHOD_SCREEN_SIDE;
                // TODO: Update button text here.
                break;
        }

        // Update the settings and the player.
        preferences.edit().putInt(Player.PREFERENCE_KEY_INPUT_METHOD, newInputMethod).commit();
        Player.updateInputMethod(this);
    }

    public void togglePowerSaver(View view)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean powerSaverOn = preferences.getBoolean(PREFERENCE_KEY_POWER_SAVER, false);
        boolean newPowerSaverOn = !powerSaverOn;
        if (newPowerSaverOn)
        {
            // TODO: Update button text here.
        }
        else
        {
            // TODO: Update button text here.
        }
        preferences.edit().putBoolean(PREFERENCE_KEY_POWER_SAVER, newPowerSaverOn).apply();
    }

    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
            {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("pie", name, importance);

                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            }
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
