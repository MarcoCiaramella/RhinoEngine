package com.outofbound.rhinoenginelib.mesh.util;

import android.content.Context;

import com.outofbound.rhinoenginelib.util.file.TextFileReader;

public class Obj {

    private final float[] vertices;
    private final float[] textureCoords;
    private final float[] normals;
    private final int[] indices;
    private final String[] content;

    public Obj(Context context, String fileName){
        content = TextFileReader.getString(context, fileName).split("\n");
        int numVertices = getNumVertices();
        vertices = new float[numVertices *3];
        textureCoords = new float[numVertices *2];
        normals = new float[numVertices *3];
        indices = new int[getNumIndices()];
    }

    public void load(){
        loadFaces();
    }

    private void loadVertices(){
        int pos = 0;
        for (String line : content) {
            String[] values = line.split(" ");
            if (isVertex(line)) {
                pos = loadVertex(vertices, pos, values[1], values[2], values[3]);
            }
        }
    }

    private float[] loadTextureCoords(){
        float[] textureCoords = new float[getNumTextureCoords()*2];
        int pos = 0;
        for (String line : content) {
            String[] values = line.split(" ");
            if (isTextureCoord(line)){
                pos = loadTextureCoord(textureCoords, pos, values[1], values[2]);
            }
        }
        return textureCoords;
    }

    private float[] loadNormals(){
        float[] normals = new float[getNumNormals()*3];
        int pos = 0;
        for (String line : content) {
            String[] values = line.split(" ");
            if (isNormal(line)){
                pos = loadNormal(normals, pos, values[1], values[2], values[3]);
            }
        }
        return normals;
    }

    private void loadFaces(){
        loadVertices();
        float[] textureCoords = loadTextureCoords();
        float[] normals = loadNormals();
        int pos = 0;
        for (String line : content){
            String[] values = line.split(" ");
            if (isFace(line)){
                pos = loadFace(values, pos, textureCoords, normals);
            }
        }
    }

    private int loadVertex(float[] vertices, int pos, String... values){
        return addToArray(vertices,pos,values);
    }

    private int loadTextureCoord(float[] textureCoords, int pos, String... values){
        return addToArray(textureCoords,pos,values);
    }

    private int loadNormal(float[] normals, int pos, String... values){
        return addToArray(normals,pos,values);
    }

    private int loadFace(String[] values, int pos, float[] textureCoords, float[] normals){
        String[] faceVertex1 = values[1].split("/");
        int iv1 = toInt(faceVertex1[0]) - 1;
        int ivt1 = toInt(faceVertex1[1]) - 1;
        int ivn1 = toInt(faceVertex1[2]) - 1;
        for (int i = 1; i < values.length - 2; i++){
            String[] faceVertex2 = values[i+1].split("/");
            int iv2 = toInt(faceVertex2[0]) - 1;
            int ivt2 = toInt(faceVertex2[1]) - 1;
            int ivn2 = toInt(faceVertex2[2]) - 1;
            String[] faceVertex3 = values[i+2].split("/");
            int iv3 = toInt(faceVertex3[0]) - 1;
            int ivt3 = toInt(faceVertex3[1]) - 1;
            int ivn3 = toInt(faceVertex3[2]) - 1;
            pos = addToArray(indices,pos,iv1,iv2,iv3);
            copy(textureCoords,ivt1,this.textureCoords,iv1,2);
            copy(textureCoords,ivt2,this.textureCoords,iv2,2);
            copy(textureCoords,ivt3,this.textureCoords,iv3,2);
            copy(normals,ivn1,this.normals,iv1,3);
            copy(normals,ivn2,this.normals,iv2,3);
            copy(normals,ivn3,this.normals,iv3,3);
        }
        return pos;
    }

    private void copy(float[] in, int inPos, float[] out, int outPos, int size){
        for (int i = 0; i < size; i++) {
            out[outPos * 2] = in[inPos * 2 + i];
        }
    }

    private int getNumVertices(){
        int num = 0;
        for (String line : content) {
            if (isVertex(line)) {
                num++;
            }
        }
        return num;
    }

    private int getNumTextureCoords(){
        int num = 0;
        for (String line : content) {
            if (isTextureCoord(line)) {
                num++;
            }
        }
        return num;
    }

    private int getNumNormals(){
        int num = 0;
        for (String line : content) {
            if (isNormal(line)) {
                num++;
            }
        }
        return num;
    }

    private int getNumIndices(){
        int numIndices = 0;
        for (String line : content){
            if (isFace(line)){
                numIndices += ((line.split(" ").length - 1) - 2) * 3;
            }
        }
        return numIndices;
    }

    private boolean isVertex(String line){
        return line.startsWith("v ");
    }

    private boolean isTextureCoord(String line){
        return line.startsWith("vt ");
    }

    private boolean isNormal(String line){
        return line.startsWith("vn ");
    }

    private boolean isFace(String line){
        return line.startsWith("f ");
    }

    private float toFloat(String value){
        return Float.parseFloat(value);
    }

    private int toInt(String value){
        return Integer.parseInt(value);
    }

    private int addToArray(float[] array, int pos, String... values){
        for (String value : values){
            array[pos++] = toFloat(value);
        }
        return pos;
    }

    private int addToArray(int[] array, int pos, Integer... values){
        for (Integer value : values){
            array[pos++] = value;
        }
        return pos;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices() {
        return indices;
    }
}
