package com.outofbound.rhinoenginelib.util.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SyncMap<V> {

    private final HashMap<String,V> hashMap;
    private final ArrayList<String> keySet;

    public SyncMap(){
        hashMap = new HashMap<>();
        keySet = new ArrayList<>();
    }

    public synchronized void put(String name, V v){
        hashMap.put(name, v);
    }

    public synchronized V get(String name){
        return hashMap.get(name);
    }

    public synchronized V remove(String name){
        return hashMap.remove(name);
    }

    public synchronized ArrayList<String> keySet(){
        keySet.clear();
        keySet.addAll(hashMap.keySet());
        return keySet;
    }
}
