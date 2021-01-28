package com.outofbound.rhinoenginelib.mesh.util;

import android.content.Context;

import com.outofbound.rhinoenginelib.util.file.TextFileReader;


public class Ply {

    private float[] vertices = null;
    private float[] normals = null;
    private float[] colors = null;
    private float[] uvs = null;
    private int[] indices = null;
    private final Context context;
    private final String file;

    public Ply(Context context, String file){
        this.context = context;
        this.file = file;
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

    public float[] getUvs(){
        return uvs;
    }

    public int[] getIndices(){
        return indices;
    }

    public void load(){
        String[] content = TextFileReader.getString(context, file).split("end_header\n");
        String[] header = content[0].split("\n");
        String[] data = content[1].split("\n");

        int numVertices = getNumVertices(data);
        int numIndices = getNumIndices(data);
        vertices = new float[numVertices*3];
        normals = new float[numVertices*3];
        if (isNotTextured(header))
            colors = new float[numVertices*4];
        else
            uvs = new float[numVertices*2];
        indices = new int[numIndices*3];
        loadVertices(data);
        loadNormals(data);
        if (isNotTextured(header)) {
            loadColors(data);
        }
        else {
            loadTextureCoords(data);
        }
        loadIndices(data);
    }

    private boolean isNotTextured(String[] header){
        for (String line : header) {
            if (line.compareTo("property float s") == 0 || line.compareTo("property float t") == 0) {
                return false;
            }
        }
        return true;
    }

    private int getNumVertices(String[] data){
        int numVertices = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (isFloat(values[0])) {
                numVertices++;
            }
        }
        return numVertices;
    }

    private int getNumIndices(String[] data){
        int numIndices = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (!isFloat(values[0])) {
                int num = Integer.parseInt(values[0]);
                for (int i = 1; i < num - 1; i++){
                    numIndices += 3;
                }
            }
        }
        return numIndices;
    }

    private void loadVertices(String[] data) {
        int pos = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (isFloat(values[0])) {
                pos = addToArray(vertices,pos,values[0],values[1],values[2]);
            }
        }
    }

    private void loadNormals(String[] data){
        int pos = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (isFloat(values[0])) {
                pos = addToArray(normals,pos,values[3],values[4],values[5]);
            }
        }
    }

    private void loadColors(String[] data){
        int pos = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (isFloat(values[0])) {
                float c1 = Float.parseFloat(values[6]) / 255f;
                float c2 = Float.parseFloat(values[7]) / 255f;
                float c3 = Float.parseFloat(values[8]) / 255f;
                float c4 = 1f;
                if (values.length == 10) {
                    c4 = Float.parseFloat(values[9]) / 255f;
                }
                pos = addToArray(colors,pos,c1,c2,c3,c4);
            }
        }
    }

    private void loadTextureCoords(String[] data){
        int pos = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (isFloat(values[0])) {
                pos = addToArray(uvs,pos,values[6],values[7]);
            }
        }
    }

    private void loadIndices(String[] data){
        int pos = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (!isFloat(values[0])) {
                int num = Integer.parseInt(values[0]);
                for (int i = 1; i < num - 1; i++){
                    pos = addToArray(indices,pos,values[1],values[i+1],values[i+2]);
                }
            }
        }
    }

    private int addToArray(float[] arr, int pos, String... values){
        for (String value : values){
            arr[pos++] = Float.parseFloat(value);
        }
        return pos;
    }

    private int addToArray(float[] arr, int pos, float... values){
        for (float value : values){
            arr[pos++] = value;
        }
        return pos;
    }

    private int addToArray(int[] arr, int pos, String... values){
        for (String value : values){
            arr[pos++] = Integer.parseInt(value);
        }
        return pos;
    }

    private boolean isFloat(String value){
        return value.contains(".");
    }
}
