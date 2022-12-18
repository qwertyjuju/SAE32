package com.example.sae32.logic;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Task extends AppObject{
    /*
    La classe AsyncTask étant obsolète, j'ai créer une classe "Task" permettant de réaliser
    un programme multi-threads
    */
    protected ExecutorService executor;
    protected Handler handler;
    protected boolean running;
    protected Task(){
        running =false;
    }

    public void run(){
        running=true;
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        executor.execute(this::doInBackground);
    }


    public void update(Runnable r){
        // appelle la fonction passée en paramètre. Le handler permet d'executer
        handler.post(r);
    }
    public void kill(){
        onShutdown();
        executor.shutdown();
        running=false;
    }

    abstract protected void doInBackground();
    abstract protected void onShutdown();

}
