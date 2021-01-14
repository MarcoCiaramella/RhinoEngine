package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.Motion;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class PlaneMotion extends Motion {

    public PlaneMotion(){
        scale = 10f;
    }

    @Override
    public void move(long ms) {
    }
}
