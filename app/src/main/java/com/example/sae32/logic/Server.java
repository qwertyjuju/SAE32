package com.example.sae32.logic;

import com.example.sae32.logic.Tasks.ClientHandler;
import com.example.sae32.logic.Tasks.ServerTask;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends AppObject{
    private int MAX_LIMIT;
    public ServerSocket socket;
    private Inet4Address ip;
    private ServerTask task;
    private List<String> blacklist;
    private List<ClientHandler> clientHandlers;

    public Server(int port){
        MAX_LIMIT=6;
        clientHandlers = new ArrayList<ClientHandler>();
        try {
            System.out.println(usedIp);
            socket = new ServerSocket(port,100, usedIp);
            logger.info("Server setup done");
            logger.info("@IP/port: "+socket.getInetAddress().toString());
            task = new ServerTask(this);
            task.run();
        }catch(IOException e){
            logger.warning("Server socket not created: "+e.getMessage());
        }

    }

    public void addClientHandler(Socket sock){
        if(clientHandlers.size()<MAX_LIMIT) {
            ClientHandler handler = new ClientHandler(sock);
            clientHandlers.add(handler);
            handler.run();
        }else{
            logger.warning("client number max limit reached");
        }
    }
}
