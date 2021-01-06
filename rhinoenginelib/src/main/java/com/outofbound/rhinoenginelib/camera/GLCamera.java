package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public abstract class GLCamera {

    protected float[] vpMatrix;
    protected float[] projectionMatrix;
    private final float[] viewMatrix;
    protected Vector3f eye;
    protected Vector3f center;
    protected Vector3f up;
    private final Vector3f eyeRes;
    protected GLMesh glMeshToFollow;
    private Vector3f pointToFollow;

    public GLCamera(Vector3f eye, Vector3f up, Vector3f center){
        vpMatrix = new float[16];
        projectionMatrix = new float[16];
        viewMatrix = new float[16];
        this.eye = new Vector3f(eye);
        this.up = new Vector3f(up);
        this.center = new Vector3f(center);
        eyeRes = this.eye.clone();
        glMeshToFollow = null;
        pointToFollow = null;
    }

    protected void setupM(){
        if (glMeshToFollow != null) {
            center.copy(glMeshToFollow.getMotion().position);
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

    public abstract float[] create(int width, int height, long ms);

    public Vector3f getEye(){
        return eye;
    }

    public Vector3f getUp(){
        return up;
    }

    public Vector3f getCenter(){
        return center;
    }

    public GLCamera rotate(float a, float x, float y, float z){
        eye.rotate(a,x,y,z);
        return this;
    }

    public GLCamera translate(Vector3f shift){
        eye.translate(shift);
        return this;
    }
}
