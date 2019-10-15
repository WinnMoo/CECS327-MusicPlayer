package com.cecs.controller;

import com.cecs.def.ProxyInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Proxy implements ProxyInterface {

    private Communication communication;
    private String objectName;
    private static int requestId = 0;

    public Proxy(Communication communication, String objectName) {
        this.communication = communication;
        this.objectName = objectName;
    }

    /*
     * Executes <code>remoteMethod(param...)</code> in a remote server. The method
     * may block until it receives the reply of the message. Passes semantic call
     * along with JSON request
     */
    public JsonObject synchExecution(String remoteMethod, String[] param, Communication.Semantic semantic) {
        JsonObject jsonRequest = new JsonObject();
        JsonObject jsonParam = new JsonObject();

        jsonRequest.addProperty("remoteMethod", remoteMethod);
        jsonRequest.addProperty("objectName", objectName);
        jsonRequest.addProperty("requestId", ++requestId);
        jsonRequest.addProperty("semantic", semantic.toString());
        if (requestId == Integer.MAX_VALUE)
            requestId = 0; // reset requestId

        // make sure that the params are in correct order.
        for (int i = 0; i < param.length; i++) {
            jsonParam.addProperty("param" + i, param[i]);
        }

        jsonRequest.add("param", jsonParam);

        JsonParser parser = new JsonParser();

        String strRet = this.communication.dispatch(jsonRequest.toString(), semantic);
        return parser.parse(strRet).getAsJsonObject();
    }

    /*
     * Executes <code>remoteMethod(param...)</code> in a remote server and returns
     * without waiting for the reply. This method does return a value.
     */
    public void asynchExecution(String remoteMethod, String[] param) {
        new Thread(() -> {
            JsonObject jsonRequest = new JsonObject();
            JsonObject jsonParam = new JsonObject();

            jsonRequest.addProperty("remoteMethod", remoteMethod);
            jsonRequest.addProperty("objectName", objectName);
            jsonRequest.addProperty("requestId", ++requestId);
            jsonRequest.addProperty("semantic", Communication.Semantic.MAYBE.toString());
            if (requestId == Integer.MAX_VALUE)
                requestId = 0; // reset requestId

            // make sure that the params are in correct order.
            for (int i = 0; i < param.length; i++) {
                jsonParam.addProperty("param" + i, param[i]);
            }

            jsonRequest.add("param", jsonParam);

            this.communication.send(jsonRequest.toString());
        }).start();
    }
}
