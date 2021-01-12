package com.outofbound.rhinoenginelib.renderer;

import com.outofbound.rhinoenginelib.camera.GLCameraOrthographic;
import com.outofbound.rhinoenginelib.camera.GLCameraPerspective;

public class GLRenderer2D extends GLRenderer {

    @Override
    public void render(int screenWidth, int screenHeight, GLCameraPerspective glCameraPerspective, GLCameraOrthographic glCameraOrthographic, long ms) {
        render(screenWidth,screenHeight, glCameraOrthographic,ms);
    }
}
