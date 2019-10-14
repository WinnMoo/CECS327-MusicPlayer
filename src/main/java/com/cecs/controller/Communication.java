package com.cecs.controller;

import com.cecs.model.RemoteRef;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
/* Semantics:
Maybe
    just send the request
At least Once:(get methods that don’t change server’s DB)
    resend the request if not receive the response after TIME_OUT
    re-execute procedure
    list of methods:
        Login
        PlaySong
        GetListofSongs
        DeleteSong, DeletePlaylist
At Most Once: (methods that may change server’s DB)
    resend the request if not receive the response after TIME_OUT
    check duplicate(server)
    retransmit reply
    list of methods:
        Register
        AddSong, AddPlaylist

* */

public class Communication {
    public enum Semantic {
        MAYBE, AT_LEAST_ONCE, AT_MOST_ONCE;
    }

    private HashMap<String, Object> listOfObjects;
    private static final byte[] buffer = new byte[32768];
    private static final int TIME_OUT = 1000;

    public Communication() {
        listOfObjects = new HashMap<>();
    }

    /**
     * Only send out a message to server, without expecting replay
     */
    public void send(String s) {
        try {
            var remoteRef = new RemoteRef();
            final var socket = new DatagramSocket();
            final var out = s.getBytes();
            final var packet = new DatagramPacket(out, out.length, remoteRef.getAddress(), remoteRef.getPort());
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
    public String dispatch(String request, Semantic semantic) {
        DatagramPacket sendPacket = null;
        DatagramPacket receivePacket = null;
        try {
            var remoteRef = new RemoteRef();
            final var socket = new DatagramSocket();
            socket.setSoTimeout(TIME_OUT);

            // Send
            final var out = request.getBytes();
            sendPacket = new DatagramPacket(out, out.length, remoteRef.getAddress(), remoteRef.getPort());
            receivePacket = new DatagramPacket(buffer, buffer.length);
            System.out.println(
                    "Sending message of size " + sendPacket.getLength() + " to " + sendPacket.getSocketAddress());

            System.out.println("Listening...");
            if (semantic == Semantic.MAYBE) // only send the request once
                socket.send(sendPacket);
            else { // AT_LEAST_ONCE or AT_MOST_ONCE: resend the request if not receive the response
                   // after TIME_OUT
                while (true) {
                    try {
                        socket.receive(receivePacket);
                    } catch (SocketTimeoutException e) {
                        // resend
                        socket.send(sendPacket);
                        continue;
                    }
                    break;
                }
            }

            System.out.println("Message received!");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receivePacket != null ? new String(receivePacket.getData(), 0, receivePacket.getLength()) : null;
    }

    /*
     * registerObject: It register the objects that handle the request
     *
     * @param remoteMethod: It is the name of the method that objectName implements.
     *
     * @objectName: It is the main class that contains the remote methods each
     * object can contain several remote methods
     */
    public void registerObject(Object remoteMethod, String objectName) {
        listOfObjects.put(objectName, remoteMethod);
    }
}
