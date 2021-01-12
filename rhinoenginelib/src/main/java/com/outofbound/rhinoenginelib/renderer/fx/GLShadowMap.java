package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.camera.GLCamera3D;
import com.outofbound.rhinoenginelib.light.GLLight;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class GLShadowMap {

    private final GLRendererOnTexture shadowMapRenderer;
    private final GLCamera3D shadowMapCamera;

    public GLShadowMap(int resolution, GLLight light, GLCamera glCamera){
        shadowMapRenderer = new GLRendererOnTexture(resolution);
        shadowMapCamera = new GLCamera3D(light.getPosition(), glCamera.getUp(), glCamera.getCenter(), glCamera.getNear(), glCamera.getFar());
    }

    public GLCamera3D getShadowMapCamera(){
        return shadowMapCamera;
    }

    public int render(GLSceneRenderer glSceneRenderer, int screenWidth, int screenHeight){
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        int texture = shadowMapRenderer.render(glSceneRenderer);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        return texture;
    }
}
