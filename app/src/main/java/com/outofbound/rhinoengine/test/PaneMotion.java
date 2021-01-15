package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.Motion;

public class PaneMotion extends Motion {

    public PaneMotion(){
        position.y = -2;
        scale = 10f;
    }

    @Override
    public void move(long ms) {
    }
}
