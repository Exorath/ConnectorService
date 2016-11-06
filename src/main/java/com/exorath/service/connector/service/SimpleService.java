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

package com.exorath.service.connector.service;

import com.exorath.service.connector.Service;
import com.exorath.service.connector.res.*;
import com.google.gson.Gson;

/**
 * Created by toonsev on 11/5/2016.
 */
public class SimpleService implements Service {
    private static final Gson GSON = new Gson();
    private DatabaseProvider databaseProvider;

    public SimpleService(DatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    public ServerInfo getServerInfo(Filter filter, Long minLastUpdate) {
        String filterId = GSON.toJson(filter);
        ServerInfo oldServerInfo = databaseProvider.getServerInfo(filterId);

        if (minLastUpdate != null && (oldServerInfo == null || oldServerInfo.getLastUpdate() == null || oldServerInfo.getLastUpdate() < minLastUpdate)) {//Server record should be updated
            ServerInfo updatedInfo = generateServerInfoFromDB(filter);//Parse server info from database
            updateServerInfoToDB(filterId, updatedInfo);
            return updatedInfo;
        } else {
            if (oldServerInfo == null)
                return new ServerInfo(0, 0, 0, 0, 0l);
            if (oldServerInfo.getLastUpdate() == null)
                throw new IllegalStateException("Get server info returned database entry without lastUpdate");
            return oldServerInfo;
        }
    }

    private void updateServerInfoToDB(String filterId, ServerInfo serverInfo) {
        databaseProvider.updateServerInfo(filterId, serverInfo);
    }

    private ServerInfo generateServerInfoFromDB(Filter filter) {
        Iterable<Server> servers = databaseProvider.getServersWithFilter(filter);
        if (servers == null)
            throw new IllegalStateException("Failed to fetch servers from database while constructing server info");
        return generateServerInfo(servers);
    }

    private ServerInfo generateServerInfo(Iterable<Server> servers) {
        int playerCount = 0;
        int serverCount = 0;
        int openServerCount = 0;
        int openSlotCount = 0;
        boolean infinitePlayers = false;
        for (Server server : servers) {
            if(server.getExpiry() < System.currentTimeMillis())
                continue;
            playerCount += server.getPlayerCount();
            serverCount++;
            if (server.isJoinable()) {
                openServerCount++;
                if (server.getMaxPlayerCount() == -1)
                    infinitePlayers = true;
                else if (server.getMaxPlayerCount() > server.getPlayerCount())
                    openSlotCount += (server.getMaxPlayerCount() - server.getPlayerCount());

            }
        }
        ServerInfo serverInfo = new ServerInfo(playerCount, serverCount, openServerCount, openSlotCount, System.currentTimeMillis());
        if(infinitePlayers == true)
            serverInfo.setOpenSlotCount(-1);
        return serverInfo;
    }

    public Success updateServer(Server server) {
        return databaseProvider.putServer(server);
    }

    public JoinSuccess joinServer(String uuid, Filter filter) {
        if(filter.isLobby() == null)
            filter.setLobby(false);
        Server serverToJoin = databaseProvider.getJoinableServer(filter);
        if (serverToJoin == null)
            return new JoinSuccess(false, null, "Failed to find a valid server.");
        if (serverToJoin.getServerId() == null)
            return new JoinSuccess(false, null, "Received server without serverId.");
        if (!serverToJoin.isJoinable())
            return new JoinSuccess(false, null, "Found server, but it's not joinable.");
        if (serverToJoin.getMaxPlayerCount() <= serverToJoin.getPlayerCount())
            return new JoinSuccess(false, null, "Found server, but there are too many players.");
        //TODO: Dispatch join request through ActionAPIProvider
        return new JoinSuccess(true, serverToJoin.getServerId());
    }
}
