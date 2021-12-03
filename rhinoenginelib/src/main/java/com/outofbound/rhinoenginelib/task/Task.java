package com.outofbound.rhinoenginelib.task;

import androidx.annotation.CallSuper;


public abstract class Task {

    private String name;
    private boolean dead;

    /**
     * Instantiate a Task.
     * @param name a name for this Task
     */
    public Task(String name){
        this.name = name;
        dead = false;
    }

    /**
     * Called when this Task is added to AbstractEngine.
     */
    @CallSuper
    public void onAdd(){
        dead = false;
    }

    /**
     * Called when this Task is removed from AbstractEngine.
     */
    @CallSuper
    public void onRemove(){
        dead = true;
    }

    /**
     * Run sync task.
     * @param ms the engine time in milliseconds.
     * @return true if alive, false otherwise.
     */
    public abstract boolean runTask(long ms);

    /**
     * Kill this Task. It will be removed from AbstractEngine.
     */
    public void kill(){
        dead = true;
    }

    /**
     * Return true if this Task is dead.
     * @return true if this Task is dead, false otherwise.
     */
    public boolean isDead(){
        return dead;
    }

    public String getName(){
        return name;
    }

}
