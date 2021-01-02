package com.outofbound.rhinoenginelib.camera;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public abstract class GLCamera {

    protected float[] mvpMatrix;
    protected float[] projectionMatrix;
    private float[] viewMatrix;
    protected Vector3f eyePos;
    protected Vector3f eyeCenter;
    protected Vector3f eyeUp;
    protected GLMesh glMeshToFollow;
    private Vector3f eyePosRes;
    private Vector3f pointToFollow;

    public GLCamera(Vector3f eyePos, Vector3f eyeUp, Vector3f eyeCenter){
        mvpMatrix = new float[16];
        projectionMatrix = new float[16];
        viewMatrix = new float[16];
        this.eyePos = new Vector3f(eyePos);
        this.eyeUp = new Vector3f(eyeUp);
        this.eyeCenter = new Vector3f(eyeCenter);
        eyePosRes = this.eyePos.clone();
        glMeshToFollow = null;
        pointToFollow = null;
    }

    protected void setupM(){
        if (glMeshToFollow != null) {
            eyeCenter.copy(glMeshToFollow.getMotion().position);
            eyePos.add(eyeCenter,eyePosRes);
        }
        else if (pointToFollow != null){
            eyeCenter.copy(pointToFollow);
            eyePos.add(eyeCenter,eyePosRes);
        }
        else {
            eyePosRes.copy(eyePos);
        }
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, eyePosRes.x, eyePosRes.y, eyePosRes.z, eyeCenter.x, eyeCenter.y, eyeCenter.z, eyeUp.x, eyeUp.y, eyeUp.z);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
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

    public Vector3f getEyePos(){
        return eyePos;
    }

    public Vector3f getEyeUp(){
        return eyeUp;
    }

    public Vector3f getEyeCenter(){
        return eyeCenter;
    }

    public GLCamera rotate(float a, float x, float y, float z){
        eyePos.rotate(a,x,y,z);
        return this;
    }

    public GLCamera translate(Vector3f shift){
        eyePos.translate(shift);
        return this;
    }
}
