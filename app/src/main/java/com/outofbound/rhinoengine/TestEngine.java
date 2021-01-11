package com.outofbound.rhinoengine;

import android.content.Context;
import android.util.AttributeSet;

import com.outofbound.rhinoenginelib.camera.GLCamera2D;
import com.outofbound.rhinoenginelib.camera.GLCamera3D;
import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.renderer.GLRenderer3D;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class TestEngine extends GLEngine {

    private static final Vector3f CAMERA_EYE = new Vector3f(3,5,4);
    private static final Vector3f CAMERA_CENTER = new Vector3f(0,0,0);
    private static final Vector3f CAMERA_UP = new Vector3f(0,1,0);

    public TestEngine(Context context){
        super(context, new GLCamera3D(CAMERA_EYE.clone(), CAMERA_UP.clone(), CAMERA_CENTER.clone(), 1, 1000), new GLCamera2D(1,1000), null);
    }

    public TestEngine(Context context, AttributeSet attrs){
        super(context, attrs, new GLCamera3D(CAMERA_EYE.clone(), CAMERA_UP.clone(), CAMERA_CENTER.clone(), 1, 1000), new GLCamera2D(1,1000), null);
    }

    @Override
    protected void init() {
        setClearColor(0,0,0,1);
        int rendererId = addGLRenderer(new GLRenderer3D());
        for (int i = 0; i < 100; i++) {
            getGLRenderer(rendererId).addGLMesh(new TestCube().setMotion(new TestMotion()));
        }
        addGLTask(new TestCameraRotation());
        configBlur(GLRendererOnTexture.RESOLUTION_1024,0.5f,10f,0.1f,1,1000);
        getGLRenderer(rendererId).configShadow(GLRendererOnTexture.RESOLUTION_1024,1,1000).enableShadow();
    }

    public void blurOn(){
        enableBlur();
    }

    public void blurOff(){
        disableBlur();
    }
}
