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
 * Created by toonsev on 11/6/2016.
 */
public class Filter {
    @SerializedName("g")
    private String gameId;
    @SerializedName("m")
    private String mapId;
    @SerializedName("f")
    private String flavorId;
    @SerializedName("l")
    private Boolean lobby;

    public Filter withGameId(String gameId){
        this.gameId = gameId;
        return this;
    }

    public Filter withMapId(String mapId){
        this.mapId = mapId;
        return this;
    }


    public Filter withFlavorId(String flavorId){
        this.flavorId = flavorId;
        return this;
    }

    public Filter withIsLobby(boolean lobby){
        this.lobby = lobby;
        return this;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getMapId() {
        return mapId;
    }

    public Boolean isLobby() {
        return lobby;
    }

    public void setLobby(Boolean lobby) {
        this.lobby = lobby;
    }
}
