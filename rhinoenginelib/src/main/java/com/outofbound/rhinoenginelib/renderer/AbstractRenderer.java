package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.mesh.Mesh;

public abstract class AbstractRenderer {

    public abstract void doRendering(int screenWidth, int screenHeight, Camera camera, long ms);

    protected void draw(Mesh mesh){
        if (mesh.getIndices() != null) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, mesh.getIndicesBuffer());
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mesh.getNumVertices());
        }
    }
}
