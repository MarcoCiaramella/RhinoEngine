package com.outofbound.rhinoengine;

import com.outofbound.rhinoenginelib.mesh.primitives.Cube;

public class TestCube extends Cube {

    public TestCube() {
        super(new float[]{1,0,0,1});
    }

    @Override
    public void doTransformation(float[] mMatrix) {
        translate(mMatrix);
        scale(mMatrix);
    }
}
