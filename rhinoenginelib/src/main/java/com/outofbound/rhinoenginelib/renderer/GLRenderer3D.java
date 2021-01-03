package com.outofbound.rhinoenginelib.renderer;

import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;

public class GLRenderer3D extends GLRenderer {

    /**
     * The renderer constructor.
     *
     * @param id the id.
     * @param glMesh   the mesh to render.
     * @param glShader the shader to render.
     */
    public GLRenderer3D(int id, GLMesh glMesh, GLShader glShader) {
        super(id, glMesh, glShader);
    }

    @Override
    public void render(float[] m3D, float[] m2D, long ms) {
        render(m3D,ms);
    }
}