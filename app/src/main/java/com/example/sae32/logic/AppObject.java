package com.example.sae32.logic;

import com.example.sae32.MainActivity;
import com.example.sae32.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

public class AppObject {
    public static Logger logger;
    private static MainActivity master;
    private static List<String> interfaceList;
    protected static InetAddress usedIp;

    public static void initClass(MainActivity activity, Logger log){
        master = activity;
        logger=log;
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

    public static void setUsedIp(String ip){
        if(!ip.equals("None")){
            try{
            usedIp= InetAddress.getByName(ip);
            }
            catch(UnknownHostException e){
                logger.warning(e.getMessage());
            }
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


}
