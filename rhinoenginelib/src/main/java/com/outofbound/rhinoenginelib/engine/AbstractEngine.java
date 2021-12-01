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
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.SceneRenderer;
import com.outofbound.rhinoenginelib.renderer.fx.Blur;
import com.outofbound.rhinoenginelib.task.Task;
import com.outofbound.rhinoenginelib.util.list.BigList;

import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * The engine abstract class.
 */
public abstract class AbstractEngine extends GLSurfaceView implements GLSurfaceView.Renderer, OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private final BigList<com.outofbound.rhinoenginelib.renderer.Renderer> renderers = new BigList<>();
    private final BigList<Task> tasks = new BigList<>();
    private Gesture gesture = null;
    private final float[] clearColor = {0,0,0,1};
    private long ms = -1;
    private Camera camera;
    private long deltaMs;
    private ScaleGestureDetector scaleDetector;
    private boolean gestureProcessed = false;
    private static AbstractEngine instance;
    private Blur blur = null;
    private SceneRenderer sceneRenderer;
    private boolean blurEnabled = false;



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
        setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE | SYSTEM_UI_FLAG_FULLSCREEN);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setOnTouchListener(this);
        this.camera = camera;
        this.gesture = gesture;
        this.sceneRenderer = this::renderScene;
        this.scaleDetector = new ScaleGestureDetector(getContext(), this);
        instance = this;
    }

    /**
     * Adds a Renderer.
     * @param renderer the Renderer to add
     * @return the Renderer id, -1 if the input Renderer is a duplicate
     */
    public int addRenderer(com.outofbound.rhinoenginelib.renderer.Renderer renderer){
        return renderers.add(renderer);
    }

    /**
     * Adds a Task.
     * @param task the Task to add
     * @return the Task id, -1 if the input Task is a duplicate
     */
    public int addTask(Task task){
        task.onAdd();
        return tasks.add(task);
    }

    /**
     * Removes the Renderer with the specified id.
     * @param id the id of Renderer to remove
     * @return this AbstractEngine
     */
    public AbstractEngine removeRenderer(int id){
        renderers.remove(id);
        return this;
    }

    /**
     * Removes the Task with the specified id.
     * @param id the id of Task to remove
     * @return this AbstractEngine
     */
    public AbstractEngine removeTask(int id){
        Task task = tasks.remove(id);
        if (task != null){
            task.onRemove();
        }
        return this;
    }

    /**
     * Returns the Renderer with input id.
     * @param id the id
     * @return the Renderer if exists, null otherwise
     */
    public com.outofbound.rhinoenginelib.renderer.Renderer getRenderer(int id){
        return renderers.get(id);
    }

    /**
     * Returns Task with the input id.
     * @param id the id
     * @return the Task if exists, null otherwise
     */
    public Task getTask(int id){
        return tasks.get(id);
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
        GLES20.glFrontFace(GLES20.GL_CCW);

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

        if (blur != null && blurEnabled){
            blur.render(sceneRenderer,getWidth(),getHeight());
        }
        else {
            sceneRenderer.doRendering(camera);
        }

        for (Task task : tasks){
            boolean alive = task.runTask(deltaMs);
            if (!alive) {
                tasks.remove(task);
            }
        }

        ms = currentMs;

    }

    private void renderScene(Camera camera){
        camera.loadVpMatrix();
        for (com.outofbound.rhinoenginelib.renderer.Renderer renderer : renderers){
            renderer.render(getWidth(), getHeight(), camera, deltaMs);
        }
    }

    /**
     * Sets the clear color for GLES20.glClearColor method.
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     */
    public synchronized void setClearColor(float r, float g, float b, float a){
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
     * Configures blur effect.
     * @param resolution the quality level. Must be RendererOnTexture.RESOLUTION_256, RendererOnTexture.RESOLUTION_512 or RendererOnTexture.RESOLUTION_1024.
     * @return this AbstractEngine
     */
    public AbstractEngine configBlur(int resolution){
        blur = new Blur(new RendererOnTexture(resolution,camera));
        return this;
    }

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

}
