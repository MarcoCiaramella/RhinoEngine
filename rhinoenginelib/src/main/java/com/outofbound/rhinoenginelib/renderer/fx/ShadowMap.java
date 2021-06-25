package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES30;

import com.outofbound.rhinoenginelib.camera.CameraOrthographic;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.SceneRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class ShadowMap {

    private final RendererOnTexture renderer;
    private int texture;
    private final CameraOrthographic camera;

    public ShadowMap(int resolution, Vector3f lightPosition, float near, float far, float clippingCubeSize){
        camera = new CameraOrthographic(lightPosition, new Vector3f(0,0,0), new Vector3f(0,1,0), near, far, clippingCubeSize);
        renderer = new RendererOnTexture(resolution,camera);
    }

    public void render(SceneRenderer sceneRenderer, int screenWidth, int screenHeight){
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glCullFace(GLES30.GL_FRONT);
        camera.loadVpMatrix();
        texture = renderer.render(sceneRenderer);
        GLES30.glDisable(GLES30.GL_CULL_FACE);
        GLES30.glViewport(0, 0, screenWidth, screenHeight);
    }

    public int getTexture(){
        return texture;
    }

    public CameraOrthographic getCamera() {
        return camera;
    }
}
