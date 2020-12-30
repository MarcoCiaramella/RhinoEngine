package com.outofbound.rhinoengine;


import com.outofbound.rhinoengine.light.GLLights;
import com.outofbound.rhinoengine.util.vector.Vector3f;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class GLLightsUnitTest {


    @Test
    public void getPositions_isCorrect(){
        GLLights glLights = new GLLights(3);
        glLights.add(0,new Vector3f(1,2,3),new Vector3f(1,1,1),0);
        glLights.add(1,new Vector3f(4,5,6),new Vector3f(1,1,1),0);
        glLights.add(2,new Vector3f(7,8,9),new Vector3f(1,1,1),0);
        float[] expected = {1,2,3,4,5,6,7,8,9};
        assertArrayEquals(expected,glLights.getPositions(),0);
    }

    @Test
    public void remove_isCorrect(){
        GLLights glLights = new GLLights(5);
        glLights.add(0,new Vector3f(1,2,3),new Vector3f(1,1,1),0);
        glLights.add(1,new Vector3f(4,5,6),new Vector3f(1,1,1),0);
        glLights.add(2,new Vector3f(7,8,9),new Vector3f(1,1,1),0);
        glLights.add(3,new Vector3f(10,11,12),new Vector3f(1,1,1),0);
        glLights.remove(1);
        glLights.remove(2);
        float[] expected1 = {1,2,3,10,11,12,0,0,0,0,0,0,0,0,0};
        assertArrayEquals(expected1,glLights.getPositions(),0);
        glLights.remove(0);
        float[] expected2 = {10,11,12,0,0,0,0,0,0,0,0,0,0,0,0};
        assertArrayEquals(expected2,glLights.getPositions(),0);
    }

    @Test
    public void addAndRemove_isCorrect(){
        GLLights glLights = new GLLights(3);
        glLights.add(0,new Vector3f(1,2,3),new Vector3f(1,1,1),0);
        glLights.add(1,new Vector3f(4,5,6),new Vector3f(1,1,1),0);
        glLights.add(2,new Vector3f(7,8,9),new Vector3f(1,1,1),0);
        glLights.remove(1);
        glLights.add(1,new Vector3f(10,11,12),new Vector3f(1,1,1),0);
        float[] expected = {1,2,3,10,11,12,7,8,9};
        assertArrayEquals(expected,glLights.getPositions(),0);
    }

    @Test
    public void size_isCorrect(){
        GLLights glLights = new GLLights(4);
        glLights.add(0,new Vector3f(1,2,3),new Vector3f(1,1,1),0);
        glLights.add(1,new Vector3f(4,5,6),new Vector3f(1,1,1),0);
        glLights.add(2,new Vector3f(7,8,9),new Vector3f(1,1,1),0);
        glLights.remove(1);
        assertEquals(2,glLights.size());
        glLights.add(1,new Vector3f(10,11,12),new Vector3f(1,1,1),0);
        assertEquals(3,glLights.size());
        glLights.add(3,new Vector3f(13,14,15),new Vector3f(1,1,1),0);
        assertEquals(4,glLights.size());
    }
}
