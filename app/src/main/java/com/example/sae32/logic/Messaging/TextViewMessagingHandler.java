package com.example.sae32.logic.Messaging;

import android.widget.TextView;

import com.example.sae32.logic.Exceptions.HandlerException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class TextViewMessagingHandler extends MessagingHandler<TextView>{
    public TextViewMessagingHandler(String name,Messaging messaging){
        super(name, messaging);
    }

    public TextViewMessagingHandler(String name,Messaging messaging,TextView textview){
        super(name, messaging);
        out = textview;
    }
    public void publish(String msg){
        System.out.println(msg);
        if(out!=null) {
            out.append(msg);
        }
    }

    public static TextViewMessagingHandler get(String name, Messaging messaging, TextView textview) {
        TextViewMessagingHandler handler;
        if ((handler = (TextViewMessagingHandler) handlers.get(name)) != null) {
            return handler;
        } else {
            return new TextViewMessagingHandler(name, messaging, textview);
        }
    }

    public static TextViewMessagingHandler get(String name, Messaging messaging) {
        TextViewMessagingHandler handler;
        if ((handler = (TextViewMessagingHandler) handlers.get(name)) != null) {
            return handler;
        } else {
            return new TextViewMessagingHandler(name, messaging);
        }
    }
}
