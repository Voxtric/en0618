package com.northumbria.en0618.engine;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.northumbria.en0618.R;

public class SoundManager {
    //MediaPlayer menuMediaPlayer;
    //MediaPlayer gameMediaPlayer;
    //MediaPlayer gameOverMediaPlayer;

    private static final SoundManager getInstance = new SoundManager();

    public static SoundManager getInstance() {
        return getInstance;
    }

    private SoundPool globalSoundPool;
    //SoundPool.Builder globalSoundPoolBuilder;
    private Context mContext;

    AudioAttributes globalAttributes;
    AudioAttributes.Builder globalAttributesBuilder;

    // Stored sound int for use within the game
    int gSoundID_menuSelect;
    int gSoundID_playerFire;
    int gSoundID_enemyFire;
    int gSoundID_playerHit;
    int gSoundID_enemyHit;
    int gSoundID_barrierHit;

    int scenePlaying = -1;

    Context context;
    boolean loaded = false; // sets is loaded to false
    public static final int MAX_STREAMS = 10; // sets max streams playing to 10

    public SoundManager() {

    }

    public void Init(Context context) {
        this.context = context;

        // checks if sounds are not loaded
        if (!loaded) {
            loaded = true;
            gCreateSoundPool(); // creates sound pool
            gLoadSounds();      // loads sounds to be used
        }
    }

    protected void gLoadSounds() {
        // loads sound effects
        gSoundID_menuSelect = globalSoundPool.load(context, R.raw.menuselect, 1);
        gSoundID_playerFire = globalSoundPool.load(context, R.raw.playerfire, 1);
        gSoundID_enemyFire = globalSoundPool.load(context, R.raw.enemyfire, 1);
        gSoundID_playerHit = globalSoundPool.load(context, R.raw.playerhit, 1);
        gSoundID_enemyHit = globalSoundPool.load(context, R.raw.enemyhit, 1);
        gSoundID_barrierHit = globalSoundPool.load(context, R.raw.barrierhit, 1);

        // loads media tracks for background music
        //menuMediaPlayer = MediaPlayer.create(context, R.raw.mainmenu);
        //gameMediaPlayer = MediaPlayer.create(context, R.raw.gamemusic);
        //gameOverMediaPlayer = MediaPlayer.create(context, R.raw.gameover);

        // loops audio tracks
        //menuMediaPlayer.setLooping(true);
        //gameMediaPlayer.setLooping(true);
        //gameOverMediaPlayer.setLooping(true);

    }

    public void gCreateSoundPool() {
        globalSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
    }

    // method to play game SFX
    public void gPlaySoundMenu() {
        globalSoundPool.play(gSoundID_menuSelect, 1, 1, 1, 0, 1);
    }

    public void gPlaySoundEnemyHit() {
        globalSoundPool.play(gSoundID_enemyHit, 1, 1, 1, 0, 1);
    }

    public void gPlaySoundPlayerHit() {
        globalSoundPool.play(gSoundID_playerHit, 1, 1, 1, 0, 1);
    }

    public void gPlaySoundBarrierHit() {
        globalSoundPool.play(gSoundID_barrierHit, 1, 1, 1, 0, 1);
    }

    public void gPlaySoundEnemyFiring() {
        globalSoundPool.play(gSoundID_enemyFire, 1, 1, 1, 0, 1);
    }

    public void gPlaySoundPlayerFiring() {
        globalSoundPool.play(gSoundID_playerFire, 1, 1, 1, 0, 1);
    }

}

//    // play main-menu music
//    public void gPlayMainMenu()
//    {
//        gStopMusic();
//        scenePlaying = 0;
//        menuMediaPlayer.start();
//    }
//
//    // play in game music
//    public void gPlayGameMusic()
//    {
//        gStopMusic();
//        scenePlaying = 1;
//        gameMediaPlayer.start();
//    }
//
//    // play game over music
//    public void gPlayGameOver()
//    {
//        gStopMusic();
//        scenePlaying = 2;
//        gameOverMediaPlayer.start();
//    }
//
//    // resume currently paused media track
//    public void gResume()
//    {
//        switch(scenePlaying)
//        {
//            case -1:
//                break;
//            case 0:
//                menuMediaPlayer.start();
//                break;
//            case 1:
//                gameMediaPlayer.start();
//                break;
//            case 2:
//                gameOverMediaPlayer.start();
//                break;
//
//        }
//    }
//
//    // pause currently paused media track
//    public void gPause()
//    {
//        switch(scenePlaying)
//        {
//            case -1:
//                break;
//            case 0:
//                menuMediaPlayer.pause();
//                break;
//            case 1:
//                gameMediaPlayer.pause();
//                break;
//            case 2:
//                gameOverMediaPlayer.pause();
//                break;
//
//        }
//    }
//
//    // stop currently playing track
//    public void gStopMusic()
//    {
//        switch(scenePlaying)
//        {
//            case -1:
//                break;
//            case 0:
//                menuMediaPlayer.stop();
//                menuMediaPlayer = MediaPlayer.create(context, R.raw.mainmenu);
//
//                break;
//            case 1:
//                gameMediaPlayer.stop();
//                gameMediaPlayer = MediaPlayer.create(context, R.raw.gamemusic);
//
//                break;
//            case 2:
//                gameOverMediaPlayer = MediaPlayer.create(context, R.raw.gameover);
//                break;
//
//        }
//
//    }
//}

