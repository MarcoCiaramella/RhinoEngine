package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.task.Task;

public class LightAnimation extends Task {

    private static final float SPEED = 0.1f;

    @Override
    public boolean runTask(long ms) {
        float angle = SPEED*ms;
        AbstractEngine.getInstance().getRenderer(ID.GLRENDERER_1).getLights().getPointLights().get(0).getPosition().rotate(angle,0,1,0);
        return true;
    }
}
