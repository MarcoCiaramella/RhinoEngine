package com.outofbound.rhinoengine.test;

public class Cube extends com.outofbound.rhinoenginelib.mesh.primitives.Cube {

    public Cube() {
        super(new float[]{1,0,0,1});
    }

    @Override
    public void doTransformation(float[] mMatrix) {
        translate(mMatrix);
        rotateY(mMatrix);
        scale(mMatrix);
    }
}
