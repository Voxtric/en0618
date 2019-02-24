package com.northumbria.en0618;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainMenuActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE_LEADERBOARD_ACTIVITY = 301;
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN_ACTIVITY = 302;

    private MediaPlayer m_mediaPlayer;
    private SoundPool m_soundPool;

    private int m_menuSoundId;
    private static final int MAX_STREAMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        determineGooglePlayGamesButton();

        ViewGroup root = findViewById(R.id.view_root);
        FontUtils.setFont(root, getString(R.string.app_font));

        m_mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mainmenu);
        m_mediaPlayer.start();

        m_soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);

        m_menuSoundId = m_soundPool.load(this, R.raw.menuselect, 1);

        //SoundManager.getInstance().Init(this);
        //SoundManager.getInstance().gPlayMainMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_mediaPlayer.start();
        m_soundPool.autoResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        m_mediaPlayer.pause();
        m_soundPool.autoPause();
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
        GamesClient gamesClient = Games.getGamesClient(this, account);
        gamesClient.setViewForPopups(findViewById(R.id.view_root));

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
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception exception)
                            {
                                exception.printStackTrace();
                            }
                        });
            }
        });
    }

    public void startGameActivity(View view)
    {
        m_soundPool.play(m_menuSoundId, 1, 1, 1, 0, 1);

        Intent intent = new Intent(this, SpaceInvadersActivity.class);
        startActivity(intent);

        m_soundPool.release();
    }

    public void startSettingsActivity(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

//    public void toggleSoundEffects(View view) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean newValue = !sp.getBoolean("play_sfx", true);
//
//        //set the property
//        sp.edit().putBoolean("play_sfx", newValue).apply();
//
//        //update button text
//        String newValueStr = getString(newValue ? R.string.on : R.string.off);
//        Button b = findViewById(R.id.sfx_button);
//
//        b.setText(getString(R.string.sfx_button, newValueStr));
//
//        //start music
//        if (newValue)
//        {
//            m_soundPool.play(m_menuSoundId, 1, 1, 1, 0, 1);
//        }
//    }
//
//    public void toggleMusic(View view)
//    {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean newValue = !sp.getBoolean("play_music", true);
//
//        //set the property
//        sp.edit().putBoolean("play_music", newValue).apply();
//
//        //update button text
//        String newValueStr = getString(newValue ? R.string.on : R.string.off);
//        Button b = findViewById(R.id.music_button);
//
//        b.setText(getString(R.string.music_button, newValueStr));
//
//        //toggle the music
//        //m_soundPool.play(m_menuSoundId, 1, 1, 1, 0, 1);
//        if (!newValue)
//        {
//            m_mediaPlayer.pause();
//        }
//        else
//        {
//            m_mediaPlayer.start();
//        }
//    }
}