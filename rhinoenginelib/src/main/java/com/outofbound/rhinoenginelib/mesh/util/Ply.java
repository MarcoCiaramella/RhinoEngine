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
            if (values.length == 9 || values.length == 10) {
                // example: 1.011523 2.923269 0.741561 0.143873 -0.582903 0.799703 154 181 0
                numVertices++;
            }
        }
        return numVertices;
    }

    private int getNumIndices(String[] data){
        int numIndices = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (values.length == 5) {
                // example: 4 0 1 2 3
                if (values[0].compareTo("4") == 0) {
                    // quad split in two triangles
                    numIndices += 2;
                }
                else if (values[0].compareTo("3") == 0) {
                    numIndices++;
                }
            }
        }
        return numIndices;
    }

    private void loadVertices(String[] data) {
        int i = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (values.length == 9 || values.length == 10) {
                // example: 1.011523 2.923269 0.741561 0.143873 -0.582903 0.799703 154 181 0
                float v1 = Float.parseFloat(values[0]);
                float v2 = Float.parseFloat(values[1]);
                float v3 = Float.parseFloat(values[2]);
                vertices[i++] = v1;
                vertices[i++] = v2;
                vertices[i++] = v3;
            }
        }
    }

    private void loadNormals(String[] data){
        int i = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (values.length == 9 || values.length == 10) {

                float n1 = Float.parseFloat(values[3]);
                float n2 = Float.parseFloat(values[4]);
                float n3 = Float.parseFloat(values[5]);
                normals[i++] = n1;
                normals[i++] = n2;
                normals[i++] = n3;
            }
        }
    }

    private void loadColors(String[] data){
        int i = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (values.length == 9 || values.length == 10) {
                float c1 = Float.parseFloat(values[6]) / 255f;
                float c2 = Float.parseFloat(values[7]) / 255f;
                float c3 = Float.parseFloat(values[8]) / 255f;
                float c4 = 1f;
                if (values.length == 10) {
                    c4 = Float.parseFloat(values[9]) / 255f;
                }
                colors[i++] = c1;
                colors[i++] = c2;
                colors[i++] = c3;
                colors[i++] = c4;
            }
        }
    }

    private void loadTextureCoords(String[] data){
        int i = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (values.length == 9 || values.length == 10) {
                float uv1 = Float.parseFloat(values[6]);
                float uv2 = Float.parseFloat(values[7]);
                uvs[i++] = uv1;
                uvs[i++] = uv2;
            }
        }
    }

    private void loadIndices(String[] data){
        int i = 0;
        for (String line : data) {
            String[] values = line.split(" ");
            if (values.length == 5) {
                // example: 4 0 1 2 3
                int num = Integer.parseInt(values[0]);
                if (num == 3) {
                    // triangle
                    int i1 = Integer.parseInt(values[1]);
                    int i2 = Integer.parseInt(values[2]);
                    int i3 = Integer.parseInt(values[3]);
                    indices[i++] = i1;
                    indices[i++] = i2;
                    indices[i++] = i3;
                } else if (num == 4) {
                    // quad split in two triangles.
                    // quad 1 2 3 4
                    //==>
                    //triangle 1 2 3
                    //triangle 3 4 1
                    int i1 = Integer.parseInt(values[1]);
                    int i2 = Integer.parseInt(values[2]);
                    int i3 = Integer.parseInt(values[3]);
                    int i4 = Integer.parseInt(values[4]);
                    indices[i++] = i1;
                    indices[i++] = i2;
                    indices[i++] = i3;
                    indices[i++] = i3;
                    indices[i++] = i4;
                    indices[i++] = i1;
                }
            }
        }
    }
}
