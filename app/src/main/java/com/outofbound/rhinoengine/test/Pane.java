package com.outofbound.rhinoengine.test;

public class Pane extends com.outofbound.rhinoenginelib.mesh.primitives.Pane {

    public Pane() {
        super("Pane", 2,2,new float[]{1,0,0,1});
        position.y = -2;
        scale = 10f;
    }

    @Override
    public void beforeRendering(long ms) {
        translate();
        rotateY();
        scale();
    }
}
