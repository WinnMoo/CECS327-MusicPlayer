package com.cecs.controller;

import com.cecs.def.DispatcherInterface;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher implements DispatcherInterface {
    private HashMap<String, Object> listOfObjects;
    private static final int port = 5500;
    private static final byte[] buffer = new byte[16384];

    public Dispatcher() {
        listOfObjects = new HashMap<>();
    }

    /**
     * Only send out a message to server, without expecting replay
     */
    public void send(String s) {
        try {
            final var address = InetAddress.getByName("localhost");
            final var socket = new DatagramSocket();

            final var out = s.getBytes();
            final var packet = new DatagramPacket(out, out.length, address, port);
            System.out.println("Closing server");
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * dispatch: Executes the remote method in the corresponding Object
     *
     * @param request: Request: it is a Json file { "remoteMethod": "getSongChunk",
     * "objectName": "SongServices", "param": { "song": 490183, "fragment": 2 } }
     */
    @Override
    public String dispatch(String request) {
        DatagramPacket packet = null;
        try {
            final var address = InetAddress.getByName("localhost");
            final var socket = new DatagramSocket();

            // Send
            final var out = request.getBytes();
            packet = new DatagramPacket(out, out.length, address, port);
            System.out.println("Sending message of size " + packet.getLength() + " to " + packet.getSocketAddress());
            socket.send(packet);

            // Receive
            System.out.println("Listening...");
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println("Message received!");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet != null ? new String(packet.getData(), 0, packet.getLength()) : null;
    }

    /*
     * dispatch: Executes the remote method in the corresponding Object
     *
     * @param request: Request: it is a Json file { "remoteMethod":"getSongChunk",
     * "objectName":"SongServices", "param": { "song":490183, "fragment":2 } }
     */
    public String dispatch2(String request) {
        var jsonReturn = new JsonObject();
        var parser = new JsonParser();
        var jsonRequest = parser.parse(request).getAsJsonObject();

        try {
            // Obtains the object pointing to SongServices
            Object object = listOfObjects.get(jsonRequest.get("objectName").getAsString());
            Method[] methods = object.getClass().getMethods();
            Method method = null;
            // Obtains the method
            for (Method value : methods) {
                if (value.getName().equals(jsonRequest.get("remoteMethod").getAsString())) {
                    method = value;
                }
            }
            if (method == null) {
                jsonReturn.addProperty("error", "Method does not exist");
                return jsonReturn.toString();
            }
            // Prepare the parameters
            Class[] types = method.getParameterTypes();
            Object[] parameter = new Object[types.length];
            String[] strParam = new String[types.length];
            JsonObject jsonParam = jsonRequest.get("param").getAsJsonObject();
            int j = 0;
            for (Map.Entry<String, JsonElement> entry : jsonParam.entrySet()) {
                strParam[j++] = entry.getValue().getAsString();
            }
            // Prepare parameters
            for (int i = 0; i < types.length; i++) {
                switch (types[i].getCanonicalName()) {
                case "java.lang.Long":
                    parameter[i] = Long.parseLong(strParam[i]);
                    break;
                case "java.lang.Integer":
                    parameter[i] = Integer.parseInt(strParam[i]);
                    break;
                case "String":
                    parameter[i] = strParam[i];
                    break;
                }
            }
            // Prepare the return
            Class returnType = method.getReturnType();
            String ret = "";
            switch (returnType.getCanonicalName()) {
            case "java.lang.Long":
            case "java.lang.Integer":
                ret = method.invoke(object, parameter).toString();
                break;
            case "java.lang.String":
                ret = (String) method.invoke(object, parameter);
                break;
            }
            jsonReturn.addProperty("ret", ret);

        } catch (InvocationTargetException | IllegalAccessException e) {
            // System.out.println(e);
            jsonReturn.addProperty("error", "Error on " + jsonRequest.get("objectName").getAsString() + "."
                    + jsonRequest.get("remoteMethod").getAsString());
        }

        return jsonReturn.toString();
    }

    /*
     * registerObject: It register the objects that handle the request
     *
     * @param remoteMethod: It is the name of the method that objectName implements.
     *
     * @objectName: It is the main class that contains the remote methods each
     * object can contain several remote methods
     */
    @Override
    public void registerObject(Object remoteMethod, String objectName) {
        listOfObjects.put(objectName, remoteMethod);
    }
}
