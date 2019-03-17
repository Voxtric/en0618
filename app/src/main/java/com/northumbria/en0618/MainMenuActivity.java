package com.northumbria.en0618;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.RawRes;
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
import com.northumbria.en0618.engine.BackgroundMusicServiceLinkedActivity;
import com.northumbria.en0618.engine.BackgroundMusicService;
import com.northumbria.en0618.engine.SoundPool;

public class MainMenuActivity extends BackgroundMusicServiceLinkedActivity
{
    private static final int REQUEST_CODE_LEADERBOARD_ACTIVITY = 301;
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN_ACTIVITY = 302;

    private static final int MAX_SOUND_STREAMS = 1;

    private SoundPool m_soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        determineGooglePlayGamesButton();
        startBackgroundSoundService();

        //set the font for the root view
        ViewGroup root = findViewById(R.id.view_root);
        FontUtils.setFont(root, getString(R.string.app_font));
    }

    @Override
    protected void onBackgroundSoundServiceBound()
    {
        super.onBackgroundSoundServiceBound();
        BackgroundMusicService soundService = getBackgroundSoundService();
        if (soundService.musicStarted())
        {
            soundService.resumeMusic();
        }
        else
        {
            soundService.startMusic(R.raw.background_menu_music);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        @RawRes int[] activitySounds = new int[] { R.raw.button_click_forward };
        m_soundPool = new SoundPool(this, MAX_SOUND_STREAMS, activitySounds);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        m_soundPool.release();
        m_soundPool = null;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopBackgroundSoundService();
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
                m_soundPool.playSound(MainMenuActivity.this, R.raw.button_click_forward);

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
                m_soundPool.playSound(MainMenuActivity.this, R.raw.button_click_forward);

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
        m_soundPool.playSound(this, R.raw.button_click_forward);
        getBackgroundSoundService().stopMusic();
        notifyActivityChanging();
        Intent intent = new Intent(this, SpaceInvadersActivity.class);
        startActivity(intent);
    }

    public void startSettingsActivity(View view)
    {
        m_soundPool.playSound(this, R.raw.button_click_forward);
        notifyActivityChanging();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}