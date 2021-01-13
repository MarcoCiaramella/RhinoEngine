package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.Motion;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class CubeMotion extends Motion {

    public CubeMotion(){
        position.x = Numbers.randomFloat(0,5);
        position.y = Numbers.randomFloat(0,5);
        position.z = Numbers.randomFloat(0,5);
        scale.x = 0.1f;
        scale.y = 0.1f;
        scale.z = 0.1f;
    }

    @Override
    public void move(long ms) {
        rotation.y -= 1f;
    }
}
