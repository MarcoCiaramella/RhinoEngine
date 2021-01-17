package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public abstract class GLCamera {

    private final float[] vpMatrix;
    protected float[] projectionMatrix;
    private final float[] viewMatrix;
    protected Vector3f eye;
    protected Vector3f center;
    protected Vector3f up;
    private final Vector3f eyeRes;
    protected GLMesh glMeshToFollow;
    private Vector3f pointToFollow;
    protected final float near;
    protected final float far;
    protected int width;
    protected int height;

    public GLCamera(Vector3f eye, Vector3f up, Vector3f center, float near, float far){
        vpMatrix = new float[16];
        projectionMatrix = new float[16];
        viewMatrix = new float[16];
        this.eye = eye;
        this.up = up;
        this.center = center;
        this.near = near;
        this.far = far;
        eyeRes = new Vector3f(0,0,0);
        glMeshToFollow = null;
        pointToFollow = null;
    }

    protected void createVpMatrix(){
        if (glMeshToFollow != null) {
            center.copy(glMeshToFollow.getPosition());
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

    public GLCamera follow(GLMesh glMeshToFollow){
        this.glMeshToFollow = glMeshToFollow;
        pointToFollow = null;
        return this;
    }

    public GLCamera follow(Vector3f pointToFollow){
        this.pointToFollow = pointToFollow;
        glMeshToFollow = null;
        return this;
    }

    public GLCamera unfollow(){
        glMeshToFollow = null;
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

    public GLCamera rotate(float a, float x, float y, float z){
        eye.rotate(a,x,y,z);
        return this;
    }

    public GLCamera translate(Vector3f shift){
        eye.translate(shift);
        return this;
    }

    public float[] getViewMatrix(){
        return viewMatrix;
    }

    public float[] getVpMatrix(){
        return vpMatrix;
    }

    public GLCamera setWidth(int width){
        this.width = width;
        return this;
    }

    public GLCamera setHeight(int height){
        this.height = height;
        return this;
    }
}
