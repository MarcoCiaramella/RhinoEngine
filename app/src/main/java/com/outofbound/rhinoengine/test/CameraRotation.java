package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.task.Task;

public class CameraRotation extends Task {

    private static final float SPEED = 0.01f;

    @Override
    public boolean runTask(long ms) {
        float angle = SPEED*ms;
        AbstractEngine.getInstance().getCamera().rotate(angle,0,1,0);
        return true;
    }
}
