package com.outofbound.rhinoengine;


import android.os.Bundle;

import com.outofbound.rhinoenginelib.activity.GLActivity;

public class MainActivity extends GLActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}