package com.outofbound.rhinoenginelib.mesh;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class AABB {

    // vertex front bottom left
    public static final int VFBL = 0;
    // vertex front bottom right
    public static final int VFBR = 1;
    // vertex front top right
    public static final int VFTR = 2;
    // vertex front top left
    public static final int VFTL = 3;
    // vertex back bottom left
    public static final int VBBL = 4;
    // vertex back bottom right
    public static final int VBBR = 5;
    // vertex back top right
    public static final int VBTR = 6;
    // vertex back top left
    public static final int VBTL = 7;
    private final Vector3f vfbl;
    private final Vector3f vfbr;
    private final Vector3f vftr;
    private final Vector3f vftl;
    private final Vector3f vbbl;
    private final Vector3f vbbr;
    private final Vector3f vbtr;
    private final Vector3f vbtl;
    private final Vector3f vfblRes;
    private final Vector3f vfbrRes;
    private final Vector3f vftrRes;
    private final Vector3f vftlRes;
    private final Vector3f vbblRes;
    private final Vector3f vbbrRes;
    private final Vector3f vbtrRes;
    private final Vector3f vbtlRes;
    private final double[] min;
    private final double[] max;
    private final float[] arr;
    private final float[] mMatrix;


    protected AABB(float[] vertices, int sizeVertex, float[] mMatrix){
        float minX = minCoord(vertices,sizeVertex,0);
        float maxX = maxCoord(vertices,sizeVertex,0);
        float minY = minCoord(vertices,sizeVertex,1);
        float maxY = maxCoord(vertices,sizeVertex,1);
        float minZ = minCoord(vertices,sizeVertex,2);
        float maxZ = maxCoord(vertices,sizeVertex,2);
        vfbl = new Vector3f(minX,minY,maxZ);
        vfbr = new Vector3f(maxX,minY,maxZ);
        vftr = new Vector3f(maxX,maxY,maxZ);
        vftl = new Vector3f(minX,maxY,maxZ);
        vbbl = new Vector3f(minX,minY,minZ);
        vbbr = new Vector3f(maxX,minY,minZ);
        vbtr = new Vector3f(maxX,maxY,minZ);
        vbtl = new Vector3f(minX,maxY,minZ);
        vfblRes = new Vector3f(0,0,0);
        vfbrRes = new Vector3f(0,0,0);
        vftrRes = new Vector3f(0,0,0);
        vftlRes = new Vector3f(0,0,0);
        vbblRes = new Vector3f(0,0,0);
        vbbrRes = new Vector3f(0,0,0);
        vbtrRes = new Vector3f(0,0,0);
        vbtlRes = new Vector3f(0,0,0);
        min = new double[3];
        max = new double[3];
        arr = new float[24];
        this.mMatrix = mMatrix;
        Matrix.setIdentityM(mMatrix,0);
    }

    private float minCoord(float[] vertices, int sizeVertex, int coord){
        float min = vertices[coord];
        for (int i = 0; i < vertices.length; i += sizeVertex){
            min = Math.min(vertices[i+coord], min);
        }
        return min;
    }

    private float maxCoord(float[] vertices, int sizeVertex, int coord){
        float max = vertices[coord];
        for (int i = 0; i < vertices.length; i += sizeVertex){
            max = Math.max(vertices[i+coord], max);
        }
        return max;
    }

    private void transform(){
        vfbl.multiplyMV(mMatrix,vfblRes);
        vfbr.multiplyMV(mMatrix,vfbrRes);
        vftr.multiplyMV(mMatrix,vftrRes);
        vftl.multiplyMV(mMatrix,vftlRes);
        vbbl.multiplyMV(mMatrix,vbblRes);
        vbbr.multiplyMV(mMatrix,vbbrRes);
        vbtr.multiplyMV(mMatrix,vbtrRes);
        vbtl.multiplyMV(mMatrix,vbtlRes);
    }

    private void loadArr(){
        int i = 0;
        arr[i++] = vfblRes.x;
        arr[i++] = vfblRes.y;
        arr[i++] = vfblRes.z;
        arr[i++] = vfbrRes.x;
        arr[i++] = vfbrRes.y;
        arr[i++] = vfbrRes.z;
        arr[i++] = vftrRes.x;
        arr[i++] = vftrRes.y;
        arr[i++] = vftrRes.z;
        arr[i++] = vftlRes.x;
        arr[i++] = vftlRes.y;
        arr[i++] = vftlRes.z;
        arr[i++] = vbblRes.x;
        arr[i++] = vbblRes.y;
        arr[i++] = vbblRes.z;
        arr[i++] = vbbrRes.x;
        arr[i++] = vbbrRes.y;
        arr[i++] = vbbrRes.z;
        arr[i++] = vbtrRes.x;
        arr[i++] = vbtrRes.y;
        arr[i++] = vbtrRes.z;
        arr[i++] = vbtlRes.x;
        arr[i++] = vbtlRes.y;
        arr[i] = vbtlRes.z;
    }

    private void calcMinMax(){
        loadArr();
        calcMin();
        calcMax();
    }

    private void calcMin(){
        min[0] = minCoord(arr,3,0);
        min[1] = minCoord(arr,3,1);
        min[2] = minCoord(arr,3,2);
    }

    private void calcMax(){
        max[0] = maxCoord(arr,3,0);
        max[1] = maxCoord(arr,3,1);
        max[2] = maxCoord(arr,3,2);
    }

    protected void calc(){
        transform();
        calcMinMax();
    }

    protected double[] getMin() {
        return min;
    }

    protected double[] getMax() {
        return max;
    }
}