package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.task.GLTask;

public class LightRotation extends GLTask {

    private static final float SPEED = 0.1f;

    @Override
    public boolean runTask(long ms) {
        float angle = SPEED*ms;
        GLEngine.getInstance().getGLRenderer(ID.GLRENDERER_0).getGLLights().getGLPointLights().get(0).getPosition().rotate(angle,0,1,0);
        return true;
    }
}
