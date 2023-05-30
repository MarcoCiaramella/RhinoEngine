package com.outofbound.rhinoenginelib.util.color;

import com.outofbound.rhinoenginelib.util.number.Numbers;
import com.outofbound.rhinoenginelib.util.vertex.Vertex;

import java.util.Arrays;

public class Color {

    private static boolean isNotIn01(float value){
        return !(value >= 0f) || !(value <= 1f);
    }

    public static float[] getVertexColor(float[] color, int verticesLength){
        if (isNotIn01(color[0]) || isNotIn01(color[1]) || isNotIn01(color[2]) || isNotIn01(color[3])){
            throw new IllegalArgumentException("color component must be >= 0.0f and <= 1.0f");
        }
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
        if (isNotIn01(min)){
            throw new IllegalArgumentException("min must be >= 0.0f and <= 1.0f");
        }
        if (isNotIn01(max)){
            throw new IllegalArgumentException("max must be >= 0.0f and <= 1.0f");
        }
        if (isNotIn01(alpha)){
            throw new IllegalArgumentException("alpha must be >= 0.0f and <= 1.0f");
        }
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
        if (isNotIn01(coeff)){
            throw new IllegalArgumentException("coeff must be >= 0.0f and <= 1.0f");
        }
        float c1 = 1f - coeff;
        float c2 = coeff;
        int a = (int) (alphaI(colorMin)*c1 + alphaI(colorMax)*c2);
        int r = (int) (redI(colorMin)*c1 + redI(colorMax)*c2);
        int g = (int) (greenI(colorMin)*c1 + greenI(colorMax)*c2);
        int b = (int) (blueI(colorMin)*c1 + blueI(colorMax)*c2);
        return colorInt(a,r,g,b);
    }
}
