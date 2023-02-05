package com.example.sae32.logic;

import com.example.sae32.MainActivity;
import com.example.sae32.R;
import com.example.sae32.logic.Messaging.ClientMessaging;
import com.example.sae32.logic.Messaging.ServerMessaging;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class AppObject {
    public static Version VERSION=new Version(0,0,2);
    public static Logger logger;
    private static MainActivity master;
    private static List<String> interfaceList;
    public static ClientMessaging clientMessaging;
    public static ServerMessaging serverMessaging;
    private static boolean _init=false;
    protected UUID _id;

    public AppObject(){
        _id=UUID.randomUUID();
    }
    public static void initClass(MainActivity activity){
        if(!_init) {
            master = activity;
            logger= Logger.getLogger("netApp");
            logger.addHandler(master.loggerView);
            serverMessaging = new ServerMessaging();
            clientMessaging = new ClientMessaging();
            _init=true;
        }
        setInterfaces();
    }

    public static void setInterfaces(){
        interfaceList= new ArrayList<String>();
        try {
            for(Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();){
                interfaceList.add(e.nextElement().getName());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static List<String>getIpInterface(String networkInterfacename){
        List<String> ipList = new ArrayList<String>();
        try {
            NetworkInterface netint = NetworkInterface.getByName(networkInterfacename);
            for(Enumeration<InetAddress> e = netint.getInetAddresses();e.hasMoreElements();){
                ipList.add(e.nextElement().toString());
            }
        }catch(SocketException e){
            logger.warning((String)master.getText(R.string.networkint_error1));
        }
        return ipList;
    }
    public static List<String> getInterfaces(){
        return interfaceList;
    }
    public static Logger getLogger(){
        return logger;
    }
    public UUID getId(){
        return _id;
    }
}
