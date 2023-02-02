package com.example.sae32.logic.Tasks;

import android.widget.TextView;

import com.example.sae32.logic.Exceptions.HandlerException;
import com.example.sae32.logic.Exceptions.MessagingException;
import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message;
import com.example.sae32.logic.Messaging.TextViewMessagingHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTask extends Task{
    private String name;
    private Queue<Message> messageQueue;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private ExecutorService readExecutor=null;

    public ClientTask(InetAddress ip, int port, String clientName)throws TaskException {
        super();
        try {
            socket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            messageQueue = new ArrayBlockingQueue<>(100);
            logger.info("Connected to: "+ip.toString()+"/"+port);
            System.out.println(this);
            name=clientName;
        }catch(IOException e){
            throw new TaskException("Socket not created: "+e.getMessage());
        }
    }

    @Override
    protected void doInBackground(){
        send(clientMessaging.createInitMessage());
        System.out.println("test1");
        try {
            Message msg = clientMessaging.getInitMessage(reader.readLine());
            if(!msg.isValid()) {
                kill();
            }
            doOnMainThreadAndWait(() -> {
                clientMessaging.publish(msg);
            });
        } catch (IOException e) {
            kill();
        }
        readExecutor = Executors.newSingleThreadExecutor();
        readExecutor.execute(()->{
            while(running){
                try {
                    Message msg = clientMessaging.getMsgMessage(reader.readLine());
                    if (msg.isValid()) {
                        doOnMainThreadAndWait(() -> {
                            clientMessaging.publish(msg);
                        });
                    }
                }catch(IOException e){
                    doOnMainThreadAndWait(()-> {
                        System.out.println(this);
                        logger.warning("couldn't receive message: " + e.getMessage());
                    });
                }
            }
        });
        while (running){
            Message msg;
            while((msg= messageQueue.poll())!=null){
                send(msg);
            }
        }
    }

    private void send(Message msg){
        try{
            writer.write(msg.toString()+"\n");
            writer.flush();
        }catch(IOException e){
            doOnMainThreadAndWait(()-> {
                logger.warning("couldn't send message: " + e.getMessage());
            });
        }
    }

    @Override
    protected void onShutdown(){
        if(readExecutor!=null) {
            readExecutor.shutdownNow();
        }
        messageQueue.clear();
        try{
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void addMessage(String msg){
        messageQueue.add(clientMessaging.createMsgMessage(msg));
    }

    public String getName(){
        return name;
    }

    protected void finalize(){
        System.out.println("clientTask destroyed");
    }
}