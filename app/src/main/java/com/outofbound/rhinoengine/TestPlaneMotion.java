package com.outofbound.rhinoengine;

import com.outofbound.rhinoenginelib.mesh.Motion;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class TestPlaneMotion extends Motion {

    public TestPlaneMotion(){
        scale.x = 10f;
        scale.y = 0.1f;
        scale.z = 10f;
    }

    @Override
    public void move(long ms) {
    }
}
