package com.outofbound.rhinoenginelib.util.list;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class BigList<E> implements Iterable<E>, Iterator<E> {

    private final ArrayList<E> list = new ArrayList<>();
    private final ArrayList<Integer> indices = new ArrayList<>();
    private int count = 0;

    public int add(E object){
        if (list.contains(object)){
            return -1;
        }
        list.add(object);
        int index = list.indexOf(object);
        indices.add(index);
        return index;
    }

    public E remove(int index){
        if (contains(index)) {
            E object = get(index);
            list.set(index, null);
            indices.remove((Integer) index);
            return object;
        }
        return null;
    }

    public E remove(E object){
        return remove(list.indexOf(object));
    }

    public E get(int index){
        if (contains(index)){
            return list.get(index);
        }
        return null;
    }

    private boolean contains(int index){
        return indices.contains(index);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        count = 0;
        return this;
    }

    @Override
    public boolean hasNext() {
        return count < indices.size();
    }

    @Override
    public E next() {
        return list.get(indices.get(count++));
    }
}
