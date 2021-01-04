package com.outofbound.rhinoengine;

import android.content.Context;
import android.util.AttributeSet;

import com.outofbound.rhinoenginelib.camera.GLCamera2D;
import com.outofbound.rhinoenginelib.camera.GLCamera3D;
import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.mesh.primitives.Cube;
import com.outofbound.rhinoenginelib.renderer.GLRenderer3D;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.shader.primitives.BaseShader;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class TestEngine extends GLEngine {

    private static final Vector3f EYE_CENTER = new Vector3f(0,0,0);
    private static final Vector3f EYE_POS = new Vector3f(3,5,4);
    private static final Vector3f EYE_UP = new Vector3f(0,1,0);

    public TestEngine(Context context){
        super(context, new GLCamera3D(EYE_POS.clone(), EYE_UP.clone(), EYE_CENTER.clone(), 1, 1000), new GLCamera2D(1,1000), null);
    }

    public TestEngine(Context context, AttributeSet attrs){
        super(context, attrs, new GLCamera3D(EYE_POS.clone(), EYE_UP.clone(), EYE_CENTER.clone(), 1, 1000), new GLCamera2D(1,1000), null);
    }

    @Override
    protected void init() {
        setClearColor(0,0,0,1);
        int rendererId = addGLRenderer(new GLRenderer3D(new BaseShader()));
        for (int i = 0; i < 1000; i++) {
            getGLRenderer(rendererId).addGLMesh(new TestCube().setMotion(new TestMotion()));
        }
        addGLTask(new TestCameraRotation());
        setBlur(GLRendererOnTexture.RESOLUTION_1024,0.5f,10f,0.1f);
    }

    public void blurOn(){
        enableBlur();
    }

    public void blurOff(){
        disableBlur();
    }
}
