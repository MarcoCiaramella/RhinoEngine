package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.physics.Gravity;
import com.outofbound.rhinoenginelib.util.color.Color;
import com.outofbound.rhinoenginelib.util.number.Numbers;

public class CubeWithGravity extends com.outofbound.rhinoenginelib.mesh.primitives.Cube {

    private final Gravity gravity;
    private boolean collisionDetected = false;

    public CubeWithGravity(String name) {
        super(name, 2,2,2,Color.randomColor(0.2f,1.0f,1.0f));
        position.x = Numbers.randomFloat(-2,2);
        position.y = Numbers.randomFloat(2,6);
        position.z = Numbers.randomFloat(-2,2);
        scale = 0.1f;
        gravity = new Gravity(-1, position.y);
    }

    @Override
    public void beforeRendering(long ms) {
        if (!collisionDetected) {
            //rotation.y -= 1f;
            position.y = gravity.calc(ms);
            translate();
        }
        rotateY();
        scale();
    }

    @Override
    public void afterRendering(long ms) {
        collisionDetected = isColliding(Engine.getInstance().getMesh("Pane"));
    }
}
