package com.outofbound.rhinoenginelib.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class RhinoEngineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException ignored){}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        /* Invoked when another activity goes to the foreground. Such as during phone call
           or when user switch to multi-window mode.
           When user return to this app the system calls onResume() callback.
         */
        super.onPause();
    }

    @Override
    protected void onStop(){
        /* Invoked when the activity is no longer visible to the user.
           This may occur, for example, when a newly launched activity covers the entire screen.
           When the user return to this app the system calls onStart() callback.
         */
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        /* Is called before the activity is destroyed. */
        super.onDestroy();
    }
}
