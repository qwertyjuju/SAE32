package com.example.sae32.logic.Tasks;


import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler extends Task{
    final private ServerTask server;
    final private  BufferedReader reader;
    final private  BufferedWriter writer;
    final private Socket socket;

    public ClientHandler(Socket sock, ServerTask server)throws TaskException{
        super();
        this.server=server;
        socket=sock;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            server.addClientHandler(this);
        }catch(IOException e){
            throw new TaskException("couldnt create ClientHandler: "+e.getMessage());
        }
    }

    @Override
    protected void doInBackground(){
        try {
            final Message initmsg =serverMessaging.getInitMessage(reader.readLine());
            send(serverMessaging.createInitMessageResponse(initmsg));
            while (running) {
                String buffer = reader.readLine();
                if(buffer!=null){
                    final Message msg = serverMessaging.getMsgMessage(buffer);
                    if (msg.isValid()) {
                        doOnMainThreadAndWait(() -> {
                            serverMessaging.publish(msg);
                        });
                    }
                }else if(socket.isConnected()){
                    kill();
                }
            }
        }
        catch(IOException e){
            doOnMainThreadAndWait(()->{
                logger.warning("error while reading message: "+e.getMessage());
            });
        }
    }

    public void send(Message msg){
        try{
            writer.write(msg.toString()+"\n");
            writer.flush();
        }catch(IOException e){
            doOnMainThreadAndWait(()-> {
                logger.warning("client handler couldn't send message: " + e.getMessage());
            });
        }
    }

    @Override
    protected void onShutdown(){
        try {
            socket.close();
            server.removeClientHandler(this);
        }catch(IOException e){
            doOnMainThreadAndWait(()->{
                logger.info("client Handler shutdown");
            });
        }
    }
}
