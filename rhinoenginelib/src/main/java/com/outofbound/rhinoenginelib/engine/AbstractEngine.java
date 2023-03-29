package com.outofbound.rhinoenginelib.engine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.gesture.Gesture;
import com.outofbound.rhinoenginelib.light.Lights;
import com.outofbound.rhinoenginelib.mesh.AABB;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.collision.Collider;
import com.outofbound.rhinoenginelib.renderer.SceneRenderer;
import com.outofbound.rhinoenginelib.renderer.BlurRenderer;
import com.outofbound.rhinoenginelib.task.Task;
import com.outofbound.rhinoenginelib.util.map.SyncMap;

import java.util.ArrayList;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * The engine abstract class.
 */
public abstract class AbstractEngine extends GLSurfaceView implements GLSurfaceView.Renderer, OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private Gesture gesture;
    private final float[] clearColor = {0,0,0,1};
    private long ms = -1;
    private Camera camera;
    private long deltaMs;
    private ScaleGestureDetector scaleDetector;
    private boolean gestureProcessed = false;
    private static AbstractEngine instance;
    private BlurRenderer blurRenderer;
    private boolean blurEnabled = false;
    private SceneRenderer sceneRenderer;
    private final SyncMap<Task> taskMap = new SyncMap<>();



    /**
     * The engine constructor.
     * @param context the context for this view
     * @param camera the Camera
     * @param gesture a Gesture
     */
    public AbstractEngine(Context context, Camera camera, Gesture gesture){
        super(context);
        config(camera, gesture);
    }

    /**
     * The engine constructor.
     * @param context the context for this view
     * @param attrs the object AttributeSet
     * @param camera the Camera
     * @param gesture a Gesture
     */
    public AbstractEngine(Context context, AttributeSet attrs, Camera camera, Gesture gesture){
        super(context,attrs);
        config(camera, gesture);
    }

    private void config(Camera camera, Gesture gesture){
        instance = this;
        setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE | SYSTEM_UI_FLAG_FULLSCREEN);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setOnTouchListener(this);
        this.camera = camera;
        this.gesture = gesture;
        this.scaleDetector = new ScaleGestureDetector(getContext(), this);
    }

    /**
     * Adds a Task.
     * @param task the Task to add
     */
    public void addTask(Task task){
        task.onAdd();
        taskMap.put(task.getName(), task);
    }

    /**
     * Removes the Task with the specified name.
     * @param name the name of the Task
     * @return this AbstractEngine
     */
    public AbstractEngine removeTask(String name){
        Task task = taskMap.remove(name);
        if (task != null){
            task.onRemove();
        }
        return this;
    }

    /**
     * Returns Task with the specified name.
     * @param name the name of the Task
     * @return the Task found
     */
    public Task getTask(String name){
        return taskMap.get(name);
    }

    /**
     * Sets a Gesture object.
     * @param gesture the Gesture object
     * @return this engine
     */
    public AbstractEngine setGesture(Gesture gesture){
        this.gesture = gesture;
        return this;
    }

    /**
     * Returns the Gesture.
     * @return the Gesture
     */
    public Gesture getGesture(){
        return gesture;
    }

    /**
     * Calls super.onPause().
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Calls super.onResume().
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Returns always true.
     * @return true
     */
    @Override
    public boolean performClick(){
        return true;
    }

    private boolean checkMotionEvent(MotionEvent event){
        int numPointer = event.getPointerCount();
        int pointerIndex = event.getActionIndex();
        if (pointerIndex >= numPointer){
            return false;
        }
        int pointerId = event.getPointerId(pointerIndex);
        return pointerId < numPointer;
    }

    /**
     * Handles on touch events.
     * @param v the view touched
     * @param event the MotionEvent object
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gesture != null) {
            if (!checkMotionEvent(event)){
                return true;
            }
            v.performClick();
            int pointerIndex = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(pointerIndex);
            float posX = event.getX(pointerId);
            float posY = getHeight() - event.getY(pointerId);
            scaleDetector.onTouchEvent(event);
            if (gestureProcessed){
                gestureProcessed = false;
                return true;
            }
            switch (action) {
                case MotionEvent.ACTION_MOVE: {
                    VelocityTracker vt = VelocityTracker.obtain();
                    vt.addMovement(event);
                    vt.computeCurrentVelocity(1);
                    float velX = vt.getXVelocity();
                    float velY = vt.getYVelocity();
                    gesture.onMove(posX, posY, velX, velY);
                    vt.recycle();
                }
                break;
                case MotionEvent.ACTION_DOWN: {
                    gesture.onClick(posX, posY);
                }
                break;
                case MotionEvent.ACTION_UP: {
                    gesture.onRelease(posX, posY);
                }
                break;
                default: {
                    return v.onTouchEvent(event);
                }
            }
        }
        return true;
    }

    /**
     * Scale event handler.
     * @param detector the scale detector object
     * @return true
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (gesture != null) {
            gesture.onScale(detector.getScaleFactor());
            gestureProcessed = true;
        }
        invalidate();
        return true;
    }

    /**
     * Scale begin handler.
     * @param detector the scale detector object
     * @return true
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        gestureProcessed = true;
        return true;
    }

    /**
     * Scale end handler.
     * @param detector the scale detector object
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        gestureProcessed = true;
    }

    /**
     * Prepares engine for rendering.
     * @param gl the OpenGL 1.0 object
     * @param config the EGLConfig object
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glClearDepthf(1.0f);

        sceneRenderer = new SceneRenderer();
        blurRenderer = new BlurRenderer(sceneRenderer);
        init();
    }

    /**
     * Changes viewport when surface is changed.
     * @param unused unused
     * @param width new surface width
     * @param height new surface height
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        camera.setWidth(width).setHeight(height);
    }

    /**
     * Draws the OpenGL frame.
     * @param gl the OpenGL 1.0 object
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        deltaMs = 0;
        long currentMs = Calendar.getInstance().getTimeInMillis();
        if (ms >= 0) {
            deltaMs = currentMs - ms;
        }

        if (blurEnabled){
            blurRenderer.doRendering(getWidth(), getHeight(), camera, deltaMs);
        }
        else {
            sceneRenderer.doRendering(getWidth(), getHeight(), camera, deltaMs);
        }

        for (String name : sceneRenderer.getMeshes().keySet()) {
            Mesh mesh = getMesh(name);
            if (mesh != null && mesh.isCollisionEnabled() && mesh.hasAABB()) {
                if (mesh.updateAABB()) Collider.update(mesh.getAABBGrid());
            }
        }
        for (String name : sceneRenderer.getMeshes().keySet()) {
            Mesh mesh = getMesh(name);
            if (mesh != null && mesh.isCollisionEnabled() && mesh.hasAABB()) {
                processCollision(mesh);
            }
        }

        for (String name : taskMap.keySet()){
            Task task = getTask(name);
            if (task == null) {
                taskMap.remove(name);
                continue;
            }
            boolean alive = task.runTask(deltaMs);
            if (!alive) {
                removeTask(name);
            }
        }

        ms = currentMs;

    }

    private void processCollision(Mesh mesh) {
        Collider.remove(mesh.getAABBGrid());
        for (AABB aabb : mesh.getAABBGrid()) {
            ArrayList<AABB> result = Collider.query(aabb);
            if (result == null) continue;
            for (AABB aabb2 : result) {
                if (aabb.getBoundingBox().intersects(aabb2.getBoundingBox())) {
                    mesh.onCollision(aabb2);
                }
            }
        }
        Collider.add(mesh.getAABBGrid());
    }

    /**
     * Sets the clear color for GLES20.glClearColor method.
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     */
    public void setClearColor(float r, float g, float b, float a){
        this.clearColor[0] = r;
        this.clearColor[1] = g;
        this.clearColor[2] = b;
        this.clearColor[3] = a;
    }

    /**
     * Returns the Camera.
     * @return the Camera
     */
    public Camera getCamera(){
        return camera;
    }

    /**
     * Gets this AbstractEngine.
     * @return the instance
     */
    public static AbstractEngine getInstance(){
        return instance;
    }

    /**
     * Initializes the engine.
     */
    protected abstract void init();

    /**
     * Enables blur effect.
     * @return this AbstractEngine
     */
    public AbstractEngine enableBlur(){
        blurEnabled = true;
        return this;
    }

    /**
     * Disables blur effect.
     * @return this AbstractEngine
     */
    public AbstractEngine disableBlur(){
        blurEnabled = false;
        return this;
    }

    /**
     * Returns frame rate.
     * @return frame rate
     */
    public int getFPS(){
        if (deltaMs == 0){
            return 0;
        }
        return (int) (1f/(deltaMs/1000f));
    }

    public void addMesh(Mesh mesh){
        sceneRenderer.addMesh(mesh);
    }

    public AbstractEngine removeMesh(String name){
        sceneRenderer.removeMesh(name);
        return this;
    }

    public Mesh getMesh(String name){
        return sceneRenderer.getMesh(name);
    }

    public Lights getLights(){
        return sceneRenderer.getLights();
    }

}
