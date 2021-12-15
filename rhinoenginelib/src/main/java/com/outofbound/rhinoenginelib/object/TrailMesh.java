package com.outofbound.rhinoenginelib.object;

import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.timer.Timer;

public abstract class TrailMesh extends Mesh {

    private final Mesh source;
    private final Timer timer;
    private final float[] newLineVertices;
    private final float[] newLineNormals;
    private final float[] newColors;
    private final float[] color;
    private final int length;

    public TrailMesh(String name, Mesh source, float[] color, int length, long ms) {
        super(name, new float[]{},3,new float[]{},null,new float[]{});
        timer = new Timer(ms);
        newLineVertices = new float[2*getSizeVertex()];
        newLineNormals = new float[newLineVertices.length];
        newColors = new float[2*4];
        this.color = color;
        this.source = source;
        this.length = length;
    }

    private void addLine(){
        float[][] bbVertices = source.getBoundingBox().getVertices();
        newLine(bbVertices, newLineVertices, newLineNormals);
        addVertices(newLineVertices);
        addNormals(newLineNormals);
    }

    protected abstract void newLine(float[][] bbVertices, float[] newLineVertices, float[] newLineNormals);

    private void addColors(){
        newColors[0] = color[0];
        newColors[1] = color[1];
        newColors[2] = color[2];
        newColors[3] = 1f;
        newColors[4] = color[0];
        newColors[5] = color[1];
        newColors[6] = color[2];
        newColors[7] = 1f;
        addColors(newColors);
    }

    private int getNumLines(){
        return getNumVertices()/2;
    }

    private void removeLine(){
        removeVertices(0,2);
        removeColors(0,2);
    }

    @Override
    public void doTransformation(long ms) {
        if (timer.isOver(ms)){
            if (getNumLines() == length){
                removeLine();
            }
            addLine();
            addColors();
            int line = 0;
            for (int i = 0; i < getColors().length; i += 8){
                float alpha = line/(float)getNumLines();
                getColors()[i+3] = alpha;
                getColors()[i+7] = alpha;
                line++;
            }
            reloadVertices();
            reloadColors();
        }
    }
}