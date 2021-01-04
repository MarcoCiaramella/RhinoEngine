package com.outofbound.rhinoengine;

import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.task.GLTask;

public class TestCameraRotation extends GLTask {

    private static final float SPEED = 0.01f;

    @Override
    public boolean runTask(long ms) {
        float angle = SPEED*ms;
        GLEngine.getInstance().getCamera3D().rotate(angle,0,1,0);
        return true;
    }
}
