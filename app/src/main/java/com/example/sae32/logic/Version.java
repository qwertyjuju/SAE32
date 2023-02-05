package com.example.sae32.logic;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
/*Classe permettant de définir une version d'application*/
public class Version {
    private int major;
    private int middle;
    private int minor;
    private List<Integer> verList;
    private String verStr;

    public Version(int maj, int mid, int min){
        major = maj;
        middle= mid;
        minor=min;
        verStr = major +"."+ middle+"."+minor;
        verList=new ArrayList<Integer>();
        verList.add(major);
        verList.add(middle);
        verList.add(minor);
    }

    public List<Integer> getList(){
        return verList;
    }

    @NonNull
    public String toString(){
        return verStr;
    }

}
