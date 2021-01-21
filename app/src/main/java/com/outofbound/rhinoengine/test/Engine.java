package com.outofbound.rhinoengine.test;

import android.content.Context;
import android.util.AttributeSet;

import com.outofbound.rhinoenginelib.camera.CameraPerspective;
import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.light.PointLight;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Engine extends AbstractEngine {

    private static final Vector3f CAMERA_EYE = new Vector3f(3,2,3);
    private static final Vector3f CAMERA_CENTER = new Vector3f(0,0,0);
    private static final Vector3f CAMERA_UP = new Vector3f(0,1,0);

    public Engine(Context context){
        super(context, new CameraPerspective(CAMERA_EYE, CAMERA_UP, CAMERA_CENTER, 1, 1000), null);
    }

    public Engine(Context context, AttributeSet attrs){
        super(context, attrs, new CameraPerspective(CAMERA_EYE, CAMERA_UP, CAMERA_CENTER, 1, 1000), null);
    }

    @Override
    protected void init() {
        setClearColor(0,0,0,1);
        ID.GLRENDERER_0 = addRenderer(new com.outofbound.rhinoenginelib.renderer.Renderer());
        for (int i = 0; i < 100; i++) {
            getRenderer(ID.GLRENDERER_0).addMesh(new Cube());
        }
        getRenderer(ID.GLRENDERER_0).addMesh(new Pane());
        addTask(new CameraRotation());
        //addGLTask(new LightRotation());
        configBlur(RendererOnTexture.RESOLUTION_1024,0.5f,10f,0.1f,1,1000);
        PointLight pointLight = new PointLight(
                new Vector3f(0,5,0),
                new Vector3f(0.5f,0.5f,0.5f),
                new Vector3f(0.7f,0.7f,0.7f),
                new Vector3f(1,1,1),
                1.0f,
                0.22f,
                0.2f
        ).configShadow(RendererOnTexture.RESOLUTION_4096,1,1000,10);
        pointLight.enableShadow();
        getRenderer(ID.GLRENDERER_0).getLights().addPointLight(pointLight);
        getRenderer(ID.GLRENDERER_0)
                .getLights()
                .getDirLight()
                .configShadow(RendererOnTexture.RESOLUTION_4096,100,1,1000,10)
                .enableShadow();
    }

    public void blurOn(){
        enableBlur();
    }

    public void blurOff(){
        disableBlur();
    }
}
