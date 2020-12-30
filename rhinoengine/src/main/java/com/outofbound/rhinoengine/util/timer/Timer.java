package com.outofbound.rhinoengine.util.timer;

public class Timer {

    private long ms;
    private long timer;

    public Timer(long ms){
        this.ms = ms;
        this.timer = 0;
    }

    public boolean isOver(long deltaMs){
        timer += deltaMs;
        if (timer >= ms){
            timer = 0;
            return true;
        }
        return false;
    }

    public long getMs(){
        return ms;
    }
}
