package com.raspisaniyevuzov.app.api;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public class MessagesFactory {

    private Hashtable<String, String> typeToClassMap;

    public MessagesFactory() {
        typeToClassMap = new Hashtable<>();
    }

    public void registerTypeForClass(Class className) {
        typeToClassMap.put(BaseMessage.getMessageName(className), className.getName());
    }

    public BaseMessage messageWithJSONObject(String uid, String type, String data, String cuid) {
        BaseMessage message = null;
        Class cls = null;
        try {
            cls = (type != null && typeToClassMap.containsKey(type)) ? Class.forName(typeToClassMap.get(type)) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cls != null) {
            try {
                message = (BaseMessage) cls.getDeclaredConstructor(String.class, String.class, String.class).newInstance(uid, data, cuid);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

}
