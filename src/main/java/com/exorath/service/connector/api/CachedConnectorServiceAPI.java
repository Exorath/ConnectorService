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

import com.exorath.service.connector.res.Filter;
import com.exorath.service.connector.res.Server;
import com.exorath.service.connector.res.ServerInfo;

import java.util.HashMap;


/**
 * Created by toonsev on 1/11/2017.
 */
public class CachedConnectorServiceAPI extends ConnectorServiceAPI {
    private HashMap<Filter, ServerInfo> serverInfoByFilter = new HashMap<>();

    public CachedConnectorServiceAPI(String address) {
        super(address);
    }

    /**
     * This is locally cached.
     */
    @Override
    public ServerInfo getServerInfo(Filter filter, Long minLastUpdate) {
        ServerInfo serverInfo = serverInfoByFilter.get(filter);
        if (serverInfo == null || serverInfo.getLastUpdate() == null || serverInfo.getLastUpdate() < minLastUpdate) {
            serverInfo = super.getServerInfo(filter, minLastUpdate);
            if (serverInfo != null)
                serverInfoByFilter.put(filter, serverInfo);
        }
        return serverInfo;
    }

    @Override
    public String joinServer(String uuid, Filter filter) {
        return super.joinServer(uuid, filter);
    }

    @Override
    public boolean putServer(Server server) {
        return super.putServer(server);
    }
}