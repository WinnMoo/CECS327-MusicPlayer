package com.cecs;

import com.cecs.controller.Dispatcher;
import com.cecs.controller.JsonService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    private final String jasonBuffer = "{ \"remoteMethod\": \"getSongChunk\", \"objectName\": \"SongServices\", \"param\": { \"song\": \"SOMZWCG12A8C13C480\", \"fragment\": 2 } }";
    private final String jasonLogin = "{ \"remoteMethod\": \"login\", \"objectName\": \"UserServices\", \"param\": { \"username\": \"chris\", \"password\": \"greer\" } }";

    @Test
    void testDispatch() {
        var dispatch = new Dispatcher();
        var ret = dispatch.dispatch(jasonBuffer);
        System.out.println("Packet: " + ret);
        var bytes = JsonService.unpackBytes(ret);

        assertEquals(8192, bytes.length);
    }

    @Test
    void testLogin() {
        var dispatch = new Dispatcher();
        var ret = dispatch.dispatch(jasonLogin);
        var playlists = JsonService.unpackPlaylists(ret);

        if (playlists == null) {
            System.out.println("This user doesn't exist");
        } else if (playlists.length == 0) {
            System.out.println("This user does not have any Playlists");
        } else {
            System.out.println("This user has " + playlists.length + " playlist(s)");
        }

        // System.out.println(Arrays.toString(bytes));
    }

    @AfterAll
    static void closeServer() {
        var dispatch = new Dispatcher();
        dispatch.send("end");
    }
}
