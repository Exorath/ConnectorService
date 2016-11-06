/*
 * Copyright 2016 Exorath
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

import com.google.gson.annotations.SerializedName;

/**
 * Created by toonsev on 11/3/2016.
 */
public class Server {
    @SerializedName("serverId")
    private String serverId;
    @SerializedName("gameId")
    private String gameId;
    @SerializedName("socket")
    private String socket;
    @SerializedName("joinable")
    private boolean joinable;
    @SerializedName("mapId")
    private String mapId = null;
    @SerializedName("flavorId")
    private String flavorId = null;
    @SerializedName("expiry")
    private long expiry;
    @SerializedName("players")
    private String[] players;
    @SerializedName("pc")
    private int playerCount;
    @SerializedName("mpc")
    private int maxPlayerCount = -1;
    @SerializedName("lobby")
    private boolean lobby = false;

    public Server(){}
    //Todo: Factory?
    public Server(String serverId, String gameId, String mapId, String flavorId, String socket, boolean joinable, long expiry, String[] players, int playerCount, int maxPlayerCount, boolean lobby){
        this.serverId = serverId;
        this.gameId = gameId;
        this.mapId = mapId;
        this.flavorId = flavorId;
        this.socket = socket;
        this.joinable = joinable;
        this.expiry = expiry;
        this.players = players;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.lobby = lobby;
    }
    public String getServerId() {
        return serverId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getSocket() {
        return socket;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public String getMapId() {
        return mapId;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public long getExpiry() {
        return expiry;
    }

    public String[] getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public boolean isLobby() {
        return lobby;
    }
}
