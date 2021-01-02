package com.outofbound.rhinoenginelib.object;

import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.util.timer.Timer;

public abstract class TrailMesh extends GLMesh {

    private GLMesh source;
    private Timer timer;
    private float[] newLine;
    private float[] newColors;
    private float[] color;
    private int length;

    public TrailMesh(GLMesh source, float[] color, int length, long ms) {
        super(new float[]{},3,null,null,new float[]{});
        timer = new Timer(ms);
        newLine = new float[2*getSizeVertex()];
        newColors = new float[2*4];
        this.color = color;
        this.source = source;
        this.length = length;
    }

    @Override
    public void onAdd(){
        super.onAdd();
        removeAllLines();
        timer = new Timer(timer.getMs());
    }

    @Override
    public void move(long ms) {
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

    private void addLine(){
        float[][] bbVertices = source.getBoundingBox().getVertices();
        newLine(bbVertices, newLine);
        addVertices(newLine);
    }

    protected abstract void newLine(float[][] bbVertices, float[] newLine);

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

    private void removeAllLines(){
        int numVertices = getNumVertices();
        removeVertices(0,numVertices);
        removeColors(0,numVertices);
    }

}