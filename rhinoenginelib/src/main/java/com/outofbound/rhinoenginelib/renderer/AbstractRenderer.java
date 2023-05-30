package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.mesh.Mesh;

public abstract class AbstractRenderer {

    public abstract void doRendering(int screenWidth, int screenHeight, Camera camera, long ms);

    protected void draw(Mesh.ShaderData shaderData){
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, shaderData.indicesBuffer.capacity(), GLES20.GL_UNSIGNED_INT, shaderData.indicesBuffer);
    }
}
