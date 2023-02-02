package com.example.sae32.logic.Messaging;

import java.util.Hashtable;

public abstract class MessagingHandler<T> {
    String name;
    Messaging messaging;
    T out;
    static Hashtable<String, MessagingHandler<?>> handlers= new Hashtable<>();

    MessagingHandler(String hName, Messaging hMessaging){
        name=hName;
        messaging=hMessaging;
        handlers.put(name, this);
        messaging.addHandler(this);
    }
    public void setOutput(T output){
        out=output;
    }
    abstract void publish(String msg);


}
