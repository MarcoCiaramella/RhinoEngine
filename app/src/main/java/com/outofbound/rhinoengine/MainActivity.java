package com.outofbound.rhinoengine;


import android.os.Bundle;
import android.widget.TextView;

import com.outofbound.rhinoenginelib.activity.GLActivity;
import com.outofbound.rhinoenginelib.engine.GLEngine;

public class MainActivity extends GLActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFPS().post(new Runnable() {
            @Override
            public void run() {
                getFPS().setText(getString(R.string.fps, GLEngine.getInstance().getFPS()));
                getFPS().postDelayed(this,100);
            }
        });
    }

    private TextView getFPS(){
        return findViewById(R.id.fps);
    }
}