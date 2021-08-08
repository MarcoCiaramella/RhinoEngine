package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


/**
 * The Camera class provides the view-projection matrix.
 */
public abstract class Camera {

    private final float[] vpMatrix;
    protected float[] projectionMatrix;
    private final float[] viewMatrix;
    protected Vector3f eye;
    protected Vector3f center;
    protected Vector3f up;
    private final Vector3f eyeRes;
    protected Mesh meshToFollow;
    private Vector3f pointToFollow;
    protected final float near;
    protected final float far;
    protected int width;
    protected int height;

    /**
     * Create a Camera object.
     * @param eye the camera position
     * @param center the center of view
     * @param up the up vector
     * @param near the near clipping plane
     * @param far the far clipping plane
     */
    public Camera(Vector3f eye, Vector3f center, Vector3f up, float near, float far){
        vpMatrix = new float[16];
        projectionMatrix = new float[16];
        viewMatrix = new float[16];
        this.eye = eye;
        this.center = center;
        this.up = up;
        this.near = near;
        this.far = far;
        eyeRes = new Vector3f(0,0,0);
        meshToFollow = null;
        pointToFollow = null;
    }

    /**
     * Create the view-projection matrix.
     */
    protected void createVpMatrix(){
        if (meshToFollow != null) {
            center.copy(meshToFollow.getPosition());
            eye.add(center, eyeRes);
        }
        else if (pointToFollow != null){
            center.copy(pointToFollow);
            eye.add(center, eyeRes);
        }
        else {
            eyeRes.copy(eye);
        }
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, eyeRes.x, eyeRes.y, eyeRes.z, center.x, center.y, center.z, up.x, up.y, up.z);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    /**
     * Enable camera to follow a Mesh.
     * @param meshToFollow mesh to follow
     * @return this camera
     */
    public Camera follow(Mesh meshToFollow){
        this.meshToFollow = meshToFollow;
        pointToFollow = null;
        return this;
    }

    /**
     * Enable camera to follow a Vector3f.
     * @param pointToFollow the point to follow
     * @return this camera
     */
    public Camera follow(Vector3f pointToFollow){
        this.pointToFollow = pointToFollow;
        meshToFollow = null;
        return this;
    }

    /**
     * Disable follow mode.
     * @return this camera
     */
    public Camera unfollow(){
        meshToFollow = null;
        pointToFollow = null;
        return this;
    }

    /**
     * The abstract method for view-projection matrix loading.
     */
    public abstract void loadVpMatrix();

    /**
     * Return the eye vector.
     * @return the eye vector
     */
    public Vector3f getEye(){
        return eye;
    }

    /**
     * Return the up vector.
     * @return the up vector
     */
    public Vector3f getUp(){
        return up;
    }

    /**
     * Return the center vector.
     * @return the center vector
     */
    public Vector3f getCenter(){
        return center;
    }

    /**
     * Return the near clipping plane.
     * @return the near clipping plane
     */
    public float getNear(){
        return near;
    }

    /**
     * Return the far clipping plane.
     * @return the far clipping plane
     */
    public float getFar(){
        return far;
    }

    /**
     * Rotate the camera.
     * @param a angle of rotation
     * @param x x axis of rotation
     * @param y y axis of rotation
     * @param z z axis of rotation
     * @return this camera
     */
    public Camera rotate(float a, float x, float y, float z){
        eye.rotate(a,x,y,z);
        return this;
    }

    /**
     * Translate the camera.
     * @param shift the translation vector
     * @return this camera
     */
    public Camera translate(Vector3f shift){
        eye.translate(shift);
        return this;
    }

    /**
     * Return the view matrix.
     * @return the view matrix
     */
    public float[] getViewMatrix(){
        return viewMatrix;
    }

    /**
     * Return the view-projection matrix.
     * @return the view-projection matrix
     */
    public float[] getVpMatrix(){
        return vpMatrix;
    }

    /**
     * Set the viewport width.
     * @param width the viewport width
     * @return this camera
     */
    public Camera setWidth(int width){
        this.width = width;
        return this;
    }

    /**
     * Set the viewport height.
     * @param height the viewport height
     * @return this camera
     */
    public Camera setHeight(int height){
        this.height = height;
        return this;
    }
}
