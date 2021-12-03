package com.outofbound.rhinoengine.test;

import android.content.Context;
import android.util.AttributeSet;

import com.outofbound.rhinoenginelib.camera.CameraPerspective;
import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.light.PointLight;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.SceneRenderer;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Engine extends AbstractEngine {

    private static final Vector3f CAMERA_EYE = new Vector3f(3,2,3);
    private static final Vector3f CAMERA_CENTER = new Vector3f(0,0,0);
    private static final Vector3f CAMERA_UP = new Vector3f(0,1,0);

    public Engine(Context context){
        super(context, new CameraPerspective(CAMERA_EYE, CAMERA_CENTER, CAMERA_UP, 1, 1000), null);
    }

    public Engine(Context context, AttributeSet attrs){
        super(context, attrs, new CameraPerspective(CAMERA_EYE, CAMERA_CENTER, CAMERA_UP, 1, 1000), null);
    }

    @Override
    protected void init() {
        setClearColor(0,0,0,1);
        addTask(new CameraRotation("CameraRotation"));
        addTask(new LightAnimation("LightAnimation"));
        PointLight pointLight = new PointLight(
                new Vector3f(3,2,0),
                1.0f,
                0.22f,
                0.2f
        );
        pointLight.configShadow(1,1000,10);
        getLights().setPointLight(pointLight);
        getLights().getDirLight().configShadow(1,1000,10);

        addMesh(new Pane());
        addMesh(new MeshObj());
        Cube cube = new Cube();
        addMesh(cube);
        addMesh(new Trail("trail", cube, new float[]{1,1,1,1}, 100, 100));
    }

    public void blurOn(){
        enableBlur();
    }

    public void blurOff(){
        disableBlur();
    }

    public void addCubes(){
        for (int i = 0; i < 3; i++) {
            addMesh(new CubeWithGravity("Cube"+i));
        }
    }

    public void removeCubes(){
        for (int i = 0; i < 3; i++) {
            removeMesh("Cube"+i);
        }
    }

    public void shadowsOn(){
        getLights().getDirLight().enableShadow();
        getLights().getPointLight().enableShadow();
    }

    public void shadowsOff(){
        getLights().getDirLight().disableShadow();
        getLights().getPointLight().disableShadow();
    }
}
