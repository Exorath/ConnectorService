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

import com.exorath.service.connector.res.*;

/**
 * Created by toonsev on 11/3/2016.
 */
public interface Service {

    /**
     * Gets general information of a collection of server instances, the gameId, mapId and flavorId act as filters for this data.
     * Returns ServerInfo with all values zero'ed if there was no server data found.
     * Throws exceptions if a database error happened.
     * @param filter The filter to receive the server info of
     * @param minLastUpdate data returned should be younger then this timestamp (otherwise it must be re-fetched), nullable
     * @return the serverInfo, up to date according the the minLastUpdate timestamp
     */
    ServerInfo getServerInfo(Filter filter, Long minLastUpdate);

    /**
     * Updates the database with the provided server record.
     * @param server the server record to put in the database
     * @return whether or not the update was successful
     */
    Success updateServer(Server server);

    /**
     * Makes a player join a specific server type.
     * The player count of this server will be incremented, all though the player may leave the network before the connection is made (the player count will be slightly faulty)
     * @param uuid the uniqueId of the player to join a game
     * @param filter the filter to find
     * @return the success of this operation (it is not guaranteed that the player was actually send, but a request to do so was) + the server id the player will be connected to
     */
    JoinSuccess joinServer(String uuid, Filter filter);
}
