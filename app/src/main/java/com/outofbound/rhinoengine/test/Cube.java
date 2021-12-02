package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.util.color.Color;

public class Cube extends com.outofbound.rhinoenginelib.mesh.primitives.Cube {

    public Cube() {
        super("Cube", 2,2,2,Color.randomColor(0.2f,1.0f,1.0f));
        scale = 0.1f;
    }

    @Override
    public void doTransformation(long ms) {
        position.z += 0.01f;
        translate();
        rotateY();
        scale();
    }
}
