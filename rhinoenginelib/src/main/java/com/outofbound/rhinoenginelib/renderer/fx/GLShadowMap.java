package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.GLCameraOrthographic;
import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class GLShadowMap {

    private final GLRendererOnTexture shadowMapRenderer;
    private final GLCameraOrthographic shadowMapCamera;
    private int texture;

    public GLShadowMap(int resolution, Vector3f lightPosition, float near, float far, float clippingCubeSize){
        shadowMapRenderer = new GLRendererOnTexture(resolution);
        shadowMapCamera = new GLCameraOrthographic(lightPosition, new Vector3f(0,1,0), new Vector3f(0,0,0), near, far, clippingCubeSize);
    }

    public GLCameraOrthographic getShadowMapCamera(){
        return shadowMapCamera;
    }

    public void render(GLSceneRenderer glSceneRenderer, int screenWidth, int screenHeight){
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        texture = shadowMapRenderer.render(glSceneRenderer);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
    }

    public int getTexture(){
        return texture;
    }
}
