package com.example.sae32.logic;

import android.os.Handler;
import android.os.Looper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;

import com.example.sae32.logic.Server;


public class ServerTask extends Task{
    private Server server;
    private Boolean run;

    ServerTask(Server serv){
        super();
        server=serv;
    }

    @Override
    protected void doInBackground(){
        try {
            while (running) {
                Socket socket;
                socket = server.socket.accept();
                server.addClientHandler(socket);
            }
        }
        catch (IOException e){
            update(() -> {
                logger.info("Error setting up Server: "+e.getMessage());
            });
            run =false;
        }
    }
    @Override
    protected void onShutdown(){}
}