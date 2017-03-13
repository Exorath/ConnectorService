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

package com.exorath.service.connector.service;

import com.exorath.service.actionapi.api.ActionAPIServiceAPI;
import com.exorath.service.actionapi.api.bungee.JoinAction;
import com.exorath.service.actionapi.res.Action;
import com.exorath.service.connector.Service;
import com.exorath.service.connector.res.*;
import com.google.gson.Gson;

/**
 * Created by toonsev on 11/5/2016.
 */
public class SimpleService implements Service {
    private static final Gson GSON = new Gson();
    private DatabaseProvider databaseProvider;
    private ActionAPIServiceAPI actionAPIServiceAPI;

    public SimpleService(DatabaseProvider databaseProvider, ActionAPIServiceAPI actionAPIServiceAPI) {
        this.databaseProvider = databaseProvider;
        this.actionAPIServiceAPI = actionAPIServiceAPI;
    }

    public ServerInfo getServerInfo(Filter filter, Long minLastUpdate) {
        ServerInfo oldServerInfo = databaseProvider.getServerInfo(filter, minLastUpdate);
        return oldServerInfo;
    }



    public Success updateServer(Server server) {
        return databaseProvider.putServer(server);
    }

    public JoinSuccess joinServer(String uuid, Filter filter) {
        Server serverToJoin = databaseProvider.getJoinableServer(filter, uuid);
        if (serverToJoin == null)
            return new JoinSuccess(false, null, "Failed to find a valid server.");
        if (serverToJoin.getServerId() == null)
            return new JoinSuccess(false, null, "Received server without serverId.");
        if (!serverToJoin.isJoinable())
            return new JoinSuccess(false, null, "Found server, but it's not joinable.");
        if (serverToJoin.getMaxPlayerCount() != 0 && serverToJoin.getMaxPlayerCount() <= serverToJoin.getPlayerCount())
            return new JoinSuccess(false, null, "Found server, but there are too many players.");
        if(actionAPIServiceAPI != null)
            actionAPIServiceAPI.publishAction(new JoinAction(uuid, serverToJoin.getSocket()));
        return new JoinSuccess(true, serverToJoin.getServerId());
    }

    @Override
    public Server getJoinableServer(Filter filter, String uuid) {
        return databaseProvider.getJoinableServer(filter, uuid);
    }
}