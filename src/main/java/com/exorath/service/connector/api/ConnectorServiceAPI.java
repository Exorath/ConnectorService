/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

    public ServerInfo getServerInfo(Filter filter, Long minLastUpdate) {
        try {
            HttpRequestWithBody req = Unirest.put(url("/info"));
            if (filter.getGameId() != null)
                req.queryString("gameId", filter.getGameId());
            if (filter.getMapId() != null)
                req.queryString("mapId", filter.getMapId());
            if (filter.getFlavorId() != null)
                req.queryString("flavorId", filter.getFlavorId());
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
