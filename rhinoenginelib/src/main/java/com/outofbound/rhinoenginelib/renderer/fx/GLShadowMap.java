package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.GLCamera3D;
import com.outofbound.rhinoenginelib.light.GLLight;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class GLShadowMap {

    private final GLRendererOnTexture shadowMapRenderer;
    private final GLCamera3D shadowMapCamera;
    private int screenWidth;
    private int screenHeight;

    public GLShadowMap(int resolution, GLLight light, float near, float far){
        shadowMapRenderer = new GLRendererOnTexture(resolution);
        Vector3f cameraEye = light.getPosition().clone();
        /*Vector3f cameraCenter = cameraEye.clone();
        cameraCenter.y = -cameraCenter.y;
        Vector3f cameraUp = cameraEye.clone();
        cameraUp.x = -cameraUp.x;
        cameraUp.y = 0;
        cameraUp.z = -cameraUp.z;*/
        Vector3f cameraCenter = new Vector3f(0,0,0);
        Vector3f cameraUp = new Vector3f(0,1,0);
        shadowMapCamera = new GLCamera3D(cameraEye, cameraUp, cameraCenter, near, far);
    }

    public float[] getVpMatrix(long ms){
        return shadowMapCamera.create(screenWidth,screenHeight,ms);
    }

    public int render(GLSceneRenderer glSceneRenderer){
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        int texture = shadowMapRenderer.render(glSceneRenderer);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        return texture;
    }

    public GLShadowMap setScreenWidth(int screenWidth){
        this.screenWidth = screenWidth;
        return this;
    }

    public GLShadowMap setScreenHeight(int screenHeight){
        this.screenHeight = screenHeight;
        return this;
    }
}
