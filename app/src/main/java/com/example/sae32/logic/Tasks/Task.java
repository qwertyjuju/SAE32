package com.example.sae32.logic.Tasks;

import android.os.Handler;
import android.os.Looper;

import com.example.sae32.logic.AppObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

public abstract class Task extends AppObject {
    /*
    La classe AsyncTask étant obsolète, j'ai créer une classe "Task" permettant de réaliser
    un programme multi-threads
    */
    protected ExecutorService executor;
    protected Lock locker;
    protected Handler handler;
    private SynchronizedRunnable syncRun;
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


    public void doOnMainThread(Runnable r)  {
        // appelle la fonction passée en paramètre. Le handler permet d'executer
        handler.post(r);
    }

    public synchronized void doOnMainThreadAndWait(Runnable r)  {
        syncRun = new SynchronizedRunnable(r);
        handler.post(syncRun);
        synchronized(syncRun){
            try {
                syncRun.wait();
                syncRun=null;
            } catch (InterruptedException ignore) {}
        }
    }

    public void kill(){
        onShutdown();
        executor.shutdownNow();
        running=false;
    }

    abstract protected void doInBackground();
    abstract protected void onShutdown();

    class  SynchronizedRunnable implements Runnable{
        private Runnable runnable;
        private boolean done;

        SynchronizedRunnable(Runnable r){
            runnable = r;
            done = false;
        }
        @Override
        public void run() {
            synchronized(this) {
                runnable.run();
                done = true;
                notify();
            }
        }
        public Boolean getDone(){
            return done;
        }
    }

}
