package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.camera.GLCameraOrthographic;
import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class GLShadowMap {

    private final GLRendererOnTexture shadowMapRenderer;
    private final GLCameraOrthographic shadowMapCamera;

    public GLShadowMap(int resolution, GLLights glLights, GLCamera glCamera){
        shadowMapRenderer = new GLRendererOnTexture(resolution);
        Vector3f lightPos = new Vector3f(0,0,0);
        glLights.getGLDirLight().getDirection().multS(100,lightPos);
        lightPos.x = -lightPos.x;
        lightPos.y = -lightPos.y;
        lightPos.z = -lightPos.z;
        shadowMapCamera = new GLCameraOrthographic(lightPos, glCamera.getUp(), glCamera.getCenter(), glCamera.getNear(), glCamera.getFar(), 10);
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
