package com.example.sae32.logic.Tasks;

import com.example.sae32.logic.Constants;
import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message.TCPMessage;
import com.example.sae32.logic.Messaging.Message.UDPMessage;
import com.example.sae32.logic.utils.ConnectionType;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;


public class ServerTask extends Task{
    //enum definissant les differents types de serveurs
    final protected ConnectionType type; // type du serveur
    private final String name;
    private ServerRunnable runnable;
    private final String welcomeMessage;


    public ServerTask(int port, ConnectionType servtype) throws TaskException {
        super();
        type = servtype;
        name="Server";
        welcomeMessage=String.format("Welcome on %s", name);
        if(type==ConnectionType.UDP){
            runnable =new UDPServerRunnable(port);
        }
        else{
            runnable = new TCPServerRunnable(port);
        }

    }

    @Override
    protected void doInBackground(){
        runnable.run();
    }



    @Override
    protected void onShutdown(){
        runnable.shutdown();
    }
    public String getName(){
        return name;
    }

    public String getWelcomeMessage(){
        return welcomeMessage;
    }

    /* Interface pour tous les Runnables serveurs*/
    public interface ServerRunnable extends Runnable{
        void shutdown();
        String getName();
        String getWelcomeMessage();
    }

    /*runnable TCP*/
    public class TCPServerRunnable implements ServerRunnable {
        private ServerSocket socket;
        private ArrayList<ClientHandler> clientHandlers;

        TCPServerRunnable(int port) throws TaskException {
            try{
                clientHandlers= new ArrayList<>();
                socket = new ServerSocket(port);
                logger.info("TCP server created: "+ InetAddress.getLocalHost().toString()+"/"+socket.getLocalPort());
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
                    System.out.println("connected client");
                    createClientHandler(clisocket);
                }
                catch (IOException e) {
                    doOnMainThreadAndWait(() -> {
                        logger.warning("TCP Error in waiting client loop: " + e.getMessage());
                        running = false;
                    });
                }
            }
        }

        public synchronized void sendToAll(TCPMessage msg){
            for(ClientHandler handler:clientHandlers){
                handler.send(msg);
            }
        }
        private void createClientHandler(Socket socket) {
            try {
                ClientHandler handler = new ClientHandler(socket, this);
                handler.run();
            } catch (TaskException e) {
                doOnMainThreadAndWait(() -> {
                    logger.warning(e.getMessage());
                });
            }
        }

        public Boolean addClientHandler(ClientHandler handler){
            if (clientHandlers.size()< Constants.SERVER_MAX_LIMIT) {
                return clientHandlers.add(handler);
            }
            else{
                doOnMainThreadAndWait(()->{
                    logger.warning("client number max limit reached");
                });
                return false;
            }
        }

        public void removeClientHandler(ClientHandler handler){
            clientHandlers.remove(handler);
        }

        @Override
        public void shutdown() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public String getName(){
            return name;
        }

        @Override
        public String getWelcomeMessage(){
            return welcomeMessage;
        }
    }

    public class UDPServerRunnable implements ServerRunnable{
        private DatagramSocket socket;
        protected final Hashtable<UUID, ClientHandler> clientHandlers;

        UDPServerRunnable(int port ) throws TaskException {
            clientHandlers= new Hashtable<>();
            try {
                socket = new DatagramSocket(port);
                logger.warning("UDP server created: "+socket.getLocalAddress().toString());
            } catch (SocketException e) {
                throw new TaskException("UDP Server socket not created: "+e.getMessage());
            }
        }
        @Override
        public void run() {
            while(running){
                UDPMessage msg = receive();
                if(msg!=null) {
                    passToClientHandler(msg);
                }
            }
        }

        private UDPMessage receive(){
            byte[] buffer = new byte[Constants.UDP_BUFFER_SIZE];
            DatagramPacket packet= new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                System.out.println("received");
                return serverMessaging.getUDPMessageFromPacket(packet);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void passToClientHandler(UDPMessage msg){
            ClientHandler handler;
            if((handler =clientHandlers.get(msg.getSenderID()))!=null){
                handler.addMessage(msg);
            }else{
                try {
                    handler = new ClientHandler(msg.getSenderID(), this);
                    System.out.println("beuteu2");
                    handler.addMessage(msg);
                    handler.run();
                }catch(TaskException e){
                    doOnMainThreadAndWait(()->{
                        logger.warning(e.getMessage());
                    });
                }
            }
        }

        @Override
        public void shutdown() {
            socket.close();
        }

        public Boolean addClientHandler(ClientHandler handler){
            if (clientHandlers.size()< Constants.SERVER_MAX_LIMIT) {
                clientHandlers.put(handler.getId(),handler);
                return true;
            }
            else{
                doOnMainThreadAndWait(()->{
                    logger.warning("client number max limit reached");
                });
                return false;
            }
        }
        public synchronized void sendToAll(UDPMessage msg){
            for(ClientHandler handler:clientHandlers.values()){
                handler.send(msg);
            }
        }
        public void removeClientHandler(ClientHandler handler){
            clientHandlers.remove(handler.getId());
        }
        @Override
        public String getName(){
            return name;
        }
        @Override
        public String getWelcomeMessage(){
            return welcomeMessage;
        }

        public UUID getId(){
            return _id;
        }
    }
}