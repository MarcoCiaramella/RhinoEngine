package com.outofbound.rhinoenginelib.light;


import com.outofbound.rhinoenginelib.camera.CameraOrthographic;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.ShadowMapRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class ShadowMap {

    private int texture;
    private final CameraOrthographic camera;
    private final RendererOnTexture rendererOnTexture;

    public ShadowMap(Vector3f lightPosition, float near, float far, float clippingCubeSize){
        camera = new CameraOrthographic(lightPosition, new Vector3f(0,0,0), new Vector3f(0,1,0), near, far, clippingCubeSize);
        rendererOnTexture = new RendererOnTexture(RendererOnTexture.RESOLUTION_1024);
    }

    public void render(ShadowMapRenderer shadowMapRenderer, long ms){
        texture = rendererOnTexture.render(shadowMapRenderer, camera, ms);
    }

    public int getTexture(){
        return texture;
    }

    public CameraOrthographic getCamera() {
        return camera;
    }
}
