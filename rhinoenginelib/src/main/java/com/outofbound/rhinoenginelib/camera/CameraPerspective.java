package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public final class CameraPerspective extends Camera {

    public CameraPerspective(Vector3f eye, Vector3f center, Vector3f up, float near, float far) {
        super(eye, center, up, near, far);
    }

    @Override
    public void loadVpMatrix() {
        float ratio = (float)width/(float)height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
        createVpMatrix();
    }
}
