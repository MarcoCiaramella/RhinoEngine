package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.util.number.Numbers;

public class Cube extends com.outofbound.rhinoenginelib.mesh.primitives.Cube {

    public Cube() {
        super(new float[]{1,0,0,1});
        position.x = Numbers.randomFloat(-2,2);
        position.y = Numbers.randomFloat(-2,2);
        position.z = Numbers.randomFloat(-2,2);
        scale = 0.1f;
    }

    @Override
    public void doTransformation(float[] mMatrix, long ms) {
        //rotation.y -= 1f;
        translate(mMatrix);
        rotateY(mMatrix);
        scale(mMatrix);
    }
}
