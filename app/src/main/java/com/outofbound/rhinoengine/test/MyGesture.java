package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.gesture.Gesture;

class MyGesture implements Gesture {
    @Override
    public void onClick(float x, float y) {

    }

    @Override
    public void onRelease(float x, float y) {

    }

    @Override
    public void onMove(float x, float y, float velX, float velY) {
        if (Math.abs(velX) >= Math.abs(velY)) {
            Engine.getInstance().getCamera().getEye().rotate(-velX * 10, 0, 1, 0);
        }
        else if (Math.abs(velY) > Math.abs(velX)) {
            Engine.getInstance().getCamera().getEye().rotate(-velY * 10, 1, 0, 0);
        }
    }

    @Override
    public void onScale(float scaleFactor) {
        Engine.getInstance().getCamera().getEye().multS(scaleFactor);
    }
}
