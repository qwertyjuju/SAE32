package com.example.sae32.logic.Tasks;

import com.example.sae32.logic.Server;

import java.net.Socket;

import java.io.IOException;


public class ServerTask extends Task{
    private Server server;
    private Boolean run;

    public ServerTask(Server serv){
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
            doOnMainThread(() -> {
                logger.info("Error setting up Server: "+e.getMessage());
            });
            run =false;
        }
    }
    @Override
    protected void onShutdown(){
        doOnMainThread(()->{

        });
    }
}