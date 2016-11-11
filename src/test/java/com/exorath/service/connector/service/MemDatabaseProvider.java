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

import com.exorath.service.connector.res.Filter;
import com.exorath.service.connector.res.Server;
import com.exorath.service.connector.res.ServerInfo;
import com.exorath.service.connector.res.Success;
import com.exorath.service.connector.service.DatabaseProvider;

import java.util.HashMap;
import java.util.HashSet;

/**
 * In memory mock of databaseProvider. All logic included!
 * Created by toonsev on 11/5/2016.
 */
public class MemDatabaseProvider implements DatabaseProvider {
    private HashMap<String, ServerInfo> infoByFilter = new HashMap<>();
    private HashMap<String, Server> serversByUuid = new HashMap<>();

    public ServerInfo getServerInfo(Filter filter, Long minLastUpdate) {
        int players = 0;
        int servers = 0;
        int openServers = 0;
        int openSlots = 0;
        for (Server server : getServersWithFilter(filter)) {
            servers++;
            if(server.isJoinable()){
                openServers++;
                openSlots += (server.getMaxPlayerCount() - server.getPlayerCount());
            }
            players += server.getPlayerCount();
        }
        return new ServerInfo(players, servers, openServers, openSlots, System.currentTimeMillis());
    }


    public HashMap<String, Server> getServersByUuid() {
        return serversByUuid;
    }


    private Iterable<Server> getServersWithFilter(Filter filter) {
        HashSet<Server> servers = new HashSet<>();
        for (Server server : serversByUuid.values())
            if (getServer(server, filter, false))
                servers.add(server);
        return servers;
    }

    public Success putServer(Server server) {
        serversByUuid.put(server.getServerId(), server);
        return new Success(true);
    }

    public Server getJoinableServer(Filter filter, String uuid) {
        for (Server server : serversByUuid.values()) {
            if (!getServer(server, filter, true))
                continue;
            return server;
        }
        return null;
    }

    private boolean getServer(Server server, Filter filter, boolean mustBeJoinable) {
        if (mustBeJoinable && !server.isJoinable())
            return false;
        if (server.getExpiry() <= System.currentTimeMillis())
            return false;
        if (filter.getGameId() != null && filter.getGameId() != server.getGameId())
            return false;
        if (filter.getMapId() != null && filter.getMapId() != server.getMapId())
            return false;
        if (filter.getFlavorId() != null && filter.getFlavorId() != server.getFlavorId())
            return false;
        return true;
    }
}
