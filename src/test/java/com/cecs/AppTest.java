package com.cecs;

import com.cecs.controller.Dispatcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    private final String jason =
    "{ " +
    "\"remoteMethod\": \"getSongChunk\", " +
    "\"objectName\": \"SongServices\", " +
    "\"param\": { " +
    "\"song\": \"SOMZWCG12A8C13C480.mp3\", \"fragment\": 2 " +
    "} " +
    "}";

    @Test
    void testDispatch() {
        var dispatch = new Dispatcher();
        var ret = dispatch.dispatch(jason);
        System.out.println("Packet: " + ret);

        // assertEquals(jason, ret);
        assertEquals(ret, "Hello from server!");
    }

    @AfterAll
    static void closeServer() {
        var dispatch = new Dispatcher();
        dispatch.send("end");
    }
//    @Test
//    public void testProxy() throws IOException, JavaLayerException {
//        Dispatcher dispatcher = new Dispatcher();
//        var file = new File("SOMZWCG12A8C13C480.mp3");
//        var proxy = new Proxy(dispatcher);
//        SongDispatcher songDispatcher = new SongDispatcher();
//        dispatcher.registerObject(songDispatcher, "SongServices");
//
//        var stream = new CECS327InputStream2(file, proxy);
//        var player = new Player(stream);
//        player.play();
//    }
}
