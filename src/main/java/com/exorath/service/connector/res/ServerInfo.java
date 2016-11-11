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
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

/**
 * Created by toonsev on 11/3/2016.
 */
@Entity
public class ServerInfo {
    @Id
    private String id;
    @Property("pc")
    @SerializedName("pc")
    private Integer playerCount;
    @Property("sc")
    @SerializedName("sc")
    private Integer serverCount;
    @Property("osc")
    @SerializedName("osc")
    private Integer openServerCount;
    @Property("opc")
    @SerializedName("opc")
    private Integer openSlotCount;
    @SerializedName("lastUpdate")
    private Long lastUpdate;

    public ServerInfo(){

    }
    public ServerInfo(Integer playerCount, Integer serverCount, Integer openServerCount, Integer openSlotCount, Long lastUpdate) {
        this.playerCount = playerCount;
        this.serverCount = serverCount;
        this.openServerCount = openServerCount;
        this.openSlotCount = openSlotCount;
        this.lastUpdate = lastUpdate;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public Integer getServerCount() {
        return serverCount;
    }

    public Integer getOpenServerCount() {
        return openServerCount;
    }

    public Integer getOpenSlotCount() {
        return openSlotCount;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setOpenSlotCount(Integer openSlotCount) {
        this.openSlotCount = openSlotCount;
    }
}
