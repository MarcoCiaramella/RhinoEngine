package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.Motion;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class CubeMotion extends Motion {

    public CubeMotion(){
        position.x = Numbers.randomFloat(0,3);
        position.y = 1;
        position.z = Numbers.randomFloat(0,3);
        scale.x = 0.5f;
        scale.y = 0.5f;
        scale.z = 0.5f;
    }

    @Override
    public void move(long ms) {
    }
}
