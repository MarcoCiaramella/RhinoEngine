package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public final class CameraOrthographic extends Camera {

    private final float size;

    public CameraOrthographic(Vector3f eye, Vector3f center, Vector3f up, float near, float far, float size) {
        super(eye, center, up, near, far);
        this.size = size;
    }

    @Override
    public void loadVpMatrix() {
        Matrix.orthoM(projectionMatrix, 0, -size, size, -size, size, near, far);
        createVpMatrix();
    }
}
