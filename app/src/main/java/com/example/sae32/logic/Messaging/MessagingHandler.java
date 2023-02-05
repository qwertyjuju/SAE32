package com.example.sae32.logic.Messaging;

import java.util.Hashtable;
/* Un Messaging Handler permet de faire l'interface entre les messages reçu et les afficheurs pour
ces messages. Par exemple pour afficher les messages sur un TextView, on créer un TextViewMessaging Handler
 */
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

    abstract void setOutput(T output);
    abstract void publish(String msg);


}
