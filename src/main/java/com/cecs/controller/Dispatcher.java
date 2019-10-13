package com.cecs.controller;

import com.cecs.def.DispatcherInterface;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

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
