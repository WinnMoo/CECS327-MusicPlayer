package com.cecs.controller;

import com.cecs.def.ProxyInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Proxy implements ProxyInterface {
    private Dispatcher dispatcher; // This is only for test. it should use the Communication Module
    private String objectName;

    public Proxy(Dispatcher dispatcher, String objectName) {
        this.dispatcher = dispatcher;
        this.objectName = objectName;
    }

    /*
     * Executes the remote method "remoteMethod". The method blocks until it
     * receives the reply of the message.
     */
    public JsonObject synchExecution(String remoteMethod, String[] param) {
        JsonObject jsonRequest = new JsonObject();
        JsonObject jsonParam = new JsonObject();

        jsonRequest.addProperty("remoteMethod", remoteMethod);
        jsonRequest.addProperty("objectName", objectName);
        // It is hardcoded. Instead it should be dynamic using RemoteRef
        if (remoteMethod.equals("getSongChunk")) {

            jsonParam.addProperty("song", param[0]);
            jsonParam.addProperty("fragment", param[1]);

        }
        if (remoteMethod.equals("getFileSize")) {
            jsonParam.addProperty("song", param[0]);
        }
        if (remoteMethod.equals("login")) {
            jsonParam.addProperty("username", param[0]);
            jsonParam.addProperty("password", param[1]);
        }
        jsonRequest.add("param", jsonParam);

        JsonParser parser = new JsonParser();
        String strRet = this.dispatcher.dispatch(jsonRequest.toString());

        return parser.parse(strRet).getAsJsonObject();
    }

    /*
     * Executes the remote method remoteMethod and returns without waiting for the
     * reply. It does similar to synchExecution but does not return any value
     *
     */
    public void asynchExecution(String remoteMethod, String[] param) {
        return;
    }
}

