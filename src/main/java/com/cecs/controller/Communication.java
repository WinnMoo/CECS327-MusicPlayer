package com.cecs.controller;

import com.cecs.model.RemoteRef;

import java.io.IOException;
import java.net.*;

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
        MAYBE, AT_LEAST_ONCE, AT_MOST_ONCE
    }

    private static final byte[] buffer = new byte[32768];
    private static final int TIME_OUT = 1000;

    public Communication() {
    }

    /**
     * Only send out a message to server, without expecting replay
     */
    public void send(String s) {
        try {
            final var remoteRef = new RemoteRef();
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
    // TODO: Add amount of times to retry before giving up. Alert on GUI. If the
    // server is down, the program will freeze
    public String dispatch(String request, Semantic semantic) {
        var retry = 0;
        var start = System.currentTimeMillis();
        var duration = System.currentTimeMillis() - start;
        System.out.format("Time needed to dispatch: %d\n", duration);

        DatagramPacket receivePacket = null;
        try {
            final var remoteRef = new RemoteRef();
            final var socket = new DatagramSocket();
            socket.setSoTimeout(TIME_OUT);

            // Send
            final var out = request.getBytes();
            var sendPacket = new DatagramPacket(out, out.length, remoteRef.getAddress(), remoteRef.getPort());
            receivePacket = new DatagramPacket(buffer, buffer.length);
            System.out.println(
                    "Sending message of size " + sendPacket.getLength() + " to " + sendPacket.getSocketAddress());

            System.out.println("Listening...");
            // MAYBE
            socket.send(sendPacket);
            // AT_LEAST_ONCE or AT_MOST_ONCE: resend the request if response times out
            if (semantic != Semantic.MAYBE) {
                while (retry < 10) {
                    try {
                        socket.receive(receivePacket);
                        break;
                    } catch (SocketTimeoutException e) {
                        retry++;
                        socket.send(sendPacket);
                    }
                }
            }
            if (retry == 10) {
                socket.close();
                return "{ \"error\": \"Request timed out\" }";
            } else {
                System.out.println("Message received!");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receivePacket != null ? new String(receivePacket.getData(), 0, receivePacket.getLength()) : null;
    }
}
