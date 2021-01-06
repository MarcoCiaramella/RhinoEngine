package com.outofbound.rhinoenginelib.renderer;

import com.outofbound.rhinoenginelib.camera.GLCamera2D;
import com.outofbound.rhinoenginelib.camera.GLCamera3D;
import com.outofbound.rhinoenginelib.shader.GLShader;

public class GLRenderer3D extends GLRenderer {

    /**
     * The renderer constructor.
     *
     * @param glShader the shader to render.
     */
    public GLRenderer3D(GLShader glShader) {
        super(glShader);
    }

    @Override
    public void render(int screenWidth, int screenHeight, GLCamera3D glCamera3D, GLCamera2D glCamera2D, long ms) {
        render(screenWidth,screenHeight,glCamera3D,ms);
    }
}
