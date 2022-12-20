package com.example.sae32.logic;

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

    ClientHandler(Socket sock){
        super();
        socket=sock;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
        }
    }

    @Override
    protected void doInBackground(){
        while(running) {
            try{
                buffer = reader.readLine();
            }
            catch(IOException e){

            }
        }
    }

    @Override
    protected void onShutdown(){
        try {
            socket.close();
        }catch(IOException e){
            update(()->{

            });
        }
    }
}
