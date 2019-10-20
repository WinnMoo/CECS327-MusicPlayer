package com.cecs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cecs.controller.Communication;
import com.cecs.controller.JsonService;
import com.cecs.controller.Proxy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class AppTest {
    private static final Communication comm = new Communication();

    @Test
    void testSongChunk() {
        var proxy = new Proxy(comm, "SongServices");
        var params = new String[] { "SOMZWCG12A8C13C480", "2" };
        var request = proxy.synchExecution("getSongChunk", params, Communication.Semantic.AT_LEAST_ONCE);
        var bytes = JsonService.unpackBytes(request);

        assertEquals(16384, bytes.length);
    }

    @Test
    void testLogin() {
        var proxy = new Proxy(comm, "UserServices");
        var params = new String[] { "user", "pass" };
        var request = proxy.synchExecution("login", params, Communication.Semantic.AT_LEAST_ONCE);
        var user = JsonService.unpackUser(request);

        if (user == null) {
            System.out.println("Login failed");
        } else {
            System.out.format("This user has %d playlist(s)\n", user.userPlaylists.size());
        }
    }

    @Test
    void testMusicChunk() {
        var proxy = new Proxy(comm, "MusicServices");
        var params = new String[] { "0", "20" };
        var request = proxy.synchExecution("loadChunk", params, Communication.Semantic.AT_LEAST_ONCE);
        var music = JsonService.unpackMusic(request);

        assertEquals(20, music.length);
    }

    @AfterAll
    static void closeServer() {
        comm.send("end");
    }
}
