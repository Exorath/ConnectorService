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

package com.exorath.service.connector.res;

import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by toonsev on 2/4/2017.
 */
public class BasicServer {
    @Id
    private String serverId;

    @Indexed
    private String gameId;
    @Indexed
    private String mapId;
    @Indexed
    private String flavorId;
    private String socket;

    public BasicServer() {}

    public BasicServer(String serverId, String gameId, String mapId, String flavorId, String socket) {
        this.serverId = serverId;
        this.gameId = gameId;
        this.mapId = mapId;
        this.flavorId = flavorId;
        this.socket = socket;
    }

    public BasicServer(String gameId, String mapId, String flavorId) throws UnknownHostException {
        this(UUID.randomUUID().toString(), gameId, mapId, flavorId,   InetAddress.getLocalHost().getHostAddress().toString() + ":25565");
        this.gameId = gameId;
        this.mapId = mapId;
        this.flavorId = flavorId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getServerId() {
        return serverId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getMapId() {
        return mapId;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public String getSocket() {
        return socket;
    }
}
