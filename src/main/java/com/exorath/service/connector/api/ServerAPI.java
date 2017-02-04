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

import com.exorath.service.connector.res.BasicServer;
import com.exorath.service.connector.res.Server;

import java.util.concurrent.Callable;

/**
 * This is the API a server should expose
 * Created by toonsev on 2/4/2017.
 */
public class ServerAPI {
    private ConnectorServiceAPI connectorServiceAPI;
    private BasicServer basicServer;
    private Callable<Long> expiryIntervalProvider;
    public ServerAPI(ConnectorServiceAPI connectorServiceAPI, BasicServer basicServer, Callable<Long> expiryIntervalProvider){
        this.connectorServiceAPI = connectorServiceAPI;
        this.basicServer = basicServer;
        this.expiryIntervalProvider = expiryIntervalProvider;
    }
    public boolean publishServer(boolean joinable, String[] players, int maxPlayers) {
        try {
            Server server = getServer(joinable, players, maxPlayers);
            return connectorServiceAPI.putServer(server);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Server getServer(boolean joinable, String[] players, int maxPlayers) throws Exception{
        long expiry = expiryIntervalProvider.call() + System.currentTimeMillis();
        return new Server(basicServer, joinable, expiry, players, players.length, maxPlayers);
    }


}
