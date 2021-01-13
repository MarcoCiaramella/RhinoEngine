package com.outofbound.rhinoengine.test;

import android.content.Context;
import android.util.AttributeSet;

import com.outofbound.rhinoenginelib.camera.GLCameraPerspective;
import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.renderer.GLRenderer;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Engine extends GLEngine {

    private static final Vector3f CAMERA_EYE = new Vector3f(3,5,4);
    private static final Vector3f CAMERA_CENTER = new Vector3f(0,0,0);
    private static final Vector3f CAMERA_UP = new Vector3f(0,1,0);

    public Engine(Context context){
        super(context, new GLCameraPerspective(CAMERA_EYE.clone(), CAMERA_UP.clone(), CAMERA_CENTER.clone(), 1, 1000), null);
    }

    public Engine(Context context, AttributeSet attrs){
        super(context, attrs, new GLCameraPerspective(CAMERA_EYE.clone(), CAMERA_UP.clone(), CAMERA_CENTER.clone(), 1, 1000), null);
    }

    @Override
    protected void init() {
        setClearColor(0,0,0,1);
        ID.GLRENDERER_ID = addGLRenderer(new GLRenderer());
        for (int i = 0; i < 100; i++) {
            getGLRenderer(ID.GLRENDERER_ID).addGLMesh(new Cube().setMotion(new CubeMotion()));
        }
        getGLRenderer(ID.GLRENDERER_ID).addGLMesh(new Cube().setMotion(new PlaneMotion()));
        addGLTask(new CameraRotation());
        //addGLTask(new LightRotation());
        configBlur(GLRendererOnTexture.RESOLUTION_1024,0.5f,10f,0.1f,1,1000);
        getGLRenderer(ID.GLRENDERER_ID).configShadow(GLRendererOnTexture.RESOLUTION_4096,getGLCamera()).enableShadow();
    }

    public void blurOn(){
        enableBlur();
    }

    public void blurOff(){
        disableBlur();
    }
}
