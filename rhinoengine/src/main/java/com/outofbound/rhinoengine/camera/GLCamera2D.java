package com.outofbound.rhinoengine.camera;


import android.opengl.Matrix;

import com.outofbound.rhinoengine.util.vector.Vector3f;


public class GLCamera2D extends GLCamera {

    private final float near;
    private final float far;

    public GLCamera2D(float near, float far){
        super(new Vector3f(0,0,1),new Vector3f(0,-1,0),new Vector3f(0,0,0));
        this.near = near;
        this.far = far;
    }

    public GLCamera2D(){
        this(1,100);
    }

    @Override
    public float[] create(int width, int height, long ms) {
        float ratio = (float)width/(float)height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -ratio, ratio, near, far);
        setupM();
        return mvpMatrix;
    }
}
