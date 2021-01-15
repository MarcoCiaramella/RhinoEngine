package com.outofbound.rhinoengine.test;

import android.content.Context;
import android.util.AttributeSet;

import com.outofbound.rhinoenginelib.camera.GLCameraPerspective;
import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.renderer.GLRenderer;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Engine extends GLEngine {

    private static final Vector3f CAMERA_EYE = new Vector3f(3,2,3);
    private static final Vector3f CAMERA_CENTER = new Vector3f(0,0,0);
    private static final Vector3f CAMERA_UP = new Vector3f(0,1,0);

    public Engine(Context context){
        super(context, new GLCameraPerspective(CAMERA_EYE, CAMERA_UP, CAMERA_CENTER, 1, 1000), null);
    }

    public Engine(Context context, AttributeSet attrs){
        super(context, attrs, new GLCameraPerspective(CAMERA_EYE, CAMERA_UP, CAMERA_CENTER, 1, 1000), null);
    }

    @Override
    protected void init() {
        setClearColor(0,0,0,1);
        ID.GLRENDERER_0 = addGLRenderer(new GLRenderer());
        for (int i = 0; i < 100; i++) {
            getGLRenderer(ID.GLRENDERER_0).addGLMesh(new Cube().setMotion(new CubeMotion()));
        }
        getGLRenderer(ID.GLRENDERER_0).addGLMesh(new Pane().setMotion(new PaneMotion()));
        addGLTask(new CameraRotation());
        addGLTask(new LightRotation());
        configBlur(GLRendererOnTexture.RESOLUTION_1024,0.5f,10f,0.1f,1,1000);
        getGLRenderer(ID.GLRENDERER_0).configShadow(GLRendererOnTexture.RESOLUTION_4096,getGLCamera()).enableShadow();
    }

    public void blurOn(){
        enableBlur();
    }

    public void blurOff(){
        disableBlur();
    }
}
