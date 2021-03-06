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

import com.exorath.service.actionapi.api.ActionAPIServiceAPI;
import com.exorath.service.commons.mongoProvider.MongoProvider;
import com.exorath.service.commons.portProvider.PortProvider;
import com.exorath.service.commons.tableNameProvider.TableNameProvider;
import com.exorath.service.connector.service.DatabaseProvider;
import com.exorath.service.connector.service.MongoDatabaseProvider;
import com.exorath.service.connector.service.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by toonsev on 11/3/2016.
 */
public class Main {
    private Service service;
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public Main() {
        DatabaseProvider databaseProvider = new MongoDatabaseProvider(MongoProvider.getEnvironmentMongoProvider().getClient(),
                TableNameProvider.getEnvironmentTableNameProvider("DB_NAME").getTableName(),
                TableNameProvider.getEnvironmentTableNameProvider("SERVERS_COL_NAME").getTableName());
        this.service = new SimpleService(databaseProvider, new ActionAPIServiceAPI(getActionAPIAddress()));
        LOG.info("Service " + this.service.getClass() + " instantiated");
        Transport.setup(service, PortProvider.getEnvironmentPortProvider());
        LOG.info("HTTP Transport initiated");
    }

    public String getActionAPIAddress(){
        String actionAPIAdress =  System.getenv("ACTIONAPI_SERVICE_ADDRESS");
        if(actionAPIAdress == null){
            System.out.println("No ACTIONAPI_SERVICE_ADDRESS environment variable set");
            System.exit(1);
        }
        return actionAPIAdress;
    }
    public static void main(String[] args) {
        new Main();
    }
}
