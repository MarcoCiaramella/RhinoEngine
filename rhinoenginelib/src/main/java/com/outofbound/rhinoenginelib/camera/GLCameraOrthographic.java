package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public final class GLCameraOrthographic extends GLCamera{

    private final float size;

    public GLCameraOrthographic(Vector3f eye, Vector3f up, Vector3f center, float near, float far, float size) {
        super(eye, up, center, near, far);
        this.size = size;
    }

    @Override
    public void loadVpMatrix() {
        Matrix.orthoM(projectionMatrix, 0, -size, size, -size, size, near, far);
        createVpMatrix();
    }
}
