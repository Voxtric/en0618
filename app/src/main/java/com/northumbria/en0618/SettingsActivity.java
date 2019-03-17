package com.northumbria.en0618;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.northumbria.en0618.engine.BackgroundSoundService;
import com.northumbria.en0618.engine.BackgroundMusicServiceLinkedActivity;

public class SettingsActivity extends BackgroundMusicServiceLinkedActivity
{
    public static final String PREFERENCE_KEY_MUSIC = "play_music";
    public static final String PREFERENCE_KEY_SFX = "play_sfx";
    public static final String PREFERENCE_KEY_POWER_SAVER = "power_saver";

    @RawRes
    private static final int[] ALL_SOUNDS = new int[] {
            R.raw.button_click_forward,
            R.raw.button_click_backward,
            R.raw.button_click_change
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set font for view root
        ViewGroup root = findViewById(R.id.view_root);
        FontUtils.setFont(root, getString(R.string.app_font));

        //setup toggle buttons
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        ToggleButton musicToggle = findViewById(R.id.music_button);
        ToggleButton sfxToggle = findViewById(R.id.sfx_button);
        ToggleButton powerSaverToggle = findViewById(R.id.frame_button);

        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
                if (backgroundSoundService != null)
                {
                    backgroundSoundService.playSound(R.raw.button_click_change);
                }

                sharedPrefs.edit().putBoolean(PREFERENCE_KEY_MUSIC, isChecked).apply();
                if (backgroundSoundService != null)
                {
                    if (isChecked)
                    {
                        backgroundSoundService.startMusic();
                    }
                    else
                    {
                        backgroundSoundService.stopMusic(false);
                    }
                }
            }
        });

        sfxToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                sharedPrefs.edit().putBoolean(PREFERENCE_KEY_SFX, isChecked).apply();
                BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
                if (backgroundSoundService != null)
                {
                    backgroundSoundService.playSound(R.raw.button_click_change);
                }
            }
        });

        powerSaverToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
                if (backgroundSoundService != null)
                {
                    backgroundSoundService.playSound(R.raw.button_click_change);
                }

                sharedPrefs.edit().putBoolean(PREFERENCE_KEY_POWER_SAVER, isChecked).apply();
            }
        });

        //set button values to be the correct settings
        Button button = findViewById(R.id.input_button);
        int inputMethod = sharedPrefs.getInt(Player.PREFERENCE_KEY_INPUT_METHOD, Player.INPUT_METHOD_SCREEN_SIDE);
        button.setText(getInputStringFromPreference(inputMethod));

        boolean isMusicOn = sharedPrefs.getBoolean(PREFERENCE_KEY_MUSIC, true);
        boolean isSFXOn = sharedPrefs.getBoolean(PREFERENCE_KEY_SFX, true);
        boolean isPowerSaverOn = sharedPrefs.getBoolean(PREFERENCE_KEY_POWER_SAVER, false);

        musicToggle.setChecked(isMusicOn);
        sfxToggle.setChecked(isSFXOn);
        powerSaverToggle.setChecked(isPowerSaverOn);
    }

    @Override
    public void onBackgroundSoundServiceBound()
    {
        BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
        backgroundSoundService.resumeMusic();
        backgroundSoundService.loadSounds(ALL_SOUNDS, false);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getBackgroundSoundService().unloadSounds(ALL_SOUNDS);
    }

    @Override
    public void onBackPressed()
    {
        getBackgroundSoundService().playSound(R.raw.button_click_backward);
        notifyActivityChanging();
        super.onBackPressed();
    }

    public void backButtonClick(View view)
    {
        getBackgroundSoundService().playSound(R.raw.button_click_backward);
        notifyActivityChanging();
        finish();
    }

    // Updates the app settings and the player with the new input method.
    @SuppressLint("ApplySharedPref")    // We want the data to be available immediately.
    public void changeInputMethod(View view)
    {
        BackgroundSoundService backgroundSoundService = getBackgroundSoundService();
        if (backgroundSoundService != null)
        {
            backgroundSoundService.playSound(R.raw.button_click_change);
        }

        // Find the current input method.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int inputMethod = preferences.getInt(Player.PREFERENCE_KEY_INPUT_METHOD, Player.INPUT_METHOD_SCREEN_SIDE);
        int newInputMethod = inputMethod;
        Button button = (Button) view;

        // Determine the new input method and update the UI.
        switch (inputMethod)
        {
            case Player.INPUT_METHOD_SCREEN_SIDE:
                newInputMethod = Player.INPUT_METHOD_PLAYER_SIDE;
                break;
            case Player.INPUT_METHOD_PLAYER_SIDE:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER))
                {
                    newInputMethod = Player.INPUT_METHOD_SCREEN_TILT;
                    break;
                }
            case Player.INPUT_METHOD_SCREEN_TILT:
                newInputMethod = Player.INPUT_METHOD_SCREEN_SIDE;
                break;
        }

        //update the button text
        button.setText(getInputStringFromPreference(newInputMethod));

        // Update the settings and the player.
        preferences.edit().putInt(Player.PREFERENCE_KEY_INPUT_METHOD, newInputMethod).apply();
        Player.updateInputMethod(this);
    }

    private String getInputStringFromPreference(int inputMethod)
    {
        @StringRes int stringID;
        switch (inputMethod)
        {
            case Player.INPUT_METHOD_PLAYER_SIDE:
                stringID = R.string.input_button_player_side;
                break;
            case Player.INPUT_METHOD_SCREEN_TILT:
                stringID = R.string.input_button_screen_tilt;
                break;
            case Player.INPUT_METHOD_SCREEN_SIDE:
            default:
                stringID = R.string.input_button_screen_side;
                break;
        }
        return getString(stringID);
    }
}
