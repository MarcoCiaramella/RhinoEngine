package com.outofbound.rhinoengine.test;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.outofbound.rhinoengine.R;
import com.outofbound.rhinoenginelib.activity.RhinoEngineActivity;
import com.outofbound.rhinoenginelib.engine.AbstractEngine;

public class MainActivity extends RhinoEngineActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFPS().post(new Runnable() {
            @Override
            public void run() {
                getFPS().setText(getString(R.string.fps, AbstractEngine.getInstance().getFPS()));
                getFPS().postDelayed(this,100);
            }
        });

        getBlur().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getEngine().blurOn();
            } else {
                getEngine().blurOff();
            }
        });

        getCubes().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getEngine().addCubes();
            } else {
                getEngine().removeCubes();
            }
        });

        getShadows().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getEngine().shadowsOn();
            } else {
                getEngine().shadowsOff();
            }
        });

        getDebug().post(new Runnable() {
            @Override
            public void run() {
                getDebug().setText(Debug.text);
                getDebug().postDelayed(this,100);
            }
        });
    }

    private Engine getEngine(){
        return findViewById(R.id.engine);
    }

    private TextView getFPS(){
        return findViewById(R.id.fps);
    }

    private SwitchCompat getBlur(){
        return findViewById(R.id.blur);
    }

    private SwitchCompat getCubes(){
        return findViewById(R.id.cubes);
    }

    private SwitchCompat getShadows(){
        return findViewById(R.id.shadows);
    }

    private TextView getDebug(){
        return findViewById(R.id.debug);
    }
}