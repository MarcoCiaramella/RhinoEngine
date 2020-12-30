package com.outofbound.rhinoengine.mesh;

import android.opengl.Matrix;

import com.outofbound.rhinoengine.util.vector.Vector3f;


public class BoundingBox {

    public static final int VFBL = 0;
    public static final int VFBR = 1;
    public static final int VFTR = 2;
    public static final int VFTL = 3;
    public static final int VBBL = 4;
    public static final int VBBR = 5;
    public static final int VBTR = 6;
    public static final int VBTL = 7;
    private Vector3f vfbl;
    private Vector3f vfbr;
    private Vector3f vftr;
    private Vector3f vftl;
    private Vector3f vbbl;
    private Vector3f vbbr;
    private Vector3f vbtr;
    private Vector3f vbtl;
    private Vector3f vfblRes;
    private Vector3f vfbrRes;
    private Vector3f vftrRes;
    private Vector3f vftlRes;
    private Vector3f vbblRes;
    private Vector3f vbbrRes;
    private Vector3f vbtrRes;
    private Vector3f vbtlRes;
    private float[][] bbVertices;
    private Vector3f min;
    private Vector3f max;
    private float[] arr;
    private float[] mvMatrix;
    private Vector3f[] aabb;


    public BoundingBox(float[] vertices, int sizeVertex){
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
        bbVertices = new float[8][3];
        min = new Vector3f(0,0,0);
        max = new Vector3f(0,0,0);
        arr = new float[24];
        mvMatrix = new float[16];
        Matrix.setIdentityM(mvMatrix,0);
        aabb = new Vector3f[]{min,max};
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

    public synchronized void copyMvMatrix(float[] mvMatrix){
        System.arraycopy(mvMatrix, 0, this.mvMatrix, 0, mvMatrix.length);
    }

    private void transform(){
        vfbl.multiplyMV(mvMatrix,vfblRes);
        vfbr.multiplyMV(mvMatrix,vfbrRes);
        vftr.multiplyMV(mvMatrix,vftrRes);
        vftl.multiplyMV(mvMatrix,vftlRes);
        vbbl.multiplyMV(mvMatrix,vbblRes);
        vbbr.multiplyMV(mvMatrix,vbbrRes);
        vbtr.multiplyMV(mvMatrix,vbtrRes);
        vbtl.multiplyMV(mvMatrix,vbtlRes);
    }

    public synchronized float[][] getVertices(){
        transform();
        bbVertices[VFBL][0] = vfblRes.x;
        bbVertices[VFBL][1] = vfblRes.y;
        bbVertices[VFBL][2] = vfblRes.z;
        bbVertices[VFBR][0] = vfbrRes.x;
        bbVertices[VFBR][1] = vfbrRes.y;
        bbVertices[VFBR][2] = vfbrRes.z;
        bbVertices[VFTR][0] = vftrRes.x;
        bbVertices[VFTR][1] = vftrRes.y;
        bbVertices[VFTR][2] = vftrRes.z;
        bbVertices[VFTL][0] = vftlRes.x;
        bbVertices[VFTL][1] = vftlRes.y;
        bbVertices[VFTL][2] = vftlRes.z;
        bbVertices[VBBL][0] = vbblRes.x;
        bbVertices[VBBL][1] = vbblRes.y;
        bbVertices[VBBL][2] = vbblRes.z;
        bbVertices[VBBR][0] = vbbrRes.x;
        bbVertices[VBBR][1] = vbbrRes.y;
        bbVertices[VBBR][2] = vbbrRes.z;
        bbVertices[VBTR][0] = vbtrRes.x;
        bbVertices[VBTR][1] = vbtrRes.y;
        bbVertices[VBTR][2] = vbtrRes.z;
        bbVertices[VBTL][0] = vbtlRes.x;
        bbVertices[VBTL][1] = vbtlRes.y;
        bbVertices[VBTL][2] = vbtlRes.z;
        return bbVertices;
    }

    public synchronized Vector3f[] getAABB(){
        transform();
        calcMinMax();
        return aabb;
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
        min.x = minCoord(arr,3,0);
        min.y = minCoord(arr,3,1);
        min.z = minCoord(arr,3,2);
    }

    private void calcMax(){
        max.x = maxCoord(arr,3,0);
        max.y = maxCoord(arr,3,1);
        max.z = maxCoord(arr,3,2);
    }

}