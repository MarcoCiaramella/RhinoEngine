package com.outofbound.rhinoenginelib.mesh;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import androidx.annotation.NonNull;

import com.outofbound.meshloaderlib.obj.Obj;
import com.outofbound.meshloaderlib.ply.Ply;
import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.util.color.Color;
import com.outofbound.rhinoenginelib.util.color.Gradient;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;


public class Mesh {

    public static class Material {
        private Vector3f ambientColor;
        private Vector3f diffuseColor;
        private Vector3f specularColor;
        private float specularExponent;

        public Material(Vector3f ambientColor, Vector3f diffuseColor, Vector3f specularColor, float specularExponent){
            this.ambientColor = ambientColor;
            this.diffuseColor = diffuseColor;
            this.specularColor = specularColor;
            this.specularExponent = specularExponent;
        }

        public void setAmbientColor(Vector3f ambientColor) {
            this.ambientColor = ambientColor;
        }

        public void setDiffuseColor(Vector3f diffuseColor) {
            this.diffuseColor = diffuseColor;
        }

        public void setSpecularColor(Vector3f specularColor) {
            this.specularColor = specularColor;
        }

        public void setSpecularExponent(float specularExponent) {
            this.specularExponent = specularExponent;
        }

        public Vector3f getAmbientColor() {
            return ambientColor;
        }

        public Vector3f getDiffuseColor() {
            return diffuseColor;
        }

        public Vector3f getSpecularColor() {
            return specularColor;
        }

        public float getSpecularExponent() {
            return specularExponent;
        }
    }

    private float[] vertices;
    private float[] normals;
    private int[] indices;
    private float[] colors;
    private float[] texCoords;
    private final int sizeVertex;
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer texCoordsBuffer;
    private IntBuffer indicesBuffer;
    private AABB aabb;
    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;
    private int texture = 0;
    private Bitmap textureBitmap;
    private Material material;
    private final String name;
    private final float[] mMatrix = new float[16];


    public Mesh(@NonNull String name, @NonNull float[] vertices, int sizeVertex, @NonNull float[] normals, int[] indices, float[] colors){
        this.name = name;
        this.vertices = vertices;
        this.sizeVertex = sizeVertex;
        this.normals = normals;
        this.indices = indices;
        this.colors = colors;
        init();
    }

    public Mesh(@NonNull String name, @NonNull String mesh){
        this.name = name;
        this.sizeVertex = 3;
        loadFromFile(mesh);
        init();
    }

    public Mesh(@NonNull String name, @NonNull String mesh, @NonNull float[] color){
        this.name = name;
        this.sizeVertex = 3;
        loadFromFile(mesh);
        this.colors = Color.getVertexColor(color,vertices.length);
        init();
    }

    public Mesh(@NonNull String name, @NonNull String mesh, @NonNull Gradient[] gradients){
        this.name = name;
        this.sizeVertex = 3;
        loadFromFile(mesh);
        this.colors = Color.gradientColoring(this.vertices,this.indices,gradients);
        init();
    }

    public Mesh(@NonNull String name, @NonNull String mesh, @NonNull Bitmap textureBitmap){
        this.name = name;
        if (!mesh.endsWith(".ply")){
            throw new RuntimeException("Mesh must be in .ply format.");
        }
        this.sizeVertex = 3;
        loadFromFile(mesh);
        this.textureBitmap = textureBitmap;
        init();
    }

    private void loadFromFile(String fileName){
        if (fileName.endsWith(".ply")){
            Ply ply = new Ply(AbstractEngine.getInstance().getContext(), fileName);
            ply.load();
            this.vertices = ply.getVertices();
            this.normals = ply.getNormals();
            this.colors = ply.getColors();
            this.texCoords = ply.getUvs();
            this.indices = ply.getIndices();
        }
        else if (fileName.endsWith(".obj")){
            Obj obj = new Obj(AbstractEngine.getInstance().getContext(), fileName);
            obj.load();
            this.vertices = obj.getVertices();
            this.texCoords = obj.getTextureCoords();
            this.normals = obj.getNormals();
            this.indices = obj.getIndices();
            this.textureBitmap = obj.getMaterial().getMapKd();
            this.material = new Material(new Vector3f(obj.getMaterial().getKa()), new Vector3f(obj.getMaterial().getKd()), new Vector3f(obj.getMaterial().getKs()),obj.getMaterial().getNs());
        }
    }

    private void init(){
        load();
        position = new Vector3f(0,0,0);
        rotation = new Vector3f(0,0,0);
        scale = 1;
        if (material == null) {
            material = new Material(new Vector3f(0.2f, 0.2f, 0.2f), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(1, 1, 1),200);
        }
    }

    private void load(){
        loadVertices();
        loadNormals();
        loadColors();
        loadTexCoords();
        loadIndices();
        loadTexture();
        if (vertices.length > 0) {
            createAABB();
        }
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
        ByteBuffer bbNormal = ByteBuffer.allocateDirect(normals.length * 4);
        bbNormal.order(ByteOrder.nativeOrder());
        normalBuffer = bbNormal.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);
    }

    private void loadColors(){
        if (colors == null) {
            colors = new float[getNumVertices() * 4];
            Arrays.fill(colors,0);
        }
        ByteBuffer bbColor = ByteBuffer.allocateDirect(colors.length * 4);
        bbColor.order(ByteOrder.nativeOrder());
        colorBuffer = bbColor.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }

    private void loadTexCoords(){
        if (texCoords == null) {
            texCoords = new float[getNumVertices() * 2];
            Arrays.fill(texCoords,0);
        }
        ByteBuffer bbTexCoords = ByteBuffer.allocateDirect(texCoords.length * 4);
        bbTexCoords.order(ByteOrder.nativeOrder());
        texCoordsBuffer = bbTexCoords.asFloatBuffer();
        texCoordsBuffer.put(texCoords);
        texCoordsBuffer.position(0);
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

    public void beforeRendering(long ms) {}

    public void afterRendering(long ms) {}

    public void onCollision(Mesh mesh) {}

    protected void rotateX(){
        Matrix.rotateM(mMatrix, 0, rotation.x, 1, 0, 0);
    }

    protected void rotateY(){
        Matrix.rotateM(mMatrix, 0, rotation.y, 0, 1, 0);
    }

    protected void rotateZ(){
        Matrix.rotateM(mMatrix, 0, rotation.z, 0, 0, 1);
    }

    protected void scale(){
        Matrix.scaleM(mMatrix, 0, scale, scale, scale);
    }

    protected void translate(){
        Matrix.translateM(mMatrix, 0, position.x, position.y, position.z);
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

    public FloatBuffer getTexCoordsBuffer() {
        return texCoordsBuffer;
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
        createAABB();
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

    private Mesh createAABB(){
        aabb = new AABB(vertices, sizeVertex);
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

    public int getTexture(){
        return texture;
    }

    private void loadTexture(){
        if (textureBitmap == null) {
            return;
        }
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        this.texture = texture[0];
        if (this.texture != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.texture);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);
            textureBitmap.recycle();
        }
        if (this.texture == 0) {
            throw new RuntimeException("Error loading texture.");
        }
    }

    public Mesh setMaterial(Material material){
        this.material = material;
        return this;
    }

    public Material getMaterial(){
        return material;
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

    public Mesh calcAABB() {
        aabb.calc(mMatrix);
        return this;
    }

    public AABB getAABB() {
        return aabb;
    }

    public boolean hasAABB() {
        return aabb != null;
    }
}
