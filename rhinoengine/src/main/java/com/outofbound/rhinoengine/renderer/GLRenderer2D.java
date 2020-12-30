package com.outofbound.rhinoengine.renderer;

import com.outofbound.rhinoengine.mesh.GLMesh;
import com.outofbound.rhinoengine.shader.GLShader;

public class GLRenderer2D extends GLRenderer {

    /**
     * The renderer constructor.
     *
     * @param id the id.
     * @param glMesh   the mesh to render.
     * @param glShader the shader to render.
     */
    public GLRenderer2D(int id, GLMesh glMesh, GLShader glShader) {
        super(id, glMesh, glShader);
    }

    @Override
    public void render(float[] m3D, float[] m2D, long ms) {
        render(m2D,ms);
    }
}
