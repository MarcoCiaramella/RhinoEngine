package com.outofbound.rhinoengine.util.color;

import com.outofbound.rhinoengine.util.number.Numbers;
import com.outofbound.rhinoengine.util.vertex.Vertex;

import java.util.Arrays;

public class Color {

    public static float[] getVertexColor(float[] color, int verticesLength){
        // set color for each vertex
        float[] vertexColor = new float[(verticesLength/3)*4];
        for (int i=0; i<vertexColor.length; i+=4){
            vertexColor[i] = color[0];
            vertexColor[i+1] = color[1];
            vertexColor[i+2] = color[2];
            vertexColor[i+3] = color[3];
        }
        return vertexColor;
    }

    public static float[] randomColor(float min, float max, float alpha){
        float[] color = new float[4];
        color[0] = Numbers.randomFloat(min,max);
        color[1] = Numbers.randomFloat(min,max);
        color[2] = Numbers.randomFloat(min,max);
        color[3] = alpha;
        return color;
    }

    public static int alphaI(int color){
        return (color >> 24) & 0xff;
    }

    public static int redI(int color){
        return (color >> 16) & 0xff;
    }

    public static int greenI(int color){
        return (color >> 8) & 0xff;
    }

    public static int blueI(int color){
        return color & 0xff;
    }

    public static float alphaF(int color){
        return alphaI(color) / 255f;
    }

    public static float redF(int color){
        return redI(color) / 255f;
    }

    public static float greenF(int color){
        return greenI(color) / 255f;
    }

    public static float blueF(int color){
        return blueI(color) / 255f;
    }

    public static int colorInt(int a, int r, int g, int b){
        return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    private static void coloringVertex(float[] colors, int index, int color){
        colors[index*4] = redF(color);
        colors[index*4+1] = greenF(color);
        colors[index*4+2] = blueF(color);
        colors[index*4+3] = alphaF(color);
    }

    private static void coloringTriangle(float[] colors, int index1, int index2, int index3, int color){
        coloringVertex(colors,index1,color);
        coloringVertex(colors,index2,color);
        coloringVertex(colors,index3,color);
    }

    public static int mixColor(int colorMin, int colorMax, float coeff){
        float c1 = 1f - coeff;
        float c2 = coeff;
        int a = (int) (alphaI(colorMin)*c1 + alphaI(colorMax)*c2);
        int r = (int) (redI(colorMin)*c1 + redI(colorMax)*c2);
        int g = (int) (greenI(colorMin)*c1 + greenI(colorMax)*c2);
        int b = (int) (blueI(colorMin)*c1 + blueI(colorMax)*c2);
        return colorInt(a,r,g,b);
    }

    public static float[] gradientColoring(float[] vertices, int[] indices, Gradient[] gradients){
        float[] colors = new float[(vertices.length/3)*4];
        Arrays.fill(colors, 1f);
        float zMax = Vertex.getMaxZInVertices(vertices);
        for (int i = 0; i < indices.length; i += 3){
            float z01 = Vertex.getMaxZInTriangle(vertices,indices[i],indices[i+1],indices[i+2]) / zMax;
            for (Gradient gradient : gradients) {
                if (z01 >= gradient.min && z01 <= gradient.max) {
                    float dist = gradient.max - gradient.min;
                    float coeff = dist > 0f ? (z01 - gradient.min) / dist : 0f;
                    int color = mixColor(gradient.colorMin,gradient.colorMax,coeff);
                    coloringTriangle(colors,indices[i],indices[i+1],indices[i+2],color);
                    break;
                }
            }
        }
        return colors;
    }
}
