package com.outofbound.rhinoengine.test;

public class Pane extends com.outofbound.rhinoenginelib.mesh.primitives.Pane {

    public Pane() {
        super(new float[]{1,0,0,1});
    }

    @Override
    public void doTransformation(float[] mMatrix) {
        translate(mMatrix);
        rotateY(mMatrix);
        scale(mMatrix);
    }
}
