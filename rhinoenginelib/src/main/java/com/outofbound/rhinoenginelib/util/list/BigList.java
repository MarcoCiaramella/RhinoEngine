package com.outofbound.rhinoenginelib.util.list;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A typed list with a fast access but an high memory consuming. Useful when list is bounded.
 * @param <E> the type.
 */
public class BigList<E> implements Iterable<E>, Iterator<E> {

    private final ArrayList<E> list = new ArrayList<>();
    private final ArrayList<Integer> indices = new ArrayList<>();
    private int count = 0;

    /**
     * Add an instance of E.
     * @param object the instance of E.
     * @return the index of object in list. -1 if object is already in list.
     */
    public int add(@NonNull E object){
        if (list.contains(object)){
            return -1;
        }
        list.add(object);
        int index = list.indexOf(object);
        indices.add(index);
        return index;
    }

    /**
     * Remove object at input index.
     * @param index the index of object to remove.
     * @return the removed object if exists, null otherwise.
     */
    public E remove(int index){
        if (contains(index)) {
            E object = get(index);
            list.set(index, null);
            indices.remove((Integer) index);
            return object;
        }
        return null;
    }

    /**
     * Remove the input object.
     * @param object the object to remove.
     * @return the removed object if exists, null otherwise.
     */
    public E remove(E object){
        return remove(list.indexOf(object));
    }

    /**
     * Return the object at input index.
     * @param index the object index.
     * @return the object at index if exists, null otherwise.
     */
    public E get(int index){
        if (contains(index)){
            return list.get(index);
        }
        return null;
    }

    /**
     * Return the size of this list.
     * @return the size of this list.
     */
    public int size(){
        return indices.size();
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
