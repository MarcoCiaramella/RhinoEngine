package com.outofbound.rhinoenginelib.mesh;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import androidx.annotation.CallSuper;
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


public abstract class Mesh {

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
    private long timeToLive = -1;
    private boolean dead = false;
    private BoundingBox boundingBox = null;
    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;
    private int texture = 0;
    private Bitmap textureBitmap;


    public Mesh(@NonNull float[] vertices, int sizeVertex, @NonNull float[] normals, int[] indices, float[] colors){
        this.vertices = vertices;
        this.sizeVertex = sizeVertex;
        this.normals = normals;
        this.indices = indices;
        this.colors = colors;
        init();
    }

    public Mesh(@NonNull String mesh){
        this.sizeVertex = 3;
        loadFromFile(mesh);
        init();
    }

    public Mesh(@NonNull String mesh, @NonNull float[] color){
        this.sizeVertex = 3;
        loadFromFile(mesh);
        this.colors = Color.getVertexColor(color,vertices.length);
        init();
    }

    public Mesh(@NonNull String mesh, @NonNull Gradient[] gradients){
        this.sizeVertex = 3;
        loadFromFile(mesh);
        this.colors = Color.gradientColoring(this.vertices,this.indices,gradients);
        init();
    }

    public Mesh(@NonNull String mesh, @NonNull Bitmap textureBitmap){
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
            this.normals = obj.getNormals();
            this.indices = obj.getIndices();
            this.textureBitmap = obj.getMaterial().getMapKd();
        }
    }

    private void init(){
        load();
        position = new Vector3f(0,0,0);
        rotation = new Vector3f(0,0,0);
        scale = 1;
    }

    private void load(){
        loadVertices();
        loadNormals();
        loadColors();
        loadTexCoords();
        loadIndices();
        loadTexture();
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

    @CallSuper
    public void onAdd(){
        dead = false;
        timeToLive = -1;
    }

    @CallSuper
    public void onRemove(){
        dead = true;
    }

    public abstract void doTransformation(float[] mMatrix, long ms);

    public Mesh rotateX(float[] mMatrix){
        Matrix.rotateM(mMatrix, 0, rotation.x, 1, 0, 0);
        return this;
    }

    public Mesh rotateY(float[] mMatrix){
        Matrix.rotateM(mMatrix, 0, rotation.y, 0, 1, 0);
        return this;
    }

    public Mesh rotateZ(float[] mMatrix){
        Matrix.rotateM(mMatrix, 0, rotation.z, 0, 0, 1);
        return this;
    }

    public Mesh scale(float[] mMatrix){
        Matrix.scaleM(mMatrix, 0, scale, scale, scale);
        return this;
    }

    public Mesh translate(float[] mMatrix){
        Matrix.translateM(mMatrix, 0, position.x, position.y, position.z);
        return this;
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

    public Mesh createBoundingBox(){
        if (boundingBox == null && vertices.length > 0) {
            boundingBox = new BoundingBox(vertices, sizeVertex);
        }
        return this;
    }

    /**
     * Set time to live. Use value < 0 for a infinite time to live.
     * @param timeToLive the time to live.
     * @return this Mesh.
     */
    public Mesh setTimeToLive(long timeToLive){
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
}
