package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


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

    public Camera follow(Mesh meshToFollow){
        this.meshToFollow = meshToFollow;
        pointToFollow = null;
        return this;
    }

    public Camera follow(Vector3f pointToFollow){
        this.pointToFollow = pointToFollow;
        meshToFollow = null;
        return this;
    }

    public Camera unfollow(){
        meshToFollow = null;
        pointToFollow = null;
        return this;
    }

    public abstract void loadVpMatrix();

    public Vector3f getEye(){
        return eye;
    }

    public Vector3f getUp(){
        return up;
    }

    public Vector3f getCenter(){
        return center;
    }

    public float getNear(){
        return near;
    }

    public float getFar(){
        return far;
    }

    public Camera rotate(float a, float x, float y, float z){
        eye.rotate(a,x,y,z);
        return this;
    }

    public Camera translate(Vector3f shift){
        eye.translate(shift);
        return this;
    }

    public float[] getViewMatrix(){
        return viewMatrix;
    }

    public float[] getVpMatrix(){
        return vpMatrix;
    }

    public Camera setWidth(int width){
        this.width = width;
        return this;
    }

    public Camera setHeight(int height){
        this.height = height;
        return this;
    }
}
