package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

/**
 * Implements a perspective projection.
 */
public final class CameraPerspective extends Camera {

    /**
     * The constructor.
     * @param eye the camera position
     * @param center the center of view
     * @param up the up vector
     * @param near the near clipping plane
     * @param far the far clipping plane
     */
    public CameraPerspective(Vector3f eye, Vector3f center, Vector3f up, float near, float far) {
        super(eye, center, up, near, far);
    }

    /**
     * Loads the perspective projection matrix and the view matrix.
     */
    @Override
    public void loadVpMatrix() {
        float ratio = (float)width/(float)height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
        createVpMatrix();
    }
}
