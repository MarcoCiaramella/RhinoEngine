package com.outofbound.rhinoenginelib.mesh;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;


public class Mesh {

    private final int sizeVertex;
    private ArrayList<AABB> aabbGrid;
    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;
    private final String name;
    private final float[] mMatrix = new float[16];
    private boolean collisionEnabled = false;
    private int aabbSizeX, aabbSizeY, aabbSizeZ;
    private boolean updateAABBRequired = true;
    private final ArrayList<ShaderData> shaderData = new ArrayList<>();


    public Mesh(@NonNull String name, @NonNull float[] vertices, int sizeVertex, @NonNull float[] normals, int[] indices, float[] colors){
        this.name = name;
        this.sizeVertex = sizeVertex;
        init();
        loadVertices(vertices);
        loadNormals(normals);
        loadColors(colors);
        loadIndices(indices);

    }

    public Mesh(@NonNull String name, @NonNull String asset){
        this.name = name;
        this.sizeVertex = 3;
        init();
        loadAsset(asset);
        loadTexture();
    }

    private void loadAsset(String fileName){
        if (fileName.endsWith(".obj")) {
            try {
                Obj obj = ObjUtils.convertToRenderable(ObjReader.read(AbstractEngine.getInstance().getContext().getAssets().open(fileName)));
                shaderData.indicesBuffer = ObjData.getFaceVertexIndices(obj);
                shaderData.vertexBuffer = ObjData.getVertices(obj);
                shaderData.texCoordsBuffer = ObjData.getTexCoords(obj, 2);
                shaderData.normalBuffer = ObjData.getNormals(obj);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            throw new RuntimeException("File extension unknown");
        }

    }

    private void init(){
        position = new Vector3f(0,0,0);
        rotation = new Vector3f(0,0,0);
        scale = 1;
        if (material == null) {
            material = new Material(new Vector3f(0.2f, 0.2f, 0.2f), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(1, 1, 1),200);
        }
        buffers = new Buffers();
    }

    private void loadVertices(float[] vertices){
        ByteBuffer bbVertex = ByteBuffer.allocateDirect(vertices.length * 4);
        bbVertex.order(ByteOrder.nativeOrder());
        shaderData.vertexBuffer = bbVertex.asFloatBuffer();
        shaderData.vertexBuffer.put(vertices);
        shaderData.vertexBuffer.position(0);
    }

    private void loadNormals(float[] normals){
        ByteBuffer bbNormal = ByteBuffer.allocateDirect(normals.length * 4);
        bbNormal.order(ByteOrder.nativeOrder());
        shaderData.normalBuffer = bbNormal.asFloatBuffer();
        shaderData.normalBuffer.put(normals);
        shaderData.normalBuffer.position(0);
    }

    private void loadColors(float[] colors){
        if (colors != null) {
            ByteBuffer bbColor = ByteBuffer.allocateDirect(colors.length * 4);
            bbColor.order(ByteOrder.nativeOrder());
            shaderData.colorBuffer = bbColor.asFloatBuffer();
            shaderData.colorBuffer.put(colors);
            shaderData.colorBuffer.position(0);
        }
    }

    private void loadIndices(int[] indices){
        if (indices != null) {
            ByteBuffer bbIndices = ByteBuffer.allocateDirect(indices.length * 4);
            bbIndices.order(ByteOrder.nativeOrder());
            shaderData.indicesBuffer = bbIndices.asIntBuffer();
            shaderData.indicesBuffer.put(indices);
            shaderData.indicesBuffer.position(0);
        }
    }

    public void beforeRendering(long ms) {}

    public void afterRendering(long ms) {}

    public void onCollision(AABB aabb) {}

    protected void rotateX(){
        Matrix.rotateM(mMatrix, 0, rotation.x, 1, 0, 0);
        updateAABBRequired = true;
    }

    protected void rotateY(){
        Matrix.rotateM(mMatrix, 0, rotation.y, 0, 1, 0);
        updateAABBRequired = true;
    }

    protected void rotateZ(){
        Matrix.rotateM(mMatrix, 0, rotation.z, 0, 0, 1);
        updateAABBRequired = true;
    }

    protected void scale(){
        Matrix.scaleM(mMatrix, 0, scale, scale, scale);
        updateAABBRequired = true;
    }

    protected void translate(){
        Matrix.translateM(mMatrix, 0, position.x, position.y, position.z);
        updateAABBRequired = true;
    }

    public FloatBuffer getVertexBuffer(){
        return shaderData.vertexBuffer;
    }

    public FloatBuffer getNormalBuffer(){
        return shaderData.normalBuffer;
    }

    public FloatBuffer getColorBuffer(){
        return shaderData.colorBuffer;
    }

    public FloatBuffer getTexCoordsBuffer() {
        return shaderData.texCoordsBuffer;
    }

    public IntBuffer getIndicesBuffer(){
        return shaderData.indicesBuffer;
    }

    public int getSizeVertex(){
        return sizeVertex;
    }

    public int getNumVertices(){
        return shaderData.vertexBuffer.capacity() / sizeVertex;
    }

    public int getNumIndices(){
        return shaderData.indicesBuffer.capacity();
    }

    private Mesh newAABBGrid(){
        aabbGrid = AABB.newAABBGrid(this);
        return this;
    }

    public Vector3f getPosition(){
        return position;
    }

    public Vector3f getRotation(){
        return rotation;
    }

    public float getScale(){
        return scale;
    }

    protected static float[] scale(float[] vertices, float scaleX, float scaleY, float scaleZ){
        float[] res = new float[vertices.length];
        for (int i = 0; i < vertices.length; i += 3){
            res[i] = vertices[i] * scaleX;
            res[i+1] = vertices[i+1] * scaleY;
            res[i+2] = vertices[i+2] * scaleZ;
        }
        return res;
    }

    private void loadTexture(){
        if (shaderData.textureBitmap == null) {
            return;
        }
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        shaderData.texture = texture[0];
        if (shaderData.texture != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shaderData.texture);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, shaderData.textureBitmap, 0);
            shaderData.textureBitmap.recycle();
        }
        if (shaderData.texture == 0) {
            throw new RuntimeException("Error loading texture.");
        }
    }

    public String getName(){
        return name;
    }

    public Mesh setScale(float scale){
        this.scale = scale;
        return this;
    }

    public Mesh resetMMatrix(){
        Matrix.setIdentityM(mMatrix, 0);
        return this;
    }

    public float[] getMMatrix(){
        return mMatrix;
    }

    public boolean updateAABB() {
        if (updateAABBRequired) {
            for (AABB aabb : aabbGrid) {
                aabb.mul(mMatrix);
            }
            updateAABBRequired = false;
            return true;
        }
        return false;
    }

    public ArrayList<AABB> getAABBGrid() {
        return aabbGrid;
    }

    public boolean hasAABB() {
        return aabbGrid != null;
    }

    public Mesh enableCollision(int aabbSizeX, int aabbSizeY, int aabbSizeZ) {
        this.aabbSizeX = aabbSizeX;
        this.aabbSizeY = aabbSizeY;
        this.aabbSizeZ = aabbSizeZ;
        if (getNumVertices() > 0) newAABBGrid();
        collisionEnabled = true;
        return this;
    }

    public Mesh enableCollision() {
        return enableCollision(1, 1, 1);
    }

    public Mesh disableCollision() {
        collisionEnabled = false;
        return this;
    }

    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    public int getAABBSizeX() {
        return this.aabbSizeX;
    }

    public int getAABBSizeY() {
        return this.aabbSizeY;
    }

    public int getAABBSizeZ() {
        return this.aabbSizeZ;
    }

    public ShaderData getShaderData() {
        return shaderData;
    }

    private static class ShaderData {
        private FloatBuffer vertexBuffer;
        private FloatBuffer normalBuffer;
        private FloatBuffer colorBuffer;
        private FloatBuffer texCoordsBuffer;
        private IntBuffer indicesBuffer;
        private Vector3f ambientColor;
        private Vector3f diffuseColor;
        private Vector3f specularColor;
        private float specularExponent;
        private int texture = 0;
        private Bitmap textureBitmap;
    }
}
