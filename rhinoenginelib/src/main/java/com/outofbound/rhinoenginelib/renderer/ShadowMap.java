package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.camera.CameraOrthographic;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.AbstractRenderer;
import com.outofbound.rhinoenginelib.renderer.ShadowMapRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class ShadowMap {

    private int texture;
    private final CameraOrthographic camera;

    public ShadowMap(Vector3f lightPosition, float near, float far, float clippingCubeSize){
        camera = new CameraOrthographic(lightPosition, new Vector3f(0,0,0), new Vector3f(0,1,0), near, far, clippingCubeSize);
    }

    public void render(ShadowMapRenderer shadowMapRenderer, int screenWidth, int screenHeight, long ms){
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        camera.loadVpMatrix();
        texture = RendererOnTexture.render(shadowMapRenderer, screenWidth, screenHeight, camera, ms);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    public int getTexture(){
        return texture;
    }

    public CameraOrthographic getCamera() {
        return camera;
    }
}
