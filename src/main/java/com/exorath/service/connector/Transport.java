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

package com.exorath.service.connector;

import com.exorath.service.commons.portProvider.PortProvider;
import com.exorath.service.connector.res.Filter;
import com.exorath.service.connector.res.Server;
import com.google.gson.Gson;
import spark.Route;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

/**
 * Created by toonsev on 11/3/2016.
 */
public class Transport {
    private static final Gson GSON = new Gson();

    public static void setup(Service service, PortProvider portProvider) {
        port(portProvider.getPort());

        get("/info", getGetServerInfoRoute(service), GSON::toJson);
        get("/joinable/:uuid", getGetJoinableServerRoute(service), GSON::toJson);
        put("/servers/:serverId", getPutServerRoute(service), GSON::toJson);
        put("/join/:uuid", getJoinServerRoute(service), GSON::toJson);
    }

    private static Route getGetJoinableServerRoute(Service service) {
        return (req, res) -> {
            Filter filter = new Filter()
                    .withGameId(req.queryParams("gameId"))
                    .withFlavorId(req.queryParams("flavorId"))
                    .withMapId(req.queryParams("mapId"));
            return service.joinServer(req.params("uuid"), filter);
        };
    }

    public static Route getGetServerInfoRoute(Service service) {
        return (req, res) -> {
            Filter filter = new Filter()
                    .withGameId(req.queryParams("gameId"))
                    .withFlavorId(req.queryParams("flavorId"))
                    .withMapId(req.queryParams("mapId"));
            Long minLastUpdate = null;
            try {
                minLastUpdate = Long.valueOf(req.queryParams("minLastUpdate"));
            } catch (Exception e) {
            }
            return service.getServerInfo(filter, minLastUpdate);
        };
    }

    public static Route getPutServerRoute(Service service) {
        return (req, res) -> {
            Server server = GSON.fromJson(req.body(), Server.class);
            server.setServerId(req.params("serverId"));
            return service.updateServer(server);
        };
    }

    public static Route getJoinServerRoute(Service service) {
        return (req, res) -> {
            String uuid = req.params("uuid");
            Filter filter = GSON.fromJson(req.body(), Filter.class);
            return service.joinServer(uuid, filter);
        };
    }
}
