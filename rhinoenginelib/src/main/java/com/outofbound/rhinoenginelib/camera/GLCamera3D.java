package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class GLCamera3D extends GLCamera {

    public GLCamera3D(Vector3f eye, Vector3f up, Vector3f center, float near, float far) {
        super(eye, up, center, near, far);
    }

    @Override
    public float[] create(int width, int height, long ms) {
        float ratio = (float)width/(float)height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
        setupM();
        return vpMatrix;
    }
}
