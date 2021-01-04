package com.outofbound.rhinoenginelib.renderer;

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
    public void render(float[] m3D, float[] m2D, long ms) {
        render(m3D,ms);
    }
}
