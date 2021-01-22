package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.CameraOrthographic;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.SceneRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class ShadowMap {

    private final RendererOnTexture renderer;
    private final CameraOrthographic camera;
    private int texture;

    public ShadowMap(int resolution, Vector3f lightPosition, float near, float far, float clippingCubeSize){
        renderer = new RendererOnTexture(resolution);
        camera = new CameraOrthographic(lightPosition, new Vector3f(0,1,0), new Vector3f(0,0,0), near, far, clippingCubeSize);
    }

    public CameraOrthographic getCamera(){
        return camera;
    }

    public void render(SceneRenderer sceneRenderer, int screenWidth, int screenHeight){
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        texture = renderer.render(sceneRenderer);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
    }

    public int getTexture(){
        return texture;
    }
}
