package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.task.Task;

public class LightAnimation extends Task {

    private static final float SPEED = 0.1f;

    /**
     * Instantiate a Task.
     *
     * @param name a name for this Task
     */
    public LightAnimation(String name) {
        super(name);
    }

    @Override
    public boolean runTask(long ms) {
        float angle = SPEED*ms;
        AbstractEngine.getInstance().getLights().getPointLight().getPosition().rotate(angle,0,1,0);
        return true;
    }
}
