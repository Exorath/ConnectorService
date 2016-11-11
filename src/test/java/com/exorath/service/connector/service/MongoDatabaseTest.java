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
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.*;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by toonsev on 11/9/2016.
 */
public class MongoDatabaseTest {
    private static final MongodStarter starter = MongodStarter.getInstance(new RuntimeConfigBuilder().defaults(Command.MongoD).processOutput(new ProcessOutput(Processors.silent(),
            Processors.namedConsole("[MONGOD>]"), Processors.silent())).build());

    private static final String dbName = "testdb";
    private static final String collName = "testcoll";

    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;

    private MongoClient mongo;
    private SimpleService service;

    @Before
    public void setup() throws Exception {
        int port = Network.getFreeServerPort();
        _mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net("localhost", port, false))
                .build());
        _mongod = _mongodExe.start();
        mongo = new MongoClient("localhost", port);
        service = new SimpleService(new MongoDatabaseProvider(mongo, dbName, collName));
    }

    @After
    public void cleanup() {
        _mongod.stop();
        _mongodExe.stop();
    }

    @Test
    public void addTwoServersWithoutFilterTest() {

        service.updateServer(new Server("sid", "gid", "mid", "fid", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("sid2", "gid", "mid", "fid", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        ServerInfo serverInfo = service.getServerInfo(new Filter().withGameId("gid"), 0l);
        assertEquals(10, serverInfo.getPlayerCount().intValue());
        assertEquals(14, serverInfo.getOpenSlotCount().intValue());
        assertEquals(2, serverInfo.getServerCount().intValue());
        assertEquals(2, serverInfo.getOpenServerCount().intValue());
    }

    @Test
    public void addTwoServersWithFilterTest() {
        service.updateServer(new Server("sid", "gid", "mid", "fid", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("sid2", "gid", "mid", "fid", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        ServerInfo serverInfo = service.getServerInfo(new Filter().withGameId("gid"), 0l);
        assertEquals(10, serverInfo.getPlayerCount().intValue());
        assertEquals(14, serverInfo.getOpenSlotCount().intValue());
        assertEquals(2, serverInfo.getServerCount().intValue());
        assertEquals(2, serverInfo.getOpenServerCount().intValue());
    }

    @Test
    public void joinServerReturnsFalseSuccessByDefaultTest() {
        Success result = service.joinServer("testUuid", new Filter());
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerTest() {
        service.updateServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter());
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsFalseSuccessWithOpenServerBecauseWrongGameIdFilterTest() {
        service.updateServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withGameId("notfound"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerBecauseRightGameIdFilterTest() {
        service.updateServer(new Server("randomSid", "foundID", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withGameId("foundID"));
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsFalseSuccessWithOpenServerBecauseWrongMapIdFilterTest() {
        service.updateServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withGameId("notfound"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerBecauseRightMapIdFilterTest() {
        service.updateServer(new Server("randomSid", "foundID", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withMapId("mapId"));
        System.out.println(result.getError());
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsFalseSuccessWithOpenServerBecauseWrongFlavorIdFilterTest() {
        service.updateServer(new Server("randomSid", "gameId", "mapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withGameId("notfound"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void joinServerReturnsTrueSuccessWithOpenServerBecauseRightFlavorFilterTest() {
        service.updateServer(new Server("randomSid", "foundID", "mapId", "flavorId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        Success result = service.joinServer("testUuid", new Filter().withFlavorId("flavorId"));
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsRightServerIdWithAllFiltersAndManyServersTest() {
        String serverId = "rightServerId";
        String gameId = "thisIsTheGameId";
        String mapId = "thisIsTheMapId";
        String flavorId = "thisIsTheFlavorId";
        service.updateServer(new Server("randomSid", gameId, mapId, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id2", gameId, "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id3", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id4", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id6", gameId, null, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id7", "wrongGameId", "wrongmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id8", gameId, null, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server(serverId, gameId, mapId, flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));

        service.updateServer(new Server("randomSid", "right", "mapId", "flavorId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        JoinSuccess result = service.joinServer("testUuid", new Filter().withGameId(gameId).withMapId(mapId).withFlavorId(flavorId));
        assertEquals(serverId, result.getServerId());
        assertTrue(result.isSuccess());
    }

    @Test
    public void joinServerReturnsNoServerIdWithAllFiltersAndManyServersTest() {
        String serverId = "rightServerId";
        String gameId = "thisIsTheGameId";
        String mapId = "thisIsTheMapId";
        String flavorId = "thisIsTheFlavorId";
        service.updateServer(new Server("randomSid", gameId, mapId, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id2", gameId, "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id3", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id4", "wrongGameId", "wrongmapId", "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id6", gameId, null, "wrongId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id7", "wrongGameId", "wrongmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        service.updateServer(new Server("id8", gameId, null, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));

        service.updateServer(new Server("randomSid", "right", "mapId", "flavorId", "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 12));
        JoinSuccess result = service.joinServer("testUuid", new Filter().withGameId(gameId).withMapId(mapId).withFlavorId(flavorId));
        assertNull(result.getServerId());
        assertFalse(result.isSuccess());
    }

    //putServerTest
    @Test
    public void updateServerUpdatesDBTest() {
        Server server = new Server("serverId", "gameId", null, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, new String[0], 0, 16);
        service.updateServer(server);
        assertEquals(1, mongo.getDatabase(dbName).getCollection(collName).count());
    }

    //getServerInfo
    @Test
    public void getServerInfoWithoutFiltersTest() {
        service.updateServer(new Server("randomSid", "gameId1", "blahmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", "gameId1", "blahmapId", "flavorId1", "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(5), 7, 12));
        service.updateServer(new Server("id2", "gameId2", "wrongmapId", "flavorId2", "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter(), 0l);
        assertEquals(2, info.getOpenServerCount().intValue());
        assertEquals(3, info.getServerCount().intValue());
        assertEquals(16, info.getOpenSlotCount().intValue());
        assertEquals(20, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdFilterTest() {
        String gameId = "ThisIsAspecificGameId";
        service.updateServer(new Server("id0", gameId, "blahmapId", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id2", "gameId1", "TheMap", "flavorId1", "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        service.updateServer(new Server("id3", gameId, "wrongmapId", "flavorId2", "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId), 0l);
        assertEquals(2, info.getOpenServerCount().intValue());
        assertEquals(3, info.getServerCount().intValue());
        assertEquals(21, info.getOpenSlotCount().intValue());
        assertEquals(15, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdAndMapIdFilterTest() {
        String gameId = "ThisIsAspecificGameId";
        String mapId = "ThisIsAspecificMaoId";
        service.updateServer(new Server("id0", gameId, mapId, null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id2", "gameId1", "TheMap", "flavorId1", "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        service.updateServer(new Server("id3", gameId, mapId, "flavorId2", "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withMapId(mapId), 0l);
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
        service.updateServer(new Server("id0", gameId, mapId, flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id2", "gameId1", "TheMap", flavorId, "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        service.updateServer(new Server("id3", gameId, mapId, flavorId, "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withMapId(mapId).withFlavorId(flavorId), 0l);
        assertEquals(1, info.getOpenServerCount().intValue());
        assertEquals(2, info.getServerCount().intValue());
        assertEquals(11, info.getOpenSlotCount().intValue());
        assertEquals(13, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdAndFlavorIdFilterTest() {
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id2", "gameId1", "TheMap", flavorId, "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        service.updateServer(new Server("id3", gameId, "map2", flavorId, "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withFlavorId(flavorId), 0l);
        assertEquals(1, info.getOpenServerCount().intValue());
        assertEquals(2, info.getServerCount().intValue());
        assertEquals(11, info.getOpenSlotCount().intValue());
        assertEquals(13, info.getPlayerCount().intValue());
    }

    @Test
    public void getServerInfoWithGameIdAndFlavorIdFilterWithOutDatedServerTest() {
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "randMap", null, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id2", "gameId1", "TheMap", flavorId, "dns.com:25566", true, System.currentTimeMillis() + 5000, getPlayers(12), 5, 12));
        service.updateServer(new Server("id3", gameId, "map2", flavorId, "dns.com:25567", false, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        service.updateServer(new Server("id4", gameId, "map2", flavorId, "dns.com:25568", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        service.updateServer(new Server("id5", gameId, "map2", flavorId, "dns.com:25569", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        service.updateServer(new Server("id6", gameId, "map2", flavorId, "dns.com:25560", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        service.updateServer(new Server("id7", gameId, "map2", flavorId, "dns.com:25561", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        service.updateServer(new Server("id8", gameId, "map2", flavorId, "dns.com:25562", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        service.updateServer(new Server("id9", gameId, "map2", flavorId, "dns.com:25563", false, System.currentTimeMillis() - 5000, getPlayers(12), 12, 12));
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withFlavorId(flavorId), 0l);
        assertEquals(1, info.getOpenServerCount().intValue());
        assertEquals(2, info.getServerCount().intValue());
        assertEquals(11, info.getOpenSlotCount().intValue());
        assertEquals(13, info.getPlayerCount().intValue());
    }
    @Test
    public void getServerInfoReturnedNewVersionOfDataBecauseMinimumTimeIsSetTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.getServerInfo(new Filter().withGameId(gameId).withFlavorId(flavorId), 0l);
        service.updateServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        Thread.sleep(1000);
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withFlavorId(flavorId), System.currentTimeMillis() - 500);
        assertEquals(5, info.getPlayerCount().intValue());
    }
    @Test
    public void getServerInfoWithExpiredServersTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 500, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));//right
        service.updateServer(new Server("id2", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));//right
        service.updateServer(new Server("id3", "wrongid", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("id4", gameId, "map1", "wrongflavor", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("id5", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));//right
        service.updateServer(new Server("id6", "wronggame", "map1", "wrongflavor", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        Thread.sleep(1000);
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withFlavorId(flavorId), 0l);
        assertEquals(20, info.getPlayerCount().intValue());
        assertEquals(16, info.getOpenSlotCount().intValue());
        assertEquals(3, info.getServerCount().intValue());
        assertEquals(2, info.getOpenServerCount().intValue());
    }

    @Test
    public void getServerInfoDoesNotExpiredAndNotJoinableServersTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 500, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));//right
        service.updateServer(new Server("id2", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));//right
        service.updateServer(new Server("id3", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 5000, getPlayers(6), 6, 12));//right
        service.updateServer(new Server("id4", "wrongid", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("id5", gameId, "map1", "wrongflavor", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("id6", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));//right
        service.updateServer(new Server("id7", "wronggame", "map1", "wrongflavor", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        Thread.sleep(1000);
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withFlavorId(flavorId), 0l);
        assertEquals(26, info.getPlayerCount().intValue());
        assertEquals(16, info.getOpenSlotCount().intValue());
        assertEquals(4, info.getServerCount().intValue());
        assertEquals(2, info.getOpenServerCount().intValue());
    }
    @Test
    public void getServerInfoDoesNotExpiredAndNotJoinableServersTest2() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 500, getPlayers(1), 1, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));//right
        service.updateServer(new Server("id2", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));//right
        service.updateServer(new Server("id3", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 5000, getPlayers(6), 6, 12));//right
        service.updateServer(new Server("ida1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));//right
        service.updateServer(new Server("id4", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 500, getPlayers(6), 6, 12));
        service.updateServer(new Server("id5", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 300, getPlayers(6), 6, 12));
        service.updateServer(new Server("id6", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 300, getPlayers(6), 6, 12));
        service.updateServer(new Server("id7", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 300, getPlayers(6), 6, 12));
        service.updateServer(new Server("id8", "wrongid", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("id9", gameId, "map1", "wrongflavor", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        service.updateServer(new Server("id10", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));//right
        service.updateServer(new Server("id11", "wronggame", "map1", "wrongflavor", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(5), 5, 12));
        Thread.sleep(1000);
        ServerInfo info = service.getServerInfo(new Filter().withGameId(gameId).withFlavorId(flavorId), 0l);
        assertEquals(38, info.getPlayerCount().intValue());
        assertEquals(16, info.getOpenSlotCount().intValue());
        assertEquals(5, info.getServerCount().intValue());
        assertEquals(2, info.getOpenServerCount().intValue());
    }
    @Test
    public void joinServerDefaultFalseSuccessTest() throws Exception{
        JoinSuccess success = service.joinServer("uuid1", new Filter());
        assertFalse(success.isSuccess());
    }

    @Test
    public void joinServerWithFilterDefaultFalseSuccessTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";

        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertFalse(success.isSuccess());
    }
    @Test
    public void joinServerWithOneGoodServerTest() throws Exception{
        service.updateServer(new Server("id1", "gid", "map1", "fid", "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        JoinSuccess success = service.joinServer("uuid1", new Filter());
        assertTrue(success.isSuccess());
        assertEquals("id1", success.getServerId());
    }
    @Test
    public void joinServerWithOneGoodServerAndFilterTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertTrue(success.isSuccess());
        assertEquals("id1", success.getServerId());
    }
    @Test
    public void joinServerWithOneGoodOneBadServerAndFilterTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", "wrongGameId", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertTrue(success.isSuccess());
        assertEquals("id1", success.getServerId());
    }
    @Test
    public void joinServerWithOneGoodOutdatedOneBadServerAndFilterTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", "wrongGameId", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 500, getPlayers(3), 3, 12));
        Thread.sleep(1000);
        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertFalse(success.isSuccess());
    }
    @Test
    public void joinServerWithOneNotJoinableAndBadServerAndFilterTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", "wrongGameId", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertFalse(success.isSuccess());
    }
    @Test
    public void joinServerWithOneFillAndBadServerAndFilterTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", "wrongGameId", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(12), 12, 12));
        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertFalse(success.isSuccess());
    }
    @Test
    public void joinServerWithoodAndOneBadAndFilterPicksBestTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", "wrongGameId", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        service.updateServer(new Server("id2", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id3", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id4", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(4), 4, 12));
        service.updateServer(new Server("id5", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id6", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id7", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertTrue(success.isSuccess());
        assertEquals("id4", success.getServerId());
    }
    @Test
    public void joinServerWithoodAndBadAndFilterPicksBestTest() throws Exception{
        String gameId = "ThisIsAspecificGameId";
        String flavorId = "ThisIsAspecificFlavorId";
        service.updateServer(new Server("id0", "wrongGameId", "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        service.updateServer(new Server("id1", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 5000, getPlayers(4), 4, 12));
        service.updateServer(new Server("id2", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id3", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id4", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 500, getPlayers(3), 3, 12));
        service.updateServer(new Server("id5", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(1), 1, 12));
        service.updateServer(new Server("id6", gameId, "map1", flavorId, "dns.com:25565", false, System.currentTimeMillis() + 5000, getPlayers(2), 2, 12));
        service.updateServer(new Server("id7", gameId, "map1", flavorId, "dns.com:25565", true, System.currentTimeMillis() + 5000, getPlayers(3), 3, 12));
        Thread.sleep(1000);
        JoinSuccess success = service.joinServer("uuid1", new Filter().withGameId(gameId).withFlavorId(flavorId));
        assertTrue(success.isSuccess());
        assertEquals("id7", success.getServerId());
    }
    private String[] getPlayers(int n) {
        String[] toReturn = new String[n];
        for (int i = 0; i < n; i++)
            toReturn[i] = UUID.randomUUID().toString();
        return toReturn;
    }
}
