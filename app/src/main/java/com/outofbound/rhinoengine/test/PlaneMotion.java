package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.Motion;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class PlaneMotion extends Motion {

    public PlaneMotion(){
        scale.x = 10f;
        scale.y = 0.1f;
        scale.z = 10f;
    }

    @Override
    public void move(long ms) {
    }
}
