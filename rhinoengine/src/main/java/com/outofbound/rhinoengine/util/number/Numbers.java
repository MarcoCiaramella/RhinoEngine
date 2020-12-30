package com.outofbound.rhinoengine.util.number;

import java.util.Random;

public class Numbers {

    private static Random random = new Random();

    public static float mapValue(float val, float min, float max){
        return min+(max - min)*val;
    }

    public static long mapValue(float val, long min, long max){
        return (long)(min+(max - min)*val);
    }

    public static float randomFloat(float min, float max){
        return mapValue(random.nextFloat(),min,max);
    }

    public static int randomInt(int min, int max){
        int val = min + random.nextInt(max-min+1);
        return val;
    }

    public static long randomLong(long min, long max){
        return mapValue(random.nextFloat(),min,max);
    }
}
