package com.outofbound.rhinoengine.test;

import android.content.Context;
import android.util.AttributeSet;

import com.outofbound.rhinoenginelib.camera.CameraPerspective;
import com.outofbound.rhinoenginelib.collision.Collider;
import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.gesture.RotateScaleGesture;
import com.outofbound.rhinoenginelib.light.PointLight;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.renderer.RenderingResolution;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Engine extends AbstractEngine {

    private static final Vector3f CAMERA_EYE = new Vector3f(3,2,3);
    private static final int NUM_CUBES = 10;

    public Engine(Context context){
        super(context, CAMERA_EYE, new RotateScaleGesture(), RenderingResolution.RESOLUTION_256);
    }

    public Engine(Context context, AttributeSet attrs){
        super(context, attrs, CAMERA_EYE, new RotateScaleGesture(), RenderingResolution.RESOLUTION_256);
    }

    @Override
    protected void init() {
        setClearColor(0,0,0,1);
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

        //Terrain terrain = new Terrain("plane_heavy.ply");
        //addMesh(terrain);
        //addMesh(new MeshObj());
        addMesh(new Mesh("car", "car1.obj"));
        Cube cube = new Cube();
        addMesh(cube);

        Collider.build(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1), 5, 16);
    }

    public void blurOn(){
        enableBlur();
    }

    public void blurOff(){
        disableBlur();
    }

    public void addCubes(){
        for (int i = 0; i < NUM_CUBES; i++) {
            addMesh(new CubeWithGravity("Cube"+i));
        }
    }

    public void removeCubes(){
        for (int i = 0; i < NUM_CUBES; i++) {
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
