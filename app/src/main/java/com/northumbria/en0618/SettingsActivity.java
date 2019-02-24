package com.northumbria.en0618;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFERENCE_KEY_MUSIC = "play_music";
    public static final String PREFERENCE_KEY_SFX = "play_sfx";
    public static final String PREFERENCE_KEY_POWER_SAVER = "power_saver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set font for view root
        ViewGroup root = findViewById(R.id.settings_root);
        FontUtils.setFont(root, getString(R.string.app_font));

        //setup toggle buttons
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        ToggleButton musicToggle = findViewById(R.id.music_button);
        ToggleButton sfxToggle = findViewById(R.id.sfx_button);
        ToggleButton frameToggle = findViewById(R.id.frame_button);

        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(PREFERENCE_KEY_MUSIC, isChecked).apply();
            }
        });

        sfxToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(PREFERENCE_KEY_SFX, isChecked).apply();
            }
        });

        frameToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(PREFERENCE_KEY_POWER_SAVER, isChecked).apply();
            }
        });

        //set button values to be the correct settings
        Button b = findViewById(R.id.input_button);
        int inputMethod = sp.getInt(Player.PREFERENCE_KEY_INPUT_METHOD, Player.INPUT_METHOD_SCREEN_SIDE);
        b.setText(getInputStringFromPreference(inputMethod));

        boolean isMusicOn = sp.getBoolean(PREFERENCE_KEY_MUSIC, true);
        boolean isSFXOn = sp.getBoolean(PREFERENCE_KEY_SFX, true);
        boolean isFrameLimitOn = sp.getBoolean(PREFERENCE_KEY_POWER_SAVER, false);

        musicToggle.setChecked(isMusicOn);
        sfxToggle.setChecked(isSFXOn);
        frameToggle.setChecked(isFrameLimitOn);
    }

    public void backButtonClick(View view) {
        finish();
    }

    // Updates the app settings and the player with the new input method.
    @SuppressLint("ApplySharedPref")    // We want the data to be available immediately.
    public void changeInputMethod(View view) {
        // Find the current input method.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int inputMethod = preferences.getInt(Player.PREFERENCE_KEY_INPUT_METHOD, Player.INPUT_METHOD_SCREEN_SIDE);
        int newInputMethod = inputMethod;
        Button b = (Button) view;

        // Determine the new input method and update the UI.
        switch (inputMethod) {
            case Player.INPUT_METHOD_SCREEN_SIDE:
                newInputMethod = Player.INPUT_METHOD_PLAYER_SIDE;
                break;
            case Player.INPUT_METHOD_PLAYER_SIDE:
                newInputMethod = Player.INPUT_METHOD_SCREEN_TILT;
                break;
            case Player.INPUT_METHOD_SCREEN_TILT:
                newInputMethod = Player.INPUT_METHOD_SCREEN_SIDE;
                break;
        }

        //update the button text
        b.setText(getInputStringFromPreference(newInputMethod));

        // Update the settings and the player.
        preferences.edit().putInt(Player.PREFERENCE_KEY_INPUT_METHOD, newInputMethod).apply();
        Player.updateInputMethod(this);
    }


    private String getInputStringFromPreference(int inputMethod) {
        int text = R.string.app_name; //default should be obvious error

        switch (inputMethod) {
            case Player.INPUT_METHOD_SCREEN_SIDE:
                text = R.string.input_button_player_side;
                break;
            case Player.INPUT_METHOD_PLAYER_SIDE:
                text = R.string.input_button_screen_tilt;
                break;
            case Player.INPUT_METHOD_SCREEN_TILT:
                text = R.string.input_button_screen_side;
                break;
        }

        return getString(text);
    }
}
