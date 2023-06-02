package com.outofbound.rhinoenginelib.gesture;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;

public class RotateScaleGesture implements Gesture {
    @Override
    public void onClick(float x, float y) {

    }

    @Override
    public void onRelease(float x, float y) {

    }

    @Override
    public void onMove(float x, float y, float velX, float velY) {
        if (Math.abs(velX) >= Math.abs(velY)) {
            AbstractEngine.getInstance().getCamera().getEye().rotate(-velX * 10, 0, 1, 0);
        }
        else if (Math.abs(velY) > Math.abs(velX)) {
            AbstractEngine.getInstance().getCamera().getEye().rotate(-velY * 10, 1, 0, 0);
        }
    }

    @Override
    public void onScale(float scaleFactor) {
        AbstractEngine.getInstance().getCamera().getEye().multS(scaleFactor);
    }
}
