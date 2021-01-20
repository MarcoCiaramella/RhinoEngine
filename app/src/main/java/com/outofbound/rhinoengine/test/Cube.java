package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.util.color.Color;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class Cube extends com.outofbound.rhinoenginelib.mesh.primitives.Cube {

    public Cube() {
        super(Color.randomColor(0.2f,1.0f,1.0f));
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
