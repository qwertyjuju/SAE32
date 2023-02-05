package com.example.sae32.logic.Tasks;

import com.example.sae32.logic.Constants;
import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message.TCPMessage;
import com.example.sae32.logic.Messaging.Message.UDPMessage;
import com.example.sae32.logic.Messaging.Message.MessageType;
import com.example.sae32.logic.utils.ConnectionType;

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
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTask extends Task{
    /* Classe principale pour une tache cliente. En fonction du type de client, le
    * TCPRunnable ou le UDP runnable sera lancé. */

    private ConnectionType connectionType;
    private String name;
    private ClientRunnable runnable;

    public ClientTask(InetAddress ip, int port, String clientName, ConnectionType type)throws TaskException {
        super();
        connectionType=type;
        name=clientName;
        if(connectionType==ConnectionType.UDP){
            runnable = new UDPClientRunnable(ip, port);
        }else{
            runnable = new TCPClientRunnable(ip, port);
        }
    }

    @Override
    protected void doInBackground(){runnable.run();}

    @Override
    protected void onShutdown(){
        runnable.shutdown();
    }

    public void addMessage(String msg){
        runnable.addMessage(msg);
    }

    public String getName(){
        return name;
    }
    interface ClientRunnable extends Runnable {
        void addMessage(String msg);
        void shutdown();
    }

    private class TCPClientRunnable implements ClientRunnable{
        private Queue<TCPMessage> TCPMessageQueue;
        private Socket socket;
        private BufferedWriter writer;
        private BufferedReader reader;
        private ExecutorService readExecutor=null;

        TCPClientRunnable(InetAddress ip, int port) throws TaskException {
            try {
                socket = new Socket(ip, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                TCPMessageQueue = new ArrayBlockingQueue<>(100);
                logger.info("Connected to: "+ip.toString()+"/"+port);
            }catch(IOException e){
                throw new TaskException("TCP Socket not created: "+e.getMessage());
            }
        }

        @Override
        public void run() {
            send(clientMessaging.createTCPMessage(MessageType.INIT, name));
            try {
                TCPMessage msg = clientMessaging.getTCPMessageFromStr(reader.readLine());
                if(!msg.isValid()) {
                    kill();
                }
                else if(msg.getType()== MessageType.CLOSING) {
                    doOnMainThreadAndWait(() -> {
                        clientMessaging.publish(msg);
                    });
                    kill();
                }else {
                    doOnMainThreadAndWait(() -> {
                        clientMessaging.publish(msg);
                    });
                }
            } catch (IOException e) {
                kill();
            }
            readExecutor = Executors.newSingleThreadExecutor();
            readExecutor.execute(()->{
                while(running){
                    try {
                        TCPMessage msg = clientMessaging.getTCPMessageFromStr(reader.readLine());
                        System.out.println(msg);
                        if (msg.isValid()) {
                            System.out.println("valid");
                            doOnMainThreadAndWait(() -> {
                                clientMessaging.publish(msg);
                            });
                            if(msg.getType()==MessageType.CLOSING){
                                kill();
                            }
                        }else {
                            System.out.println("BEUTEU DE OUF");
                        }
                    }catch(IOException e){
                        doOnMainThreadAndWait(()-> {
                            logger.warning("Client couldn't receive message: " + e.getMessage());
                        });
                        kill();
                    }
                }
            });
            while (running){
                TCPMessage msg;
                while((msg= TCPMessageQueue.poll())!=null){
                    send(msg);
                }
            }
        }


        private void send(TCPMessage msg){
            try{
                System.out.println("client sending");
                System.out.println(msg.toString());
                writer.write(msg.toString());
                writer.newLine();
                writer.flush();
            }catch(IOException e){
                doOnMainThreadAndWait(()-> {
                    logger.warning("couldn't send message: " + e.getMessage());
                });
            }
        }

        public void addMessage(String msg){
            TCPMessageQueue.add(clientMessaging.createTCPMessage(MessageType.MSG, name, msg));
        }

        public void shutdown(){
            if(readExecutor!=null) {
                readExecutor.shutdownNow();
            }
            TCPMessageQueue.clear();
            try{
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    private class UDPClientRunnable implements ClientRunnable{
        private Queue<UDPMessage> UDPMessageQueue;
        private DatagramSocket socket;
        private InetAddress dstIp;
        private int dstPort;
        private ExecutorService readExecutor=null;

        UDPClientRunnable(InetAddress ip, int port) throws TaskException {
            dstPort=port;
            dstIp=ip;
            UDPMessageQueue = new ArrayBlockingQueue<UDPMessage>(10);
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                throw new TaskException("UDP socket not created:"+e.getMessage());
            }
        }
        @Override
        public void run() {
            initExchange();
            readExecutor = Executors.newSingleThreadExecutor();
            readExecutor.execute(()->{
                while(running){
                    UDPMessage msg = receive();
                    if(msg!=null){
                        doOnMainThreadAndWait(()->{
                            clientMessaging.publish(msg);
                        });
                    }
                }
            });
            while(running){
                UDPMessage msg;
                while((msg= UDPMessageQueue.poll())!=null){
                    send(msg);
                }
            }
        }

        private void initExchange(){
            send(serverMessaging.createUDPMessage(MessageType.INIT, getId(), getName()));
            UDPMessage msg = receive();
            if(msg!=null){
                if(msg.getType()==MessageType.CLOSING){
                    System.out.println("test1");
                    doOnMainThreadAndWait(()->{
                        clientMessaging.publish(msg);
                    });
                    kill();
                }
                else{
                    doOnMainThreadAndWait(()->{
                        logger.info("Connected to: "+dstIp.toString()+"/"+dstPort);
                        clientMessaging.publish(msg);
                    });
                }
            }else{
                doOnMainThreadAndWait(()->{
                    logger.warning("impossible to connect to server");
                });
                kill();
            }

        }

        private void send(UDPMessage msg){
            try {
                msg.serialize();
                DatagramPacket packet= new DatagramPacket(msg.getBuffer(), msg.getBufferSize(), dstIp, dstPort);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private UDPMessage receive(){
            byte[] buffer = new byte[Constants.UDP_BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                UDPMessage msg = UDPMessage.deSerialize(packet);
                return msg;
            } catch (IOException | ClassNotFoundException ignore) {

            }
            return null;
        }

        @Override
        public void addMessage(String msg) {
            UDPMessageQueue.add(clientMessaging.createUDPMessage(MessageType.MSG, getId(), getName(), msg));
        }

        @Override
        public void shutdown() {
            socket.close();
        }
    }
}