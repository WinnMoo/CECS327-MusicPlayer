package com.cecs;

import com.cecs.controller.Communication;
import com.cecs.controller.JsonService;
import com.cecs.controller.Proxy;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    private final String jasonBuffer = "{ \"remoteMethod\": \"getSongChunk\", \"objectName\": \"SongServices\", \"param\": { \"song\": \"SOMZWCG12A8C13C480\", \"fragment\": 2 } }";
    private final String jasonLogin = "{ \"remoteMethod\": \"login\", \"objectName\": \"UserServices\", \"param\": { \"username\": \"chris\", \"password\": \"greer\" } }";

    @Test
    void testDispatch() {
        var dispatch = new Communication();
        var ret = dispatch.dispatch(jasonBuffer);
        var parser = new JsonParser();
        var request  = parser.parse(ret).getAsJsonObject();
        var bytes = JsonService.unpackBytes(request);

        assertEquals(8192, bytes.length);
    }

    @Test
    void testLogin() {
        var dispatch = new Communication();
        var ret = dispatch.dispatch(jasonLogin);
        var parser = new JsonParser();
        var request  = parser.parse(ret).getAsJsonObject();
        var playlists = JsonService.unpackPlaylists(request);

        if (playlists == null) {
            System.out.println("This user doesn't exist");
        } else if (playlists.length == 0) {
            System.out.println("This user does not have any Playlists");
        } else {
            System.out.println("This user has " + playlists.length + " playlist(s)");
        }
    }

    @Test
    void testProxy() {
        var proxy = new Proxy(new Communication(), "UserServices");
        var params = new String[] {"chris", "greer"};
        var request = proxy.synchExecution("login", params);
        var user = JsonService.unpackUser(request);
        if (user != null) {
            System.out.println(user.username + " logged in!");
        }
    }

    @AfterAll
    static void closeServer() {
        var dispatch = new Communication();
        dispatch.send("end");
    }
}
