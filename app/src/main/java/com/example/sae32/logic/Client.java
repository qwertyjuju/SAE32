package com.example.sae32.logic;


import com.example.sae32.logic.Tasks.ClientTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client extends AppObject{
    public Socket socket;
    public ClientTask task;
    public Queue<String> messageQueue;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    public Client(InetAddress ip, int port){
        messageQueue = new ArrayBlockingQueue<String>(100);
        try {
            socket = new Socket(ip, port);
            task = new ClientTask(this);
            task.run();
            logger.info("Connected to: "+ip.toString()+"/"+port);
        }catch(IOException e){
            logger.warning("Socket not created: "+e.getMessage());
        }
    }
    public void addMessage(String msg){
        messageQueue.add(msg+"\n");
    }
}
