package com.outofbound.rhinoengine;

import com.outofbound.rhinoenginelib.mesh.Motion;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class TestMotion extends Motion {

    public TestMotion(){
        position.x = Numbers.randomFloat(-3,3);
        position.y = Numbers.randomFloat(-3,3);
        position.z = Numbers.randomFloat(-3,3);
        scale.x = 0.1f;
        scale.y = 0.1f;
        scale.z = 0.1f;
    }

    @Override
    public void move(long ms) {
    }
}
