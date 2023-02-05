package com.example.sae32.logic.Tasks;


import com.example.sae32.logic.Constants;
import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message.TCPMessage;
import com.example.sae32.logic.Messaging.Message.UDPMessage;
import com.example.sae32.logic.Messaging.Message.MessageType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends Task{
    private HandlerRunnable runnable;

    public ClientHandler(Socket sock, ServerTask.TCPServerRunnable server)throws TaskException{
        super();
        runnable = new TCPHandlerRunnable(sock, server);
    }
    public ClientHandler(UUID handlerid, ServerTask.UDPServerRunnable server)throws TaskException{
        super();
        _id=handlerid;
        runnable= new UDPHandlerRunnable(server);
    }
    @SuppressWarnings("unchecked")
    public synchronized void send(TCPMessage msg){
        runnable.send(msg);
    }
    @SuppressWarnings("unchecked")
    public synchronized void send(UDPMessage msg){
        runnable.send(msg);
    }

    @SuppressWarnings("unchecked")
    public void addMessage(UDPMessage msg){
        runnable.addMessage(msg);
    }

    @Override
    protected void doInBackground(){runnable.run();}
    @Override
    protected void onShutdown(){
        runnable.shutdown();
    }

    public interface HandlerRunnable<msgType> extends Runnable{
        void shutdown();
        void send(msgType msg);
        void addMessage(msgType msg);
    }


    /* Runnables pour handler TCP ou UDP  */
    public class TCPHandlerRunnable implements HandlerRunnable<TCPMessage>{
        private ServerTask.TCPServerRunnable server;
        private final BufferedReader reader;
        private final BufferedWriter writer;
        private final Socket socket;

        TCPHandlerRunnable(Socket sock, ServerTask.TCPServerRunnable server) throws TaskException {
            socket=sock;
            this.server=server;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (!server.addClientHandler(ClientHandler.this)){
                    send(serverMessaging.createTCPMessage(MessageType.CLOSING,server.getName(),"Server full"));
                    kill();
                };
            }catch(IOException e){
                send(serverMessaging.createTCPMessage(MessageType.CLOSING,server.getName(),e.getMessage()));
                throw new TaskException("couldnt create ClientHandler: "+e.getMessage());
            }
        }
        @Override
        public void run() {
            try {
                final TCPMessage initmsg =serverMessaging.getTCPMessageFromStr(reader.readLine());
                if(!initmsg.isValid()){
                    send(serverMessaging.createTCPMessage(MessageType.CLOSING, server.getName(), "message not valid"));
                    kill();
                }else{
                    send(serverMessaging.createTCPMessage(MessageType.ACK, server.getName(), server.getWelcomeMessage()));
                }
                while (running) {
                    String buffer = reader.readLine();
                    if(buffer!=null){
                        final TCPMessage msg = serverMessaging.getTCPMessageFromStr(buffer);
                        if (msg.isValid()) {
                            server.sendToAll(msg);
                            doOnMainThreadAndWait(() -> {
                                serverMessaging.publish(msg);
                            });
                        }
                    }else {
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

        @Override
        public void shutdown() {
            try {
                socket.close();
                server.removeClientHandler(ClientHandler.this);
                doOnMainThreadAndWait(()->{
                    logger.info("client Handler shutdown");
                });
            }catch(IOException ignore){}
        }

        public synchronized void send(TCPMessage msg) {
            try{
                writer.write(msg.toString());
                writer.newLine();
                writer.flush();
            }catch(IOException e){
                doOnMainThreadAndWait(()-> {
                    logger.warning("client handler couldn't send message: " + e.getMessage());
                });
            }
        }

        @Override
        public void addMessage(TCPMessage msg) {
        }


    }
    public class UDPHandlerRunnable implements HandlerRunnable<UDPMessage>{
        private ArrayBlockingQueue<UDPMessage> messageQueue;
        private ServerTask.UDPServerRunnable server;
        private DatagramSocket socket;
        private InetAddress dstIp;
        private int dstPort;

        UDPHandlerRunnable(ServerTask.UDPServerRunnable server) throws TaskException {
            this.server=server;
            messageQueue = new ArrayBlockingQueue<UDPMessage>(10);
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                throw new TaskException("Couldn't create UDP Handler:"+e.getMessage());
            }
        }
        @Override
        public void run() {
            UDPMessage initmsg = messageQueue.poll();
            if(initmsg!=null) {
                if (initmsg.getType() != MessageType.INIT) {
                    kill();
                }else{
                    dstIp=initmsg.getSenderIp();
                    dstPort=initmsg.getSenderPort();
                    if(!server.addClientHandler(ClientHandler.this)){
                        send(serverMessaging.createUDPMessage(MessageType.CLOSING, _id, server.getName(), "Server Full"));
                        kill();
                    }else {
                        send(serverMessaging.createUDPMessage(MessageType.ACK, _id, server.getName(),server.getWelcomeMessage()));
                    }
                }
            }else{
                kill();
            }
            while(running){
                try {
                    UDPMessage msg = messageQueue.poll(Constants.UDP_CLIENT_HANDLER_TIMEOUT, TimeUnit.SECONDS);
                    if(msg!=null){
                        if(msg.getType()==MessageType.MSG){
                            server.sendToAll(msg);
                        }
                    }else{
                        send(serverMessaging.createUDPMessage(MessageType.CLOSING, _id, server.getName(), server.getWelcomeMessage()));
                    }
                } catch (InterruptedException ignore) {}
            }

        }

        @Override
        public void shutdown() {
            socket.close();
            messageQueue.clear();
            server.removeClientHandler(ClientHandler.this);
        }

        @Override
        public synchronized void send(UDPMessage msg) {
            try {
                msg.serialize();
                DatagramPacket packet= new DatagramPacket(msg.getBuffer(), msg.getBufferSize(), dstIp, dstPort);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void addMessage(UDPMessage msg) {
            messageQueue.add(msg);
        }
    }
}
