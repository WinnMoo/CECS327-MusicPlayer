package com.cecs.controller;

import com.cecs.def.ProxyInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Proxy implements ProxyInterface {

    private Communication communication;
    private String objectName;
    private Communication.Semantic semantic;
    private static int requestId = 0;

    public Proxy(Communication communication, String objectName, Communication.Semantic semantic) {
        this.communication = communication;
        this.objectName = objectName;
        this.semantic = semantic;
    }

    /*
     * Executes the remote method "remoteMethod". The method blocks until it
     * receives the reply of the message.
     *  add semantic call along with json request
     */
    public JsonObject synchExecution(String remoteMethod, String[] param) {
        JsonObject jsonRequest = new JsonObject();
        JsonObject jsonParam = new JsonObject();

        jsonRequest.addProperty("remoteMethod", remoteMethod);
        jsonRequest.addProperty("objectName", objectName);
        jsonRequest.addProperty("requestId", ++requestId);
        jsonRequest.addProperty("semantic", semantic.toString());
        if(requestId == Integer.MAX_VALUE) requestId = 0; // reset requestId

        // make sure that the params are in correct order.
        for(int i = 0; i < param.length; i++){
            jsonParam.addProperty("param"+i, param[i]);
        }

        jsonRequest.add("param", jsonParam);

        JsonParser parser = new JsonParser();
        String strRet = this.communication.dispatch(jsonRequest.toString(), semantic);

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

