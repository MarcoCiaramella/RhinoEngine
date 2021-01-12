package com.outofbound.rhinoenginelib.camera;


import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class GLCamera2D extends GLCamera {

    public GLCamera2D(float near, float far){
        this(new Vector3f(0,0,1),new Vector3f(0,-1,0),new Vector3f(0,0,0),near,far);
    }

    public GLCamera2D(Vector3f eye, Vector3f up, Vector3f center, float near, float far){
        super(eye, up, center, near, far);
    }

    @Override
    public void loadVpMatrix(int width, int height) {
        float ratio = (float)width/(float)height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -ratio, ratio, near, far);
        createVpMatrix();
    }
}
