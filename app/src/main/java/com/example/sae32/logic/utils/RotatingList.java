package com.example.sae32.logic.utils;

import java.util.ArrayList;

public class RotatingList<E> extends ArrayList<E> {
    private int limit;
    public RotatingList(int capacity){
        limit = capacity;
    }
    public boolean add(E element){
        if (size()<limit) {
            return super.add(element);
        }else{
            remove(0);
            return super.add(element);
        }
    }
}
