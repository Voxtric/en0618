package com.northumbria.en0618;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.northumbria.en0618.engine.Game;
import com.northumbria.en0618.engine.GameActivity;
import com.northumbria.en0618.engine.GameObject;
import com.northumbria.en0618.engine.Input;
import com.northumbria.en0618.engine.TextGameObject;
import com.northumbria.en0618.engine.opengl.Font;
import com.northumbria.en0618.engine.opengl.GameSurfaceView;
import com.northumbria.en0618.engine.opengl.Sprite;

import java.util.Locale;

public class SpaceInvadersActivity extends GameActivity
{
    private static final String PREFERENCE_KEY_HIGH_SCORE = "high_score";

    private static final int POWER_SAVER_FRAME_RATE = 30;

    private static final float PLAYER_SHOT_SPAWN_WAIT = 0.9f;
    private static final float SHOT_OFFSET_MULTIPLIER = 0.45f;

    private static final float SCREEN_DISTANCE_FONT_SIZE = 0.05f;

    private int m_currentLevel = 1;
    private float m_timeToShotSpawn = PLAYER_SHOT_SPAWN_WAIT;
    private float m_playerShotOffset = 0.0f;

    private Player m_player;
    private TextGameObject m_scoreText;
    private TextGameObject m_levelText;
    private CollisionLists m_collidableObjects;
    private AlienManager m_alienManager;
    private AsteroidManager m_asteroidManager;

    private boolean m_inLevelTransition = false;

    private AlertDialog m_gameOverDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
        {
            GamesClient gamesClient = Games.getGamesClient(SpaceInvadersActivity.this, account);
            gamesClient.setViewForPopups(getSurfaceView());
            gamesClient.setGravityForPopups(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        }
    }

    @Override
    public void onGameReady()
    {
        Game game = getGame();
        game.setPauseDialogLayoutID(R.layout.dialog_game_pause);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(MainMenuActivity.PREFERENCE_KEY_POWER_SAVER, false))
        {
            ((GameSurfaceView) getSurfaceView()).setTargetFrameRate(POWER_SAVER_FRAME_RATE);
        }

        Font font = Font.getFont(this, "death_star.ttf", (int)(Input.getScreenHeight() * SCREEN_DISTANCE_FONT_SIZE), 6);
        // Score
        m_scoreText = new TextGameObject(font, "Score: 0", 10.0f, Input.getScreenHeight() - (font.getHeight() * 0.5f) - 10.0f);
        m_scoreText.setColor(1.0f, 1.0f, 1.0f, 0.8f);
        game.addGameObject(m_scoreText, true);
        // Level
        m_levelText = new TextGameObject(font, "Level: 1", 10.0f, Input.getScreenHeight() - (font.getHeight() * 1.5f) - 10.0f);
        m_levelText.setColor(1.0f, 1.0f, 1.0f, 0.8f);
        game.addGameObject(m_levelText, true);

        // Settings Button
        GameObject settingsButton = new SettingsButton(game);
        game.addGameObject(settingsButton, true);

        // Add the tiling background.
        int count = (int)(Input.getScreenHeight() / BackgroundTile.SIZE) + 2;
        float height = count * BackgroundTile.SIZE;
        for (float x = BackgroundTile.SIZE * 0.5f; x < Input.getScreenWidth() + (BackgroundTile.SIZE * 0.5f); x += BackgroundTile.SIZE)
        {
            for (float y = BackgroundTile.SNAP_HEIGHT; y < height; y += BackgroundTile.SIZE)
            {
                BackgroundTile backgroundTile = new BackgroundTile(this, x, y, height);
                ((Sprite)backgroundTile.getRenderable()).setUseTransparency(false);
                game.addGameObject(backgroundTile);
            }
        }

        // Creation of Player Character
        m_player = new Player(game, m_scoreText);
        game.addGameObject(m_player);
        m_playerShotOffset = m_player.getXSize() * SHOT_OFFSET_MULTIPLIER;

        m_collidableObjects = new CollisionLists(m_player, this);

        // Member Variable to store all Collidable Objects for Automated Collision Detection

        // Manager class for all ALien Objects
        m_alienManager = new AlienManager(m_collidableObjects, game);
        m_alienManager.createAliens();

        m_asteroidManager = new AsteroidManager(m_collidableObjects, game);
        m_asteroidManager.createAsteroids();


        super.onGameReady();
    }

    @Override
    public void onGameUpdate(float deltaTime)
    {
        final Game game = getGame();
        if(!m_player.isDead() && !m_alienManager.checkAlienWin())
        {
            if(m_alienManager.alienRemaining())
            {
                // Only runs if there is at least 1 alien alive
                m_collidableObjects.checkCollisions(); // Checks Collisions for all objects
                m_alienManager.update(deltaTime); // Updates Manager for Positional Checks and removal

                m_timeToShotSpawn -= deltaTime;
                if(m_timeToShotSpawn <= 0.0f)
                {
                    // Calculates time to fire weapon and creates Bullet
                    float bulletX = m_player.getX() + m_playerShotOffset;
                    m_playerShotOffset = -m_playerShotOffset;
                    Bullet bullet = new Bullet(
                            game.getActivity(), R.drawable.player_shot, bulletX, m_player.getY());
                    game.addGameObject(bullet);
                    m_collidableObjects.addBullet(bullet, true);
                    m_timeToShotSpawn = PLAYER_SHOT_SPAWN_WAIT;
                }
            }
            else
            {
                if (!m_inLevelTransition)
                {
                    final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                    if (account != null)
                    {
                        final String[] levelAchievementIDs = getResources().getStringArray(R.array.level_achievement_ids);
                        if (m_currentLevel <= levelAchievementIDs.length)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Games.getAchievementsClient(SpaceInvadersActivity.this, account)
                                            .unlock(levelAchievementIDs[m_currentLevel - 1]);
                                }
                            });
                        }
                    }
                }
                m_inLevelTransition = true;

                if(m_player.newLevel(deltaTime))
                {
                    m_inLevelTransition = false;
                    // newLevel will continue to run until the player has made sufficient moves that result
                    // in it being off the screen.

                    m_currentLevel++;
                    m_levelText.setText(String.format(Locale.getDefault(), "Level: %d", m_currentLevel));

                    m_collidableObjects.destroyAll(false);
                    m_collidableObjects = new CollisionLists(m_player, this);

                    m_alienManager = new AlienManager(m_collidableObjects, game);
                    m_alienManager.incrementBaseAlienSpeed(m_currentLevel);
                    m_alienManager.createAliens();

                    m_asteroidManager = new AsteroidManager(m_collidableObjects, game);
                    m_asteroidManager.createAsteroids();
                }
            }
        }
        else
        {
            endGame(m_player.getScore());
        }
    }

    public int getCurrentLevel()
    {
        return m_currentLevel;
    }

    public void restartGame(View view)
    {
        Game game = getGame();

        m_currentLevel = 1;
        m_levelText.setText("Level: 1");

        m_player = new Player(game, m_scoreText);
        game.addGameObject(m_player);
        m_scoreText.setText("Score: 0");

        m_collidableObjects.destroyAll(true);
        m_collidableObjects = new CollisionLists(m_player, SpaceInvadersActivity.this);

        m_alienManager = new AlienManager(m_collidableObjects, game);
        m_alienManager.createAliens();

        m_asteroidManager = new AsteroidManager(m_collidableObjects, game);
        m_asteroidManager.createAsteroids();

        game.setSuppressPauseDialog(false);
        game.unPause();
        m_gameOverDialog.dismiss();
    }

    public void returnToMainMenu(View view)
    {
        m_gameOverDialog.dismiss();
        m_gameOverDialog = null;
        finish();
    }

    private void endGame(final long score)
    {
        getGame().pause(false);
        getGame().setSuppressPauseDialog(true);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_game_over, null);
                TextView scoreTextView = view.findViewById(R.id.score_text_view);
                scoreTextView.setText(String.format(Locale.getDefault(), "Score: %d", score));
                final ProgressBar newHighScoreProgressBar = view.findViewById(R.id.new_high_score_progress_bar);
                final TextView newHighScoreTextView = view.findViewById(R.id.new_high_score_text_view);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SpaceInvadersActivity.this);
                final long score = m_player.getScore();
                final long locallySavedHighScore = preferences.getLong(PREFERENCE_KEY_HIGH_SCORE, 0);
                if (score > locallySavedHighScore)
                {
                  preferences.edit().putLong(PREFERENCE_KEY_HIGH_SCORE, score).apply();
                }

                final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SpaceInvadersActivity.this);
                if (account == null)
                {
                  newHighScoreProgressBar.setVisibility(View.GONE);
                  newHighScoreTextView.setVisibility(View.VISIBLE);
                  if (score < locallySavedHighScore)
                  {
                      newHighScoreTextView.setText(String.format(Locale.getDefault(), "Current High Score: %d", locallySavedHighScore));
                  }
                }
                else
                {
                  final LeaderboardsClient leaderboardsClient = Games.getLeaderboardsClient(SpaceInvadersActivity.this, account);
                  // Always submit the score as google tracks high scores over different periods.
                  leaderboardsClient.submitScore(getString(R.string.global_leaderboard_id), m_player.getScore());

                  Task<AnnotatedData<LeaderboardScore>> loadScoreTask =
                          leaderboardsClient.loadCurrentPlayerLeaderboardScore(
                                  getString(R.string.global_leaderboard_id),
                                  LeaderboardVariant.TIME_SPAN_ALL_TIME,
                                  LeaderboardVariant.COLLECTION_PUBLIC);
                  loadScoreTask.addOnFailureListener(new OnFailureListener()
                  {
                      @Override
                      public void onFailure(@NonNull Exception exception)
                      {
                          newHighScoreProgressBar.setVisibility(View.GONE);
                          newHighScoreTextView.setVisibility(View.VISIBLE);
                          if (score < locallySavedHighScore)
                          {
                              newHighScoreTextView.setText(String.format(Locale.getDefault(), "Current High Score: %d", locallySavedHighScore));
                          }
                      }
                  });
                  loadScoreTask.addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardScore>>()
                  {
                      @Override
                      public void onSuccess(AnnotatedData<LeaderboardScore> leaderboardScoreAnnotatedData)
                      {
                          LeaderboardScore leaderboardScores = leaderboardScoreAnnotatedData.get();
                          if (leaderboardScores != null)
                          {
                              long cloudSavedHighScore = leaderboardScores.getRawScore();
                              long highScore = Math.max(locallySavedHighScore, cloudSavedHighScore);
                              newHighScoreProgressBar.setVisibility(View.GONE);
                              newHighScoreTextView.setVisibility(View.VISIBLE);
                              if (score < highScore)
                              {
                                  newHighScoreTextView.setText(String.format(Locale.getDefault(), "Current High Score: %d", highScore));
                              }
                          }
                      }
                  });
                  loadScoreTask.addOnCompleteListener(new OnCompleteListener<AnnotatedData<LeaderboardScore>>()
                  {
                      @Override
                      public void onComplete(@NonNull Task<AnnotatedData<LeaderboardScore>> task)
                      {
                          if (newHighScoreProgressBar.getVisibility() != View.GONE)
                          {
                              newHighScoreProgressBar.setVisibility(View.GONE);
                              newHighScoreTextView.setVisibility(View.VISIBLE);
                              if (score < locallySavedHighScore)
                              {
                                  newHighScoreTextView.setText(String.format(Locale.getDefault(), "Current High Score: %d", locallySavedHighScore));
                              }
                          }
                      }
                  });
                }

                m_gameOverDialog = new AlertDialog.Builder(SpaceInvadersActivity.this)
                      .setView(view)
                      .create();
                m_gameOverDialog.setCancelable(false);
                m_gameOverDialog.setCanceledOnTouchOutside(false);
                m_gameOverDialog.show();
            }
        });
    }
}
