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

    /*
    Read PLY text-based file format. Load vertices, normals, colors and indices values.
     */
    public void load(){
        int numVertices = 0;
        int numIndices = 0;
        boolean textured = false;
        String[] content = TextFileReader.getString(context, file).split("end_header\n");
        String[] header = content[0].split("\n");
        // parse header
        for(String line : header) {
            if(line.compareTo("property float s") == 0 || line.compareTo("property float t") == 0){
                // textured mesh
                textured = true;
            }
        }
        String[] data = content[1].split("\n");
        // initial data parse
        for(String line : data) {
            String[] values = line.split(" ");
            if (values.length == 9 || values.length == 10) {
                // example: 1.011523 2.923269 0.741561 0.143873 -0.582903 0.799703 154 181 0
                numVertices++;
            } else {
                // example: 4 0 1 2 3
                if(values[0].compareTo("4") == 0)
                    // quad split in two triangles
                    numIndices+=2;
                else if(values[0].compareTo("3") == 0)
                    numIndices++;
                else
                    return;
            }
        }
        vertices = new float[numVertices*3];
        normals = new float[numVertices*3];
        if(!textured)
            colors = new float[numVertices*4];
        else
            uvs = new float[numVertices*2];
        indices = new int[numIndices*3];
        int indexV = 0;
        int indexN = 0;
        int indexC = 0;
        int indexUV = 0;
        int indexI = 0;
        for(String line : data){
            String[] values = line.split(" ");
            if(values.length == 9 || values.length == 10) {
                // example: 1.011523 2.923269 0.741561 0.143873 -0.582903 0.799703 154 181 0
                float v1 = Float.parseFloat(values[0]);
                float v2 = Float.parseFloat(values[1]);
                float v3 = Float.parseFloat(values[2]);
                vertices[indexV++] = v1;
                vertices[indexV++] = v2;
                vertices[indexV++] = v3;

                float n1 = Float.parseFloat(values[3]);
                float n2 = Float.parseFloat(values[4]);
                float n3 = Float.parseFloat(values[5]);
                normals[indexN++] = n1;
                normals[indexN++] = n2;
                normals[indexN++] = n3;

                if(!textured) {
                    float c1 = Float.parseFloat(values[6]) / 255f;
                    float c2 = Float.parseFloat(values[7]) / 255f;
                    float c3 = Float.parseFloat(values[8]) / 255f;
                    float c4 = 1f;
                    if (values.length == 10) {
                        c4 = Float.parseFloat(values[9]) / 255f;
                    }
                    colors[indexC++] = c1;
                    colors[indexC++] = c2;
                    colors[indexC++] = c3;
                    colors[indexC++] = c4;
                }
                else{
                    float uv1 = Float.parseFloat(values[6]);
                    float uv2 = Float.parseFloat(values[7]);
                    uvs[indexUV++] = uv1;
                    uvs[indexUV++] = uv2;
                }
            }
            else{
                // example: 4 0 1 2 3
                int num = Integer.parseInt(values[0]);
                if(num == 3){
                    // triangle
                    int i1 = Integer.parseInt(values[1]);
                    int i2 = Integer.parseInt(values[2]);
                    int i3 = Integer.parseInt(values[3]);
                    indices[indexI++] = i1;
                    indices[indexI++] = i2;
                    indices[indexI++] = i3;
                }
                else if(num == 4){
                    // quad split in two triangles.
                    // quad 1 2 3 4
                    //==>
                    //triangle 1 2 3
                    //triangle 3 4 1
                    int i1 = Integer.parseInt(values[1]);
                    int i2 = Integer.parseInt(values[2]);
                    int i3 = Integer.parseInt(values[3]);
                    int i4 = Integer.parseInt(values[4]);
                    indices[indexI++] = i1;
                    indices[indexI++] = i2;
                    indices[indexI++] = i3;
                    indices[indexI++] = i3;
                    indices[indexI++] = i4;
                    indices[indexI++] = i1;
                }
            }
        }
    }
}
