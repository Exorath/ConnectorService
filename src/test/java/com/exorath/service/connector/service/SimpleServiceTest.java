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

import com.exorath.service.connector.res.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by toonsev on 11/5/2016.
 */
public class SimpleServiceTest {
    private MemDatabaseProvider dbProvider;
    private SimpleService service;

    @Before
    public void setup() {
        dbProvider = new MemDatabaseProvider();
        service = new SimpleService(dbProvider);
    }

    //JoinServer tests
    @Test
    public void joinServerReturnsFalseSuccessByDefaultTest() {
        Success result = service.joinServer("testUuid", new Filter());
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerTest() {
        dbProvider.putServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter());
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsFalseSuccessWithOpenServerBecauseWrongGameIdFilterTest() {
        dbProvider.putServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter("notfound"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerBecauseRightGameIdFilterTest() {
        dbProvider.putServer(new Server("randomSid", "foundID", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter("foundID"));
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsFalseSuccessWithOpenServerBecauseWrongMapIdFilterTest() {
        dbProvider.putServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter("notfound"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerBecauseRightMapIdFilterTest() {
        dbProvider.putServer(new Server("randomSid", "foundID", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withMapId("mapId"));
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsFalseSuccessWithOpenServerBecauseWrongFlavorIdFilterTest() {
        dbProvider.putServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter("notfound"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerBecauseRightFlavorFilterTest() {
        dbProvider.putServer(new Server("randomSid", "foundID", "mapId", "flavorId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withFlavorId("flavorId"));
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsRightServerIdWithAllFiltersAndManyServersTest() {
        String serverId = "rightServerId";
        String gameId = "thisIsTheGameId";
        String mapId = "thisIsTheMapId";
        String flavorId = "thisIsTheFlavorId";
        dbProvider.putServer(new Server("randomSid", gameId, mapId, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id2", gameId, "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id3", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id4", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id6", gameId, null, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id7", "wrongGameId", "wrongmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id8", gameId, null, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server(serverId, gameId, mapId, flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));

        dbProvider.putServer(new Server("randomSid", "right", "mapId", "flavorId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        JoinSuccess result = service.joinServer("testUuid", new Filter(gameId).withMapId(mapId).withFlavorId(flavorId));
        assertEquals(serverId, result.getServerId());
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsNoServerIdWithAllFiltersAndManyServersTest() {
        String serverId = "rightServerId";
        String gameId = "thisIsTheGameId";
        String mapId = "thisIsTheMapId";
        String flavorId = "thisIsTheFlavorId";
        dbProvider.putServer(new Server("randomSid", gameId, mapId, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id2", gameId, "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id3", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id4", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id6", gameId, null, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id7", "wrongGameId", "wrongmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        dbProvider.putServer(new Server("id8", gameId, null, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));

        dbProvider.putServer(new Server("randomSid", "right", "mapId", "flavorId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        JoinSuccess result = service.joinServer("testUuid", new Filter(gameId).withMapId(mapId).withFlavorId(flavorId));
        assertNull(result.getServerId());
        assertFalse(result.isSuccess());
    }

    //putServerTest
    @Test
    public void updateServerUpdatesDBTest() {
        Server server = new Server("serverId", "gameId", null, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 16);
        service.updateServer(server);
        assertEquals(1, dbProvider.getServersByUuid().size());
        assertEquals(server, dbProvider.getServersByUuid().get("serverId"));
    }

    //getServerInfo
    @Test
    public void getServerInfoWithoutFiltersTest() {
        dbProvider.putServer(new Server("randomSid", "gameId1", "blahmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        dbProvider.putServer(new Server("id1", "gameId1", "blahmapId", "flavorId1", "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(5), 7, 12));
        dbProvider.putServer(new Server("id2", "gameId2", "wrongmapId", "flavorId2", "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter(), 0l);
        assertEquals(2, info.getOpenServerCount().intValue());
        assertEquals(3, info.getServerCount().intValue());
        assertEquals(16, info.getOpenSlotCount().intValue());
        assertEquals(20, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdFilterTest() {
        String gameId = "ThisIsAspecificGameId";
        dbProvider.putServer(new Server("id0", gameId, "blahmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        dbProvider.putServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        dbProvider.putServer(new Server("id2", "gameId1", "TheMap", "flavorId1", "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        dbProvider.putServer(new Server("id3", gameId, "wrongmapId", "flavorId2", "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter(gameId), 0l);
        assertEquals(2, info.getOpenServerCount().intValue());
        assertEquals(3, info.getServerCount().intValue());
        assertEquals(21, info.getOpenSlotCount().intValue());
        assertEquals(15, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdAndMapIdFilterTest() {
        String gameId = "ThisIsAspecificGameId";
        String mapId = "ThisIsAspecificMaoId";
        dbProvider.putServer(new Server("id0", gameId, mapId, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        dbProvider.putServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        dbProvider.putServer(new Server("id2", "gameId1", "TheMap", "flavorId1", "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        dbProvider.putServer(new Server("id3", gameId, mapId, "flavorId2", "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter(gameId).withMapId(mapId), 0l);
        assertEquals(1, info.getOpenServerCount().intValue());
        assertEquals(2, info.getServerCount().intValue());
        assertEquals(11, info.getOpenSlotCount().intValue());
        assertEquals(13, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdAndMapIdAndFlavorIdFilterTest() {
        String gameId = "ThisIsAspecificGameId";
        String mapId = "ThisIsAspecificMaoId";
        String flavorId = "ThisIsAspecificFlavorId";
        dbProvider.putServer(new Server("id0", gameId, mapId, flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        dbProvider.putServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        dbProvider.putServer(new Server("id2", "gameId1", "TheMap", flavorId, "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        dbProvider.putServer(new Server("id3", gameId, mapId, flavorId, "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter(gameId).withMapId(mapId).withFlavorId(flavorId), 0l);
        assertEquals(1, info.getOpenServerCount().intValue());
        assertEquals(2, info.getServerCount().intValue());
        assertEquals(11, info.getOpenSlotCount().intValue());
        assertEquals(13, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdAndFlavorIdFilterTest() {
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        dbProvider.putServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        dbProvider.putServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        dbProvider.putServer(new Server("id2", "gameId1", "TheMap", flavorId, "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        dbProvider.putServer(new Server("id3", gameId, "map2", flavorId, "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter(gameId).withFlavorId(flavorId), 0l);
        assertEquals(1, info.getOpenServerCount().intValue());
        assertEquals(2, info.getServerCount().intValue());
        assertEquals(11, info.getOpenSlotCount().intValue());
        assertEquals(13, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdAndFlavorIdFilterWithOutDatedServerTest() {
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        dbProvider.putServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        dbProvider.putServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        dbProvider.putServer(new Server("id2", "gameId1", "TheMap", flavorId, "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        dbProvider.putServer(new Server("id3", gameId, "map2", flavorId, "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        dbProvider.putServer(new Server("id4", gameId, "map2", flavorId, "dns.com:25568", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        dbProvider.putServer(new Server("id5", gameId, "map2", flavorId, "dns.com:25569", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        dbProvider.putServer(new Server("id6", gameId, "map2", flavorId, "dns.com:25560", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        dbProvider.putServer(new Server("id7", gameId, "map2", flavorId, "dns.com:25561", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        dbProvider.putServer(new Server("id8", gameId, "map2", flavorId, "dns.com:25562", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        dbProvider.putServer(new Server("id9", gameId, "map2", flavorId, "dns.com:25563", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter(gameId).withFlavorId(flavorId), 0l);
        assertEquals(1, info.getOpenServerCount().intValue());
        assertEquals(2, info.getServerCount().intValue());
        assertEquals(11, info.getOpenSlotCount().intValue());
        assertEquals(13, info.getPlayerCount().intValue());
    }
    @Test
    public void getServerInfoReturnedNewVersionOfDataBecauseMinimumTimeIsSetTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        dbProvider.putServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.getServerInfo(new Filter(gameId).withFlavorId(flavorId), 0l);
        dbProvider.putServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        Thread.sleep(1000);
        ServerInfo info = service.getServerInfo(new Filter(gameId).withFlavorId(flavorId), System.currentTimeMillis() - 500);
        assertEquals(5, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoEmptyAfterExpiryExpires() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        dbProvider.putServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 1000, getPlayers(1), 1, 12));
        Thread.sleep(1100);
        ServerInfo info = service.getServerInfo(new Filter(gameId).withFlavorId(flavorId), 0l);
        assertEquals(0, info.getPlayerCount().intValue());
    }
    private String[] getPlayers(int n) {
        String[] toReturn = new String[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = UUID.randomUUID().toString();
        return toReturn;
    }
}
