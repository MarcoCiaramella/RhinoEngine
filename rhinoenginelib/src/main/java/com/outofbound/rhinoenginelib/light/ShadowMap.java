package com.outofbound.rhinoenginelib.light;


import com.outofbound.rhinoenginelib.camera.CameraOrthographic;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.ShadowMapRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class ShadowMap {

    private int texture;
    private final CameraOrthographic camera;

    public ShadowMap(Vector3f lightPosition, float near, float far, float clippingCubeSize){
        camera = new CameraOrthographic(lightPosition, new Vector3f(0,0,0), new Vector3f(0,1,0), near, far, clippingCubeSize);
    }

    public void render(ShadowMapRenderer shadowMapRenderer, int screenWidth, int screenHeight, long ms){
        texture = RendererOnTexture.render(shadowMapRenderer, screenWidth, screenHeight, camera, ms);
    }

    public int getTexture(){
        return texture;
    }

    public CameraOrthographic getCamera() {
        return camera;
    }
}
