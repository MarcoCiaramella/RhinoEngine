package com.outofbound.rhinoenginelib.mesh;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.util.bitmap.BitmapUtil;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjSplitting;
import de.javagl.obj.ObjUtils;


public class Mesh {

    private ArrayList<AABB> aabbGrid;
    protected Vector3f position = new Vector3f(0,0,0);
    protected Vector3f rotation = new Vector3f(0,0,0);
    protected float scale = 1;
    private final String name;
    private final float[] mMatrix = new float[16];
    private boolean collisionEnabled = false;
    private int aabbSizeX, aabbSizeY, aabbSizeZ;
    private boolean updateAABBRequired = true;
    private final ArrayList<ShaderData> shaderData = new ArrayList<>();


    public Mesh(@NonNull String name, @NonNull float[] vertices, @NonNull float[] normals, int[] indices, float[] colors){
        this.name = name;
        shaderData.add(new ShaderData());
        loadVertices(shaderData.get(0), vertices);
        loadNormals(shaderData.get(0), normals);
        loadColors(shaderData.get(0), colors);
        loadIndices(shaderData.get(0), indices);

    }

    public Mesh(@NonNull String name, @NonNull String asset){
        this.name = name;
        loadAsset(asset);
    }

    private void loadAsset(String fileName){
        if (fileName.endsWith(".obj")) {
            try {
                Obj obj = ObjUtils.convertToRenderable(ObjReader.read(AbstractEngine.getInstance().getContext().getAssets().open(fileName)));

                List<Mtl> allMtls = new ArrayList<>();
                for (String mtlFileName : obj.getMtlFileNames()) {
                    allMtls.addAll(MtlReader.read(AbstractEngine.getInstance().getContext().getAssets().open(mtlFileName)));
                }
                Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(obj);
                for (Mtl mtl : allMtls) {
                    ShaderData sd = new ShaderData();
                    shaderData.add(sd);
                    sd.indicesBuffer = ObjData.getFaceVertexIndices(materialGroups.get(mtl.getName()));
                    sd.vertexBuffer = ObjData.getVertices(materialGroups.get(mtl.getName()));
                    sd.texCoordsBuffer = ObjData.getTexCoords(materialGroups.get(mtl.getName()), 2);
                    sd.normalBuffer = ObjData.getNormals(materialGroups.get(mtl.getName()));
                    FloatTuple ka = mtl.getKa();
                    sd.ambientColor = new Vector3f(ka.getX(), ka.getY(), ka.getZ());
                    FloatTuple kd = mtl.getKd();
                    sd.diffuseColor = new Vector3f(kd.getX(), kd.getY(), kd.getZ());
                    FloatTuple ks = mtl.getKs();
                    sd.specularColor = new Vector3f(ks.getX(), ks.getY(), ks.getZ());
                    sd.specularExponent = mtl.getNs();
                    sd.textureBitmap = BitmapUtil.getBitmapFromAsset(AbstractEngine.getInstance().getContext(), mtl.getMapKd());
                    loadTexture(sd);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            throw new RuntimeException("File extension unknown");
        }

    }

    private void loadVertices(ShaderData shaderData, float[] vertices){
        ByteBuffer bbVertex = ByteBuffer.allocateDirect(vertices.length * 4);
        bbVertex.order(ByteOrder.nativeOrder());
        shaderData.vertexBuffer = bbVertex.asFloatBuffer();
        shaderData.vertexBuffer.put(vertices);
        shaderData.vertexBuffer.position(0);
    }

    private void loadNormals(ShaderData shaderData, float[] normals){
        ByteBuffer bbNormal = ByteBuffer.allocateDirect(normals.length * 4);
        bbNormal.order(ByteOrder.nativeOrder());
        shaderData.normalBuffer = bbNormal.asFloatBuffer();
        shaderData.normalBuffer.put(normals);
        shaderData.normalBuffer.position(0);
    }

    private void loadColors(ShaderData shaderData, float[] colors){
        if (colors != null) {
            ByteBuffer bbColor = ByteBuffer.allocateDirect(colors.length * 4);
            bbColor.order(ByteOrder.nativeOrder());
            shaderData.colorBuffer = bbColor.asFloatBuffer();
            shaderData.colorBuffer.put(colors);
            shaderData.colorBuffer.position(0);
        }
    }

    private void loadIndices(ShaderData shaderData, int[] indices){
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

    private void loadTexture(ShaderData shaderData){
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
        newAABBGrid();
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

    public ArrayList<ShaderData> getShaderData() {
        return shaderData;
    }

    public static class ShaderData {
        public FloatBuffer vertexBuffer;
        public FloatBuffer normalBuffer;
        public FloatBuffer colorBuffer;
        public FloatBuffer texCoordsBuffer;
        public IntBuffer indicesBuffer;
        public Vector3f ambientColor = new Vector3f(0.2f, 0.2f, 0.2f);
        public Vector3f diffuseColor = new Vector3f(0.5f, 0.5f, 0.5f);
        public Vector3f specularColor = new Vector3f(1, 1, 1);
        public float specularExponent = 200;
        public int texture = 0;
        public Bitmap textureBitmap;
    }
}
