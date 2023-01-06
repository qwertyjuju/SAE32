package com.example.sae32.logic.Tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler extends Task{
    private static String buffer;
    private static Socket socket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    public ClientHandler(Socket sock){
        super();
        socket=sock;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
            logger.warning("couldnt create reader on ClientHandler: " +e.getMessage());
        }
    }

    @Override
    protected void doInBackground(){
        while(running) {
            try{
                buffer = reader.readLine();
                doOnMainThread(()->{
                    logger.info(buffer);
                });
            }
            catch(IOException e){
                doOnMainThread(()->{
                    logger.warning("error while reading message: "+e.getMessage());
                });
            }
        }
    }

    @Override
    protected void onShutdown(){
        try {
            socket.close();
        }catch(IOException e){
            doOnMainThread(()->{
                logger.info("client Handler shutdown");
            });
        }
    }
}
