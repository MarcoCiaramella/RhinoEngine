package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.Motion;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class CubeMotion extends Motion {

    public CubeMotion(){
        position.x = Numbers.randomFloat(-2,2);
        position.y = Numbers.randomFloat(-2,2);
        position.z = Numbers.randomFloat(-2,2);
        scale = 0.1f;
    }

    @Override
    public void move(long ms) {
        rotation.y -= 1f;
    }
}
