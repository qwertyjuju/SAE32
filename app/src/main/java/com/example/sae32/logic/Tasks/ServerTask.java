package com.example.sae32.logic.Tasks;

import android.widget.TextView;

import com.example.sae32.logic.Exceptions.HandlerException;
import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message;
import com.example.sae32.logic.Messaging.TextViewMessagingHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class ServerTask extends Task{

    public enum ServerType{
        TCP, UDP
    } //enum definissant les differents types de serveurs
    private ServerType type; // type du serveur
    private int MAX_LIMIT= 6; // nombre max de clients (serveur TCP)
    private Runnable runnable;
    private String name;
    private String welcomeMessage;
    private List<ClientHandler> clientHandlers;

    public ServerTask(int port, ServerType servtype) throws TaskException {
        super();
        type = servtype;
        name="Server";
        welcomeMessage="Welcome on server";
        switch(type){
            case UDP:
                runnable =new UDPServerRunnable(port);
                break;
            default:
                runnable = new TCPServerRunnable(port);
                break;
        }
        clientHandlers = new ArrayList<>();
    }

    @Override
    protected void doInBackground(){
        runnable.run();
    }

    @Override
    protected void onShutdown(){
        doOnMainThread(()->{

        });
    }
    public String getName(){
        return name;
    }
    public String getWelcomeMessage(){
        return welcomeMessage;
    }

    private void createClientHandler(Socket socket){
        if(clientHandlers.size()<MAX_LIMIT) {
            try {
                ClientHandler handler = new ClientHandler(socket, this);
                handler.run();
            }catch(TaskException e){
                doOnMainThreadAndWait(()->{
                    logger.warning(e.getMessage());
                });
            }
        }else{
            doOnMainThreadAndWait(()->{
                logger.warning("client number max limit reached");
            });
        }
    }
    public void sendToAll(Message msg){
        for(ClientHandler handler:clientHandlers){
            handler.send(msg);
        }
    }

    public Boolean addClientHandler(ClientHandler handler){
        return clientHandlers.add(handler);
    }
    public Boolean removeClientHandler(ClientHandler handler){
        return clientHandlers.remove(handler);
    }

    private class TCPServerRunnable implements Runnable {
        private ServerSocket socket;

        TCPServerRunnable(int port) throws TaskException {
            try{
                socket = new ServerSocket(port);
                logger.info("TCP server created: "+ InetAddress.getLocalHost().toString());
            }catch(IOException e){
                throw new TaskException("TCP Server socket not created: "+e.getMessage());
            }
        }
        @Override
        public void run() {
            while (running) {
                Socket clisocket;
                try {
                    clisocket = socket.accept();
                    createClientHandler(clisocket);
                }
                catch (IOException e) {
                    doOnMainThreadAndWait(() -> {
                        logger.info("Error accepting client: " + e.getMessage());
                        running = false;
                    });
                }
            }
        }
    }
    private class UDPServerRunnable implements Runnable{
        private DatagramSocket socket;
        UDPServerRunnable(int port ) throws TaskException {
            try {
                socket = new DatagramSocket(port);
                logger.info("UDP server created: "+socket.getInetAddress().toString());
            } catch (SocketException e) {
                throw new TaskException("UDP Server socket not created: "+e.getMessage());
            }
        }
        @Override
        public void run() {
        }
    }
}