package com.example.sae32.logic.Tasks;

import com.example.sae32.logic.Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ClientTask extends Task{
    private Client client;
    private BufferedWriter writer;

    public ClientTask(Client cli){
        super();
        client = cli;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(client.socket.getOutputStream()));
        }catch(IOException e){
            logger.warning(e.getMessage());
        }
    }

    @Override
    protected void doInBackground() {
        while (running){
            String msg;
            while((msg= client.messageQueue.poll())!=null){
                try{
                    writer.write(msg);
                    writer.flush();
                }catch(IOException e){
                    doOnMainThread(()-> {
                        logger.warning("couldn't send message: " + e.getMessage());
                    });
                }
            }
        }
    }

    @Override
    protected void onShutdown() {

    }
}