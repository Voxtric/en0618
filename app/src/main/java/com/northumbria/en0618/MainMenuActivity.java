package com.northumbria.en0618;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.northumbria.en0618.engine.GameActivity;

public class MainMenuActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void startGame(View view)
    {
        Intent intent = new Intent(this, SpaceInvadersActivity.class);
        startActivity(intent);
    }
}
