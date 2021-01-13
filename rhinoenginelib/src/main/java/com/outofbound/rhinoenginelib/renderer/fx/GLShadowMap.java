package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.camera.GLCameraOrthographic;
import com.outofbound.rhinoenginelib.light.GLLight;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;

public class GLShadowMap {

    private final GLRendererOnTexture shadowMapRenderer;
    private final GLCameraOrthographic shadowMapCamera;

    public GLShadowMap(int resolution, GLLight light, GLCamera glCamera){
        shadowMapRenderer = new GLRendererOnTexture(resolution);
        shadowMapCamera = new GLCameraOrthographic(light.getPosition(), glCamera.getUp(), glCamera.getCenter(), glCamera.getNear(), glCamera.getFar(), 10);
    }

    public GLCameraOrthographic getShadowMapCamera(){
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
