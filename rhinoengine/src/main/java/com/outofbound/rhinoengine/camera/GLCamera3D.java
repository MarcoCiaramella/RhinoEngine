package com.outofbound.rhinoengine.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoengine.util.vector.Vector3f;


public class GLCamera3D extends GLCamera {

    private final float near;
    private final float far;

    public GLCamera3D(Vector3f eyePos, Vector3f eyeUp, Vector3f eyeCenter, float near, float far) {
        super(eyePos, eyeUp, eyeCenter);
        this.near = near;
        this.far = far;
    }

    public GLCamera3D(Vector3f eyePos, Vector3f eyeUp, Vector3f eyeCenter) {
        this(eyePos, eyeUp, eyeCenter, 1, 100);
    }

    @Override
    public float[] create(int width, int height, long ms) {
        float ratio = (float)width/(float)height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
        setupM();
        return mvpMatrix;
    }
}
