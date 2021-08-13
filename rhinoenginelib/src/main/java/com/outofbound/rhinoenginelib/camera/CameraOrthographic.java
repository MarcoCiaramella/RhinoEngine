package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

/**
 * Implements an orthographic projection.
 */
public final class CameraOrthographic extends Camera {

    private final float size;

    /**
     * The constructor.
     * @param eye the camera position
     * @param center the center of view
     * @param up the up vector
     * @param near the near clipping plane
     * @param far the far clipping plane
     * @param size the size of the clipping plane
     */
    public CameraOrthographic(Vector3f eye, Vector3f center, Vector3f up, float near, float far, float size) {
        super(eye, center, up, near, far);
        this.size = size;
    }

    /**
     * Loads the orthographic projection matrix and the view matrix.
     */
    @Override
    public void loadVpMatrix() {
        Matrix.orthoM(projectionMatrix, 0, -size, size, -size, size, near, far);
        createVpMatrix();
    }
}
