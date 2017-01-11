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

import com.exorath.service.commons.mongoProvider.MongoProvider;
import com.exorath.service.commons.tableNameProvider.TableNameProvider;
import com.exorath.service.connector.res.Filter;
import com.exorath.service.connector.res.Server;
import com.exorath.service.connector.res.ServerInfo;
import com.exorath.service.connector.res.Success;
import com.mongodb.*;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import org.bson.BSON;
import org.bson.BsonValue;
import org.bson.Document;
import org.mongodb.morphia.*;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;

/**
 * Created by toonsev on 11/8/2016.
 */
public class MongoDatabaseProvider implements DatabaseProvider {
    final Morphia morphia = new Morphia();

    public static final String GAME_ID_FIELD = "gameId";
    public static final String MAP_ID_FIELD = "mapId";
    public static final String FLAVOR_ID_FIELD = "flavorId";
    public static final String PLAYER_COUNT_FIELD = "playerCount";
    public static final String MAX_PLAYER_COUNT_FIELD = "maxPlayerCount";
    public static final String PLAYERS_FIELD = "playerCount";
    public static final String EXPIRY_FIELD = "expiry";
    public static final String SOCKET_FIELD = "socket";
    public static final String JOINABLE_FIELD = "joinable";

    private AdvancedDatastore datastore;
    private String collectionName;

    public MongoDatabaseProvider(MongoClient client, String databaseName, String collectionName) {
        morphia.map(Server.class);
        datastore = (AdvancedDatastore) morphia.createDatastore(client, databaseName);
        this.collectionName = collectionName;
        try {
            client.getDatabase(databaseName).createCollection(collectionName);
        }catch (MongoCommandException e){
            if(e.getErrorCode() != 48)
                throw e;
        }
    }

    public MongoDatabaseProvider(MongoProvider provider, TableNameProvider databaseNameProvider, TableNameProvider collectionNameProvider) {
        this(provider.getClient(), databaseNameProvider.getTableName(), collectionNameProvider.getTableName());
    }

    @Override
    public ServerInfo getServerInfo(Filter filter, Long minLastUpdate) {
        //Fingers crossed
        BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
        if (filter.getGameId() != null)
            builder.append("gameId", filter.getGameId());
        if (filter.getMapId() != null)
            builder.append("mapId", filter.getMapId());
        if (filter.getFlavorId() != null)
            builder.append("flavorId", filter.getFlavorId());
        builder.append("expiry", new BasicDBObject("$gte", System.currentTimeMillis()));
        MapReduceCommand mapReduceCommand = new MapReduceCommand(datastore.getDB().getCollection(collectionName),
                "function(){" +
                        "var ret = {pc:0, opc:0, sc:0, osc:0};" +
                        "ret.pc = this.pc; ret.sc = 1;" +
                        "if(this.joinable && this.pc < this.mpc){ ret.opc = this.mpc - this.pc; ret.osc = 1;}" +
                        "emit('server', ret);}",
                "function(key, values){" +
                        "var ret = {pc:0, opc:0, sc:0, osc:0};" +
                        "values.forEach( function(value) {" +
                        "       ret.pc+= value.pc; ret.sc++;" +
                        "       ret.osc+= value.osc; ret.opc+= value.opc" +
                        "   });" +
                        "return ret;" +
                        "}", null, MapReduceCommand.OutputType.INLINE, builder.get());
        MapReduceOutput results = datastore.getDB().getCollection(collectionName).mapReduce(mapReduceCommand);
        if(results.getOutputCount() == 0)
            return new ServerInfo(0, 0, 0, 0, System.currentTimeMillis());
        if(results.getOutputCount() > 1)
            throw new IllegalStateException("mapReduce returned multiple results.");
        for (DBObject res : results.results()) {
            DBObject val = (DBObject) res.get("value");
            return new ServerInfo(((Double) val.get("pc")).intValue(),
                    ((Double) val.get("sc")).intValue(),
                    ((Double) val.get("osc")).intValue(),
                    ((Double) val.get("opc")).intValue(),
                    System.currentTimeMillis());
        }
////         MapreduceResults<ServerInfo> results = datastore.mapReduce(MapreduceType.INLINE, getFilterQuery(filter), ServerInfo.class, mapReduceCommand);
////        if(results.getCounts().getOutputCount() == 0) {
////            System.out.println("output 0 :*");
//            return null;
//        }
//        System.out.println("ms: " + results.getElapsedMillis());
//        results.forEach(info -> System.out.println(info.getOpenSlotCount()));
        return null;
    }

    @Override
    public Success putServer(Server server) {
        try {
            datastore.save(collectionName, server);
        } catch (Exception e) {
            e.printStackTrace();
            return new Success(false, e.getMessage());
        }
        return new Success(true);
    }

    @Override
    public Server getJoinableServer(Filter filter, String uuid) {
        Query<Server> query = getFilterQuery(filter)
                .field("joinable")
                .equal(true)
                .where("this.pc < this.mpc")
                .field("expiry").greaterThan(System.currentTimeMillis())
                .limit(1);
        query.order("pc");
        query.getSortObject().put("pc", -1);

        UpdateOperations<Server> updateOperations = datastore.createUpdateOperations(Server.class);
        updateOperations.inc("pc", 1);
        updateOperations.add("players", uuid);
        Server server = datastore.findAndModify(query, updateOperations, false, false);
        if (server == null)
            return null;
        return server;
    }

    private Document getServerDoc(Server server) {
        Document doc = new Document();
        if (server.getGameId() != null)
            doc.append(GAME_ID_FIELD, server.getGameId());
        if (server.getMapId() != null)
            doc.append(GAME_ID_FIELD, server.getMapId());
        if (server.getFlavorId() != null)
            doc.append(FLAVOR_ID_FIELD, server.getFlavorId());
        doc.append(PLAYER_COUNT_FIELD, server.getPlayerCount());
        doc.append(MAX_PLAYER_COUNT_FIELD, server.getPlayers());
        doc.append(EXPIRY_FIELD, server.getExpiry());
        doc.append(PLAYERS_FIELD, server.getMaxPlayerCount());
        doc.append(SOCKET_FIELD, server.getSocket());
        return doc;
    }

    private Query<Server> getFilterQuery(Filter filter) {
        Query<Server> query = getServerQuery();
        query.field("expiry").greaterThanOrEq(System.currentTimeMillis());
        if (filter.getGameId() != null)
            query.field("gameId").equal(filter.getGameId());
        if (filter.getMapId() != null)
            query.field("mapId").equal(filter.getMapId());
        if (filter.getFlavorId() != null)
            query.field("flavorId").equal(filter.getFlavorId());
        return query;
    }

    private Query<Server> getServerQuery() {
        return datastore.createQuery(collectionName, Server.class);
    }
}
