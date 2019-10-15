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
    private final Communication comm = new Communication();

    @Test
    void testDispatch() {
        var ret = comm.dispatch(jasonBuffer, Communication.Semantic.AT_MOST_ONCE);
        var parser = new JsonParser();
        var request = parser.parse(ret).getAsJsonObject();
        var bytes = JsonService.unpackBytes(request);

        assertEquals(8192, bytes.length);
    }

    @Test
    void testLogin() {
        var ret = comm.dispatch(jasonLogin, Communication.Semantic.AT_MOST_ONCE);
        var parser = new JsonParser();
        var request = parser.parse(ret).getAsJsonObject();
        var user = JsonService.unpackUser(request);

        if (user == null) {
            System.out.println("Login failed");
        } else if (user.userPlaylists.size() == 0) {
            System.out.println("This user does not have any Playlists");
        } else {
            System.out.format("This user has %d playlist(s)", user.userPlaylists.size());
        }
    }

    @Test
    void testProxy() {
        var proxy = new Proxy(comm, "UserServices", Communication.Semantic.AT_MOST_ONCE);
        var params = new String[] { "chris", "greer" };
        var request = proxy.synchExecution("login", params);
        var user = JsonService.unpackUser(request);
        if (user != null) {
            System.out.println(user.username + " logged in!");
        }
    }

    @Test
    void testMusicChunk() {
        var proxy = new Proxy(comm, "MusicServices", Communication.Semantic.AT_LEAST_ONCE);
        var params = new String[] { "0", "20" };
        var request = proxy.synchExecution("loadChunk", params);
        var music = JsonService.unpackMusic(request);

        assertEquals(20, music.length);
    }

    @AfterAll
    void closeServer() {
        comm.send("end");
    }
}
