package com.outofbound.rhinoenginelib.mesh;


import android.opengl.Matrix;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.mesh.util.Ply;
import com.outofbound.rhinoenginelib.util.color.Color;
import com.outofbound.rhinoenginelib.util.color.Gradient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;


public abstract class GLMesh {

    private float[] vertices;
    private float[] normals;
    private int[] indices;
    private float[] colors;
    private final int sizeVertex;
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer colorBuffer;
    private IntBuffer indicesBuffer;
    private long timeToLive = -1;
    private boolean dead = false;
    private BoundingBox boundingBox = null;
    private Motion motion = null;


    public GLMesh(@NonNull float[] vertices, int sizeVertex, float[] normals, int[] indices, float[] colors){
        this.vertices = vertices;
        this.sizeVertex = sizeVertex;
        this.normals = normals;
        this.indices = indices;
        this.colors = colors;
        init();
    }

    public GLMesh(String mesh){
        Ply ply = new Ply(GLEngine.getInstance().getContext(), mesh);
        ply.load();
        this.vertices = ply.getVertices();
        this.sizeVertex = 3;
        this.normals = ply.getNormals();
        this.colors = ply.getColors();
        this.indices = ply.getIndices();
        init();
    }

    public GLMesh(String mesh, float[] color){
        Ply ply = new Ply(GLEngine.getInstance().getContext(), mesh);
        ply.load();
        this.vertices = ply.getVertices();
        this.sizeVertex = 3;
        this.normals = ply.getNormals();
        this.colors = Color.getVertexColor(color,vertices.length);
        this.indices = ply.getIndices();
        init();
    }

    public GLMesh(String mesh, Gradient[] gradients){
        Ply ply = new Ply(GLEngine.getInstance().getContext(), mesh);
        ply.load();
        this.vertices = ply.getVertices();
        this.sizeVertex = 3;
        this.normals = ply.getNormals();
        this.indices = ply.getIndices();
        this.colors = Color.gradientColoring(this.vertices,this.indices,gradients);
        init();
    }

    private void init(){
        load();
    }

    private void load(){
        loadVertices();
        loadNormals();
        loadColors();
        loadIndices();
    }

    public void reloadVertices(){
        loadVertices();
    }

    public void reloadNormals(){
        loadNormals();
    }

    public void reloadColors(){
        loadColors();
    }

    public void reloadIndices(){
        loadIndices();
    }

    private void loadVertices(){
        ByteBuffer bbVertex = ByteBuffer.allocateDirect(vertices.length * 4);
        bbVertex.order(ByteOrder.nativeOrder());
        vertexBuffer = bbVertex.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    private void loadNormals(){
        if (normals != null) {
            ByteBuffer bbNormal = ByteBuffer.allocateDirect(normals.length * 4);
            bbNormal.order(ByteOrder.nativeOrder());
            normalBuffer = bbNormal.asFloatBuffer();
            normalBuffer.put(normals);
            normalBuffer.position(0);
        }
    }

    private void loadColors(){
        if (colors != null) {
            ByteBuffer bbColor = ByteBuffer.allocateDirect(colors.length * 4);
            bbColor.order(ByteOrder.nativeOrder());
            colorBuffer = bbColor.asFloatBuffer();
            colorBuffer.put(colors);
            colorBuffer.position(0);
        }
    }

    private void loadIndices(){
        if (indices != null) {
            ByteBuffer bbIndices = ByteBuffer.allocateDirect(indices.length * 4);
            bbIndices.order(ByteOrder.nativeOrder());
            indicesBuffer = bbIndices.asIntBuffer();
            indicesBuffer.put(indices);
            indicesBuffer.position(0);
        }
    }

    @CallSuper
    public void onAdd(){
        dead = false;
        timeToLive = -1;
    }

    @CallSuper
    public void onRemove(){
        dead = true;
    }

    public abstract void doTransformation(float[] mvMatrix);

    public GLMesh rotateX(float[] mvMatrix){
        Matrix.rotateM(mvMatrix, 0, getMotion().rotation.x, 1, 0, 0);
        return this;
    }

    public GLMesh rotateY(float[] mvMatrix){
        Matrix.rotateM(mvMatrix, 0, getMotion().rotation.y, 0, 1, 0);
        return this;
    }

    public GLMesh rotateZ(float[] mvMatrix){
        Matrix.rotateM(mvMatrix, 0, getMotion().rotation.z, 0, 0, 1);
        return this;
    }

    public GLMesh scale(float[] mvMatrix){
        Matrix.scaleM(mvMatrix, 0, getMotion().scale.x, getMotion().scale.y, getMotion().scale.z);
        return this;
    }

    public GLMesh translate(float[] mvMatrix){
        Matrix.translateM(mvMatrix, 0, getMotion().position.x, getMotion().position.y, getMotion().position.z);
        return this;
    }

    public void move(long ms){
        if (motion != null) {
            motion.move(ms);
        }
    }

    public boolean isDead(long ms){
        if (timeToLive != -1) {
            if (timeToLive-ms <= 0){
                dead = true;
            }
            else{
                timeToLive -= ms;
            }
        }
        return dead;
    }

    public void kill(){
        dead = true;
    }

    public FloatBuffer getVertexBuffer(){
        return vertexBuffer;
    }

    public FloatBuffer getNormalBuffer(){
        return normalBuffer;
    }

    public FloatBuffer getColorBuffer(){
        return colorBuffer;
    }

    public IntBuffer getIndicesBuffer(){
        return indicesBuffer;
    }

    public int getSizeVertex(){
        return sizeVertex;
    }

    public float[] getVertices(){
        return vertices;
    }

    public float[] getNormals(){
        return normals;
    }

    public float[] getColors(){
        return colors;
    }

    public int[] getIndices(){
        return indices;
    }

    private float[] addFloats(float[] floats1, float[] floats2){
        float[] result = Arrays.copyOf(floats1,floats1.length+floats2.length);
        System.arraycopy(floats2,0,result,floats1.length,floats2.length);
        return result;
    }

    private int[] addInts(int[] ints1, int[] ints2){
        int[] result = Arrays.copyOf(ints1,ints1.length+ints2.length);
        System.arraycopy(ints2,0,result,ints1.length,ints2.length);
        return result;
    }

    private float[] removeFloats(float[] floats, int pos, int length){
        float[] result = new float[floats.length-length];
        System.arraycopy(floats,0,result,0,pos);
        System.arraycopy(floats,pos+length,result,pos,floats.length-(pos+length));
        return result;
    }

    private int[] removeInts(int[] ints, int pos, int length){
        int[] result = new int[ints.length-length];
        System.arraycopy(ints,0,result,0,pos);
        System.arraycopy(ints,pos+length,result,pos,ints.length-(pos+length));
        return result;
    }

    public void addVertices(float[] vertices){
        this.vertices = addFloats(this.vertices,vertices);
        if (this.vertices.length > 0) {
            boundingBox = new BoundingBox(vertices, sizeVertex);
        }
    }

    public void addNormals(float[] normals){
        this.normals = addFloats(this.normals,normals);
    }

    public void addColors(float[] colors){
        this.colors = addFloats(this.colors,colors);
    }

    public void addIndices(int[] indices){
        this.indices = addInts(this.indices,indices);
    }

    public void removeVertices(int pos, int length){
        vertices = removeFloats(vertices,pos*sizeVertex,length*sizeVertex);
    }

    public void removeNormals(int pos, int length){
        normals = removeFloats(normals,pos*sizeVertex,length*sizeVertex);
    }

    public void removeColors(int pos, int length){
        colors = removeFloats(colors,pos*4,length*4);
    }

    public void removeIndices(int pos, int length){
        indices = removeInts(indices,pos,length);
    }

    public int getNumVertices(){
        return vertices.length/sizeVertex;
    }

    public int getNumIndices(){
        return indices.length;
    }

    public GLMesh setMotion(Motion motion){
        this.motion = motion;
        return this;
    }

    public Motion getMotion(){
        return motion;
    }

    public GLMesh createBoundingBox(){
        if (boundingBox == null && vertices.length > 0) {
            boundingBox = new BoundingBox(vertices, sizeVertex);
        }
        return this;
    }

    /**
     * Set time to live. Use value < 0 for a infinite time to live.
     * @param timeToLive the time to live.
     * @return this GLMesh.
     */
    public GLMesh setTimeToLive(long timeToLive){
        this.timeToLive = timeToLive;
        return this;
    }

    /**
     * Return the bounding box.
     * @return the bounding box.
     */
    public BoundingBox getBoundingBox(){
        return boundingBox;
    }
}
