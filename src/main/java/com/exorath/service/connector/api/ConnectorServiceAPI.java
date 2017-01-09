package com.exorath.service.connector.api;

import com.exorath.service.connector.res.*;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;

/**
 * Created by toonsev on 1/8/2017.
 */
public class ConnectorServiceAPI {
    private static final Gson GSON = new Gson();
    private String address;

    public ConnectorServiceAPI(String address) {
        this.address = address;
    }

    public ServerInfo getServerInfo(String gameId, String mapId, String flavorId, Long minLastUpdate) {
        try {
            HttpRequestWithBody req = Unirest.put(url("/info"));
            if (gameId != null)
                req.queryString("gameId", gameId);
            if (mapId != null)
                req.queryString("mapId", mapId);
            if (flavorId != null)
                req.queryString("flavorId", flavorId);
            if (minLastUpdate != null)
                req.queryString("minLastUpdate", minLastUpdate);
            String body = req.asString().getBody();
            ServerInfo serverInfo = GSON.fromJson(body, ServerInfo.class);
            return serverInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String joinServer(String uuid, Filter filter) {
        try {
            HttpResponse<String> res = Unirest.put(url("/join/{uuid}"))
                    .routeParam("uuid", uuid)
                    .body(GSON.toJson(filter))
                    .asString();
            String body = res.getBody();
            JoinSuccess success = GSON.fromJson(body, JoinSuccess.class);
            return success.getServerId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean putServer(Server server) {
        try {
            HttpResponse<String> res = Unirest.put(url("/servers/{serverId}"))
                    .routeParam("serverId", server.getServerId())
                    .body(GSON.toJson(server))
                    .asString();
            String body = res.getBody();
            Success success = GSON.fromJson(body, Success.class);
            return success.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private String url(String endpoint) {
        return address + endpoint;
    }
}
